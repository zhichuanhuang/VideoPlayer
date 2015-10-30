package ucweb.web.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ucweb.web.model.entity.TVSeriesEntity;
import ucweb.util.LogUtil;

/**
 * 保存剧集的数据，包括播放记录
 */
public class TvSeriesDBService extends DBService {
    
    public static final String TABLE_NAME = "tbl_tv_series";

    public static final String ID_KEY = "hash";

    public static final String TBL_CREATE = "create table if not exists " + TABLE_NAME
            + "(id integer primary key autoincrement, hash text null, "
            + "child text null, count integer null, currNum integer null, "
            + "title text null, content text null);";

    
    public static long insertData(Context c, String idValue, TVSeriesEntity video) {
        
        if (!isOpen(c)) return -1;
        
        return insertData(TABLE_NAME, ID_KEY, idValue, video);
    }

    public static long insertData(Context c, TVSeriesEntity video) {

        if (!isOpen(c)) return -1;

        return insert(TABLE_NAME, video);
    }

    /** 通过cid查找 */
    public static Object queryData(Context c, String idValue, TVSeriesEntity video) {
        
        if (!isOpen(c)) return null;
        
        return queryData(TABLE_NAME, ID_KEY, idValue, video);
    }

    public static long updateData(Context c, String idValue, TVSeriesEntity video) {
        
        if (!isOpen(c)) return -1;
        
        return updateData(TABLE_NAME, ID_KEY, idValue, video);
    }

    /**
     * 降序查找TABLENAME表的所有数据
     *
     * @return
     */
    public static List<TVSeriesEntity> queryTableDESC() {
        Cursor cursor = null;

        List<TVSeriesEntity> dataList = new ArrayList<TVSeriesEntity>();

        try {
            cursor = mDb.query(TABLE_NAME, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToLast()) {

                while (!cursor.isBeforeFirst()) {

                    TVSeriesEntity data = (TVSeriesEntity) queryCursor(cursor, new TVSeriesEntity());

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
    public static List<TVSeriesEntity> queryTableASC() {
        Cursor cursor = null;

        List<TVSeriesEntity> dataList = new ArrayList<TVSeriesEntity>();

        try {
            cursor = mDb.query(TABLE_NAME, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {

                while (!cursor.isAfterLast()) {

                    TVSeriesEntity data = (TVSeriesEntity) queryCursor(cursor, new TVSeriesEntity());

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

    public static long updatePlayRecord(Context c, TVSeriesEntity tv) {

        if (!isOpen(c)) return -1;

        ContentValues values = new ContentValues();

        try {
            values.put("child", tv.getChild());
            values.put("currNum", tv.getCurrNum());

            return mDb.update(TABLE_NAME, values, ID_KEY + "= '" + tv.getHash() + "'", null);
        } finally {
            values.clear();
        }
    }

}
