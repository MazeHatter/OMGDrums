package com.monadpad.omgdrums;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

public class Main extends Activity {

    MonadJam mJam;

    DrumMachineView drumMachine;


//    private Libeniz libeniz;

    private final static int DIALOG_TAGS = 11;
    private final static int DIALOG_TEMPO = 22;
    private final static int DIALOG_KITS = 33;

    private OMGHelper omgHelper;

    private Button drumMuteButton;


    private boolean mainBananaClicked = false;

    private ImageView mainLibenizHead;

    private boolean mIsVisible = false;

    private boolean turnedHeadBobOff = false;

    private HeadBob headbob;

    private boolean loaded = false;

    private boolean modified = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mJam = new MonadJam(this);

        setContentView(R.layout.main);

        drumMachine = (DrumMachineView)findViewById(R.id.drum_machine);
        drumMachine.setJam(mJam);

        //      libeniz = new Libeniz(this, mJam);


        loadFirstJam();

        Intent intent = getIntent();
        if (intent.hasExtra("bpm")) {

            mJam.setBPM(intent.getFloatExtra("bpm", 120.0f));

            if (intent.hasExtra("started")) {
                mJam.setStarted(intent.getLongExtra("started", System.currentTimeMillis()));
            }

    //        libeniz.skip(false);

        }
        else {
    //        libeniz.letsMakeASong();
        }

        mJam.makeChannels();
        drumMachine.handleFirstColumn(0);


        mainLibenizHead = (ImageView)findViewById(R.id.libeniz_head);
        mainLibenizHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mJam.monkeyWithEverything();

                mJam.rewind();

