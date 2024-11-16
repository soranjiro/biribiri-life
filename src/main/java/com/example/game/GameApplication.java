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
    private final boolean[][] grid = new boolean[40][28];
    private List<Obstacle> obstacles = new ArrayList<>();

    public GameController() {
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 28; j++) {
                grid[i][j] = false;
            }
        }
        grid[10][10] = true;
        grid[11][11] = true;
        grid[10][11] = true;
        grid[11][10] = true;
        // 他の障害物を追加

        obstacles.clear();
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 28; j++) {
                if (grid[i][j]) {
                    obstacles.add(new Obstacle(i * 20, j * 20 + 20, 20, 20));
                }
            }
        }
    }

    @PostMapping("/game-data")
    public GameData updatePlayer(@RequestBody Player newPlayer) {
        player.x = newPlayer.x;
        player.y = newPlayer.y;

        // 障害物の動きを更新しオブジェクトを生成
        nextObstacles();

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

    private void nextObstacles() {
        // 障害物の動きを制御するロジックを追加
        nextGeneration();

        obstacles.clear();
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 28; j++) {
                if (grid[i][j]) {
                    obstacles.add(new Obstacle(i * 20, j * 20 + 20, 20, 20));
                }
            }
        }
    }

    // 隣接するセルの数を数えるメソッド
    private int countNeighbors(int row, int col, boolean[][] grid) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                // 自分自身を除外
                if (i == 0 && j == 0) continue;

                int newRow = row + i;
                int newCol = col + j;

                // 境界チェック
                if (newRow >= 0 && newRow < 40 && newCol >= 0 && newCol < 28) {
                    if (grid[newRow][newCol]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    // ライフゲームの状態を1世代進めるメソッド
    private void nextGeneration() {
        boolean[][] copyGrid = new boolean[40][28];
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 28; j++) {
                copyGrid[i][j] = grid[i][j];
            }
        }

        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 28; j++) {
                int neighbors = countNeighbors(i, j, copyGrid);

                // 生きているセルが過疎または過密な場合は死ぬ
                if (copyGrid[i][j]) {
                    grid[i][j] = (neighbors == 2 || neighbors == 3);
                }
                // 死んでいるセルがちょうど3つの生きたセルに囲まれている場合は生き返る
                else {
                    grid[i][j] = (neighbors == 3);
                }
            }
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
    public Obstacle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
