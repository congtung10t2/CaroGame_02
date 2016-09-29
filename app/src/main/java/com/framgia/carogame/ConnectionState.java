package com.framgia.carogame;

/**
 * Created by framgia on 27/09/2016.
 */
public class ConnectionState {
    private static ConnectionState instance = new ConnectionState();
    private State currentState = State.STATE_NONE;
    private StateListener stateListener;

    public enum State {
        STATE_INVALID(-1),
        STATE_NONE(0), // we're doing nothing
        STATE_LISTEN(1), // now listening for incoming connections
        STATE_CONNECTING(2), // now initiating an outgoing connections
        STATE_CONNECTED(3); // now connected to a remote device

        private int value;
        State(int id) {
            value = id;
        }
        public static State getState(int id) {
            if(id > STATE_CONNECTED.value || id < STATE_NONE.value) return STATE_INVALID;
            return values()[id];
        }
        public static int toInt(State state){
            return state.value;
        }

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
