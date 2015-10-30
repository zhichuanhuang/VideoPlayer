package ucweb.video.proxy.model.server;

import android.content.Context;

import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import ucweb.util.LogUtil;

/**
 * desc: 请求监听线程
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
public class ProxyRequestListenerThread extends Thread {

    private static final String TAG = ProxyRequestListenerThread.class.getSimpleName();

    private ServerSocket serverSocket = null;

    private final HttpParams params;

    private final HttpService httpService;

    public ProxyRequestListenerThread(int port, Context context, String fPath) throws IOException {
        serverSocket = new ServerSocket(port);

        HttpProcessor httpProc = new ImmutableHttpProcessor(new HttpResponseInterceptor[]{new ResponseDate(),
                new ResponseServer(), new ResponseContent(), new ResponseConnControl()});
        params = new BasicHttpParams();
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 18000)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");

        // Set up request handlers
        HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
        //WebServiceHandler用来处理webservice请求。
        registry.register("*", new ProxyRequestHandler(context, fPath));

        httpService = new HttpService(httpProc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
        httpService.setParams(params);
        // 为http服务设置注册好的请求处理器。
        httpService.setHandlerResolver(registry);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Socket socket = serverSocket.accept();
                LogUtil.i(TAG, "Incoming connection from " + socket.getInetAddress());
                DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                conn.bind(socket, params);

                // Start worker thread
                Thread t = new ProxyWorkerThread(this.httpService, conn);
                t.setDaemon(true);
                t.start();
            } catch (InterruptedIOException e) {
                if (LogUtil.DEBUG) e.printStackTrace();
                break;
            } catch (IOException e) {
                if (LogUtil.DEBUG) e.printStackTrace();
                break;
            }
        }
    }
}
