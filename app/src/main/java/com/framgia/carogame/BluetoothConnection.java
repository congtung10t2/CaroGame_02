package com.framgia.carogame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by framgia on 27/09/2016.
 */
public class BluetoothConnection {
    private static BluetoothConnection instance = new BluetoothConnection();
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList devices = new ArrayList();
    private Set<BluetoothDevice> pairedDevices;
    private Context mainContext;

    private BluetoothConnection() {
    }

    public void enable() {
        if (bluetoothAdapter == null) return;
        if (bluetoothAdapter.isEnabled()) {
            Toast.makeText(mainContext, Resources.getSystem().getString(R.string.already_on), Toast.LENGTH_LONG).show();
            return;
        }
        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity) mainContext).startActivityForResult(turnOn, ServicesDef.FIND_DEVICE_CODE);
        Toast.makeText(mainContext, Resources.getSystem().getString(R.string.turned_on), Toast.LENGTH_LONG).show();
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

    public void init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
}
