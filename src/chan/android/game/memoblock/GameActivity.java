package chan.android.game.memoblock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.ViewFlipper;
import chan.android.game.memoblock.generator.Generatable;
import chan.android.game.memoblock.generator.NthRandomGenerator;
import chan.android.game.memoblock.generator.OneFourthGenerator;
import chan.android.game.memoblock.generator.RandomGenerator;
import chan.android.game.memoblock.highscore.HighScoreActivity;
import chan.android.game.memoblock.highscore.HighScoreDataSource;
import chan.android.game.memoblock.highscore.HighScoreManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GameActivity extends Activity implements PatternView.OnMoveListener {

    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Minimum grid pattern size r x c
     */
    private static final int MIN_ROW = 4;
    private static final int MIN_COLUMN = 4;

    /**
     * Maximum grid pattern size r x c
     */
    private static final int MAX_ROW = 5;
    private static final int MAX_COLUMN = 5;

    /**
     * The delay time between current game and next game in milliseconds
     */
    private static final long NEXT_GAME_DELAY_TIME = 500;

    /**
     * Flipper to switch between main menu and game
     */
    private ViewFlipper viewFlipper;

    /**
     * The main view to display pattern grid
     */
    private PatternView patternView;

    /**
     * The grid pattern of a game, this grid is used
     * by both patternView and this activity.
     * GameActivity handles of game logic
     * PatternView handles drawing logic
     */
    private PatternGrid patternGrid;

    /**
     * The current score view
     */
    private ScoreBoxView currentScoreBoxView;

    /**
     * Best score view update only when it reaches total game
     */
    private ScoreBoxView bestScoreBoxView;

    /**
     * The current number of block being removed
     */
    private TextView moveCountTextView;

    /**
     * The current number of row in game
     */
    private int row = MIN_ROW;

    /**
     * The current number of column in game
     */
    private int column = MIN_COLUMN;

    /**
     * Score for a single game
     */
    private int currentGameScore = 0;

    /**
     * Random generator
     */
    private Generatable randomGenerator;

    /**
     * 1/4 generator
     */
    private Generatable oneFourthGenerator;

    /**
     * 1/4 generator
     */
    private Generatable nthGenerator;


    /**
     * Random to switch between 2 generators
     */
    private Random random;

    /**
     * Sound effect
     */
    private SoundManager soundManager;

    /**
     * Vibrator effect
     */
    private Vibrator vibrator;

    /**
     * Data source for highscore
     */
    private HighScoreDataSource highScoreDataSource;

    /**
     * The time that user see the pattern before it disappear in milliseconds
     */
    private long showPatternTime = 2000;

    private AnimationSet slideInAnimation;

    private long timerCount = 0;

    private CountDownTimer nextGameTimer;

    private CountDownTimer removePatternTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        soundManager = new SoundManager(this);

        highScoreDataSource = new HighScoreManager(this);

        viewFlipper = (ViewFlipper) findViewById(R.id.game_$_viewflipper);

        TextView newGameTextView = (TextView) findViewById(R.id.game_$_textview_newgame);
        newGameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(1);
                restart();
            }
        });

        TextView settingsTextView = (TextView) findViewById(R.id.game_$_textview_settings);
        settingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(SettingsActivity.class);
            }
        });

        TextView highScoreTextView = (TextView) findViewById(R.id.game_$_textview_highscore);
        highScoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(HighScoreActivity.class);
            }
        });

        currentScoreBoxView = (ScoreBoxView) findViewById(R.id.game_$_scoreboxview_current);
        bestScoreBoxView = (ScoreBoxView) findViewById(R.id.game_$_scoreboxview_best);
        moveCountTextView = (TextView) findViewById(R.id.game_$_textview_move_count);
        patternView = (PatternView) findViewById(R.id.game_$_pattern_view);
        patternView.setOnMoveListener(this);

        random = new Random();
        randomGenerator = new RandomGenerator();
        oneFourthGenerator = new OneFourthGenerator();
        nthGenerator = new NthRandomGenerator();

        slideInAnimation = buildAnimation(patternView.getRootView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GameSettings.isSoundEnabled()) {
            soundManager.turnOn();
        } else {
            soundManager.turnOff();
        }

        bestScoreBoxView.setScore(GameSettings.getBestScore());
        bestScoreBoxView.invalidate();
        showPatternTime = GameSettings.getDifficulty().delayTime;
    }

    private AnimationSet buildAnimation(View view) {
        Animation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, view.getWidth(), Animation.ABSOLUTE, 0, Animation.ABSOLUTE, view.getHeight(), Animation.ABSOLUTE, 0);
        translateAnimation.setDuration(500);
        Animation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setFillBefore(false);
        AnimationSet animSet = new AnimationSet(false);
        animSet.addAnimation(translateAnimation);
        animSet.addAnimation(scaleAnimation);
        return animSet;
    }

    private void launchActivity(Class<?> clazz) {
        startActivity(new Intent(this, clazz));
    }

    private void restart() {
        row = MIN_ROW;
        column = MIN_COLUMN;
        currentGameScore = 0;
        timerCount = 0;
        currentScoreBoxView.setScore(currentGameScore);
        startNewGame(row, column);
    }

    private int pickRandom(int row, int column) {
        int n = row * column;
        int lower = n / 3;
        int upper = n / 2;
        int r = lower + random.nextInt(upper - lower + 1);
        return r;
    }

    private void startNewGame(int row, int column) {
        patternView.setActiveCell(-1, -1, MoveState.NEUTRAL);
        patternGrid = new PatternGrid(row, column);
        NthRandomGenerator r = (NthRandomGenerator) nthGenerator;
        r.setNumberOfBlocks(pickRandom(row, column));
        patternGrid.generate(nthGenerator);
        patternView.setPatternGrid(patternGrid);
        patternView.setTouchable(false);
        patternView.setShowPattern(true);
        patternView.invalidate();
        removePatternTimer = new RemovePatternTimer();
        removePatternTimer.start();
        moveCountTextView.setText(patternGrid.countRemoval() + " of " + patternGrid.getMarkedCount());
    }

    private int nextRow() {
        row++;
        if (row > MAX_ROW) {
            row = MAX_ROW;
        }
        return row;
    }

    private int nextColumn() {
        column++;
        if (column > MAX_COLUMN) {
            column = MAX_COLUMN;
        }
        return column;
    }

    private void cancelTimer(CountDownTimer timer) {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        if (viewFlipper.getDisplayedChild() == 1) {
            cancelTimer(nextGameTimer);
            cancelTimer(removePatternTimer);
            viewFlipper.setDisplayedChild(0);
        } else {
            super.onBackPressed();
        }
    }

    public void showGameOverDialog(int score) {
        if (currentGameScore > bestScoreBoxView.getScore()) {
            bestScoreBoxView.setScore(currentGameScore);
            GameSettings.persistBestScore(bestScoreBoxView.getScore());
        }

        if (currentGameScore > 0) {
            highScoreDataSource.insertNewScore(currentGameScore, GameSettings.getDifficulty());
            Date now = new Date();
            GameSettings.persistNewestScoreDate(DATE_FORMATTER.format(now));
        }

        final GameDialog d = new GameDialog(this, "Congratulation! Your new score is " + score + ". Play again?", "Cancel", "New Game");
        d.setOnGameDialogClickListener(new GameDialog.OnGameDialogClickListener() {
            @Override
            public void onLeftClick() {
                d.dismiss();
            }

            @Override
            public void onRightClick() {
                d.dismiss();
                restart();
            }
        });
        d.show();
    }

    @Override
    public void onMove(Cell cell) {
        patternGrid.removeMark(cell.r, cell.c);
        if (patternGrid.countRemoval() == patternGrid.getMarkedCount()) {
            patternView.setActiveCell(cell.r, cell.c, MoveState.WON);
            patternView.setTouchable(false);
            int score = patternGrid.getScore();
            currentScoreBoxView.addScore(score);
            currentGameScore += score;
            goToNextGame();
            soundManager.playWinSound();
        } else if (patternGrid.isMarked(cell.r, cell.c) == false) {
            patternView.setActiveCell(cell.r, cell.c, MoveState.LOST);
            patternView.setTouchable(false);
            patternView.setShowPattern(true);
            patternView.invalidate();
            showGameOverDialog(currentGameScore);
            soundManager.playLoseSound();
            vibrator.vibrate(500);
        } else {
            soundManager.playMoveSound();
        }

        int remaining = patternGrid.getMarkedCount() - patternGrid.countRemoval();
        if (remaining > 0) {
            moveCountTextView.setText(patternGrid.countRemoval() + " of " + patternGrid.getMarkedCount());
        } else {
            moveCountTextView.setText("Yay! no more");
        }
        patternView.invalidate();
    }

    private void goToNextGame() {
        nextGameTimer = new NextGameTimer();
        nextGameTimer.start();
    }

    private class RemovePatternTimer extends CountDownTimer {

        public RemovePatternTimer() {
            super(showPatternTime, 1000);
        }

        @Override
        public void onFinish() {
            patternView.setShowPattern(false);
            patternView.setTouchable(true);
            patternView.invalidate();
        }

        @Override
        public void onTick(long untilFinish) {
            // Don't care :)
        }
    }

    private class NextGameTimer extends CountDownTimer {

        public NextGameTimer() {
            super(NEXT_GAME_DELAY_TIME, 1000);
        }

        @Override
        public void onFinish() {
            patternView.startAnimation(slideInAnimation);
            startNewGame(nextRow(), nextColumn());
        }

        @Override
        public void onTick(long untilFinish) {
            // Don't care :)
        }
    }
}

