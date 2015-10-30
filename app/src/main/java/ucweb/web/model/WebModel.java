package ucweb.web.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.URLUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ucweb.util.FileUtil;
import ucweb.video.model.BaseModel;
import ucweb.video.proxy.model.controller.ProxyService;
import ucweb.web.controller.VideoParams;
import ucweb.web.model.db.PlayRecordDBService;
import ucweb.task.TaskWithHandler;
import ucweb.task.UserTask;
import ucweb.util.LogUtil;
import ucweb.util.MD5;
import ucweb.web.model.db.TvSeriesDBService;
import ucweb.web.model.db.VideoSqliteData;
import ucweb.web.model.entity.TVSeriesEntity;
import ucweb.web.model.entity.VideoEntity;
import ucweb.web.model.service.FileService;
import ucweb.web.model.service.FileServiceHandler;
import ucweb.web.model.service.RemoteVideoServiceHandler;
import ucweb.web.model.service.RemoteVideoServiceHandlerCallback;
import ucweb.web.ui.WebUIListener;

/**
 * desc: web activity model
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
public class WebModel extends BaseModel implements RemoteVideoServiceHandlerCallback {

    private final static String TAG = WebModel.class.getSimpleName();

    public int getCurrPlayNum() {
        return currPlayNum;
    }

    public void setCurrPlayNum(int currPlayNum) {
        tvSeriesSwtich(currPlayNum);
    }

    /**
     * 正在播放的剧集集数
     */
    private int currPlayNum;

    private List<VideoEntity> vList;

    private VideoEntity titleVideo;

    private WebModelCallBack modelCallBack;

    public void setViewListener(WebUIListener viewListener) {
        this.viewListener = viewListener;
    }

    private WebUIListener viewListener;

    private RemoteVideoServiceHandler remoteServiceHandler;

    private FileServiceHandler fileServiceHandler;

    public WebModel(Activity activity, WebModelCallBack callBack) {
        super(activity);
        this.modelCallBack = callBack;
        handler = new ModelHandler(this);
    }

    private boolean tvSeries = true;

    private ModelHandler handler;

    /** 网络代理地址*/
    private final String proxyBasePath = "http://127.0.0.1:" + ProxyService.PORT + "/doAction";

    /** 视频目标地址*/
    private final String targetBasePath = "http://127.0.0.1:" + FileService.PORT + "/doAction";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate");

        VideoSqliteData.getInstance().init(mContext);
        VideoSqliteData.getInstance().openDB();

        fileServiceHandler = new FileServiceHandler(((Activity) mContext));
        fileServiceHandler.onCreate(savedInstanceState);

        remoteServiceHandler = new RemoteVideoServiceHandler(mContext, this);
        remoteServiceHandler.onCreate(savedInstanceState);

        Intent proxy = new Intent(mContext, ProxyService.class);
        mContext.startService(proxy);

        Intent intent = ((Activity) mContext).getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            tvSeries = false;
            externalVideo(uri);
        }

        initDatas(mContext, modelCallBack.getViewHandler());
    }

    public void onNewIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            tvSeries = false;
            externalVideo(uri);
        }
    }

    private void externalVideo(Uri uri) {
        String path = uri.toString();
        String hash = MD5.toMD5(path);

        String proxyPath = new StringBuilder().append(proxyBasePath).append("?target=").append(path).toString();

        String vPath = !URLUtil.isNetworkUrl(path) ? path : proxyPath;

        titleVideo = new VideoEntity();
        titleVideo.setPath(vPath);
        titleVideo.setHash(hash);
        titleVideo.setTitle(path);

        // 保存数据库
        VideoEntity videoModel = (VideoEntity) PlayRecordDBService.queryData(mContext, hash, new VideoEntity());
        if (videoModel == null) {
            PlayRecordDBService.insertData(mContext, titleVideo);
        } else {
            titleVideo.setCurrPos(videoModel.getCurrPos());
        }

        viewListener.setTitle(titleVideo);
        viewListener.reset(titleVideo.getPath(), titleVideo.getCurrPos());
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
        if (modelCallBack != null && modelCallBack.dataInit() && tvSeries) {
            refreshData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
        VideoSqliteData.getInstance().close();

        if (remoteServiceHandler != null) {
            remoteServiceHandler.onDestroy();
        }

        if (fileServiceHandler != null) {
            fileServiceHandler.onDestroy();
        }

        try {
            String filePath = new StringBuilder()
                    .append(Environment.getExternalStorageDirectory().toString())
                    .append(File.separator).append("UCVideoCache").toString();
            FileUtil.deleteFile(new File(filePath));
        } catch (Exception e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        }
    }

    /**
     * 初始化数据
     */
    private void initDatas(final Context c, final Handler handler) {
        TaskWithHandler asyncTask = new TaskWithHandler(c, handler);
        asyncTask.registerCallback(new UserTask.TaskCallback<Integer, Void, Integer>() {
            @Override
            public Integer call(UserTask<Integer, Void, Integer> task, Integer[] params) throws Exception {
                String tvHash = getTvHash();

                ArrayList<VideoEntity> videoList = new ArrayList<VideoEntity>();
                for (int i = 0; i < 10; i++) {
                    final int num = i + 1;

                    final String title = "画千骨" + num;
                    String hash = MD5.toMD5(title + targetBasePath + i);

                    VideoEntity video = new VideoEntity();
                    video.setNumInt(i);
                    video.setParent(tvHash);
                    video.setHash(hash);
                    String targetPath = new StringBuilder().append(targetBasePath).append("?num=").append(num).toString();
                    String proxyPath = new StringBuilder().append(proxyBasePath).append("?target=").append(targetPath).toString();
                    video.setPath(proxyPath); // basePath + "?num=" + num
                    video.setTitle(title);
                    video.setContent("五代十国，各国间战火不断。");
                    video.setPreview("hqg_" + num);
                    videoList.add(video);

                    // 保存数据库
                    VideoEntity videoModel = (VideoEntity) PlayRecordDBService.queryData(c, hash, video);
                    if (videoModel == null) {
                        PlayRecordDBService.insertData(c, video);
                    } else {
                        video.setCurrPos(videoModel.getCurrPos());
                    }
                }

                // 保存剧集数据
                TVSeriesEntity tvModel = (TVSeriesEntity) TvSeriesDBService.queryData(c, tvHash, new TVSeriesEntity());
                if (tvModel == null) {
                    tvModel = new TVSeriesEntity();
                    tvModel.setHash(tvHash);
                    tvModel.setCount(10);
                    tvModel.setTitle("画千骨");
                    TvSeriesDBService.insertData(c, tvModel);

                    if (tvSeries) {
                        titleVideo = videoList.get(0);
                    }
                } else {
                    currPlayNum = tvModel.getCurrNum();
                    if (tvSeries) {
                        titleVideo = videoList.get(currPlayNum);
                    }
                }

                vList = videoList;

                if (handler == null) return 0;

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("videoList", videoList);
                bundle.putInt("currPlayNum", currPlayNum);
                Message msg = handler.obtainMessage();
                msg.what = 0x004;
                msg.setData(bundle);
                handler.sendMessage(msg);

                if (tvSeries) {
                    Bundle b = new Bundle();
                    b.putParcelable("titleVideo", titleVideo);
                    Message m = handler.obtainMessage();
                    m.what = 0x005;
                    m.setData(b);
                    handler.sendMessage(m);
                }

                return 0;
            }
        });
        asyncTask.execute(0);
    }

    /**
     * 刷新界面数据
     */
    private void refreshData() {
        LogUtil.i(TAG, "refreshData");
        if (viewListener == null) return;

        vList = PlayRecordDBService.queryTableASC(getTvHash());
        viewListener.refreshData(vList);

        TVSeriesEntity tv = (TVSeriesEntity) TvSeriesDBService.queryData(mContext, getTvHash(), new TVSeriesEntity());
        if (tv == null) return;
        currPlayNum = tv.getCurrNum();

        if (vList == null) return;
        titleVideo = vList.get(currPlayNum);
        viewListener.setTitle(titleVideo);
    }

    public String getTvHash() {
        return MD5.toMD5("画千骨");
    }

    public void bindRemoteService() {
        if (remoteServiceHandler != null) {
            remoteServiceHandler.bindService();
        }
    }

    /**
     * 保存播放记录
     */
    public void savePlayRecord(String hash, int dur, int currPos) {

        PlayRecordDBService.updatePlayRecord(mContext, hash, dur, currPos);
    }

    public void setTvSeries(boolean tvSeries) {
        this.tvSeries = tvSeries;
    }

    public boolean getTvSeries() {
        return tvSeries;
    }

    public void nextBtnClick(View v) {
        // 查看是否是剧集，如果是继续播放下一集
        if (!tvSeries) return;

        if (currPlayNum == vList.size() - 1) {
            // 已经是最后一集
            return;
        }

        LogUtil.i(TAG, "nextBtnClick currPlayNum = " + currPlayNum);

        tvSeriesSwtich(currPlayNum + 1);
    }

    public void tvSeriesSwtich(int num) {
        this.currPlayNum = num;

        if (vList != null) {
            try {
                titleVideo = vList.get(currPlayNum);
            } catch (IndexOutOfBoundsException e) {
                if (LogUtil.DEBUG) e.printStackTrace();
            }
        }

        if (titleVideo == null) return;

        viewListener.setTitle(titleVideo);
        viewListener.reset(titleVideo.getPath(), titleVideo.getCurrPos());

        TVSeriesEntity tvModel = new TVSeriesEntity();
        tvModel.setHash(getTvHash());
        tvModel.setChild(titleVideo.getHash());
        tvModel.setCurrNum(currPlayNum);
        TvSeriesDBService.updatePlayRecord(mContext, tvModel);
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

    public void onPrepared() {
        handler.post(statisRunn);
    }

    public boolean onError() {
        handler.removeCallbacks(statisRunn);

        Message msg = handler.obtainMessage();
        msg.arg1 = handler.PLAY_ERR;
        msg.what = VideoParams.PLAY_RECORD_STATISTICS;
        handler.sendMessage(msg);

        return true;
    }

    public void onCompletion() {
        handler.removeCallbacks(statisRunn);

        Message msg = handler.obtainMessage();
        msg.arg1 = handler.PLAY_COMPLETE;
        msg.what = VideoParams.PLAY_RECORD_STATISTICS;
        msg.obj = titleVideo.getHash();
        handler.sendMessage(msg);

        nextBtnClick(null);
    }

    public void onPlaying() {
        handler.postDelayed(statisRunn, handler.STATISTICS_DELAYED);
    }

    public void onVideoPause() {
        handler.removeCallbacks(statisRunn);

        Message msg = handler.obtainMessage();
        msg.arg1 = handler.PLAY_PAUSE;
        msg.what = VideoParams.PLAY_RECORD_STATISTICS;
        handler.sendMessage(msg);
    }

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

        private WeakReference<WebModel> mOuter;

        public ModelHandler(WebModel webModel) {
            mOuter = new WeakReference<WebModel>(webModel);
        }

        public void handleMessage(Message msg) {
            WebModel outer = mOuter.get();
            if (outer == null) return;

            switch (msg.what) {
                case VideoParams.PLAY_RECORD_STATISTICS:
                    LogUtil.i(TAG, "handleMessage PLAY_RECORD_STATISTICS");
                    if (outer.titleVideo == null || outer.viewListener == null) break;
                    try {
                        if (outer.currPlayNum != outer.titleVideo.getNumInt()) {
                            break;
                        }

                        int arg1 = msg.arg1;

                        String hash = outer.titleVideo.getHash();
                        int dur = outer.viewListener.getDuration();
                        int currPos = outer.viewListener.getCurrentPosition();

                        // -1 是播放完成状态
                        if (arg1 == PLAY_COMPLETE) {
                            hash = (String) msg.obj;
                            currPos = -1;
                            dur = -1;
                        }

                        outer.savePlayRecord(hash, dur, currPos);

                        // 同步播放器端数据
                        if (outer.vList != null) {
                            VideoEntity vm = outer.vList.get(outer.currPlayNum);
                            if (vm != null) {
                                if (dur > 0) vm.setDur(dur);
                                vm.setCurrPos(currPos);

                                outer.viewListener.notifyDataSetChanged();
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
                default:
                    break;
            }
        }
    }

    public VideoEntity getTitleVideo() {
        return titleVideo;
    }
}
