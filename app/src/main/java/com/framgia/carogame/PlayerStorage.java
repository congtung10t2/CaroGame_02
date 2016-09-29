package com.framgia.carogame;

/**
 * Created by framgia on 28/09/2016.
 */
public class PlayerStorage {
    private int win;
    private int lost;
    private float rate;
    private int streak;

    public PlayerStorage(int win, int lost, int rate, int streak) {
        this.win = win;
        this.lost = lost;
        this.rate = rate;
        this.streak = streak;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win){
        setWin(win, true);
    }

    public void setWin(int win, boolean sync) {
        if(this.win == win) return;
        this.win = win;
        if(sync == false) return;
        synchronize(true);
    }

    private void synchronize(boolean isWin) {
        syncRate();
        if(isWin) streak++;
        else streak = 0;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost){
        setLost(lost, true);
    }

    public void setLost(int lost, boolean sync) {
        if(this.lost == lost) return;
        this.lost = lost;
        if(sync == false) return;
        synchronize(false);
    }

    public int getStreak(){
        return streak;
    }

    public void setStreak(int streak){
        this.streak = streak;
    }

    public void syncRate() {
        rate = win/(win + lost);
    }

    public String getRatePercent() {
       return String.format("%.0f%%", rate * GameDef.FLOAT_TO_PERCENT);
    }

    public float getRate(){
        return rate;
    }

    public void increaseWinBy(int score){
        setWin(win + score, true);
    }

    public void increaseLostBy(int score){
        setLost(lost + score, true);
    }
}
