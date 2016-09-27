package com.framgia.carogame;

/**
 * Created by framgia on 26/09/2016.
 */
public class PlayerInfo {
    private String displayName;
    private int score;

    public PlayerInfo(String displayName, int score) {
        this.displayName = displayName;
        this.score = score;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void increaseScoreBy(int addScore) {
        setScore(score + addScore);
        // TODO : handle changed event in setter method.
    }
}
