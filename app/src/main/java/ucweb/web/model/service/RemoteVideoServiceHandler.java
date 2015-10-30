package ucweb.web.model.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import ucweb.util.LogUtil;
import ucweb.video.AidlRemoteService;
import ucweb.video.AidlWebActivity;
import ucweb.video.model.service.RemoteVideoService;
import ucweb.web.model.db.PlayRecordDBService;
import ucweb.web.model.db.TvSeriesDBService;
import ucweb.web.model.entity.TVSeriesEntity;

/**
 * desc: remote video service handler
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
public class RemoteVideoServiceHandler {

    private final static String TAG = RemoteVideoServiceHandler.class.getSimpleName();

    protected Context mContext;

    private RemoteVideoServiceHandlerCallback handlerCallback;

    public RemoteVideoServiceHandler(Context context, RemoteVideoServiceHandlerCallback callback) {
        this.mContext = context;
        this.handlerCallback = callback;
    }

    public void onCreate(Bundle savedInstanceState) {
        // TODO
    }

    public void onDestroy() {
        try {
            mContext.unbindService(mServiceConn);
        } catch (Exception e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        }
    }

    public void bindService() {
        Bundle bundle = new Bundle();
        Intent service = new Intent();
        service.setClass(mContext, RemoteVideoService.class);
        service.putExtras(bundle);
        mContext.bindService(service, mServiceConn, Context.BIND_AUTO_CREATE);
        mContext.startService(service);
    }

    AidlRemoteService remoteService;

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.i("RemotePlayActivity", "onServiceConnected");
            remoteService = AidlRemoteService.Stub.asInterface(service);

            try {
                remoteService.registerWebCall(callBack);
            } catch (RemoteException e) {
                if (LogUtil.DEBUG) e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.i("RemotePlayActivity", "onServiceDisconnected");
            remoteService = null;
        }
    };

    private AidlWebActivity callBack = new AidlWebActivity.Stub() {

        @Override
        public void playMemoryRecord(final String hash, final int dur, final int currPos) throws RemoteException {
            LogUtil.i(TAG, "playMemoryRecord hash = " + hash + " ,dur =" + dur + " ,currPos=" + currPos);

            // 存储数据库
            long id = PlayRecordDBService.updatePlayRecord(mContext, hash, dur, currPos);
            LogUtil.i(TAG, "playMemoryRecord id = " + id);
        }

        @Override
        public void currNumInt(String hash, int numInt) throws RemoteException {
            LogUtil.i(TAG, "currNumInt numInt = " + numInt + " hash = " + hash);

            if (handlerCallback == null) return;

            // 更新剧集数据
            handlerCallback.setCurrPlayNum(numInt);

            String tvHash = handlerCallback.getTvHash();
            TVSeriesEntity tvModel = new TVSeriesEntity();
            tvModel.setHash(tvHash);
            tvModel.setChild(hash);
            tvModel.setCurrNum(numInt);
            TvSeriesDBService.updatePlayRecord(mContext, tvModel);
        }

        @Override
        public void statistics() throws RemoteException {

        }
    };
}
