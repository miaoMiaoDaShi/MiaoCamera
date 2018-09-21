package com.miaomaio.miaocameralibrary;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/24
 * Description :
 */
public class TakePhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TakePhotoActivity";
    private ImageView mIvClose;
    private ImageView mIvConfirm;
    private CameraView mCameraView;
    private ImageView mIvChangeFlash;
    private Handler mBackgroundHandler;

    private static final int REQUEST_PERMISSION = 1;


    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };
    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        mCameraView = (CameraView) findViewById(R.id.mCameraView);
        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
            mCameraView.setAutoFocus(true);
        }


        mIvClose = findViewById(R.id.mIvClose);
        mIvConfirm = findViewById(R.id.mIvConfirm);
        mIvChangeFlash = findViewById(R.id.mIvChangeFlash);

        mIvClose.setOnClickListener(this);
        mIvConfirm.setOnClickListener(this);
        mIvChangeFlash.setOnClickListener(this);
    }

    private int mCurrentFlash;

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.mIvClose) {
            finish();
        } else if (id == R.id.mIvConfirm) {
            mIvConfirm.setEnabled(false);
            if (mCameraView != null) {
                mCameraView.takePicture();
            }
        } else if (id == R.id.mIvChangeFlash) {
            if (mCameraView != null) {
                mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                mIvChangeFlash.setImageResource(FLASH_ICONS[mCurrentFlash]);
                mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (permissions.length != 2 || grantResults.length != 2) {
                    throw new RuntimeException("Error on requesting  permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "cannot do anything without permission",
                            Toast.LENGTH_SHORT).show();
                }
                // No need to start camera here; it is handled by onResume
                break;
        }
    }


    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "onPictureTaken " + data.length);

            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                            String.format("%s.jpg", System.currentTimeMillis()));

                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                        os.write(data);
                        os.close();

                        final Message message = Message.obtain();
                        message.obj = file.getAbsolutePath();
                        message.what = WHAT_SUCCESS;
                        mBackgroundHandler.sendMessage(message);
                    } catch (IOException e) {
                        mBackgroundHandler.sendEmptyMessage(WHAT_FAILED);
                        Log.w(TAG, "Cannot write to " + file, e);
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }
                }
            });
        }

    };


    private final int WHAT_SUCCESS = 0x10;
    private final int WHAT_FAILED = 0x11;

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case WHAT_SUCCESS:
                            final Intent intent = new Intent(TakePhotoActivity.this, ConfirmImageActivity.class);
                            intent.putExtra("imagePath", ((String)msg.obj));
                            startActivityForResult(intent, 0x10);
                            break;
                        case WHAT_FAILED:
                            Intent errorIntent = new Intent();
                            setResult(Activity.RESULT_OK, errorIntent);
                            finish();
                            break;
                    }
                }
            };
        }
        return mBackgroundHandler;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIvConfirm.setEnabled(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x10 && resultCode == ConfirmImageActivity.RESULT_CLOSE) {//取消选择
            finish();
        } else if (requestCode == 0x10 && resultCode == ConfirmImageActivity.RESULT_CONFIRM) {//确认选择
            setResult(Activity.RESULT_OK, data);
            finish();
        } else if (requestCode == 0x10 && resultCode == ConfirmImageActivity.RESULT_RESET) {//从新选择

        }
    }
}
