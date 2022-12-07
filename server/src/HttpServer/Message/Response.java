package HttpServer.Message;

import utils.JsonReader.ServerJsonReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Response {
    private String version;

    private int code;

    private String status;

    private Map<String, String> header;

    private byte[] message;

    /** 解析inputSteam流，返回一个Response对象,用于client接受来自server的响应
     * @param rspStream inputSteam流
     * @return 一个Response对象,用于client接受来自server的响应
     */
    public static Response parseResponse(InputStream rspStream) throws IOException {
        Response response = new Response();
        decodeResponseLineAndHeader(rspStream, response);
        decodeResponseMessage(rspStream, response);
        return response;
    }

    /** 用于在server端创建将要发送的response
     * @param request Request类（客户端发出的消息）
     * @return 在server端将要发送的response
     */
    public static Response buildResponse(Request request) {
        ServerJsonReader serverJsonReader = ServerJsonReader.getInstance();
        return serverJsonReader.createResponse(request);
    }


    /***
     * 设置response的行和头
     */
    private static void decodeResponseLineAndHeader(InputStream resStream, Response response) throws IOException {
        List<String> lines = getLines(resStream);
        String[] line = lines.get(0).replace("\r\n", "").split(" ", 3);
        response.setVersion(line[0]);
        response.setCode(line[1]);
        response.setStatus(line[2]);
        Map<String, String> header = new HashMap<>();
        for (int i = 1; i < lines.size()-1; i++) {
            String[] entry = lines.get(i).replace("\r\n", "").split(":", 2);
            header.put(entry[0], entry[1]);
        }
        response.setHeader(header);
    }

    /***
     * @return line和 header组成的List
     */
    private static List<String> getLines(InputStream resStream) throws IOException {
        int i = -1;
        byte[] buffer = new byte[1024];
        List<String> lines = new ArrayList<>();
        while (resStream.available() > 0) {
            int b = resStream.read();
            buffer[++i] = (byte) b;
            if (i >= 1 && (buffer[i-1] == '\r') && (buffer[i] == '\n')) {
                byte[] line = Arrays.copyOfRange(buffer, 0, i+1);
                lines.add(new String(line, StandardCharsets.UTF_8));
                if (i == 1) {
                    break;
                } else {
                    i = -1;
                }
            }
        }
        return lines;
    }

    /***
     * 将响应体的内容以byte[]保存
     */
    private static void decodeResponseMessage(InputStream resStream, Response response) throws IOException {
        int messageLen = Integer.parseInt(response.getHeader().getOrDefault("Content-Length", "0").trim()); //响应体有多少字节
        if (messageLen != 0) {
            int remainingByte = messageLen;
            byte[] buffer = new byte[messageLen];
            int i = 0;
            while (remainingByte > 0) {
                int alreadyRead = resStream.read(buffer, i, remainingByte);
                remainingByte -= alreadyRead;
                i += alreadyRead;
            }
            response.setMessage(buffer);
        }
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public void setCode(String s) {code = Integer.parseInt(s);}

    public int getCode() {return code;}

    public void setStatus(String s) {status = s;}

    public String getStatus() {return status;}

    public void setVersion(String s) {version = s;}

    public String getVersion() {return version;}

    public void setHeader(Map<String, String> map) {header = map;}

    public Map<String, String> getHeader() {return header;}

    public String text() {return new String(message, StandardCharsets.UTF_8); }

    public byte[] content() {return message; }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        String responseLine = version + " " + String.valueOf(code) + " " + status + "\r\n";
        sb.append(responseLine);

        for (Map.Entry<String, String> entry : header.entrySet()) {
            String tmp = entry.getKey() + ":" + entry.getValue() + "\r\n";
            sb.append(tmp);
        }
        sb.append("\r\n");
        if (message != null) {
            sb.append(text());
        }

        return sb.toString();
    }

    public void send(OutputStream out) throws IOException {
        String responseLine = version + " " + String.valueOf(code) + " " + status + "\r\n";
        out.write(responseLine.getBytes(StandardCharsets.UTF_8));
        for (Map.Entry<String, String> entry : header.entrySet()) {
            String tmp = entry.getKey() + ":" + entry.getValue() + "\r\n";
            out.write(tmp.getBytes(StandardCharsets.UTF_8));
        }
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
        if (message != null) {
            out.write(message);
        }
    }
}
