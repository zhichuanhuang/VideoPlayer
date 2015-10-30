package ucweb.video.proxy.model.server;

import android.content.Context;

import java.io.IOException;

/**
 * desc: 本地网络视频资源代理。
 * <br/>
 * 播放器请求代理链接，代理再去请求网络视频资源，
 * <br/>
 * 把网络视频下载到本地，然后代理返回本地资源给到播放器
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/9/24
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class ProxyServer {

    private static Context mContext;

    private static String fileDirPath;

    public ProxyServer(Context context, String fPath) {
        mContext = context;
        fileDirPath = fPath;
    }

    public void star(int port) throws IOException {
        Thread t = new ProxyRequestListenerThread(port, mContext, fileDirPath);
        t.setDaemon(true);
        t.start();
    }


}
