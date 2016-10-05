package com.framgia.carogame.libs;

import android.app.ProgressDialog;
import android.content.Context;

import com.framgia.carogame.viewmodel.services.BluetoothConnection;

/**
 * Created by framgia on 03/10/2016.
 */
public class ProgressDialogUtils {
    public static ProgressDialog showPB(Context context, int titleId, int msgId){
        return ProgressDialog.show(context, context.getString(titleId),
            context.getString(msgId), true);
    }
    public static ProgressDialog showPB(int titleId, int msgId){
        return showPB(BluetoothConnection.getInstance().getGameContext(), titleId, msgId);
    }
    public static ProgressDialog showPB(String title, String msg){
        return showPB(BluetoothConnection.getInstance().getGameContext(), title, msg);
    }
    public static ProgressDialog showPB(Context context, String title, String msg){
        return ProgressDialog.show(context, title, msg, true);
    }
}
