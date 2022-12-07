package HttpClient;

import HttpServer.HttpServer;


public class HttpClientTest {
    public static void main(String[] args) throws InterruptedException {
        HttpClient client1 = new HttpClient("localhost", HttpServer.DEFAULT_PORT);
        client1.get("/test.html");
        client1.get("/tomb_raider.png");
        Thread.sleep(50000);
        client1.get("/1.png");
        client1.close();
    }
}
