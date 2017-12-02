package com.framgia.carogame.viewmodel;

import com.framgia.carogame.libs.UserStorage;
import com.framgia.carogame.model.data.PlayerStorage;
import com.framgia.carogame.model.constants.GameDef;

/**
 * Created by framgia on 05/10/2016.
 */
public class PlayerStorageViewModel {
    private PlayerStorage playerStorage;

    public void setPlayerFromStorage(){
        UserStorage.getInstance().load();
        playerStorage = UserStorage.getInstance().getPlayerStorage();
        if(playerStorage != null) return;
        playerStorage = new PlayerStorage();
    }

    public PlayerStorage getPlayerStorage() {
        return playerStorage;
    }

    public String getRatePercent() {
        float rate = 0;
        int win = playerStorage.getWin();
        int lost = playerStorage.getLost();
        if(win + lost > 0)
        rate = win/(float)(win + lost);
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
