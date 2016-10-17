package com.framgia.carogame.libs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.view.View;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.framgia.carogame.viewmodel.services.ThreadCancel;

/**
 * Created by framgia on 28/09/2016.
 */
public class GameHelper {
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return model.startsWith(manufacturer) ? capitalize(model)
            : (capitalize(manufacturer) + " " + model);
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) return "";
        char first = s.charAt(0);
        return Character.isUpperCase(first) ? s : (Character.toUpperCase(first) + s.substring(1));
    }

    public static void stopThread(ThreadCancel thread) {
        if(thread == null) return;
        thread.cancel();
    }

    public static Bitmap takeScreenShot(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static SharePhotoContent shareImage(View view){
        Bitmap image = GameHelper.takeScreenShot(view);
        SharePhoto photo = new SharePhoto.Builder()
            .setBitmap(image)
            .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
            .addPhoto(photo)
            .build();
        return content;
    }

    public static boolean isValidInRange(int val, int min, int max){
        return val >= min && val <= max;
    }
}
