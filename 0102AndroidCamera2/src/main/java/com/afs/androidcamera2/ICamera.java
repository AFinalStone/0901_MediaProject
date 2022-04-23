package com.afs.androidcamera2;

import android.view.SurfaceHolder;

public interface ICamera {

    int getBackCameraId();

    int getFrontCameraId();

    /**
     * 打开相机
     *
     * @param cameraId
     */
    void openCamera(int cameraId);

    /**
     * 切换相机
     */
    void switchCamera();

    /**
     * 开始预览
     *
     * @param surfaceHolder
     */
    void startPreview(SurfaceHolder surfaceHolder);

    /**
     * 停止预览
     */
    void stopPreview();

    /**
     * 释放相机资源
     */
    void releaseCamera();

    /**
     * 设置相机参数
     */
    void setCameraParameters();


}
