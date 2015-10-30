package ucweb.video.player.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

import ucweb.video.player.listener.IControllerView;
import ucweb.video.player.listener.ControllerCallback;
import ucweb.video.player.listener.SelectionsCallback;
import ucweb.net.NetworkUtil;
import ucweb.util.LogUtil;
import ucweb.util.SystemUtil;
import ucweb.util.ToastUtil;
import ucweb.video.R;
import ucweb.video.player.manager.PlayerParams;
import ucweb.video.player.widget.VerticalSeekBar;

/**
 * desc: 播放器控制栏
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/9/6
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class PlayerControllerView implements IControllerView, View.OnClickListener, SelectionsCallback {

    private static final String TAG = PlayerControllerView.class.getSimpleName();

    private Context mContext;

    /**
     * 默认为0, 全屏是1，小窗是2
     */
    public int screenType = 0;

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    private AudioManager mAudioManager = null;

    /**
     * 开启和暂停按钮
     */
    private ImageButton mTurnButton;

    private ImageButton mScaleButton;

    private ImageButton mSoundButton;

    /**
     * 关闭按钮
     */
    private View mCloseVdButton;

    /**
     * 加载视图
     */
    private View loading;

    private TextView loading_tx;

    private int maxVolume = 0;

    private int currentVolume = 0;

    private View soundSeekbarLayout;

    private VerticalSeekBar seekSound;

    private View titleLayout, controlLayout;

    private ProgressBar mProgress;

    private TextView mEndTime, mCurrentTime;

    private TextView mTitle, selections;

    private static final int sDefaultTimeout = 5000;

    private static final int FADE_OUT = 1;

    private static final int SHOW_PROGRESS = 2;

    private static final int SHOW_LOADIGN = 3;

    private static final int HIDE_LOADIGN = 4;

    /**
     * 正在显示状态条
     */
    private boolean mShowing;

    /**
     * 正在拖动状态条
     */
    private boolean mDragging;

    private StringBuilder mFormatBuilder;

    private Formatter mFormatter;

    private ControllerCallback myCallback;

    private ControllerViewHandler mHandler;

    /**
     * 播放器中间的播放按钮
     */
    private ImageView mCenterPlayBtn;

    /**
     * 无网络时显示的View
     */
    private View noNetworkView;

    /**
     * 无网络时显示的图片
     */
    private ImageView noNetworkImg;

    /** 停止播放是显示界面*/
    private FrameLayout playPauseView;

    /**
     * 播放器状态控制栏View
     */
    private View contentView;

    /** 状态控制栏*/
    private View controlView;

    /** 选集界面*/
    private SelectionsPopWin selectionsPopWin;

    private ImageButton nextBtn;

    private FrameLayout bottomView;

    /** 声音提示*/
    private TextView videoSoundHint;

    private boolean tvSeries = false;

    public PlayerControllerView(Context context) {
        mContext = context;
        onCreate();
    }

    @Override
    public View getView() {
        return contentView;
    }

    @Override
    public void setMyCallback(ControllerCallback myControllerCallback) {
        myCallback = myControllerCallback;
    }

    /**
     * 界面初始化
     */
    public void init() {
        LogUtil.i(TAG, "init");

        if (screenType == 0) {
            selections.setVisibility(View.GONE);
        }
    }

    /**
     * 界面创建
     */
    private void onCreate() {
        LogUtil.i(TAG, "onCreate");
        mHandler = new ControllerViewHandler(this);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        contentView = inflater.inflate(R.layout.player_controller, null);

        bottomView = (FrameLayout) contentView.findViewById(R.id.bottom_view);
        bottomView.setFocusable(true);
        bottomView.setFocusableInTouchMode(true);
        bottomView.requestFocus();
        bottomView.setOnTouchListener(mTouchListener);

        initControllerView(contentView);

        initSoundView();

        playPauseView = (FrameLayout) contentView.findViewById(R.id.play_pause_view);
        mCenterPlayBtn = (ImageView) contentView.findViewById(R.id.play_btn);
        noNetworkView = contentView.findViewById(R.id.no_network_view);
        noNetworkImg = (ImageView) contentView.findViewById(R.id.no_network_img);
        mCenterPlayBtn.setOnClickListener(this);
        noNetworkView.setOnClickListener(this);
    }

    private void initControllerView(View v) {
        LogUtil.i(TAG, "initControllerView");
        controlView = v.findViewById(R.id.control_view);
        titleLayout = v.findViewById(R.id.title_part);
        controlLayout = v.findViewById(R.id.control_layout);
        loading = v.findViewById(R.id.loading_layout);
        loading_tx = (TextView) v.findViewById(R.id.loading_tx);
        mTurnButton = (ImageButton) v.findViewById(R.id.turn_button);
        mScaleButton = (ImageButton) v.findViewById(R.id.scale_button);
        mSoundButton = (ImageButton) v.findViewById(R.id.sound_button);
        mCloseVdButton = v.findViewById(R.id.back_btn);
        nextBtn = (ImageButton) v.findViewById(R.id.next_btn);

        mTurnButton.requestFocus();
        mTurnButton.setOnClickListener(this);
        mScaleButton.setOnClickListener(this);
        mSoundButton.setOnClickListener(this);
        mCloseVdButton.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        mProgress = (ProgressBar) v.findViewById(R.id.seekbar);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mProgress.setMax(1000);
        }

        mEndTime = (TextView) v.findViewById(R.id.duration);
        mCurrentTime = (TextView) v.findViewById(R.id.has_played);
        mTitle = (TextView) v.findViewById(R.id.title);
        selections = (TextView) v.findViewById(R.id.selections);
        selections.setOnClickListener(this);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        soundSeekbarLayout = v.findViewById(R.id.sound_seek_layout);
        seekSound = (VerticalSeekBar) v.findViewById(R.id.sound_seek);
