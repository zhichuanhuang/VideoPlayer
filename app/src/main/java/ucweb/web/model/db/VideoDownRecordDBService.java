package ucweb.web.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ucweb.util.LogUtil;
import ucweb.video.proxy.model.downloader.VideoDownRecordEntity;

/**
 * 保存视频的下载记录
 */
public class VideoDownRecordDBService extends DBService {
    
    public static final String TABLE_NAME = "tbl_video_down_record";

    public static final String ID_KEY = "urlId";

    public static final String TBL_CREATE = "create table if not exists " + TABLE_NAME
            + "(id integer primary key autoincrement, urlId text null, "
            + "length integer null, starPos integer null, endPos integer null);";

    /** 慎用*/
    public static long insertData(Context c, String idValue, VideoDownRecordEntity video) {

        if (!isOpen(c)) return -1;

        return insertData(TABLE_NAME, ID_KEY, idValue, video);
    }

    public static long insertData(Context c, VideoDownRecordEntity video) {

        if (!isOpen(c)) return -1;

        return insert(TABLE_NAME, video);
    }

    /** 通过cid查找 */
    public static Object queryData(Context c, String idValue, VideoDownRecordEntity video) {
        
        if (!isOpen(c)) return null;
        
        return queryData(TABLE_NAME, ID_KEY, idValue, video);
    }

    public static long updateData(Context c, String idValue, VideoDownRecordEntity video) {
        
        if (!isOpen(c)) return -1;
        
        return updateData(TABLE_NAME, ID_KEY, idValue, video);
    }

    public static int delete(Context c) {
        
        if (!isOpen(c)) return -1;
        
        return delete(TABLE_NAME);
    }

    public static int delete(Context c, String idValue) {

        if (!isOpen(c)) return -1;

        try {

            return mDb.delete(TABLE_NAME, ID_KEY + "= '" + idValue + "'", null);
        } catch (Exception e) {

            if (LogUtil.DEBUG) e.printStackTrace();
        }

        return -1;
    }

    public static long updateDownRecord(Context c, String idValue, int starPos, int endPos) {

        if (!isOpen(c)) return -1;

        ContentValues values = new ContentValues();

        try {
            if (starPos > 0) {
                values.put("starPos", starPos);
            }

            if (endPos > 0) {
                values.put("endPos", endPos);
            }

            return mDb.update(TABLE_NAME, values, ID_KEY + "= '" + idValue + "'", null);
        } catch (Exception e) {

            if (LogUtil.DEBUG) e.printStackTrace();
        } finally {
            values.clear();
        }

        return 0;
    }

    /**
     * 升序查找TABLENAME表的所有数据
     *
     * @return List<VideoEntity>
     */
    public static List<VideoDownRecordEntity> queryTableASC() {
        Cursor cursor = null;

        List<VideoDownRecordEntity> dataList = new ArrayList<VideoDownRecordEntity>();

        try {
            cursor = mDb.query(TABLE_NAME, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {

                while (!cursor.isAfterLast()) {

                    VideoDownRecordEntity data = (VideoDownRecordEntity) queryCursor(cursor, new VideoDownRecordEntity());

                    dataList.add(data);

                    cursor.moveToNext();
                }
            }

            return dataList;
        } catch (Exception e) {

            if (LogUtil.DEBUG) e.printStackTrace();
        } finally {

            if (cursor != null) cursor.close();
        }

        return null;
    }
}
