package com.monadpad.omgdrums;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HeadBob {

    private View mView;

    private ViewGroup.MarginLayoutParams layoutParams;

    private ObjectAnimator anim;

    private int bobHeight;

    public HeadBob(ImageView view) {

        mView = view;

        bobHeight = mView.getHeight() / 4;
        layoutParams = (ViewGroup.MarginLayoutParams)mView.getLayoutParams();

        anim = ObjectAnimator.ofFloat(this, "headHeight", 0.0f, 1.0f);
        anim.setRepeatCount(-1);

    }



    public void start(int beatMS) {
        Log.d("MGH", "headbob starting");

        anim.setDuration(beatMS);
        anim.start();

    }

    public void setHeadHeight(float f) {
        float f2;
        if (f < 0.2f) {
            f2 = 0;
        }
        else if (f < 0.7f) {
            f2= - bobHeight * ((f - 0.2f) / 0.5f) ;
        }
        else {
            f2 = bobHeight * ((f - 0.7f)/ 0.3f) - bobHeight;
        }

        layoutParams.topMargin = (int)(f2);

        mView.setLayoutParams(layoutParams);

    }

}
