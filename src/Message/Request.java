package Message;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private String method;
    private String url;
    private String version;
    private Map<String, String> header;
    private String message;

    //解析inputSteam流，返回一个request对象，用于server接受来自client的请求
    public static Request parseRequest(InputStream reqStream) throws IOException {
        Request request = new Request();
        BufferedReader bf = new BufferedReader(new InputStreamReader(reqStream, "UTF-8"));
        decodeRequestLine(bf, request);
        decodeRequestHeader(bf, request);
        decodeRequestMessage(bf, request);
        return request;
    }

    //用于在client端创建request
    public static Request buildRequest() {
        return null;
    }

    private static void decodeRequestLine(BufferedReader bf, Request request) throws IOException {
        String firstLine = bf.readLine();
        String[] lines = firstLine.split(" ");
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
        int messageLen = Integer.parseInt(request.getHeader().getOrDefault("Content-Length", "0")); //请求体有多少字节，可能为0
        if (messageLen != 0) {
            char[] chars = new char[messageLen];
            int readByte = bf.read(chars);
            assert readByte == messageLen;
            String message = new String(chars);
            request.setMessage(message);
        }
    }


    public void setMethod(String s) {method = s;}

    public String getMethod() {return method;}

    public void setUrl(String s) {url = s;}

    public String getUrl() {return url;}

    public void setVersion(String s) {version = s;}

    public String getVersion() {return version;}

    public void setHeader(Map<String, String> map) {header = map;}

    public Map<String, String> getHeader() {return header;}

    public void setMessage(String s) {message = s;}

    public String getMessage() {return message;}
}