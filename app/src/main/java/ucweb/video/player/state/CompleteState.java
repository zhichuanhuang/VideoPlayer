package ucweb.video.player.state;

import ucweb.video.player.UCMediaPlayer;

/**
 * desc: 完成播放状态
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
public class CompleteState implements IPlayState {

    private UCMediaPlayer myMediaPlayer;

    private IStateChange iStateChange;

    public CompleteState(UCMediaPlayer myMediaPlayer, IStateChange iStateChange) {
        this.myMediaPlayer = myMediaPlayer;
        this.iStateChange = iStateChange;
    }

    @Override
    public void entry() {
        myMediaPlayer.setCompleteState();
    }

    @Override
    public void doAction(int msgId) {
        switch (msgId) {
            case PlayStateManager.PLAY_BTN_CLICK_ID:
                iStateChange.jump(PlayStateManager.STATE_PLAYING);
                myMediaPlayer.setComplete2Replay();
                break;
            case PlayStateManager.TURN_BTN_CLICK:
                iStateChange.jump(PlayStateManager.STATE_PLAYING);
                myMediaPlayer.setComplete2Replay();
                break;
            case PlayStateManager.PLAY_ID:
                iStateChange.jump(PlayStateManager.STATE_PREPARE);
                break;
            case PlayStateManager.TOUCH_2_SEEK:
                myMediaPlayer.touch2seek();
                break;
            case PlayStateManager.TOUCH_2_SEEK_END:
                if (iStateChange.getCurrState() == PlayStateManager.STATE_COMPLETE) {
                    iStateChange.jump(PlayStateManager.STATE_COMPLETE);
                }
                break;
        }
    }

    @Override
    public void exit() {

    }
}
