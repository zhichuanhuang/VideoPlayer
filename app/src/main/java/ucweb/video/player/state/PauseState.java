package ucweb.video.player.state;

import android.util.Log;

import ucweb.video.player.UCMediaPlayer;

/**
 * desc: 暂停状态
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/9/8
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class PauseState implements IPlayState {

    private static final String TAG = PauseState.class.getSimpleName();

    private UCMediaPlayer myMediaPlayer;

    private IStateChange iStateChange;

    public PauseState(UCMediaPlayer myMediaPlayer, IStateChange iStateChange) {
        this.myMediaPlayer = myMediaPlayer;
        this.iStateChange = iStateChange;
    }

    @Override
    public void entry() {
        Log.i(TAG, "entry");
        myMediaPlayer.setPauseState();
    }

    @Override
    public void doAction(int msgId) {
        Log.i(TAG, "doAction msgId = " + msgId);
        switch (msgId) {
            case PlayStateManager.TURN_BTN_CLICK:
                iStateChange.jump(PlayStateManager.STATE_PLAYING);
                break;
            case PlayStateManager.PROGRESS_SEEK_ID:
                myMediaPlayer.setProgressSeekPauseState();
                break;
            case PlayStateManager.PLAY_BTN_CLICK_ID:
                iStateChange.jump(PlayStateManager.STATE_PLAYING);
                break;
            case PlayStateManager.ACTIVITY_ON_RESUME_ID:
                iStateChange.jump(PlayStateManager.STATE_PLAYING);
                break;
            case PlayStateManager.PLAY_ID:
                iStateChange.jump(PlayStateManager.STATE_PREPARE);
                break;
            case PlayStateManager.TOUCH_2_SEEK:
                myMediaPlayer.touch2seek();
                break;
            case PlayStateManager.TOUCH_2_SEEK_END:
                if (iStateChange.getCurrState() == PlayStateManager.STATE_PAUSE) {
                    iStateChange.jump(PlayStateManager.STATE_PAUSE);
                }
                break;
        }
    }

    @Override
    public void exit() {
        myMediaPlayer.setPlayResumeState();
    }

}
