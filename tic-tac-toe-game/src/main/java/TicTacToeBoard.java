public class TicTacToeBoard {
    Integer[][] grid;
    int gridDimension;

    TicTacToeBoard(int gridDimension)
    {
        this.grid = new Integer[gridDimension][gridDimension];
        this.gridDimension = gridDimension;
    }

    void updatedBoard(int row, int col, int player)
    {
        grid[row][col] = player;
    }


}
