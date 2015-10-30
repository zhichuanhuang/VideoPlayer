package ucweb.task;

import android.content.Context;
import android.os.Handler;

/**
 * 异步请求任务处理
 */
public class TaskWithHandler extends UserTask<Integer, Void, Integer> {

    public Handler mHandler = null;

    public TaskWithHandler(Context context) {
        super(context);
    }

    public TaskWithHandler(Context context, Handler handler) {
        super(context);
        this.mHandler = handler;
    }

    protected void onTaskPrepare() {
        if ( mHandler != null ) {
            mHandler.sendEmptyMessage(0x001);
        }
        super.onTaskPrepare();
    }

    protected void onErrorHandle(Context context, Exception error) {
        if ( mHandler != null ) {
            mHandler.sendEmptyMessage(0x002);
        }
    }

    protected void onTaskFinished(Context context, Integer result) {
        if ( mHandler != null ) {
            mHandler.sendEmptyMessage(0x003);
        }
    }

}
