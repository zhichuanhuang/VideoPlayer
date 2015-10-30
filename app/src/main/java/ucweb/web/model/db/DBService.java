package ucweb.web.model.db;

import android.content.ContentValues;
import android.database.Cursor;

import ucweb.util.LogUtil;

public class DBService extends DBBaseService {

    /** 如果存在数据会更新object的数据，要慎用*/
    protected static long insertData(String tblName, String idKey, String idValue, Object object) {
        
        if (queryData(tblName, idKey, idValue, object) != null) {
            
            return updateData(tblName, idKey, idValue, object);
        }

        return insert(tblName, object);
    }

    /** 通过cid查找 */
    protected static Object queryData(String tblName, String idKey, String idValue, Object object) {
        Cursor cursor = null;
        try {
            String sql = "select * from " + tblName + " where " + idKey + " = '" + idValue + "'";
            cursor = mDb.rawQuery(sql, null);
            
            return queryObject(cursor, object);
        } catch (Exception e) {
            
            if (LogUtil.DEBUG) e.printStackTrace();
        } finally {
            
            if (cursor != null) cursor.close();
        }
        
        return null;
    }

    /**
     * update
     * 
     * @param tblName
     * @param object
     * @return
     */
    protected static long updateData(String tblName, String idKey, String idValue, Object object) {
        ContentValues values = null;
        try {
            values = getContentValues(object);

            return mDb.update(tblName, values, idKey + "= '" + idValue + "'", null);
        } catch (Exception e) {
            
            if (LogUtil.DEBUG) e.printStackTrace();
        } finally {
            
            if (values != null) values.clear();
        }
        
        return -1;
    }

    protected static int delete(String tblName) {
        try {
            
            return mDb.delete(tblName, null, null);
        } catch (Exception e) {
            
            if (LogUtil.DEBUG) e.printStackTrace();
        }
        
        return -1;
    }
}
