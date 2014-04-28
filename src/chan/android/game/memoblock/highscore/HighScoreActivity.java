package chan.android.game.memoblock.highscore;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import chan.android.game.memoblock.GameDialog;
import chan.android.game.memoblock.GameSettings;
import chan.android.game.memoblock.R;
import chan.android.game.memoblock.RoundedRectListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HighScoreActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private HighScoreCursorAdapter cursorAdapter;

    private HighScoreManager highScoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscore);

        highScoreManager = new HighScoreManager(this);

        // Set up list view
        RoundedRectListView listview = (RoundedRectListView) findViewById(R.id.highscore_$_listview);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) cursorAdapter.getItem(position - 1);
                confirmDelete(cursor);
                return true;
            }
        });
        View header = getLayoutInflater().inflate(R.layout.highscore_header, null);
        listview.addHeaderView(header);

        // Prepare loader
        getLoaderManager().initLoader(0, null, this);
        cursorAdapter = new HighScoreCursorAdapter(this);
        listview.setAdapter(cursorAdapter);
    }

    private void confirmDelete(final Cursor cursor) {
        final String date = cursor.getString(cursor.getColumnIndexOrThrow(HighScoreDbTable.COLUMN_DATE));
        final String score = cursor.getString(cursor.getColumnIndexOrThrow(HighScoreDbTable.COLUMN_SCORE));
        final GameDialog d = new GameDialog(this, "Are you sure you want to delete the score '" + score + "' achieved about " + readableTimeStamp(date) + " ?", "Cancel", "Yes");
        d.setOnGameDialogClickListener(new GameDialog.OnGameDialogClickListener() {
            @Override
            public void onLeftClick() {
                d.dismiss();
            }

            @Override
            public void onRightClick() {
                d.dismiss();
                highScoreManager.deleteScore(date);
            }
        });
        d.show();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] projection = {
                HighScoreDbTable.COLUMN_ID,
                HighScoreDbTable.COLUMN_SCORE,
                HighScoreDbTable.COLUMN_LEVEL,
                HighScoreDbTable.COLUMN_DATE
        };
        CursorLoader loader = new CursorLoader(this, HighScoreContentProvider.CONTENT_URI, projection, null, null, "score DESC");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    static class ViewHolder {
        TextView score;
        TextView date;
        TextView level;

        public ViewHolder(View v) {
            score = (TextView) v.findViewById(R.id.highscore_row_$_score);
            date = (TextView) v.findViewById(R.id.highscore_row_$_date);
            level = (TextView) v.findViewById(R.id.highscore_row_$_level);
        }
    }

    class HighScoreCursorAdapter extends CursorAdapter {

        private LayoutInflater inflater;

        public HighScoreCursorAdapter(Context context) {
            super(context, null, false);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = inflater.inflate(R.layout.highscore_row, parent, false);
            ViewHolder vh = new ViewHolder(view);
            view.setTag(vh);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final ViewHolder vh = (ViewHolder) view.getTag();
            final int score = cursor.getInt(cursor.getColumnIndexOrThrow(HighScoreDbTable.COLUMN_SCORE));
            final String date = cursor.getString(cursor.getColumnIndexOrThrow(HighScoreDbTable.COLUMN_DATE));
            final String level = cursor.getString(cursor.getColumnIndexOrThrow(HighScoreDbTable.COLUMN_LEVEL));
            String newest = GameSettings.getNewestScoreDate();
            if (newest != null && date.compareTo(newest) == 0) {
                view.setBackgroundColor(Color.YELLOW);
            } else {
                view.setBackgroundColor(Color.WHITE);
            }
            vh.level.setText(level);
            vh.score.setText(Integer.toString(score));
            vh.date.setText(readableTimeStamp(date));
        }
    }

    private String readableTimeStamp(String date) {
        try {
            Date before = DATE_FORMATTER.parse(date);
            Date now = new Date();
            long duration = now.getTime() - before.getTime();

            long days = TimeUnit.MILLISECONDS.toDays(duration);
            if (days > 0) {
                return days + " days ago";
            }

            long hours = TimeUnit.MILLISECONDS.toHours(duration);
            if (hours > 0) {
                return hours + " hours ago";
            }

            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            if (minutes > 0) {
                return minutes + " minutes ago";
            }

            return "just now";
        } catch (ParseException e) {
            // Should never happen
        }
        return "";
    }

    public void shareHighScore(int score) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Your new score " + Integer.toString(score) + "!");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Freecell New Highscore");
        startActivity(Intent.createChooser(shareIntent, "Share..."));
    }
}
