package chan.android.game.memoblock;


import android.content.Context;
import android.content.SharedPreferences;

public class GameSettings {

    private enum Key {
        ENABLE_SOUND,
        ENABLE_CLOCK,
        BEST_SCORE,
        DIFFICULTY,
        NEWEST_SCORE_DATE,
    }

    private static SharedPreferences prefs;

    public static void initialize(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences("chan.android.game.memoblock.prefs_", Context.MODE_PRIVATE);
        }
    }

    public static void persistNewestScoreDate(String date) {
        prefs.edit().putString(Key.NEWEST_SCORE_DATE.name(), date).commit();
    }

    public static String getNewestScoreDate() {
        return prefs.getString(Key.NEWEST_SCORE_DATE.name(), null);
    }

    public static void persistBestScore(int score) {
        prefs.edit().putInt(Key.BEST_SCORE.name(), score).commit();
    }

    public static int getBestScore() {
        return prefs.getInt(Key.BEST_SCORE.name(), 0);
    }

    public static void persistEnableSound(boolean enabled) {
        prefs.edit().putBoolean(Key.ENABLE_SOUND.name(), enabled).commit();
    }

    public static boolean isSoundEnabled() {
        return prefs.getBoolean(Key.ENABLE_SOUND.name(), true);
    }

    public static void persistEnableClock(boolean enabled) {
        prefs.edit().putBoolean(Key.ENABLE_CLOCK.name(), enabled).commit();
    }

    public static boolean isClockEnabled() {
        return prefs.getBoolean(Key.ENABLE_CLOCK.name(), true);
    }

    public static void persistDifficulty(Difficulty difficulty) {
        prefs.edit().putLong(Key.DIFFICULTY.name(), difficulty.getDelayTime()).commit();
    }

    public static Difficulty getDifficulty() {
        return Difficulty.getEnum(prefs.getLong(Key.DIFFICULTY.name(), Difficulty.EASY.delayTime));
    }
}
