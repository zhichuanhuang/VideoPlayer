package ucweb.video.model;

import android.content.Context;
import android.os.Bundle;

/**
 * desc: base model
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
public class BaseModel {

    protected Context mContext;

    public BaseModel(Context context) {
        this.mContext = context;
    }

    protected void onCreate(Bundle savedInstanceState) {

    }

    protected void onResume() {

    }

    protected void onStop() {

    }

    protected void onDestroy() {

    }
}
