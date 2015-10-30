package ucweb.video;

// 视频播放器前端aidl接口
interface AidlVideoActivity {

    void cacheVideoReady(long cacheSize, long totalSize);
    
    void downloadFinish();
    
    void downloadUpdate(long cacheSize);
}