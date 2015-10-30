package ucweb.util;

import java.text.DecimalFormat;

/**
 * desc: 数字显示格式工具
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/9/11
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class NumberUtil {

    public static String getCurrPos(int dur, int currPos) {
        long pos = 1000L * currPos / dur;
        double p = (double) pos * 100 / 1000;
        DecimalFormat df = new DecimalFormat("0.0");

        return df.format(p);
    }
}
