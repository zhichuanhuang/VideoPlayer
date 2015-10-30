package ucweb.video.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import ucweb.util.LogUtil;
import ucweb.web.controller.VideoParams;

/**
 * desc: The base play video activity
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
public abstract class BaseVideoActivity extends Activity {

    private final static String TAG = "BaseVideoActivity";

    protected TelephonyManager tm = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter fliter = new IntentFilter();
        fliter.addAction(Intent.ACTION_HEADSET_PLUG);
        fliter.addAction(VideoParams.CONNECTIVITY_CHANGE_ACTION);
        registerReceiver(mReceiver, fliter);

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) { // 监听来电
            tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG, "onStop->");
    }

    protected void onDestroy() {
        LogUtil.e(TAG, "onDestroy");

        unregisterReceiver(mReceiver);

        if (tm != null) { // 去掉电话监听
            tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        super.onDestroy();
    }

    protected final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            onBroadcastReceive(context, intent);
        }
    };

    protected PhoneStateListener phoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {

            onPhoneStateChanged(state, incomingNumber);

            super.onCallStateChanged(state, incomingNumber);
        }
    };

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LogUtil.i(TAG, "onConfigurationChanged land");
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            LogUtil.i(TAG, "onConfigurationChanged port");
        }
    }

    /** 广播事件分发*/
    protected abstract void onBroadcastReceive(Context context, Intent intent);

    /** 电话事件分发*/
    protected abstract void onPhoneStateChanged(int state, String incomingNumber);
}
