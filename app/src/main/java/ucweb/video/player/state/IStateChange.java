package ucweb.video.player.state;

/**
 * desc: 状态跳转接口
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
public interface IStateChange {

    /**
     * 状态跳转
     * @param state
     */
    void jump(int state);

    /** 获取当前状态*/
    int getCurrState();
}
