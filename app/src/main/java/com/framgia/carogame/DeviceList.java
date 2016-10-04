package com.framgia.carogame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceList extends Activity {
    private ArrayAdapter<String> adapterDevices;
    private ListView listDevices;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        listDevices = (ListView) findViewById(R.id.devices);
    }

    public void setList(ArrayList devices) {
        adapterDevices = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, devices);
        listDevices.setAdapter(adapterDevices);
        listDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info = ((TextView) view).getText().toString();
                String[] device = info.split("\n");
                BluetoothConnection.getInstance().connect(BluetoothConnection.getInstance()
                    .getBluetoothAdapter().getRemoteDevice(device[1]), true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBarUtils.showPB(DeviceList.this, getString(R.string.loading),
                            getString(R.string.loading_date));
                    }
                });

            }
        });
    }

    public void scan(View view) {
        setList(BluetoothConnection.getInstance().scan());
    }
}
