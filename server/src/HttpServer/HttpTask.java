package HttpServer;

import Message.Request;
import Message.Response;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpTask implements Runnable{
    private final Socket socket;

    HttpTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
//        System.out.println(Thread.currentThread().getName() + " is running for " + socket.getInetAddress() + ":" + socket.getPort());
        try {
            InputStream inFromClient = socket.getInputStream();
            Request request = Request.parseRequest(inFromClient);
            System.out.println(request.toString());
            Response response = Response.buildResponse(request);
            assert response != null;
            System.out.print(response.toString());
            OutputStream outToClient = socket.getOutputStream();
            outToClient.write(response.toString().getBytes(StandardCharsets.UTF_8));
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(Thread.currentThread().getName() + " Complete!");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
