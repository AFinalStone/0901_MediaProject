package com.afs.androidcamera1;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.List;

public class Camera1Impl implements ICamera {
    public static final String TAG = "Camera1Impl======";

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

    public Camera1Impl(Activity activity) {
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

    /**
     * 开始预览
     *
     * @param surfaceHolder
     */
    public void startPreview(SurfaceHolder surfaceHolder) {
        try {
            mSurfaceHolder = surfaceHolder;
            //设置实时预览
            mCamera.setPreviewDisplay(mSurfaceHolder);
            //这里可以设置监听预览数据的回调，一般返回的是YUV格式中类型为NV21的图像数据
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    Log.d(TAG, "预览图像数据长度: " + data.length);
                }
            });
            //Orientation
            // 设置相机方向
            mCamera.setDisplayOrientation(getCameraDisplayOrientation());
            //开始预览
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拍照
     */
    public void takePicture() {
        if (null != mCamera) {
            mCamera.takePicture(new Camera.ShutterCallback() {
                @Override
                public void onShutter() {
                    Log.d(TAG, "onShutter快门按下后的回调=====");
                }
            }, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Log.d(TAG, "onPictureTaken回调=====raw图像数据长度：" + (data == null ? 0 : data.length));
                }
            }, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(final byte[] data, Camera camera) {
                    mCamera.startPreview();
                    Log.d(TAG, "onPictureTaken回调=====jpeg图像生成以后的回调" + (data == null ? 0 : data.length));
                }
            });
        }
    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    /**
     * 释放相机资源
     */
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.stopFaceDetection();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 设置相机参数
     */
    public void setCameraParameters() {
        if (null == mCamera) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21); //default

        if (isSupportFocus(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (isSupportFocus(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        //设置预览图片大小
//        parameters.setPreviewSize(int width, int height);
        //设置图片大小
//        parameters.setPictureSize(int width, int height);

        mCamera.setParameters(parameters);
    }

    private boolean isSupportFocus(String focusModel) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            List<String> supportList = parameters.getSupportedFocusModes();
            return supportList.contains(focusModel);
        }
        return false;
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
