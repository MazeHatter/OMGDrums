package com.monadpad.omgdrums;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * User: m
 * Date: 11/12/13
 * Time: 10:07 PM
 */
public class MonadJam {

    private Random rand = new Random();

    private int subbeats = 4;
    private int beats = 8;
    private int subbeatLength = 125; //70 + rand.nextInt(125); // 125;

    private SoundPool pool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
    private boolean soundPoolLoaded = false;

    private Context mContext;
    private Main mActivity;

    PlaybackThread playbackThread;

    boolean cancel = false;

    private int looped = 0;

    private boolean drumsEnabled = true;

    private boolean playing = false;

    private long holdingMain = 0;
    private long holdingDrums = 0;

    private long holdTime = 60000; // 60 seconds

    private boolean[] default_kick = new boolean[] {
            true, false, false, false,
            false, false, false, false,
            true, false, false, false,
            false, false, false, false,
            true, false, false, false,
            false, false, false, false,
            true, false, false, false,
            false, false, false, false,
    };
    private boolean[] default_clap = new boolean[] {
            false, false, false, false,
            true, false, false, false,
            false, false, false, false,
            true, false, false, false,
            false, false, false, false,
            true, false, false, false,
            false, false, false, false,
            false, false, false, false,
    };
    private boolean[] default_hihat = new boolean[] {
            true, false, false, false,
            true, false, false, false,
            true, false, false, false,
            true, false, false, false,
            true, false, false, false,
            true, false, false, false,
            true, false, false, false,
            true, true, true, true,
    };
    private boolean[] default_hihat2 = new boolean[] {
            false, false, true,  false,
            false, false, true,  false,
            false, false, true,  false,
            false, false, true,  false,
            false, false, true,  false,
            false, false, true,  false,
            false, false, false, false,
            false, false, false, false,
    };

    private boolean[][] pattern = new boolean[8][subbeats * beats];
    private int[][] soundIds = new int[2][8];

    private boolean demo = true;

    private int drumset = 0;

    public final static int EVERY_RULE_CHANGED = 2;
    public final static int NORMAL = 0;


    private long lastHumanInteraction = 0l;

    private boolean shouldRewind = false;

    private String[] captions;

    private long started = 0;

    private long mutedTime = 0;

    private String[][] presetNames;

    public MonadJam(Context context) {

        mContext = context;
        mActivity = (Main)mContext;

        setCaptions();

    }

