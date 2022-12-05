package HttpClient;

import Message.Request;
import Message.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpClient {
    private final String hostname;
    private final int port;

    HttpClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void get(String url) {
        try {
            Socket client = new Socket(hostname, port);
            System.out.println("连接到主机：" + hostname + " ,端口号：" + port);
            System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
            Request request = Request.buildRequest(url);
            System.out.print(request);
            OutputStream outToServer = client.getOutputStream();
            outToServer.write(request.toString().getBytes(StandardCharsets.UTF_8));
            InputStream inFromServer = client.getInputStream();
            Response response = Response.parseResponse(inFromServer);
            System.out.print(response);
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return 登录相关
     */
    public void post() {

    }
}
