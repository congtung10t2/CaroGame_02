package com.framgia.carogame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class gameView extends View {
    Paint paint = new Paint();
    public Canvas canvas;
    public static final int MAX_WIDTH = 10;
    public static final int MAX_HEIGHT = 10;
    private final float SIDE = 20.0f;
    private final float OFFSET_X = 0.5f;
    private final float OFFSET_Y = 0.5f;
    private final float RADIUS = 20.0f;

    enum TickType {
        INVALID,
        CIRCLE,
        SQUARED
    }

    public TickType[][] ticks = new TickType[MAX_WIDTH][MAX_HEIGHT];
    private TickType currentTick = TickType.SQUARED;

    public void resetGame() {
        for (int i = 0; i < MAX_WIDTH; i++) {
            for (int j = 0; j < MAX_HEIGHT; j++) {
                ticks[i][j] = TickType.INVALID;
            }
        }
        this.invalidate();
    }

    public gameView(Context context) {
        super(context);
        init();
    }

    public gameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setColor(Color.BLACK);
        resetGame();
    }

    float getDeltaX() {
        return canvas.getWidth() / (float) MAX_WIDTH;
    }

    float getDeltaY() {
        return (canvas.getWidth()) / (float) MAX_WIDTH;
        //use same deltaX & deltaY for game board is squared. But still redefine to ez modify
    }

    int getIndexX(float x) {
        return (int) (x / getDeltaX());
    }

    int getIndexY(float y) {
        return (int) ((y - subHeightWidth()) / getDeltaY());
    }

    private float subHeightWidth() {
        return (canvas.getHeight() - canvas.getWidth());
    }

    private void drawBoard() {
        paint.setColor(Color.BLACK);
        for (int i = 0; i < MAX_WIDTH; i++) {
            canvas.drawLine(getDeltaX() * i, subHeightWidth(), getDeltaX() * i, canvas.getHeight(),
                paint);
        }
        for (int i = 0; i < MAX_HEIGHT; i++) {
            canvas.drawLine(0, getDeltaY() * i + subHeightWidth(), canvas.getWidth(),
                getDeltaY() * i + subHeightWidth(), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        postInvalidate();
        float touchX = event.getX();
        float touchY = event.getY();
        int indexX = getIndexX(touchX);
        int indexY = getIndexY(touchY);
        if (indexX < 0 || indexX > MAX_WIDTH)
            return false;
        if (indexY < 0 || indexY > MAX_HEIGHT)
            return false;
        ticks[indexX][indexY] = currentTick;
        return true;
    }

    void updateTicks() {
        for (int i = 0; i < MAX_WIDTH; i++) {
            for (int j = 0; j < MAX_HEIGHT; j++) {
                switch (ticks[i][j]) {
                    case SQUARED:
                        paint.setColor(Color.BLUE);
                        canvas.drawRect((i + OFFSET_X) * getDeltaX() - SIDE,
                            (j + OFFSET_Y) * getDeltaY() + subHeightWidth() - SIDE,
                            (i + OFFSET_X) * getDeltaX() + SIDE,
                            (j + OFFSET_Y) * getDeltaY() + subHeightWidth() + SIDE, paint);
                        break;
                    case CIRCLE:
                        paint.setColor(Color.RED);
                        canvas.drawCircle((i + OFFSET_X) * getDeltaX(),
                            (j + OFFSET_Y) * getDeltaY() + subHeightWidth(), RADIUS, paint);
                        break;
                }
            }
        }
    }

    public void onDraw(Canvas canvas) {
        this.canvas = canvas;
        drawBoard();
        updateTicks();
    }
}
