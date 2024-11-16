document.addEventListener("DOMContentLoaded", () => {
  const canvas = document.getElementById("gameCanvas");
  const ctx = canvas.getContext("2d");
  let player = { x: 0, y: 0, size: 20 };
  let isRequestInProgress = false;
  let stage = 1;
  const stageNumberElement = document.getElementById("stageNumber");
  let moveCount = 0;
  const moveCountElement = document.getElementById("moveCount");
  console.log("moveCount", moveCount);

  // キーボード入力を受け付ける
  document.addEventListener("keydown", function (event) {
    if (isRequestInProgress) return;

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
      default:
        break;
    }
    if (positionChanged) {
      moveCount++;
      moveCountElement.textContent = moveCount.toString();
      getGameData();
    }
  });

  async function getGameData() {
    isRequestInProgress = true;
    try {
      // ゲームデータを取得
      const response = await fetch("/game-data", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(player),
      });
      const data = await response.json();
      player = data.player;
      await drawGame(data);

      // ゲームオーバーまたはゲームクリアの場合
      if (data.status === "game-over" || data.status === "game-clear") {
        // ヒットストップ
        for (let i = 0; i < 3; i++) {
          ctx.fillStyle = "pink";
          ctx.fillRect(player.x, player.y, player.size, player.size);
          await new Promise((resolve) => setTimeout(resolve, 250));
          ctx.fillStyle = "blue";
          ctx.fillRect(player.x, player.y, player.size, player.size);
          await new Promise((resolve) => setTimeout(resolve, 250));
        }

        // ゲームオーバーまたはゲームクリアのアラートを表示
        let alertMessage = "";
        if (data.status === "game-over") {
          alertMessage = "ゲームオーバー";
        }
        else if (moveCount > 68) {
          alertMessage = "ゲームクリア！\n\n" + moveCount + "手でクリアしました！\n\nでももう少し早くクリアできるよね？";
        }
        else {
          alertMessage = "ゲームクリア！\n\n" + moveCount + "手でクリアしました！\n\nすごい！";
        }
        alert(alertMessage);

        // ゲームリセット
        if (data.status === "game-over") {
          stage = 1;
        } else {
          stage++;
        }
        stageNumberElement.textContent = stage;
        await resetGame();
      }

    } catch (error) {
      console.error("Error fetching game data:", error);
    } finally {
      isRequestInProgress = false;
    }
  }

  async function resetGame() {
    try {
      const response = await fetch(`/game-reset?stage=${stage}`, {
        method: "GET",
      });
      const data = await response.json();
      player = data.player;
      await drawGame(data);
      moveCount = 0;
      moveCountElement.textContent = moveCount;
    } catch (error) {
      console.error("Error resetting game:", error);
    }
  }

  async function drawGame(data) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

	// プレーヤーを描画
	ctx.fillStyle = "blue";
	ctx.fillRect(player.x, player.y, player.size, player.size);

    // ゴールを描画
    ctx.fillStyle = "green";
    ctx.fillRect(data.goal.x, data.goal.y, data.goal.size, data.goal.size);

    // 障害物を描画
    ctx.fillStyle = "red";
    data.obstacles.forEach((obstacle) => {
      ctx.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
    });
  }

  resetGame();
});
