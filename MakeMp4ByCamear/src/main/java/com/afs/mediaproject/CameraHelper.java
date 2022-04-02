package com.afs.mediaproject;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.util.List;

public class CameraHelper implements Camera.PreviewCallback {

    public static final String TAG = "CameraHelper";
    private int width;
    private int height;
    private int mCameraId;
    private Camera mCamera;
    private Camera.PreviewCallback mPreviewCallback;
    private SurfaceTexture mSurfaceTexture;
    byte[] i420;
    byte[] buffer;

    public CameraHelper(int width, int height) {
        this.width = width;
        this.height = height;
        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        stopPreview();
        startPreview(mSurfaceTexture);
    }

    public int getCameraId() {
        return mCameraId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * 开始相机预览
     *
     * @param surfaceTexture
     */
    public void startPreview(SurfaceTexture surfaceTexture) {
        stopPreview();
        try {
            mSurfaceTexture = surfaceTexture;
            //获取Camera对象
            mCamera = Camera.open(mCameraId);
            //获取Camera的属性
            Camera.Parameters parameters = mCamera.getParameters();
            //设置预览数据格式为NV21
            parameters.setPreviewFormat(ImageFormat.NV21);
            boolean isSupportSize = false;
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
                if (supportedPreviewSize.width == width && supportedPreviewSize.height == height) {
                    isSupportSize = true;
                    break;
                }
            }
            if (!isSupportSize) {
                Camera.Size size = supportedPreviewSizes.get(0);
                width = size.width;
                height = size.height;
            }
            //设置摄像头宽高
            parameters.setPreviewSize(width, height);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            mCamera.setDisplayOrientation(90);
            //设置摄像头、图像传感器的角度、方向
            mCamera.setParameters(parameters);
            buffer = new byte[width * height * 3 / 2];
            //数据缓存区
            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallbackWithBuffer(this);
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 停止相机预览
     */
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.toString();
            mCamera.release();
            mCamera = null;
        }
    }


    public void setPreviewCallback(Camera.PreviewCallback mPreviewCallback) {
        this.mPreviewCallback = mPreviewCallback;
        i420 = new byte[width * height * 3 / 2];
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mPreviewCallback != null) {
            NV21ToI420(data, i420, width, height);
            mPreviewCallback.onPreviewFrame(i420, camera);
        }
        mCamera.addCallbackBuffer(buffer);
    }

    /**
     * @param nv21   YYYYYYYYYYYYYYYY VUVUVUVU
     * @param i420   YYYYYYYYYYYYYYYY UUUUVVVV
     * @param width  图像宽度
     * @param height 图像高度
     */
    public void NV21ToI420(byte[] nv21, byte[] i420, int width, int height) {
        int frameSize = width * height;
        System.arraycopy(nv21, 0, i420, 0, frameSize);
        int index = frameSize;
        for (int i = frameSize; i < nv21.length; i = i + 2) {
            //U
            i420[index++] = nv21[i + 1];
        }
        for (int i = frameSize; i < nv21.length; i = i + 2) {
            //V
            i420[index++] = nv21[i];
        }
    }


    /**
     * @param nv21   YYYYYYYYYYYYYYYY VUVUVUVU
     * @param nv12   YYYYYYYYYYYYYYYY UVUVUVUV
     * @param width  图像宽度
     * @param height 图像高度
     */
    public static void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {
        if (nv21 == null || nv12 == null) {
            return;
        }
        int frameSize = width * height;
        System.arraycopy(nv21, 0, nv12, 0, frameSize);
        for (int i = 0; i < frameSize / 2; i += 2) {
            // U
            nv12[frameSize + i * 2] = nv21[frameSize + i + 1];
            // V
            nv12[frameSize + i * 2 + 1] = nv21[frameSize + i];
        }
    }


}
