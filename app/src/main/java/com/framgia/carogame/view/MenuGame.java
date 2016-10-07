package com.framgia.carogame.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.framgia.carogame.R;
import com.framgia.carogame.databinding.MenuGameActivityBinding;
import com.framgia.carogame.libs.GameHelper;
import com.framgia.carogame.libs.ProgressDialogUtils;
import com.framgia.carogame.libs.ToastUtils;
import com.framgia.carogame.model.constants.ServicesDef;
import com.framgia.carogame.viewmodel.PlayerStorageViewModel;
import com.framgia.carogame.viewmodel.services.BluetoothConnection;

public class MenuGame extends AppCompatActivity {
    private MenuGameActivityBinding binding;
    private PlayerStorageViewModel playerStorageViewModel;
    private CheckBox checkBluetooth;
    private Button findDevices;
    private Button createGame;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initView();
        initBluetooth();
    }
    @Override
    protected void onResume(){
        super.onResume();
        binding.invalidateAll();
    }

    public void initBluetooth() {
        BluetoothConnection.getInstance().setGameContext(this);
        BluetoothConnection.getInstance().init();
        if (BluetoothConnection.getInstance().getBluetoothAdapter().isEnabled()) {
            checkBluetooth.setChecked(true);
            return;
        }
        checkBluetooth.setChecked(false);
    }

    public void activePlay() {
        findDevices.setEnabled(true);
        createGame.setEnabled(true);
    }

    public void disablePlay() {
        findDevices.setEnabled(false);
        createGame.setEnabled(false);
    }

    public void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.menu_game_activity);
        playerStorageViewModel = new PlayerStorageViewModel();
        playerStorageViewModel.setPlayerFromStorage();
        binding.setPlayerStorage(playerStorageViewModel);
        Snackbar snackBar = Snackbar.make(binding.getRoot(), GameHelper.getDeviceName(), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.dissmiss, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView deviceName = (TextView) view.getRootView().findViewById(R.id.device_name);
                        deviceName.setText(GameHelper.getDeviceName());
                        TextView deviceAddress = (TextView) view.getRootView().findViewById(R.id.device_address);
                        deviceAddress.setText(BluetoothConnection.getInstance().getBluetoothAdapter().getAddress());
                    }
                });
        snackBar.show();
        checkBluetooth = (CheckBox) this.findViewById(R.id.bluetooth_check);
        checkBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    BluetoothConnection.getInstance().enable();
                    activePlay();
                    return;
                }
                disablePlay();
                ToastUtils.showToast(R.string.turn_off);
                BluetoothConnection.getInstance().disable();
            }
        });
        findDevices = (Button) findViewById(R.id.find_devices);
        createGame = (Button) findViewById(R.id.create_game);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch (requestCode) {
            case ServicesDef.REQUEST_ENABLE_BT: {
                if (resultCode != RESULT_OK) {
                    checkBluetooth.setChecked(false);
                    return;
                }
                ToastUtils.showToast(R.string.turned_on);
            }
            break;
            default:
                //This will handle in next time
                break;
        }
    }

    public void findDevices(View view) {
        startActivity(new Intent(this, DeviceList.class));
    }

    public void startGame(View view) {
        BluetoothConnection.getInstance().startServer();
        ProgressDialog pd = ProgressDialogUtils.showPB(MenuGame.this, R.string.loading,
            R.string.please_wait);
        BluetoothConnection.getInstance().setProgressDialog(pd);
    }
}
