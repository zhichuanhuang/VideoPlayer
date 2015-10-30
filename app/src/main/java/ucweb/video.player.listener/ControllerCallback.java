package ucweb.video.player.listener;

import android.view.View;

import java.util.List;

import ucweb.web.model.entity.VideoEntity;

/**
 * desc: PlayerUIManager 对 MyMediaPlayer的回调接口
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
public interface ControllerCallback {

    void onSeekTo(int msec);

    boolean isPlaying();

    void onTurnBtnClick();

    void onBackBtnClick(View v);

    /**
     * 缩放按钮点击事件
     */
    void onZoomBtnClick(View v);

    int getDuration();

    int getCurrentPosition();

    int getBufferPercentage();

    /**
     * 屏幕中间播放按钮点击事件
     */
    void onCenterPlayBtnClick(View v);

    /**
     * 无网络图片点击事件
     */
    void onNoNetworkViewClick(View v);

    /**
     * 获取剧集数据
     */
    List<VideoEntity> getTvSeriesData();

    /**
     * 视频切换
     */
    void videoSwitch(int nextNumInt);

    /**
     * 下一集按钮点击事件
     */
    void nextBtnClick(View v);

    void onTouch2seek();

    void onTouch2seekEnd();
}
