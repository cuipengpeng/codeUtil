package com.caishi.chaoge.utils;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import java.io.File;
import java.nio.ByteBuffer;

/**
 * 音频相关的操作类
 */

public class AudioCodec {
    final static int TIMEOUT_USEC = 0;
    private static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 从视频文件中分离出音频，并保存到本地
     */
    public static void getAudioFromVideo(String videoPath, final String savePath, final AudioDecodeListener listener) {
        final MediaExtractor extractor = new MediaExtractor();
        int audioTrack = -1;
        boolean hasAudio = false;
        try {
            File file = new File(savePath);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
            }
            final String audioSavePath = savePath + System.currentTimeMillis() + ".mp3";
            extractor.setDataSource(videoPath);
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat trackFormat = extractor.getTrackFormat(i);
                String mime = trackFormat.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio/")) {
                    audioTrack = i;
                    hasAudio = true;
                    break;
                }
            }
            if (hasAudio) {
                extractor.selectTrack(audioTrack);
                final int finalAudioTrack = audioTrack;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MediaMuxer mediaMuxer = new MediaMuxer(audioSavePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                            MediaFormat trackFormat = extractor.getTrackFormat(finalAudioTrack);
                            int writeAudioIndex = mediaMuxer.addTrack(trackFormat);
                            mediaMuxer.start();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(trackFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE));
                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                            extractor.readSampleData(byteBuffer, 0);
                            if (extractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                                extractor.advance();
                            }
                            while (true) {
                                int readSampleSize = extractor.readSampleData(byteBuffer, 0);
                                Log.e("hero", "---读取音频数据，当前读取到的大小-----：：：" + readSampleSize);
                                if (readSampleSize < 0) {
                                    break;
                                }

                                bufferInfo.size = readSampleSize;
                                bufferInfo.flags = extractor.getSampleFlags();
                                bufferInfo.offset = 0;
                                bufferInfo.presentationTimeUs = extractor.getSampleTime();
                                Log.e("hero", "----写入音频数据---当前的时间戳：：：" + extractor.getSampleTime());

                                mediaMuxer.writeSampleData(writeAudioIndex, byteBuffer, bufferInfo);
                                extractor.advance();//移动到下一帧
                            }
                            mediaMuxer.release();
                            extractor.release();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) {
                                        listener.decodeOver(audioSavePath);
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) {
                                        listener.decodeFail();
                                    }
                                }
                            });
                        }
                    }
                }).start();
            } else {
                Log.e("hero", " extractor failed !!!! 没有音频信道");
                if (listener != null) {
                    listener.decodeFail();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("hero", " extractor failed !!!!");
            if (listener != null) {
                listener.decodeFail();
            }
        }
    }



    public interface AudioDecodeListener {
        void decodeOver(String audioSavePath);

        void decodeFail();
    }
}
