package com.monadpad.omgdrums;

/**
 * User: m
 * Date: 11/15/13
 * Time: 2:06 PM
 */
public class Note {

    private Note naked;
    private Note previous;

    private double mBeatPosition;
    private double mBeats;
    private int noteNumber;

    private boolean isrest = false;

    private boolean isplaying = false;

    public void setBeats(double beats) {

        mBeats = beats;

    }

    public void setRest(boolean value) {
        isrest = value;

    }

    public void setNote(int number) {
        noteNumber = number;
    }

    public int getNote() {
        return noteNumber;
    }

    public boolean isRest() {
        return isrest;
    }

    public double getBeats() {
        return mBeats;
    }


    public Note clone() {
        Note ret = new Note();
        ret.mBeats = mBeats;
        ret.isrest = isrest;
        ret.noteNumber = noteNumber;
        ret.mBeatPosition = mBeatPosition;

        if (naked != null) {
            ret.naked = naked;
        }
        else {
            ret.naked = this;
        }

        ret.previous = this;

        return ret;
    }

    public Note cloneNaked() {
        if (naked == null) {
            return clone();
        }

        Note ret = new Note();
        ret.mBeats = naked.mBeats;
        ret.isrest = naked.isrest;
        ret.noteNumber = naked.noteNumber;
        ret.naked = naked;
        ret.previous = this;

        ret.mBeatPosition = mBeatPosition;

        return ret;
    }

    public void setBeatPosition(double pos) {
        mBeatPosition = pos;
    }

    public double getBeatPosition() {
        return mBeatPosition;
    }

    public double getNakedBeats() {
        return naked == null ? mBeats : naked.mBeats;
    }

    public int getNakedNote() {
        return naked == null ? noteNumber : naked.noteNumber;
    }

    public boolean isPlaying() {
        return isplaying;
    }
    public void isPlaying(boolean value) {
        isplaying = value;
    }
}
