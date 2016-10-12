package com.framgia.carogame.viewmodel.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.framgia.carogame.R;
import com.framgia.carogame.libs.LogUtils;
import com.framgia.carogame.model.constants.ServicesDef;
import com.framgia.carogame.viewmodel.states.ConnectionState;
import com.framgia.carogame.libs.ToastUtils;

import java.io.IOException;

/**
 * Created by framgia on 30/09/2016.
 */
public class AcceptThread extends Thread implements ThreadCancel {
    private BluetoothServerSocket serverSocket;
    private String socketType;

    public AcceptThread(boolean secure) {
        socketType = secure ? ServicesDef.SECURE : ServicesDef.INSECURE;
        try {
            BluetoothAdapter adapterBT = BluetoothConnection.getInstance().getBluetoothAdapter();
            serverSocket = secure ? adapterBT.listenUsingRfcommWithServiceRecord(ServicesDef.NAME_SECURE,
                    ServicesDef.MY_UUID_SECURE) :
                adapterBT.listenUsingInsecureRfcommWithServiceRecord(ServicesDef.NAME_INSECURE,
                    ServicesDef.MY_UUID_INSECURE);
        } catch (IOException e) {
            LogUtils.logD("Create accept socket fail!", e);
        }
    }

    public AcceptThread() {
        try {
            serverSocket = BluetoothConnection.getInstance().getBluetoothAdapter().
                listenUsingRfcommWithServiceRecord(ServicesDef.NAME_SECURE,
                    ServicesDef.MY_UUID_SECURE);
            ToastUtils.showToast(R.string.start_game);
        } catch (IOException e) {
            LogUtils.logD("Create accept socket fail!", e);
        }
    }

    public synchronized void waiting(BluetoothSocket socket) {
        switch (ConnectionState.getInstance().getCurrentState()) {
            case STATE_LISTEN:
            case STATE_CONNECTING:
                BluetoothConnection.getInstance().connected(socket, socket.getRemoteDevice(), socketType);
                break;
            default:
                cancel();
                break;
        }
    }

    public void run() {
        BluetoothSocket socket;
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                LogUtils.logD("Socket accept run error!", e);
                return;
            }
            if (socket == null) return;
            waiting(socket);
        }
    }

    public void cancel() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            LogUtils.logD("Socket accept cancel error!", e);
        }
    }
}
