package com.framgia.carogame.viewmodel;

import com.framgia.carogame.model.data.PlayerStorage;
import com.framgia.carogame.model.constants.GameDef;

/**
 * Created by framgia on 05/10/2016.
 */
public class PlayerStorageViewModel {
    private PlayerStorage playerStorage;

    public PlayerStorageViewModel(){
        playerStorage = new PlayerStorage(0, 0, 0, 0);
    }

    public PlayerStorage getPlayerStorage() {
        return playerStorage;
    }

    public String getRatePercent() {
        float win = playerStorage.getWin();
        float lost = playerStorage.getLost();
        float rate = win/(win + lost);
        return String.format("%.0f%%", rate * GameDef.FLOAT_TO_PERCENT);
    }

    public String getWin() {
        return Integer.toString(playerStorage.getWin());
    }

    public String getLost() {
        return Integer.toString(playerStorage.getLost());
    }

    public String getStreak() {
        return Integer.toString(playerStorage.getStreak());
    }
}
