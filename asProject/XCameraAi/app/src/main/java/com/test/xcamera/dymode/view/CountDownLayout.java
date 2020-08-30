package com.test.xcamera.dymode.view;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.test.xcamera.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/3/28
 * e-mail zhouxuecheng1991@163.com
 * 抖音倒计时控件
 */

public class CountDownLayout extends RelativeLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private int alreadyRecordProgress;
    private float musicTotalTime;
    private float touchEndTime;
    private float alreadyRecordTime;
    private boolean isTouchSeekBar = false;
    private float copyAlreadyRecordTime;
    private int touchProgress = -1;
    private int COUNTDOWN_FLAG = 101;
    private boolean isLooperMusic = true;
    private View coverView;
    private ImageView imageView;
    private TextView showTimeTextView;
    private LayoutParams coverLayoutParams;
    private TextView totalDaojishiTime;
    private SeekBar mySeekBar;
    private ThouchTrimListener thouchTrimListener;
    private TextView selectTextView_3;
    private TextView selectTextView_10;
    private MediaPlayer player;
    private String inputPath;
    private WeakReference<DyFPVShotView> mDyFPVShotView;
    private Handler countDowunHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isLooperMusic) {
                alreadyRecordTime = copyAlreadyRecordTime;
                reStartPlay(false, alreadyRecordTime);
                countDowunHandler.sendEmptyMessageDelayed(COUNTDOWN_FLAG, (long) (touchEndTime * 1000 - alreadyRecordTime * 1000));
            }
        }
    };

    public CountDownLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View daojishi_layout = View.inflate(context, R.layout.countdown_layout, this);
        RelativeLayout daojishiLayout = daojishi_layout.findViewById(R.id.daojishiLayout);
        daojishiLayout.measure(0, 0);
        coverView = daojishi_layout.findViewById(R.id.coverView);
        coverLayoutParams = (LayoutParams) coverView.getLayoutParams();
        totalDaojishiTime = daojishi_layout.findViewById(R.id.totalDaojishiTime);
        RelativeLayout relativeLayout = daojishi_layout.findViewById(R.id.trimRootLayout);
        ViewGroup.LayoutParams layoutRoot = relativeLayout.getLayoutParams();
        mySeekBar = daojishi_layout.findViewById(R.id.seekBar);
        mySeekBar.setOnSeekBarChangeListener(this);

        imageView = daojishi_layout.findViewById(R.id.bgImg);
        showTimeTextView = daojishi_layout.findViewById(R.id.timeTextView);

        selectTextView_3 = daojishi_layout.findViewById(R.id.selectTextView_3);
        selectTextView_10 = daojishi_layout.findViewById(R.id.selectTextView_10);
        selectTextView_3.setOnClickListener(this);
        selectTextView_10.setOnClickListener(this);

        Button startTake = daojishi_layout.findViewById(R.id.startTake);
        startTake.setOnClickListener(this);

        select3s();

        clipLayoutAnimation(daojishiLayout);
        daojishiLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        relativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDyFPVShotView.get() != null)
                    mDyFPVShotView.get().clickCountDownLayout();
            }
        });

    }

    public void init(DyFPVShotView shotView) {
        mDyFPVShotView = new WeakReference<>(shotView);
    }

    private void clipLayoutAnimation(RelativeLayout clipLayout) {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        final int height = dm.heightPixels;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) clipLayout.getLayoutParams();
        layoutParams.width = height;
        clipLayout.setLayoutParams(layoutParams);

        ObjectAnimator anim = ObjectAnimator.ofFloat(clipLayout, "rotation", 0f, -270f);

        anim.setDuration(100);
        anim.start();
    }

    public void setThouchTrimListener(ThouchTrimListener listener) {
        this.thouchTrimListener = listener;
    }

    /**
     * @param alreadyRecordTime 倒计时结束时间
     * @param musicTotalTime    总时间
     */
    public void setCountDownTime(float alreadyRecordTime, float musicTotalTime, String inputPath) {
        this.inputPath = inputPath;
        this.musicTotalTime = musicTotalTime;
        this.alreadyRecordTime = alreadyRecordTime;
        this.copyAlreadyRecordTime = alreadyRecordTime;
        this.alreadyRecordProgress = (int) ((alreadyRecordTime / this.musicTotalTime) * 100.0f);

        initLayout();
    }

    private void initLayout() {
        imageView.measure(0, 0);
        int coverLocation = (int) ((copyAlreadyRecordTime / this.musicTotalTime) * imageView.getWidth());
        coverLayoutParams.width = coverLocation;
        coverLayoutParams.height = imageView.getMeasuredHeight();
        coverView.setLayoutParams(coverLayoutParams);
        mySeekBar.setProgress(100);//始终保持最大化和抖音保持一致
        totalDaojishiTime.setText(this.musicTotalTime + "");
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        if (copyAlreadyRecordTime < 0) {
            copyAlreadyRecordTime = 0;
        }
        String distanceString = decimalFormat.format(copyAlreadyRecordTime);
        showTimeTextView.setText(distanceString);
    }

    public void startPlay(float startTime, float stopTime) {
        isLooperMusic = true;
        reStartPlay(true, startTime < 0 ? 0 : startTime);
    }

    /**
     * 停止播放
     */
    public void pausePlay() {
        if (player != null) {
            player.stop();
        }
        countDowunHandler.removeMessages(COUNTDOWN_FLAG);
        isLooperMusic = false;
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
        if (countDowunHandler != null) {
            countDowunHandler.removeMessages(COUNTDOWN_FLAG);
            countDowunHandler.removeCallbacksAndMessages(null);
            countDowunHandler = null;
        }
        isLooperMusic = false;
    }

    /**
     * 当前位置播
     *
     * @param restart
     * @param startTime 从什么位置开始播放
     */
    private void reStartPlay(boolean restart, float startTime) {
        if (inputPath == null) {
            return;
        }
        if (player == null) {
            player = new MediaPlayer();
        }
        try {
            if (restart) {
                player.reset();
                player.setDataSource(inputPath);
                player.prepare();
                player.start();
                if (startTime != 0)
                    player.seekTo((int) (startTime * 1000));
            } else {
                player.seekTo((int) (startTime * 1000));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float scale = progress / 100.0f;
        touchEndTime = scale * musicTotalTime;
        if ((touchEndTime - alreadyRecordTime) > 0.5) {
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            String distanceString = decimalFormat.format(touchEndTime);
            showTimeTextView.setText(distanceString);
            if (isTouchSeekBar) {
                if (player != null && !player.isPlaying()) {
                    reStartPlay(true, alreadyRecordTime);
                }
                reStartPlay(false, alreadyRecordTime);
            }
            touchProgress = progress;
        } else {
        }
        mySeekBar.setProgress(touchProgress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTouchSeekBar = true;
        countDowunHandler.removeMessages(COUNTDOWN_FLAG);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isTouchSeekBar = false;
        float tempAlreadyRecordTime = (touchEndTime - 3) > alreadyRecordTime ? (touchEndTime - 3) : alreadyRecordTime;
        startPlay(tempAlreadyRecordTime, 0);
        countDowunHandler.sendEmptyMessageDelayed(COUNTDOWN_FLAG, (long) (touchEndTime * 1000 - tempAlreadyRecordTime * 1000));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.selectTextView_3:
                select3s();
                break;
            case R.id.selectTextView_10:
                select10s();
                break;
            case R.id.startTake:
                if (thouchTrimListener != null) {
                    pausePlay();
                    if (touchEndTime > alreadyRecordTime)
                        thouchTrimListener.thouchTime(touchEndTime);
                    else
                        thouchTrimListener.thouchTime(musicTotalTime);
                }
                break;
        }
    }

    private void select3s() {
        selectTextView_3.setSelected(true);
        selectTextView_3.setTextColor(0XFFFFFFFF);

        selectTextView_10.setSelected(false);
        selectTextView_10.setTextColor(0XFF717171);
        if (thouchTrimListener != null) {
            thouchTrimListener.countDownChanged(3);
        }
    }

    private void select10s() {
        selectTextView_10.setSelected(true);
        selectTextView_10.setTextColor(0XFFFFFFFF);

        selectTextView_3.setSelected(false);
        selectTextView_3.setTextColor(0XFF717171);

        if (thouchTrimListener != null) {
            thouchTrimListener.countDownChanged(10);
        }
    }

    public interface ThouchTrimListener {
        void thouchTime(float time);

        void countDownChanged(int count);
    }
}
