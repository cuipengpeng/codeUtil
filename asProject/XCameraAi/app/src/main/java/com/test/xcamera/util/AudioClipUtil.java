package com.test.xcamera.util;

import android.annotation.TargetApi;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/3/26
 * e-mail zhouxuecheng1991@163.com
 * <p>
 * 音频裁剪
 */

public class AudioClipUtil {
    //适当的调整SAMPLE_SIZE可以更加精确的裁剪音乐
    private static final int SAMPLE_SIZE = 1024 * 200;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean clip(String inputPath, String outputPath, long startClipTime, long endClipTime) {
        MediaExtractor extractor = null;
        BufferedOutputStream outputStream = null;
        try {
            extractor = new MediaExtractor();
            extractor.setDataSource(inputPath);
            int track = getAudioTrack(extractor);
            if (track < 0) {
                return false;
            }
            //选择音频轨道
            extractor.selectTrack(track);
            outputStream = new BufferedOutputStream(
                    new FileOutputStream(outputPath), SAMPLE_SIZE);
            startClipTime = startClipTime * 1000;
            endClipTime = endClipTime * 1000;
            //跳至开始裁剪位置
            extractor.seekTo(startClipTime, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
            while (true) {
                ByteBuffer buffer = ByteBuffer.allocate(SAMPLE_SIZE);
                int sampleSize = extractor.readSampleData(buffer, 0);
                long timeStamp = extractor.getSampleTime();
                // >= 1000000是要裁剪停止和指定的裁剪结尾不小于1秒，否则可能产生需要9秒音频
                //裁剪到只有8.6秒，大多数音乐播放器是向下取整，这样对于播放器变成了8秒，
                // 所以要裁剪比9秒多一秒的边界
                if (timeStamp > endClipTime && timeStamp - endClipTime >= 1000000) {
                    break;
                }
                if (sampleSize <= 0) {
                    break;
                }
                byte[] buf = new byte[sampleSize];
                buffer.get(buf, 0, sampleSize);
                //写入文件
                outputStream.write(buf);
                //音轨数据往前读
                extractor.advance();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (extractor != null) {
                extractor.release();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 获取音频数据轨道
     *
     * @param extractor
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static int getAudioTrack(MediaExtractor extractor) {
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio")) {
                return i;
            }
        }
        return -1;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void printMusicFormat(String musicPath) {
        try {
            MediaExtractor extractor = new MediaExtractor();
            extractor.setDataSource(musicPath);
            MediaFormat format = extractor.getTrackFormat(getAudioTrack(extractor));
            Log.i("music", "码率：" + format.getInteger(MediaFormat.KEY_BIT_RATE));
            Log.i("music", "轨道数:" + format.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
            Log.i("music", "采样率：" + format.getInteger(MediaFormat.KEY_SAMPLE_RATE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
