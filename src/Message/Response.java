package Message;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private String version;

    private int code;

    private String status;

    private Map<String, String> header;

    private String message;

    //解析inputSteam流，返回一个Response对象,用于client接受来自server的响应
    public static Response parseResponse(InputStream rspStream) throws IOException {
        Response response = new Response();
        BufferedReader bf = new BufferedReader(new InputStreamReader(rspStream, "UTF-8"));
        decodeResponseLine(bf, response);
        decodeResponseHeader(bf, response);
        decodeResponseMessage(bf, response);
        return response;
    }

    //用于在server端创建将要发送的response
    public static Response buildResponse() {
        return null;
    }

    private static void decodeResponseLine(BufferedReader bf, Response response) throws IOException {
        String firstLine = bf.readLine();
        String[] lines = firstLine.split(" ");
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
        int messageLen = Integer.parseInt(response.getHeader().getOrDefault("Content-Length", "0")); //响应体有多少字节
        if (messageLen != 0) {
            char[] chars = new char[messageLen];
            int readByte = bf.read(chars);
            assert readByte == messageLen;
            String message = new String(chars);
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
}
