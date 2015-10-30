package ucweb.splash.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.ImageView;

import ucweb.util.LogUtil;
import ucweb.util.SystemUtil;
import ucweb.video.R;
import ucweb.web.activity.WebActivity;

/**
 * desc: 闪屏启动页
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/9/15
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemUtil.hideNavigationBar(this);
        super.onCreate(savedInstanceState);
        LogUtil.i("SplashActivity", "onCreate");
        ImageView img = new ImageView(this);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        img.setLayoutParams(lp);
        img.setImageResource(R.drawable.splash);

        setContentView(img);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, WebActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemUtil.hideNavigationBar(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        SystemUtil.hideNavigationBar(this);
    }
}
