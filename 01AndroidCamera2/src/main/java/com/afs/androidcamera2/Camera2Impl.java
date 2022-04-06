package com.afs.androidcamera2;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.view.SurfaceHolder;

import java.util.concurrent.TimeUnit;

public class Camera2Impl implements ICamera {

    public static final String TAG = "Camera2Impl======";

    private Activity mActivity;
    private CameraManager mCameraManager;

    //后置摄像头信息
    private int mBackCameraId;
    //前置摄像头信息
    private int mFrontCameraId;

    private ImageReader mImageReader;

    public Camera2Impl(Activity activity) {
        mActivity = activity;
        mCameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        initCameraInfo();
    }

    private void initCameraInfo() {
        try {
            for (String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (CameraCharacteristics.LENS_FACING_BACK == facing) {
                    // 后置摄像头信息
                    mBackCameraId = facing;
                } else if (CameraCharacteristics.LENS_FACING_FRONT == facing) {
                    // 前置摄像头信息
                    mFrontCameraId = facing;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getBackCameraId() {
        return 0;
    }

    @Override
    public int getFrontCameraId() {
        return 0;
    }

    @Override
    public void openCamera(int cameraId) {

    }

    @Override
    public void switchCamera() {

    }

    @Override
    public void startPreview(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void stopPreview() {

    }

    @Override
    public void releaseCamera() {

    }

    @Override
    public void setCameraParameters() {

    }

    private void isHardwareLevelSupported(int requiredLevel) {
        int[] soutedLevels = {CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3};

//        CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
//        Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
//
//        int deviceLevel = CameraCharacteristics.

    }
}
