
package com.example.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObstacleManager {
    private final boolean[][] grid = new boolean[40][30];
    private List<Obstacle> obstacles = new ArrayList<>();

    public void setupObstacles(int stage) {
        clearGrid();
        switch (stage) {
            case 1:
                setupStage1Obstacles();
                break;
            case 2:
                setupStage2Obstacles();
                break;
            default:
                setupRandomObstacles(250 + 50 * stage);
                break;
        }
        grid[0][0] = false;
        updateObstacles();
    }

    public void nextObstacles() {
        nextGeneration();

        obstacles.clear();
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 30; j++) {
                if (grid[i][j]) {
                    obstacles.add(new Obstacle(i * 20, j * 20, 20, 20));
                }
            }
        }

        if (obstacles.size() == 0) {
            setupRandomObstacles(250);
        }
    }

    public boolean checkCollision(Player player) {
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


    // 2回障害物を動かして，現在の位置と同じかどうかをチェック
    public boolean checkObstaclePosition() {
        boolean[][] copyGrid = new boolean[40][30];
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 30; j++) {
                copyGrid[i][j] = grid[i][j];
            }
        }

        nextGeneration();
        nextGeneration();

        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 30; j++) {
                if (copyGrid[i][j] != grid[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    private void clearGrid() {
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 30; j++) {
                grid[i][j] = false;
            }
        }
    }

    private void setupStage1Obstacles() {
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(40 - 17);
            int y = random.nextInt(30 - 17);
            initialPulsar(x, y);
        }
    }

    private void setupStage2Obstacles() {
        int x = 2;
        int y = 5;
        initialGosperGliderGun(x, y);
    }

    private void setupRandomObstacles(int num_obstacles) {
        Random random = new Random();
        int numberOfObstacles = random.nextInt(num_obstacles) + 1;
        for (int i = 0; i < numberOfObstacles; i++) {
            int x = random.nextInt(40);
            int y = random.nextInt(30);
            grid[x][y] = true;
        }
    }

    private void initialPulsar(int x, int y) {
        if (x < 0 || x + 17 >= 40 || y < 0 || y + 17 >= 30) {
            return;
        }
        // パルサー
        grid[x + 10][y + 10] = true;
        grid[x + 11][y + 10] = true;
        grid[x + 12][y + 10] = true;
        grid[x + 16][y + 10] = true;
        grid[x + 17][y + 10] = true;
        grid[x + 9][y + 12] = true;
        grid[x + 13][y + 12] = true;
        grid[x + 9][y + 13] = true;
        grid[x + 13][y + 13] = true;
        grid[x + 9][y + 14] = true;
        grid[x + 13][y + 14] = true;
        grid[x + 10][y + 15] = true;
        grid[x + 10][y + 16] = true;
        grid[x + 10][y + 17] = true;
    }

    private void initialGosperGliderGun(int x, int y) {
        if (x < 0 || x + 36 >= 40 || y < 0 || y + 9 >= 30) {
            return;
        }
        // Gosper Glider Gun
        grid[x + 1][y + 5] = true;
        grid[x + 2][y + 5] = true;
        grid[x + 1][y + 6] = true;
        grid[x + 2][y + 6] = true;

        grid[x + 13][y + 3] = true;
        grid[x + 14][y + 3] = true;
        grid[x + 12][y + 4] = true;
        grid[x + 16][y + 4] = true;
        grid[x + 11][y + 5] = true;
        grid[x + 17][y + 5] = true;
        grid[x + 11][y + 6] = true;
        grid[x + 15][y + 6] = true;
        grid[x + 17][y + 6] = true;
        grid[x + 18][y + 6] = true;
        grid[x + 11][y + 7] = true;
        grid[x + 17][y + 7] = true;
        grid[x + 12][y + 8] = true;
        grid[x + 16][y + 8] = true;
        grid[x + 13][y + 9] = true;
        grid[x + 14][y + 9] = true;

        grid[x + 25][y + 1] = true;
        grid[x + 23][y + 2] = true;
        grid[x + 25][y + 2] = true;
        grid[x + 21][y + 3] = true;
        grid[x + 22][y + 3] = true;
        grid[x + 21][y + 4] = true;
        grid[x + 22][y + 4] = true;
        grid[x + 21][y + 5] = true;
        grid[x + 22][y + 5] = true;
        grid[x + 23][y + 6] = true;
        grid[x + 25][y + 6] = true;
        grid[x + 25][y + 7] = true;

        grid[x + 35][y + 3] = true;
        grid[x + 36][y + 3] = true;
        grid[x + 35][y + 4] = true;
        grid[x + 36][y + 4] = true;
    }

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

                if (copyGrid[i][j]) {
                    grid[i][j] = (neighbors == 2 || neighbors == 3);
                } else {
                    grid[i][j] = (neighbors == 3);
                }
            }
        }
    }

    private int countNeighbors(int row, int col, boolean[][] grid) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                int newRow = row + i;
                int newCol = col + j;

                if (newRow >= 0 && newRow < 40 && newCol >= 0 && newCol < 30) {
                    if (grid[newRow][newCol]) {
                        count++;
                    }
                }
            }
        }
        return count;
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
}
