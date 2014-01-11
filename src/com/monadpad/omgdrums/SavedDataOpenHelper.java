package com.monadpad.omgdrums;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * User: m
 * Date: 12/4/13
 * Time: 6:39 PM
 */
public class SavedDataOpenHelper extends SQLiteOpenHelper {

    SavedDataOpenHelper(Context context) {
        super(context, "OMG_BANANAS", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bananas (TAGS TEXT, TYPE TEXT, DATA TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
