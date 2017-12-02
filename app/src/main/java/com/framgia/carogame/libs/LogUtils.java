package com.framgia.carogame.libs;

import android.util.Log;

/**
 * Created by framgia on 05/10/2016.
 */
public class LogUtils {
    public static final String TAG = "CARO_GAME_BLUETOOTH";

    public static void logD(String msg, Throwable ex) {
        Log.d(TAG, msg, ex);
    }

    public static void logD(String msg) {
        Log.d(TAG, msg);
    }
}
