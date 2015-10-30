package ucweb.web.model.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * desc: file service handler
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
public class FileServiceHandler {

    private final static String TAG = FileServiceHandler.class.getSimpleName();

    protected Activity mActivity;

    protected Context mContext;

    public FileServiceHandler(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        Intent fileService = new Intent();
        fileService.setClass(mContext, FileService.class);
        mContext.startService(fileService);
    }

    public void onDestroy() {
        Intent fileService = new Intent();
        fileService.setClass(mContext, FileService.class);
        mContext.stopService(fileService);
    }
}
