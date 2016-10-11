package com.framgia.carogame.model.constants;

import java.util.UUID;

/**
 * Created by framgia on 27/09/2016.
 */
public class ServicesDef {

    public static final String DEVICE_NAME = "device_name";
    public static final String NAME_SECURE = "CaroGameSecure";
    public static final String NAME_INSECURE = "CaroGameInSecure";
    public static final UUID MY_UUID_SECURE =
        UUID.fromString("42a3ff9a-0583-4ca8-971c-b4dcc289fa34");
    public static final UUID MY_UUID_INSECURE =
        UUID.fromString("ed8758ce-579f-4dca-949b-deb61eb02df3");
    public static final int FIND_DEVICE_CODE = 0;
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int DISCOVERABLE_DURATION = 300;
    public static final String SECURE = "Secure";
    public static final String INSECURE = "Insecure";
    public static final String TOAST = "toast";
    public static final int MAX_BYTES_ALLOC = 1024;
    public static final String fileStorage = "player.dat";
}
