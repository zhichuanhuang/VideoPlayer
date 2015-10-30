package ucweb.jni.bridge;

/**
 * desc: jni调用的接口类
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
public class FileJniLib {

    private static final FileJniLib INSTANCE = new FileJniLib();

    private FileJniLib() {}

    public static FileJniLib getInstance() {
        return INSTANCE;
    }

    static {
        System.loadLibrary("readfile-jni");
    }

    public native int readBytes(String fPath, long offset, byte[] buffer, int byteOffset, int byteCount);
}
