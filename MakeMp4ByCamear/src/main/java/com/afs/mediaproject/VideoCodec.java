package com.afs.mediaproject;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoCodec {

    private boolean isRecording;
    private Handler mHandler;
    private MediaCodec mMediaCodec;
    private MediaMuxer mMediaMuxer;
    private int mVideoTrack;

    public void startRecording(String path, int width, int height, int degress) {
        try {
            MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
            //图片颜色采样格式
//            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            //码率 1Mbps=128kb/s*8=1048576
            format.setInteger(MediaFormat.KEY_BIT_RATE, 1048_576);
            //帧率 fps 25
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
            //关键帧间隔 I帧间隔
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            //创建mediacodec
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
//            mMediaCodec = MediaCodec.createByCodecName("OMX.google.h264.encoder");
            mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();
            //混合编码器
            mMediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mMediaMuxer.setOrientationHint(degress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HandlerThread handlerThread = new HandlerThread("videoCodec");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        isRecording = true;
    }

    public void stopRecording() {
        isRecording = false;
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
        }
        if (mMediaMuxer != null) {
            mMediaMuxer.stop();
            mMediaMuxer.release();
            mMediaMuxer = null;
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void queueEncode(byte[] buffer) {
        if (!isRecording) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //拿到有空闲的输入缓存的区下标
                int inputBufferId = mMediaCodec.dequeueInputBuffer(-1);
                if (inputBufferId > 0) {
                    //有效的空的缓存区
                    ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputBufferId);
                    inputBuffer.clear();
                    inputBuffer.put(buffer, 0, buffer.length);
                    long presentationTime = System.nanoTime() / 1000;
                    mMediaCodec.queueInputBuffer(inputBufferId, 0, buffer.length, presentationTime, 0);
                }
                while (true) {
                    //获得输出缓冲区
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                    //得到成功编码后输出的outputBufferId
                    if (mMediaCodec == null) {
                        break;
                    }
                    int outputBufferId = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                    if (outputBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        //请稍后重试，我们直接退出
                        break;
                    } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        //输出格式发生了改变，第一次运行总会触发，所以可以在这里开启混合器
                        MediaFormat mediaFormat = mMediaCodec.getOutputFormat();
                        mVideoTrack = mMediaMuxer.addTrack(mediaFormat);
                        mMediaMuxer.start();
                    } else if (outputBufferId == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        //忽略不急
                    } else {
                        //如果当前buffer是配置信息，直接忽略
                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                            bufferInfo.size = 0;
                        }
                        //正常则encoderStatus获得缓冲区下标
                        ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(outputBufferId);
                        if (bufferInfo.size > 0) {
                            byte[] h264 = new byte[bufferInfo.size];
                            outputBuffer.get(h264);
                            //设置从哪里开始读取数据（读取出来就是编码的数据）
                            outputBuffer.position(bufferInfo.offset);
                            //设置能读数据的总长度
                            outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                            //写出MP4文件
                            mMediaMuxer.writeSampleData(mVideoTrack, outputBuffer, bufferInfo);
                        }
                        mMediaCodec.releaseOutputBuffer(outputBufferId, false);
                    }
                }
            }
        });
    }

    private static String TAG = "VideoCodec====";

    public static void getSupportType() {
        MediaCodecList allMediaCodecLists = new MediaCodecList(-1);
        MediaCodecInfo avcCodecInfo = null;
        for (MediaCodecInfo mediaCodecInfo : allMediaCodecLists.getCodecInfos()) {
            if (mediaCodecInfo.isEncoder()) {
                String[] supportTypes = mediaCodecInfo.getSupportedTypes();
                for (String supportType : supportTypes) {
                    if (supportType.equals(MediaFormat.MIMETYPE_VIDEO_AVC)) {
                        avcCodecInfo = mediaCodecInfo;
                        Log.d(TAG, "编码器名称:" + mediaCodecInfo.getName() + "  " + supportType);
                        MediaCodecInfo.CodecCapabilities codecCapabilities = avcCodecInfo.getCapabilitiesForType(MediaFormat.MIMETYPE_VIDEO_AVC);
                        int[] colorFormats = codecCapabilities.colorFormats;
                        for (int colorFormat : colorFormats) {
                            switch (colorFormat) {
                                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411Planar:
                                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411PackedPlanar:
                                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
                                    Log.d(TAG, "支持的格式::" + colorFormat);
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }
}
