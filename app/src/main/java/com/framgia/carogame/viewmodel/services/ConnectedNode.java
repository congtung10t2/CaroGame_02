package com.framgia.carogame.viewmodel.services;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;

import com.framgia.carogame.R;
import com.framgia.carogame.libs.LogUtils;
import com.framgia.carogame.model.constants.ServicesDef;
import com.framgia.carogame.model.enums.MessageTypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by framgia on 30/09/2016.
 */
public class ConnectedNode extends Thread implements ThreadCancel {
    private BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public ConnectedNode(BluetoothSocket socket, String socketType) {
        LogUtils.logD("Create connected to: " + socketType);
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
        while (socket != null) {
            try {
                bytes = inputStream.read(buffer);
                BluetoothConnection.getInstance().handler.obtainMessage(MessageTypes.
                    toInt(MessageTypes.READ), bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                connectionLost();
            }
        }
    }

    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = BluetoothConnection.getInstance().handler.
            obtainMessage(MessageTypes.toInt(MessageTypes.TOAST));
        Bundle bundle = new Bundle();
        bundle.putString(ServicesDef.TOAST, BluetoothConnection.getInstance().getGameContext().
            getString(R.string.unable_to_connect_device));
        msg.setData(bundle);
        BluetoothConnection.getInstance().handler.sendMessage(msg);
        cancel();
    }

    private void connectionLost() {
        Message msg = BluetoothConnection.getInstance().handler.
            obtainMessage(MessageTypes.toInt(MessageTypes.CONNECTION_LOST));
        Bundle bundle = new Bundle();
        bundle.putString(ServicesDef.TOAST,BluetoothConnection.getInstance().getGameContext().
            getString(R.string.device_connection_lost));
        msg.setData(bundle);
        BluetoothConnection.getInstance().handler.sendMessage(msg);
        cancel();
    }

    public boolean write(byte[] buffer) {
        try {
            outputStream.write(buffer);
            BluetoothConnection.getInstance().handler.obtainMessage
                (MessageTypes.toInt(MessageTypes.WRITE), -1, -1, buffer)
                .sendToTarget();
            return true;
        } catch (IOException e) {
            LogUtils.logD("write data error", e);
            return false;
        }
    }

    public void cancel() {
        try {
            if(socket == null) return;
            socket.close();
        } catch (IOException e) {
            LogUtils.logD("cancel connected socket error", e);
        }
        socket = null;
    }
}