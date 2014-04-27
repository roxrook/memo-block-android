package chan.android.game.memoblock;


public class Cell {

    public int r;
    public int c;

    public Cell(int r, int c) {
        this.r = r;
        this.c = c;
    }

    public boolean equals(Cell other) {
        return r == other.r && c == other.c;
    }

    @Override
    public int hashCode() {
        int result = r;
        result = 31 * result + c;
        return result;
    }
}
