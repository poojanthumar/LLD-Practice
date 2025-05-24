public class TicTacToeBoard {
    private Integer[][] grid;

    private int gridDimension;

    TicTacToeBoard(int gridDimension)
    {
        this.grid = new Integer[gridDimension][gridDimension];
        this.gridDimension = gridDimension;
    }

    void updatedBoard(int row, int col, int player)
    {
        grid[row][col] = player;
    }
    public int getGridDimension() {
        return gridDimension;
    }

    public Integer[][] getGrid() {
        return grid;
    }
    Integer getGridValue(int row, int col)
    {
        return grid[row][col];
    }


}
