package HttpServer;

import Message.Request;
import Message.Response;

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
                    if(inFromClient.available()>0){
                        lastReceiveTime=currentTime;//更新最近的连接时间
                        Request request = Request.parseRequest(inFromClient);
                        System.out.println(request.toString());
                        Response response = Response.buildResponse(request);
                        assert response != null;
                        System.out.print(response.toString());
                        OutputStream outToClient = socket.getOutputStream();
                        outToClient.write(response.toString().getBytes(StandardCharsets.UTF_8));
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
