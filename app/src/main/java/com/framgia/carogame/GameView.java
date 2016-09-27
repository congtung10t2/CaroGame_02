package com.framgia.carogame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    private Paint paint = new Paint();
    private CaroGame caroGame;
    private Canvas canvas;
    private final float SIDE = 20.0f;
    private final float OFFSET_X = 0.5f;
    private final float OFFSET_Y = 0.5f;
    private final float RADIUS = 20.0f;
    private TickType[][] ticks = new TickType[GameDef.MAX_WIDTH][GameDef.MAX_HEIGHT];
    private TickType currentTick = TickType.SQUARED;

    enum TickType {
        INVALID,
        CIRCLE,
        SQUARED
    }

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setColor(Color.BLACK);
        resetGame();
    }

    public void resetGame() {
        for (int i = 0; i < GameDef.MAX_WIDTH; i++) {
            for (int j = 0; j < GameDef.MAX_HEIGHT; j++) {
                ticks[i][j] = TickType.INVALID;
            }
        }
        this.invalidate();
    }

    float getDeltaX() {
        return canvas.getWidth() / (float) GameDef.MAX_WIDTH;
    }

    float getDeltaY() {
        return (canvas.getWidth()) / (float) GameDef.MAX_HEIGHT;
        //use same deltaX & deltaY TODO game board is squared. But still redefine to ez modify
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
        for (int i = 0; i < GameDef.MAX_WIDTH; i++) {
            canvas.drawLine(getDeltaX() * i, subHeightWidth(), getDeltaX() * i, canvas.getHeight(),
                paint);
        }
        for (int i = 0; i < GameDef.MAX_HEIGHT; i++) {
            canvas.drawLine(0, getDeltaY() * i + subHeightWidth(), canvas.getWidth(),
                getDeltaY() * i + subHeightWidth(), paint);
        }
    }

    public void setGameContext(CaroGame caroGame) {
        this.caroGame = caroGame;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case (MotionEvent.ACTION_DOWN):
                caroGame.hideProgressBar();
                break;
            case (MotionEvent.ACTION_UP): {
                postInvalidate();
                float touchX = event.getX();
                float touchY = event.getY();
                int indexX = getIndexX(touchX);
                int indexY = getIndexY(touchY);
                caroGame.showProgressBar();
                if (indexX < 0 || indexX > GameDef.MAX_WIDTH) return false;
                if (indexY < 0 || indexY > GameDef.MAX_HEIGHT) return false;
                if (ticks[indexX][indexY] == TickType.INVALID) {
                    ticks[indexX][indexY] = currentTick;
                    if (currentTick == TickType.SQUARED) {
                        caroGame.onEnemyTurn();
                        currentTick = TickType.CIRCLE;
                    } else {
                        currentTick = TickType.SQUARED;
                        caroGame.onPlayerTurn();
                    }
                }
            }
            break;
        }
        return true;
    }

    boolean isLimit(int indexX, int indexY) {
        if (indexX < 0 || indexX > GameDef.MAX_WIDTH - 1) return false;
        if (indexY < 0 || indexY > GameDef.MAX_HEIGHT - 1) return false;
        return true;
    }

    int countOnDirection(int indexX, int indexY, TickType value, int stepX, int stepY) {
        int sum = 0;
        for (int k = 1; k < GameDef.WIN_TICK; k++) {
            if (isLimit(indexX + k * stepX, indexY + k * stepY)) return sum;
            if (ticks[indexX + k * stepX][indexY + k * stepY] == value) sum++;
            else return sum;
        }
        return sum;
    }

    boolean isWin(int indexX, int indexY, TickType value) {
        if (countOnDirection(indexX, indexY, value, 0, 1) + countOnDirection(indexX, indexY,
            value, 0, -1) == GameDef.WIN_TICK - 1) return true;
        if (countOnDirection(indexX, indexY, value, 1, 0) + countOnDirection(indexX, indexY,
            value, -1, 0) == GameDef.WIN_TICK - 1) return true;
        if (countOnDirection(indexX, indexY, value, 1, 1) + countOnDirection(indexX, indexY,
            value, -1, -1) == GameDef.WIN_TICK - 1) return true;
        return false;
    }

    void updateTicks() {
        for (int i = 0; i < GameDef.MAX_WIDTH; i++) {
            for (int j = 0; j < GameDef.MAX_HEIGHT; j++) {
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
        invalidate();
    }
}
