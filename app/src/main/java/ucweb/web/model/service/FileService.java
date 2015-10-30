package ucweb.web.model.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;

import ucweb.file.server.HttpServer;
import ucweb.util.LogUtil;
import ucweb.util.ToastUtil;

/**
 * 文件服务器
 */
public class FileService extends Service {

    private static final String TAG = FileService.class.getSimpleName();

    /** http server port*/
    public static final int PORT = 9999;

    private static final String fileDir = "ucvideo";
    
    @Override
    public void onCreate() {
        LogUtil.i(TAG, "onCreate");
        super.onCreate();

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            ToastUtil.showToastLong(this, "请插入SDCard");
            return;
        }

        String fileDirPath = Environment.getExternalStorageDirectory().toString() + File.separator + fileDir;

        HttpServer httpServer = new HttpServer(fileDirPath);
        try {
            httpServer.star(PORT);
        } catch (Exception e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}