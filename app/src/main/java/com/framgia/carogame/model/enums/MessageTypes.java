package com.framgia.carogame.model.enums;

/**
 * Created by framgia on 05/10/2016.
 */
public enum MessageTypes {
    INVALID_MESSAGE(-1),
    STATE_CHANGE(0),
    READ(1),
    WRITE(2),
    DEVICE_NAME(3),
    TOAST(4);
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