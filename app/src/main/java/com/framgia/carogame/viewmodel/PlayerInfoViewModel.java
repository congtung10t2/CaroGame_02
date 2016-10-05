package com.framgia.carogame.viewmodel;

import android.content.Context;

import com.framgia.carogame.R;
import com.framgia.carogame.model.data.PlayerInfo;

/**
 * Created by framgia on 05/10/2016.
 */
public class PlayerInfoViewModel {
    private PlayerInfo enemyInfo;
    private PlayerInfo playerInfo;

    public PlayerInfoViewModel(){
        enemyInfo = new PlayerInfo("", 0);
        playerInfo = new PlayerInfo("", 0);
    }

    public PlayerInfoViewModel(Context context){
        enemyInfo = new PlayerInfo(context.getString(R.string.enemy_name), 0);
        playerInfo = new PlayerInfo(context.getString(R.string.player_name), 0);
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public PlayerInfo getEnemyInfo() {
        return enemyInfo;
    }

    public String getPlayerName() {
        return playerInfo.getDisplayName();
    }

    public String getEnemyName() {
        return enemyInfo.getDisplayName();
    }

    public String getPlayerScore() {
       return Integer.toString(playerInfo.getScore());
    }

    public String getEnemyScore() {
        return Integer.toString(enemyInfo.getScore());
    }
}
