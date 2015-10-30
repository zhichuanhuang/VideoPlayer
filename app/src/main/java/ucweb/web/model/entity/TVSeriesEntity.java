package ucweb.web.model.entity;

/**
 * desc: 视频剧集实体类
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/9/10
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class TVSeriesEntity {

    private String hash;

    private String title;

    private int count;

    private int currNum;

    private String child;

    private String content;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCurrNum() {
        return currNum;
    }

    public void setCurrNum(int currNum) {
        this.currNum = currNum;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
