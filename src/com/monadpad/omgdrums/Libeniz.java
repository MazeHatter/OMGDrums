package com.monadpad.omgdrums;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * User: m
 * Date: 11/18/13
 * Time: 12:41 PM
 */
public class Libeniz {

    private Main mActivity;
    private TextView mStatusText;

    private int step = 0;

    public Libeniz(Main mainActivity) {
        mActivity = mainActivity;
        mStatusText = (TextView)mainActivity.findViewById(R.id.auto_status);

    }


    public void showInstructions() {

        View head = (ImageView) mActivity.findViewById(R.id.libeniz_head);
        Animation turnin = AnimationUtils.loadAnimation(mActivity, R.anim.rotate);
        head.startAnimation(turnin);

        say(5000, "Press the drum to change beats", true, new Runnable() {
            @Override
            public void run() {

                final ImageView banana = (ImageView) mActivity.findViewById(R.id.main_banana);
                Animation turnin = AnimationUtils.loadAnimation(mActivity, R.anim.rotate);
                banana.startAnimation(turnin);
                say(5000, "Press OMG to save >", false, null);

            }
        });
    }


    public void say(final int length, final String text, final boolean delegateOnUIThread, final Runnable after) {

        if (Build.VERSION.SDK_INT < 11) {

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStatusText.setText(text);

                }
            });

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    long started = System.currentTimeMillis();
                    float pct;
                    while (true) {
                        pct = Math.min(1.0f, (System.currentTimeMillis() - started) / 300.0f);
                        mStatusText.getBackground().setAlpha((int)(pct * 255));
                        mStatusText.postInvalidate();

                        if (pct == 1.0f) {
                            break;
                        }
                    }

                    setTimeout(length, new Runnable() {
                        @Override
                        public void run() {
                            long started = System.currentTimeMillis();
                            float pct;
                            while (true) {
                                pct = 1.0f - Math.min(1.0f, (System.currentTimeMillis() - started) / 300.0f);
                                mStatusText.getBackground().setAlpha((int) (pct * 255));
                                mStatusText.postInvalidate();

                                if (pct == 0.0f) {
                                    break;
                                }
                            }
                            if (after != null) {
                                mActivity.runOnUiThread(after);
                            }

                        }
                    });
                }
            });
            thread.start();
            return;
        }


        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusText.setText(text);

                ObjectAnimator anim = ObjectAnimator.ofFloat(Libeniz.this,
                        "statusTextAlpha", 0, 1);
                anim.setDuration(300);

                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {}
                    @Override
                    public void onAnimationCancel(Animator animator) {}
                    @Override
                    public void onAnimationRepeat(Animator animator) {}

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        setTimeoutUIThread(length, new Runnable() {
                            @Override
                            public void run() {
                                ObjectAnimator anim = ObjectAnimator.ofFloat(Libeniz.this,
                                        "statusTextAlpha", 1, 0);
                                anim.setDuration(300);
                                anim.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {}

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        if (after != null) {
                                            after.run();
                                        }

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {}

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {}
                                });
                                anim.start();

                            }
                        });
                    }
                });

                anim.start();



            }
        });
    }

    public void setTimeoutUIThread(final int timeout, final Runnable delegate) {
        new Thread() {
            public void run() {
                try{
                    Thread.sleep(timeout);
                }
                catch (InterruptedException e) {}

                mActivity.runOnUiThread(delegate);

            }
        }.start();
    }

    public void setTimeout(final int timeout, final Runnable delegate) {
        new Thread() {
            public void run() {
                try{
                    Thread.sleep(timeout);
                }
                catch (InterruptedException e) {}

                delegate.run();

            }
        }.start();
    }


    public void setStatusTextAlpha(float param){

/*        View panel = findViewById(R.id.right_panel);
        ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) panel.getLayoutParams();
        margins.rightMargin = (int)(panel.getWidth() * param * -1);
        panel.setLayoutParams(margins);
 */
        mStatusText.setAlpha(param);


    }

}
