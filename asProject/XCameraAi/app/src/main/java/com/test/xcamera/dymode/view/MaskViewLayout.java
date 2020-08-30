package com.test.xcamera.dymode.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.util.AudioClipUtil;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DateUtils;
import com.test.xcamera.utils.LoggerUtils;

import java.io.IOException;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/3/28
 * e-mail zhouxuecheng1991@163.com
 */

public class MaskViewLayout extends RelativeLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private final String TAG = "MaskViewLayout";
    private final float SEERBAR_MAXPROGRESS = 100.0f;
    private Bitmap bitmap;
    private MaskDrawable maskDrawable;
    private MediaPlayer player;
    private long totalLength;
    private String inputPath;
    private String outputPath;
    private TextView startTimeTextView;
    private TextView totalTimeTextView;
    private ImageView a2Img;
    private TextView bgmTextView;
    private float startTime;
    private float endTime;
    private float clipTime;
    private SeekBar clipSeekBar;
    private ClipListener clipListener;
    private RelativeLayout mParentLayout;

    private int progressSet = 0;
    private final int seekBarInitLocation = 3;

    public MaskViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBitmap();
        initView(context);
    }

    private void initBitmap() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maskview_cover);
        maskDrawable = new MaskDrawable(bitmap);
    }

    private void initView(Context context) {
        View clipView = View.inflate(context, R.layout.maskview_layout, this);
        RelativeLayout clipLayout = clipView.findViewById(R.id.clipLayout);
        clipSeekBar = clipView.findViewById(R.id.clipSeekBar);
        a2Img = clipView.findViewById(R.id.a2Img);
        a2Img.setImageDrawable(maskDrawable);
        startTimeTextView = clipView.findViewById(R.id.startTime);
        totalTimeTextView = clipView.findViewById(R.id.totalTime);
        bgmTextView = clipView.findViewById(R.id.bgmTextView);
        ImageView sureImg = clipView.findViewById(R.id.sureImg);
        RelativeLayout imgLayout = clipView.findViewById(R.id.imgLayout);
        sureImg.setOnClickListener(this);
        imgLayout.setOnClickListener(this);

        clipSeekBar.setOnSeekBarChangeListener(this);

        clipLayoutAnimation(clipLayout);

        mParentLayout = findViewById(R.id.mask_view_parent_layout);
        mParentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void clipLayoutAnimation(RelativeLayout clipLayout) {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        final int height = dm.heightPixels;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) clipLayout.getLayoutParams();
        layoutParams.width = height;
        layoutParams.leftMargin = -height / 2;
        clipLayout.setLayoutParams(layoutParams);

        ObjectAnimator anim = ObjectAnimator.ofFloat(clipLayout, "rotation", 0f, -270f);
        anim.setDuration(100);
        anim.start();
    }

    /**
     * 滑块显示的位置
     *
     * @param progress
     */
    public void locationChange(float progress) {
        float totalTime = totalLength / 1000;
        int measuredWidth = bitmap.getWidth();
        float maskWidth = measuredWidth * (clipTime / totalTime);
        float scale = progress / SEERBAR_MAXPROGRESS;
        float startLocatoin = scale * measuredWidth;
        startTime = scale * totalLength;
        endTime = (startTime + clipTime * 1000);
        if (endTime <= totalLength) {

            progressSet = (int) progress <= seekBarInitLocation ? seekBarInitLocation : (int) progress;
            startTime = (int) progress <= seekBarInitLocation ? 0 : startTime;
            startLocatoin = (int) progress <= seekBarInitLocation ? 0 : startLocatoin;

            maskDrawable.changLocation(startLocatoin, 0, maskWidth + startLocatoin, bitmap.getHeight());
            if (player != null && !player.isPlaying()) {
                reStartPlay();
            }
            if (player != null) {
                int playSeekToTime = (int) startTime;
                LoggerUtils.i(TAG, "playSeekToTime " + playSeekToTime + " player = " + player.toString());

                player.seekTo(playSeekToTime);
                startTimeTextView.setText(DateUtils.stringForTime(playSeekToTime));
            }
        }
        if (progressSet != 0) {
            clipSeekBar.setProgress(progressSet);
        } else {
            clipSeekBar.setProgress(0);
        }
    }

    private int alreadySelectTiem = -1;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (player != null) {
            locationChange(progress);
            alreadySelectTiem = progress;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    /**
     * 添加背景音乐
     *
     * @param inputPath  原文件的位置
     * @param outputPath 裁剪完成文件放在什么位置
     * @param clipTime   裁剪时间
     */
    public void setPlayResources(String inputPath, String outputPath, float clipTime, ClipListener clipListener) {
        this.inputPath = inputPath;
        this.clipTime = clipTime;
        this.outputPath = outputPath;
        this.clipListener = clipListener;
        this.progressSet = 0;
        this.alreadySelectTiem = -1;
    }

//    public void rePlay(float reStartTime) {
//
//        float totalTime = totalLength / 1000;
//        int measuredWidth = bitmap.getWidth();
//        float maskWidth = measuredWidth * (clipTime / totalTime);
//        float progress = (reStartTime / totalTime) * 100.0f;
//        float scale = progress / SEERBAR_MAXPROGRESS;
//        float startLocatoin = scale * measuredWidth;
//        float startTime = scale * totalLength;
//        float endTime = (startTime + clipTime * 1000);
//
//        maskDrawable.changLocation(startLocatoin, 0, maskWidth + startLocatoin, bitmap.getHeight());
//        if (player != null) {
//            int startTime1 = (int) startTime;
//            player.seekTo(startTime1);
//            startTimeTextView.setText(DateUtils.stringForTime(startTime1) + "");
//        }
//        clipSeekBar.setProgress((int) progress);
//    }

    /**
     * 当前位置播
     */
    private void reStartPlay() {
        try {
            if (player == null) {
                player = new MediaPlayer();
            }
            player.reset();
            player.setDataSource(inputPath);
            player.prepare();
            totalLength = player.getDuration();
            player.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reStartPlay(int seekTo) {
        try {
            if (player == null) {
                player = new MediaPlayer();
            }
            player.reset();
            player.setDataSource(inputPath);
            player.prepare();
            totalLength = player.getDuration();
            player.start();
            player.seekTo(seekTo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 开始播放
     */
    public void startPlay() {
        if (alreadySelectTiem != -1) {
            float scale = alreadySelectTiem / SEERBAR_MAXPROGRESS;
            int seekTime = (int) (scale * totalLength);
            locationChange(alreadySelectTiem);
            reStartPlay(seekTime);
        } else {
            startTimeTextView.measure(0, 0);
            totalTimeTextView.measure(0, 0);
            reStartPlay();
            if (totalLength != 0) {
                totalTimeTextView.setText(DateUtils.stringForTime(totalLength) + "");
                bgmTextView.setText(getResources().getString(R.string.touch_clip_music));
            }

            int width = startTimeTextView.getMeasuredWidth();
            int totalWidth = totalTimeTextView.getMeasuredWidth();
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) clipSeekBar.getLayoutParams();
            layoutParams.leftMargin = width;
            layoutParams.rightMargin = totalWidth;
            clipSeekBar.setLayoutParams(layoutParams);
            clipSeekBar.setProgress(seekBarInitLocation);

            int measuredWidth = bitmap.getWidth();
            float maskWidth = measuredWidth * (clipTime / (totalLength / 1000));
            maskDrawable.changLocation(0, 0, maskWidth, bitmap.getHeight());

        }
    }

    /**
     * 停止播放
     */
    public void pausePlay() {
        if (player != null) {
            player.stop();
        }
    }

    /**
     * 销毁播放器
     */
    public void destory() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (player == null) {
            CameraToastUtil.show(getResources().getString(R.string.addmusic_bgm), getContext());
            return;
        }
        if (v.getId() == R.id.sureImg) {
            boolean clip = AudioClipUtil.clip(inputPath, outputPath, (long) startTime, (long) endTime);
            if (clip) {
                if (clipListener != null) {
                    clipListener.clipOk(outputPath);
                }
            } else {
                if (clipListener != null) {
                    clipListener.clipError();
                }
            }
        }
    }

    public interface ClipListener {
        void clipOk(String outputPath);

        void clipError();
    }
}
