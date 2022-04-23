package com.example.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity======";

    ImageView imageView01;
    ImageView imageView02;
    Bitmap[] bitmap = new Bitmap[1000];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView01 = findViewById(R.id.image01);
        imageView02 = findViewById(R.id.image02);
        findViewById(R.id.btn_load_image_shake).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < 1000; i++) {
                    bitmap[i] = BitmapFactory.decodeResource(getResources(), R.mipmap.img_1);
                }

            }
        });
        findViewById(R.id.btn_load_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test01(R.mipmap.img_1);
                test01(R.mipmap.img_2);
                test01(R.mipmap.img_2_compress);

            }
        });
        findViewById(R.id.btn_load_image_compress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test02(R.mipmap.img_1);
                test02(R.mipmap.img_2);
                test02(R.mipmap.img_2_compress);

            }
        });
//        imageView01.setImageResource(R.mipmap.img_1);
//        imageView02.setImageResource(R.mipmap.img_2);
    }

    private void test01(int bitmapResId) {
        Log.d(TAG, "test01---------------------------------------------------");
        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), bitmapResId, options);
        Log.d(TAG, "options.outWidth=" + options.outWidth + " options.outHeight=" + options.outHeight + " options.outMimeType=" + options.outMimeType + " options.inPreferredConfig=" + options.inPreferredConfig);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bitmapResId);
        Log.d(TAG, "bitmap.getWidth()=" + bitmap.getWidth() + " bitmap.getHeight()=" + bitmap.getHeight() + " bitmap.getConfig=" + bitmap.getConfig());
        //获取图片的字节长度
        Log.d(TAG, "图片的字节长度=" + String.valueOf(bitmap.getByteCount()));
        //获取图片的字节长度
        byte[] bytes = BitmapUtil.bitmap2Bytes01(bitmap);
        Log.d(TAG, "图片的字节长度_bytes.length=" + String.valueOf(bytes.length));
        //获取JPEG压缩的图片的字节长度
        byte[] bytes_jpeg = BitmapUtil.bitmap2Bytes02(bitmap, Bitmap.CompressFormat.JPEG);
        Log.d(TAG, "图片的字节长度_JPEG=" + String.valueOf(bytes_jpeg.length));
        //获取JPEG压缩的图片的字节长度
        byte[] bytes_png = BitmapUtil.bitmap2Bytes02(bitmap, Bitmap.CompressFormat.PNG);
        Log.d(TAG, "图片的字节长度_PNG=" + String.valueOf(bytes_png.length));
        //获取WEBP压缩的图片的字节长度
        byte[] bytes_webp = BitmapUtil.bitmap2Bytes02(bitmap, Bitmap.CompressFormat.WEBP);
        Log.d(TAG, "图片的字节长度_WEBP=" + String.valueOf(bytes_webp.length));
    }

    private void test02(int bitmapResId) {
        Log.d(TAG, "test02-----------------------");
        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeResource(getResources(), bitmapResId, options);
        Log.d(TAG, "width=" + options.outWidth + " height=" + options.outHeight + " outMimeType=" + options.outMimeType + " inPreferredConfig=" + options.inPreferredConfig);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inMutable = true;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bitmapResId, options);
        //获取图片的字节长度
        Log.d(TAG, "图片的字节长度=" + String.valueOf(bitmap.getByteCount()));
        //获取图片的字节长度
        byte[] bytes = BitmapUtil.bitmap2Bytes01(bitmap);
        Log.d(TAG, "图片的字节长度_bytes.length=" + String.valueOf(bytes.length));
        //获取JPEG压缩的图片的字节长度
        byte[] bytes_jpeg = BitmapUtil.bitmap2Bytes02(bitmap, Bitmap.CompressFormat.JPEG);
        Log.d(TAG, "图片的字节长度_JPEG=" + String.valueOf(bytes_jpeg.length));
        //获取JPEG压缩的图片的字节长度
        byte[] bytes_png = BitmapUtil.bitmap2Bytes02(bitmap, Bitmap.CompressFormat.PNG);
        Log.d(TAG, "图片的字节长度_PNG=" + String.valueOf(bytes_png.length));
        //获取WEBP压缩的图片的字节长度
        byte[] bytes_webp = BitmapUtil.bitmap2Bytes02(bitmap, Bitmap.CompressFormat.WEBP);
        Log.d(TAG, "图片的字节长度_WEBP=" + String.valueOf(bytes_webp.length));
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888
    }
}