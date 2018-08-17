package com.miaomaio.miaocameralibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.miaomaio.miaocameralibrary.CameraHandlerThread;
import com.miaomaio.miaocameralibrary.CameraPreview;
import com.miaomaio.miaocameralibrary.CameraWrapper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/17
 * Description : 相机的控制和预览视图的装载
 */
public class DisplayView extends FrameLayout implements Camera.PreviewCallback, Camera.PictureCallback {
    private CameraWrapper mCameraWrapper;
    private CameraPreview mPreview;
    private CameraHandlerThread mCameraHandlerThread;
    private Boolean mFlashState;
    private String mImageFilePath;
    /**
     * 自动对焦
     */
    private boolean mAutofocusState = true;

    public DisplayView(@NonNull Context context) {
        this(context, null);
    }

    public DisplayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DisplayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mImageFilePath = Environment
                .getExternalStorageDirectory() + "/DCIM/Camera/";// 保存图像的路径
    }

    public void startCamera(int cameraId) {
        if (this.mCameraHandlerThread == null) {
            this.mCameraHandlerThread = new CameraHandlerThread(this);
        }

        this.mCameraHandlerThread.startCamera(cameraId);
    }

    public void setupCameraPreview(CameraWrapper cameraWrapper) {
        this.mCameraWrapper = cameraWrapper;
        if (this.mCameraWrapper != null) {
            this.setupLayout(this.mCameraWrapper);
            if (this.mFlashState != null) {
                this.setFlash(this.mFlashState);
            }

            this.setAutoFocus(this.mAutofocusState);
        }

    }

    public final void setupLayout(CameraWrapper cameraWrapper) {
        this.removeAllViews();
        this.mPreview = new CameraPreview(this.getContext(), cameraWrapper, this);
        this.mPreview.setAspectTolerance(0);
        this.addView(this.mPreview);
    }

    public void startCamera() {
        this.startCamera(CameraUtils.getDefaultCameraId());
    }

    public void stopCamera() {
        if (this.mCameraWrapper != null) {
            this.mPreview.stopCameraPreview();
            this.mPreview.setCamera((CameraWrapper) null, (Camera.PreviewCallback) null);
            this.mCameraWrapper.mCamera.release();
            this.mCameraWrapper = null;
        }

        if (this.mCameraHandlerThread != null) {
            this.mCameraHandlerThread.quit();
            this.mCameraHandlerThread = null;
        }

    }

    public void stopCameraPreview() {
        if (this.mPreview != null) {
            this.mPreview.stopCameraPreview();
        }

    }

    protected void resumeCameraPreview() {
        if (this.mPreview != null) {
            this.mPreview.showCameraPreview();
        }

    }

    public void setFlash(boolean flag) {
        this.mFlashState = flag;
        if (this.mCameraWrapper != null && CameraUtils.isFlashSupported(this.mCameraWrapper.mCamera)) {
            Camera.Parameters parameters = this.mCameraWrapper.mCamera.getParameters();
            if (flag) {
                if (parameters.getFlashMode().equals("torch")) {
                    return;
                }

                parameters.setFlashMode("torch");
            } else {
                if (parameters.getFlashMode().equals("off")) {
                    return;
                }

                parameters.setFlashMode("off");
            }

            this.mCameraWrapper.mCamera.setParameters(parameters);
        }

    }

    public boolean getFlash() {
        if (this.mCameraWrapper != null && CameraUtils.isFlashSupported(this.mCameraWrapper.mCamera)) {
            Camera.Parameters parameters = this.mCameraWrapper.mCamera.getParameters();
            return parameters.getFlashMode().equals("torch");
        } else {
            return false;
        }
    }


    public void toggleFlash() {
        if (this.mCameraWrapper != null && CameraUtils.isFlashSupported(this.mCameraWrapper.mCamera)) {
            Camera.Parameters parameters = this.mCameraWrapper.mCamera.getParameters();
            if (parameters.getFlashMode().equals("torch")) {
                parameters.setFlashMode("off");
            } else {
                parameters.setFlashMode("torch");
            }

            this.mCameraWrapper.mCamera.setParameters(parameters);
        }

    }


    /**
     * 设置相机的闪光 模式
     *
     * @param flashMode
     */
    public void setFlash(String flashMode) {
        if (this.mCameraWrapper != null && CameraUtils.isFlashSupported(this.mCameraWrapper.mCamera)) {
            Camera.Parameters parameters = this.mCameraWrapper.mCamera.getParameters();
            parameters.setFlashMode(flashMode);
            this.mCameraWrapper.mCamera.setParameters(parameters);
        }

    }

    /**
     * 获取闪光的  模式
     *
     */
    public String getFlashMode() {
        if (this.mCameraWrapper != null && CameraUtils.isFlashSupported(this.mCameraWrapper.mCamera)) {
            Camera.Parameters parameters = this.mCameraWrapper.mCamera.getParameters();

            return parameters.getFlashMode();
        }
        return "";
    }

    public void setAutoFocus(boolean state) {
        this.mAutofocusState = state;
        if (this.mPreview != null) {
            this.mPreview.setAutoFocus(state);
        }

    }

    public void takePhoto() {
        this.mCameraWrapper.mCamera.takePicture(null, null, this);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    /**
     * 拍照回调
     *
     * @param data
     * @param camera
     */
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        BufferedOutputStream bufferedOutputStream = null;
        try {
            @SuppressLint("DefaultLocale") final File imageFile = new File(mImageFilePath, String.format("%d.jpg", System.currentTimeMillis()));
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(imageFile));

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            //旋转相应的角度
            bitmap = BitmapUtils.rotateBitmapByDegree(bitmap, mPreview.getDisplayOrientation());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            bufferedOutputStream.flush();
            if (mOnPictureListener != null) {
                mOnPictureListener.onSuccess(imageFile, imageFile.getAbsolutePath(), bitmap);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (mOnPictureListener != null) {
                mOnPictureListener.onFailed(e.getMessage());
            }
        } finally {
            try {
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private OnPictureListener mOnPictureListener;

    public void setOnPictureListener(OnPictureListener onPictureListener) {
        this.mOnPictureListener = onPictureListener;
    }

    public interface OnPictureListener {
        void onSuccess(File imageFile, String path, Bitmap bitmap);

        void onFailed(String e);
    }
}
