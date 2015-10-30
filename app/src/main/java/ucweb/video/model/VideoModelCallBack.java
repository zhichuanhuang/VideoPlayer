package ucweb.video.model;

/**
 * desc: video model callBack
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
public interface VideoModelCallBack {

    /** 获取视频时长*/
    long getDuration();

    /** 获取当前播放位置*/
    long getCurrentPosition();

    /** 设置title*/
    void setTitle(String title);

    /** 重置播放*/
    void reset(String path, int msec);
}
