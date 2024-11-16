package com.example.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class GameApplication {
    public static void main(String[] args) {
        SpringApplication.run(GameApplication.class, args);
    }
}

@RestController
class GameController {
    private final Player player = new Player(0, 0, 20);
    private final Goal goal = new Goal(780, 580, 20);
    private final List<Obstacle> obstacles = new ArrayList<>();

    public GameController() {
        obstacles.add(new Obstacle(100, 100, 100, 20));
        obstacles.add(new Obstacle(300, 200, 20, 100));
        // 他の障害物を追加
    }

    @PostMapping("/game-data")
    public GameData updatePlayer(@RequestBody Player newPlayer) {
        player.x = newPlayer.x;
        player.y = newPlayer.y;

        // 障害物の動きを更新
        updateObstacles();

        // 衝突チェック
        if (checkCollision()) {
            resetGame();
            return new GameData(player, goal, obstacles, "game-over");
        }

        // ゴールチェック
        if (checkGoal()) {
            resetGame();
            return new GameData(player, goal, obstacles, "game-clear");
        }

        return new GameData(player, goal, obstacles, "playing");
    }

    private void updateObstacles() {
        // 障害物の動きを制御するロジックを追加
        for (Obstacle obstacle : obstacles) {
            obstacle.move();
        }
    }

    private boolean checkCollision() {
        for (Obstacle obstacle : obstacles) {
            if (player.x < obstacle.x + obstacle.width &&
                player.x + player.size > obstacle.x &&
                player.y < obstacle.y + obstacle.height &&
                player.y + player.size > obstacle.y) {
                return true;
            }
        }
        return false;
    }

    private boolean checkGoal() {
        return player.x < goal.x + goal.size &&
               player.x + player.size > goal.x &&
               player.y < goal.y + goal.size &&
               player.y + player.size > goal.y;
    }

    private void resetGame() {
        player.x = 0;
        player.y = 0;
        obstacles.clear();
        obstacles.add(new Obstacle(100, 100, 100, 20));
        obstacles.add(new Obstacle(300, 200, 20, 100));
        // 他の障害物を追加
    }
}

class Player {
    public int x, y, size;
    public Player(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }
}

class Goal {
    public int x, y, size;
    public Goal(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }
}

class Obstacle {
    public int x, y, width, height;
    private int dx = 20; // 障害物の移動速度
    public Obstacle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move() {
        x += dx;
        if (x < 0 || x + width > 800) {
            dx = -dx; // 画面端で反転
        }
    }
}

class GameData {
    public Player player;
    public Goal goal;
    public List<Obstacle> obstacles;
    public String status;
    public GameData(Player player, Goal goal, List<Obstacle> obstacles, String status) {
        this.player = player;
        this.goal = goal;
        this.obstacles = obstacles;
        this.status = status;
    }
}
