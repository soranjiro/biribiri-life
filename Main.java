import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new GameHandler());
        server.createContext("/style.css", new CssHandler());
        server.createContext("/game.js", new JsHandler());
        server.setExecutor(null); // デフォルトのエグゼキュータを使用
        server.start();
        logger.info("Server started at http://localhost:8080/");
    }

    static class GameHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            logger.info("Received request for /");
            String htmlResponse = "<html><head>" +
                    "<link rel='stylesheet' type='text/css' href='/style.css'>" +
                    "<script src='/game.js' defer></script>" +
                    "</head><body>" +
                    "<h1>ビリビリ棒ゲーム</h1>" +
                    "<canvas id='gameCanvas' width='500' height='500'></canvas>" +
                    "<button id='infoButton'>説明を見る</button>" +
                    "<div id='infoModal' class='modal'>" +
                    "<div class='modal-content'>" +
                    "<span class='close'>&times;</span>" +
                    "<p>青がプレーヤー．赤がブロック．緑がゴールです。</p>" +
                    "</div></div>" +
                    "</body></html>";

            byte[] responseBytes = htmlResponse.getBytes("UTF-8");
            t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            t.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }

    static class CssHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            logger.info("Received request for /style.css");
            String css = "body { font-family: Arial, sans-serif; background-color: #f0f0f0; text-align: center; padding: 50px; }" +
                            "h1 { color: #333; }" +
                            "#gameCanvas { border: 1px solid #000; background-color: #fff; }" +
                            "#infoButton { margin-top: 20px; padding: 10px 20px; font-size: 16px; }" +
                            ".modal { display: none; position: fixed; z-index: 1; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgb(0,0,0); background-color: rgba(0,0,0,0.4); }" +
                            ".modal-content { background-color: #fefefe; margin: 15% auto; padding: 20px; border: 1px solid #888; width: 80%; }" +
                            ".close { color: #aaa; float: right; font-size: 28px; font-weight: bold; }" +
                            ".close:hover, .close:focus { color: black; text-decoration: none; cursor: pointer; }";
            byte[] cssBytes = css.getBytes("UTF-8");
            t.getResponseHeaders().set("Content-Type", "text/css; charset=UTF-8");
            t.sendResponseHeaders(200, cssBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(cssBytes);
            os.close();
        }
    }

    static class JsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            logger.info("Received request for /game.js");
            String js = "document.addEventListener('DOMContentLoaded', function() {" +
                        "const canvas = document.getElementById('gameCanvas');" +
                        "const ctx = canvas.getContext('2d');" +
                        "const player = { x: 0, y: 0, size: 20, color: 'blue' };" +
                        "const goal = { x: 480, y: 480, size: 20, color: 'green' };" +
                        "const blocks = [{ x: 100, y: 100, size: 20, color: 'red' }, { x: 200, y: 200, size: 20, color: 'red' }];" +
                        "document.addEventListener('keydown', movePlayer);" +
                        "function movePlayer(e) {" +
                        "  switch (e.key) {" +
                        "    case 'ArrowUp': player.y -= 20; break;" +
                        "    case 'ArrowDown': player.y += 20; break;" +
                        "    case 'ArrowLeft': player.x -= 20; break;" +
                        "    case 'ArrowRight': player.x += 20; break;" +
                        "  }" +
                        "  checkCollision();" +
                        "  draw();" +
                        "}" +
                        "function checkCollision() {" +
                        "  if (player.x === goal.x && player.y === goal.y) {" +
                        "    alert('You win!');" +
                        "    resetGame();" +
                        "  }" +
                        "  blocks.forEach(block => {" +
                        "    if (player.x === block.x && player.y === block.y) {" +
                        "      alert('Game over!');" +
                        "      resetGame();" +
                        "    }" +
                        "  });" +
                        "}" +
                        "function resetGame() {" +
                        "  player.x = 0;" +
                        "  player.y = 0;" +
                        "}" +
                        "function draw() {" +
                        "  ctx.clearRect(0, 0, canvas.width, canvas.height);" +
                        "  drawRect(player);" +
                        "  drawRect(goal);" +
                        "  blocks.forEach(drawRect);" +
                        "}" +
                        "function drawRect(rect) {" +
                        "  ctx.fillStyle = rect.color;" +
                        "  ctx.fillRect(rect.x, rect.y, rect.size, rect.size);" +
                        "}" +
                        "const infoButton = document.getElementById('infoButton');" +
                        "const infoModal = document.getElementById('infoModal');" +
                        "const closeButton = document.getElementsByClassName('close')[0];" +
                        "infoButton.onclick = function() { infoModal.style.display = 'block'; };" +
                        "closeButton.onclick = function() { infoModal.style.display = 'none'; };" +
                        "window.onclick = function(event) { if (event.target == infoModal) { infoModal.style.display = 'none'; } };" +
                        "draw();" +
                        "});";
            byte[] jsBytes = js.getBytes("UTF-8");
            t.getResponseHeaders().set("Content-Type", "application/javascript; charset=UTF-8");
            t.sendResponseHeaders(200, jsBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(jsBytes);
            os.close();
        }
    }
}
