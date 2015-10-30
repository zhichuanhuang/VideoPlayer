package ucweb.video.proxy.model.server;

import android.content.Context;
import android.text.TextUtils;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import ucweb.util.LogUtil;
import ucweb.util.MD5;
import ucweb.video.proxy.model.downloader.ProxyVideoDownloader;
import ucweb.video.proxy.model.downloader.VideoDownRecordEntity;
import ucweb.video.proxy.model.file.CustomFileInputStream;
import ucweb.web.model.db.VideoDownRecordDBService;

/**
 * desc: http请求处理，视频网络代理
 * <br/>
 * 记录两段下载记录，第一段是开始的记录，第二段是seek的记录
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
public class ProxyRequestHandler implements HttpRequestHandler {

    private static final String TAG = ProxyRequestHandler.class.getSimpleName();

    private static Context mContext;

    private static String fileDirPath;

    public ProxyRequestHandler(Context context, String fPath) {
        super();
        mContext = context;
        fileDirPath = fPath;
    }

    ProxyVideoDownloader downloader;

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException, NullPointerException, NumberFormatException {
        String method = request.getRequestLine().getMethod()
                .toUpperCase(Locale.ENGLISH);

        // get uri
        String target = request.getRequestLine().getUri();

        String remoteUrl = getTarget(target);

        // 视频标识，用户数据库存储，记录两段下载记录，第一段是开始的记录，第二段是seek的记录
        // 第一段文件存在起始点永远是0
        String urlId = MD5.toMD5(target);
        String urlId2 = urlId + 1;

        String filePath = new StringBuilder().append(fileDirPath).append(File.separator).append(urlId).toString();

        int videoTotalSize = 0;

        // 数据库记录查找
        VideoDownRecordEntity video = (VideoDownRecordEntity) VideoDownRecordDBService.queryData(mContext, urlId, new VideoDownRecordEntity());
        if (video == null) {
            File file = new File(filePath);
            if (file.exists()) file.delete();
        } else {
            videoTotalSize = video.getLength();
        }

        // 判断file是否存在
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();

            URL url = new URL(remoteUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) url
                    .openConnection();
            httpConnection.setConnectTimeout(6000);
            httpConnection.setRequestProperty("RANGE", "bytes=" + 0 + "-");

            videoTotalSize = httpConnection.getContentLength();
            LogUtil.i(TAG, "videoTotalSize = " + videoTotalSize);

            video = new VideoDownRecordEntity();
            video.setUrlId(urlId);
            video.setLength(videoTotalSize);

            VideoDownRecordDBService.delete(mContext, urlId);
            VideoDownRecordDBService.insertData(mContext, video);
            VideoDownRecordDBService.delete(mContext, urlId2);
        }

        if (method.equals("GET")) {
            // 首先要停止掉上一次下载的线程
            if (downloader != null) {
                downloader.setStop();
                try {
                    downloader.interrupt();
                } catch (Exception e) {
                    if (LogUtil.DEBUG) e.printStackTrace();
                }

                downloader = null;
            }

            try {
                int range = 0;
                int range2 = 0;
                Header header = request.getFirstHeader("Range");
                if (header != null) {
                    String rangeStr = header.getValue();
                    LogUtil.i(TAG, "rangeStr = " + rangeStr);
                    if (!TextUtils.isEmpty(rangeStr)) {
                        int p1 = rangeStr.indexOf("=");
                        String s2 = rangeStr.substring(p1 + 1);
                        String[] rangeArr = s2.split("-");
                        if (rangeArr.length > 0) {
                            range = Integer.valueOf(rangeArr[0]);
                        }

                        if (rangeArr.length > 1) {
                            range2 = Integer.valueOf(rangeArr[1]);
                        }
                    }
                }

                LogUtil.i(TAG, "range = " + range + " ,range2 = " + range2);

                if (range2 < 1) {
                    // bytes=0-
                    range2 = videoTotalSize - 1;
                }

                int recordStar1;
                int recordEnd1;
                /** 判断是否和record1有交集*/
                boolean inRecord1 = false;
                // 比较第一段记录
                if (video != null && range < video.getEndPos() && range >= video.getStarPos()) {
                    // 播放器请求的range范围开始部分和本地文件存在交集
                    if (range2 <= video.getEndPos()) {
                        // 播放器请求的rang范围已经存在文件内
                        respone(response, range, range2, file, videoTotalSize, urlId, urlId2);
                        // 无需再下载，直接respose
                        return;
                    }

                    recordStar1 = video.getEndPos();
                    recordEnd1 = range2;
                    inRecord1 = true;
                } else {
                    // 播放器请求的range范围开始部分和本地文件不存在交集
                    recordStar1 = range;
                    recordEnd1 = range2;

                    if (recordStar1 == 0) {
                        inRecord1 = true;
                    }
                }

                // 同步第一段文件存在起始点，第一段文件存在起始点永远是0
