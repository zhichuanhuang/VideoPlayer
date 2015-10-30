package ucweb.video.player.listener;

/**
 * desc: MyVideoView 对 MyMediaPlayer的回调接口
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
public interface VideoViewCallBack {

    /** 显示视频加载进度*/
    public void onShowLoading();

    /** 隐藏视频加载进度*/
    public void onHideLoading();

    public void onPlayingError();

    public void onComplete();

    /** Controller显示*/
    public void onControllerShow();

    /** Show the media controls when we're paused into a
      video.web.activity and make 'em stick.*/
    public void onControllerShow(int timeout);

    public void onControllerHide();

    public boolean getControllerShowing();

}
