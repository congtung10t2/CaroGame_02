package com.framgia.carogame.model.data;

import android.content.Context;

import com.framgia.carogame.CaroGameApplication;
import com.framgia.carogame.libs.LogUtils;
import com.framgia.carogame.model.constants.ServicesDef;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by framgia on 28/09/2016.
 */
public class PlayerStorage implements Serializable {
    private int win;
    private int lost;
    private int streak;

    public PlayerStorage(int win, int lost, int streak) {
        this.win = win;
        this.lost = lost;
        this.streak = streak;
    }

    public PlayerStorage() {
        win = 0;
        lost = 0;
        streak = 0;
    }

    public static PlayerStorage load() {
        PlayerStorage playerStorage = null;
        try {
            FileInputStream fis = CaroGameApplication.getInstance()
                .openFileInput(ServicesDef.fileStorage);
            ObjectInputStream is = new ObjectInputStream(fis);
            playerStorage = (PlayerStorage) is.readObject();
            is.close();
            fis.close();
        } catch (IOException e) {
            LogUtils.logD("Input stream error!", e);
        } catch (ClassNotFoundException e) {
            LogUtils.logD("Class not found!", e);
        }
        return playerStorage;
    }

    public static PlayerStorage loadByGps(){
        PlayerStorage playerStorage = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(CaroGameApplication.
                getInstance().loadGame());
            ObjectInput in = null;
            in = new ObjectInputStream(bis);
            playerStorage = (PlayerStorage) in.readObject();
            bis.close();
            in.close();
        } catch (IOException e) {
            LogUtils.logD("Saved to Gps error!", e);
        } catch (ClassNotFoundException e) {
            LogUtils.logD("Class not found!", e);
        }
        return playerStorage;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public void setWin(int win, boolean sync) {
        if (this.win == win) return;
        this.win = win;
        if (sync == false) return;
        synchronize(true);
    }

    private void synchronize(boolean isWin) {
        if (isWin) streak++;
        else streak = 0;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        setLost(lost, true);
    }

    public void setLost(int lost, boolean sync) {
        if (this.lost == lost) return;
        this.lost = lost;
        if (sync == false) return;
        synchronize(false);
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public void increaseWinBy(int score) {
        setWin(win + score, true);
    }

    public void increaseLostBy(int score) {
        setLost(lost + score, true);
    }

    public void saveByGps(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutput objOut = new ObjectOutputStream(bos);
            objOut.writeObject(this);
            objOut.flush();
            CaroGameApplication.getInstance().saveGame(bos.toByteArray());
            bos.close();
            objOut.close();
        } catch (IOException e) {
            LogUtils.logD("Saved to Gps error!", e);
        }
    }

    public boolean save() {
        try {
            FileOutputStream fos = CaroGameApplication.getInstance()
                .openFileOutput(ServicesDef.fileStorage, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
            return true;
        } catch (IOException e) {
            LogUtils.logD("Write to file error!", e);
        }
        return false;
    }
}
