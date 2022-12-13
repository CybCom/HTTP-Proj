package edu.njunet.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Request {
    private String method;
    private String url;
    private String version;
    private Map<String, String> header;
    private byte[] message;

    /**
     * 用于在server端创建将要发送的response
     *
     * @param reqStream binary stream from client.
     */
    public Request(InputStream reqStream) throws IOException {
        decodeRequestLineAndHeader(reqStream, this);
        decodeRequestMessage(reqStream, this);
        System.out.println("Request Starts Here: \n" + this);
    }

    /***
     * 设置request的行和头
     */
    private static void decodeRequestLineAndHeader(InputStream reqStream, Request request) throws IOException {
        List<String> lines = getLines(reqStream);
        String[] line = lines.get(0).replace("\r\n", "").split(" ", 3);
        request.setMethod(line[0]);
        request.setUrl(line[1]);
        request.setVersion(line[2]);

        Map<String, String> header = new HashMap<>();
        for (int i = 1; i < lines.size() - 1; i++) {
            String[] entry = lines.get(i).replace("\r\n", "").split(":", 2);
            header.put(entry[0], entry[1]);
        }
        request.setHeader(header);
    }

    /***
     * @return line和 header组成的List
     */
    private static List<String> getLines(InputStream reqStream) throws IOException {
        byte[] buffer = new byte[1024];
        List<String> lines = new ArrayList<>();

        int i = -1;
        while (reqStream.available() > 0) {
            buffer[++i] = (byte) reqStream.read();
            if (i >= 1 && (buffer[i - 1] == '\r') && (buffer[i] == '\n')) {
                byte[] line = Arrays.copyOfRange(buffer, 0, i + 1);
                lines.add(new String(line));
                if (i == 1) {
                    break;
                } else {
                    i = -1;
                }
            }
        }
        return lines;
    }

    private static void decodeRequestMessage(InputStream reqStream, Request request) throws IOException {
        int messageLen = Integer.parseInt(request.getHeader().getOrDefault("Content-Length", "0").trim()); //请求体有多少字节，可能为0
        if (messageLen != 0) {
            int remainingByte = messageLen;
            byte[] buffer = new byte[messageLen];
            int i = 0;
            while (remainingByte > 0) {
                int alreadyRead = reqStream.read(buffer, i, remainingByte);
                remainingByte -= alreadyRead;
                i += alreadyRead;
            }
            request.setMessage(buffer);
        }
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String s) {
        method = s;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String s) {
        url = s;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String s) {
        version = s;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> map) {
        header = map;
    }

    public String text() {
        return message == null ? null : new String(message);
    }

    public void setMessage(byte[] s) {
        message = s;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        String requestLine = method + " " + url + " " + version + "\r\n";
        sb.append(requestLine);

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
}
