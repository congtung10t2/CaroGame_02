package com.framgia.carogame;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.framgia.carogame.libs.LogUtils;
import com.framgia.carogame.libs.UserStorage;
import com.framgia.carogame.model.constants.ServicesDef;
import com.framgia.carogame.model.data.PlayerStorage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;

import java.io.IOException;

/**
 * Created by framgia on 10/10/2016.
 */
public class CaroGameApplication extends Application
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static CaroGameApplication instance;
    private GoogleApiClient googleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initGps();
    }

    private void initGps(){
        googleApiClient = new GoogleApiClient.Builder(this, this, this)
            .addApi(Games.API).addScope(Games.SCOPE_GAMES).build();
    }

    public GoogleApiClient getGoogleApiClient(){
        return googleApiClient;
    }

    public void connectGps(){
        if(googleApiClient.isConnected()) return;
        googleApiClient.connect();
    }

    public void disconnectGps(){
        if(!googleApiClient.isConnected()) return;
        googleApiClient.disconnect();
    }

    public static CaroGameApplication getInstance(){
        return instance;
    }

    public void saveGame(byte[] data){
        if(data == null) return;
        Snapshots.OpenSnapshotResult snapshotResult = Games.Snapshots.open(
            googleApiClient, ServicesDef.fileStorage
            , true).await();
        Snapshot snapshot = snapshotResult.getSnapshot();
        snapshot.getSnapshotContents().writeBytes(data);
        Snapshots.CommitSnapshotResult commit = Games.Snapshots.commitAndClose(
            googleApiClient, snapshot, SnapshotMetadataChange.EMPTY_CHANGE).await();
    }

    public byte[] loadGame(){
        Snapshots.OpenSnapshotResult snapshotResult = Games.Snapshots.open(googleApiClient,
            ServicesDef.fileStorage, true).await();
        Snapshot snapshot = snapshotResult.getSnapshot();
        try {
            return snapshot.getSnapshotContents().readFully();
        } catch (IOException e) {
            LogUtils.logD("Data doesn't exist");
            return null;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(!UserStorage.getInstance().isFirstPlay()) return;
        PlayerStorage playerStorage = PlayerStorage.loadByGps();
        if(playerStorage == null) return;
        UserStorage.getInstance().setPlayerStorage(playerStorage);
    }

    @Override
    public void onConnectionSuspended(int i) {
        LogUtils.logD("onConnectionSuspended " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogUtils.logD("onConnectionFailed " + connectionResult.getErrorMessage());
        googleApiClient.connect();
    }
}