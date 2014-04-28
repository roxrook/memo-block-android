package chan.android.game.memoblock.highscore;


import chan.android.game.memoblock.Difficulty;

public interface HighScoreDataSource {

    public void insertNewScore(int score, Difficulty difficulty);

    public boolean deleteScore(String date);
}