                updateUI(MonadJam.EVERY_RULE_CHANGED);

            }
        });

        headbob = new HeadBob(mainLibenizHead);
        headbob.start(500);


        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    //            libeniz.skip(true);
                fadePanel(view, false);
            }
        });

        findViewById(R.id.bpm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_TEMPO);
            }
        });


        updateTempo();

        setupMainBanana();


        //drumControls = findViewById(R.id.drums);

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
                    mJam.unmute();
                    drumMuteButton.setBackgroundColor(Color.GREEN);

                }
                mJam.rewind();
            }
        });

        findViewById(R.id.kit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                PopupMenu popup = new PopupMenu(Main.this, findViewById(R.id.kit_button));
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
            */
                showDialog(DIALOG_KITS);
            }
        });

        View sketchatuneButton = findViewById(R.id.sketchatune);
        sketchatuneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sketchatuneIntent = getPackageManager().
                        getLaunchIntentForPackage("com.monadpad.sketchatune2");
                if (sketchatuneIntent != null) {
                    sketchatuneIntent.putExtra("bpm", mJam.getBPM());
                    sketchatuneIntent.putExtra("started", mJam.getStarted());
                    sketchatuneIntent.putExtra("caller", "com.monadpad.omgdrums");

                    startActivity(sketchatuneIntent);
                } else {
                    startActivity(new Intent(Main.this, GetSketchaTuneActivity.class));
                }

            }
        });

        View drawmusicButton = findViewById(R.id.drawmusic);
        drawmusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sketchatuneIntent = getPackageManager().
                        getLaunchIntentForPackage("com.monadpad.le");
                if (sketchatuneIntent != null) {
                    sketchatuneIntent.putExtra("bpm", mJam.getBPM());
                    sketchatuneIntent.putExtra("started", mJam.getStarted());
                    sketchatuneIntent.putExtra("caller", "com.monadpad.omgdrums");

                    startActivity(sketchatuneIntent);
                }
                else {
                    startActivity(new Intent(Main.this, GetDrawMusicActivity.class));
                }

            }
        });

        View bitarButton = findViewById(R.id.bitar);
        bitarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sketchatuneIntent = getPackageManager().
                        getLaunchIntentForPackage("com.monadpad.ax");
                if (sketchatuneIntent != null) {
                    sketchatuneIntent.putExtra("duration", mJam.getDuration());
                    sketchatuneIntent.putExtra("started", mJam.getStarted());
                    sketchatuneIntent.putExtra("caller", "com.monadpad.omgdrums");

                    startActivity(sketchatuneIntent);
                }
                else {
                    startActivity(new Intent(Main.this, GetDrawMusicActivity.class));
                }

            }
        });


        findViewById(R.id.saved_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main.this, SavedListActivity.class));
            }
        });


        Intent sketchatune = getPackageManager().
                getLaunchIntentForPackage("com.monadpad.sketchatune2");
        Intent drawmusic = getPackageManager().
                getLaunchIntentForPackage("com.monadpad.le");

        //experimenting
        String installer = getPackageManager().getInstallerPackageName(getPackageName());
        boolean installedFromAmazon = "com.amazon.venezia".equals(installer);
        boolean hideSketchatune = sketchatune == null && !installedFromAmazon;

        if (hideSketchatune) {
            ViewGroup.LayoutParams params = sketchatuneButton.getLayoutParams();
            params.width = 0;
            sketchatuneButton.setLayoutParams(params);
        }
        if (drawmusic == null && !hideSketchatune) {
            ViewGroup.LayoutParams params = drawmusicButton.getLayoutParams();
            params.width = 0;
            drawmusicButton.setLayoutParams(params);
        }

        Intent bitarIntent = getPackageManager().
                getLaunchIntentForPackage("com.monadpad.ax");
        if (bitarIntent == null) {
            ViewGroup.LayoutParams params = bitarButton.getLayoutParams();
            params.width = 0;
            bitarButton.setLayoutParams(params);
        }



        IntentFilter filter = new IntentFilter();
        filter.addAction("com.androidinstrument.drum.SETBPMEXTERNAL");
        filter.addAction("com.androidinstrument.drum.STARTPLAYBACK");
        filter.addAction("com.androidinstrument.drum.STOPPLAYBACK");
        registerReceiver(androidInstrumentBroadCastReceiver, filter);

        if (Build.VERSION.SDK_INT < 11)
            findViewById(R.id.auto_status).getBackground().setAlpha(0);

    }

    @Override
    public void onNewIntent(Intent intent) {

        if (intent.hasExtra("beatData")) {
            if (mJam.loadData(intent.getStringExtra("beatData"))) {
                updateTempo();
            }
            else {
                Toast.makeText(this,
                        "Something went wrong loading beat data.", Toast.LENGTH_LONG).show();
            }


        }

    }


    @Override
    public void onPause() {
        super.onPause();

        mIsVisible = false;

        if (mJam.isDrumsMuted())
            mJam.mute(); // this provides user interaction
                        // otherwise it'll shut off


    //    libeniz.finish();

        if (headbob != null)
            headbob.finish();

        turnedHeadBobOff = true;

        if (isFinishing()) {
            mJam.finish();
        }

    }

    void fadePanel(final View v, final boolean turnOn) {

        if (turnOn)
            v.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT < 11) {
            if (!turnOn)
                v.setVisibility(View.GONE);
            return;
        }

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
        if (drumMachine != null) {
            drumMachine.postInvalidate();
            return;
        }
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

                    omgHelper.submitWithTags(tags, modified);
                    modified = false;
                }
            });
            return dl;
        }
        else if (dialog == DIALOG_TEMPO) {

            return tempoDialog();
        }
        else if (dialog == DIALOG_KITS) {
            return kitsDialog();
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

                    mainBanana.setImageDrawable(getResources().getDrawable(R.drawable.omg128));

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
                                        omgHelper.submitWithTags("", modified);
                                        modified = false;

                                    }
                                });
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
        newHeadBobTempo();
    //    libeniz.newHeadBobTempo();
    }


    private void showBanana(ImageView view) {
        view.setImageDrawable(getResources().getDrawable(R.drawable.omg128));

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

                try {
                    mJam.setSubbeatLength((int)(60000.0f / Integer.parseInt(txt.getText().toString()) / 4));
                    removeDialog(DIALOG_TEMPO);
                    updateUI(0);
                }
                catch (NumberFormatException e) {
                    Toast.makeText(Main.this, "Not a valid number", Toast.LENGTH_LONG).show();
                }

            }
        });
        return dl;


    }

    private Dialog kitsDialog() {

        final Dialog dl = new Dialog(this);
        dl.setTitle(getString(R.string.kits_dialog_title));
        dl.setContentView(R.layout.kits);

        dl.findViewById(R.id.hip_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mJam.setDrumset(0);
                drumMachine.setCaptions();
                removeDialog(DIALOG_KITS);

            }
        });
        dl.findViewById(R.id.rock_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mJam.setDrumset(1);
                drumMachine.setCaptions();
                removeDialog(DIALOG_KITS);

            }
        });

        dl.findViewById(R.id.cancel_button).setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                removeDialog(DIALOG_KITS);
            }
        });

        return dl;


    }

    private BroadcastReceiver androidInstrumentBroadCastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String s = intent.getAction();

            if (s.contains("SETBPMEXTERNAL")) {
                mJam.setBPM(intent.getFloatExtra("bpmval", 120));
                updateTempo();

            }

            else if (s.contains("STARTPLAYBACK")) {
                mJam.unmute();
                drumMuteButton.setBackgroundColor(Color.GREEN );

            }

            else if (s.contains("STOPPLAYBACK")) {
                mJam.mute();
                drumMuteButton.setBackgroundColor(Color.RED );


            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        //try {
            unregisterReceiver(androidInstrumentBroadCastReceiver);
        //} catch (Exception e) {};
    }

    @Override
    public void onResume() {
        super.onResume();

        mIsVisible = true;

        if (mJam.isDrumsMuted()) {
            mJam.mute(); // this provides user interaction
                         // otherwise it'll shut off
            mJam.resume();
        }

        if (turnedHeadBobOff) {
            updateTempo();
            turnedHeadBobOff = false;
        }

        if (!loaded) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        mJam.kickIt();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new Libeniz(Main.this).showInstructions();
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            loaded = true;
        }
    }

    public boolean isVisible() {
        return mIsVisible;
    }

    public void skipDemo() {
    //    if (libeniz != null)
    //        libeniz.skip(false);
    }

    public void newHeadBobTempo() {

        if (headbob != null)
            headbob.start(mJam.getBeatLength());

    }

    public void loadFirstJam() {

        SavedDataOpenHelper dataHelper = new SavedDataOpenHelper(this);
        String lastJam = dataHelper.getLastSaved();

        if (lastJam.length() > 0) {
            mJam.loadData(lastJam);
            updateTempo();
            drumMachine.setCaptions();

            Toast.makeText(this, "Loaded last saved beats.", Toast.LENGTH_LONG).show();
        }
        else {
            mJam.loadDefaultJam();
        }

    }

    public void onModify() {

        modified = true;
    }
}
