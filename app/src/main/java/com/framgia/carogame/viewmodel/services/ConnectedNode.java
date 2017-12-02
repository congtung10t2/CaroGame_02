package com.framgia.carogame.viewmodel.services;

import android.bluetooth.BluetoothSocket;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;

import com.framgia.carogame.libs.LogUtils;
import com.framgia.carogame.libs.ProgressBarUtils;
import com.framgia.carogame.R;
import com.framgia.carogame.model.constants.ServicesDef;
import com.framgia.carogame.model.enums.MessageTypes;
import com.framgia.carogame.viewmodel.states.ConnectionState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by framgia on 30/09/2016.
 */
public class ConnectedNode extends Thread implements ThreadCancel {
    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public ConnectedNode(BluetoothSocket socket, String socketType) {
        this.socket = socket;
        InputStream tempIn = null;
        OutputStream tempOut = null;
        try {
            tempIn = socket.getInputStream();
            tempOut = socket.getOutputStream();
        } catch (IOException e) {
            LogUtils.logD("Create connected socket error", e);
        }
        inputStream = tempIn;
        outputStream = tempOut;
    }

    public void run() {
        byte[] buffer = new byte[ServicesDef.MAX_BYTES_ALLOC];
        int bytes;
        while (ConnectionState.getInstance().getCurrentState() ==
            ConnectionState.State.STATE_CONNECTED) {
            try {
                bytes = inputStream.read(buffer);
                BluetoothConnection.getInstance().handler.obtainMessage(MessageTypes
                    .READ.ordinal(), bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                connectionLost();
                BluetoothConnection.getInstance().StartServer();
                break;
            }
        }
    }

    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = BluetoothConnection.getInstance().handler.
            obtainMessage(MessageTypes.TOAST.ordinal());
        Bundle bundle = new Bundle();
        bundle.putString(ServicesDef.TOAST, Resources.getSystem().getString(R.string
            .unable_to_connect_device));
        msg.setData(bundle);
        BluetoothConnection.getInstance().handler.sendMessage(msg);
        // Start the service over to restart listening mode
        BluetoothConnection.getInstance().StartServer();
    }

    private void connectionLost() {
        Message msg = BluetoothConnection.getInstance().handler.
            obtainMessage(MessageTypes.TOAST.ordinal());
        Bundle bundle = new Bundle();
        bundle.putString(ServicesDef.TOAST,Resources.getSystem().
            getString(R.string.device_connection_lost));
        msg.setData(bundle);
        BluetoothConnection.getInstance().handler.sendMessage(msg);
        BluetoothConnection.getInstance().StartServer();
        ProgressBarUtils.showPB(R.string.please_wait, R.string.loading_date);
    }

    public void write(byte[] buffer) {
        try {
            outputStream.write(buffer);
            BluetoothConnection.getInstance().handler.obtainMessage
                (MessageTypes.WRITE.ordinal(), -1, -1, buffer).sendToTarget();
        } catch (IOException e) {
            LogUtils.logD("write data error", e);
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            LogUtils.logD("cancel connected socket error", e);
        }
    }
}