package ucweb.web.ui;

import java.util.List;

import ucweb.web.model.entity.VideoEntity;

/**
 * desc: web view video.video.player.listener
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/9/29
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public interface WebUIListener {

    void refreshData(List<VideoEntity> vList);

    void setTitle(VideoEntity videoModel);

    int getDuration();

    int getCurrentPosition();

    void reset(String path, int msec);

    void notifyDataSetChanged();
}