//        int bound = SystemUtil.dip2px(mContext, 15);
//        ViewUtil.expandViewTouchDelegate(seekSound, 0, 0, bound, bound);

        soundSeekbarLayout.setVisibility(View.GONE);
        setVisibility(View.VISIBLE);

        updateScaleButton(screenType);
    }

    private void initSoundView() {
        Log.i(TAG, "initSoundView");
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekSound.setOnSeekBarChangeListener(soundSeekListener);

        videoSoundHint = (TextView) contentView.findViewById(R.id.video_sound_hint);
        videoSoundHint.setVisibility(View.GONE);

        seekSound.setThumbOffset(0);
        int progress = 0;
        if (maxVolume > 0) {
            progress = currentVolume * 100 / maxVolume;
        }
        seekSound.setProgress(progress);
    }

    /** 设置声音提示*/
    private void setSoundProgress(float soundProgress) {
        final int progress = (int) (soundProgress / 10);
//        LogUtil.i(TAG, "setSoundProgress progress = " + progress);
        seekSound.setProgress(progress);

        try {
            Drawable dw;
            if (progress == 0) {
                dw = mContext.getResources().getDrawable(R.drawable.video_sound_no_hint);
            } else {
                dw = mContext.getResources().getDrawable(R.drawable.video_sound_hint);
            }

            dw.setBounds(0, 0, dw.getIntrinsicWidth(), dw.getIntrinsicHeight());
            videoSoundHint.setCompoundDrawables(null, dw, null, null);

            videoSoundHint.setText(progress + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 获取声音进度*/
    private int getSoundProgress() {

        if (maxVolume == 0) return 0;

        return currentVolume * 1000 / maxVolume;
    }

    float dx = 0, dy = 0;

    /** 音量调节显示*/
    boolean soundProgressDisplay = false;

    /** 视频进度调节显示*/
    boolean videoProgressDisplay = false;

    /** 屏幕亮度调节显示*/
    boolean brightnessDisplay = false;

    /** 声音加大时的系数*/
    private static final float SOUND_ADD_FACTOR = 1.0f;

    /** 声音减小时的系数*/
    private static final float SOUND_DEC_FACTOR = 1.0f;

    /** ACTION_DOWN时音量的进度*/
    float downSoundProgress;

    /** ACTION_DOWN时视频的进度*/
    float downVideoProgress;

    /** ACTION_DOWN时屏幕的亮度*/
    float brightnessProgress;

    float downVideoDuration;

    long previousPos;

    int displayHeight;

    int displayWidth;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                toggleMediaControlsVisiblity();

                dx = event.getX();
                dy = event.getY();
            }

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float mx = event.getX();
                float my = event.getY();

//                LogUtil.i(TAG, "ACTION_MOVE mx = " + mx + " my = " + my);

                float disX = mx - dx;
                float disY = my - dy;

                float absX = Math.abs(disX);
                float absY = Math.abs(disY);

//                LogUtil.i(TAG, "ACTION_MOVE absX = " + absX + " absY = " + absY);

                if (brightnessDisplay) { // 亮控
//                    LogUtil.i(TAG, "ACTION_MOVE brightnessProgress = " + brightnessProgress);
                    float p = absY * 1000 / displayHeight;
//                    LogUtil.i(TAG, "ACTION_MOVE p = " + p);
                    if (disY > 0) {
                        brightnessProgress -= p;
                    } else {
                        brightnessProgress += p;
                    }
                    int brightness = (int) (brightnessProgress * 255 / 1000);
//                    LogUtil.i(TAG, "ACTION_MOVE brightness = " + brightness);
                    if (brightness < 0 || brightness > 255) {
                        dy = my;
                    }

                    if (brightnessProgress < 0) {
                        brightness = 0;
                        brightnessProgress = 0;
                    }

                    if (brightnessProgress > 1000) {
                        brightness = 255;
                        brightnessProgress = 1000;
                    }

                    if (disY > 0) { // 减小
                        if (brightness <= curBrightness) {
                            dy = my;

                            setBrightness(brightness, (int) (brightnessProgress / 10));
                        }
                    } else { // 加大
                        if (brightness >= curBrightness) {
                            dy = my;

                            setBrightness(brightness, (int) (brightnessProgress / 10));
                        }
                    }
                }

                if (soundProgressDisplay) { // 声控
//                    LogUtil.i(TAG, "ACTION_MOVE displayHeight = " + displayHeight);
                    float p = (disY > 0 ? SOUND_DEC_FACTOR : SOUND_ADD_FACTOR) * absY * 1000 / displayHeight;
//                    LogUtil.i(TAG, "ACTION_MOVE p = " + p);
                    if (disY > 0) {
                        downSoundProgress -= p;
                    } else {
                        downSoundProgress += p;
                    }
//                    LogUtil.i(TAG, "ACTION_MOVE downSoundProgress = " + downSoundProgress);
                    int index = (int) (downSoundProgress * maxVolume / 1000);
//                    LogUtil.i(TAG, "ACTION_MOVE index = " + index);

                    if (index < 0 || index > maxVolume) {
                        dy = my;
                    }

                    if (downSoundProgress < 0) {
                        index = 0;
                        downSoundProgress = 0;
                    }

                    if (downSoundProgress > 1000) {
                        index = maxVolume;
                        downSoundProgress = 1000;
                    }

                    if (disY > 0) { // 减小
                        if (index <= currentVolume) {
                            dy = my;

                            updateVolume(index);
                            setSoundProgress(downSoundProgress);
                        }
                    } else { // 加大
                        if (index >= currentVolume) {
                            dy = my;

                            updateVolume(index);
                            setSoundProgress(downSoundProgress);
                        }
                    }
                }

                if (videoProgressDisplay) { // 视控
//                    LogUtil.i(TAG, "ACTION_MOVE displayWidth = " + displayWidth);
                    float p = absX * 1000 / displayWidth;
//                    LogUtil.i(TAG, "ACTION_MOVE p = " + p);
                    if (disX > 0) { // 前进
                        downVideoProgress += p;
                    } else { // 后退
                        downVideoProgress -= p;
                    }
//                    LogUtil.i(TAG, "ACTION_MOVE downVideoProgress = " + downVideoProgress);
//                    LogUtil.i(TAG, "ACTION_MOVE downVideoDuration = " + downVideoDuration);

                    long currPos = (long) ((downVideoDuration * downVideoProgress) / 1000L);
//                    LogUtil.i(TAG, "ACTION_MOVE currPos = " + currPos);

                    if (currPos <= 0 || currPos >= downVideoDuration) {
                        dx = mx;
                    }

                    if (currPos <= 0) {
                        currPos = 0;
                        downVideoProgress = 0;
                    }

                    if (currPos >= downVideoDuration) {
                        currPos = (long) downVideoDuration;
                        downVideoProgress = 1000;
                    }

                    if (disX > 0) { // 前进
                        if (currPos >= previousPos) {
                            dx = mx;

                            seekProgressBar(0, currPos);
                        }
                    } else { // 后退
                        if (currPos <= previousPos) {
                            dx = mx;

                            seekProgressBar(1, currPos);
                        }
                    }
                }

                if (!brightnessDisplay && !soundProgressDisplay && !videoProgressDisplay && screenType != 0) { // 初始状态
                    if (absY >= absX) { // 声控 和 亮控
//                        LogUtil.i(TAG, "ACTION_MOVE absY >= absX");

                        if (displayHeight == 0) {
                            displayHeight = SystemUtil.getDisplayHeight(mContext);
                        }

                        if (displayWidth == 0) {
                            displayWidth = SystemUtil.getDisplayWidth(mContext);
                        }

                        if (dx >= displayWidth/2) { // 亮控
                            brightnessDisplay = true;
                            int brightness = getCurrBrightness();
                            brightnessProgress = brightness * 1000 / 255;
//                            LogUtil.i(TAG, "ACTION_MOVE brightnessProgress = " + brightnessProgress + " brightness = " + brightness);
                            setBrightness(brightness, (int) (brightnessProgress / 10));
                            videoSoundHint.setVisibility(View.VISIBLE);
                        } else { // 声控
                            soundProgressDisplay = true;

                            downSoundProgress = getSoundProgress();

                            setSoundProgress(downSoundProgress);

                            videoSoundHint.setVisibility(View.VISIBLE);
                        }
                    } else { // 视控
                        LogUtil.i(TAG, "ACTION_DOWN absX > absY");

                        if (displayWidth == 0) {
                            displayWidth = SystemUtil.getDisplayWidth(mContext);
                        }

                        if (getVideoProgress() != -1) {
                            videoProgressDisplay = true;

                            downVideoProgress = getVideoProgress();

                            downVideoDuration = getVideoDuration();

                            if (disX > 0) { // 前进
                                seekProgressBar(0, getVideoCurrentPos());
                            } else {
                                seekProgressBar(1, getVideoCurrentPos());
                            }
                            videoSoundHint.setVisibility(View.VISIBLE);
                        }
                    }

                    if (myCallback != null) {
                        myCallback.onTouch2seek();
                    }

                    dx = mx;
                    dy = my;
                }

//                LogUtil.i(TAG, "ACTION_MOVE dx = " + dx + " dy = " + dy);
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (videoProgressDisplay) {

                    setVideoProgress();

                    show();

                    if (myCallback != null) {
                        myCallback.onTouch2seekEnd();
                    }
                }

                brightnessDisplay = false;
                soundProgressDisplay = false;
                videoProgressDisplay = false;
                videoSoundHint.setVisibility(View.GONE);
            }

            return true;
        }
    };

    private int curBrightness;

    /** 设置亮度调节提示*/
    private void setBrightness(int brightness, int progress) {
//        LogUtil.i(TAG, "setBrightness brightness = " + brightness);
//        LogUtil.i(TAG, "setBrightness progress = " + progress);
        curBrightness = brightness;

        // 根据当前进度改变亮度
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, brightness);
        brightness = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, -1);
