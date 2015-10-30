package ucweb.web.model;

import android.os.Handler;

/**
 * desc:
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
public interface WebModelCallBack {

    Handler getViewHandler();

    boolean dataInit();
}
