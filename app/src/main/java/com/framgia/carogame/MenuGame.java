package com.framgia.carogame;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.framgia.carogame.databinding.MenuGameActivityBinding;

public class MenuGame extends AppCompatActivity {
    private MenuGameActivityBinding binding;
    private PlayerStorage playerStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    public void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.menu_game_activity);
        playerStorage = new PlayerStorage(0, 0, 0, 0);
        binding.setPlayerStorage(playerStorage);
        Snackbar snackBar =  Snackbar.make(binding.getRoot(), GameHelper.getDeviceName(), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.dissmiss, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView deviceName =
                        (TextView) view.getRootView().findViewById(R.id.device_name);
                    deviceName.setText(GameHelper.getDeviceName());
                    TextView deviceAddress =
                        (TextView) view.getRootView().findViewById(R.id.device_address);
                    deviceAddress.setText(getResources().getString(R.string.address));
                    //Will replace address string by device address
                }
            });
        snackBar.show();
    }
}