//                VideoDownRecordDBService.updateDownRecord(mContext, urlId, 0, 0);

                int rangDownStar;
                int rangDownEnd;
                String uId = urlId;
                // 查找第二段记录
                VideoDownRecordEntity record2 = new VideoDownRecordEntity();
                record2 = (VideoDownRecordEntity) VideoDownRecordDBService.queryData(mContext, urlId2, record2);
                // 比较第二段记录
                if (record2 != null && recordStar1 < record2.getEndPos() && recordStar1 >= record2.getStarPos()) {
                    // 播放器请求的range范围开始部分和本地文件存在交集
                    if (recordEnd1 <= record2.getEndPos()) {
                        // 播放器请求的rang范围已经存在文件内
                        respone(response, range, range2, file, videoTotalSize, urlId, urlId2);
                        // 无需再下载，直接respose
                        return;
                    }

                    rangDownStar = record2.getEndPos();
                    rangDownEnd = recordEnd1;

                    // 同步数据库记录，记录文件存在起始点
                    VideoDownRecordDBService.updateDownRecord(mContext, urlId2, record2.getStarPos(), 0);

                    uId = urlId2;
                } else {
                    // 播放器请求的range范围开始部分和本地文件不存在交集
                    rangDownStar = recordStar1;
                    rangDownEnd = recordEnd1;

                    // 如果和record1存在交集，则更新record1，如果没有，则更新record2
                    uId = inRecord1 ? urlId : urlId2;

                    if (!inRecord1) {
                        //
                        VideoDownRecordEntity record = new VideoDownRecordEntity();
                        record.setUrlId(urlId2);
                        record.setLength(videoTotalSize);
                        record.setStarPos(rangDownStar);
                        VideoDownRecordEntity record_ = (VideoDownRecordEntity) VideoDownRecordDBService.queryData(mContext, urlId2, new VideoDownRecordEntity());
                        if (record_ == null) {
                            VideoDownRecordDBService.insertData(mContext, record);
                        } else {
                            VideoDownRecordDBService.updateData(mContext, urlId2, record);
                        }
                    }
                }

                rangDownEnd = rangDownEnd != 0 ? rangDownEnd : videoTotalSize - 1;
                LogUtil.i(TAG, "seek rangDownStar = " + rangDownStar + " ,rangDownEnd = " + rangDownEnd);

                downloader = new ProxyVideoDownloader(mContext, uId, remoteUrl, rangDownStar, rangDownEnd, filePath);
                downloader.start();

                respone(response, range, range2, file, videoTotalSize, urlId, urlId2);
            } catch (Exception e) {
                if (LogUtil.DEBUG) e.printStackTrace();
                response.setStatusCode(HttpStatus.SC_EXPECTATION_FAILED);
                StringEntity entity = new StringEntity("exception");
                response.setEntity(entity);
            } finally {
                // TODO
            }
        } else if (method.equals("POST")) {
            response.setStatusCode(HttpStatus.SC_OK);
            StringEntity entity = new StringEntity("post method is not support");
            response.setEntity(entity);
        } else {
            throw new MethodNotSupportedException(method + " method not supported");
        }
    }

    private void respone(HttpResponse response, int range, int range2, File file, long fileLength, String urlId, String urlId2) {
        if (response == null) return;
        try {
            // 206，支持断点续传
            response.setStatusCode(HttpStatus.SC_PARTIAL_CONTENT);
            LogUtil.i(TAG, "GET");

            // tell the client to allow accept-ranges
            response.setHeader("Accept-Ranges", "bytes");
            // 断点开始
            // 响应的格式是:
            // Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
            String contentRange = new StringBuilder()
                    .append("bytes ")
                    .append(range)
                    .append("-")
                    .append(fileLength - 1)
                    .append("/")
                    .append(fileLength)
                    .toString();
            response.setHeader("Content-Range", contentRange);

            String fileName = file.getName();
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Connection", "keep-alive");

            CustomFileInputStream fis = new CustomFileInputStream(mContext, urlId, urlId2, file.getPath(), fileLength);
//                FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.skip(range);

            // BasicHttpEntity这类就是一个输入流的内容包装类，包装内容的相关的编码格式，长度等
            BasicHttpEntity entity = new BasicHttpEntity();
            //设置内容
            entity.setContent(bis);
            //设置长度
            // Content-Length 不能随便设置，否则会报
            // org.apache.http.ProtocolException: Content-Length header already present
            // Content-Length首部对于持久链接是必不可少的
            // 有一种情况，使用持久连接可以没有Content-Length首部，即采用分块编码（chunked encoding）时。
//                entity.setContentLength(bis.available());
            response.setEntity(entity);
        } catch (Exception e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        } finally {

        }
    }

    /**
     * 获取目标地址
     *
     * @param uri eg: /doAction?num=2
     * @return
     */
    private static String getTarget(String uri) {
        try {
            int p = uri.indexOf("=");
            return uri.substring(p + 1);
        } catch (NullPointerException e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        }

        return null;
    }
}
