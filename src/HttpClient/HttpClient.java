package HttpClient;

import Message.Request;
import Message.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class HttpClient {
    private String hostname;
    private int port;

    HttpClient (String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void get(String url) {
        try {
            Socket client = new Socket(hostname, port);
            System.out.println("连接到主机：" + hostname + " ,端口号：" + port);
            System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
            Request request = Request.buildRequest(url);
            System.out.print(request.toString());
            OutputStream outToServer = client.getOutputStream();
            outToServer.write(request.toString().getBytes(StandardCharsets.UTF_8));
            InputStream inFromServer = client.getInputStream();
            Response response = Response.parseResponse(inFromServer);
            System.out.print(response.toString());
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void post() {

    }
}
