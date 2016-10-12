package com.framgia.carogame.libs;

import com.framgia.carogame.CaroGameApplication;
import com.framgia.carogame.model.data.PlayerStorage;

/**
 * Created by framgia on 10/10/2016.
 */
public class UserStorage {
    private static UserStorage instance;
    private PlayerStorage playerStorage;
    private boolean dirty;
    private boolean firstPlay;

    public boolean isFirstPlay() {
        return firstPlay;
    }

    public void setFirstPlay(boolean firstPlay) {
        this.firstPlay = firstPlay;
    }

    private UserStorage() {
    }

    public static UserStorage getInstance() {
        if(instance == null) instance = new UserStorage();
        return instance;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public PlayerStorage getPlayerStorage() {
        return playerStorage;
    }

    public void setPlayerStorage(PlayerStorage playerStorage) {
        this.playerStorage = playerStorage;
    }

    public void save() {
        if (!dirty) return;
        dirty = !playerStorage.save();
        if(!CaroGameApplication.getInstance().getGoogleApiClient().isConnected()) return;
        playerStorage.saveByGps();
    }

    public void save(int win, int lost) {
        playerStorage.increaseWinBy(win);
        playerStorage.increaseLostBy(lost);
        setDirty(true);
        save();
    }

    public void load() {
        playerStorage = PlayerStorage.load();
        if (playerStorage != null) return;
        if(CaroGameApplication.getInstance().getGoogleApiClient().isConnected())
        playerStorage = PlayerStorage.loadByGps();
        if (playerStorage != null) return;
        firstPlay = true;
        playerStorage = new PlayerStorage();
    }
}
