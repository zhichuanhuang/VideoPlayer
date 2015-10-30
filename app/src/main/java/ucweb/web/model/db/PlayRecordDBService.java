package ucweb.web.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ucweb.web.model.entity.VideoEntity;
import ucweb.util.LogUtil;

/**
 * 保存单个视频的数据，包括播放记录
 */
public class PlayRecordDBService extends DBService {
    
    public static final String TABLE_NAME = "tbl_play_record";

    public static final String ID_KEY = "hash";

    public static final String TBL_CREATE = "create table if not exists " + TABLE_NAME
            + "(id integer primary key autoincrement, hash text null, "
            + "parent text null, dur integer null, currPos integer null, path text null, "
            + "title text null, content text null, preview text null, videoType text null, " +
            "numInt integer null);";

    
    public static long insertData(Context c, String idValue, VideoEntity video) {
        
        if (!isOpen(c)) return -1;
        
        return insertData(TABLE_NAME, ID_KEY, idValue, video);
    }

    public static long insertData(Context c, VideoEntity video) {

        if (!isOpen(c)) return -1;

        return insert(TABLE_NAME, video);
    }

    /** 通过cid查找 */
    public static Object queryData(Context c, String idValue, VideoEntity video) {
        
        if (!isOpen(c)) return null;
        
        return queryData(TABLE_NAME, ID_KEY, idValue, video);
    }

    public static long updateData(Context c, String idValue, VideoEntity video) {
        
        if (!isOpen(c)) return -1;
        
        return updateData(TABLE_NAME, ID_KEY, idValue, video);
    }

    /**
     * 降序查找TABLENAME表的所有数据
     *
     * @return
     */
    public static List<VideoEntity> queryTableDESC() {
        Cursor cursor = null;

        List<VideoEntity> dataList = new ArrayList<VideoEntity>();

        try {
            cursor = mDb.query(TABLE_NAME, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToLast()) {

                while (!cursor.isBeforeFirst()) {

                    VideoEntity data = (VideoEntity) queryCursor(cursor, new VideoEntity());

                    dataList.add(data);

                    cursor.moveToPrevious();
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

    /**
     * 升序查找TABLENAME表的所有数据
     *
     * @return
     */
    public static List<VideoEntity> queryTableASC(String parent) {
        Cursor cursor = null;

        List<VideoEntity> dataList = new ArrayList<VideoEntity>();

        try {
            cursor = mDb.query(TABLE_NAME, null, "parent = '" + parent + "'", null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {

                while (!cursor.isAfterLast()) {

                    VideoEntity data = (VideoEntity) queryCursor(cursor, new VideoEntity());

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

    public static int delete(Context c) {
        
        if (!isOpen(c)) return -1;
        
        return delete(TABLE_NAME);
    }

    public static long updatePlayRecord(Context c, String idValue, int dur, int currPos) {

        if (!isOpen(c)) return -1;

        ContentValues values = new ContentValues();

        try {
            if (dur > 0) {
                values.put("dur", dur);
            }
            values.put("currPos", currPos);

            return mDb.update(TABLE_NAME, values, ID_KEY + "= '" + idValue + "'", null);
        } finally {
            values.clear();
        }
    }
}
