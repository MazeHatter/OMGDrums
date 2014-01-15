package com.monadpad.omgdrums;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * User: m
 * Date: 1/13/14
 * Time: 1:21 AM
 */
public class GetSketchaTuneActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.getsketchatune);

        findViewById(R.id.googleplay_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent stopDrums = new Intent("com.androidinstrument.drum.STOPPLAYBACK");
                sendBroadcast(stopDrums);


                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id=com.monadpad.sketchatune2"));
                startActivity(intent);
            }
        });

        findViewById(R.id.amazon_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stopDrums = new Intent("com.androidinstrument.drum.STOPPLAYBACK");
                sendBroadcast(stopDrums);


                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("amzn://apps/android?p=com.monadpad.sketchatune2"));
                    startActivity(intent);

                }
                catch (Exception e) {
                    Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://amzn.com/B00CM1A51Y"));
                    startActivity(browser);
                }

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        if (!isFinishing())
            finish();
    }

}
