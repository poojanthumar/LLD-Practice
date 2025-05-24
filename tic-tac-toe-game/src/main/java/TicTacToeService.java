import java.util.Map;

public class TicTacToeService implements GameService {

    Map<Integer, TicTacToeGame> games;

    public Integer createNewGame(int gameDimension, int noOfPlayers, int minConsecutivesForWin)
    {
        TicTacToeGame ticTacToeGame = new TicTacToeGame(gameDimension,noOfPlayers,minConsecutivesForWin);
        int gameId = games.size() + 1;
        games.put(gameId, ticTacToeGame);
        return gameId;
    }

    public int getNextPlayer(int gameId)
    {
        return games.get(gameId).getNextPlayer();
    }

    public MoveResult makeMove(int row, int col,int player, int game)
    {
        return games.get(game).makeMove(row,col,player);
    }

}
