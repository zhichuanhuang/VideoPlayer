package ucweb.video.player.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.RemoteViews;

import java.lang.ref.WeakReference;

import ucweb.video.player.listener.VideoViewCallBack;
import ucweb.util.LogUtil;
import ucweb.util.SystemUtil;

/**
 * desc: Video view
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/8/17
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class PlayerVideoView extends SurfaceView {

    private String TAG = PlayerVideoView.class.getSimpleName();

    private Context mContext;

    // settable by the client
    private Uri mUri;

    private int mDuration;

    private SurfaceHolder mSurfaceHolder = null;

    private MediaPlayer mMediaPlayer = null;

    private boolean mIsPrepared;

    private int mVideoWidth;

    private int mVideoHeight;

    private int mSurfaceWidth;

    private int mSurfaceHeight;

    private OnCompletionListener mOnCompletionListener;

    private OnPreparedListener mOnPreparedListener;

    private int mCurrentBufferPercentage;

    private OnErrorListener mOnErrorListener;

    private boolean mStartWhenPrepared;

    private int mSeekWhenPrepared;

    private MySizeChangeLinstener mMyChangeLinstener;

    private boolean bFitXY = true;

    public VideoViewCallBack myVideoViewCallBack;

    public void setMyVideoViewCallBack(VideoViewCallBack myVideoViewCallBack) {
        this.myVideoViewCallBack = myVideoViewCallBack;
    }

    private PrepareListener prepareListener;

    /** 有可能有视频一直加载不出来，设置一个15秒的加载时间，如果超过这个时间还没加载成功，则认为加载失败*/
    private static final long PREPARE_TIME_OUT = 15000;

    private BaseHandler mHandler;

    private OnInfoListener mOnInfoListener = new OnInfoListener() {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {

            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    LogUtil.e(TAG, "statistics MEDIA_INFO_BUFFERING_START time = " + System.currentTimeMillis());
                    if (myVideoViewCallBack != null) {
                        myVideoViewCallBack.onShowLoading();
                    }
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    LogUtil.e(TAG, "statistics MEDIA_INFO_BUFFERING_END time = " + System.currentTimeMillis());
                    if (myVideoViewCallBack != null) {
                        myVideoViewCallBack.onHideLoading();
                    }
                    break;
            }
            return false;
        }
    };

    private String mPath;

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public void setVideoScale(int width, int height) {
        LayoutParams lp = getLayoutParams();
        lp.height = height;
        lp.width = width;
        setLayoutParams(lp);
    }

    public static interface MySizeChangeLinstener {
        public void doMyThings();
    }

    public void setMySizeChangeLinstener(MySizeChangeLinstener l) {
        mMyChangeLinstener = l;
    }

    public PlayerVideoView(Context context) {
        super(context);
        mContext = context;
        initVideoView();
    }

    public PlayerVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        initVideoView();
    }

    public PlayerVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initVideoView();
    }

    /**
     * 设置是否占满屏幕宽高，默认是true
     */
    public void setFitXY(boolean flag) {
        bFitXY = flag;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(bFitXY) {
            onMeasureFitXY(widthMeasureSpec, heightMeasureSpec);
        } else {
            onMeasureKeepAspectRatio(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void onMeasureFitXY(int widthMeasureSpec, int heightMeasureSpec) {
        // Log.i("@@@@", "onMeasure");
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

        setMeasuredDimension(width, height);
        if (width < height && mVideoWidth > 0 && mVideoHeight > 0) {
            //设置全屏宽和高
            setVideoScale(SystemUtil.getDisplayWidth(mContext), SystemUtil.getDisplayHeight(mContext));
        }
    }

    /** 安卓源代码VideoView中的onMeasure，用于保持视频宽高比 */
    private void onMeasureKeepAspectRatio(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = heightSpecSize;
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                }
            } else {
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        }
        setMeasuredDimension(width, height);
    }

    public void resetHolderSize() {
        getHolder().setFixedSize(mSurfaceWidth > 0 ? mSurfaceWidth - 1 : 0, mSurfaceHeight > 0 ? mSurfaceHeight - 1 : 0);
    }

    public int resolveAdjustedSize(int desiredSize, int measureSpec) {
        int result = desiredSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                /*
                 * Parent says we can be as big as we want. Just don't be larger
                 * than max size imposed on ourselves.
                 */
                result = desiredSize;
                break;

            case MeasureSpec.AT_MOST:
                /*
                 * Parent says we can be as big as we want, up to specSize.
                 * Don't be larger than specSize, and don't be larger than the
                 * max size imposed on ourselves.
                 */
                result = Math.min(desiredSize, specSize);
                break;

            case MeasureSpec.EXACTLY:
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
    }

    private void initVideoView() {
        mHandler = new BaseHandler(this);
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

//        setZOrderOnTop(true);
//        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    public void setVideoPath(String path) {
        mPath = path;
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mUri = uri;
        mStartWhenPrepared = true;
        LogUtil.i(TAG, "setVideoURI mSeekWhenPrepared = " + mSeekWhenPrepared);
//        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();

        LogUtil.e(TAG, "statistics setVideoURI time = " + System.currentTimeMillis());
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        try {
            mIsPrepared = false;
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
            prepareListener = new PrepareListener(mSeekWhenPrepared);
            mMediaPlayer.setOnPreparedListener(prepareListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            LogUtil.i(TAG, "reset duration to -1 in openVideo");
            mDuration = -1;
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(mContext, mUri);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();

            mHandler.postDelayed(timerTask, PREPARE_TIME_OUT);
        } catch (Exception ex) {
            if (LogUtil.DEBUG) ex.printStackTrace();
        }
    }

    private Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage((int) PREPARE_TIME_OUT);
        }
    };

    public void cancelTimer() {
        mHandler.removeCallbacks(timerTask);
    }

    protected static class BaseHandler extends Handler {

        private WeakReference<PlayerVideoView> mOuter;

        public BaseHandler(PlayerVideoView videoView) {
            mOuter = new WeakReference<PlayerVideoView>(videoView);
        }

        public void handleMessage(Message msg) {
            PlayerVideoView outer = mOuter.get();
            if (outer == null) return;

            switch (msg.what) {
                case (int) PREPARE_TIME_OUT:
                    if (outer.myVideoViewCallBack != null) {
                        outer.myVideoViewCallBack.onPlayingError();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            if (mMyChangeLinstener != null) {
                mMyChangeLinstener.doMyThings();
            }

            if (mVideoWidth != 0 && mVideoHeight != 0) {
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            }
        }
    };

    private final class PrepareListener implements OnPreparedListener {

        public void setPosition(int position) {
            this.position = position;
        }

        private int position;

        public PrepareListener(int position) {
            this.position = position;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            LogUtil.e(TAG, "statistics onPrepared time = " + System.currentTimeMillis());
            mIsPrepared = true;
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }

            cancelTimer();

            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                // Log.i("@@@@", "video size: " + mVideoWidth +"/"+
                // mVideoHeight);
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                    // We didn't actually change the size (it was already at the
                    // size
                    // we need), so we won't get a "surface changed" callback,
                    // so
                    // start the video here instead of in the callback.
                    if (mSeekWhenPrepared != 0) {
                        Log.i(TAG, "onPrepared mSeekWhenPrepared1 = " + mSeekWhenPrepared);
                        mMediaPlayer.seekTo(mSeekWhenPrepared);
                        mSeekWhenPrepared = 0;
                    }
                    if (mStartWhenPrepared) {
                        mMediaPlayer.start();
                        mStartWhenPrepared = false;
                        if (myVideoViewCallBack != null) {
                            myVideoViewCallBack.onControllerShow();
                        }
                    } else if (!isPlaying() && (mSeekWhenPrepared != 0 || getCurrentPosition() > 0)) {
                        if (myVideoViewCallBack != null) {
                            // Show the media controls when we're paused into a
                            // video and make 'em stick.
                            myVideoViewCallBack.onControllerShow(0);
                        }
                    }
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (mSeekWhenPrepared != 0) {
                    LogUtil.i(TAG, "onPrepared mSeekWhenPrepared2 = " + mSeekWhenPrepared);
                    mMediaPlayer.seekTo(mSeekWhenPrepared);
                    mSeekWhenPrepared = 0;
                }
                if (mStartWhenPrepared) {
                    mMediaPlayer.start();
                    mStartWhenPrepared = false;
                }
               
            }
            LogUtil.i(TAG, "onPrepared position = " + position);
            if (position > 0) {
                LogUtil.i(TAG, "seekTo position = " + position);
                mMediaPlayer.seekTo(position);
                position = 0;
            }
        }
    };

    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (myVideoViewCallBack != null) {
                myVideoViewCallBack.onComplete();
            }
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };

    private OnErrorListener mErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            LogUtil.i(TAG, "Error: " + framework_err + "," + impl_err);
            if(impl_err != MediaPlayer.MEDIA_ERROR_IO){
                // TODO media error io
            }

            /* If an error handler has been supplied, use it and finish. */
            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                    return true;
                }
            }

            return true;
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
        }
    };

    /**
     * Register a callback to be invoked when the media file is loaded and ready
     * to go.
     * 
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file has been
     * reached during playback.
     * 
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs during playback or
     * setup. If no video.video.player.listener is specified, or if the video.video.player.listener returned false,
     * VideoUIManager will inform the user of any errors.
     * 
     * @param l The callback that will be run
     */
    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {

        private int position;

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            Log.i(TAG, "surfaceChanged mSeekWhenPrepared1 = " + mSeekWhenPrepared);
            boolean toStart = mSurfaceHeight != mVideoHeight;
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            if (mMediaPlayer != null && mIsPrepared && mVideoWidth == w && mVideoHeight == h) {
                if (toStart) {
                    mMediaPlayer.start();
                } else {
//                    if (myVideoViewCallBack != null) myVideoViewCallBack.onComplete();
                }
                if (myVideoViewCallBack != null) {
                    myVideoViewCallBack.onControllerShow();
                }
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "surfaceCreated mSeekWhenPrepared1 = " + mSeekWhenPrepared + ", position = " + position);
            mSurfaceHolder = holder;
            if (position > 0 && mPath != null) {
                play(position);
                position = 0;
            } else {
                openVideo();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "surfaceDestroyed mSeekWhenPrepared1 = " + mSeekWhenPrepared + ", position = " + position);
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            if (myVideoViewCallBack != null) {
                myVideoViewCallBack.onControllerHide();
            }
            if (mMediaPlayer != null) {
                position = mMediaPlayer.getCurrentPosition();
                closePlayer();
            }
        }
    };

    private void play(int position) {
        LogUtil.e(TAG, "statistics play time = " + System.currentTimeMillis());
        if (mSeekWhenPrepared != 0) {
            position = mSeekWhenPrepared;
        }

        try {
            openVideo();
            mMediaPlayer.setOnPreparedListener(new PrepareListener(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mIsPrepared && keyCode != KeyEvent.KEYCODE_BACK
                && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                && keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL
                && keyCode != KeyEvent.KEYCODE_ENDCALL && mMediaPlayer != null
                && myVideoViewCallBack != null) {
            if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP && mMediaPlayer.isPlaying()) {
                pause();
                myVideoViewCallBack.onControllerShow();
            } else {
                toggleMediaControlsVisiblity();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (myVideoViewCallBack.getControllerShowing()) {
            myVideoViewCallBack.onControllerHide();
        } else {
            myVideoViewCallBack.onControllerShow();
        }
    }

    public void closePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public boolean start() {
        LogUtil.e(TAG, "statistics start time = " + System.currentTimeMillis());
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.start();
            mStartWhenPrepared = false;
            return true;
        } else {
            mStartWhenPrepared = true;
            return false;
        }
    }

    public void pause() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
        mStartWhenPrepared = false;
    }

    public int getDuration() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mDuration > 0) {
                return mDuration;
            }
            mDuration = mMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    public void setVideoSize(int width, int height) {
        getHolder().setFixedSize(width, height);
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int msec) {
        LogUtil.e(TAG, "statistics seekTo time = " + System.currentTimeMillis());
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.seekTo(msec);
        } else {
            mSeekWhenPrepared = msec;
            if (prepareListener != null) {
                prepareListener.setPosition(msec);
            }
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

}