package ucweb.web.model.db;

import java.lang.reflect.Field;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ucweb.util.LogUtil;

/** 
 * DB基类
 * @author zhichuan.huang
 */
public class DBBaseService {

    protected static SQLiteDatabase mDb = null;
    
    public static void init(SQLiteDatabase SQLDb) {
        mDb = SQLDb;
    }
    
    protected static boolean isOpen(Context c) {
        
        if (mDb != null && mDb.isOpen()) return true;
        
        VideoSqliteData dbService = VideoSqliteData.getInstance();
        dbService.init(c);
        
        return dbService.openDB();
    }
    
    protected static long insert(String table, Object object) {
        ContentValues values = null;
        try {
            values = getContentValues(object);
            
            return mDb.insert(table, null, values);
        } catch (Exception e) {
            
            if (LogUtil.DEBUG) e.printStackTrace();
        } finally {
            
            if (values != null) values.clear();
        }
        
        return -1;
    }
    
    protected static ContentValues getContentValues(Object obj) throws Exception {
        ContentValues initialValues = new ContentValues();
        Class<?> cls = obj.getClass();
        Field[] f_arr = cls.getDeclaredFields();
        
        for(int i=0; i<f_arr.length; i++) {
            Field f = f_arr[i];
            f.setAccessible(true);

            Class<?> cl = f.getType();

            String key = f.getName();
            Object vObj = f.get(obj);

            if (cl.equals(Integer.class) || cl.equals(int.class)) {
                Integer value = (Integer) vObj;
                initialValues.put(key, value);
            } else if (cl.equals(String.class)) {
                String value = (String) vObj;
                initialValues.put(key, value);
            } else if (cl.equals(Float.class) || cl.equals(float.class)) {
                Float value = (Float) vObj;
                initialValues.put(key, value);
            } else if (cl.equals(Double.class) || cl.equals(double.class)) {
                Double value = (Double) vObj;
                initialValues.put(key, value);
            } else if (cl.equals(Long.class) || cl.equals(long.class)) {
                Long value = (Long) vObj;
                initialValues.put(key, value);
            } else if (cl.equals(Short.class) || cl.equals(short.class)) {
                Short value = (Short) vObj;
                initialValues.put(key, value);
            } else if (cl.equals(Byte.class) || cl.equals(byte.class)) {
                Byte value = (Byte) vObj;
                initialValues.put(key, value);
            }
        }

        return initialValues;
    }
    
    /**
     * 根据Cursor查找对应的对象
     * 
     * @param cursor
     * @param object
     * @return
     * @throws Exception
     */
    protected static Object queryObject(Cursor cursor, Object object) throws Exception {
        
        if (cursor != null && cursor.moveToFirst()) {
            
            return queryCursor(cursor, object);
        }
        
        return null;
    }
    
    /**
     * 根据Cursor查找对应的对象
     * 
     * @param cursor
     * @param object
     * @return
     * @throws Exception
     */
    protected static Object queryCursor(Cursor cursor, Object object) throws Exception {
        Class<?> cls = object.getClass();
        Field[] f_arr = cls.getDeclaredFields();
        
        for (int i = 0; i < f_arr.length; i++) {
            Field f = f_arr[i];
            f.setAccessible(true);

            String key = f.getName();

            Class<?> cl = f.getType();

            if (cl.equals(Integer.class) || cl.equals(int.class)) {
                Integer value = cursor.getInt(cursor.getColumnIndexOrThrow(key));
                f.set(object, value);
            } else if (cl.equals(String.class)) {
                String value = cursor.getString(cursor.getColumnIndexOrThrow(key));
                f.set(object, value);
            } else if (cl.equals(Float.class) || cl.equals(float.class)) {
                Float value = cursor.getFloat(cursor.getColumnIndexOrThrow(key));
                f.set(object, value);
            } else if (cl.equals(Double.class) || cl.equals(double.class)) {
                Double value = cursor.getDouble(cursor.getColumnIndexOrThrow(key));
                f.set(object, value);
            } else if (cl.equals(Long.class) || cl.equals(long.class)) {
                Long value = cursor.getLong(cursor.getColumnIndexOrThrow(key));
                f.set(object, value);
            } else if (cl.equals(Short.class) || cl.equals(short.class)) {
                Short value = cursor.getShort(cursor.getColumnIndexOrThrow(key));
                f.set(object, value);
            } else if (cl.equals(Byte.class) || cl.equals(byte.class)) {
                try{
                    String value = cursor.getString(cursor.getColumnIndexOrThrow(key));
                    f.set(object, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        return object;
    }
}
