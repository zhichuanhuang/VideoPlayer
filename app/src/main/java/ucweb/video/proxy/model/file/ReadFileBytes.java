package ucweb.video.proxy.model.file;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import ucweb.util.LogUtil;
import ucweb.video.proxy.model.downloader.VideoDownRecordEntity;
import ucweb.web.model.db.VideoDownRecordDBService;

/**
 * desc: 读取文件字节
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/9/26
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class ReadFileBytes {

    private static final String TAG = ReadFileBytes.class.getSimpleName();

    private RandomAccessFile raf = null;

    private long fileLength = 0;

    private Context mContext;

    private String urlId;

    private String urlId2;

    public ReadFileBytes(Context context, String urlId, String urlId2, String filePath, long fileLength) {
        this.mContext = context;
        this.urlId = urlId;
        this.urlId2 = urlId2;
        this.fileLength = fileLength;
        try {
            raf = new RandomAccessFile(new File(filePath), "rws");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void skip(long byteCount) throws IOException {
        raf.seek(byteCount);
    }

    public int readBytes(byte[] buffer, int byteOffset, int byteCount) {
        try {
            long starPos = raf.getFilePointer();

            if (starPos >= fileLength) return -1;

            long endPos = starPos + byteCount;

            endPos = endPos > fileLength ? fileLength : endPos;

            VideoDownRecordEntity video = (VideoDownRecordEntity) VideoDownRecordDBService.queryData(mContext, urlId, new VideoDownRecordEntity());

            if (video != null && (starPos < video.getStarPos() || starPos > video.getEndPos() || endPos > video.getEndPos() || endPos < video.getStarPos())) {
                VideoDownRecordEntity record2 = (VideoDownRecordEntity) VideoDownRecordDBService.queryData(mContext, urlId2, new VideoDownRecordEntity());
                if (record2 == null) {
                    return 0;
                }

                if (starPos < record2.getStarPos() || starPos > record2.getEndPos() || endPos > record2.getEndPos() || endPos < record2.getStarPos()) {
                    return 0;
                }
            }

            return raf.read(buffer, byteOffset, byteCount);
        } catch (IOException e) {
            if (LogUtil.DEBUG) e.printStackTrace();
        } finally {
            // TODO
        }

        return -1;
    }

    public void close() throws IOException {
        LogUtil.i(TAG, "close");
        if (raf != null) {
            try {
                raf.close();
            } catch (IOException e) {
                if (LogUtil.DEBUG) e.printStackTrace();
            }
        }
    }
}
