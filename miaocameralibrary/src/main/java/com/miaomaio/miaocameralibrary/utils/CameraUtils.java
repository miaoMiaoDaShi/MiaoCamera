package com.miaomaio.miaocameralibrary.utils;

import android.hardware.Camera;

import java.util.List;


/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/17
 * Description :
 */

public class CameraUtils {
    public CameraUtils() {
    }

    public static Camera getCameraInstance() {
        return getCameraInstance(getDefaultCameraId());
    }

    public static int getDefaultCameraId() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int defaultCameraId = -1;

        for(int i = 0; i < numberOfCameras; ++i) {
            defaultCameraId = i;
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == 0) {
                return i;
            }
        }

        return defaultCameraId;
    }

    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;

        try {
            if (cameraId == -1) {
                c = Camera.open();
            } else {
                c = Camera.open(cameraId);
            }
        } catch (Exception var3) {
            ;
        }

        return c;
    }

    public static boolean isFlashSupported(Camera camera) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getFlashMode() == null) {
                return false;
            } else {
                List<String> supportedFlashModes = parameters.getSupportedFlashModes();
                return supportedFlashModes != null && !supportedFlashModes.isEmpty() && (supportedFlashModes.size() != 1 || !((String)supportedFlashModes.get(0)).equals("off"));
            }
        } else {
            return false;
        }
    }
}