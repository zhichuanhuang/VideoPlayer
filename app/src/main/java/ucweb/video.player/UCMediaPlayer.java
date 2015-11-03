package ucweb.video.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;

import ucweb.video.player.listener.ControllerCallback;
import ucweb.video.player.listener.MediaPlayerCallback;
import ucweb.video.player.listener.VideoViewCallBack;
import ucweb.video.player.listener.OnPauseListener;
import ucweb.video.player.listener.OnPlayListener;
import ucweb.video.player.listener.OnSeekListener;
import ucweb.video.player.manager.PlayerParams;
import ucweb.video.player.manager.PlayerUIManager;
import ucweb.video.player.view.PlayerVideoView;
import ucweb.web.model.entity.VideoEntity;
import ucweb.net.NetworkUtil;
import ucweb.video.player.state.PlayStateManager;
import ucweb.util.LogUtil;
import ucweb.util.ToastUtil;
import ucweb.web.controller.VideoParams;

/**
 * desc: 视频播放器
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/8/27
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class UCMediaPlayer extends FrameLayout implements ControllerCallback, VideoViewCallBack {

    private final static String TAG = UCMediaPlayer.class.getSimpleName();

    private Context mContext;

    /** 视频播放器 */
    protected PlayerVideoView myVideoView = null;

    /** 视频控制器 */
    protected PlayerUIManager playerUIManager;

    protected final static int UPDATE_MOIVE_VIEW_BG = 5;

    /** 回退到后台*/
    protected final static int STATUS_PAUSE = 8;

    /** 回退到前台*/
    protected final static int STATUS_RESUME = 9;

    protected BaseHandler baseHandler;

    /** 视频播放地址 */
    public String vPath;

    /** 默认为0, 全屏是1，小窗是2*/
    public int screenType = 0;

    protected int mDelayUpdateMVTime = 1000;

    /** 视频宽高控制参数 */
    private FrameLayout.LayoutParams mLayoutParams;

    /** 视频默认高度 */
    private int mDefaultHeight;

    /** 提供给外部监听屏幕缩放事件*/
    private View.OnClickListener zoomListener;

    private View.OnClickListener backListener;

    private PlayStateManager stateManager;

    private MediaPlayerCallback mediaPlayerCallback;

    public UCMediaPlayer(Context context) {
        super(context);
        init(context);
    }

    public UCMediaPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        baseHandler = new BaseHandler(UCMediaPlayer.this);
    }

    /**
     * 播放初始化
     */
    public void onCreate(int screenType) {
        LogUtil.i(TAG, "onCreate");
        this.screenType = screenType;

        myVideoView = new PlayerVideoView(mContext);
        myVideoView.setMyVideoViewCallBack(this);
        myVideoView.setBackgroundColor(Color.BLACK);
        myVideoView.setFitXY(false);

        playerUIManager = new PlayerUIManager(mContext);
        playerUIManager.setScreenType(screenType);
        playerUIManager.setMyControllerCallback(this);
        playerUIManager.init();

        myVideoView.setOnPreparedListener(mOnPreparedListener);
        myVideoView.setOnErrorListener(onErrorListener);
        myVideoView.setOnCompletionListener(onCompletionListener);

        addView(myVideoView);
        addView(playerUIManager.getControllerView());

        stateManager = new PlayStateManager(this);
        stateManager.handleMessage(PlayStateManager.PLAYER_INIT_ID);
    }

    public void setMediaPlayerCallback(MediaPlayerCallback mediaPlayerCallback) {
        this.mediaPlayerCallback = mediaPlayerCallback;
    }

    /** 设置视频区域的默认高度*/
    public void setDefaultHeight(int mDefaultHeight) {
        this.mDefaultHeight = mDefaultHeight;
    }

    /** 获取16：9的默认高度*/
    private int getDefaultHeight() {
        return getResources().getDisplayMetrics().widthPixels * 9 / 16;
    }

    /**
     * 设置屏幕类型
     * @Link PlayerParams
     */
    public void setScreenType(int screenType) {
        this.screenType = screenType;
        switch (screenType) {
            case PlayerParams.SCREEN_FULL:
                switchFullScreenMode();
                break;
            case PlayerParams.SCREEN_DEFAULT:
                switchDefaultScreenMode();
                break;
        }
    }

    /**
     * 设置视频播放地址
     * @param vPath
     */
    public void setVPath(String vPath) {
        this.vPath = vPath;
    }

    /**
     * 执行播放
     */
    public void play() {
        stateManager.handleMessage(PlayStateManager.PLAY_ID);
    }

    /**
     * 暂停
     */
    public void pause() {
        stateManager.handleMessage(PlayStateManager.SET_PAUSE_ID);
    }

    /** 重置*/
    public void reset(String vPath, int msec) {
        LogUtil.i(TAG, "reset vPath = " + vPath);
        this.vPath = vPath;
        if (playerUIManager != null) {
            playerUIManager.reset();
        }

        if (myVideoView != null) {
            myVideoView.pause();
            myVideoView.stopPlayback();
            myVideoView.seekTo(0);
            myVideoView.resetHolderSize();

            myVideoView.seekTo(msec);
        }

        play();
    }

    /** 设置title*/
    public void setTitle(String title) {
        if (playerUIManager != null) {
            playerUIManager.setTitle(title);
        }
    }

    /** 提供外部设置初始播放进度*/
    public void seekTo(int msec) {
        if (myVideoView != null) {
            myVideoView.seekTo(msec);
        }
    }

    public void onResume() {
        LogUtil.i(TAG, "onResume");

        if (playerUIManager.getControllerVisibility() == View.GONE) {
            playerUIManager.controllerShow(200);
        }

        baseHandler.sendEmptyMessageDelayed(STATUS_RESUME, 200);
        stateManager.handleMessage(PlayStateManager.ACTIVITY_ON_RESUME_ID);
    }

    public void onStop() {
        LogUtil.i(TAG, "onStop");
        stateManager.handleMessage(PlayStateManager.ACTIVITY_ON_STOP_ID);
        baseHandler.sendEmptyMessageDelayed(STATUS_PAUSE, 500);
    }

    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        if (playerUIManager != null) {
            playerUIManager.onDestroy();
            playerUIManager = null;
        }

        closePlayer();

        if (baseHandler != null) {
            baseHandler.removeCallbacksAndMessages(null);
        }
    }

    private void closePlayer() {
        LogUtil.i(TAG, "closePlayer");
        if (myVideoView != null) {
            myVideoView.closePlayer();
        }
    }

    /** 初始化播放器状态*/
    public void setInitState() {
        LogUtil.i(TAG, "setInitState");
        playerUIManager.initState();
    }

    /** 设置准备状态时的动作*/
    public void setPrepareState() {
        LogUtil.i(TAG, "setPrepareState");
        playerUIManager.prepareState();
        checkNetwork2Play();
    }

    /** 设置无网络状态时的动作*/
    public void setNoNetErr() {
        LogUtil.i(TAG, "setNoNetErr");
        if (playerUIManager != null) {
            playerUIManager.setNoNetworkErr();
        }
    }

    /** 设置播放状态*/
    public void setPlayState() {
        LogUtil.i(TAG, "setPlayState");
        playerUIManager.playingState();
        baseHandler.removeMessages(UPDATE_MOIVE_VIEW_BG);
        baseHandler.sendEmptyMessageDelayed(UPDATE_MOIVE_VIEW_BG, mDelayUpdateMVTime);
        mDelayUpdateMVTime = 500;

        if (playListener != null) playListener.onPlay();
    }

    /** 设置暂停状态*/
    public void setPauseState() {
        LogUtil.i(TAG, "setPauseState");
        myVideoView.pause();
        playerUIManager.pauseState();

        if (pauseListener != null) pauseListener.onPause();
    }

    /** 设置暂停后重启播放状态*/
    public void setPlayResumeState() {
        myVideoView.start();
        setPlayState();

        playerUIManager.updatePausePlay();
    }

    /** 暂停状态时拖动进度条状态*/
    public void setProgressSeekPauseState() {
        playerUIManager.progressSeekPauseState();
    }

    /** 播放状态时拖动进度条状态*/
    public void setProgressSeekPlayState() {
        playerUIManager.progressSeekPlayState();
    }

    /** 设置播放错误状态*/
    public void setPlayErrState() {
        LogUtil.i(TAG, "setPlayErrState");
        if (!NetworkUtil.isAvalidNetSetting(mContext)) {
            setNoNetErr();
        } else {
            playerUIManager.playErrorState();
        }
    }

    /** 设置播放完成状态*/
    public void setCompleteState() {
        if (playerUIManager != null) {
            playerUIManager.completeState();
        }
    }

    /** 播放完之后重新播放*/
    public void setComplete2Replay() {
        setPlayResumeState();
    }

    /** 滑动屏幕拖动视频进度*/
    public void touch2seek() {
        if (playerUIManager != null) {
            playerUIManager.touch2seek();
        }
    }

    /**
     * 检测网络并开始播放
     */
    private void checkNetwork2Play() {
        LogUtil.i(TAG, "checkNetwork2Play");
        if (!NetworkUtil.isAvalidNetSetting(mContext)) {

            Toast.makeText(mContext, "未找到可用的网络连接", Toast.LENGTH_LONG).show();

            stateManager.handleMessage(PlayStateManager.NO_NET_ERR_ID);

            return;
        }

        first2PlayVideo();
    }

    /**
     * 检测是否首次播放，并开始播放
     */
    private void first2PlayVideo() {
        LogUtil.i(TAG, "first2PlayVideo");
        playVideo(vPath);
    }

    /**
     * 播放视频
     * @param vPath
     */
    protected void playVideo(String vPath) {
        LogUtil.i(TAG, "playVideo vPath = " + vPath);
        if (TextUtils.isEmpty(vPath)) {

            ToastUtil.showToastLong(mContext, "视频地址出错！");

            stateManager.handleMessage(PlayStateManager.URI_ERR_ID);

            return;
        }

        myVideoView.setVideoPath(vPath);
        myVideoView.requestFocus();
    }

    /**
     * 设置视频播放模式
     *
     * @param flag
     */
    private void setVideoScaleType(int flag) {
        switch (flag) {
            case PlayerParams.SCREEN_FULL:
                setVideoAreaSize(FrameLayout.LayoutParams.MATCH_PARENT);
                break;

            case PlayerParams.SCREEN_DEFAULT:
                int h = mDefaultHeight == 0 ? getDefaultHeight() : mDefaultHeight;
                setVideoAreaSize(h);
                break;
        }
    }

    /**
     * 设置视频区域大小
     *
     * @param h
     */
    private void setVideoAreaSize(int h) {
        if (mLayoutParams == null) {
            mLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    h);
        }
        mLayoutParams.height = h;
        setLayoutParams(mLayoutParams);

        FrameLayout.LayoutParams vvLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, h);
        vvLP.gravity = Gravity.CENTER;
        vvLP.height = h;
        myVideoView.setLayoutParams(vvLP);
    }

    /**
     * 切换为全屏模式
     */
    private void switchFullScreenMode() {
        setVideoScaleType(PlayerParams.SCREEN_FULL);
        playerUIManager.updateScaleButton(screenType);
    }

    /**
     * 切换为默认模式
     */
    private void switchDefaultScreenMode() {
        setVideoScaleType(PlayerParams.SCREEN_DEFAULT);
        playerUIManager.updateScaleButton(screenType);
    }

    /** 网络切换时执行方法*/
    private void switchNetworkPlay() {
        LogUtil.i(TAG, "switchNetworkPlay -> 网络切换.. ");
        if (!NetworkUtil.isAvalidNetSetting(mContext)) {
            Toast.makeText(mContext, "未找到可用的网络连接", Toast.LENGTH_LONG).show();
            stateManager.handleMessage(PlayStateManager.NO_NET_ERR_ID);
            return;
        }

        stateManager.handleMessage(PlayStateManager.AVALID_NET_ID);
    }

    @Override
    public void onZoomBtnClick(View v) {
        if (zoomListener != null) {
            zoomListener.onClick(v);
        }

        switch (screenType) {
            case PlayerParams.SCREEN_FULL:
                // switchFullScreenMode();
                break;
            case PlayerParams.SCREEN_DEFAULT:
                switchDefaultScreenMode();
                break;
        }
    }

    @Override
    public int getDuration() {
        if (myVideoView != null) {
            return myVideoView.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (myVideoView != null) {
            return myVideoView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        if (myVideoView != null) {
            return myVideoView.getBufferPercentage();
        }
        return 0;
    }

    @Override
    public void onCenterPlayBtnClick(View v) {
        stateManager.handleMessage(PlayStateManager.PLAY_BTN_CLICK_ID);
    }

    @Override
    public void onNoNetworkViewClick(View v) {
        stateManager.handleMessage(PlayStateManager.NO_NETWORK_VIEW_ID);
    }

    @Override
    public List<VideoEntity> getTvSeriesData() {
        if (mediaPlayerCallback != null) {
            return mediaPlayerCallback.getTvSeriesData();
        }

        return null;
    }

    @Override
    public void videoSwitch(int nextNumInt) {
        if (mediaPlayerCallback != null) {
            mediaPlayerCallback.videoSwitch(nextNumInt);
        }
    }

    @Override
    public void nextBtnClick(View v) {
        if (mediaPlayerCallback != null) {
            mediaPlayerCallback.nextBtnClick(v);
        }
    }

    @Override
    public void onTouch2seek() {
        stateManager.handleMessage(PlayStateManager.TOUCH_2_SEEK);
    }

    @Override
    public void onTouch2seekEnd() {
        stateManager.handleMessage(PlayStateManager.TOUCH_2_SEEK_END);
    }

    @Override
    public void onSeekTo(int msec) {
        LogUtil.i(TAG, "onSeekTo msec = " + msec);
        if (seekListener != null) {
            seekListener.onSeekTo(msec, myVideoView.isPlaying());
        }

        if (myVideoView != null) {
            myVideoView.seekTo(msec);
        }

        stateManager.handleMessage(PlayStateManager.PROGRESS_SEEK_ID);
    }

    @Override
    public boolean isPlaying() {
        if (myVideoView != null) {
            return myVideoView.isPlaying();
        }

        return false;
    }

    @Override
    public void onTurnBtnClick() {
        stateManager.handleMessage(PlayStateManager.TURN_BTN_CLICK);
    }

    @Override
    public void onBackBtnClick(View v) {
        if (screenType == PlayerParams.SCREEN_FULL) {
            baseHandler.sendEmptyMessageDelayed(STATUS_PAUSE, 500);
        }

        if (backListener != null) {
            backListener.onClick(v);
        }
    }

    @Override
    public void onShowLoading() {
        if (playerUIManager != null) {
            playerUIManager.showLoading();
        }
    }

    @Override
    public void onHideLoading() {
        if (playerUIManager != null) {
            playerUIManager.hideLoading();
        }
    }

    @Override
    public void onPlayingError() {
        if (errorListener != null) {
            errorListener.onError(null, 0, 0);
        }

        stateManager.handleMessage(PlayStateManager.PLAY_ERR_ID);
    }

    @Override
    public void onComplete() {
        stateManager.handleMessage(PlayStateManager.PLAY_COMPLETE_ID);
    }

    @Override
    public void onControllerShow() {
        if (playerUIManager != null) {
            playerUIManager.controllerShow();
        }
    }

    @Override
    public void onControllerShow(int timeout) {
        if (playerUIManager != null) {
            // Show the media controls when we're paused into a
            // video.web.activity and make 'em stick.
            playerUIManager.controllerShow(0);
        }
    }

    @Override
    public void onControllerHide() {
        if (playerUIManager != null) {
            playerUIManager.setControllerHide();
        }
    }

    @Override
    public boolean getControllerShowing() {
        if (playerUIManager != null) {
            return playerUIManager.getControllerShowing();
        }

        return false;
    }

    protected static class BaseHandler extends Handler {

        private WeakReference<UCMediaPlayer> mOuter;

        public BaseHandler(UCMediaPlayer playHelper) {
            mOuter = new WeakReference<UCMediaPlayer>(playHelper);
        }

        public void handleMessage(Message msg) {
            UCMediaPlayer outer = mOuter.get();
            if (outer == null) return;

            switch (msg.what) {
                case UPDATE_MOIVE_VIEW_BG:
                    if (outer.myVideoView != null) {
                        outer.myVideoView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    break;
                case STATUS_PAUSE:
                    if (outer.myVideoView != null) {
                        outer.myVideoView.setBackgroundColor(Color.BLACK);
                    }
                    break;
                case STATUS_RESUME:
                    removeMessages(STATUS_PAUSE);
                    if (outer.myVideoView != null) {
                        outer.myVideoView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            LogUtil.i(TAG, "onPrepared");
            if (preparedListener != null) {
                preparedListener.onPrepared(mp);
            }

            ((Activity) mContext).runOnUiThread(new Runnable() {
                public void run() {
                    stateManager.handleMessage(PlayStateManager.ON_PREPARED_ID);
                }
            });
        }
    };

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            stateManager.handleMessage(PlayStateManager.PLAY_ERR_ID);

            if (errorListener != null) {
                errorListener.onError(mp, what, extra);
            }

            return true;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (completionListener != null) {
                completionListener.onCompletion(mp);
            }
        }
    };

    private MediaPlayer.OnPreparedListener preparedListener;

    /** 提供外部设置准备状态监听*/
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        preparedListener = l;
    }

    /** 提供外部设置缩放按钮点击事件监听*/
    public void setOnZoomListener(View.OnClickListener zoomListener) {
        this.zoomListener = zoomListener;
    }

    /** 提供外部设置返回按钮点击事件监听*/
    public void setOnBackListener(View.OnClickListener backListener) {
        this.backListener = backListener;
    }

    private MediaPlayer.OnErrorListener errorListener;

    /** 提供外部设置错误状态监听*/
    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
        errorListener = l;
    }

    private MediaPlayer.OnCompletionListener completionListener;

    /** 提供外部设置完成状态监听*/
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
        completionListener = l;
    }

    private OnPlayListener playListener;

    /** 提供外部设置播放状态监听*/
    public void setOnPlayListener(OnPlayListener l) {
        playListener = l;
    }

    private OnPauseListener pauseListener;

    /** 提供外部设置暂停状态监听*/
    public void setOnPauseListener(OnPauseListener l) {
        pauseListener = l;
    }

    private OnSeekListener seekListener;

    public void setOnSeekListener(OnSeekListener l) {
        this.seekListener = l;
    }

    /** 广播事件接收*/
    public void onBroadcastReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "onBroadcastReceive->");
        String action = intent.getAction();
        if (Intent.ACTION_HEADSET_PLUG.equals(action)) { // 耳麦拔插事件
            final int state = intent.getIntExtra("state", 0);

            if (state == 0) { // onHeadsetOff
                playerUIManager.controllerShow();
                stateManager.handleMessage(PlayStateManager.SET_PAUSE_ID);
            } else { // onHeadsetOn

            }
        } else if (VideoParams.CONNECTIVITY_CHANGE_ACTION.equals(intent.getAction())) {
            switchNetworkPlay();
        }
    }

}
