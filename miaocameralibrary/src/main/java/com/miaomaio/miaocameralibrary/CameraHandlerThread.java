package com.miaomaio.miaocameralibrary;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.miaomaio.miaocameralibrary.utils.CameraUtils;
import com.miaomaio.miaocameralibrary.utils.DisplayView;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/17
 * Description : 装载视图 to View
 */
public class CameraHandlerThread extends HandlerThread {
    private static final String LOG_TAG = "CameraHandlerThread";
    private DisplayView mDisplayView;

    public CameraHandlerThread(DisplayView displayView) {
        super("CameraHandlerThread");
        this.mDisplayView = displayView;
        this.start();
    }

    public void startCamera(final int cameraId) {
        Handler localHandler = new Handler(this.getLooper());
        localHandler.post(new Runnable() {
            public void run() {
                final Camera camera = CameraUtils.getCameraInstance(cameraId);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    public void run() {
                        CameraHandlerThread.this.mDisplayView.setupCameraPreview(CameraWrapper.getWrapper(camera, cameraId));
                    }
                });
            }
        });
    }
}

