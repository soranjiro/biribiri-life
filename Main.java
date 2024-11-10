import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static AtomicInteger targetNumber = new AtomicInteger(new Random().nextInt(100) + 1);

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // デフォルトのエグゼキュータを使用
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String query = t.getRequestURI().getQuery();
            String response;
            if (query != null && query.startsWith("guess=")) {
                try {
                    int guess = Integer.parseInt(query.split("=")[1]);
                    int target = targetNumber.get();
                    if (guess < target) {
                        response = "Too low!";
                    } else if (guess > target) {
                        response = "Too high!";
                    } else {
                        response = "Correct! Generating new number...";
                        targetNumber.set(new Random().nextInt(100) + 1);
                    }
                } catch (NumberFormatException e) {
                    response = "Invalid number!";
                }
            } else {
                response = "Please provide a guess parameter, e.g., /?guess=50";
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
