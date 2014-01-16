package com.monadpad.omgdrums;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SavedDataAdapter extends SimpleCursorAdapter {
    private final Context context;
    private final Cursor mCursor;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");

    public SavedDataAdapter(Context context, int layout, Cursor c, String[] from, int[] to){
        super(context, layout, c, from, to);
        this.context = context;
        this.mCursor = c;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        final View rowView;
        final ViewHolder holder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.saved_row, parent, false);
            holder = new ViewHolder();

            holder.tags = (TextView)rowView.findViewById(R.id.saved_data_tags);
            holder.date = (TextView)rowView.findViewById(R.id.saved_data_date);
            rowView.setTag(holder);

       }
        else {
            rowView = convertView;
            holder = (ViewHolder)convertView.getTag();
        }

        mCursor.moveToPosition(position);
        String tags = mCursor.getString(mCursor.getColumnIndex("tags"));
        if (tags.length() == 0) {
            tags = "(no tags)";
        }
        holder.tags.setText(tags);

        Date date = new Date();
        date.setTime(mCursor.getLong(mCursor.getColumnIndex("time")) * 1000);
        holder.date.setText(dateFormat.format(date));

        //String date = mCursor.getString(mCursor.getColumnIndex("artist"));
        //holder.date.setText(date);
        final String json = mCursor.getString(mCursor.getColumnIndex("data"));

        return rowView;
    }

    static class ViewHolder {
        TextView tags;
        TextView date;
    }

}

