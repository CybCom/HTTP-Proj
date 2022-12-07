package HttpClient;

import HttpServer.HttpServer;


public class HttpClientTest {
    public static void main(String[] args) {
        HttpClient client1 = new HttpClient("localhost", HttpServer.DEFAULT_PORT);

        client1.get("/test.html");
//        client1.get("/tomb_raider.png");
//        client1.get("/1.png");
    }
}
