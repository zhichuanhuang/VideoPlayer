package ucweb.video.proxy.model.downloader;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ucweb.util.LogUtil;
import ucweb.web.model.db.VideoDownRecordDBService;

/**
 * desc: 通过代理下载视频文件
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/10/23
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class ProxyVideoDownloader extends Thread {

    private static final String TAG = ProxyVideoDownloader.class.getSimpleName();

    private Context mContext;

    private String urlId;

    private String remoteUrl;

    private int rangDownStar;

    private int rangDownEnd;

    private String filePath;

    public void setStop() {
        this.stop = true;
    }

    private boolean stop = true;

    public ProxyVideoDownloader(Context context, String urlId, String remoteUrl, int rangDownStar, int rangDownEnd, String filePath) {
        this.mContext = context;
        this.urlId = urlId;
        this.remoteUrl = remoteUrl;
        this.rangDownStar = rangDownStar;
        this.rangDownEnd = rangDownEnd;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        super.run();
        stop = false;
        InputStream is = null;
        HttpURLConnection conn;
        RandomAccessFile raf = null;
        try {
            URL url = new URL(remoteUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("Range", "bytes=" + rangDownStar + "-" + rangDownEnd);

            raf = new RandomAccessFile(new File(filePath), "rws");
            raf.seek(rangDownStar);

            is = conn.getInputStream();

            if (stop) return;

            byte[] buf = new byte[1024 * 8];
            int len;
            int downloadedSize = 0;
            while ((len = is.read(buf)) != -1) {

                if (stop) break;

                raf.write(buf, 0, len);
                downloadedSize += len;

                // 同步数据库记录
                VideoDownRecordDBService.updateDownRecord(mContext, urlId, 0, rangDownStar + downloadedSize);
            }
        } catch (MalformedURLException e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        } catch (IOException e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        } catch (NullPointerException e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        } finally {
            stop = true;
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    if (LogUtil.DEBUG) e.printStackTrace();
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    if (LogUtil.DEBUG) e.printStackTrace();
                }
            }
        }
    }
}
