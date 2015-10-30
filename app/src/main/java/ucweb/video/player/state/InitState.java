package ucweb.video.player.state;

import android.util.Log;

import ucweb.video.player.UCMediaPlayer;

/**
 * desc: 初始状态
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
public class InitState implements IPlayState {

    private static final String TAG = InitState.class.getSimpleName();

    private UCMediaPlayer myMediaPlayer;

    private IStateChange iStateChange;

    public InitState(UCMediaPlayer myMediaPlayer, IStateChange iStateChange) {
        this.myMediaPlayer = myMediaPlayer;
        this.iStateChange = iStateChange;
    }

    @Override
    public void entry() {
        Log.i(TAG, "entry");
        myMediaPlayer.setInitState();
    }

    @Override
    public void doAction(int msgId) {
        Log.i(TAG, "doAction msgId = " + msgId);
        switch (msgId) {
            case PlayStateManager.PLAYER_INIT_ID:
                myMediaPlayer.setInitState();
                break;
            case PlayStateManager.PLAY_BTN_CLICK_ID:
                iStateChange.jump(PlayStateManager.STATE_PREPARE);
                break;
            case PlayStateManager.TURN_BTN_CLICK:
                iStateChange.jump(PlayStateManager.STATE_PREPARE);
                break;
            case PlayStateManager.PLAY_ID:
                iStateChange.jump(PlayStateManager.STATE_PREPARE);
                break;
        }
    }

    @Override
    public void exit() {

    }

}
