package ucweb.video.player.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.List;

import ucweb.video.player.view.adapter.SeriesListAdapter;
import ucweb.video.player.listener.SelectionsCallback;
import ucweb.web.model.entity.VideoEntity;
import ucweb.util.SystemUtil;
import ucweb.video.R;

/**
 * desc: 选集popwindow
 * <br/>
 * author: zhichuanhuang
 * <br/>
 * date: 2015/9/14
 * <br/>
 * mail: 670534823@qq.com
 * <br/>
 * phone: 15914375424
 * <br/>
 * version: 1.0
 */
public class SelectionsPopWin implements AdapterView.OnItemClickListener {

    private PopupWindow popupWindow;

    private Context context;

    private View parent;

    private PopupWindow.OnDismissListener dismissListener;

    public void setSelectionsCallback(SelectionsCallback selectionsCallback) {
        this.selectionsCallback = selectionsCallback;
    }

    private SelectionsCallback selectionsCallback;

    public SelectionsPopWin(Context c, View p) {
        context = c;
        parent = p;
    }

    public void show(List<VideoEntity> vList) {
        if (vList == null) return;

        View view = View.inflate(context, R.layout.tv_selections, null);
        ListView seriesList = (ListView) view.findViewById(R.id.series_list);
        seriesList.setOnItemClickListener(this);
        SeriesListAdapter adapter = new SeriesListAdapter(context, vList);
        seriesList.setAdapter(adapter);

        int popWidth = SystemUtil.dip2px(context, 200); //SystemUtil.getDisplayWidth(context) / 3;
        popupWindow = new PopupWindow(view, popWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.selections_pop_win_anim_style);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(dismissListener);

        int xPos = 0;
        int yPos = 0;
        popupWindow.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, xPos, yPos);
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener l) {
        this.dismissListener = l;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (selectionsCallback != null) {
            selectionsCallback.videoSwitch(position);
        }

        popupWindow.dismiss();
    }
}
