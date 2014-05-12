package chan.android.game.memoblock;


public enum Difficulty {

    EASY(1000),
    NORMAL(800),
    DIFFICULT(700),
    EXTREME(600);

    final long delayTime;

    Difficulty(long milliseconds) {
        delayTime = milliseconds;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public static Difficulty getEnum(long delayTime) {
        if (delayTime == EASY.delayTime) {
            return EASY;
        } else if (delayTime == NORMAL.delayTime) {
            return NORMAL;
        } else if (delayTime == DIFFICULT.delayTime) {
            return DIFFICULT;
        } else if (delayTime == EXTREME.delayTime) {
            return EXTREME;
        }
        return EASY;
    }

    @Override
    public String toString() {
        if (delayTime == EASY.delayTime) {
            return "Easy";
        } else if (delayTime == NORMAL.delayTime) {
            return "Normal";
        } else if (delayTime == DIFFICULT.delayTime) {
            return "Difficult";
        } else if (delayTime == EXTREME.delayTime) {
            return "Extreme";
        }
        return "Easy";
    }
}
