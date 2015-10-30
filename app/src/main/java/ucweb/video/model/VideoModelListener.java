package ucweb.video.model;

/**
 * desc: video model listener
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
public interface VideoModelListener {

    void onPrepared();

    boolean onError();

    void onCompletion();

    void onPlay();

    void onPause();

    void onSeek(boolean status);
}
