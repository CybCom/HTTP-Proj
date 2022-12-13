package edu.njunet.session;

import edu.njunet.protocol.Request;
import edu.njunet.protocol.Response;

import java.io.IOException;
import java.io.InputStream;
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
        for (long currTime = System.currentTimeMillis();
             currTime - lastReceiveTime < MAX_GAP;
             currTime = System.currentTimeMillis()) {//保证长连接
            try {
                InputStream inFromClient = socket.getInputStream();
                if (inFromClient.available() > 0) {
                    lastReceiveTime = currTime;//更新最近的连接时间
                    new Response(new Request(inFromClient)).send(socket.getOutputStream());
                }
                System.err.println("one try");
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
