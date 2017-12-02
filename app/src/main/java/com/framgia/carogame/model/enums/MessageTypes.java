package com.framgia.carogame.model.enums;

/**
 * Created by framgia on 05/10/2016.
 */
public enum MessageTypes {
    INVALID_MESSAGE(0),
    STATE_CHANGE(1),
    READ(2),
    WRITE(3),
    DEVICE_NAME(4),
    CONNECTION_LOST(5),
    TOAST(6);
    private int value;
    MessageTypes(int id) {
        value = id;
    }
    public static MessageTypes getMessageType(int id) {
        if(id > TOAST.value || id < STATE_CHANGE.value) return INVALID_MESSAGE;
        return values()[id];
    }
    public static int toInt(MessageTypes type){
        return type.value;
    }
}