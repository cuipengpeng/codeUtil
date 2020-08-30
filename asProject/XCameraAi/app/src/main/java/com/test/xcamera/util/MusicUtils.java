package com.test.xcamera.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.test.xcamera.phonealbum.bean.MusicBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 音乐工具类,扫描系统里面的音频文件，返回一个list集合
 */
public class MusicUtils {
    public static List<MusicBean> getLocalMusicList(Context context) {
        List<MusicBean> list = new ArrayList<MusicBean>();

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MusicBean song = new MusicBean();
                //歌曲
                song.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                //歌手
                song.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                //路径
                song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                //时长
                song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                if (song.getSize() > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (song.getName().contains("-")) {
                        String[] str = song.getName().split("-");
                        song.setSinger(str[0].trim());
                        song.setName(str[1].trim());
                    }
                    //设置拼音首字母
                    list.add(song);
                }

            }

            // 释放资源
            cursor.close();
        }

        return list;
    }

    /**
     * 定义一个方法用来格式化获取到的时间
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }

    public static String formatTimeStrWithUs(long us) {
        int second = (int) (us / 1000000.0);
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        return hh > 0 ? String.format("%02d:%02d:%02d", hh, mm, ss) : String.format("%02d:%02d", mm, ss);
    }
}