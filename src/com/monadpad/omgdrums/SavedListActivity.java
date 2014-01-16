package com.monadpad.omgdrums;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SavedListActivity extends ListActivity
{
    private Cursor cursor;
    private int page;
    private boolean noMoreToDownload = false;

    private TextView foot;
    private TextView head;

    private ArrayList<String> jsonArray = new ArrayList<String>();

    private int headerOffset = 0;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.saved_list_title));
        page = 1;


        cursor = new SavedDataOpenHelper(this).getSavedCursor();

        SimpleCursorAdapter curA = new SavedDataAdapter(SavedListActivity.this,
                R.layout.saved_row,
                cursor, new String[]{"tags", "time"},
                new int[]{R.id.saved_data_tags, R.id.saved_data_date});
        setListAdapter(curA);

/*        head = new TextView(this);
        final int savedGrooves = SdListManager.getSavedGrooveCount();
        if (savedGrooves > 0) {
            head.setText(getString(R.string.saved_count_on_sd));
            head.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            head.setPadding(20, 20, 20, 20);
            head.setGravity(0x11);
            getListView().addHeaderView(head, -7, true);

            headerOffset = 1;
        }

        foot = new TextView(this);
        foot.setText(R.string.loading_please_wait);
        foot.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        foot.setPadding(20, 20, 20, 20);
        foot.setGravity(0x11);
        getListView().addFooterView(foot, -7, true);
*/

    }


    public void onListItemClick(ListView l, View v, int position, long id){

/*        if (v == head) {
            startActivity(new Intent(this, SdListActivity.class));
            return;
        }

        if (v == foot){
            if (((TextView)v).getText().equals(getString(R.string.get_more)) && !noMoreToDownload){
                page++;
                new DownloadGallery().execute();
            }
            return;
        }
*/


        cursor.moveToPosition(position - headerOffset);
        Intent intent = new Intent(this, Main.class);
        String json = cursor.getString(cursor.getColumnIndex("data"));
        intent.putExtra("beatData", json);

        startActivity(intent);

    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            cursor.close();
        } catch (Exception e) {}

        if (!isFinishing())
            finish();
    }



}
