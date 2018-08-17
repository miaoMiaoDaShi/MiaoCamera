package com.miaomaio.miaocameralibrary;

import android.hardware.Camera;
import android.support.annotation.NonNull;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/17
 * Description : 相机信息
 */
public class CameraWrapper {
    public final Camera mCamera;
    public final int mCameraId;

    private CameraWrapper(@NonNull Camera camera, int cameraId) {
        if (camera == null) {
            throw new NullPointerException("Camera cannot be null");
        } else {
            this.mCamera = camera;
            this.mCameraId = cameraId;
        }
    }

    public static CameraWrapper getWrapper(Camera camera, int cameraId) {
        return camera == null ? null : new CameraWrapper(camera, cameraId);
    }
}
