package com.framgia.carogame;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by framgia on 03/10/2016.
 */
public class ProgressBarUtils {
    public static void showPB(Context context, int titleId, int msgId){
        ProgressDialog.show(context, context.getString(titleId),
            context.getString(msgId), true);
    }
    public static void showPB(int titleId, int msgId){
        showPB(BluetoothConnection.getInstance().getGameContext(), titleId, msgId);
    }
    public static void showPB(String title, String msg){
        showPB(BluetoothConnection.getInstance().getGameContext(), title, msg);
    }
    public static void showPB(Context context, String title, String msg){
        ProgressDialog.show(context, title, msg, true);
    }
}
