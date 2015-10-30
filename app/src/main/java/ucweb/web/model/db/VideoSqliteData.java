package ucweb.web.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ucweb.util.LogUtil;

/**
 * 数据库业务实现类
 */
public class VideoSqliteData {
    
    private static final VideoSqliteData INSTANCE = new VideoSqliteData();
    private VideoSqliteData() {}
    public static VideoSqliteData getInstance() {
        return INSTANCE;
    }

    private Context mContext;

    private static final String DATABASE_NAME = "play_record.video.model.db";

    private static final int DATABASE_VERSION = 1;

    private DataBaseHelper mDBHelper;

    private SQLiteDatabase mDB;
    
    public void init( Context c ) {
        this.mContext = c;
    }
    
    private static class DataBaseHelper extends SQLiteOpenHelper {
        
        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(PlayRecordDBService.TBL_CREATE);
            db.execSQL(TvSeriesDBService.TBL_CREATE);
            db.execSQL(VideoDownRecordDBService.TBL_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //
        }
    }
    
    public boolean openDB() {
        try {
            mDBHelper = new DataBaseHelper(mContext);
            mDB = mDBHelper.getWritableDatabase();
            
            initDBService();
            
            return true;
        } catch (Exception e) {
            if ( LogUtil.ERROR ) e.printStackTrace();
        }
        
        return false;
    }
    
    public void initDBService() {
        DBBaseService.init(mDB);
    }
    
    public boolean isOpen() {
        
        if (mDB != null && mDB.isOpen()) return true;
        
        return false;
    }
    
    public void close() {
        try {
            
            if (mDB != null && mDB.isOpen()) mDB.close();
            
        } catch (Exception e) {
            if ( LogUtil.ERROR ) e.printStackTrace();
        }
        
        mDB = null;
        
        try {
            
            if (mDBHelper != null) mDBHelper.close();
            
        } catch (Exception e) {
            if ( LogUtil.ERROR ) e.printStackTrace();
        }
        
        mDBHelper = null;
    }
    
}
