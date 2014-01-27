package com.monadpad.omgdrums;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * User: m
 * Date: 11/20/13
 * Time: 11:51 PM
 */
public class OMGHelper {

    private static String mSubmitUrl = "omg";
    //private static String mHomeUrl = "http://www.openmusicgallery.com";
    //private static String mHomeUrl = "http://10.0.2.2:8888/";
    private static String mHomeUrl = "http://openmusicgallery.appspot.com/";

    private Context mContext;

    private Type mType;
    private String mData;

    public enum Type {
        DRUMBEAT
    }

    public OMGHelper(Context context, Type type, String data) {
        mContext =  context;
        mType = type;
        mData = data;

    }

    public void submitWithTags(String tags, boolean upload) {

        ContentValues data = new ContentValues();
        data.put("tags", tags);
        data.put("data", mData);
        data.put("time", System.currentTimeMillis()/1000);

        SQLiteDatabase db = new SavedDataOpenHelper(mContext).getWritableDatabase();
        db.insert("saves", null, data);
        db.close();

        if (upload)
            new SaveToOMG().execute(mHomeUrl + mSubmitUrl, mType.toString(), tags, mData);

    }

}
