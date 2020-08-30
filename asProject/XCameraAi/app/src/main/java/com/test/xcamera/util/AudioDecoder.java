package com.test.xcamera.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import com.test.xcamera.bean.AudioFrameInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by zll on 2019/11/8.
 */

public class AudioDecoder {
    private static final String TAG = "AudioDecoder";
    private static final int KEY_SAMPLE_RATE = 48000;
    private Worker mWorker;
    private byte[] mPcmData;
    private ArrayList<AudioFrameInfo> mAudioFrameInfos;

    public void start() {
        mAudioFrameInfos = new ArrayList<>();
        if (mWorker == null) {
            mWorker = new Worker();
            mWorker.setRunning(true);
            mWorker.start();
        }
    }

    public void stop() {
        if (mWorker != null) {
            mWorker.setRunning(false);
            mWorker.release();
            mWorker = null;
        }
        if (mAudioFrameInfos != null) {
            mAudioFrameInfos.clear();
        }
    }

    public void receiveData(AudioFrameInfo audioFrameInfo) {
        if (mWorker != null && mWorker.isRunning) {
            mAudioFrameInfos.add(audioFrameInfo);
        }
    }

    private class Worker extends Thread {
        private boolean isRunning = false;
        private AudioTrack mPlayer;
        private MediaCodec mDecoder;
        MediaCodec.BufferInfo mBufferInfo;

        public void setRunning(boolean run) {
            isRunning = run;
        }

        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void run() {
            super.run();
            if (!prepare()) {
                isRunning = false;
                Log.d(TAG, "音频解码器初始化失败");
            }
            while (isRunning) {
                decode();
            }
            release();
        }

        /**
         * 等待客户端连接，初始化解码器
         *
         * @return初始化失败返回false，成功返回true
         */
        public boolean prepare() {
            //等待客户端
            mBufferInfo = new MediaCodec.BufferInfo();
            int bufferSize = AudioTrack.getMinBufferSize(KEY_SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
            mPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, KEY_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
            mPlayer.play();
            try {
                String mine = "audio/mp4a-latm";
                mDecoder = MediaCodec.createDecoderByType(mine);

                MediaFormat format = new MediaFormat();
                format.setString(MediaFormat.KEY_MIME, mine);
                format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
                format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 44100);
                format.setInteger(MediaFormat.KEY_BIT_RATE, AudioFormat.ENCODING_PCM_16BIT);
                format.setInteger(MediaFormat.KEY_IS_ADTS, 1);
                format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
//                byte[] bytes = new byte[]{
//                        csd_0[7], csd_0[8]
//                };
                byte[] bytes = new byte[]{(byte) 0x11, (byte) 0x90};
                ByteBuffer csd_0 = ByteBuffer.wrap(bytes);
                format.setByteBuffer("csd-0", csd_0);
                mDecoder.configure(format, null, null, 0);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            if (mDecoder == null) {
                Log.e(TAG, "createmediaDecodefailed");
                return false;
            }
            mDecoder.start();
            return true;
        }

        /**
         * aac解码+播放
         */
        public void decode() {
            while (isRunning) {
                if (mAudioFrameInfos.isEmpty()) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                //获取可用的inputBuffer-1代表一直等待，0表示不等待建议-1,避免丢帧
                AudioFrameInfo frame = mAudioFrameInfos.remove(0);
                int inputIndex = mDecoder.dequeueInputBuffer(frame.getmTimeMs());
                if (inputIndex >= 0) {
                    ByteBuffer inputBuffer = mDecoder.getInputBuffer(inputIndex);
                    if (inputBuffer == null) {
                        return;
                    }
                    inputBuffer.clear();
//                    if (frame == null) {
//                        mDecoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//                        isEOF = true;
//                        isRunning = false;
//                    } else {
                    inputBuffer.put(frame.getmAuidoData(), 0, frame.getmAuidoData().length);
                    mDecoder.queueInputBuffer(inputIndex, 0, frame.getmAuidoData().length, 0, 0);
//                    }
                } else {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                int outputIndex = mDecoder.dequeueOutputBuffer(mBufferInfo, 0);
                Log.d(TAG, "audio decoding .....");
                ByteBuffer outputBuffer;
                //每次解码完成的数据不一定能一次吐出 所以用while循环，保证解码器吐出所有数据
                while (outputIndex >= 0) {
                    outputBuffer = mDecoder.getOutputBuffer(outputIndex);
                    if (mPcmData == null || mPcmData.length < mBufferInfo.size) {
                        mPcmData = new byte[mBufferInfo.size];
                    }
                    // chunkPCM = new byte[mBufferInfo.size];
                    outputBuffer.get(mPcmData, 0, mBufferInfo.size);
                    outputBuffer.clear();//数据取出后一定记得清空此Buffer MediaCodec是循环使用这些Buffer的，不清空下次会得到同样的数据
                    //播放音乐
                    mPlayer.write(mPcmData, 0, mBufferInfo.size);
                    mDecoder.releaseOutputBuffer(outputIndex, false);//此操作一定要做，不然MediaCodec用完所有的Buffer后 将不能向外输出数据
                    outputIndex = mDecoder.dequeueOutputBuffer(mBufferInfo, 0);//再次获取数据，如果没有数据输出则outputIndex=-1 循环结束
                }
            }
        }

        /**
         * 释放资源
         */

        public void release() {
            try {

            } catch (Exception e) {

                if (mDecoder != null) {
                }
                mDecoder.stop();
                mDecoder.release();
            }
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }

        }
    }
}
