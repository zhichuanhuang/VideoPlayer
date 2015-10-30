package ucweb.file.server;

import java.io.IOException;

import ucweb.util.LogUtil;

/**
 * desc: 简单的http服务器
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
public class HttpServer {

    private static final String TAG = HttpServer.class.getSimpleName();

    private static String fileDirPath;

    public HttpServer(String fPath) {
        fileDirPath = fPath;
    }

    public void star(int port) throws IOException {
        LogUtil.i(TAG, "onCreate");
        Thread t = new HttpRequestListenerThread(port, fileDirPath);
        t.setDaemon(true);
        t.start();
    }
}
