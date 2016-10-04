package com.framgia.carogame;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by framgia on 03/10/2016.
 */
public class ToastUtils {
    public static void showToast(String message){
        showToast(message, BluetoothConnection.getInstance().getGameContext());
    }

    public static void showToast(int id){
        showToast(BluetoothConnection.getInstance().getGameContext().getString(id));
    }

    public static void showToast(String message, Context context) {
        Toast.makeText(context, message,Toast.LENGTH_SHORT).show();
    }
    public static void showToast(int id, Context context) {
        Toast.makeText(context, context.getString(id),Toast.LENGTH_SHORT).show();
    }
}
