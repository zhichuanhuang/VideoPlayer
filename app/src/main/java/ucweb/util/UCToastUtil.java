package ucweb.util;

import android.content.Context;

import ucweb.net.NetworkUtil;
import ucweb.video.R;

/**
 * desc: Toast util
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/8/17
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class UCToastUtil {

    public static boolean checkNetworkAndShowToastIfNecessary(Context context) {
        if (!NetworkUtil.isNetworkConected(context)) {
            ToastUtil.showToastShort(context, R.string.no_network_tip_toast);
            return false;
        }
        return true;
    }
}
