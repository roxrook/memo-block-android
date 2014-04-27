package chan.android.game.memoblock;


import chan.android.game.memoblock.generator.Generatable;
import chan.android.game.memoblock.generator.RandomGenerator;

public class PatternGrid {

    public static final int EMPTY = 0;

    public static final int MARKED = 1;

    public static final int MARK_REMOVED = 2;

    private int row;

    private int column;

    private int[][] grid;

    private Generatable generator;

    private int count;

    private int score;

    public PatternGrid(int row, int column) {
        this.row = row;
        this.column = column;
        this.grid = new int[row][column];
        this.generator = new RandomGenerator();
        this.count = 0;
        this.score = 0;
    }

    public void generate(Generatable generator) {
        this.generator = generator;
        count = generator.generate(grid);
        score = count * 5;
    }

    public int getMarkedCount() {
        return count;
    }

    public boolean isMarked(int r, int c) {
        if (!isValid(r, c)) {
            throw new RuntimeException("Invalid index for [" + r + ","  + c + "], grid size[" + row + "," + column + "]");
        }
        return grid[r][c] == MARKED || grid[r][c] == MARK_REMOVED;
    }

    public boolean isMarkRemoved(int r, int c) {
        if (!isValid(r, c)) {
            throw new RuntimeException("Invalid index for [" + r + ","  + c + "], grid size[" + row + "," + column + "]");
        }
        return grid[r][c] == MARK_REMOVED;
    }

    public void removeMark(int r, int c) {
        if (!isValid(r, c)) {
            throw new RuntimeException("Invalid index for [" + r + ","  + c + "], grid size[" + row + "," + column + "]");
        }
        if (grid[r][c] == MARKED) {
            grid[r][c] = MARK_REMOVED;
        }
    }

    public int countRemoval() {
        int cnt = 0;
        for (int r = 0; r < row; ++r) {
            for (int c = 0; c < column; ++c) {
                if (grid[r][c] == MARK_REMOVED) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    private boolean isValid(int r, int c) {
        return !(r < 0 || c < 0 || r >= row || c >= column);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void makeNewGrid(int row, int column) {
        this.row = row;
        this.column = column;
        this.grid = new int[row][column];
        this.count = 0;
        this.score = 0;
    }

    public void setDimension(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getScore() {
        return score;
    }
}
