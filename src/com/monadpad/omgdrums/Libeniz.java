package com.monadpad.omgdrums;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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

    private View mLibenizView;

    private MonadJam mJam;

    private View mMainLayOut;

    private View mSongControls;
    private View mDrumControls;

    private DrumMachineView mDrumMachine;

    private int topMargin;
    private int height;

    private int step = 0;

    private boolean isSkipping = false;

    private HeadBob headbob;

    public Libeniz(Main mainActivity, MonadJam jam) {
        mActivity = mainActivity;
        mStatusText = (TextView)mainActivity.findViewById(R.id.auto_status);
        mLibenizView = mainActivity.findViewById(R.id.status_line);
        mJam = jam;

        mMainLayOut = mainActivity.findViewById(R.id.main_layout);
        mSongControls = mainActivity.findViewById(R.id.song_controls);
        mDrumControls = mainActivity.findViewById(R.id.drums);
        mDrumMachine = (DrumMachineView)mainActivity.findViewById(R.id.drum_machine);


        letsMakeASong();

    }

    public void letsMakeASong() {

        say(1200, mActivity.getString(R.string.hi_make_music), true,
                new Runnable() {
                    @Override
                    public void run() {
                        step = 1;

                        if (mJam.isPoolLoaded()) {
                            step2();
                        }
                        else {
                            say(3000, "Actually, I'm still loading sounds.", false, null);
                        }
                    }

                });
        mJam.makeChannels();
    }


    public void step2() {

        if (isSkipping) {
            return;
        }

        say(2000, "We need a drum beat.", true, new Runnable() {
            @Override
            public void run() {

            if (isSkipping) {
                return;
            }

            say(3000, "This is a hi hat.", false, new Runnable() {
                @Override
                public void run() {
                    step2b();
                }
            });

            step = 2;

            mDrumMachine.handleFirstColumn(2);
            mJam.makeHiHatBeats(true);
            mJam.kickIt();

            headbob = new HeadBob((ImageView)mLibenizView.findViewById(R.id.libeniz_head));
            headbob.start(500);

            }
        });
    }

    public void step2b() {

        if (isSkipping)
            return;


        say(3400, "Press boxes to change the pattern.", false, new Runnable() {
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

        mJam.makeKickBeats(true);

        mDrumMachine.handleFirstColumn(0);

        say(7000, "Let's add a kick drum", true, new Runnable() {
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

        mJam.makeClapBeats(true);

        mDrumMachine.handleFirstColumn(1);
        say(8000, "Now add the claps.", false, new Runnable() {
            @Override
            public void run() {
                //step5();
            }
        });
    }



    public void say(final int length, final String text, final boolean delegateOnUIThread, final Runnable after) {

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

        if (step < 1) {
            mActivity.fadePanel(mSongControls, true);
            mActivity.fadePanel(mDrumControls, true);
        }

        if (step < 2) {
            mJam.makeHiHatBeats(true);
            mJam.kickIt();
            headbob = new HeadBob((ImageView)mLibenizView.findViewById(R.id.libeniz_head));
            headbob.start(500);

        }

        if (step < 3) {
            mJam.makeKickBeats(true);

        }

        if (step < 4) {
            mJam.makeClapBeats(true);
        }


        showBanana();
        mJam.finishDemo();

    }

    void showBanana() {
        ImageView banana = (ImageView) mActivity.findViewById(R.id.main_banana);
        Animation turnin = AnimationUtils.loadAnimation(mActivity, R.anim.rotate);
        banana.startAnimation(turnin);


        mActivity.fadePanel(banana, true);
        mActivity.fadePanel(mActivity.findViewById(R.id.skip), false);



    }

    public void newHeadBobTempo() {

        if (headbob != null)
            headbob.start(mJam.getBeatLength());

    }
}
