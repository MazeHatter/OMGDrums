package com.monadpad.omgdrums;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * User: m
 * Date: 12/4/13
 * Time: 6:39 PM
 */
public class SavedDataOpenHelper extends SQLiteOpenHelper {

    SavedDataOpenHelper(Context context) {
        super(context, "OMG_BANANAS", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE saves (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tags TEXT, data TEXT, time INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        if (oldVersion == 1) {
            db.execSQL("CREATE TABLE saves (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tags TEXT, data TEXT, time INTEGER)");

/*            Cursor cursor = db.rawQuery("SELECT * FROM bananas", null);
            ContentValues data;

            int tagsColumn = cursor.getColumnIndex("TAGS");
            int dataColumn = cursor.getColumnIndex("DATA");


            while (cursor.moveToNext()) {
                data = new ContentValues();
                data.put("tags", cursor.getString(tagsColumn));
                data.put("data", cursor.getString(dataColumn));
                data.put("time", System.currentTimeMillis()/1000);
                db.insert("saves", null, data);

            }
            cursor.close();
*/

            db.execSQL("DROP TABLE bananas");

        }


    }

    public Cursor getSavedCursor() {
        SQLiteDatabase db = getWritableDatabase();

        return db.rawQuery("SELECT * FROM saves ORDER BY time DESC", null);

    }

    public String getLastSaved() {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM saves ORDER BY time DESC LIMIT 1", null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            ret = cursor.getString(cursor.getColumnIndex("data"));
        }
        cursor.close();
        return ret;
    }

}
