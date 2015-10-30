package ucweb.video.proxy.model.server;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;

import java.io.IOException;

import ucweb.util.LogUtil;

/**
 * desc: 网络请求分发线程
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/10/24
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class ProxyWorkerThread extends Thread {

    private static final String TAG = ProxyWorkerThread.class.getSimpleName();

    private final HttpService httpService;
    private final HttpServerConnection serverConn;

    public ProxyWorkerThread(HttpService httpService, HttpServerConnection serverConn) {
        this.httpService = httpService;
        this.serverConn = serverConn;
    }

    @Override
    public void run() {
        HttpContext context = new BasicHttpContext(null);
        try {
            while (!Thread.interrupted() && this.serverConn.isOpen()) {
                this.httpService.handleRequest(this.serverConn, context);
            }
        } catch (ConnectionClosedException ex) {
            if (LogUtil.DEBUG) ex.printStackTrace();
        } catch (IOException ex) {
            if (LogUtil.DEBUG) ex.printStackTrace();
        } catch (HttpException ex) {
            if (LogUtil.DEBUG) ex.printStackTrace();
        } catch (NullPointerException ex) {
            if (LogUtil.DEBUG) ex.printStackTrace();
        } catch (NumberFormatException ex) {
            if (LogUtil.DEBUG) ex.printStackTrace();
        } finally {
            try {
                this.serverConn.shutdown();
            } catch (IOException ignore) {
                if (LogUtil.DEBUG) ignore.printStackTrace();
            }
        }
    }
}
