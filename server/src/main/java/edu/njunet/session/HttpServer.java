package edu.njunet.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {
    public static final Path RESOURCES_ROOT = Paths.get(
            System.getProperty("user.dir") + "/server/src/main/resources/webroot"); //资源存放路径

    public static final int DEFAULT_PORT = 8080;

    /***
     * 启动服务器，开始监听
     */
    @SuppressWarnings("InfiniteLoopStatement")  // have to do it :-)
    public static void start() {
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            System.out.println(serverSocket.getLocalSocketAddress());
            System.out.println("服务器端正在监听端口：" + DEFAULT_PORT + "...");
            while (true) {
                final Socket socket = serverSocket.accept();
                System.out.println("建立了与客户端一个新的tcp连接，客户端地址为：" +
                        socket.getInetAddress() + ":" + socket.getPort());
                new Thread(new HttpRequestHandler(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("服务器启动失败，端口可能被占用！");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        start();
    }
}
