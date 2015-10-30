package ucweb.video.player.state;

import ucweb.video.player.UCMediaPlayer;

/**
 * desc: 播放错误状态
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
public class ErrorState implements IPlayState {

    private UCMediaPlayer myMediaPlayer;

    private IStateChange iStateChange;

    public ErrorState(UCMediaPlayer myMediaPlayer, IStateChange iStateChange) {
        this.myMediaPlayer = myMediaPlayer;
        this.iStateChange = iStateChange;
    }


    @Override
    public void entry() {
        myMediaPlayer.setPlayErrState();
    }

    @Override
    public void doAction(int msgId) {
        switch(msgId) {
            case PlayStateManager.PLAY_BTN_CLICK_ID:
                iStateChange.jump(PlayStateManager.STATE_PREPARE);
                break;
            case PlayStateManager.TURN_BTN_CLICK:
                iStateChange.jump(PlayStateManager.STATE_PREPARE);
                break;
            case PlayStateManager.NO_NETWORK_VIEW_ID:
                iStateChange.jump(PlayStateManager.STATE_PREPARE);
                break;
            case PlayStateManager.AVALID_NET_ID:
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
