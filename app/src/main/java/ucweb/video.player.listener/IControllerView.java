package ucweb.video.player.listener;

import android.view.View;

/**
 * desc: all controller view video.video.player.listener
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
public interface IControllerView {

    View getView();

    void setMyCallback(ControllerCallback myControllerCallback);

    void setScreenType(int screenType);

    void init();

    boolean isShowing();

    void hide();

    void updatePausePlay();

    void showLoading();

    void hideLoading();

    void setNoNetworkErr();

    void show();

    void show(int timeout);

    void onDestroy();

    int getVisibility();

    void setVisibility(int visibility);

    void reset();

    void setTitle(String title);

    void updateScaleButton(int screenType);

    void setEnabled(boolean enabled);

    /** 初始状态*/
    void initState();

    /** 准备状态*/
    void prepareState();

    /** 正在播放状态*/
    void playingState();

    /** 播放暂停状态*/
    void pauseState();

    void progressSeekPlayState();

    void progressSeekPauseState();

    void playErrorState();

    /** 播放完成状态*/
    void completeState();

    /** 滑动屏幕拖动视频进度*/
    void touch2seek();
}
