package com.miaomaio.miaocameralibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/17
 * Description :
 */
public class DisplayUtils {
    public DisplayUtils() {
    }

    @SuppressLint("ObsoleteSdkInt")
    public static Point getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService("window");
        Display display = wm.getDefaultDisplay();
        Point screenResolution = new Point();
        if (Build.VERSION.SDK_INT >= 13) {
            display.getSize(screenResolution);
        } else {
            screenResolution.set(display.getWidth(), display.getHeight());
        }

        return screenResolution;
    }

    public static int getScreenOrientation(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService("window");
        Display display = null;
        if (wm != null) {
            display = wm.getDefaultDisplay();
        }
        int orientation;
        if (display.getWidth() == display.getHeight()) {
            orientation = 3;
        } else if (display.getWidth() < display.getHeight()) {
            orientation = 1;
        } else {
            orientation = 2;
        }

        return orientation;
    }
}
