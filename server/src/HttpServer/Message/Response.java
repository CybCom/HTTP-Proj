package HttpServer.Message;

import utils.JsonReader.ServerJsonReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private String version;

    private int code;

    private String status;

    private Map<String, String> header;

    private String message;

    /**
     * @param rspStream inputSteam流
     * @return 一个Response对象,用于client接受来自server的响应
     */
    //解析inputSteam流，返回一个Response对象,用于client接受来自server的响应
    public static Response parseResponse(InputStream rspStream) throws IOException {
        Response response = new Response();
        BufferedReader bf = new BufferedReader(new InputStreamReader(rspStream, StandardCharsets.UTF_8));
        decodeResponseLine(bf, response);
        decodeResponseHeader(bf, response);
        decodeResponseMessage(bf, response);
        return response;
    }

    /**
     * @param request Request类（客户端发出的消息）
     * @return 在server端将要发送的response
     */
    //用于在server端创建将要发送的response
    public static Response buildResponse(Request request) {
        ServerJsonReader serverJsonReader = ServerJsonReader.getInstance();
        return serverJsonReader.createResponse(request);
    }



    private static void decodeResponseLine(BufferedReader bf, Response response) throws IOException {
        String firstLine = bf.readLine();
        String[] lines = firstLine.split(" ", 3);
        assert lines.length == 3;
        response.setVersion(lines[0]);
        response.setCode(lines[1]);
        response.setStatus(lines[2]);
    }

    private static void decodeResponseHeader(BufferedReader bf, Response response) throws IOException {
        Map<String, String> map = new HashMap<>();
        String line;
        while (!(line = bf.readLine()).equals("")) {
            String[] lines = line.split(":");
            assert lines.length == 2;
            map.put(lines[0], lines[1]);
        }
        response.setHeader(map);
    }

    private static void decodeResponseMessage(BufferedReader bf, Response response) throws IOException {
        int messageLen = Integer.parseInt(response.getHeader().getOrDefault("Content-Length", "0").trim()); //响应体有多少字节
        if (messageLen != 0) {
            char[] chars = new char[messageLen];
            int readByte = bf.read(chars);
            String message = new String(chars).substring(0, readByte);
            response.setMessage(message);
        }
    }


    public void setCode(String s) {code = Integer.parseInt(s);}

    public int getCode() {return code;}

    public void setStatus(String s) {status = s;}

    public String getStatus() {return status;}

    public void setVersion(String s) {version = s;}

    public String getVersion() {return version;}

    public void setHeader(Map<String, String> map) {header = map;}

    public Map<String, String> getHeader() {return header;}

    public void setMessage(String s) {message = s;}

    public String getMessage() {return message;}

    public String toString() {
        StringBuilder sb = new StringBuilder();
        String responseLine = version + " " + String.valueOf(code) + " " + status + "\r\n";
        sb.append(responseLine);

        for (Map.Entry<String, String> entry : header.entrySet()) {
            String tmp = entry.getKey() + ":" + entry.getValue() + "\r\n";
            sb.append(tmp);
        }
        sb.append("\r\n");
        sb.append(message);
        sb.append("\r\n");

        return sb.toString();
    }
}
