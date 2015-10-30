package ucweb.video.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ucweb.util.LogUtil;
import ucweb.web.controller.VideoParams;
import ucweb.web.model.entity.VideoEntity;
import ucweb.video.model.service.RemoteVideoService;

/**
 * desc: video model
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
public class VideoModel extends BaseModel implements VideoModelListener {

    private final static String TAG = VideoModel.class.getSimpleName();

    private ModelHandler handler;

    private VideoEntity currVideo = null;

    private ArrayList<VideoEntity> vList;

    private int currNumInt;

    private boolean tvSeries = false;

    public VideoModelListener getVideoModelListener() {
        return this;
    }

    private VideoModelCallBack callBack;

    public VideoModel(Context context, VideoModelCallBack callBack) {
        super(context);
        this.callBack = callBack;
        handler = new ModelHandler(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate");
        Intent intent = ((Activity) mContext).getIntent();
        tvSeries = intent.getBooleanExtra(VideoParams.TV_SERIES, false);
        if (tvSeries) { // 剧集
            vList = intent.getParcelableArrayListExtra(VideoParams.VIDEO_LIST);
            if (vList == null || vList.isEmpty()) {
                ((Activity) mContext).finish();
            }
            currNumInt = intent.getIntExtra(VideoParams.CURR_NUM_INT, 0);
            currVideo = vList.get(currNumInt);
        } else { // 单个视频
            currVideo = intent.getParcelableExtra(VideoParams.VIDEO_MODEL);
        }
    }

    public List<VideoEntity> getTvSeriesData() {
        return vList;
    }

    public void videoSwitch(int nextNumInt) {
        handler.removeCallbacks(statisRunn);
        tvSeriesSwtich(nextNumInt);
    }

    public void nextBtnClick(View v) {
        // 查看是否是剧集，如果是继续播放下一集
        if (!tvSeries) return;

        if (currNumInt == vList.size() - 1) {
            // 已经是最后一集
            return;
        }

        LogUtil.i(TAG, "nextBtnClick currNumInt = " + currNumInt);

        tvSeriesSwtich(currNumInt + 1);
    }

    /** 剧集切换*/
    private void tvSeriesSwtich(int nextNumInt) {
        Message msg1 = handler.obtainMessage();
        msg1.what = VideoParams.CURR_NUM_INT_CHANGE;
        msg1.arg1 = nextNumInt;
        handler.sendMessage(msg1);
    }

    Runnable statisRunn = new Runnable() {
        @Override
        public void run() {
            LogUtil.i(TAG, "statisRunn");
            Message msg = handler.obtainMessage();
            msg.arg1 = handler.PLAY_PLAYING;
            msg.what = VideoParams.PLAY_RECORD_STATISTICS;
            handler.sendMessage(msg);
        }
    };

    @Override
    public void onPrepared() {
        handler.post(statisRunn);
    }

    @Override
    public boolean onError() {
        handler.removeCallbacks(statisRunn);

        Message msg = handler.obtainMessage();
        msg.arg1 = handler.PLAY_ERR;
        msg.what = VideoParams.PLAY_RECORD_STATISTICS;
        handler.sendMessage(msg);

        return true;
    }

    @Override
    public void onCompletion() {
        handler.removeCallbacks(statisRunn);

        Message msg = handler.obtainMessage();
        msg.arg1 = handler.PLAY_COMPLETE;
        msg.what = VideoParams.PLAY_RECORD_STATISTICS;
        msg.obj = currVideo.getHash();
        handler.sendMessage(msg);

        nextBtnClick(null);
    }

    @Override
    public void onPlay() {
        handler.postDelayed(statisRunn, handler.STATISTICS_DELAYED);
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(statisRunn);

        Message msg = handler.obtainMessage();
        msg.arg1 = handler.PLAY_PAUSE;
        msg.what = VideoParams.PLAY_RECORD_STATISTICS;
        handler.sendMessage(msg);
    }

    @Override
    public void onSeek(boolean status) {
        handler.removeCallbacks(statisRunn);

        Message msg = handler.obtainMessage();
        msg.arg1 = status ? handler.PLAY_PLAYING : handler.PLAY_PAUSE;
        msg.what = VideoParams.PLAY_RECORD_STATISTICS;
        handler.sendMessage(msg);
    }

    private static class ModelHandler extends Handler {

        /** 准备播放记录统计*/
        protected static final int PLAY_PREPARED = 0x01;

        /** 正在播放记录统计*/
        protected static final int PLAY_PLAYING = 0x02;

        /** 完成播放记录统计*/
        protected static final int PLAY_COMPLETE = 0x03;

        /** 播放错误记录统计*/
        protected static final int PLAY_ERR = 0x04;

        /** 播放暂停记录统计*/
        protected static final int PLAY_PAUSE = 0x05;

        /** 拖动进度条记录统计*/
        protected static final int PROGRESS_SEEK = 0x06;

        /** 播放记录统计*/
        private static final long STATISTICS_DELAYED = 5000;

        private WeakReference<VideoModel> mOuter;

        public ModelHandler(VideoModel videoModel) {
            mOuter = new WeakReference<VideoModel>(videoModel);
        }

        public void handleMessage(Message msg) {
            VideoModel outer = mOuter.get();
            if (outer == null) return;

            switch (msg.what) {
                case VideoParams.PLAY_RECORD_STATISTICS:
                    LogUtil.i(TAG, "handleMessage PLAY_RECORD_STATISTICS");
                    try {
                        if (outer.currNumInt != outer.currVideo.getNumInt()) {
                            break;
                        }

                        int arg1 = msg.arg1;

                        String hash = outer.currVideo.getHash();
                        int dur = (int) outer.callBack.getDuration();
                        int currPos = (int) outer.callBack.getCurrentPosition();

                        // -1 是播放完成状态
                        if (arg1 == PLAY_COMPLETE) {
                            hash = (String) msg.obj;
                            currPos = -1;
                            dur = -1;
                        }

                        Bundle bundle = new Bundle();
                        bundle.putInt("what", VideoParams.PLAY_RECORD_STATISTICS);
                        bundle.putInt("dur", dur);
                        bundle.putInt("currPos", currPos);
                        bundle.putString("hash", hash);
                        Intent service = new Intent();
                        service.putExtras(bundle);
                        service.setClass(outer.mContext, RemoteVideoService.class);
                        outer.mContext.startService(service);

                        // 同步播放器端数据
                        if (outer.vList != null) {
                            VideoEntity vm = outer.vList.get(outer.currNumInt);
                            if (vm != null) {
                                if (dur > 0) vm.setDur(dur);
                                vm.setCurrPos(currPos);
                            }
                        }

                        if (arg1 == PLAY_PREPARED || arg1 == PLAY_PLAYING) {
                            removeCallbacks(outer.statisRunn);
                            postDelayed(outer.statisRunn, STATISTICS_DELAYED);
                        }
                    } catch (Exception e) {
                        if (LogUtil.DEBUG) e.printStackTrace();
                    }
                    break;
                case VideoParams.CURR_NUM_INT_CHANGE:
                    LogUtil.i(TAG, "handleMessage CURR_NUM_INT_CHANGE");
                    outer.currNumInt = msg.arg1;
                    outer.currVideo = outer.vList.get(outer.currNumInt);
                    outer.callBack.setTitle(outer.currVideo.getTitle());
                    int msec = 0;
                    int currPos = outer.currVideo.getCurrPos();
                    int dur = outer.currVideo.getDur();
                    if (currPos != 0 && currPos != dur && currPos != -1) {
                        msec = currPos;
                    }
                    outer.callBack.reset(outer.currVideo.getPath(), msec);

                    Bundle bundle = new Bundle();
                    bundle.putInt("what", VideoParams.CURR_NUM_INT_CHANGE);
                    bundle.putInt("currNumInt", outer.currNumInt);
                    bundle.putString("hash", outer.currVideo.getHash());
                    Intent service = new Intent();
                    service.putExtras(bundle);
                    service.setClass(outer.mContext, RemoteVideoService.class);
                    outer.mContext.startService(service);
                    break;
                default:
                    break;
            }
        }
    }

    public String getPath() {
        return currVideo.getPath();
    }

    public String getTitle() {
        return currVideo.getTitle();
    }

    public int getCurrPos() {
        return currVideo.getCurrPos();
    }

    public int getDur() {
        return currVideo.getDur();
    }
}
