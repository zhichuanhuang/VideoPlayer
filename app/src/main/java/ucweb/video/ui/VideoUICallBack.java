package ucweb.video.ui;

import android.view.View;

import java.util.List;

import ucweb.web.model.entity.VideoEntity;

/**
 * desc: VideView callBack
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
public interface VideoUICallBack {

    /** 获取剧集数据*/
    List<VideoEntity> getTvSeriesData();

    /** 视频切换*/
    void videoSwitch(int nextNumInt);

    /** 下一集按钮点击事件*/
    void nextBtnClick(View v);

    String getPath();

    String getTitle();

    int getCurrPos();

    int getDur();
}
