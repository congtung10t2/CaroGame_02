package com.framgia.carogame.viewmodel.services;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.framgia.carogame.R;
import com.framgia.carogame.libs.GameHelper;
import com.framgia.carogame.libs.LogUtils;
import com.framgia.carogame.libs.ToastUtils;
import com.framgia.carogame.model.constants.ServicesDef;
import com.framgia.carogame.model.enums.MessageTypes;
import com.framgia.carogame.view.CaroGame;
import com.framgia.carogame.viewmodel.games.OnGameCallback;
import com.framgia.carogame.viewmodel.states.ConnectionState;
import com.framgia.carogame.viewmodel.states.StateListener;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by framgia on 27/09/2016.
 */
public class BluetoothConnection implements StateListener {
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
    private BluetoothDevice currentDevice;
    private ProgressDialog progressDialog;
    private OnGameCallback gameCallback;
    private boolean isServer;
    private boolean isSecureConnect;

    public ConnectThread getConnectThread() {
        return connectThread;
    }

    public void setConnectThread(ConnectThread connectThread) {
        this.connectThread = connectThread;
    }

    public boolean isSecureConnect() {
        return isSecureConnect;
    }

    public void setSecureConnect(boolean secureConnect) {
        isSecureConnect = secureConnect;
    }

    private BluetoothConnection() {
    }

    public void setCurrentDevice(BluetoothDevice device){
        currentDevice = device;
    }

    public BluetoothDevice getCurrentDevice(){
        return currentDevice;
    }

    public ProgressDialog getProgressDialog(){
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog){
        this.progressDialog = progressDialog;
    }

    public String getEnemyDeviceName() {
        return enemyDeviceName;
    }

    public void setEnemyDeviceName(String enemyDeviceName) {
        this.enemyDeviceName = enemyDeviceName;
    }

    public void setGameCallback(OnGameCallback gcb){
        gameCallback = gcb;
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

    public boolean isServer(){
        return isServer;
    }

    public void setServer(){
        isServer = true;
    }

    public void setClient(){
        isServer = false;
    }

    public void stopForConnectedNode() {
        stopConnectThread();
        stopAcceptThread();
    }

    public synchronized void startServer() {
        stopConnectThread();
        ConnectionState.getInstance().setState(ConnectionState.State.STATE_LISTEN);
        if (secureAcceptThread == null) {
            secureAcceptThread = new AcceptThread(true);
            secureAcceptThread.start();
        }

        setServer();
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
            MessageTypes msgType = MessageTypes.getMessageType(message.what);
            ConnectionState.State msgArg = ConnectionState.State.getState(message.arg1);
            switch (msgType) {
                case STATE_CHANGE:
                    LogUtils.logD("State changed");
                    break;
                case WRITE:
                    break;
                case READ:
                    byte[] readBuf = (byte[]) message.obj;
                    String readMessage = new String(readBuf, 0, message.arg1);
                    gameCallback.onMessage(readMessage);
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

    public void writes(String msg){
        connectedNode.write(msg.getBytes());
    }

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
        ConnectionState.getInstance().registerState(this);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mainContext.registerReceiver(mReceiver, filter);
    }

    public void onStateChanged(ConnectionState.State oldState, ConnectionState.State newState){
        handler.obtainMessage(MessageTypes.toInt(MessageTypes.STATE_CHANGE), ConnectionState
            .State.toInt(newState), -1).sendToTarget();
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
        BluetoothConnection.getInstance().setCurrentDevice(device);
        connectedNode = new ConnectedNode(socket, socketType);
        connectedNode.start();
        if(progressDialog != null) progressDialog.dismiss();
        Message msg = handler.obtainMessage(MessageTypes.toInt(MessageTypes.DEVICE_NAME));
        Bundle bundle = new Bundle();
        bundle.putString(ServicesDef.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);
        mainContext.startActivity(new Intent(mainContext, CaroGame.class));
        ConnectionState.getInstance().setState(ConnectionState.State.STATE_CONNECTED);
    }

    public synchronized void connect(BluetoothDevice device, boolean secure) {
        stopConnectThread();
        setSecureConnect(secure);
        connectThread = new ConnectThread(device, secure);
        connectThread.start();
        ConnectionState.getInstance().setState(ConnectionState.State.STATE_CONNECTING);
        setClient();
    }
}
