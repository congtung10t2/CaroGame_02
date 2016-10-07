package com.framgia.carogame.model.enums;

/**
 * Created by framgia on 05/10/2016.
 */
public enum Commands {
    INVALID(-1),
    SURRENDER(0),
    WIN(1),
    LEAVE(2),
    TICK(3);
    private int value;

    Commands(int id) {
        value = id;
    }

    public static Commands getCommand(int id) {
        if(id > TICK.value || id < SURRENDER.value) return INVALID;
        return values()[id];
    }
}
