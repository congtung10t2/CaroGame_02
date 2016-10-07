package com.framgia.carogame.libs;

import android.os.Build;

import com.framgia.carogame.viewmodel.services.ThreadCancel;

/**
 * Created by framgia on 28/09/2016.
 */
public class GameHelper {
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return model.startsWith(manufacturer) ? capitalize(model)
            : (capitalize(manufacturer) + " " + model);
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) return "";
        char first = s.charAt(0);
        return Character.isUpperCase(first) ? s : (Character.toUpperCase(first) + s.substring(1));
    }

    public static void stopThread(ThreadCancel thread) {
        if(thread == null) return;
        thread.cancel();
    }

    public static boolean isValidInRange(int val, int min, int max){
        return val >= min && val <= max;
    }
}
