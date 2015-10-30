package ucweb.video.player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import ucweb.video.R;

/**
 * desc: Loading加载控件
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015-08-17
 */
public class LoadingImageView extends ImageView {

    public LoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LoadingImageView(Context context) {
        super(context);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.comm_loading);
        startAnimation(hyperspaceJumpAnimation);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
    }
    
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.INVISIBLE || visibility == View.GONE) {
            clearAnimation();
            return;
        }
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.comm_loading);
        startAnimation(hyperspaceJumpAnimation);
    }
}
