package edu.njunet;

import edu.njunet.message.Request;
import edu.njunet.message.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpRequestHandler implements Runnable {
    private static final int MAX_GAP = 30000;//不发消息的最大时间跨度
    private final Socket socket;
    private long lastReceiveTime;

    HttpRequestHandler(Socket socket) {
        this.socket = socket;
        lastReceiveTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (true) {//保证长连接
            try {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastReceiveTime > MAX_GAP) {//中断连接
                    socket.close();
                    break;
                } else {
                    InputStream inFromClient = socket.getInputStream();
                    System.out.println(inFromClient.available());
                    if (inFromClient.available() > 0) {
                        lastReceiveTime = currentTime;//更新最近的连接时间
                        Request request = Request.parseRequest(inFromClient);
                        System.out.println(request);
                        Response response = Response.buildResponse(request);
                        assert response != null;
                        System.out.println(response);
                        OutputStream outToClient = socket.getOutputStream();
                        response.send(outToClient);
                    }
                    Thread.sleep(100);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
