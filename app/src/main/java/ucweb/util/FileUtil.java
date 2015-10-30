package ucweb.util;

import java.io.File;

/**
 * desc: file util
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
public class FileUtil {

    /**
     * 删除文件
     * @param file
     */
    public static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }

        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFile = file.listFiles();

            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }

            for (File f : childFile) {
                deleteFile(f);
            }

            file.delete();
        }
    }
}
