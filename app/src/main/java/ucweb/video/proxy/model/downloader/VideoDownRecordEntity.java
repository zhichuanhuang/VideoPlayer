package ucweb.video.proxy.model.downloader;

/**
 * desc: 视频下载记录实体
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/10/22
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class VideoDownRecordEntity {

    private String urlId;

    private int length;

    private int starPos;

    private int endPos;

    public String getUrlId() {
        return urlId;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getStarPos() {
        return starPos;
    }

    public void setStarPos(int starPos) {
        this.starPos = starPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }
}
