package ucweb.video.player.state;

import android.util.Log;

import ucweb.video.player.UCMediaPlayer;

/**
 * desc: 正在播放状态
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
public class PlayingState implements IPlayState {

    private static final String TAG = PlayingState.class.getSimpleName();

    private UCMediaPlayer myMediaPlayer;

    private IStateChange iStateChange;

    public PlayingState(UCMediaPlayer myMediaPlayer, IStateChange iStateChange) {
        this.myMediaPlayer = myMediaPlayer;
        this.iStateChange = iStateChange;
    }

    @Override
    public void entry() {
        Log.i(TAG, "entry");
        myMediaPlayer.setPlayState();
    }

    @Override
    public void doAction(int msgId) {
        Log.i(TAG, "doAction msgId = " + msgId);
        switch (msgId) {
            case PlayStateManager.TURN_BTN_CLICK:
                iStateChange.jump(PlayStateManager.STATE_PAUSE);
                break;
            case PlayStateManager.PROGRESS_SEEK_ID:
                myMediaPlayer.setProgressSeekPlayState();
                break;
            case PlayStateManager.PLAY_ERR_ID:
                iStateChange.jump(PlayStateManager.STATE_ERROR);
                break;
            case PlayStateManager.PLAY_COMPLETE_ID:
                iStateChange.jump(PlayStateManager.STATE_COMPLETE);
                break;
            case PlayStateManager.ACTIVITY_ON_STOP_ID:
                iStateChange.jump(PlayStateManager.STATE_PAUSE);
                break;
            case PlayStateManager.SET_PAUSE_ID:
                iStateChange.jump(PlayStateManager.STATE_PAUSE);
                break;
            case PlayStateManager.PLAY_ID:
                iStateChange.jump(PlayStateManager.STATE_PREPARE);
                break;
            case PlayStateManager.TOUCH_2_SEEK_END:
                if (iStateChange.getCurrState() == PlayStateManager.STATE_PLAYING) {
                    myMediaPlayer.setPlayState();
                }
                break;
        }
    }

    @Override
    public void exit() {

    }
}
