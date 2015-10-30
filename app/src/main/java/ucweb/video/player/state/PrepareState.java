package ucweb.video.player.state;

import android.util.Log;

import ucweb.video.player.UCMediaPlayer;

import static ucweb.video.player.state.PlayStateManager.*;

/**
 * desc: 准备播放状态
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
public class PrepareState implements IPlayState {

    private static final String TAG = PrepareState.class.getSimpleName();

    private UCMediaPlayer myMediaPlayer;

    private IStateChange iStateChange;

    public PrepareState(UCMediaPlayer myMediaPlayer, IStateChange iStateChange) {
        this.myMediaPlayer = myMediaPlayer;
        this.iStateChange = iStateChange;
    }

    @Override
    public void entry() {
        Log.i(TAG, "entry");
        myMediaPlayer.setPrepareState();
    }

    @Override
    public void doAction(int msgId) {
        Log.i(TAG, "doAction msgId = " + msgId);
        switch (msgId) {
            case NO_NET_ERR_ID:
                iStateChange.jump(STATE_ERROR);
                break;
            case URI_ERR_ID:
                iStateChange.jump(STATE_INIT);
                break;
            case PLAY_BTN_CLICK_ID:
                //
                break;
            case ON_PREPARED_ID:
                iStateChange.jump(STATE_PLAYING);
                break;
        }
    }

    @Override
    public void exit() {

    }
}
