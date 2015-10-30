package ucweb.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * desc: Network util
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
public class NetworkUtil {

    /**
     * 是否连网
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConected(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isAvailable());
        }
        return false;
    }

    /**
     * 判断是不是有效的网络链接 <br/>
     * 3G，wifi，GPRS
     *
     * @param context
     * @return
     */
    public static boolean isAvalidNetSetting(Context context) {
        return !(NetworkType.UNKNOWN.endsWith(getNetworkType(context)));
    }

    /**
     * 获取网络类型,最高支持4G网络
     *
     * @param context
     * @return 网络类型
     */
    public static String getNetworkType(Context context) {
        return getNetworkType4G(context);
    }

    /**
     * 获取网络类型，最高支持4G网络.
     *
     * @param context
     * @return 网络类型
     */
    private static String getNetworkType4G(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return NetworkType.UNKNOWN;
        }
        NetworkInfo netInfo = null;
        try {
            netInfo = cm.getActiveNetworkInfo();
        } catch (Exception e) {
            // 修改崩溃 机型8185
            return NetworkType.UNKNOWN;
        }
        if (netInfo == null) {
            return NetworkType.UNKNOWN;
        }
        if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NetworkType.WIFI;
        }
        //手机通过数据线连接电脑上网，网络类型当做WiFi处理。
        if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
            return NetworkType.WIFI;
        }
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        int netType = tm.getNetworkType();

        switch (netType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NetworkType.NET_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case 17: // Sony XPERIA 移动4G手机强指3G网络
                return NetworkType.NET_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NetworkType.NET_4G;
            default:
                Log.d("NetworkUtil", "getNetworkType returns a unknown value:" + netType);
                return NetworkType.NET_3G;
        }
    }
}
