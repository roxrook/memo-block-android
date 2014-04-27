package chan.android.game.memoblock;


public enum Difficulty {

    EASY(3000),
    NORMAL(2000),
    DIFFICULT(1000),
    EXTREME(700);

    final long delayTime;

    Difficulty(long milliseconds) {
        delayTime = milliseconds;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public static Difficulty getEnum(long delayTime) {
        if (delayTime == 3000) {
            return EASY;
        } else if (delayTime == 2000) {
            return NORMAL;
        } else if (delayTime == 1000) {
            return DIFFICULT;
        } else if (delayTime == 700) {
            return EXTREME;
        }
        return EASY;
    }

    @Override
    public String toString() {
        if (delayTime == 3000) {
            return "Easy";
        } else if (delayTime == 2000) {
            return "Normal";
        } else if (delayTime == 1000) {
            return "Difficult";
        } else if (delayTime == 700) {
            return "Extreme";
        }
        return "Easy";
    }
}
