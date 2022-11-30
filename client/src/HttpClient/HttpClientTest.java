package HttpClient;

import HttpServer.HttpServer;

public class HttpClientTest {
    public static void main(String[] args) {
        HttpClient client1 = new HttpClient("localhost", HttpServer.DEFAULT_PORT);
        client1.get("/test.html");
    }
}
