package HttpServer;

import Message.Request;
import Message.Response;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;

public class HttpTask implements Runnable {
    private static final String rootPath = "webRoot";
    Socket socket;

    HttpTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
//        System.out.println(Thread.currentThread().getName() + " is running for " + socket.getInetAddress() + ":" + socket.getPort());
        try {
            InputStream inFromClient = socket.getInputStream();
            Request request = Request.parseRequest(inFromClient);
            System.out.println(request);
            Response response = createByMethod(request); //get或post
            assert response != null;
            System.out.print(response);
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

    private Response createByMethod(Request request) {
        if (request.getMethod().equals("GET")) {
            String url = request.getUrl();
            try {
                InputStream resource = HttpServer.class.getResourceAsStream(rootPath + url);
                assert resource != null;
                BufferedReader bf = new BufferedReader(new InputStreamReader(resource));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                    sb.append("\r\n");
                }
                String message = sb.toString();
//                String responseLine = "HTTP/1.1 200 OK";
//                Response rs = Response.buildResponse(responseLine, message);
                Response rs = new Response();
                rs.setVersion("HTTP/1.1");
                rs.setCode("200");
                rs.setStatus("OK");
                rs.setMessage(message);
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", " text/html");
                map.put("Content-Length", " " + message.getBytes(StandardCharsets.UTF_8).length);
                rs.setHeader(map);
                return rs;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (request.getMethod().equals("POST")) {
            //TODO
        } else {
            System.out.println("Unknown request method!");
            exit(1);
        }
        return null;
    }
}
