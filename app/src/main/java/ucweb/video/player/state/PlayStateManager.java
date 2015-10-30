package ucweb.video.player.state;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ucweb.video.player.UCMediaPlayer;

/**
 * desc: 播放状态管理类
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
public class PlayStateManager implements IStateChange {

    private static final String TAG = PlayStateManager.class.getSimpleName();

    /** 初始化播放器事件*/
    public static final int PLAYER_INIT_ID = 0x1000001;

    /** 中间播放按钮点击事件id*/
    public static final int PLAY_BTN_CLICK_ID = 0x1000002;

    /** 缩放按钮点击事件id*/
    public static final int SCALE_BTN_CLICK_ID = 0x1000003;

    /** 播放事件*/
    public static final int PLAY_ID = 0x1000004;

    /** 无网络事件*/
    public static final int NO_NET_ERR_ID = 0x1000005;

    /** 播放地址出错事件*/
    public static final int URI_ERR_ID = 0x1000006;

    /** 播放和暂停按钮点击事件ID*/
    public static final int TURN_BTN_CLICK = 0x1000007;

    /** 准备完毕事件ID*/
    public static final int ON_PREPARED_ID = 0x1000008;

    /** 返回按钮点击事件ID*/
    public static final int BACK_BTN_CLICK_ID = 0x1000009;

    /** 进度条拖动事件ID*/
    public static final int PROGRESS_SEEK_ID = 0x1000010;

    /** 播放错误事件ID*/
    public static final int PLAY_ERR_ID = 0x1000011;

    /** 播放完成事件ID*/
    public static final int PLAY_COMPLETE_ID = 0x1000012;

    /** web.activity onStop事件ID*/
    public static final int ACTIVITY_ON_STOP_ID = 0x1000013;

    /** web.activity onResume事件ID*/
    public static final int ACTIVITY_ON_RESUME_ID = 0x1000014;

    /** 主动设置暂停事件ID*/
    public static final int SET_PAUSE_ID = 0x1000015;

    /** 无网络view点击事件ID*/
    public static final int NO_NETWORK_VIEW_ID = 0x1000016;

    /** 有网络是事件ID*/
    public static final int AVALID_NET_ID = 0x1000017;

    /** 滑动屏幕拖动视频进度事件ID*/
    public static final int TOUCH_2_SEEK = 0x1000018;

    /** 滑动屏幕拖动视频进度结束事件ID*/
    public static final int TOUCH_2_SEEK_END = 0x1000019;

    /************************华丽的分割线********************/

    /** 初始化状态*/
    protected static final int STATE_INIT = 0;

    /** 准备状态*/
    protected static final int STATE_PREPARE = 1;

    /** 播放状态*/
    protected static final int STATE_PLAYING = 2;

    /** 暂停状态*/
    protected static final int STATE_PAUSE = 3;

    /** 完成状态*/
    protected static final int STATE_COMPLETE = 4;

    /** 错误状态*/
    protected static final int STATE_ERROR = 5;

    private int currStateInt;

    private IPlayState currState;

    /** 状态列表*/
    private List<IPlayState> stateList = new ArrayList<IPlayState>();

    public PlayStateManager(UCMediaPlayer myMediaPlayer) {
        InitState initState = new InitState(myMediaPlayer, this);
        PrepareState prepareState = new PrepareState(myMediaPlayer, this);
        PlayingState playingState = new PlayingState(myMediaPlayer, this);
        PauseState pauseState = new PauseState(myMediaPlayer, this);
        CompleteState completeState = new CompleteState(myMediaPlayer, this);
        ErrorState errorState = new ErrorState(myMediaPlayer, this);
        stateList.add(initState);
        stateList.add(prepareState);
        stateList.add(playingState);
        stateList.add(pauseState);
        stateList.add(completeState);
        stateList.add(errorState);

        currState = initState;
    }

    public void handleMessage(int msgId) {
        Log.i(TAG, "handleMessage currStateInt = " + currStateInt);
        currState.doAction(msgId);
    }

    public void jump(int state) {
        currState.exit();
        currState = stateList.get(state);
        currState.entry();
        currStateInt = state;
    }

    /** 提供外部访问当前状态*/
    public int getCurrState() {
        return currStateInt;
    }

}
