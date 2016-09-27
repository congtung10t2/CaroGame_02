package com.framgia.carogame;

/**
 * Created by framgia on 27/09/2016.
 */
public interface StateListener {
    void onStateChanged(ConnectionState.State oldState, ConnectionState.State newState);
}