//        LogUtil.i(TAG, "setBrightness brightness = " + brightness);
        WindowManager.LayoutParams wl = ((Activity) mContext).getWindow().getAttributes();
        float tmpFloat = (float) brightness / 255;
//        LogUtil.i(TAG, "setBrightness tmpFloat = " + tmpFloat);
        if (tmpFloat > 0 && tmpFloat <= 1) {
            wl.screenBrightness = tmpFloat;
        }
        ((Activity) mContext).getWindow().setAttributes(wl);

        videoSoundHint.setText(progress + "%");

        try {
            Drawable dw = mContext.getResources().getDrawable(R.drawable.video_brightness_hint);

            dw.setBounds(0, 0, dw.getIntrinsicWidth(), dw.getIntrinsicHeight());
            videoSoundHint.setCompoundDrawables(null, dw, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 获取亮度*/
    private int getCurrBrightness() {
        int brightness = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, -1);
//        LogUtil.i(TAG, "getBrightnessProgress brightness = " + brightness);
        return brightness;
    }

    /** 获取视频的当前位置*/
    private long getVideoCurrentPos() {
        return myCallback.getCurrentPosition();
    }

    /** 获取视频的长度*/
    private long getVideoDuration() {
        return myCallback.getDuration();
    }

    /** 获取当前视频的进度*/
    private int getVideoProgress() {
        int duration = myCallback.getDuration();
        if (duration > 0) {
            int position = myCallback.getCurrentPosition();
            // use long to avoid overflow
            long progress = 1000L * position / duration;
            return (int) progress;
        }

        return -1;
    }

    /** 拖动视频进度*/
    private void seekProgressBar(int direction, long currPos) {
        previousPos = currPos;
        try {
            Drawable dw;
            if (direction == 0) {
                dw = mContext.getResources().getDrawable(R.drawable.video_fast_forward);
            } else {
                dw = mContext.getResources().getDrawable(R.drawable.video_fast_back);
            }

            dw.setBounds(0, 0, dw.getIntrinsicWidth(), dw.getIntrinsicHeight());
            videoSoundHint.setCompoundDrawables(null, dw, null, null);

            String currTimeStr = stringForTime((int) currPos);

            if (videoSoundHint != null) {
                videoSoundHint.setText(currTimeStr);
            }

            if (mCurrentTime != null) {
                mCurrentTime.setText(currTimeStr);
            }

            long pos = 1000L * currPos / getVideoDuration();
            mProgress.setProgress((int) pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 设置视频提示*/
    private void setVideoProgress() {
//        LogUtil.i(TAG, "setVideoProgress previousPos = " + previousPos);
        myCallback.onSeekTo((int) previousPos);
        setProgress();
    }

    private void toggleMediaControlsVisiblity() {
        if (animDoing) return;

        if (getVisibility() == View.VISIBLE) {
            hide();
        } else {
            show();
        }
    }

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        int new_position = 0;

        boolean change = false;

        public void onStartTrackingTouch(SeekBar bar) {
            if (myCallback == null) {
                return;
            }
            show(3600000);

            mDragging = true;
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (myCallback == null) {
                return;
            }
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = myCallback.getDuration();
            long newposition = (duration * progress) / 1000L;
            new_position = (int) newposition;
            change = true;
        }

        public void onStopTrackingTouch(SeekBar bar) {
            if (myCallback == null) return;

            if (change) {
                myCallback.onSeekTo(new_position);
                if (mCurrentTime != null)
                    mCurrentTime.setText(stringForTime(new_position));
            }

            mDragging = false;
            setProgress();
            updatePausePlay();
            show(sDefaultTimeout);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mShowing = true;
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    private void updateVolume(int index) {
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
            currentVolume = index;
        }
    }

    private VerticalSeekBar.OnSeekBarChangeListener soundSeekListener
            = new VerticalSeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
            int index = progress * maxVolume / 100;
            updateVolume(index);
        }

        @Override
        public void onStartTrackingTouch(VerticalSeekBar seekBar) {
            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        @Override
        public void onStopTrackingTouch(VerticalSeekBar seekBar) {
            try {
                show(sDefaultTimeout);
                // Ensure that progress is properly updated in the future,
                // the call to show() does not guarantee this because it is a
                // no-op if we are already showing.
                mShowing = true;
                mHandler.sendEmptyMessage(SHOW_PROGRESS);
                if (seekSound != null)
                    seekSound.setProgress(currentVolume * 100 / maxVolume);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void setEnabled(boolean enabled) {
        controlView.setEnabled(enabled);

        if (mTurnButton != null) {
            mTurnButton.setEnabled(enabled);
        }

        if (mProgress != null) {
            mProgress.setEnabled(enabled);
        }
    }

    @Override
    public void initState() {
        setVisibility(View.VISIBLE);
        showLoading();
        playPauseView.setVisibility(View.VISIBLE);
        mCenterPlayBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void prepareState() {
        setVisibility(View.VISIBLE);
        playPauseView.setVisibility(View.GONE);
        showLoading();

        setEnabled(false);
    }

    /**
     * 开始播放时状态显示
     */
    public void playingState() {
        Log.i(TAG, "playingState");
        mTurnButton.setImageResource(R.drawable.star_stop_btn);
        playPauseView.setVisibility(View.GONE);
        mCenterPlayBtn.setVisibility(View.GONE);
        setEnabled(true);
        hideLoading();
    }

    /**
     * 播放暂停时状态显示
     */
    public void pauseState() {
        LogUtil.i(TAG, "pauseState");
        playPauseView.setVisibility(View.VISIBLE);
        mCenterPlayBtn.setVisibility(View.VISIBLE);
        noNetworkView.setVisibility(View.GONE);
        mTurnButton.setImageResource(R.drawable.player_player_btn);
        hideLoading();
    }

    @Override
    public void progressSeekPlayState() {
        showLoading();
    }

    @Override
    public void progressSeekPauseState() {
        hideLoading();
    }

    boolean animDoing = false;

    public void setVisibility(int visibility) {
//        controlView.setVisibility(visibility);

        if (visibility == View.VISIBLE && !titleLayout.isShown()) {
            Animation titleAnim = AnimationUtils.loadAnimation(mContext, R.anim.in_from_top);
            titleAnim.setFillAfter(true);
            titleLayout.startAnimation(titleAnim);

            Animation controlAnim = AnimationUtils.loadAnimation(mContext, R.anim.in_from_bottom);
            controlAnim.setFillAfter(true);
            controlAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    animDoing = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animDoing = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            controlLayout.startAnimation(controlAnim);

            controlView.setVisibility(visibility);
        } else if (visibility == View.GONE && titleLayout.isShown()) {
            Animation titleAnim = AnimationUtils.loadAnimation(mContext, R.anim.out_to_top);
            titleAnim.setFillAfter(true);
            titleLayout.startAnimation(titleAnim);

            Animation controlAnim = AnimationUtils.loadAnimation(mContext, R.anim.out_to_bottom);
            controlAnim.setFillAfter(true);
            controlAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    animDoing = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    controlView.setVisibility(View.GONE);
                    animDoing = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            controlLayout.startAnimation(controlAnim);
        }
    }

    public int getVisibility() {
        return controlView.getVisibility();
    }

    public void playErrorState() {
        hideLoading();
        if (!NetworkUtil.isNetworkConected(mContext)) {
            ToastUtil.showToastShort(mContext, "视频加载失败，请检查网络连接情况");
        }
        setEnabled(true);
        playPauseView.setVisibility(View.VISIBLE);
        mCenterPlayBtn.setVisibility(View.VISIBLE);
        noNetworkView.setVisibility(View.GONE);
        mTurnButton.setImageResource(R.drawable.player_player_btn);
    }

    public void completeState() {
        hide();
        hideLoading();
        playPauseView.setVisibility(View.VISIBLE);
        mCenterPlayBtn.setVisibility(View.VISIBLE);
        noNetworkView.setVisibility(View.GONE);
    }

    @Override
    public void touch2seek() {
        hideLoading();
        playPauseView.setVisibility(View.GONE);
    }

    @Override
    public void reset() {
        mCurrentTime.setText("00:00");
        mEndTime.setText("00:00");
        mProgress.setProgress(0);
        mTurnButton.setImageResource(R.drawable.player_player_btn);
        mCenterPlayBtn.setVisibility(View.GONE);
        noNetworkView.setVisibility(View.GONE);
        setVisibility(View.VISIBLE);
        showLoading();
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void showLoading() {
        mHandler.sendEmptyMessage(SHOW_LOADIGN);
    }

    public void hideLoading() {
        mHandler.sendEmptyMessage(HIDE_LOADIGN);
    }

    private void dismissSoundWindow() {
        if (soundSeekbarLayout != null)
            soundSeekbarLayout.setVisibility(View.GONE);
    }

    /**
     * 隐藏显示控制栏
     */
    public void hide() {
        if (mShowing) {
            mHandler.removeMessages(SHOW_PROGRESS);
            dismissSoundWindow();
            setVisibility(View.GONE);
            mShowing = false;
        }
    }

    /**
     * 显示控制栏，设置默认显示时间
     */
    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * 显示控制栏
     */
    public void show(int timeout) {
        if (!mShowing) {
            setProgress();
            if (mTurnButton != null) {
                mTurnButton.requestFocus();
            }
        }

        updatePausePlay();

        // cause the progress bar to be updated even if mShowing
        // was already true. This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mShowing = true;
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    @Override
    public void videoSwitch(int nextNumInt) {
        if (myCallback != null) {
            myCallback.videoSwitch(nextNumInt);
        }
    }

    private static class ControllerViewHandler extends Handler {

        private WeakReference<PlayerControllerView> mOuter;

        public ControllerViewHandler(PlayerControllerView myControllerView) {
            mOuter = new WeakReference<PlayerControllerView>(myControllerView);
        }

        @Override
        public void handleMessage(Message msg) {
            PlayerControllerView outer = mOuter.get();
            if (outer == null) return;

            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    outer.hide();
                    break;
                case SHOW_PROGRESS:
                    pos = outer.setProgress();
                    outer.setVisibility(View.VISIBLE);
                    if (!outer.mDragging && outer.mShowing && outer.myCallback != null && outer.myCallback.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        outer.loading.setVisibility(View.GONE);
                    }
                    outer.setEnabled(true);
                    break;
                case SHOW_LOADIGN:
                    outer.loading.setVisibility(View.VISIBLE);
                    outer.loading_tx.setText(outer.mContext.getString(R.string.p2refresh_doing_end_refresh));
                    break;
                case HIDE_LOADIGN:
                    outer.loading.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public void updateScaleButton(int screenType) {
        this.screenType = screenType;
        if (screenType == PlayerParams.SCREEN_FULL) {
            mScaleButton.setImageResource(R.drawable.star_zoom_in);
            setVisibility(View.VISIBLE);
        } else {
            mScaleButton.setImageResource(R.drawable.player_scale_btn);
            setVisibility(View.VISIBLE);
        }
    }

    public void updatePausePlay() {
        if (myCallback != null && myCallback.isPlaying()) {
            mTurnButton.setImageResource(R.drawable.star_stop_btn);
        } else {
            mTurnButton.setImageResource(R.drawable.player_player_btn);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_btn) {
            if (myCallback != null) myCallback.onBackBtnClick(v);
        } else if (id == R.id.scale_button) {
            dismissSoundWindow();
            if (myCallback != null) {
                myCallback.onZoomBtnClick(v);
            }
        } else if (id == R.id.sound_button) {
            if (soundSeekbarLayout.getVisibility() == View.VISIBLE) {
                soundSeekbarLayout.setVisibility(View.GONE);
            } else {
                soundSeekbarLayout.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.turn_button) {
            if (myCallback != null) {
                myCallback.onTurnBtnClick();

            }
            show();
        } else if (id == R.id.play_btn) {
            if (myCallback != null) {
                myCallback.onCenterPlayBtnClick(v);
            }
            show();
        } else if (id == R.id.no_network_view) {
            if (NetworkUtil.isNetworkConected(mContext)) {
                if (myCallback != null) {
                    myCallback.onNoNetworkViewClick(v);
                }
            } else {
                ToastUtil.showToastShort(mContext, "视频加载失败，请检查网络连接情况");
            }
        } else if (id == R.id.selections) {
            hide();

            if (myCallback == null) return;

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (selectionsPopWin == null) {
                        selectionsPopWin = new SelectionsPopWin(mContext, contentView);
                    }
                    selectionsPopWin.setSelectionsCallback(PlayerControllerView.this);
                    selectionsPopWin.setOnDismissListener(onDismissListener);
                    selectionsPopWin.show(myCallback.getTvSeriesData());
                }
            }, 200);
        } else if (id == R.id.next_btn) {
            if (myCallback != null) {
                myCallback.nextBtnClick(v);
            }
        }
    }

    private int setProgress() {
        if (myCallback == null || mDragging) {
            return 0;
        }
        int position = myCallback.getCurrentPosition();
        int duration = myCallback.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = myCallback.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        if (mEndTime != null)
            mEndTime.setText(stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));

        return position;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public void setNoNetworkErr() {
        if (!NetworkUtil.isNetworkConected(mContext)) {
            ToastUtil.showToastShort(mContext, "视频加载失败，请检查网络连接情况");
        }

        hideLoading();
        playPauseView.setVisibility(View.VISIBLE);
        noNetworkView.setVisibility(View.VISIBLE);
        mCenterPlayBtn.setVisibility(View.GONE);
        hide();
    }

    private PopupWindow.OnDismissListener onDismissListener = new PopupWindow.OnDismissListener() {

        @Override
        public void onDismiss() {
            LogUtil.i(TAG, "PopupWindow.OnDismissListener onDismiss");
//            show();
        }
    };

    public void onDestroy() {
        dismissSoundWindow();
        seekSound = null;
    }
}
