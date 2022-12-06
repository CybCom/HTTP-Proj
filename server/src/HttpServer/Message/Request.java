package HttpServer.Message;

import utils.DefaultRequestHead;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private String method;
    private String url;
    private String version;
    private Map<String, String> header;
    private String message; //由于我们只用request.post()发送账号、密码等文本信息，固请求体部分简化为String进行存取

    /***
     * 解析inputSteam流，返回一个request对象，用于server接受来自client的请求
     * @param reqStream 请求报文的字节流
     * @return 解析好的request报文对象
     * @throws IOException
     */
    public static Request parseRequest(InputStream reqStream) throws IOException {
        Request request = new Request();
        BufferedReader bf = new BufferedReader(new InputStreamReader(reqStream, StandardCharsets.UTF_8));
        decodeRequestLine(bf, request);
        decodeRequestHeader(bf, request);
        decodeRequestMessage(bf, request);
        return request;
    }

    /***
     * 用于在client端创建request
     * @param url URL
     * @return 建立好的request报文
     */
    public static Request buildRequest(String url) {
        Request request = new Request();
        request.setMethod("GET");
        request.setUrl(url);
        request.setVersion("HTTP/1.1");
        request.setHeader(DefaultRequestHead.DEFAULT_HEADER);
        return request;
    }

    private static void decodeRequestLine(BufferedReader bf, Request request) throws IOException {
        String firstLine = bf.readLine();
        String[] lines = firstLine.split(" ", 3);
        assert lines.length == 3;
        request.setMethod(lines[0]);
        request.setUrl(lines[1]);
        request.setVersion(lines[2]);
    }

    private static void decodeRequestHeader(BufferedReader bf, Request request) throws IOException {
        Map<String, String> map = new HashMap<>();
        String line = null;
        while (!(line = bf.readLine()).equals("")) {
            String[] lines = line.split(":");
            assert lines.length == 2;
            map.put(lines[0], lines[1]);
        }
        request.setHeader(map);
    }

    private static void decodeRequestMessage(BufferedReader bf, Request request) throws IOException {
        int messageLen = Integer.parseInt(request.getHeader().getOrDefault("Content-Length", "0").trim()); //请求体有多少字节，可能为0
        if (messageLen != 0) {
            char[] chars = new char[messageLen];
            int readByte = bf.read(chars);
            assert readByte == messageLen;
            String message = new String(chars).substring(0, readByte);
            request.setMessage(message);
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String s) {
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
            sb.append(message);
        }
        sb.append("\r\n");

        return sb.toString();
    }
}
