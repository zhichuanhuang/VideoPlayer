package ucweb.web.ui;

import android.view.View;

import ucweb.web.model.entity.VideoEntity;

/**
 * desc: web view callBack
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/9/29
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public interface WebUICallBack {

    int getCurrPlayNum();

    void setCurrPlayNum(int num);

    String getTvHash();

    void bindRemoteService();

    void savePlayRecord(String hash, int dur, int currPos);

    void onPrepared();

    void onPlaying();

    boolean onError();

    /** 当前视频播放完成*/
    void onCompletion();

    void onVideoPause();

    void onSeek(boolean status);

    void nextBtnClick(View v);

    void setTvSeries(boolean tvSeries);

    boolean getTvSeries();

    VideoEntity getTitleVideo();
}
