package ucweb.video;

import ucweb.video.AidlVideoActivity;

// 视频播放器服务端aidl接口
interface AidlVideoService {

    void registerActivityCall(AidlVideoActivity callback);
    
    boolean checkIsBuffered(int next2sec);
    
    void seekLoadVideo(long time);
    
    void cancelDownload();
    
    long getCacheSize();
    
    long getTotalSize();
}