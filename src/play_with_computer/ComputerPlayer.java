package play_with_computer;

/**
 * Created by El-Samman.
 */
public class ComputerPlayer {
    private int x, y;

    private int difficulty;

    private int [][] board;
    public int level = PlayWithComputer.level;
    private int boardCellUsed;

    public ComputerPlayer(int difficulty) {
        x = -1;
        y = -1;
        board = new int[3][3];
        if(level != 0){
            this.difficulty = difficulty;
        }
        boardCellUsed = 0;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                board[i][j] = 10;
            }
        }
    }

    public void playNextMove() {
        int score = -100000;

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if (board[i][j] == 10) {
                    board[i][j] = 0;
                    int curScore = -backtrack(3, 0, 0, difficulty);
                    if(curScore > score) {
                        score = curScore;
                        x = i;
                        y = j;
                    }
                    board[i][j] = 10;
                }
            }
        }
        boardCellUsed++;
        board[x][y] = 0;
    }

    public void playHumanMove(int x, int y) {
        board[x][y] = 1;
        boardCellUsed++;
    }
    
    public void playOtherHumanMove(int x, int y) {
        board[x][y] = 0;
        boardCellUsed++;
    }

    public boolean checkHumanWin() {
        return checkWin(3);
    }

    public boolean checkComputerWin() {
        return checkWin(0);
    }

    public boolean checkDraw() {
        return boardCellUsed == 9 && !checkWin(3);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private boolean checkWin(int score) {
        boolean row = false, col = false;
        int diagSum = 0, invDiagSum = 0;

        for(int i = 0; i < 3; i++) {
            int rowSum = 0;
            int colSum = 0;

            for(int j = 0; j < 3; j++) {
                rowSum += board[i][j];
                colSum += board[j][i];
            }
            diagSum += board[i][i];
            invDiagSum += board[i][2 - i];
            row |= (rowSum == score);
            col |= (colSum == score);

        }

        return row || col || (diagSum == score) || (invDiagSum == score);
    }

    public int backtrack(int player1, int player2, int depth, int difficulty) {
        if(checkWin(player1)) {
            return 1000;
        }

        if(checkWin(player2)) {
            return -1000;
        }

        if(depth == difficulty) {
            return 10000;
        }

        int score = -10000;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board[i][j] == 10) {
                    board[i][j] = (player1 == 0 ? 0 : 1);
                    int curScore = -backtrack(player2, player1, depth + 1, difficulty);
                    if(score < curScore)
                        score = curScore;
                    board[i][j] = 10;
                }
            }
        }
        if(score == -10000 || score == 0) {
            return 0;
        }
        else if(score < 0) {
            return score + 1;
        }
        else {
            return score - 1;
        }
    }

    public void printCurrentBoard() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                System.out.print(board[i][j] == 10 ? "-" : board[i][j] == 1 ? "X" : "O");
            }
            System.out.println();
        }
    }
}
