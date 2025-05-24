public interface GameService {
    Integer createNewGame(int gameDimension, int noOfPlayers, int minConsecutivesForWin);
    int getNextPlayer(int gameId);
    MoveResult makeMove(int row, int col,int player, int game);
}
