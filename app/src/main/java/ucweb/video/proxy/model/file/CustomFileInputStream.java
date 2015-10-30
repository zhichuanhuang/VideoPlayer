package ucweb.video.proxy.model.file;

import android.content.Context;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import ucweb.util.LogUtil;

/**
 * desc: 自定义读取文件字节的FileInputStream
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
public class CustomFileInputStream extends FileInputStream {

    private static final String TAG = CustomFileInputStream.class.getSimpleName();

    private ReadFileBytes readFileBytes;

    public CustomFileInputStream(Context context, String urlId, String urlId2, String filePath, long fileLength) throws FileNotFoundException {
        super(new File(filePath));
        this.readFileBytes = new ReadFileBytes(context, urlId, urlId2, filePath, fileLength);
    }

    public CustomFileInputStream(FileDescriptor fd) {
        super(fd);
    }

    public CustomFileInputStream(String path) throws FileNotFoundException {
        super(path);
    }

    @Override
    public long skip(long byteCount) throws IOException {
        LogUtil.i(TAG, "skip byteCount = " + byteCount);
        readFileBytes.skip(byteCount);
        return super.skip(byteCount);
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {

        return readFileBytes.readBytes(buffer, byteOffset, byteCount);
    }

    @Override
    public void close() throws IOException {
        LogUtil.i(TAG, "close");
        super.close();
        if (readFileBytes != null) {
            readFileBytes.close();
        }
    }
}
