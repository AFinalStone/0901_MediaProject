package com.example.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class BitmapUtil {

    /**
     * bitmap转字节码
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes01(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
        byte[] data = buffer.array(); //Get the bytes array of the bitmap
        return data;
    }

    /**
     * bitmap转字节码
     *
     * @param bitmap
     * @param compressFormat 压缩方式
     * @return
     */
    public static byte[] bitmap2Bytes02(Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

    /**
     * 字节码转bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }


    /**
     * Bitmap转换成YuvImage
     *
     * @param b
     * @return
     */
    public static YuvImage Bytes2Bimap(Bitmap b) {
        int bytes = b.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        b.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

        byte[] data = buffer.array(); //Get the bytes array of the bitmap
        YuvImage yuv = new YuvImage(data, ImageFormat.NV21, b.getWidth(), b.getHeight(), null);
        return yuv;
    }
}
