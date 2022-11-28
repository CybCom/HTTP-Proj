package HttpServer;

import java.io.*;
import java.net.*;

public class HttpServer {
    public static final int DEFAULT_PORT = 8080; //默认8080端口

    public static void start(){
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("服务器端正在监听端口："+serverSocket.getLocalPort()+"...");
            while(true){//死循环时刻监听客户端链接
                final Socket socket = serverSocket.accept();
                System.out.println("建立了与客户端一个新的tcp连接，客户端地址为："+socket.getInetAddress() +":"+socket.getPort());
                //开始服务
                service(socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void service(Socket socket) {
        HttpTask httpTask = new HttpTask(socket);
        Thread thread = new Thread(httpTask);
        thread.start();
    }




}
