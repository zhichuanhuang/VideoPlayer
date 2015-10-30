package ucweb.video.proxy.model.controller;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;

import ucweb.util.LogUtil;
import ucweb.util.ToastUtil;
import ucweb.video.proxy.model.server.ProxyServer;
import ucweb.web.model.db.VideoSqliteData;

/**
 * desc: 本地网络代理service
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/10/22
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class ProxyService extends Service {

    private static final String TAG = ProxyService.class.getSimpleName();

    private static final String fileDir = "UCVideoCache";

    /**
     * 本地网络代理端口号
     */
    public static final int PORT = 9998;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate");

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            ToastUtil.showToastLong(this, "请插入SDCard");
            return;
        }

        // 开启数据库
        VideoSqliteData.getInstance().init(this);
        VideoSqliteData.getInstance().openDB();

        String fileDirPath = new StringBuilder()
                .append(Environment.getExternalStorageDirectory().toString())
                .append(File.separator).append(fileDir).toString();

        File dir = new File(fileDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        ProxyServer proxyServer = new ProxyServer(this, fileDirPath);
        try {
            proxyServer.star(PORT);
        } catch (Exception e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        // 关闭数据库
        VideoSqliteData.getInstance().close();
        super.onDestroy();
    }
}
