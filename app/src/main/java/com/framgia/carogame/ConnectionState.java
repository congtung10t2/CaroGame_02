package com.framgia.carogame;

/**
 * Created by framgia on 27/09/2016.
 */
public class ConnectionState {
    private static ConnectionState instance = new ConnectionState();
    private State currentState = State.STATE_NONE;
    private StateListener stateListener;
    public enum State {
        STATE_NONE, // we're doing nothing
        STATE_LISTEN, // now listening for incoming connections
        STATE_CONNECTING, // now initiating an outgoing connections
        STATE_CONNECTED; // now connected to a remote device
    }

    private ConnectionState() {
    }

    public State getCurrentState() {
        return currentState;
    }

    public static ConnectionState getInstance() {
        return instance;
    }

    public void registerState(StateListener listener) {
        stateListener = listener;
    }

    public void setState(State newState) {
        if (currentState == newState) return;
        currentState = newState;
        if (stateListener == null) return;
        stateListener.onStateChanged(currentState, newState);
    }
}
