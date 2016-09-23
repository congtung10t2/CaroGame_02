package com.framgia.carogame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class gameView extends View {
    Paint paint = new Paint();
    public Canvas canvas;
    public static final int MAX_WIDTH = 10;
    public static final int MAX_HEIGHT = 10;
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
    }
    float getDeltaX() {
        return canvas.getWidth()/(float)MAX_WIDTH;
    }
    float getDeltaY() {
        return (canvas.getWidth())/(float)MAX_WIDTH;
        //use same deltaX & deltaY for game board is squared. But still redefine to ez modify
    }
    void drawBoard() {
        for(int i = 0; i < MAX_WIDTH; i++) {
            canvas.drawLine(getDeltaX()*i, (canvas.getHeight()- canvas.getWidth()), getDeltaX()*i, canvas.getHeight(), paint);
        }
        for(int i = 0; i < MAX_HEIGHT; i++) {
            canvas.drawLine(0, getDeltaY()*i+(canvas.getHeight()- canvas.getWidth()), canvas.getWidth(), getDeltaY()*i+(canvas.getHeight()- canvas.getWidth()), paint);
        }
    }
    public void onDraw(Canvas canvas) {
        this.canvas = canvas;
        drawBoard();
    }
}
