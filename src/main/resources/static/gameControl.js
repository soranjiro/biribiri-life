document.addEventListener("DOMContentLoaded", () => {
    const canvas = document.getElementById("gameCanvas");
    const ctx = canvas.getContext("2d");
    let player = { x: 0, y: 0, size: 20 };

    document.addEventListener("keydown", function(event) {
        let positionChanged = false;
        switch (event.key) {
            case "ArrowUp":
                player.y = Math.max(0, player.y - 20);
                positionChanged = true;
                break;
            case "ArrowDown":
                player.y = Math.min(canvas.height - player.size, player.y + 20);
                positionChanged = true;
                break;
            case "ArrowLeft":
                player.x = Math.max(0, player.x - 20);
                positionChanged = true;
                break;
            case "ArrowRight":
                player.x = Math.min(canvas.width - player.size, player.x + 20);
                positionChanged = true;
                break;
        }
        if (positionChanged) {
            updatePlayerPosition();
        }
    });

    function updatePlayerPosition() {
        fetch("/game-data", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(player)
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === "game-over" || data.status === "game-clear") {
                alert(data.status === "game-over" ? "ゲームオーバー" : "ゲームクリア");
                resetGame();
                return;
            }
            player = data.player;
            drawGame(data);
        })
        .catch(error => console.error("Error updating player position:", error));
    }

    function resetGame() {
        player = { x: 0, y: 0, size: 20 };
    }

    function drawGame(data) {
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        // プレーヤーを描画
        ctx.fillStyle = "blue";
        ctx.fillRect(player.x, player.y, player.size, player.size);

        // ゴールを描画
        ctx.fillStyle = "green";
        ctx.fillRect(data.goal.x, data.goal.y, data.goal.size, data.goal.size);

        // 障害物を描画
        ctx.fillStyle = "red";
        data.obstacles.forEach(obstacle => {
            ctx.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        });
    }

});
