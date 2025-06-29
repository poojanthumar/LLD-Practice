import java.util.concurrent.CyclicBarrier;

public class TicTacToe {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}

class Game {
    Integer[][] board;
    int n; // size of board
    int m; // number of players
    int p; // number of consecutive markers required to win
    int currentPlayerId;
    int chancesLeft;
    StatusType status;

    public Game(int n, int m, int p) {
        this.n = n;
        this.m = m;
        this.p = p;
        board = new Integer[n][n];
        currentPlayerId = 0;
        chancesLeft = n * n;
        status = StatusType.INPROGRESS;
    }

    public Integer[][] getBoard() {
        return this.board;
    }

    public int getChancesLeft() {
        return this.chancesLeft;
    }

    public int getN() {
        return this.n;
    }

    public int getM() {
        return this.m;
    }

    public int getP() {
        return this.p;
    }

    public boolean isCurrentPlayer(int playerId) {
        return currentPlayerId == playerId;
    }

    public boolean canPlaceMarker(int row, int col) {
        return isInsideBoard(row) && isInsideBoard(col) && null == board[row][col];
    }

    public void placeMarker(int row, int col, int player) {
        board[row][col] = player;
        this.chancesLeft--;
    }

    private boolean isInsideBoard(int x) {
        return x > - 1 && x < n;
    }
}

class Result {
    StatusType status;
    Integer playerId; // If the game is won -> who won it. If a game is ongoing -> who has the next chance
    boolean wasLegalMove;

    public Result(StatusType status, Integer playerId, boolean wasLegalMove) {
        this.status = status;
        this.playerId = playerId;
        this.wasLegalMove = wasLegalMove;
    }
}

enum StatusType {
    DRAW, WIN, INPROGRESS
}

interface GameService2 {
    public Game createGame(int n, int m, int p);
    public Result placeMarker(int player, int row, int col, Game game);
}

class GameServiceImpl implements GameService2 {
    public Game createGame(int n, int m, int p) {
        return new Game(n, m, p);
    }

    public Result placeMarker(int player, int row, int col, Game game) {
        if(!isValidMove(player, row, col, game)) {
            return new Result(null, null, false);
        }
        game.placeMarker(row, col, player);
        if(isWon(game, row, col)) {
            return new Result(StatusType.WIN, player, true);
        }
        if(isDraw(game)) {
            return new Result(StatusType.DRAW, null, true);
        }
        return new Result(StatusType.INPROGRESS, getNextPlayer(game, player), true);
    }

    private boolean isValidMove(int player, int row, int col, Game game) {
        return null != game && game.isCurrentPlayer(player) && game.canPlaceMarker(row, col);
    }

    private boolean isWon(Game game, int row, int col) {
        Integer[][] board = game.getBoard();
        int player = board[row][col];
        int p = game.getP();
        int n = game.getN();

        // check row
        int consecutive = 1;
        int j = col + 1;
        while(j < n && player == board[row][j]) {
            j++;
            consecutive++;
        }
        j = col - 1;
        while(j > -1 && player == board[row][j]) {
            j--;
            consecutive++;
        }
        if(consecutive >= p) {
            return true;
        }

        // check col
        consecutive = 1;
        int i = row + 1;
        while(i < n && player == board[i][col]) {
            i++;
            consecutive++;
        }
        i = row - 1;
        while(i > -1 && player == board[i][col]) {
            i--;
            consecutive++;
        }
        if(consecutive >= p) {
            return true;
        }

        // check left diag
        consecutive = 1;
        i = row + 1;
        j = col + 1;
        while(i < n && j < n && player == board[i][j]) {
            i++;
            j++;
            consecutive++;
        }
        i = row - 1;
        j = col - 1;
        while(i > -1 && j > -1 && player == board[i][j]) {
            i--;
            j--;
            consecutive++;
        }
        if(consecutive >= p) {
            return true;
        }


        // check right diag
        consecutive = 1;
        i = row - 1;
        j = col + 1;
        while(i > -1 && j < n && player == board[i][j]) {
            i--;
            j++;
            consecutive++;
        }
        i = row + 1;
        j = col - 1;
        while(i < n && j > -1 && player == board[i][j]) {
            i++;
            j--;
            consecutive++;
        }
        if(consecutive >= p) {
            return true;
        }


        return false;
    }

    private boolean isDraw(Game game) {
        return 0 == game.getChancesLeft();
    }

    private int getNextPlayer(Game game, int currentPlayer) {
        return (currentPlayer + 1) % game.getM();
    }
}