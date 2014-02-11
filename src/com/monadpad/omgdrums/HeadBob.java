package com.monadpad.omgdrums;

import android.animation.ObjectAnimator;
import android.os.Build;
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

        bobHeight = 12; //mView.getHeight() / 4;
        layoutParams = (ViewGroup.MarginLayoutParams)mView.getLayoutParams();

        if (Build.VERSION.SDK_INT >= 11) {
            anim = ObjectAnimator.ofFloat(this, "headHeight", 0.0f, 1.0f);
        }

    }



    public void start(int beatMS) {

        if (anim != null) {
            anim.setRepeatCount(-1);
            anim.setDuration(beatMS);
            anim.start();
        }
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

    public void finish() {
        if (Build.VERSION.SDK_INT >= 11) {
            anim.setRepeatCount(0);
        }
    }
}
