package chan.android.game.memoblock.generator;


import java.util.Random;

public class OneFourthGenerator implements Generatable {

    private Random random;

    private Position[] positions;

    public OneFourthGenerator() {
        random = new Random();
    }

    private void initializePositions(int row, int column) {
        positions = new Position[row * column];
        int i = 0;
        for (int r = 0; r < row; ++r) {
            for (int c = 0; c < column; ++c) {
                positions[i++] = new Position(r, c);
            }
        }
    }

    @Override
    public int generate(int[][] grid) {
        int row = grid.length;
        int column = grid[0].length;
        initializePositions(row, column);

        // Pick about 1/4
        int n = positions.length / 4;
        pick(n);

        int r;
        int c;
        for (int i = 0; i < n; ++i) {
            r = positions[i].x;
            c = positions[i].y;
            grid[r][c] = 1;
        }
        return n;
    }

    /**
     * @return A random number x between [from, to]
     * i.e.
     *      from <= x <= to
     */
    private int random(int from, int to) {
        return from + random.nextInt(to - from + 1);
    }

    /**
     * Pick n position by pick a random index between
     *      [0, m - 1] -> swap with 0
     *      [1, m - 1] -> swap with 1
     *      [2, m - 1] -> swap with 2
     *      ...
     *      ..
     *      [n, m - 1] -> swap with n
     * @param n
     */
    private void pick(int n) {
        int j = 0;
        int m = positions.length - 1;
        int i = 0;
        while (n-- > 0) {
            // Pick a random index from [i, m]
            j = random(i, m);
            // Swap it with the beginning of the array
            swap(i++, j);
        }
    }

    private void swap(int i, int j) {
        Position temp = positions[i];
        positions[i] = positions[j];
        positions[j] = temp;
    }

    private static class Position {
        final int x;
        final int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
