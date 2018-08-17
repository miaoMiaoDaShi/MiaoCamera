package com.yalantis.ucrop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lfy.ucrop.R;

import java.io.File;
import java.io.IOException;

public class ShowPhotoActivity extends AppCompatActivity {
    private ImageView iv,iv_cancel,iv_choice,iv_show;
    private float rotation=90;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ucrop_activity_show_photo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.BLACK);
            }
        }

        iv=findViewById(R.id.iv_roat);
        iv_cancel=findViewById(R.id.iv_cancel);
        iv_choice=findViewById(R.id.iv_choice);
        iv_show=findViewById(R.id.iv_show);

         final Uri uri = getIntent().getData();
        if (uri != null) {
            iv_show.setImageURI(uri);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iv_show.animate().rotation(rotation);
                        rotation+=90;
                    }
                });

                iv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                iv_choice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                 cutImage(uri,getTempImage(ShowPhotoActivity.this));

                    }
                });

        }
    }

    /**
     * 裁剪图片方法实现
     */
    private void cutImage(Uri uri,File path) {
        if (uri == null) {
            return;
        }
        UCrop.Options options = new UCrop.Options();
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //是否隐藏底部容器，默认显示
        options.setHideBottomControls(true);//不显示
        //是否能调整裁剪框
        options.setFreeStyleCropEnabled(false);
        //设置圆形裁剪图
        options.setCircleDimmedLayer(true);
        //隐藏方框
        options.setShowCropFrame(false);
        //设置竖线数量
        options.setCropGridColumnCount(0);
        //设置横线的数量
        options.setCropGridRowCount(0);
        UCrop.of(uri, Uri.fromFile(getTempImage(this)))
                .withAspectRatio(1, 1)
                .withMaxResultSize(340, 340)
                .withOptions(options)
                .start(this);
    }

    public File getTempImage(Context context) {
        File file=null;
        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {
            Long l = System.currentTimeMillis();
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), l+".jpg");

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);

            setResult(UCrop.REQUEST_CROP, new Intent()
                    .putExtra(UCrop.EXTRA_OUTPUT_URI, resultUri)
                    .putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, data.getStringExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO))
                    .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_WIDTH, data.getStringExtra(UCrop.EXTRA_OUTPUT_IMAGE_WIDTH))
                    .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT, data.getStringExtra(UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT))
                    .putExtra(UCrop.EXTRA_OUTPUT_OFFSET_X, data.getStringExtra(UCrop.EXTRA_OUTPUT_OFFSET_X))
                    .putExtra(UCrop.EXTRA_OUTPUT_OFFSET_Y, data.getStringExtra(UCrop.EXTRA_OUTPUT_OFFSET_Y))
            );
        }else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            setResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, cropError));
        }
        finish();
    }
}
