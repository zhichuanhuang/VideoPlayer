package ucweb.video.player.state;

/**
 * desc: 播放状态接口
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
public interface IPlayState {

    /** 进入状态*/
    void entry();

    /** 状态动作*/
    void doAction(int msgId);

    /** 退出状态*/
    void exit();
}
