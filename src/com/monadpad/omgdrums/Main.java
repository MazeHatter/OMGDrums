package com.monadpad.omgdrums;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

public class Main extends Activity {

    MonadJam mJam;

    View drumControls;

    DrumMachineView drumMachine;

    private boolean drumMachineVisible = false;

    private Libeniz libeniz;

    private int collapsedPanelHeight = -1;
    private int drumPanelHeight = -1;


    private final static int DIALOG_TAGS = 11;
    private final static int DIALOG_TEMPO = 22;

    private OMGHelper omgHelper;

    private Button drumMuteButton;


    private boolean mainBananaClicked = false;
    private boolean drumBananaClicked = false;

    private ImageView mainLibenizHead;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mJam = new MonadJam(this);

        setContentView(R.layout.main);


        libeniz = new Libeniz(this, mJam);
        //libeniz.letsMakeASong();

        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                libeniz.skip();
                fadePanel(view, false);
            }
        });

        findViewById(R.id.bpm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_TEMPO);
            }
        });

        mainLibenizHead = (ImageView)findViewById(R.id.libeniz_head);
        mainLibenizHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mJam.monkeyWithEverything();
                updateUI(MonadJam.EVERY_RULE_CHANGED);

            }
        });

        updateTempo();

        setupMainBanana();

        drumMachine = (DrumMachineView)findViewById(R.id.drum_machine);
        drumMachine.setJam(mJam);

        drumControls = findViewById(R.id.drums);

        drumMuteButton = (Button)findViewById(R.id.mute_button);
        drumMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drumMuteButton.setBackgroundColor(mJam.toggleMuteDrums() ?
                        Color.GREEN : Color.RED);
            }
        });

        findViewById(R.id.rewind_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mJam.isDrumsMuted()) {
                    drumMuteButton.callOnClick();

                }
                mJam.rewind();
            }
        });

        findViewById(R.id.kit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(Main.this, findViewById(R.id.kit_button));
                popup.inflate(R.menu.kits);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int set = menuItem.getTitle().equals("Rock Kit") ? 1 : 0;
                        mJam.setDrumset(set);
                        drumMachine.setCaptions();
                        return true;
                    }
                });
            }
        });

    }



    @Override
    public void onStop() {
        super.onStop();
        mJam.finish();
    }

    void fadePanel(final View v, final boolean turnOn) {

        if (turnOn)
            v.setVisibility(View.VISIBLE);

        ObjectAnimator anim = ObjectAnimator.ofFloat(v,
                "alpha", turnOn ? 0 : 1, turnOn ? 1 : 0);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!turnOn) {
                    v.setVisibility(View.GONE);
                }
                else {
                    if (collapsedPanelHeight == -1)
                        collapsedPanelHeight = v.getHeight();

                    if (v == drumControls) {
                        drumPanelHeight = ((View)drumControls.getParent()).getHeight() - drumControls.getTop() -
                                drumControls.getPaddingBottom() - collapsedPanelHeight;
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        anim.setDuration(300);
        anim.start();


    }


    void updatePanel() {
        //if (drumMachineVisible) {
            drumMachine.postInvalidate();
            return;
        //}
    }




    protected Dialog onCreateDialog(int dialog){

        if (dialog == DIALOG_TAGS)  {

            final Dialog dl = new Dialog(this);
            dl.setTitle(getString(R.string.tags_dialog_title));
            dl.setContentView(R.layout.gettags);


            dl.findViewById(R.id.why_button).setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    removeDialog(DIALOG_TAGS);
                }
            });

            dl.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    String tags = ((EditText) dl.findViewById(R.id.txt_tags)).getText().toString();

                    removeDialog(DIALOG_TAGS);

                    omgHelper.submitWithTags(tags);
                }
            });
            return dl;
        }
        else if (dialog == DIALOG_TEMPO) {

            return tempoDialog();
        }
        return null;
    }



    public void updateOnUIThread(final int state) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUI(state);
            }
        });
    }

    private void updateUI(int state) {
        drumMuteButton.setBackgroundColor(mJam.isDrumsMuted() ? Color.RED: Color.GREEN);

        updateTempo();

        drumMachine.setCaptions();

        if (state == MonadJam.EVERY_RULE_CHANGED) {
            Animation turnin = AnimationUtils.loadAnimation(this, R.anim.rotate);
            turnin.setRepeatCount(0);
            mainLibenizHead.startAnimation(turnin);
        }
    }

    private void setupMainBanana() {

        final ImageView mainBanana = (ImageView)findViewById(R.id.main_banana);
        mainBanana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mainBananaClicked) {
                    mainBananaClicked = false;

                    mainBanana.setImageDrawable(getResources().getDrawable(R.drawable.banana48));

                    showDialog(11);

                }
                else {
                    mainBanana.setImageDrawable(getResources().getDrawable(R.drawable.add_tag_white));
                    mainBananaClicked = true;

                    mainBanana.clearAnimation();

                    mJam.holdMain();

                    omgHelper = new OMGHelper(Main.this, OMGHelper.Type.DRUMBEAT,
                            mJam.getData(OMGHelper.Type.DRUMBEAT));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                            }
                            if (mainBananaClicked) {

                                mainBananaClicked = false;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showBanana(mainBanana);
                                    }
                                });

                                omgHelper.submitWithTags("");

                            }

                        }
                    }).start();
                }
            }
        });

    }



    private void updateTempo() {
        //int beatMS = mJam.getBPM();
        //int bpm = 60000 / beatMS;
        ((TextView)findViewById(R.id.bpm_button)).setText(Integer.toString(mJam.getBPM()) + " bpm");
        libeniz.newHeadBobTempo();
    }


    public void refreshDrumHeight() {

        drumPanelHeight = ((View)drumControls.getParent()).getHeight() - drumControls.getTop() -
                drumControls.getPaddingBottom() - collapsedPanelHeight;
    }

    private void showBanana(ImageView view) {
        view.setImageDrawable(getResources().getDrawable(R.drawable.banana48));

        Animation turnin = AnimationUtils.loadAnimation(this, R.anim.rotate);
        view.startAnimation(turnin);

    }

    private long lastTap = 0;
    private Dialog tempoDialog() {

        final Dialog dl = new Dialog(this);
        dl.setTitle(getString(R.string.tempo_dialog_title));
        dl.setContentView(R.layout.tempo);

        final EditText txt = (EditText)dl.findViewById(R.id.txt_bpm);
        txt.setText(Integer.toString(mJam.getBPM()));

        Button tap = (Button)dl.findViewById(R.id.tap_button);
        tap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    long now = System.currentTimeMillis();
                    long timeSinceLastTap = (now - lastTap);
                    if (timeSinceLastTap < 1000) {
                        txt.setText(Long.toString(60000 / timeSinceLastTap));
                    }

                    lastTap = now;
                }

                return true;
            }
        });

        dl.findViewById(R.id.cancel_button).setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                removeDialog(DIALOG_TEMPO);
            }
        });

        dl.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mJam.setSubbeatLength((int)(60000.0f / Integer.parseInt(txt.getText().toString()) / 4));
                removeDialog(DIALOG_TEMPO);
                updateUI(0);

//                    String tags = ((EditText) dl.findViewById(R.id.txt_tags)).getText().toString();
//                    omgHelper.submitWithTags(tags);
            }
        });
        return dl;


    }

}
