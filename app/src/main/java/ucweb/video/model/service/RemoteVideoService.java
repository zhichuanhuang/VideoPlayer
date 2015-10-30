package ucweb.video.model.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import ucweb.util.LogUtil;
import ucweb.video.AidlRemoteService;
import ucweb.video.AidlWebActivity;
import ucweb.web.controller.VideoParams;

/**
 * desc: 远程播放器服务
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/9/10
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class RemoteVideoService extends Service {

    private static final String TAG = RemoteVideoService.class.getSimpleName();

    private AidlWebActivity mWebActivity;

    @Override
    public void onCreate() {
        LogUtil.i(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        LogUtil.i(TAG, "onStart");
        super.onStart(intent, startId);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int what = bundle.getInt("what");
            switch(what) {
                case VideoParams.PLAY_RECORD_STATISTICS:
                    int dur = bundle.getInt("dur");
                    int currPos = bundle.getInt("currPos");
                    String hash = bundle.getString("hash");
                    try {
                        if (mWebActivity != null)
                            mWebActivity.playMemoryRecord(hash, dur, currPos);
                    } catch (RemoteException e) {
                        if (LogUtil.DEBUG) e.printStackTrace();
                    }
                    break;
                case VideoParams.CURR_NUM_INT_CHANGE:
                    hash = bundle.getString("hash");
                    int currNumInt = bundle.getInt("currNumInt");
                    try {
                        if (mWebActivity != null)
                            mWebActivity.currNumInt(hash, currNumInt);
                    } catch (RemoteException e) {
                        if (LogUtil.DEBUG) e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.i(TAG, "onBind");
        return mBind;
    }

    private final AidlRemoteService.Stub mBind = new AidlRemoteService.Stub() {

        @Override
        public void registerWebCall(AidlWebActivity cb) throws RemoteException {
            LogUtil.i(TAG, "registerWebCall");
            mWebActivity = cb;
        }
    };

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
