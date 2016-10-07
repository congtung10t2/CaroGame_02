package com.framgia.carogame.libs;

import com.framgia.carogame.model.data.PlayerStorage;

/**
 * Created by framgia on 10/10/2016.
 */
public class UserStorage {
    private static UserStorage instance = new UserStorage();
    private PlayerStorage playerStorage;
    private boolean dirty;

    private UserStorage() {
    }

    public static UserStorage getInstance() {
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
        playerStorage = new PlayerStorage();
    }
}
