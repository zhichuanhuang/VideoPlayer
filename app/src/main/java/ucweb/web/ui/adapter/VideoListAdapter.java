package ucweb.web.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ucweb.util.LogUtil;
import ucweb.web.model.entity.VideoEntity;
import ucweb.util.NumberUtil;
import ucweb.video.R;

/**
 * desc: 视频列表adapter
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
public class VideoListAdapter extends UCBaseAdapter<VideoEntity> {

    private Context c;

    private LayoutInflater inflater;

    public VideoListAdapter(Context c, List<VideoEntity> videoList) {
        super(videoList);
        this.c = c;
        inflater = LayoutInflater.from(c);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LogUtil.i("VideoListAdapter", "getView position = " + position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.video_list_item, null);
            holder.preview = (ImageView) convertView.findViewById(R.id.preview);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.playRecord = (TextView) convertView.findViewById(R.id.play_record);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        VideoEntity video = getItem(position);
        if (video == null) return null;

        String hash = video.getHash();

        try {
            int resID = c.getResources().getIdentifier(video.getPreview(), "drawable", c.getPackageName());
            holder.preview.setImageResource(resID);
        } catch (NullPointerException e) {

        }

        holder.title.setText(video.getTitle());

        holder.playRecord.setTag(hash);
        holder.playRecord.setTag(R.id.list_play_record_id, video);

        int currPos = video.getCurrPos();
//        LogUtil.i("VideoListAdapter", "getView dur = " + video.web.activity.getDur() + " currPos = " + currPos);
        setPlayRecord(holder.playRecord, video.getDur(), currPos);

        return convertView;
    }

    public void setPlayRecord(TextView tv, int dur, int currPos) {
//        LogUtil.i("VideoListAdapter", "setPlayRecord dur = " + dur + " currPos = " + currPos);
        if (currPos == 0) { // 还没开始播放
            tv.setText("未播放");
        } else if (currPos == -1) { //播放完成
            tv.setText("已看完");
        } else {
            if (dur < 1) {
                tv.setText("未播放");
            } else {
                tv.setText("上次看到" + NumberUtil.getCurrPos(dur, currPos) + "%");
            }
        }
    }

    class ViewHolder {
        public ImageView preview;

        public TextView title;

        public TextView playRecord;
    }
}
