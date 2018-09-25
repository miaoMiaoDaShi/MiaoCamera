package com.google.android.cameraview;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;


/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/6
 * Description :
 */
class CameraRequest {
    CameraRequest(Camera scanner, int requestCode) {
        Activity activity = scanner.getActivity();
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, TakePhotoActivity.class);

        Fragment fragment = scanner.getFragment();
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

}
