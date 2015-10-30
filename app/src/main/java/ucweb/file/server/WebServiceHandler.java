package ucweb.file.server;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import ucweb.util.LogUtil;

/**
 * desc: http请求处理
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/10/27
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class WebServiceHandler implements HttpRequestHandler {

    private static final String TAG = WebServiceHandler.class.getSimpleName();

    private String fileDirPath;

    public WebServiceHandler(String path) {
        super();
        this.fileDirPath = path;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException, NullPointerException, NumberFormatException {
        String method = request.getRequestLine().getMethod()
                .toUpperCase(Locale.ENGLISH);

        // get uri
        String target = request.getRequestLine().getUri();

        int num = getNum(target);
        String filePath = fileDirPath + File.separator + num + ".mp4";

        LogUtil.i(TAG, "filePath = " + filePath);

        // 判断file是否存在
        File file = new File(filePath);
        if (!file.exists()) return;

        if (method.equals("GET")) {
            try {
                // 206，支持断点续传
                response.setStatusCode(HttpStatus.SC_PARTIAL_CONTENT);
                LogUtil.i(TAG, "GET");

                // tell the client to allow accept-ranges
                response.setHeader("Accept-Ranges", "bytes");

                long range = 0;
                Header header = request.getFirstHeader("Range");
                if (header != null) {
                    String rangeStr = header.getValue();
                    if (rangeStr != null) {
                        int p1 = rangeStr.indexOf("=");
                        String s2 = rangeStr.substring(p1 + 1);
                        String[] rangeArr = s2.split("-");
                        if (rangeArr.length > 0) {
                            range = Long.valueOf(rangeArr[0]);
                        }
                    }
                }

                // get file length
                long fileLength = file.length();

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

                FileInputStream fis = new FileInputStream(file);
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
                entity.setContentLength(bis.available());
                response.setEntity(entity);
            } catch (Exception e) {
                if (LogUtil.DEBUG) e.printStackTrace();
                response.setStatusCode(HttpStatus.SC_EXPECTATION_FAILED);
                StringEntity entity = new StringEntity("<xml><method>get</method><url>" + target + "</url></xml>");
                response.setEntity(entity);
            }
        } else if (method.equals("POST")) {
            response.setStatusCode(HttpStatus.SC_OK);
            StringEntity entity = new StringEntity("<xml><method>post</method><url>" + target + "</url></xml>");
            response.setEntity(entity);
        } else {
            throw new MethodNotSupportedException(method + " method not supported");
        }
    }

    /**
     * 获取集数
     * @param uri eg: /doAction?num=2
     * @return
     */
    private static int getNum(String uri) {
        try {
            int p = uri.indexOf("=");
            String str = uri.substring(p + 1);
            return Integer.valueOf(str);
        } catch (NullPointerException e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        } catch (NumberFormatException e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        }

        return 1;
    }
}
