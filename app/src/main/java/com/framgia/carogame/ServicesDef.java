package com.framgia.carogame;

import android.content.res.Resources;

import java.util.UUID;

/**
 * Created by framgia on 27/09/2016.
 */
public class ServicesDef {
    public static final String TAG = Resources.getSystem().getString(R.string.tag);
    public static final String NAME_SECURE =
        Resources.getSystem().getString(R.string.caro_game_secure);
    public static final String NAME_INSECURE =
        Resources.getSystem().getString(R.string.caro_game_insecure);
    public static final UUID MY_UUID_SECURE =
        UUID.fromString(Resources.getSystem().getString(R.string.my_uuid_secure));
    public static final UUID MY_UUID_INSECURE =
        UUID.fromString(Resources.getSystem().getString(R.string.my_uuid_insecure));
    public static final int FIND_DEVICE_CODE = 0;
}
