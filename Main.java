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
        server.createContext("/style.css", new CssHandler()); // CSSファイル用のコンテキストを追加
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

            String htmlResponse = "<html><head>" +
                    "<link rel='stylesheet' type='text/css' href='/style.css'>" +
                    "</head><body>" +
                    "<h1>Guess the Number Game</h1>" +
                    "<p>" + response + "</p>" +
                    "<form method='get' action='/'>" +
                    "  <label for='guess'>Enter your guess:</label>" +
                    "  <input type='number' id='guess' name='guess'>" +
                    "  <input type='submit' value='Submit'>" +
                    "</form>" +
                    "</body></html>";

            t.sendResponseHeaders(200, htmlResponse.length());
            OutputStream os = t.getResponseBody();
            os.write(htmlResponse.getBytes());
            os.close();
        }
    }

    static class CssHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String css = "body { font-family: Arial, sans-serif; background-color: #f0f0f0; text-align: center; padding: 50px; }" +
                         "h1 { color: #333; }" +
                         "p { font-size: 1.2em; }" +
                         "form { margin-top: 20px; }" +
                         "input[type='number'] { padding: 10px; font-size: 1em; }" +
                         "input[type='submit'] { padding: 10px 20px; font-size: 1em; background-color: #4CAF50; color: white; border: none; cursor: pointer; }" +
                         "input[type='submit']:hover { background-color: #45a049; }";
            t.sendResponseHeaders(200, css.length());
            OutputStream os = t.getResponseBody();
            os.write(css.getBytes());
            os.close();
        }
    }
}
