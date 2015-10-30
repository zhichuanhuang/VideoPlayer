package ucweb.video.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import ucweb.video.controller.VideoController;
import ucweb.util.LogUtil;
import ucweb.util.SystemUtil;

/**
 * desc: 远程全屏视频播放入口 ，进程ID是remote_play
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/8/14
 * <br/>
 * version: 1.0
 */
public class VideoActivity extends BaseVideoActivity {

    private final static String TAG = VideoActivity.class.getSimpleName();

    private VideoController videoController;

    protected void onCreate(Bundle savedInstanceState) {
        SystemUtil.hideNavigationBar(this);
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate");
        videoController = new VideoController(this);
        videoController.onCreate(savedInstanceState);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        if (videoController != null) {
            videoController.onKeyUp(keyCode, event);
        }

        return false;
    }

    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume->");
        if (videoController != null) {
            videoController.onResume();
        }
    }

    protected void onStart() {
        super.onStart();
        LogUtil.i(TAG, "onStart->");
        if (videoController != null) {
            videoController.onStart();
        }
    }

    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG, "onPause->");
        if (videoController != null) {
            videoController.onPause();
        }
    }

    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG, "onStop->");
        if (videoController != null) {
            videoController.onStop();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG, "onDestroy");
        if (videoController != null) {
            videoController.onDestroy();
        }
    }

    @Override
    protected void onPhoneStateChanged(int state, String incomingNumber) {
        LogUtil.i(TAG, "onPhoneStateChanged->");
        // TODO
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtil.i(TAG, "onSaveInstanceState->");
        // TODO
    }

    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        LogUtil.i(TAG, "onRestoreInstanceState->");
        // TODO
    }

    @Override
    protected void onBroadcastReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "onBroadcastReceive->");
        if (videoController != null) {
            videoController.onBroadcastReceive(context, intent);
        }
    }
}