    public void makeChannels() {
        //basslineChannel = dialpad.makeBasslineChannel();

        if (Build.VERSION.SDK_INT >= 11) {
            pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                int loadedSounds = 0;
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    loadedSounds++;

                    if (loadedSounds == 70) {
                        soundPoolLoaded = true;
                    }
                }
            });
        }
        loadPool();


    }

    private void loadPool() {

        soundIds[0][0] = pool.load(mContext, R.raw.hh_kick, 1);
        soundIds[0][1] = pool.load(mContext, R.raw.hh_clap, 1);
        soundIds[0][2] = pool.load(mContext, R.raw.rock_hithat_closed, 1);
        soundIds[0][3] = pool.load(mContext, R.raw.hh_hihat, 1);
        soundIds[0][4] = pool.load(mContext, R.raw.hh_tamb, 1);
        soundIds[0][5] = pool.load(mContext, R.raw.hh_scratch, 1);

        soundIds[1][0] = pool.load(mContext, R.raw.rock_kick, 1);
        soundIds[1][1] = pool.load(mContext, R.raw.rock_snare, 1);
        soundIds[1][2] = soundIds[0][2];
        soundIds[1][3] = pool.load(mContext, R.raw.rock_hithat_med, 1);
        soundIds[1][4] = pool.load(mContext, R.raw.rock_hihat_open, 1);
        soundIds[1][5] = pool.load(mContext, R.raw.rock_crash, 1);

        presetNames = new String[][] {
                {"PRESET_HH_KICK", "PRESET_ROCK_KICK"},
                {"PRESET_HH_CLAP", "PRESET_ROCK_SNARE"},
                {"PRESET_ROCK_HIHAT_CLOSED", "PRESET_ROCK_HIHAT_CLOSED"},
                {"PRESET_HH_HIHAT", "PRESET_ROCK_HIHAT_MED"},
                {"PRESET_HH_TAMB", "PRESET_ROCK_HIHAT_OPEN"},
                {"PRESET_HH_SCRATCH", "PRESET_ROCK_CRASH"},
                {"", ""},
                {"", ""}
        };

    }


    private float drumVolume = 1.0f;
    public void playBeatSampler(int subbeat) {

        if (drumsEnabled) {
            for (int i = 0; i < pattern.length; i++) {
                if (pattern[i][subbeat]) {
                    pool.play(soundIds[drumset][i], drumVolume, drumVolume, 10, 0, 1);
                }
            }
        }

    }


    public void makeDrumBeats() {

        makeKickBeats(false);
        makeHiHatBeats(false);
        makeClapBeats(false);
    }

    public void kickIt() {

        playbackThread = new PlaybackThread();
        playbackThread.start();

        playing = true;

    }


    public boolean toggleMuteDrums() {
        drumsEnabled = !drumsEnabled;
        if (!drumsEnabled) {
            mutedTime = System.currentTimeMillis();
        }

        return drumsEnabled;
    }

    public void mute() {
        drumsEnabled = false;
        mutedTime = System.currentTimeMillis();
    }

    public void unmute() {
        drumsEnabled = true;
    }

    public void makeHiHatBeats(boolean defaultPattern) {

        boolean[] hihat = pattern[2];
        if (defaultPattern) {
            for (int i = 0; i < hihat.length; i++) {
                hihat[i] = default_hihat[i];
            }
            return;
        }

        int pattern = rand.nextInt(5);

//        hihat = new boolean[beats * subbeats];
        for (int i = 0; i < hihat.length; i++) {
            hihat[i] = pattern == 0 ?  i % subbeats == 0 :
                       pattern == 1 ? i % 2 == 0 :
                       pattern == 2 ? rand.nextBoolean() :
                        rand.nextBoolean() || rand.nextBoolean();
        }
    }

    public void makeHiHat2Beats(boolean defaultPattern) {

        boolean[] hihat = pattern[3];
        if (defaultPattern) {
            for (int i = 0; i < hihat.length; i++) {
                hihat[i] = default_hihat2[i];
            }
            return;
        }

        int pattern = rand.nextInt(5);

//        hihat = new boolean[beats * subbeats];
        for (int i = 0; i < hihat.length; i++) {
            hihat[i] = pattern == 0 ?  i % subbeats == 0 :
                    pattern == 1 ? i % 2 == 0 :
                            pattern == 2 ? rand.nextBoolean() :
                                    rand.nextBoolean() || rand.nextBoolean();
        }
    }


    public void makeKickBeats(boolean defaultPattern) {

        boolean[] kick = pattern[0];
        if (defaultPattern) {
            for (int i = 0; i < kick.length; i++) {
                kick[i] = default_kick[i];
            }
            return;
        }

        int pattern = rand.nextInt(5);

//        kick = new boolean[beats * subbeats];
        for (int i = 0; i < kick.length; i++) {
            kick[i] = pattern == 0 ? i % subbeats == 0 :
                      pattern == 1 ? i % 8 == 0 :
                    (i == 0 || i == 8 || i == 16 ||
                    (rand.nextBoolean() && rand.nextBoolean())); //rand.nextBoolean();
        }
    }
    public void makeClapBeats(boolean defaultPattern) {

        boolean[] clap = pattern[1];
        if (defaultPattern) {
            for (int i = 0; i < clap.length; i++) {
                clap[i] = default_clap[i];
            }
            return;
        }

        int pattern = rand.nextInt(10);

//        clap = new boolean[beats * subbeats];
        for (int i = 0; i < clap.length; i++) {
            clap[i] = pattern != 0 && (
                    pattern == 1 ? i == 4 || i == 12 || i == 20 || i == 28 :
                    pattern == 2 ? i == 4 || i == 12 || i == 13 || i == 20 :
                            i == 4 || i == 12 || i == 20);

        }

    }

    public int getCurrentSubbeat() {
        int i = playbackThread.i;
        if (i == 0) i = beats * subbeats;
        return i - 1;

    }

    class PlaybackThread extends Thread {

        int i;
        int lastI;

        public void run() {
            onNewLoop();

            long now;
            long nowInLoop;

            i = 0;

            if (started == 0) {
                started = System.currentTimeMillis();
            }
            else {
                now = System.currentTimeMillis();
                nowInLoop = now - started;
                i = (int)Math.ceil(nowInLoop / (double)subbeatLength);
                i = i % (beats * subbeats);
            }

            while (!cancel) {

                now = System.currentTimeMillis();

                if (shouldRewind) {
                    nowInLoop = 0;
                    i = 0;
                    shouldRewind = false;
                    started = now;
                }
                else {
                    nowInLoop = now - started;
                }

                if (nowInLoop < i * subbeatLength) {
                    continue;
                }

                playBeatSampler(i);

                lastI = i++;

                if (i == beats * subbeats) {
                    i = 0;
                    started += subbeatLength * subbeats * beats;
                    onNewLoop();
                }

                mActivity.updatePanel();

                if (!drumsEnabled && System.currentTimeMillis() - mutedTime > 30000
                        && !mActivity.isVisible()) {
                    cancel = true;
                }

            }

            started = 0;

        }



    }


    void onNewLoop() {

        int state = NORMAL;

        looped++;


    }

    public void finish() {
        cancel = true;
    }


    public void everyRuleChange() {
        long now = System.currentTimeMillis();

        boolean holding = false;

        if (holdingDrums < now) {
            drumset = rand.nextInt(2);
            setCaptions();
            makeDrumBeats();
        }
        else {
            holding = true;
        }


        if (!holding) {
            subbeatLength = 70 + rand.nextInt(125); // 125
        }


        if (playbackThread != null)
            playbackThread.i = 0;

        unmute();
    }


    public boolean[] getHiHat() {
        return pattern[2];
    }
    public boolean[] getKick() {
        return pattern[0];
    }
    public boolean[] getClap() {
        return pattern[1];
    }
    public boolean[] getTrack(int x)  {
        return pattern[x];
    }

    public int getBPM() {
        if (subbeatLength > 0)
            return 60000 / (subbeatLength * subbeats);
        else
            return 120;
    }

    public void setBPM(float bpm) {
        subbeatLength = (int)(60000 / bpm / subbeats);
    }

    public int getBeatLength() {
        return subbeatLength * subbeats;
    }

    public boolean isPlaying() {
        return playing;
    }

    public String getData(OMGHelper.Type type) {
        StringBuilder sb = new StringBuilder();

        if (type == OMGHelper.Type.DRUMBEAT) {
            getDrumBeatData(sb);
        }

        return sb.toString();
    }


    public void getDrumBeatData(StringBuilder sb) {
        int totalBeats = beats * subbeats;
        sb.append("{\"type\" : \"DRUMBEAT\", \"bpm\" : ");
        sb.append(getBPM());
        sb.append(", \"kit\": ");
        sb.append(drumset);
        sb.append(", \"data\": [");

        for (int p = 0; p < pattern.length; p++) {
            sb.append("{\"name\": \"");
            sb.append(captions[p]);
            sb.append("\", \"sound\": \"");
            sb.append(presetNames[p][drumset]);
            sb.append("\", \"data\": [");
            for (int i = 0; i < totalBeats; i++) {
                sb.append(pattern[p][i] ?1:0) ;
                if (i < totalBeats - 1)
                    sb.append(",");
            }
            sb.append("]}");

            if (p < pattern.length - 1)
                sb.append(",");

        }

        sb.append("]}");

    }





    public boolean isDrumsMuted() {
        return !drumsEnabled;
    }


    public boolean isPoolLoaded() {
        return true; //soundPoolLoaded;
    }

    public void holdMain() {

        holdingMain = Math.max(System.currentTimeMillis(), holdingMain) + holdTime;

    }

    public void holdDrums() {

        holdingMain = Math.max(System.currentTimeMillis(), holdingMain) + holdTime / 2;
        holdingDrums = Math.max(System.currentTimeMillis(), holdingDrums) + holdTime;

    }

    public void monkeyWithEverything() {
        holdingMain = 0;
        holdingDrums = 0;

        everyRuleChange();

    }

    public void monkeyWithDrums() {
        holdingDrums = 0;
        makeDrumBeats();
    }

    public void finishDemo() {
        demo = false;
    }

    public void setDrumset(int set) {
        drumset = set;
        setCaptions();
    }

    public int getDrumset() {
        return drumset;
    }

    public void rewind() {
        shouldRewind = true;
    }

    public void setSubbeatLength(int length) {
        subbeatLength = length;
    }

    private void setCaptions() {
        if (drumset == 0)
            captions = new String[] {"kick", "clap", "closed hi-hat", "open hi-hat",
                    "tambourine", "scratch", "", ""};
        else if (drumset == 1) {
            captions = new String[] {"kick", "snare", "closed hi-hat", "med hi-hat",
                    "open hi-hat", "crash", "", ""};
        }

    }

    public String[] getCaptions() {
        return captions;
    }

    public long getStarted() {
        return started;
    }

    public void setStarted(long start) {
        started = start;
    }

    public boolean loadData(String data) {

        boolean good = false;
        try {
            JSONObject jsonData = new JSONObject(data);

            if (jsonData.has("bpm")) {
                setBPM((float) jsonData.getDouble("bpm"));
            }

            if (jsonData.has("kit")) {
                drumset = jsonData.getInt("kit");
            }

            JSONArray channels = jsonData.getJSONArray("data");
            JSONObject channel;
            JSONArray channelData;
            for (int i = 0; i < Math.min(channels.length(), pattern.length); i++) {
                channel = channels.getJSONObject(i);
                channelData = channel.getJSONArray("data");

                for (int j = 0; j < Math.min(channelData.length(), pattern[i].length); j++) {
                    pattern[i][j] = channelData.getInt(j) == 1;
                }

            }
            good = true;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        setCaptions();

        return good;
    }

    public void resume() {
        if (cancel ) {

            started = 0;
            cancel = false;

            playbackThread = new PlaybackThread();
            playbackThread.start();

            playing = true;

        }
    }

    public boolean isDemoMode() {
        return demo;
    }

    public void loadDefaultJam() {
        makeKickBeats(true);
        makeClapBeats(true);
        makeHiHatBeats(true);
        makeHiHat2Beats(true);
    }

    public long getDuration() {
        return subbeatLength * subbeats * beats;
    }
}
