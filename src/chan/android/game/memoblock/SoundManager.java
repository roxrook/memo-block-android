package chan.android.game.memoblock;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public final class SoundManager {

    private int win;

    private int lose;

    private int move;

    private SoundPool soundPool;

    private AudioManager audioManager;

    private float volume;

    private boolean isOn;

    public SoundManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        move = soundPool.load(context, R.raw.tap, 1);
        lose = soundPool.load(context, R.raw.tap, 1);
        win = soundPool.load(context, R.raw.win, 1);
        isOn = true;
    }

    public void turnOn() {
        isOn = true;
    }

    public void turnOff() {
        isOn = false;
    }

    public void playWinSound() {
        if (isOn) {
            soundPool.play(win, volume, volume, 1, 0, 1.0f);
        }
    }

    public void playLoseSound() {
        if (isOn) {
            soundPool.play(lose, volume, volume, 1, 0, 1.0f);
        }
    }

    public void playMoveSound() {
        if (isOn) {
            soundPool.play(move, volume, volume, 1, 0, 1.0f);
        }
    }
}

