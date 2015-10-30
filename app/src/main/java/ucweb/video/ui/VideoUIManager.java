package ucweb.video.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import java.util.List;

import ucweb.util.LogUtil;
import ucweb.util.SystemUtil;
import ucweb.video.R;
import ucweb.video.model.VideoModelListener;
import ucweb.video.player.UCMediaPlayer;
import ucweb.video.player.listener.MediaPlayerCallback;
import ucweb.video.player.listener.OnPauseListener;
import ucweb.video.player.listener.OnPlayListener;
import ucweb.video.player.listener.OnSeekListener;
import ucweb.video.player.manager.PlayerParams;
import ucweb.web.model.entity.VideoEntity;

/**
 * desc:
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
public class VideoUIManager extends BaseUIManager implements View.OnClickListener, MediaPlayerCallback {

    private final static String TAG = VideoUIManager.class.getSimpleName();

    private UCMediaPlayer ucMediaPlayer;

    public void setVideoModelListener(VideoModelListener modelListener) {
        this.modelListener = modelListener;
    }

    private VideoModelListener modelListener;

    private VideoUICallBack callBack;

    public VideoUIManager(Context context, VideoUICallBack callBack) {
        super(context);
        this.callBack = callBack;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate");
        ((Activity) mContext).setContentView(R.layout.play_video);

        // 播放器
        ucMediaPlayer = (UCMediaPlayer) ((Activity) mContext).findViewById(R.id.media_player);
        ucMediaPlayer.onCreate(PlayerParams.SCREEN_FULL);
        ucMediaPlayer.setBackListener(this);
        ucMediaPlayer.setZoomListener(this);
        ucMediaPlayer.setMediaPlayerCallback(this);
        ucMediaPlayer.setOnPreparedListener(onPreparedListener);
        ucMediaPlayer.setOnErrorListener(onErrorListener);
        ucMediaPlayer.setOnCompletionListener(onCompletionListener);
        ucMediaPlayer.setOnPlayListener(onPlayListener);
        ucMediaPlayer.setOnPauseListener(onPauseListener);
        ucMediaPlayer.setOnSeekListener(onSeekListener);
        ucMediaPlayer.setScreenType(PlayerParams.SCREEN_FULL);
        ucMediaPlayer.setVPath(callBack.getPath());
        ucMediaPlayer.setTitle(callBack.getTitle());

        int currPos = callBack.getCurrPos();
        int dur = callBack.getDur();
        if (currPos != 0 && currPos != dur && currPos != -1) {
            ucMediaPlayer.seekTo(currPos);
        }

        ucMediaPlayer.play();
    }

    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume->");
        if (ucMediaPlayer != null) {
            ucMediaPlayer.onResume();
        }
    }

    public void onStop() {
        super.onStop();
        LogUtil.i(TAG, "onStop->");
        if (ucMediaPlayer != null) {
            ucMediaPlayer.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ucMediaPlayer != null) {
            ucMediaPlayer.onDestroy();
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            SystemUtil.toggleHideyBar(((Activity) mContext));
        }
        return false;
    }

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            LogUtil.i(TAG, "onPrepared");
            if (modelListener != null) {
                modelListener.onPrepared();
            }
        }
    };

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            LogUtil.i(TAG, "onError");

            if (modelListener != null) {
                return modelListener.onError();
            }

            return true;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            LogUtil.i(TAG, "onCompletion");
            if (modelListener != null) {
                modelListener.onCompletion();
            }
        }
    };

    private OnPlayListener onPlayListener = new OnPlayListener() {
        @Override
        public void onPlay() {
            LogUtil.i(TAG, "onPlay");
            if (modelListener != null) {
                modelListener.onPlay();
            }
        }
    };

    private OnPauseListener onPauseListener = new OnPauseListener() {
        @Override
        public void onPause() {
            LogUtil.i(TAG, "onPause");
            if (modelListener != null) {
                modelListener.onPause();
            }
        }
    };

    private OnSeekListener onSeekListener = new OnSeekListener() {
        @Override
        public void onSeekTo(int mesc, boolean status) {
            LogUtil.i(TAG, "onSeekTo mesc = " + mesc + " status" + status);
            if (modelListener != null) {
                modelListener.onSeek(status);
            }
        }
    };

    public void onBroadcastReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "onBroadcastReceive->");
        if (ucMediaPlayer != null) {
            ucMediaPlayer.onBroadcastReceive(context, intent);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_btn) {
            if (mContext != null) ((Activity) mContext).finish();
        } else if (id == R.id.scale_button) {
            if (mContext != null) ((Activity) mContext).finish();
        }
    }

    public long getDuration() {
        if (ucMediaPlayer != null) {
            return ucMediaPlayer.getDuration();
        }

        return -1;
    }

    public long getCurrentPosition() {
        if (ucMediaPlayer != null) {
            return ucMediaPlayer.getCurrentPosition();
        }

        return -1;
    }

    public void setTitle(String title) {
        if (ucMediaPlayer != null) {
            ucMediaPlayer.setTitle(title);
        }
    }

    public void reset(String path, int msec) {
        if (ucMediaPlayer != null) {
            ucMediaPlayer.reset(path, msec);
        }
    }

    @Override
    public List<VideoEntity> getTvSeriesData() {
        if (callBack != null) {
            return callBack.getTvSeriesData();
        }

        return null;
    }

    @Override
    public void videoSwitch(int nextNumInt) {
        if (callBack != null) {
            callBack.videoSwitch(nextNumInt);
        }
    }

    @Override
    public void nextBtnClick(View v) {
        if (callBack != null) {
            callBack.nextBtnClick(v);
        }
    }
}
