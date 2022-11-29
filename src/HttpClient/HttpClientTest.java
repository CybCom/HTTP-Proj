package HttpClient;

import HttpServer.HttpServer;
import HttpServer.HttpServerTest;

import java.io.IOException;
import java.io.InputStream;

public class HttpClientTest {
    public static void main(String[] args) throws IOException {
        HttpClient client1 = new HttpClient("localhost", HttpServer.DEFAULT_PORT);
        client1.get("/test.html");
    }
}
