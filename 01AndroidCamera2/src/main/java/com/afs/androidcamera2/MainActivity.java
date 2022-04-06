package com.afs.androidcamera2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity======";

    private ICamera mCameraHelper;
    private SurfaceView mSurfaceView;
    private Button mBtnOpenCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
//        mCameraHelper = new CameraHelper(this);
        openCamera();
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(perms, 200);
        }
    }

    private void initView() {
        mSurfaceView = findViewById(R.id.camera_preview);
        mBtnOpenCamera = findViewById(R.id.btn_open_camera);
        findViewById(R.id.btn_open_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mBtnOpenCamera.getText().toString();
                if ("关闭相机".equals(text)) {
                    mBtnOpenCamera.setText("打开相机");
                    mSurfaceView.setVisibility(View.INVISIBLE);
                } else {
                    mBtnOpenCamera.setText("关闭相机");
                    mSurfaceView.setVisibility(View.VISIBLE);

                }
            }
        });
        findViewById(R.id.btn_switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurfaceView.setVisibility(View.INVISIBLE);
                mCameraHelper.switchCamera();
                mSurfaceView.setVisibility(View.VISIBLE);
            }
        });
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated: ");
                mCameraHelper.startPreview(holder);//让mSurfaceView和Camera产生关联
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged: ");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed: ");
                mCameraHelper.stopPreview();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            openCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraHelper.releaseCamera();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mCameraHelper.openCamera(mCameraHelper.getFrontCameraId());
        }
    }
}


