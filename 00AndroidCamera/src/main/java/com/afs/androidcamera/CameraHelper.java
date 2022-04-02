package com.afs.androidcamera;

import android.app.Activity;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;

public class CameraHelper {

    private Activity mActivity;
    //后置摄像头信息
    private int mBackCameraId;
    private Camera.CameraInfo mBackCameraInfo;
    //前置摄像头信息
    private int mFrontCameraId;
    private Camera.CameraInfo mFrontCameraInfo;
    Camera mCamera;
    int mCameraId;
    private Camera.CameraInfo mCameraInfo;
    private SurfaceHolder mSurfaceHolder;

    public CameraHelper(Activity activity) {
        this.mActivity = activity;
        initCameraInfo();
    }

    public int getBackCameraId() {
        return mBackCameraId;
    }

    public int getFrontCameraId() {
        return mFrontCameraId;
    }

    /**
     * 初始化相机信息
     */
    private void initCameraInfo() {
        int numberOfCameras = Camera.getNumberOfCameras();// 获取摄像头个数
        for (int cameraId = 0; cameraId < numberOfCameras; cameraId++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                // 后置摄像头信息
                mBackCameraId = cameraId;
                mBackCameraInfo = cameraInfo;
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 前置摄像头信息
                mFrontCameraId = cameraId;
                mFrontCameraInfo = cameraInfo;
            }
        }
    }

    /**
     * 打开相机
     *
     * @param cameraId
     */
    public void openCamera(int cameraId) {
        //打开相机
        mCamera = Camera.open(cameraId);
        mCameraId = cameraId;
        mCameraInfo = cameraId == mFrontCameraId ? mFrontCameraInfo : mBackCameraInfo;
    }

    /**
     * 切换相机
     */
    public void switchCamera() {
        if (mCamera == null) {
            return;
        }
        releaseCamera();
        if (mCameraId == mFrontCameraId) {
            openCamera(mBackCameraId);
        } else {
            openCamera(mFrontCameraId);
        }
    }

    public void startPreview(SurfaceHolder surfaceHolder) {
        try {
            mSurfaceHolder = surfaceHolder;
            //设置实时预览
            mCamera.setPreviewDisplay(mSurfaceHolder);
            //Orientation
            // 设置相机方向
            mCamera.setDisplayOrientation(getCameraDisplayOrientation());
            //开始预览
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private int getCameraDisplayOrientation() {
        int roration = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        // 屏幕显示方向角度(相对局部坐标Y轴正方向夹角)
        int degrees = 0;
        switch (roration) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (mCameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (mCameraInfo.orientation - degrees + 360) % 360;
        }
        // 相机需要校正的角度
        return result;
    }


}
