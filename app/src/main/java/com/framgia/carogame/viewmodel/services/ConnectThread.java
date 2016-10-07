package com.framgia.carogame.viewmodel.services;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.framgia.carogame.libs.LogUtils;
import com.framgia.carogame.model.constants.ServicesDef;

import java.io.IOException;

/**
 * Created by framgia on 30/09/2016.
 */
public class ConnectThread extends Thread implements ThreadCancel {
    private BluetoothSocket socket;
    private final BluetoothDevice device;
    private String socketType;

    public ConnectThread(BluetoothDevice device) {
        this.device = device;
        try {
            socket = device.createRfcommSocketToServiceRecord(ServicesDef.MY_UUID_SECURE);
        } catch (IOException e) {
            LogUtils.logD("Create connect socket fail", e);
        }
    }

    public ConnectThread(BluetoothDevice device, boolean secure) {
        this.device = device;
        socketType = secure ? ServicesDef.SECURE : ServicesDef.INSECURE;
        try {
            socket = secure ? device.createRfcommSocketToServiceRecord(ServicesDef.MY_UUID_SECURE) :
                device.createInsecureRfcommSocketToServiceRecord(ServicesDef.MY_UUID_INSECURE);
        } catch (IOException e) {
            LogUtils.logD("Create connect socket fail", e);
        }
    }

    public void run() {
        BluetoothConnection.getInstance().getBluetoothAdapter().cancelDiscovery();
        try {
            socket.connect();
        } catch (IOException e) {
            LogUtils.logD("Connect error!", e);
            return;
        }
        BluetoothConnection.getInstance().setConnectThread(null);
        BluetoothConnection.getInstance().connected(socket, device, socketType);
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            LogUtils.logD("Socket error!", e);
        }
    }
}