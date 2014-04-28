package chan.android.game.memoblock.highscore;


import android.content.ContentValues;
import android.content.Context;
import chan.android.game.memoblock.Difficulty;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HighScoreManager implements HighScoreDataSource {

    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Context appContext;

    public HighScoreManager(Context context) {
        appContext = context.getApplicationContext();
    }

    @Override
    public void insertNewScore(int score, Difficulty difficulty) {
        ContentValues cv = new ContentValues();
        cv.put(HighScoreDbTable.COLUMN_SCORE, score);
        cv.put(HighScoreDbTable.COLUMN_LEVEL, difficulty.toString());
        Date now = new Date();
        cv.put(HighScoreDbTable.COLUMN_DATE, DATE_FORMATTER.format(now));
        appContext.getContentResolver().insert(HighScoreContentProvider.CONTENT_URI, cv);
    }

    @Override
    public boolean deleteScore(String date) {
        appContext.getContentResolver().delete(HighScoreContentProvider.CONTENT_URI, "date=?", new String[]{date});
        return false;
    }
}
