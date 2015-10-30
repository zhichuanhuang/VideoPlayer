package ucweb.video.player.manager;

import android.content.Context;
import android.view.View;

import java.util.List;

import ucweb.video.player.listener.IControllerView;
import ucweb.video.player.listener.ControllerCallback;
import ucweb.video.player.view.PlayerControllerView;
import ucweb.web.model.entity.VideoEntity;
import ucweb.util.LogUtil;

/**
 * desc: 视频播放控制器
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/9/6
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class PlayerUIManager implements ControllerCallback {

    private static final String TAG = "PlayerUIManager";

    /** 视频控制栏界面*/
    private IControllerView myControllerView;

    private Context context;

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    /** 默认为0, 全屏是1，小窗是2*/
    private int screenType = 0;

    public void setMyControllerCallback(ControllerCallback myControllerCallback) {
        this.myControllerCallback = myControllerCallback;
    }

    /** UI控制条回调事件接口*/
    private ControllerCallback myControllerCallback;

    public PlayerUIManager(Context context) {
        this.context = context;
    }

    public void init() {
        myControllerView = new PlayerControllerView(context);
        myControllerView.setMyCallback(this);
        myControllerView.setScreenType(screenType);
        myControllerView.init();
    }

    public View getControllerView() {
        LogUtil.i(TAG, "getControllerView");
        return myControllerView.getView();
    }

    /** 初始化控制条状态*/
    public void initState() {
        LogUtil.i(TAG, "initState");
        myControllerView.initState();
    }

    public void prepareState() {
        myControllerView.prepareState();
    }

    /**
     * 开始播放时状态显示
     */
    public void playingState() {
        LogUtil.i(TAG, "playingState");
        myControllerView.playingState();
    }

    public void pauseState() {
        LogUtil.i(TAG, "pauseState");
        if (myControllerView != null) {
            myControllerView.pauseState();
        }
    }

    /** 播放状态时拖动进度条状态*/
    public void progressSeekPlayState() {
        if (myControllerView != null) {
            myControllerView.progressSeekPlayState();
        }
    }

    /** 暂停状态时拖动进度条状态*/
    public void progressSeekPauseState() {
        if (myControllerView != null) {
            myControllerView.progressSeekPauseState();
        }
    }

    public void playErrorState() {
        myControllerView.playErrorState();
    }

    public boolean getControllerShowing() {
        if (myControllerView != null) {
            return myControllerView.isShowing();
        }

        return false;
    }

    public void setControllerHide() {
        if (myControllerView != null) {
            myControllerView.hide();
        }
    }

    public void updatePausePlay() {
        if (myControllerView != null) {
            myControllerView.updatePausePlay();
        }
    }

    public void completeState() {
        if (myControllerView != null) {
            myControllerView.completeState();
        }
    }

    public void touch2seek() {
        if (myControllerView != null) {
            myControllerView.touch2seek();
        }
    }

    public void showLoading() {
        if (myControllerView != null) {
            myControllerView.showLoading();
        }
    }

    public void hideLoading() {
        if (myControllerView != null) {
            myControllerView.hideLoading();
        }
    }

    public void setNoNetworkErr() {
        if (myControllerView != null) {
            myControllerView.setNoNetworkErr();
        }
    }

    public void reset() {
        if (myControllerView != null) {
            myControllerView.reset();
        }
    }

    public void setTitle(String title) {
        if (myControllerView != null) {
            myControllerView.setTitle(title);
        }
    }

    public void updateScaleButton(int screenType) {
        if (myControllerView != null) {
            myControllerView.updateScaleButton(screenType);
        }
    }

    @Override
    public void onZoomBtnClick(View v) {
        if (myControllerCallback != null) {
            myControllerCallback.onZoomBtnClick(v);
        }
    }

    @Override
    public void onCenterPlayBtnClick(View v) {
        if (myControllerCallback != null) {
            myControllerCallback.onCenterPlayBtnClick(v);
        }
    }

    @Override
    public void onNoNetworkViewClick(View v) {
        if (myControllerCallback != null) {
            myControllerCallback.onNoNetworkViewClick(v);
        }
    }

    @Override
    public List<VideoEntity> getTvSeriesData() {
        if (myControllerCallback != null) {
            return myControllerCallback.getTvSeriesData();
        }

        return null;
    }

    @Override
    public void videoSwitch(int nextNumInt) {
        if (myControllerCallback != null) {
            myControllerCallback.videoSwitch(nextNumInt);
        }
    }

    @Override
    public void nextBtnClick(View v) {
        if (myControllerCallback != null) {
            myControllerCallback.nextBtnClick(v);
        }
    }

    @Override
    public void onTouch2seek() {
        if (myControllerCallback != null) {
            myControllerCallback.onTouch2seek();
        }
    }

    @Override
    public void onTouch2seekEnd() {
        if (myControllerCallback != null) {
            myControllerCallback.onTouch2seekEnd();
        }
    }

    @Override
    public void onSeekTo(int msec) {
        if (myControllerCallback != null) {
            myControllerCallback.onSeekTo(msec);
        }
    }

    @Override
    public boolean isPlaying() {
        if (myControllerCallback != null) {
            return myControllerCallback.isPlaying();
        }
        return false;
    }

    @Override
    public void onBackBtnClick(View v) {
        if (myControllerCallback != null) {
            myControllerCallback.onBackBtnClick(v);
        }
    }

    @Override
    public void onTurnBtnClick() {
        if (myControllerCallback != null)
            myControllerCallback.onTurnBtnClick();
    }

    @Override
    public int getDuration() {
        if (myControllerCallback != null) {
            return myControllerCallback.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (myControllerCallback != null) {
            return myControllerCallback.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        if (myControllerCallback != null) {
            return myControllerCallback.getBufferPercentage();
        }
        return 0;
    }

    public int getControllerVisibility() {
        return myControllerView.getVisibility();
    }

    public void controllerShow() {
        myControllerView.show();
    }

    public void controllerShow(int timeout) {
        myControllerView.show(timeout);
    }

    public void onDestroy() {
        if (myControllerView != null) {
            myControllerView.onDestroy();
        }
    }
}
