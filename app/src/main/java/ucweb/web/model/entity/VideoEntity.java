package ucweb.web.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * desc: 单一视频实体类
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/8/18
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class VideoEntity implements Parcelable {

    private String parent;

    private int dur;

    private int currPos;

    private String hash;

    /** 视频地址*/
    private String path;

    /** 视频标题*/
    private String title;

    /** 视频的描述*/
    private String content;

    /** 预览图*/
    private String preview;

    private int videoType;

    /** 第几集*/
    private int numInt;

    public int getNumInt() {
        return numInt;
    }

    public void setNumInt(int numInt) {
        this.numInt = numInt;
    }

    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public VideoEntity() {}

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getCurrPos() {
        return currPos;
    }

    public void setCurrPos(int currPos) {
        this.currPos = currPos;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public int getDur() {
        return dur;
    }

    public void setDur(int dur) {
        this.dur = dur;
    }

    public VideoEntity(Parcel source) {
        parent = source.readString();
        dur = source.readInt();
        currPos = source.readInt();
        hash = source.readString();
        path = source.readString();
        title = source.readString();
        content = source.readString();
        preview = source.readString();
        videoType = source.readInt();
        numInt = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parent);
        dest.writeInt(dur);
        dest.writeInt(currPos);
        dest.writeString(hash);
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(preview);
        dest.writeInt(videoType);
        dest.writeInt(numInt);
    }

    public final static Parcelable.Creator<VideoEntity> CREATOR = new Creator<VideoEntity>() {
        @Override
        public VideoEntity createFromParcel(Parcel source) {
            return new VideoEntity(source);
        }

        @Override
        public VideoEntity[] newArray(int size) {
            return new VideoEntity[size];
        }
    };
}
