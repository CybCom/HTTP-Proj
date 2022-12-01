package HttpClient;

import HttpServer.HttpServer;

import java.io.IOException;

public class HttpClientTest {
    public static void main(String[] args) throws IOException {
        HttpClient client1 = new HttpClient("localhost", HttpServer.DEFAULT_PORT);
        client1.get("/test.html");
    }
}
