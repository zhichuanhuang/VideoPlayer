package ucweb.web.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ucweb.util.LogUtil;
import ucweb.util.UCToastUtil;
import ucweb.video.R;
import ucweb.video.activity.VideoActivity;
import ucweb.video.player.UCMediaPlayer;
import ucweb.video.player.listener.MediaPlayerCallback;
import ucweb.video.player.listener.OnPauseListener;
import ucweb.video.player.listener.OnPlayListener;
import ucweb.video.player.listener.OnSeekListener;
import ucweb.video.player.manager.PlayerParams;
import ucweb.video.ui.BaseUIManager;
import ucweb.web.controller.VideoParams;
import ucweb.web.model.db.TvSeriesDBService;
import ucweb.web.model.entity.TVSeriesEntity;
import ucweb.web.model.entity.VideoEntity;
import ucweb.web.ui.adapter.VideoListAdapter;

/**
 * desc: web view
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
public class WebUIManager extends BaseUIManager implements AdapterView.OnItemClickListener, View.OnClickListener, WebUIListener, MediaPlayerCallback {

    private final static String TAG = WebUIManager.class.getSimpleName();

    /**
     * 视频列表
     */
    private ListView vListView;

    private VideoListAdapter adapter;

    public ViewHandler getHandler() {
        return handler;
    }

    private ViewHandler handler;

    public WebUIListener getViewListener() {
        return this;
    }

    private WebUICallBack viewCallBack;

    private UCMediaPlayer ucMediaPlayer;

    public boolean dataInit() {
        if (adapter != null && !adapter.isEmpty()) {
            return true;
        }

        return false;
    }

    public WebUIManager(Activity activity, WebUICallBack callBack) {
        super(activity);
        this.viewCallBack = callBack;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Activity) mContext).setContentView(R.layout.web_activity_layout);

        handler = new ViewHandler(this);

        vListView = (ListView) ((Activity) mContext).findViewById(R.id.video_list);
        vListView.setOnItemClickListener(this);

        // 播放器
        ucMediaPlayer = (UCMediaPlayer) ((Activity) mContext).findViewById(R.id.media_player);
        ucMediaPlayer.onCreate(PlayerParams.SCREEN_DEFAULT);
        ucMediaPlayer.setOnBackListener(this);
        ucMediaPlayer.setOnZoomListener(this);
        ucMediaPlayer.setMediaPlayerCallback(this);
        ucMediaPlayer.setOnPreparedListener(onPreparedListener);
        ucMediaPlayer.setOnErrorListener(onErrorListener);
        ucMediaPlayer.setOnCompletionListener(onCompletionListener);
        ucMediaPlayer.setOnPlayListener(onPlayListener);
        ucMediaPlayer.setOnPauseListener(onPauseListener);
        ucMediaPlayer.setOnSeekListener(onSeekListener);
        ucMediaPlayer.setScreenType(PlayerParams.SCREEN_DEFAULT);
        ucMediaPlayer.setPauseState();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        if (ucMediaPlayer != null) {
            if (viewCallBack != null && adapter != null) {
                ArrayList<VideoEntity> vList = adapter.getDatas();
                if (vList == null) return;
                int currPlayNum = viewCallBack.getCurrPlayNum();
                VideoEntity videoEntity = vList.get(currPlayNum);
                if (videoEntity == null) return;
                int currPos = ucMediaPlayer.getCurrentPosition();
                if (currPos > 0) {
                    viewCallBack.savePlayRecord(videoEntity.getHash(), 0, ucMediaPlayer.getCurrentPosition());
                }
            }
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_btn) {
            if (mContext != null) ((Activity) mContext).finish();
        } else if (id == R.id.scale_button) {
            go2fullScreen();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int pos = position;
        if (viewCallBack != null) {
            viewCallBack.setCurrPlayNum(pos);

            viewCallBack.setTvSeries(true);
        }
    }

    @Override
    public void refreshData(List<VideoEntity> vList) {
        if (adapter == null) {
            adapter = new VideoListAdapter(mContext, vList);
        } else {
            adapter.setData(vList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置视频属性
     */
    public void setTitle(VideoEntity videoModel) {
        if (videoModel == null || ucMediaPlayer == null
                || viewCallBack == null) return;

        int currPos = videoModel.getCurrPos();
        int dur = videoModel.getDur();
        if (currPos != 0 && currPos != dur && currPos != -1) {
            ucMediaPlayer.seekTo(currPos);
        }

        ucMediaPlayer.setTitle(videoModel.getTitle());

        ucMediaPlayer.setVPath(videoModel.getPath());
    }

    /**
     * 视频播放
     */
    public void play() {
        ucMediaPlayer.play();
    }

    /** 重置播放*/
    public void reset(String path, int msec) {
        if (ucMediaPlayer != null) {
            ucMediaPlayer.reset(path, msec);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (handler != null) {
            handler.sendEmptyMessage(0x006);
        }
    }

    @Override
    public int getDuration() {
        return ucMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return ucMediaPlayer.getCurrentPosition();
    }

    @Override
    public List<VideoEntity> getTvSeriesData() {
        return null;
    }

    @Override
    public void videoSwitch(int nextNumInt) {

    }

    @Override
    public void nextBtnClick(View v) {
        if (viewCallBack != null) {
            viewCallBack.nextBtnClick(v);
        }
    }

    protected static class ViewHandler extends Handler {

        private WeakReference<WebUIManager> mOuter;

        public ViewHandler(WebUIManager playHelper) {
            mOuter = new WeakReference<WebUIManager>(playHelper);
        }

        public void handleMessage(Message msg) {
            WebUIManager outer = mOuter.get();
            if (outer == null) return;

            switch (msg.what) {
                case 0x001:
                    break;
                case 0x002:
                    break;
                case 0x003:
                    break;
                case 0x004:
                    LogUtil.i(TAG, "0x004");
                    Bundle bundle = msg.getData();
                    if (bundle == null) break;

                    ArrayList<VideoEntity> videoList = null;
                    try {
                        videoList = bundle.getParcelableArrayList("videoList");
                    } catch (Exception e) {
                        if (LogUtil.DEBUG) e.printStackTrace();
                    }
                    if (videoList == null) break;

                    if (outer.adapter == null) {
                        outer.adapter = new VideoListAdapter(outer.mContext, videoList);
                    }

                    outer.vListView.setAdapter(outer.adapter);
                    break;
                case 0x005: // 设置头部
                    Bundle b = msg.getData();
                    if (b == null) break;
                    VideoEntity titleModel = b.getParcelable("titleVideo");
                    if (titleModel != null) outer.setTitle(titleModel);
                    break;
                case 0x006: // refresh list
                    if (outer.adapter != null) {
                        outer.adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 启动播放界面
     */
    private void go2fullScreen() {
        LogUtil.e(TAG, "statistics go2fullScreen time = " + System.currentTimeMillis());

        if (viewCallBack == null || adapter == null) return;

        if (!viewCallBack.getTvSeries()) {
            VideoEntity videoEntity = viewCallBack.getTitleVideo();
            Intent i = new Intent(mContext, VideoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean(VideoParams.TV_SERIES, false);
            bundle.putParcelable(VideoParams.VIDEO_MODEL, videoEntity);
            i.putExtras(bundle);
            mContext.startActivity(i);

            if (ucMediaPlayer != null) {
                int currPos = ucMediaPlayer.getCurrentPosition();

                if (currPos > 0) {
                    videoEntity.setCurrPos(currPos);
                    viewCallBack.savePlayRecord(videoEntity.getHash(), 0, currPos);
                }

                ucMediaPlayer.pause();
            }

            viewCallBack.bindRemoteService();

            return;
        }

        ArrayList<VideoEntity> vList = adapter.getDatas();
        if (vList == null) return;
        int currPlayNum = viewCallBack.getCurrPlayNum();
        VideoEntity videoEntity = vList.get(currPlayNum);
        if (videoEntity == null) return;

        if (ucMediaPlayer != null) {
            int currPos = ucMediaPlayer.getCurrentPosition();

            if (currPos > 0) {
                videoEntity.setCurrPos(currPos);
                viewCallBack.savePlayRecord(videoEntity.getHash(), 0, currPos);
            }

            ucMediaPlayer.pause();
        }

        Intent i = new Intent(mContext, VideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(VideoParams.TV_SERIES, true);
        bundle.putParcelableArrayList(VideoParams.VIDEO_LIST, vList);
        bundle.putInt(VideoParams.CURR_NUM_INT, currPlayNum);
        i.putExtras(bundle);
        mContext.startActivity(i);

        // 更新剧集数据
        String tvHash = viewCallBack.getTvHash();
        TVSeriesEntity tvModel = new TVSeriesEntity();
        tvModel.setHash(tvHash);
        tvModel.setChild(videoEntity.getHash());
        tvModel.setCurrNum(viewCallBack.getCurrPlayNum());
        TvSeriesDBService.updatePlayRecord(mContext, tvModel);

        viewCallBack.bindRemoteService();
    }

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            LogUtil.i(TAG, "onPrepared");
            if (viewCallBack != null) {
                viewCallBack.onPrepared();
            }
        }
    };

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            LogUtil.i(TAG, "onError");

            if (viewCallBack != null) {
                return viewCallBack.onError();
            }

            return true;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            LogUtil.i(TAG, "onCompletion");
            if (viewCallBack != null) {
                viewCallBack.onCompletion();
            }
        }
    };

    private OnPlayListener onPlayListener = new OnPlayListener() {
        @Override
        public void onPlay() {
            LogUtil.i(TAG, "onPlay");
            if (viewCallBack != null) {
                viewCallBack.onPlaying();
            }
        }
    };

    private OnPauseListener onPauseListener = new OnPauseListener() {
        @Override
        public void onPause() {
            LogUtil.i(TAG, "onPause");
            if (viewCallBack != null) {
                viewCallBack.onVideoPause();
            }
        }
    };

    private OnSeekListener onSeekListener = new OnSeekListener() {
        @Override
        public void onSeekTo(int mesc, boolean status) {
            LogUtil.i(TAG, "onSeekTo mesc = " + mesc + " status" + status);
            if (viewCallBack != null) {
                viewCallBack.onSeek(status);
            }
        }
    };

}
