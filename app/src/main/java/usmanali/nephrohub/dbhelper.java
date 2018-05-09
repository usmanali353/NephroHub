package usmanali.nephrohub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HelloWorldSolution on 5/6/2018.
 */

public class dbhelper extends SQLiteOpenHelper {
    static final String Database_NAME="Reports";
    static final String SCANNED_REPORTS_TABLE="Scanned_reports";
    static final String IMAGE_REPORTS_TABLE="Image_reports";
    public dbhelper(Context context) {
        super(context,Database_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL("CREATE TABLE "+SCANNED_REPORTS_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,scanned_reports_obj TEXT )");
        db.execSQL("CREATE TABLE "+IMAGE_REPORTS_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,image_reports_obj TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS "+SCANNED_REPORTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+IMAGE_REPORTS_TABLE);
        onCreate(db);
    }
    public long insert_scanned_reports(String scanned_reports_obj){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("scanned_reports_obj",scanned_reports_obj);
        long l= db.insert(SCANNED_REPORTS_TABLE,null,cv);
        return l;
    }
    public long insert_image_reports(String image_reports_obj){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("image_reports_obj",image_reports_obj);
      long l= db.insert(IMAGE_REPORTS_TABLE,null,cv);
        return l;
    }
    public Cursor read_scanned_reports(){
        SQLiteDatabase db=this.getReadableDatabase();
     Cursor c= db.rawQuery("SELECT * FROM "+SCANNED_REPORTS_TABLE,null);
        return c;
    }
    public Cursor read_image_reports(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c= db.rawQuery("SELECT * FROM "+IMAGE_REPORTS_TABLE,null);
        return c;
    }
}
