package com.afs.androidmedia;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements Camera.PreviewCallback {

    private Button mBtnRecord;
    private TextureView mTextureView;
    private CameraHelper mCameraHelper;
    private VideoCodec mVideoCodec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraHelper = new CameraHelper(640, 480);
        mCameraHelper.setPreviewCallback(this);
        mVideoCodec = new VideoCodec();
        mBtnRecord = findViewById(R.id.btn_record);
        mTextureView = findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                mCameraHelper.startPreview(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                mCameraHelper.stopPreview();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

            }
        });
        mBtnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoCodec.isRecording()) {
                    mBtnRecord.setText("开始录制");
                    mVideoCodec.stopRecording();
                } else {
                    mBtnRecord.setText("停止录制");
//                    String mp4Path = Environment.getExternalStorageState() + File.separator + "AAAAA.mp4";
                    String mp4Path = "/sdcard/AAAAA.mp4";
                    mVideoCodec.startRecording(mp4Path, mCameraHelper.getWidth(), mCameraHelper.getHeight(), 90);
                }
            }
        });
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(perms, 200);
        }

        mVideoCodec.getSupportType();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mVideoCodec.queueEncode(data);
    }

}