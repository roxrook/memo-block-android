package chan.android.game.memoblock.generator;

import chan.android.game.memoblock.PatternGrid;

import java.util.Random;

public class RandomGenerator implements Generatable {

    private final int RANDOM_NUMBER = 5;

    private Random random;

    public RandomGenerator() {
        random = new Random();
    }

    @Override
    public int generate(int[][] grid) {
        int row = grid.length;
        int column = grid[0].length;
        int count = 0;
        for (int r = 0; r < row; ++r) {
            for (int c = 0; c < column; ++c) {
                if (random.nextInt(RANDOM_NUMBER) == 0) {
                    grid[r][c] = PatternGrid.MARKED;
                    count++;
                } else {
                    grid[r][c] = PatternGrid.EMPTY;
                }
            }
        }
        // Guarantee that we have at least 1 marker
        if (count == 0) {
            int randRow = random.nextInt(row);
            int randColumn = random.nextInt(column);
            grid[randRow][randColumn] = PatternGrid.MARKED;
            ++count;
        }
        return count;
    }
}
