package ucweb.video.application;

import android.app.Application;

/**
 * desc:
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/10/19
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class VideoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}
