package ucweb.web.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import ucweb.util.LogUtil;
import ucweb.video.controller.BaseController;
import ucweb.web.model.WebModel;
import ucweb.web.model.WebModelCallBack;
import ucweb.web.model.entity.VideoEntity;
import ucweb.web.ui.WebUIManager;
import ucweb.web.ui.WebUICallBack;

/**
 * desc: web controller
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
public class WebController extends BaseController implements WebUICallBack, WebModelCallBack {

    private final static String TAG = "WebController";

    private WebUIManager webUIManager;

    private WebModel webModel;

    public WebController(Activity activity) {
        super(activity);
        webUIManager = new WebUIManager(activity, this);
        webModel = new WebModel(activity, this);

        webModel.setViewListener(webUIManager.getViewListener());
    }

    public void onNewIntent(Intent intent) {
        if (webModel == null) {
            webModel = new WebModel((Activity) mContext, this);
        }
        webModel.onNewIntent(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate");
        webUIManager.onCreate(savedInstanceState);
        webModel.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
        if (webModel != null) webModel.onResume();
    }

    @Override
    public void onPause() {
        if (webUIManager != null) {
            webUIManager.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webModel != null) webModel.onDestroy();
    }

    @Override
    public int getCurrPlayNum() {
        if (webModel != null) {
            return webModel.getCurrPlayNum();
        }

        return -1;
    }

    @Override
    public void setCurrPlayNum(int num) {
        if (webModel != null) {
            webModel.setCurrPlayNum(num);
        }
    }

    @Override
    public String getTvHash() {
        if (webModel != null) {
            return webModel.getTvHash();
        }

        return null;
    }

    @Override
    public void bindRemoteService() {
        if (webModel != null) {
            webModel.bindRemoteService();
        }
    }

    @Override
    public void savePlayRecord(String hash, int dur, int currPos) {
        if (webModel != null) {
            webModel.savePlayRecord(hash, dur, currPos);
        }
    }

    @Override
    public void onPrepared() {
        if (webModel != null) {
            webModel.onPrepared();
        }
    }

    @Override
    public void onPlaying() {
        if (webModel != null) {
            webModel.onPlaying();
        }
    }

    @Override
    public boolean onError() {
        if (webModel != null) {
            return webModel.onError();
        }

        return false;
    }

    public void onVideoPause() {
        if (webModel != null) {
            webModel.onVideoPause();
        }
    }

    @Override
    public void onCompletion() {
        if (webModel != null) {
            webModel.onCompletion();
        }
    }

    @Override
    public void onSeek(boolean status) {
        if (webModel != null) {
            webModel.onSeek(status);
        }
    }

    @Override
    public void nextBtnClick(View v) {
        if (webModel != null) {
            webModel.nextBtnClick(v);
        }
    }

    @Override
    public void setTvSeries(boolean tvSeries) {
        if (webModel != null) {
            webModel.setTvSeries(tvSeries);
        }
    }

    @Override
    public boolean getTvSeries() {
        if (webModel != null) {
            return webModel.getTvSeries();
        }

        return true;
    }

    @Override
    public VideoEntity getTitleVideo() {
        if (webModel != null) {
            return webModel.getTitleVideo();
        }

        return null;
    }

    @Override
    public Handler getViewHandler() {
        if (webUIManager != null) {
            return webUIManager.getHandler();
        }
        return null;
    }

    @Override
    public boolean dataInit() {
        if (webUIManager != null) {
            return webUIManager.dataInit();
        }

        return false;
    }
}
