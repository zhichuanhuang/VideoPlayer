package ucweb.video.application;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;

import ucweb.util.LogUtil;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author Mr.huang
 */
public class CrashHandler implements UncaughtExceptionHandler {

    //系统默认的UncaughtException处理类 
    private UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    public void uncaughtException(Thread thread, Throwable ex) {
        if (ex == null && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            if (LogUtil.ERROR) {
                ex.printStackTrace();
            }
            handleException(ex);

            // 退出视频播放时需要杀掉远程进程
            System.exit(0);

            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 自定义错误处理.
     *
     * @param ex
     */
    private void handleException(Throwable ex) {
        LogUtil.e("handleException", "----------------handleException-----------------------");
    }
}
