package edu.njunet.protocol;

import edu.njunet.utils.JsonOps.JsonService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Response {
    private String version;

    private int code;

    private String status;

    private Map<String, String> header;

    private byte[] message;

    public Response(Request request) {
        JsonService.getInstance().service(request, this);
        System.out.println("Response Starts Here: \n" + this);
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(String s) {
        code = Integer.parseInt(s);
    }

    public void setStatus(String s) {
        status = s;
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
        return new String(message, StandardCharsets.UTF_8);
    }

    public byte[] content() {
        return message;
    }

    public void send(OutputStream out) throws IOException {
        String responseLine = version + " " + code + " " + status + "\r\n";
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        String responseLine = version + " " + code + " " + status + "\r\n";
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
}
