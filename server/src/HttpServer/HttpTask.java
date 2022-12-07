package HttpServer;

import HttpServer.Message.Request;
import HttpServer.Message.Response;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpTask implements Runnable{
    private final Socket socket;
    private static final int MAX_GAP=30000;//不发消息的最大时间跨度
    private long lastReceiveTime=System.currentTimeMillis();
    private boolean exit=false;

    HttpTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
//        System.out.println(Thread.currentThread().getName() + " is running for " + socket.getInetAddress() + ":" + socket.getPort());
        while(!exit){//保证长连接
            try{
                long currentTime=System.currentTimeMillis();
                if(currentTime-lastReceiveTime>MAX_GAP){//中断连接
                    socket.close();
                    exit=true;
                }else{
                    InputStream inFromClient = socket.getInputStream();
                    System.out.println(inFromClient.available());
                    if(inFromClient.available()>0){
                        lastReceiveTime=currentTime;//更新最近的连接时间
                        Request request = Request.parseRequest(inFromClient);
                        System.out.println(request);
                        Response response = Response.buildResponse(request);
                        assert response != null;
                        System.out.println(response);
                        OutputStream outToClient = socket.getOutputStream();
                        response.send(outToClient);
                    }
                    Thread.sleep(1000);
                }

    //        System.out.println(Thread.currentThread().getName() + " Complete!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
