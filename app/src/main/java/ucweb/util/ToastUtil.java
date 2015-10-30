package ucweb.util;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

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
public class ToastUtil {
    
    private static Toast mToast;
    
    private static boolean checkToastNotNull(Context context) {
        if (mToast == null && context != null) {
            mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        return (mToast != null);
    }
    
    private static boolean isInMainThread() {
        Looper myLooper = Looper.myLooper();
        Looper mainLooper = Looper.getMainLooper();
        if (myLooper == mainLooper) {
            return true;
        }
        return false;
    }
    
    public static Toast showToastShort(Context context, String msg) {
        if (isInMainThread() && checkToastNotNull(context) && !isEmpty(msg)) {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.show();
            return mToast;
        }
        return null;
    }

    public static Toast showToastShort(Context context, int resId) {
        if (isInMainThread() && checkToastNotNull(context) && resId != 0) {
            checkToastNotNull(context);
            mToast.setText(resId);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.show();
            return mToast;
        }
        return null;
    }

    public static Toast showToastLong(Context context, String msg) {
        if (isInMainThread() && checkToastNotNull(context) && !isEmpty(msg)) {
            checkToastNotNull(context);
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.show();
            return mToast;
        }
        return null;
    }

    public static Toast showToastLong(Context context, int resId) {
        if (isInMainThread() && checkToastNotNull(context) && resId != 0) {
            checkToastNotNull(context);
            mToast.setText(resId);
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.show();
            return mToast;
        }
        return null;
    }

    public static boolean isEmpty(String str) {
        if(str != null) {
            str = str.trim();
            return TextUtils.isEmpty(str);
        }
        return false;
    }
}
