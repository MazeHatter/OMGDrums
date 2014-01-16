package com.monadpad.omgdrums;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

    private MonadJam mJam;

    private DrumMachineView mDrumMachine;

    private int step = 0;

    private boolean isSkipping = false;

    private HeadBob headbob;

    public Libeniz(Main mainActivity, MonadJam jam) {
        mActivity = mainActivity;
        mStatusText = (TextView)mainActivity.findViewById(R.id.auto_status);

        mJam = jam;

        mDrumMachine = (DrumMachineView)mainActivity.findViewById(R.id.drum_machine);

        mJam.makeChannels();

    }

    public void letsMakeASong() {

        say(1200, mActivity.getString(R.string.hi_make_music), true,
                new Runnable() {
                    @Override
                    public void run() {
                        step = 1;

                        if (mJam.isPoolLoaded()) {
                            step2();
                        } else {
                            say(3000, "Actually, I'm still loading sounds.", false, null);
                        }
                    }

                });
    }


    public void step2() {

        if (isSkipping) {
            return;
        }

        say(2000, "Let's start with a kick drum.", true, new Runnable() {
            @Override
            public void run() {

            if (isSkipping) {
                return;
            }

            say(3000, "On beats 1, 3, 5, and 7.", false, new Runnable() {
                @Override
                public void run() {
                    step2b();
                }
            });

            step = 2;

            mJam.makeKickBeats(true);
            mDrumMachine.handleFirstColumn(0);
            mJam.kickIt();

            headbob = new HeadBob((ImageView)mActivity.findViewById(R.id.libeniz_head));
            headbob.start(500);

            }
        });
    }

    public void step2b() {

        if (isSkipping)
            return;


        say(3400, "Next the hand claps.", false, new Runnable() {
                @Override
                public void run() {
                    step3();
                }
            });
    }



    public void step3() {


        if (isSkipping) {
            return;
        }

        step = 3;

        mJam.makeClapBeats(true);
        mDrumMachine.handleFirstColumn(1);

        say(7000, "On beats 2, 4, and 6", true, new Runnable() {
            @Override
            public void run() {
                step4();
            }
        });


    }


    public void step4() {

        if (isSkipping) {
            return;
        }

        step = 4;
        mDrumMachine.handleFirstColumn(2);
        mJam.makeHiHatBeats(true);

        say(8000, "Here is the hi-hat", false, new Runnable() {
            @Override
            public void run() {
                step5();
            }
        });
    }

    private void step5() {
        if (isSkipping)
            return;

        step = 5;
        mDrumMachine.handleFirstColumn(3);
        mJam.makeHiHat2Beats(true);

        say(8000, "Here is the open hi-hat", false, new Runnable() {
            @Override
            public void run() {
                step6();
            }
        });

    }



    private void step5b() {
        if (isSkipping) {
            return;
        }
        say(4000, "Press me to change it up", false, new Runnable() {
            @Override
            public void run() {
                step6();
            }
        });

    }

    private void step6() {
        if (isSkipping) {
            return;
        }
        step = 6;
        showBanana();
        say(4000, "Press the Banana to Save", false, new Runnable() {
            @Override
            public void run() {
                step7();
            }
        });

    }

    private void step7() {
        if (isSkipping) {
            return;
        }
        say(4000, "Now its your turn!", false, new Runnable() {
            @Override
            public void run() {
                mJam.finishDemo();
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
                        //Log.d("MGH setting alph", Integer.toString((int)(pct * 255)));

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
                                after.run();
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



    public void skip() {
        isSkipping = true;

        if (step < 2) {
            mJam.makeKickBeats(true);
            mJam.kickIt();
            headbob = new HeadBob((ImageView)mActivity.findViewById(R.id.libeniz_head));
            headbob.start(500);

        }

        if (step < 3) {
            mJam.makeClapBeats(true);
        }

        if (step < 4) {
            //mDrumMachine.handleFirstColumn(2);
            mJam.makeHiHatBeats(true);
        }

        if (step < 5) {
            mDrumMachine.handleFirstColumn(3);
            mJam.makeHiHat2Beats(true);
        }

        if (step < 6) {
            showBanana();
        }

        mJam.finishDemo();

    }

    void showBanana() {

        scrollUpBottomPanel();

        final ImageView banana = (ImageView) mActivity.findViewById(R.id.main_banana);
        Animation turnin = AnimationUtils.loadAnimation(mActivity, R.anim.rotate);
        banana.startAnimation(turnin);


        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.fadePanel(banana, true);
                mActivity.fadePanel(mActivity.findViewById(R.id.skip), false);
            }
        });

    }

    public void newHeadBobTempo() {

        if (headbob != null)
            headbob.start(mJam.getBeatLength());

    }

    private void scrollUpBottomPanel() {

        mBottomPanel = mActivity.findViewById(R.id.bottom_panel);
        currentHeight = mDrumMachine.getHeight();
        finalHeight = currentHeight - mBottomPanel.getHeight() - 8;
        diff = currentHeight - finalHeight;

        Log.d("MGH drumheight", Integer.toString(mDrumMachine.getHeight()));

        if (mDrumMachine.getHeight() == 0) {
            mDrumMachine.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {

                    currentHeight = mDrumMachine.getHeight();
                    finalHeight = currentHeight - mBottomPanel.getHeight() - 8;

                    ViewGroup.LayoutParams params = mDrumMachine.getLayoutParams();
                    params.height = finalHeight;
                    mDrumMachine.setLayoutParams(params);

                    mDrumMachine.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
            return;
        }


        if (Build.VERSION.SDK_INT < 11) {
            scrollUpBottomPanel2();
            return;
        }


        ObjectAnimator anim = ObjectAnimator.ofFloat(Libeniz.this,
                "drumMachineHeight", 1, 0);
        anim.setDuration(300);
        anim.start();


    }

    private View mBottomPanel;
    private int currentHeight;
    private int finalHeight;
    private int diff;


    public void setDrumMachineHeight(float pct) {
        final ViewGroup.LayoutParams params = mDrumMachine.getLayoutParams();
        params.height = (int)(finalHeight + pct * (float)diff);
        mDrumMachine.setLayoutParams(params);
        //mDrumMachine.invalidate();
    }

    private void scrollUpBottomPanel2() {


        final View bottomPanel = mActivity.findViewById(R.id.bottom_panel);
        final ViewGroup.LayoutParams params = mDrumMachine.getLayoutParams();
        final int currentHeight = mDrumMachine.getHeight();
        final int finalHeight = currentHeight - bottomPanel.getHeight() - 8;
        final int diff = currentHeight - finalHeight;

        final long started = System.currentTimeMillis();


        (new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    float pct = (System.currentTimeMillis() - started) / 300.0f;
                    pct = Math.min(1.0f, pct);
                    params.height = (int)(finalHeight + (1.0f - pct) * (float)diff);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDrumMachine.setLayoutParams(params);
                            //mDrumMachine.invalidate();
                        }
                    });


/*                    try {
                        Thread.sleep(1000/30);
                    } catch (InterruptedException e) {
                        break;
                    }
 */
                    if (pct == 1.0f)
                        break;
                }

            }
        })).start();


    }

    public void finish() {
        if (headbob != null)
            headbob.finish();
    }
}
