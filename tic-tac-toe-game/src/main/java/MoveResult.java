public class MoveResult {

    public GameStatus gameStatus;
    public MoveStatus moveStatus;
    public int player;

    public MoveResult(GameStatus gameStatus, MoveStatus moveStatus, int p) {
        this.gameStatus = gameStatus;
        this.moveStatus = moveStatus;
        this.player = p;
    }
}

enum GameStatus {
    STARTED,DRAW,WIN,CONTINUE
}

enum MoveStatus {
    VALID,OUT_OF_BOUNDS,DUPLICATE, WRONG_PLAYER, GAME_COMPLETED
}