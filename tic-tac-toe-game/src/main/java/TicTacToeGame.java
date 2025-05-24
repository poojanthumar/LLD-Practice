import java.util.Objects;

public class TicTacToeGame {
    private TicTacToeBoard board;
    private int currentPlayer;
    private int noOfPlayers;
    private int minConsecutivesForWin;
    private int wonPlayer;
    private int chancesLeft;

    TicTacToeGame(int gameDimension, int noOfPlayers, int minConsecutivesForWin )
    {
        board = new TicTacToeBoard(gameDimension);
        currentPlayer = 0;
        this.noOfPlayers = noOfPlayers;
        this.minConsecutivesForWin = minConsecutivesForWin;
        chancesLeft = gameDimension*gameDimension;
    }
    int getNextPlayer()
    {
        return currentPlayer;
    }
    public MoveResult makeMove(int row, int col,int player)
    {
        if(currentPlayer != player) return new MoveResult(GameStatus.CONTINUE,MoveStatus.WRONG_PLAYER, player);
        if(chancesLeft < 0) return new MoveResult(GameStatus.WIN,MoveStatus.GAME_COMPLETED, wonPlayer);
        if(chancesLeft == 0) return new MoveResult(GameStatus.DRAW,MoveStatus.GAME_COMPLETED, currentPlayer);
        if(row >= board.getGridDimension()) return new MoveResult(GameStatus.CONTINUE, MoveStatus.OUT_OF_BOUNDS, player);
        if(col >= board.getGridDimension()) return new MoveResult(GameStatus.CONTINUE, MoveStatus.OUT_OF_BOUNDS, player);
        if(Objects.nonNull(board.getGridValue(row,col))) return new MoveResult(GameStatus.CONTINUE, MoveStatus.DUPLICATE, player);

        board.updatedBoard(row,col,currentPlayer);
        if(isWon(row,col)) {
            wonPlayer = currentPlayer;
            chancesLeft = -1;
            return new MoveResult(GameStatus.WIN,MoveStatus.GAME_COMPLETED,wonPlayer);
        }
        if(isDraw())
        {
            return new MoveResult(GameStatus.DRAW, MoveStatus.GAME_COMPLETED, currentPlayer);
        }
        currentPlayer = (currentPlayer + 1)%noOfPlayers;
        return new MoveResult(GameStatus.CONTINUE, MoveStatus.VALID, currentPlayer);
    }
    private boolean isDraw()
    {
        return chancesLeft <= 0;
    }

    public boolean isWon(int row, int col)
    {
        // check row
        int player = board.getGridValue(row,col);
        int consecutive = 1;
        int j = col + 1;
        while(j < board.getGridDimension() && player == board.getGridValue(row,j)) {
            j++;
            consecutive++;
        }
        j = col - 1;
        while(j > -1 && player == board.getGridValue(row,j)) {
            j--;
            consecutive++;
        }
        if(consecutive >= minConsecutivesForWin) {
            return true;
        }

        // check col
        consecutive = 1;
        int i = row + 1;
        while(i < board.getGridDimension() && player == board.getGridValue(i,col)) {
            i++;
            consecutive++;
        }
        i = row - 1;
        while(i > -1 && player == board.getGridValue(i,col)) {
            i--;
            consecutive++;
        }
        if(consecutive >= minConsecutivesForWin) {
            return true;
        }

        // check left diag
        consecutive = 1;
        i = row + 1;
        j = col + 1;
        while(i < board.getGridDimension() && j < board.getGridDimension() && player == board.getGridValue(i,j)) {
            i++;
            j++;
            consecutive++;
        }
        i = row - 1;
        j = col - 1;
        while(i > -1 && j > -1 && player == board.getGridValue(i,j)) {
            i--;
            j--;
            consecutive++;
        }
        if(consecutive >= minConsecutivesForWin) {
            return true;
        }


        // check right diag
        consecutive = 1;
        i = row - 1;
        j = col + 1;
        while(i > -1 && j < board.getGridDimension() && player == board.getGridValue(i,j)) {
            i--;
            j++;
            consecutive++;
        }
        i = row + 1;
        j = col - 1;
        while(i < board.getGridDimension() && j > -1 && player == board.getGridValue(i,j)) {
            i++;
            j--;
            consecutive++;
        }
        if(consecutive >= minConsecutivesForWin) {
            return true;
        }


        return false;
    }



}
