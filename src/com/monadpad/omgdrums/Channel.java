package com.monadpad.omgdrums;

/**
 * User: m
 * Date: 11/15/13
 * Time: 1:56 PM
 */
public abstract class Channel {

    protected boolean enabled = true;

    private long finishAt;

    private Note lastPlayedNote;

    public void playNote(Note note) {

        if (lastPlayedNote != null)
            lastPlayedNote.isPlaying(false);

        if (!enabled)
            return;

        note.isPlaying(true);
        lastPlayedNote = note;


    }

    public void toggleEnabled() {
        if (enabled) {
            disable();
        }
        else {
            enable();
        }
    }

    public void disable() {
        enabled = false;
        mute();
    }

    public void enable() {
        enabled = true;
    }

    public void finishCurrentNoteAt(long time) {
        finishAt = time;
    }

    public long getFinishAt() {
        return finishAt;
    }

    public abstract void mute();

    public void loadPool() {

    }
}
