package com.framgia.carogame;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

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
            ToastUtils.showToast(R.string.start_game);
        } catch (IOException e) {
            ServicesDef.log("Create accept socket fail!", e);
        }
    }

    public AcceptThread() {
        try {
            serverSocket = BluetoothConnection.getInstance().getBluetoothAdapter().
                listenUsingRfcommWithServiceRecord(ServicesDef.NAME_SECURE,
                    ServicesDef.MY_UUID_SECURE);
            ToastUtils.showToast(R.string.start_game);
        } catch (IOException e) {
            ServicesDef.log("Create accept socket fail!", e);
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
        BluetoothSocket socket = null;
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                ServicesDef.log("Socket accept run error!", e);
            }
            if (socket == null) continue;
            waiting(socket);
        }
    }

    public void cancel() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            ServicesDef.log("Socket accept cancel error!", e);
        }
    }
}
