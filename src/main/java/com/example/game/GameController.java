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
    public GameData updateGameData(@RequestBody GameDataRequest request) {
        String status = "playing";
        player.x = request.player.x;
        player.y = request.player.y;
        int moveCount = request.moveCount;

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

        String[] result = generateAlertMessage(status, moveCount);
        status = result[0];
        String alertMessage = result[1];

        return new GameData(player, goal, obstacleManager.getObstacles(), status, alertMessage);
    }

    @GetMapping("/game-reset")
    public GameData gameReset(@RequestParam int stage) {
        player.x = 0;
        player.y = 0;
        obstacleManager.setupObstacles(stage);
        return new GameData(player, goal, obstacleManager.getObstacles(), "playing", "");
    }

    private boolean checkGoal() {
        return player.x < goal.x + goal.size &&
               player.x + player.size > goal.x &&
               player.y < goal.y + goal.size &&
               player.y + player.size > goal.y;
    }

    private String[] generateAlertMessage(String status, int moveCount) {
        String message = "";
        if (status.equals("game-over")) {
            if (moveCount < 68) {
                message = "ゲームオーバー！\n\n" + moveCount + "回しか動いてないのにね。\n\n悲しいね。";
            } else {
                message = "ゲームオーバー！\n\n" + moveCount + "回の移動でクリアできなかったね。\n\nすごい悲しいね。";
            }
        } else if (status.equals("game-clear")) {
            if (moveCount >= 68 * 5) {
                message = "ゲームクリア！\n\n" + moveCount + "回の移動でクリアしました！\n\nでも流石にかかりすぎだよね。\n\nもう一回やってみよっか!!";
                status = "retry";
            } else
            if (moveCount >= 68 * 2) {
                for (int i = 2; i < 5; i++) {
                    if (moveCount <= 68 * i * (3 / 2)) {
                        message = "ゲームクリア！\n\n" + moveCount + "回の移動でクリアしました！\n\nでも，最小回数の" + (i - 1) + "倍以上かかったね。\n正直ビビっちゃったね。";
                        break;
                    }
                }
            } else if (moveCount > 68) {
                if (obstacleManager.checkObstaclePosition()) {
                    message = "ゲームクリア！\n\n" + moveCount + "回の移動でクリアしました！\n\nでも，そんな単純な動きされたら簡単すぎるし\nそれでステージ上がっても嬉しくないよね？？？\n\nもう一回やってみよっか!!";
                    status = "retry";
                } else {
                    message = "ゲームクリア！\n\n" + moveCount + "回の移動でクリアしました！\n\nでも，もうちょっと早くクリアできるよね？？";
                }
            } else {
                message = "ゲームクリア！\n\n" + moveCount + "回の移動でクリアしました！\n\nすごい！";
            }
        }
        return new String[]{status, message};
    }
}
