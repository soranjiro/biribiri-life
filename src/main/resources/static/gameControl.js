document.addEventListener("DOMContentLoaded", () => {
    const canvas = document.getElementById("gameCanvas");
    const ctx = canvas.getContext("2d");
    let player = { x: 0, y: 0, size: 20 };

    document.addEventListener("keydown", (event) => {
        switch (event.key) {
            case "ArrowUp":
                player.y = Math.max(0, player.y - 20);
                break;
            case "ArrowDown":
                player.y = Math.min(canvas.height - player.size, player.y + 20);
                break;
            case "ArrowLeft":
                player.x = Math.max(0, player.x - 20);
                break;
            case "ArrowRight":
                player.x = Math.min(canvas.width - player.size, player.x + 20);
                break;
        }
    });

    function fetchGameData() {
        fetch("/game-data")
            .then(response => response.json())
            .then(data => {
                if (data.status === "game-over") {
                    alert("ゲームオーバー");
                    return;
                }
                if (data.status === "game-clear") {
                    alert("ゲームクリア");
                    return;
                }
                drawGame(data);
            })
            .catch(error => console.error("Error fetching game data:", error));
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

    setInterval(fetchGameData, 100); // 100ミリ秒ごとにゲームデータを取得
});
