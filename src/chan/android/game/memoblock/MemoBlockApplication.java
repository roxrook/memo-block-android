package chan.android.game.memoblock;

import android.app.Application;


public class MemoBlockApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GameSettings.initialize(this);
    }
}
