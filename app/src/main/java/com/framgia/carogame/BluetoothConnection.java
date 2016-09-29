package com.framgia.carogame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by framgia on 27/09/2016.
 */
public class BluetoothConnection {
    private static BluetoothConnection instance = new BluetoothConnection();
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList devices = new ArrayList();
    private Set<BluetoothDevice> pairedDevices;
    private ConnectThread connectThread;
    private ConnectedNode connectedNode;
    private AcceptThread secureAcceptThread;
    private AcceptThread insecureAcceptThread;
    private Context mainContext;
    private String enemyDeviceName;

    public String getEnemyDeviceName() {
        return enemyDeviceName;
    }

    public void setEnemyDeviceName(String enemyDeviceName) {
        this.enemyDeviceName = enemyDeviceName;
    }

    private BluetoothConnection() {
    }

    public void stopAcceptThread() {
        GameHelper.stopThread(secureAcceptThread);
        secureAcceptThread = null;
        GameHelper.stopThread(insecureAcceptThread);
        insecureAcceptThread = null;
    }

    public void stopConnectThread() {
        GameHelper.stopThread(connectThread);
        connectThread = null;
        GameHelper.stopThread(connectedNode);
        connectedNode = null;
    }


    public void stopForConnectedNode() {
        stopConnectThread();
        stopAcceptThread();
    }

    public synchronized void StartServer() {
        stopConnectThread();
        ConnectionState.getInstance().setState(ConnectionState.State.STATE_LISTEN);
        if (secureAcceptThread == null) {
            secureAcceptThread = new AcceptThread(true);
            ToastUtils.showToast(R.string.start_server_secure);
            secureAcceptThread.start();
        }
        if (insecureAcceptThread != null) return;
        insecureAcceptThread = new AcceptThread(false);
        ToastUtils.showToast(R.string.start_server_insecure);
        insecureAcceptThread.start();
    }

    public void enable() {
        if (bluetoothAdapter == null) return;
        if (mainContext == null) return;
        if (bluetoothAdapter.isEnabled()) return;
        ((Activity) mainContext).startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
            ServicesDef.REQUEST_ENABLE_BT);
    }

    public ArrayList getDeviceList() {
        return devices;
    }

    private void discovery() {
        if (bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
            return;
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
            ServicesDef.DISCOVERABLE_DURATION);
        mainContext.startActivity(discoverableIntent);
    }

    public ArrayList scan() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        devices.clear();
        for (BluetoothDevice bt : pairedDevices)
            devices.add(bt.getName() + "\n" + bt.getAddress());
        discovery();
        return devices;
    }

    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            FragmentActivity activity = (FragmentActivity) BluetoothConnection.getInstance()
                .getGameContext();
            if (activity == null) return;
            ServicesDef.MessageType msgType = ServicesDef.MessageType.getMessageType(message.what);
            ConnectionState.State msgArg = ConnectionState.State.getState(message.arg1);
            switch (msgType) {
                case STATE_CHANGE:
                    if (msgArg != ConnectionState.State.STATE_CONNECTED) break;
                    activity.startActivity(new Intent(activity, GameView.class));
                    break;
                case WRITE:
                    break;
                case READ:
                    byte[] readBuf = (byte[]) message.obj;
                    String readMessage = new String(readBuf, 0, message.arg1);
                    //TODO: handle data from other user in game
                    break;
                case DEVICE_NAME:
                    setEnemyDeviceName(message.getData().getString(ServicesDef.DEVICE_NAME));
                    ToastUtils.showToast(mainContext.getString(R.string.connected_to) +
                        getEnemyDeviceName());
                    break;
                default:
                    ToastUtils.showToast(message.getData().getString(ServicesDef.TOAST));
                    break;
            }
        }
    };

    public void disable() {
        if (bluetoothAdapter == null) return;
        bluetoothAdapter.disable();
    }

    public static BluetoothConnection getInstance() {
        return instance;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void setGameContext(Context context) {
        this.mainContext = context;
    }

    public Context getGameContext() {
        return mainContext;
    }

    public void init() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mainContext.registerReceiver(mReceiver, filter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!action.equals(BluetoothDevice.ACTION_FOUND)) return;
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            devices.add(device.getName() + "\n" + device.getAddress());
        }
    };

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
        device, final String socketType) {
        stopForConnectedNode();
        connectedNode = new ConnectedNode(socket, socketType);
        connectedNode.start();
        Message msg = handler.obtainMessage(ServicesDef.MessageType.
            toInt(ServicesDef.MessageType.DEVICE_NAME));
        Bundle bundle = new Bundle();
        bundle.putString(ServicesDef.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);
        ConnectionState.getInstance().setState(ConnectionState.State.STATE_CONNECTED);
    }

    public synchronized void connect(BluetoothDevice device, boolean secure) {
        stopConnectThread();
        connectThread = new ConnectThread(device, secure);
        connectThread.start();
        ConnectionState.getInstance().setState(ConnectionState.State.STATE_CONNECTING);
    }
}
