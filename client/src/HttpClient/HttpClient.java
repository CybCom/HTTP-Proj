package HttpClient;

import HttpServer.Message.Request;
import HttpServer.Message.Response;
import utils.JsonReader.ClientJsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpClient {
    private final String hostname;
    private final int port;

    private Socket client;

    private final ClientJsonReader clientJsonReader;

    HttpClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        clientJsonReader = new ClientJsonReader();
        try {
            client = new Socket(hostname, port);
            System.out.println("连接到主机：" + hostname + " ,端口号：" + port);
            System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 关闭服务器的socket资源
     */
    public void close() {
        try {
            client.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /***
     * 发送get请求
     * @param url 待请求的url
     */
    public void get(String url) {
        try {
            Request request = Request.buildRequest(url);
            reRequest(request);
            System.out.println(request);
            OutputStream outToServer = client.getOutputStream();
            request.send(outToServer);

            InputStream inFromServer = client.getInputStream();
            while (inFromServer.available() == 0) { //服务器给响应了才继续
            }
            Response response = Response.parseResponse(inFromServer);
            handleResponse(response, url);
//            System.out.println(response);

        } catch (IOException ex) {
            try {
                client = new Socket(hostname, port);
                System.out.println("超时，重新建立连接！");
                System.out.println("连接到主机：" + hostname + " ,端口号：" + port);
                System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
                get(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 发送post请求，主要用于login，若用户不存在，服务器自动注册
     * @param url 服务器login表的url
     * @param user_name 用户名
     * @param password 密码
     */

    public void post(String url, String user_name, String password) {
        try {
            Request request = Request.buildRequest(url);
            switchToPost(request, user_name, password);
            System.out.println(request);
            OutputStream outToServer = client.getOutputStream();
            outToServer.write(request.toString().getBytes(StandardCharsets.UTF_8));

            InputStream inFromServer = client.getInputStream();
            while (inFromServer.available() == 0) { //服务器给响应了才继续
            }
            Response response = Response.parseResponse(inFromServer);
            System.out.println(response);

        } catch (IOException ex) {
            try {
                client = new Socket(hostname, port);
                System.out.println("超时，重新建立连接！");
                System.out.println("连接到主机：" + hostname + " ,端口号：" + port);
                System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
                post(url, user_name, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 将默认请求报文设为post请求
     * @param request 生成的默认请求报文，为get请求
     * @param user_name 用户名
     * @param password 密码
     */
    private void switchToPost(Request request, String user_name, String password) {
        request.setMethod("POST");
        String message = "User_name:"+user_name+",Password:"+password;
        request.setMessage(message.getBytes());
    }

    /***
     * 检查url是否已缓存，若true，则在request中加上条件请求if_modified_since 或 if_no_match
     * @param request 由 Message.Request 生成的默认请求报文
     */
    private void reRequest(Request request) {
        clientJsonReader.searchUrl(request);
    }

    /***
     * 对响应报文状态码的处理
     * @param response 从server接受的响应报文
     */
    private void handleResponse(Response response, String url) throws IOException {
        clientJsonReader.updateResource(response, url);
    }

}
