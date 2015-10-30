package ucweb.util;

/**
 * log工具
 *
 * @author zhichuan.huang
 */
public final class LogUtil {

    public static final boolean DEBUG = true;
    public static final boolean INFO = true;
    public static final boolean WARN = true;
    public static final boolean ERROR = true;

    public static int d(String tag, String msg) {
        if (DEBUG) {
            return android.util.Log.d(tag, msg);
        } else {
            return 0;
        }
    }

    public static int i(String tag, String msg) {
        if (INFO) {
            return android.util.Log.i(tag, msg);
        } else {
            return 0;
        }
    }

    public static int w(String tag, String msg) {
        if (WARN) {
            return android.util.Log.w(tag, msg);
        } else {
            return 0;
        }
    }

    public static int e(String tag, String msg) {
        if (ERROR) {
            return android.util.Log.e(tag, msg);
        } else {
            return 0;
        }
    }
}
