package com.miaomaio.miaocameralibrary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.miaomaio.miaocameralibrary.utils.DisplayView;

import java.io.File;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/24
 * Description :
 */
public class TakePhotoActivity extends AppCompatActivity implements View.OnClickListener, DisplayView.OnPictureListener {
    private DisplayView mDisplayView;
    private ImageView mIvClose;
    private ImageView mIvConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        mDisplayView = findViewById(R.id.mDisPlayView);
        mDisplayView.setOnPictureListener(this);


        mIvClose = findViewById(R.id.mIvClose);
        mIvConfirm = findViewById(R.id.mIvConfirm);

        mIvClose.setOnClickListener(this);
        mIvConfirm.setOnClickListener(this);
    }

    private boolean mIsTaked = false;

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.mIvClose) {
            finish();
        } else if (id == R.id.mIvConfirm) {
            mIvConfirm.setEnabled(false);
            mDisplayView.takePhoto();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIvConfirm.setEnabled(true);
        mDisplayView.startCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisplayView.stopCamera();
    }


    @Override
    public void onSuccess(File imageFile, String path, Bitmap bitmap) {
        final Intent intent = new Intent(this, ConfirmImageActivity.class);
        intent.putExtra("imagePath", path);
        startActivityForResult(intent, 0x10);
    }

    @Override
    public void onFailed(String e) {
        Toast.makeText(this, e, Toast.LENGTH_SHORT).show();
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
