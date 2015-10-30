package ucweb.web.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ucweb.util.LogUtil;
import ucweb.web.controller.WebController;

/**
 * desc: 网页界面
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/8/14.
 */
public class WebActivity extends Activity {

    private final static String TAG = WebActivity.class.getSimpleName();

    private WebController webController;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate");
        webController = new WebController(this);
        webController.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        webController.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
        if (webController != null) {
            webController.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (webController != null) {
            webController.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtil.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webController != null) {
            webController.onDestroy();
        }
    }

}
