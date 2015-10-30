package ucweb.video.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import java.util.List;

import ucweb.util.LogUtil;
import ucweb.video.model.VideoModel;
import ucweb.video.model.VideoModelCallBack;
import ucweb.video.ui.VideoUIManager;
import ucweb.video.ui.VideoUICallBack;
import ucweb.web.model.entity.VideoEntity;
import ucweb.video.model.service.RemoteVideoService;

/**
 * desc: video controller
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
public class VideoController extends BaseController implements VideoModelCallBack, VideoUICallBack {

    private final static String TAG = VideoController.class.getSimpleName();

    private VideoUIManager videoUIManager;

    private VideoModel videoModel;

    public VideoController(Context context) {
        super(context);
        LogUtil.i(TAG, "VideoController");
        videoUIManager = new VideoUIManager(context, this);
        videoModel = new VideoModel(context, this);

        videoUIManager.setVideoModelListener(videoModel.getVideoModelListener());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate");
        // service
        Intent service = new Intent();
        service.setClass(mContext, RemoteVideoService.class);
        mContext.startService(service);

        videoModel.onCreate(savedInstanceState);
        videoUIManager.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume->");
        if (videoUIManager != null) {
            videoUIManager.onResume();
        }
    }

    public void onStart() {
        LogUtil.i(TAG, "onStart->");
    }

    public void onPause() {
        LogUtil.i(TAG, "onPause->");
    }

    public void onStop() {
        LogUtil.i(TAG, "onStop->");
        if (videoUIManager != null) {
            videoUIManager.onStop();
        }
    }

    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        if (videoUIManager != null) {
            videoUIManager.onDestroy();
        }

        Intent service = new Intent();
        service.setClass(mContext, RemoteVideoService.class);
        mContext.stopService(service);

        // 退出视频播放时需要杀掉远程进程
        System.exit(0);

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (videoUIManager != null) {
            videoUIManager.onKeyUp(keyCode, event);
        }

        return false;
    }

    public List<VideoEntity> getTvSeriesData() {
        if (videoModel != null) {
            return videoModel.getTvSeriesData();
        }

        return null;
    }

    public void videoSwitch(int nextNumInt) {
        if (videoModel != null) {
            videoModel.videoSwitch(nextNumInt);
        }
    }

    public void nextBtnClick(View v) {
        if (videoModel != null) {
            videoModel.nextBtnClick(v);
        }
    }

    @Override
    public String getPath() {
        if (videoModel != null) {
            return videoModel.getPath();
        }

        return null;
    }

    @Override
    public String getTitle() {
        if (videoModel != null) {
            return videoModel.getTitle();
        }

        return null;
    }

    @Override
    public int getCurrPos() {
        if (videoModel != null) {
            return videoModel.getCurrPos();
        }

        return -1;
    }

    @Override
    public int getDur() {
        if (videoModel != null) {
            return videoModel.getDur();
        }

        return -1;
    }

    public void onBroadcastReceive(Context context, Intent intent) {
        if (videoUIManager != null) {
            videoUIManager.onBroadcastReceive(context, intent);
        }
    }

    @Override
    public long getDuration() {
        if (videoUIManager != null) {
            return videoUIManager.getDuration();
        }

        return -1;
    }

    @Override
    public long getCurrentPosition() {
        if (videoUIManager != null) {
            return videoUIManager.getCurrentPosition();
        }

        return -1;
    }

    @Override
    public void setTitle(String title) {
        if (videoUIManager != null) {
            videoUIManager.setTitle(title);
        }
    }

    @Override
    public void reset(String path, int msec) {
        if (videoUIManager != null) {
            videoUIManager.reset(path, msec);
        }
    }
}
