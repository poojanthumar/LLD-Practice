import java.util.Map;

public class TicTacToeService {

    Map<Integer, TicTacToeGame> games;

    Integer createNewGame(int gameDimension, int noOfPlayers, int minConsecutivesForWin)
    {
        TicTacToeGame ticTacToeGame = new TicTacToeGame(gameDimension,noOfPlayers,minConsecutivesForWin);
        int gameId = games.size() + 1;
        games.put(gameId, ticTacToeGame);
        return gameId;
    }

    int getNextPlayer(int gameId)
    {
        return games.get(gameId).getNextPlayer();
    }

    public MoveResult makeMove(int row, int col,int player, int game)
    {
        return games.get(game).makeMove(row,col,player);
    }

}
