public class GameOfLife {

    // グリッドのサイズ
    private static final int ROWS = 5;
    private static final int COLS = 5;

    // ライフゲームのグリッド（trueが生きているセル、falseが死んでいるセル）
    private boolean[][] grid = new boolean[ROWS][COLS];

    // コンストラクタで初期状態を設定（例: いくつかのセルを生かす）
    public GameOfLife() {
        grid[1][2] = true;
        grid[2][2] = true;
        grid[3][2] = true;
    }

    // 隣接するセルの数を数えるメソッド
    private int countNeighbors(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                // 自分自身を除外
                if (i == 0 && j == 0) continue;

                int newRow = row + i;
                int newCol = col + j;

                // 境界チェック
                if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS) {
                    if (grid[newRow][newCol]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    // ライフゲームの状態を1世代進めるメソッド
    public void nextGeneration() {
        boolean[][] newGrid = new boolean[ROWS][COLS]; // 次の世代を保存する新しいグリッド

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int neighbors = countNeighbors(i, j);

                // 生きているセルが過疎または過密な場合は死ぬ
                if (grid[i][j]) {
                    newGrid[i][j] = (neighbors == 2 || neighbors == 3);
                }
                // 死んでいるセルがちょうど3つの生きたセルに囲まれている場合は生き返る
                else {
                    newGrid[i][j] = (neighbors == 3);
                }
            }
        }

        // 新しいグリッドを現在のグリッドにコピー
        grid = newGrid;
    }

    // グリッドの状態を1次元で表示するメソッド
    public void printGrid() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                // trueなら'1'、falseなら'0'で表示
                System.out.print(grid[i][j] ? "1" : "0");
            }
        }
        System.out.println();  // 一行終わった後に改行
    }

    public static void main(String[] args) {
        GameOfLife game = new GameOfLife();

        System.out.println("Initial Generation:");
        game.printGrid();

        // 5世代分、ライフゲームの状態を進めて表示
        for (int gen = 1; gen <= 5; gen++) {
            game.nextGeneration();
            System.out.println("\nGeneration " + gen + ":");
            game.printGrid();
        }
    }
}
