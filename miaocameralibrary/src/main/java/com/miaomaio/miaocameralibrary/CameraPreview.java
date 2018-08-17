package com.miaomaio.miaocameralibrary;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.miaomaio.miaocameralibrary.utils.DisplayUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/17
 * Description :相机预览视图  包含对于相机的直接设置
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";
    private CameraWrapper mCameraWrapper;
    private Handler mAutoFocusHandler;
    private boolean mPreviewing = true;
    private boolean mAutoFocus = true;
    private boolean mSurfaceCreated = false;
    private boolean mShouldScaleToFill = true;
    private Camera.PreviewCallback mPreviewCallback;
    private float mAspectTolerance = 0.1F;
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (CameraPreview.this.mCameraWrapper != null && CameraPreview.this.mPreviewing && CameraPreview.this.mAutoFocus && CameraPreview.this.mSurfaceCreated) {
                CameraPreview.this.safeAutoFocus();
            }

        }
    };
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            CameraPreview.this.scheduleAutoFocus();
        }
    };

    public CameraPreview(Context context, CameraWrapper cameraWrapper, Camera.PreviewCallback previewCallback) {
        super(context);
        this.init(cameraWrapper, previewCallback);
    }

    public CameraPreview(Context context, AttributeSet attrs, CameraWrapper cameraWrapper, Camera.PreviewCallback previewCallback) {
        super(context, attrs);
        this.init(cameraWrapper, previewCallback);
    }

    public void init(CameraWrapper cameraWrapper, Camera.PreviewCallback previewCallback) {
        this.setCamera(cameraWrapper, previewCallback);
        this.mAutoFocusHandler = new Handler();
        this.getHolder().addCallback(this);
        this.getHolder().setType(3);
    }

    public void setCamera(CameraWrapper cameraWrapper, Camera.PreviewCallback previewCallback) {
        this.mCameraWrapper = cameraWrapper;
        this.mPreviewCallback = previewCallback;
    }

    public void setShouldScaleToFill(boolean scaleToFill) {
        this.mShouldScaleToFill = scaleToFill;
    }

    public void setAspectTolerance(float aspectTolerance) {
        this.mAspectTolerance = aspectTolerance;
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.mSurfaceCreated = true;
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if (surfaceHolder.getSurface() != null) {
            this.stopCameraPreview();
            this.showCameraPreview();
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.mSurfaceCreated = false;
        this.stopCameraPreview();
    }

    public void showCameraPreview() {
        if (this.mCameraWrapper != null) {
            try {
                this.getHolder().addCallback(this);
                this.mPreviewing = true;
                this.setupCameraParameters();
                this.mCameraWrapper.mCamera.setPreviewDisplay(this.getHolder());
                this.mCameraWrapper.mCamera.setDisplayOrientation(this.getDisplayOrientation());
                this.mCameraWrapper.mCamera.setOneShotPreviewCallback(this.mPreviewCallback);
                this.mCameraWrapper.mCamera.startPreview();
                if (this.mAutoFocus) {
                    if (this.mSurfaceCreated) {
                        this.safeAutoFocus();
                    } else {
                        this.scheduleAutoFocus();
                    }
                }
            } catch (Exception var2) {
                Log.e("CameraPreview", var2.toString(), var2);
            }
        }

    }

    public void safeAutoFocus() {
        try {
            this.mCameraWrapper.mCamera.autoFocus(this.autoFocusCB);
        } catch (RuntimeException var2) {
            this.scheduleAutoFocus();
        }

    }

    public void stopCameraPreview() {
        if (this.mCameraWrapper != null) {
            try {
                this.mPreviewing = false;
                this.getHolder().removeCallback(this);
                this.mCameraWrapper.mCamera.cancelAutoFocus();
                this.mCameraWrapper.mCamera.setOneShotPreviewCallback((Camera.PreviewCallback)null);
                this.mCameraWrapper.mCamera.stopPreview();
            } catch (Exception var2) {
                Log.e("CameraPreview", var2.toString(), var2);
            }
        }

    }

    public void setupCameraParameters() {
        Camera.Size optimalSize = this.getOptimalPreviewSize();
        Camera.Parameters parameters = this.mCameraWrapper.mCamera.getParameters();
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        this.mCameraWrapper.mCamera.setParameters(parameters);
        this.adjustViewSize(optimalSize);
    }

    private void adjustViewSize(Camera.Size cameraSize) {
        Point previewSize = this.convertSizeToLandscapeOrientation(new Point(this.getWidth(), this.getHeight()));
        float cameraRatio = (float)cameraSize.width / (float)cameraSize.height;
        float screenRatio = (float)previewSize.x / (float)previewSize.y;
        if (screenRatio > cameraRatio) {
            this.setViewSize((int)((float)previewSize.y * cameraRatio), previewSize.y);
        } else {
            this.setViewSize(previewSize.x, (int)((float)previewSize.x / cameraRatio));
        }

    }

    private Point convertSizeToLandscapeOrientation(Point size) {
        return this.getDisplayOrientation() % 180 == 0 ? size : new Point(size.y, size.x);
    }

    private void setViewSize(int width, int height) {
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        int tmpWidth;
        int tmpHeight;
        if (this.getDisplayOrientation() % 180 == 0) {
            tmpWidth = width;
            tmpHeight = height;
        } else {
            tmpWidth = height;
            tmpHeight = width;
        }

        if (this.mShouldScaleToFill) {
            int parentWidth = ((View)this.getParent()).getWidth();
            int parentHeight = ((View)this.getParent()).getHeight();
            float ratioWidth = (float)parentWidth / (float)tmpWidth;
            float ratioHeight = (float)parentHeight / (float)tmpHeight;
            float compensation;
            if (ratioWidth > ratioHeight) {
                compensation = ratioWidth;
            } else {
                compensation = ratioHeight;
            }

            tmpWidth = Math.round((float)tmpWidth * compensation);
            tmpHeight = Math.round((float)tmpHeight * compensation);
        }

        layoutParams.width = tmpWidth;
        layoutParams.height = tmpHeight;
        this.setLayoutParams(layoutParams);
    }

    public int getDisplayOrientation() {
        if (this.mCameraWrapper == null) {
            return 0;
        } else {
            Camera.CameraInfo info = new Camera.CameraInfo();
            if (this.mCameraWrapper.mCameraId == -1) {
                Camera.getCameraInfo(0, info);
            } else {
                Camera.getCameraInfo(this.mCameraWrapper.mCameraId, info);
            }

            WindowManager wm = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            int rotation = display.getRotation();
            int degrees = 0;
            switch(rotation) {
                case 0:
                    degrees = 0;
                    break;
                case 1:
                    degrees = 90;
                    break;
                case 2:
                    degrees = 180;
                    break;
                case 3:
                    degrees = 270;
            }

            int result;
            if (info.facing == 1) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }

            return result;
        }
    }

    private Camera.Size getOptimalPreviewSize() {
        if (this.mCameraWrapper == null) {
            return null;
        } else {
            List<Camera.Size> sizes = this.mCameraWrapper.mCamera.getParameters().getSupportedPreviewSizes();
            int w = this.getWidth();
            int h = this.getHeight();
            if (DisplayUtils.getScreenOrientation(this.getContext()) == 1) {
                int portraitWidth = h;
                h = w;
                w = portraitWidth;
            }

            double targetRatio = (double)w / (double)h;
            if (sizes == null) {
                return null;
            } else {
                Camera.Size optimalSize = null;
                double minDiff = 1.7976931348623157E308D;
                int targetHeight = h;
                Iterator var10 = sizes.iterator();

                Camera.Size size;
                while(var10.hasNext()) {
                    size = (Camera.Size)var10.next();
                    double ratio = (double)size.width / (double)size.height;
                    if (Math.abs(ratio - targetRatio) <= (double)this.mAspectTolerance && (double)Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = (double)Math.abs(size.height - targetHeight);
                    }
                }

                if (optimalSize == null) {
                    minDiff = 1.7976931348623157E308D;
                    var10 = sizes.iterator();

                    while(var10.hasNext()) {
                        size = (Camera.Size)var10.next();
                        if ((double)Math.abs(size.height - targetHeight) < minDiff) {
                            optimalSize = size;
                            minDiff = (double)Math.abs(size.height - targetHeight);
                        }
                    }
                }

                return optimalSize;
            }
        }
    }

    public void setAutoFocus(boolean state) {
        if (this.mCameraWrapper != null && this.mPreviewing) {
            if (state == this.mAutoFocus) {
                return;
            }

            this.mAutoFocus = state;
            if (this.mAutoFocus) {
                if (this.mSurfaceCreated) {
                    Log.v("CameraPreview", "Starting autofocus");
                    this.safeAutoFocus();
                } else {
                    this.scheduleAutoFocus();
                }
            } else {
                Log.v("CameraPreview", "Cancelling autofocus");
                this.mCameraWrapper.mCamera.cancelAutoFocus();
            }
        }

    }

    private void scheduleAutoFocus() {
        this.mAutoFocusHandler.postDelayed(this.doAutoFocus, 1000L);
    }
}

