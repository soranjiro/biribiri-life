package com.example.game;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController {
    private final Player player = new Player(0, 0, 20);
    private final Goal goal = new Goal(780, 580, 20);
    private final ObstacleManager obstacleManager = new ObstacleManager();

    public GameController() {
        obstacleManager.setupObstacles(1);
    }

    @PostMapping("/game-data")
    public GameData updateGameData(@RequestBody Player newPlayer) {
        String status = "playing";
        player.x = newPlayer.x;
        player.y = newPlayer.y;

        // 障害物の動きを更新しオブジェクトを生成
        obstacleManager.nextObstacles();

        // 衝突チェック
        if (obstacleManager.checkCollision(player)) {
            status = "game-over";
        }

        // ゴールチェック
        if (checkGoal()) {
            status = "game-clear";
        }

        return new GameData(player, goal, obstacleManager.getObstacles(), status);
    }

    @GetMapping("/game-reset")
    public GameData gameReset(@RequestParam int stage) {
        player.x = 0;
        player.y = 0;
        obstacleManager.setupObstacles(stage);
        return new GameData(player, goal, obstacleManager.getObstacles(), "playing");
    }

    private boolean checkGoal() {
        return player.x < goal.x + goal.size &&
               player.x + player.size > goal.x &&
               player.y < goal.y + goal.size &&
               player.y + player.size > goal.y;
    }
}
