package com.example.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private final boolean[][] grid = new boolean[40][30];
    private List<Obstacle> obstacles = new ArrayList<>();

    public GameController() {
        setupObstacles(1);
    }

    @PostMapping("/game-data")
    public GameData updateGameData(@RequestBody Player newPlayer) {
        String status = "playing";
        player.x = newPlayer.x;
        player.y = newPlayer.y;

        // 障害物の動きを更新しオブジェクトを生成
        nextObstacles();

        // 衝突チェック
        if (checkCollision()) {
            status = "game-over";
        }

        // ゴールチェック
        if (checkGoal()) {
            status = "game-clear";
        }

        return new GameData(player, goal, obstacles, status);
    }

    @GetMapping("/game-reset")
    public GameData gameReset(@RequestParam int stage) {
        player.x = 0;
        player.y = 0;
        setupObstacles(stage);
        return new GameData(player, goal, obstacles, "playing");
    }

    private void setupObstacles(int stage) {
        clearGrid();
        switch (stage) {
            case 1:
                setupStage1Obstacles();
                break;
            case 2:
                setupStage2Obstacles();
                break;
            default:
                setupRandomObstacles(250 + 50*stage);
                break;
        }
        grid[0][0] = false;
        updateObstacles();
    }

    private void clearGrid() {
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 30; j++) {
                grid[i][j] = false;
            }
        }
    }

    private void initialPulsar(int x, int y) {
        if (x < 0 || x+17 >= 40 || y < 0 || y+17 >= 30) {
            return;
        }
        // パルサー
        grid[x+10][y+10] = true;
        grid[x+11][y+10] = true;
        grid[x+12][y+10] = true;
        grid[x+16][y+10] = true;
        grid[x+17][y+10] = true;
        grid[x+9][y+12] = true;
        grid[x+13][y+12] = true;
        grid[x+9][y+13] = true;
        grid[x+13][y+13] = true;
        grid[x+9][y+14] = true;
        grid[x+13][y+14] = true;
        grid[x+10][y+15] = true;
        grid[x+10][y+16] = true;
        grid[x+10][y+17] = true;
    }

    private void initialGosperGliderGun(int x, int y) {
        if (x < 0 || x+36 >= 40 || y < 0 || y+9 >= 30) {
            return;
        }
        // Gosper Glider Gun
        grid[x+1][y+5] = true;
        grid[x+2][y+5] = true;
        grid[x+1][y+6] = true;
        grid[x+2][y+6] = true;

        grid[x+13][y+3] = true;
        grid[x+14][y+3] = true;
        grid[x+12][y+4] = true;
        grid[x+16][y+4] = true;
        grid[x+11][y+5] = true;
        grid[x+17][y+5] = true;
        grid[x+11][y+6] = true;
        grid[x+15][y+6] = true;
        grid[x+17][y+6] = true;
        grid[x+18][y+6] = true;
        grid[x+11][y+7] = true;
        grid[x+17][y+7] = true;
        grid[x+12][y+8] = true;
        grid[x+16][y+8] = true;
        grid[x+13][y+9] = true;
        grid[x+14][y+9] = true;

        grid[x+25][y+1] = true;
        grid[x+23][y+2] = true;
        grid[x+25][y+2] = true;
        grid[x+21][y+3] = true;
        grid[x+22][y+3] = true;
        grid[x+21][y+4] = true;
        grid[x+22][y+4] = true;
        grid[x+21][y+5] = true;
        grid[x+22][y+5] = true;
        grid[x+23][y+6] = true;
        grid[x+25][y+6] = true;
        grid[x+25][y+7] = true;

        grid[x+35][y+3] = true;
        grid[x+36][y+3] = true;
        grid[x+35][y+4] = true;
        grid[x+36][y+4] = true;
    }

    private void setupStage1Obstacles() {
        // ランダムにx, yを5パターン生成
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(40-17);
            int y = random.nextInt(30-17);
            initialPulsar(x, y);
        }
    }

    private void setupStage2Obstacles() {
        int x = 2;
        int y = 5;
        // グライダー銃を生成
        initialGosperGliderGun(x, y);
    }

    private void setupRandomObstacles(int num_obstacles) {
        Random random = new Random();
        int numberOfObstacles = random.nextInt(num_obstacles) + 1; // ランダムな個数の障害物を生成
        for (int i = 0; i < numberOfObstacles; i++) {
            int x = random.nextInt(40);
            int y = random.nextInt(30);
            grid[x][y] = true;
        }
    }

    private void updateObstacles() {
        obstacles.clear();
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 30; j++) {
                if (grid[i][j]) {
                    obstacles.add(new Obstacle(i * 20, j * 20, 20, 20));
                }
            }
        }
    }

    private void nextObstacles() {
        // 障害物の動きを制御するロジックを追加
        nextGeneration();

        obstacles.clear();
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 30; j++) {
                if (grid[i][j]) {
                    obstacles.add(new Obstacle(i * 20, j * 20, 20, 20));
                }
            }
        }

        // 障害物がなくなった場合は新たに生成
        if (obstacles.size() == 0) {
            setupRandomObstacles(250);
            if (checkCollision()) {
                setupRandomObstacles(250);
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
                if (newRow >= 0 && newRow < 40 && newCol >= 0 && newCol < 30) {
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
        boolean[][] copyGrid = new boolean[40][30];
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 30; j++) {
                copyGrid[i][j] = grid[i][j];
            }
        }

        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 30; j++) {
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
