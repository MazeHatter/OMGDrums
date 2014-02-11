package com.monadpad.omgdrums;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * User: m
 * Date: 11/15/13
 * Time: 11:01 PM
 */
public class DrumMachineView extends View {

    private Paint paint;
    private Paint paintOff;

    private int width = -1;
    private int height = -1;

    private int marginX;
    private int marginY;

    private int boxWidth;
    private int boxHeight;

    private int wide = 5;
    private int tall = 8;

    private boolean[] data;
    private MonadJam mJam;

    private Paint topPanelPaint;
    private Paint paintText;

    private int firstRowButton = 0;

    private String[][] captions;
    private float[][] captionWidths;

    private Paint paintBeat;

    private Paint blackPaint;

    private int adjustUp = 12;
    private int adjustDown = 18;

    private boolean isLive = false;
    private int lastX = -1;
    private int lastY = -1;

    private boolean hasChanged = false;

    public DrumMachineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setARGB(255, 255, 255, 255);
        paint.setShadowLayer(10, 0, 0, 0xFFFFFFFF);

        paintText = new Paint();
        paintText.setARGB(255, 255, 255, 255);
        paintText.setTextSize(24);

        paintBeat =  new Paint();
        paintBeat.setARGB(255, 255, 0, 0);

        paintOff = new Paint();
        paintOff.setARGB(255, 128, 128, 128);
        //paintOff.setShadowLayer(10, 0, 0, 0xFFFFFFFF);
        paintOff.setStyle(Paint.Style.STROKE);

        blackPaint = new Paint();
        blackPaint.setARGB(255, 0, 0, 0);
        blackPaint.setStyle(Paint.Style.STROKE);

        paintOff.setTextSize(paintText.getTextSize());
        blackPaint.setTextSize(22);


        topPanelPaint = new Paint();
        topPanelPaint.setARGB(255, 192, 192, 255);

        data = new boolean[32];

//        setBackgroundColor(Color.BLACK);
    }

    public void onDraw(Canvas canvas) {

        if (height != getHeight()) {
            width = getWidth();
            height = getHeight();
            marginX = width / 64;
            marginY = height / 128;
            boxWidth = width / wide;
            boxHeight = height / tall;

            if (boxHeight < 90) {
                blackPaint.setTextSize(12);
                adjustUp = 6;
                adjustDown = 8;
            }


            setCaptions();
        }

        canvas.drawRect(0, 0,
                boxWidth, height,
                topPanelPaint);

        if (mJam.isDrumsMuted())
            paintBeat.setARGB(255, 255, 0, 0);
        else
            paintBeat.setARGB(255, 0, 255, 0);


        boolean on;

        if (mJam != null && mJam.isPlaying()) {
            int i = 1 + (mJam.getCurrentSubbeat() % (wide - 1));
            int j = mJam.getCurrentSubbeat() / (wide - 1);
            canvas.drawRect(boxWidth * i,  j * boxHeight,
                    boxWidth * i + boxWidth, j * boxHeight + boxHeight,
                    paintBeat);
        }

        for (int j = 0; j < tall; j++) {
            for (int i = 0; i < wide; i++) {

                on = (i > 0 && data[(i - 1) + j * (wide - 1)]) || (i==0 && j==firstRowButton);
                canvas.drawRect(boxWidth * i + marginX,  j * boxHeight + marginY,
                        boxWidth * i + boxWidth - marginX, j * boxHeight + boxHeight - marginY,
                         on? paint:paintOff);

                if (i == 0) {

                    if (captionWidths[j].length == 1) {
                        canvas.drawText(captions[j][0], boxWidth / 2 - captionWidths[j][0] / 2,
                                j * boxHeight + boxHeight / 2 + 6, blackPaint);
                    }
                    else {
                        canvas.drawText(captions[j][0], boxWidth / 2 - captionWidths[j][0] / 2,
                                j * boxHeight + boxHeight / 2 - adjustUp, blackPaint);
                        canvas.drawText(captions[j][1], boxWidth / 2 - captionWidths[j][1] / 2,
                                j * boxHeight + boxHeight / 2 + adjustDown, blackPaint);
                    }
                }
                else {
                    canvas.drawText(i==1?Integer.toString(j+1):i==2?"e":i==3?"+":"a", i * boxWidth + boxWidth / 2 - 6,
                            boxHeight * j + boxHeight / 2 + 6, on? paintOff:paintText);
                }
            }
        }

    }


    public boolean onTouchEvent(MotionEvent event) {

        int boxX = (int)Math.floor(event.getX() / boxWidth);
        int boxY = (int)Math.floor(event.getY() / boxHeight);

        boxX = Math.min(wide - 1, Math.max(0, boxX));
        boxY = Math.min(tall - 1, Math.max(0, boxY));

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (boxX == 0) {
                handleFirstColumn(boxY);
            }
            else {
                handleTouch(boxX - 1, boxY);
                isLive = true;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isLive && boxX > 0) {

                if (boxX != lastX || boxY != lastY) {
                    handleTouch(boxX - 1, boxY);
                }
            }
        }

        lastX = boxX;
        lastY = boxY;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            isLive = false;
            lastX = -1;
            lastY = -1;
        }

        invalidate();
        return true;
    }

    private void handleTouch(int x, int y) {

        ((Main)getContext()).onModify();

        int beatColumns = wide - 1;
        int i = x % beatColumns + y * beatColumns;

        if (i >= 0 && data.length > i)
            data[i] = !data[i];

    }

    public void setJam(MonadJam jam) {
        mJam = jam;
    }

    void handleFirstColumn(int x)  {

        data = mJam.getTrack(x);

        firstRowButton = x;

        postInvalidate();
    }

    public void setCaptions() {

        captionWidths = new float[8][];
        captions = new String[8][];
        String[] caps = mJam.getCaptions();
        String[] lines;
        for (int i = 0; i < caps.length; i++) {

            captions[i] = caps[i].split(" ");
            captionWidths[i] = new float[captions[i].length];
            for (int j = 0; j < captions[i].length; j++) {
                captionWidths[i][j] = blackPaint.measureText(captions[i][j]) ;
            }

        }

    }


}
