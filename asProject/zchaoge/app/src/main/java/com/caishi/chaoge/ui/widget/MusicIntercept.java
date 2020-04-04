package com.caishi.chaoge.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.utils.DisplayMetricsUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class MusicIntercept extends RelativeLayout {

    private ImageView leftView;
    private LinearLayout lineLayout;
    private View view;
    private ImageView rightView;
    private RelativeLayout sliderLayout;

    private double minX;//最小值
    private double maxX;//最大值

    private int duration;//音乐时长  秒

    private int leftMargin;//左边距
    private int rightMargin;//右边距

    private double totalWidth;//总宽度

    private TextView tvStartView;
    private TextView tvEndView;

    private OnMusicInterceptListener listener;

    public void setListener(OnMusicInterceptListener listener) {
        this.listener = listener;
    }

    public interface OnMusicInterceptListener {
        void onTimeChange(int lTime, int rTime);

        void onStartChange(int lTime, int time);
    }

    public int setDuration(int duration) {
        this.duration = (duration / 1000);

        tvStartView.setText("00:00");
        tvEndView.setText(formatTime(duration));

        leftMargin = tvStartView.getWidth();
        rightMargin = tvEndView.getWidth();

        totalWidth = DisplayMetricsUtil.getScreenWidth(getContext()) - leftMargin - rightMargin;

        RelativeLayout.LayoutParams sliderLP = (LayoutParams) sliderLayout.getLayoutParams();

        //最大值
        maxX = totalWidth * 0.4;

        //最小值
        minX = totalWidth * 0.3;

        sliderLP.width = (int) (totalWidth * 0.3);

        sliderLayout.setLayoutParams(sliderLP);

        //计算初始长度

        double rPercent = div((totalWidth * 0.3), totalWidth, 2);

        int rTime = (int) ((rPercent) * 40);

        return rTime;
    }

    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            return 0.0;
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private String formatTime(int second) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式。
        return formatter.format((long) second);
    }

    public MusicIntercept(Context context) {
        this(context, null);
    }

    public MusicIntercept(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicIntercept(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(0xFFFFFFF);

        tvStartView = new TextView(context);
        tvStartView.setId(tvStartView.hashCode());
        tvStartView.setPadding(DisplayMetricsUtil.dip2px(getContext(), 15), 0, DisplayMetricsUtil.dip2px(getContext(),12), 0);
        tvStartView.setTextColor(Color.GRAY);
        tvStartView.setTextSize(12);
        tvStartView.setText("00:00");
        RelativeLayout.LayoutParams startLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        startLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        startLP.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(tvStartView, startLP);

        tvEndView = new TextView(context);
        tvEndView.setTextColor(Color.GRAY);
        tvEndView.setTextSize(12);
        tvEndView.setId(tvEndView.hashCode());
        tvEndView.setPadding(DisplayMetricsUtil.dip2px(getContext(),12), 0, DisplayMetricsUtil.dip2px(getContext(),15), 0);
        tvEndView.setText("00:00");
        RelativeLayout.LayoutParams endLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        endLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        endLP.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(tvEndView, endLP);

        RelativeLayout container = new RelativeLayout(context);
        RelativeLayout.LayoutParams containerLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerLP.addRule(RelativeLayout.LEFT_OF, tvEndView.getId());
        containerLP.addRule(RelativeLayout.RIGHT_OF, tvStartView.getId());
        addView(container, containerLP);

        RelativeLayout.LayoutParams bgViewLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        bgViewLayout.addRule(RelativeLayout.CENTER_VERTICAL);
        LinearLayout bgView = new LinearLayout(context);
        bgView.setBackgroundColor(0xFFE5E5E5);
        container.addView(bgView, bgViewLayout);

        sliderLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams sliderLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sliderLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        container.addView(sliderLayout, sliderLP);

        leftView = new ImageView(context);
        leftView.setId(leftView.hashCode());
        leftView.setScaleType(ImageView.ScaleType.CENTER);
        leftView.setImageResource(R.drawable.edit_music_seekbar_thumb);
        leftView.setOnTouchListener(leftTouchListener);
        RelativeLayout.LayoutParams leftLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        leftLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftLP.addRule(RelativeLayout.CENTER_VERTICAL);
        sliderLayout.addView(leftView, leftLP);

        rightView = new ImageView(context);
        rightView.setId(rightView.hashCode());
        rightView.setOnTouchListener(rightTouchListener);
        rightView.setScaleType(ImageView.ScaleType.CENTER);
        rightView.setImageResource(R.drawable.rangseekbar_hint);
        RelativeLayout.LayoutParams rightLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rightLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightLP.addRule(RelativeLayout.CENTER_VERTICAL);
        sliderLayout.addView(rightView, rightLP);

        lineLayout = new LinearLayout(context);
        lineLayout.setGravity(Gravity.CENTER_VERTICAL);
        lineLayout.setOnTouchListener(lineTouchListener);
        RelativeLayout.LayoutParams lineLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lineLP.addRule(RelativeLayout.RIGHT_OF, leftView.getId());
        lineLP.addRule(RelativeLayout.LEFT_OF, rightView.getId());
        sliderLayout.addView(lineLayout, lineLP);

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 22);
        view = new View(context);
        view.setBackgroundResource(R.drawable.circle_corner_gray_bg_normal_2dp);
        lineLayout.addView(view, linearParams);
    }

    private void silderMethod() {
        int emptyLeft = sliderLayout.getRight() - sliderLayout.getWidth();

        double r = (rightView.getRight() + emptyLeft);//当前控件在父容器所在的位置

        double l = (leftView.getLeft() + emptyLeft);//当前控件在父容器所在的位置

        double rPercent = div(r, totalWidth, 2);
        double lPercent = div(l, totalWidth, 2);


        int lTime = (int) (duration * lPercent);
        int rTime = (int) (lTime + (rPercent - lPercent) * 40);

        if (listener != null) {
            listener.onTimeChange(lTime, rTime);
        }

        tvStartView.setText(formatTime((lTime * 1000)));
    }

    private void changeStart() {

        int emptyLeft = sliderLayout.getRight() - sliderLayout.getWidth();

        double r = (rightView.getRight() + emptyLeft);//当前控件在父容器所在的位置

        double l = (leftView.getLeft() + emptyLeft);//当前控件在父容器所在的位置

        double rPercent = div(r, totalWidth, 2);
        double lPercent = div(l, totalWidth, 2);

        int lTime = (int) (duration * lPercent);
        int rTime = (int) (lTime + (rPercent - lPercent) * 40);

        if (listener != null) {
            listener.onStartChange(lTime, rTime);
        }
    }

    private int leftLeft;
    private int leftRight;

    private int rightLeft;
    private int rightRight;

    private int centerLeft;
    private int centerRight;

    private int viewLeft;
    private int viewRight;

    public void resetPosition() {
        leftLeft = 0;
        leftRight = 0;

        rightLeft = 0;
        rightRight = 0;

        centerLeft = 0;
        centerRight = 0;

        viewLeft = 0;
        viewRight = 0;
    }

    private OnTouchListener rightTouchListener = new OnTouchListener() {

        private int lastX;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;

                    int right = sliderLayout.getRight() + dx;

                    if ((right + leftMargin) < (DisplayMetricsUtil.getScreenWidth(getContext()) - rightMargin) && (right - sliderLayout.getLeft()) <= maxX && (right - sliderLayout.getLeft()) >= minX) {
                        leftLeft = leftView.getLeft();
                        leftRight = leftView.getRight();

                        rightLeft = rightView.getLeft() + dx;
                        rightRight = rightView.getRight() + dx;

                        centerLeft = lineLayout.getLeft();
                        centerRight = lineLayout.getRight() + dx;

                        viewLeft = view.getLeft();
                        viewRight = view.getRight() + dx;

                        if (rightRight >= right) {
                            rightRight = right;
                        }

                        sliderLayout.layout(sliderLayout.getLeft(), sliderLayout.getTop(), right, sliderLayout.getBottom());
                        rightView.layout(rightLeft, rightView.getTop(), rightRight, rightView.getBottom());
                        lineLayout.layout(lineLayout.getLeft(), lineLayout.getTop(), centerRight, lineLayout.getBottom());

                        view.layout(view.getLeft(), view.getTop(), viewRight, view.getBottom());

                        silderMethod();
                    }

                    lastX = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_UP:
                    changeStart();
                    break;
            }
            return true;
        }
    };

    private OnTouchListener leftTouchListener = new OnTouchListener() {

        private int lastX;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;

                    int left = sliderLayout.getLeft() + dx;

                    if (left >= 0 && (left + minX) <= sliderLayout.getRight() && (sliderLayout.getRight() - left) <= maxX) {
                        leftLeft = leftView.getLeft();
                        leftRight = leftView.getRight();

                        rightLeft = rightView.getLeft() + -dx;
                        rightRight = rightView.getRight() + -dx;

                        centerLeft = lineLayout.getLeft();
                        centerRight = lineLayout.getRight() + -dx;

                        viewLeft = view.getLeft();
                        viewRight = view.getRight() + -dx;

                        sliderLayout.layout(left, sliderLayout.getTop(), sliderLayout.getRight(), sliderLayout.getBottom());
                        rightView.layout(rightLeft, rightView.getTop(), rightRight, rightView.getBottom());
                        leftView.layout(leftLeft, leftView.getTop(), leftRight, leftView.getBottom());
                        lineLayout.layout(centerLeft, lineLayout.getTop(), centerRight, lineLayout.getBottom());
                        view.layout(viewLeft, view.getTop(), viewRight, view.getBottom());

                        silderMethod();
                    }

                    lastX = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_UP:
                    changeStart();
                    break;
            }
            return true;
        }
    };

    private OnTouchListener lineTouchListener = new OnTouchListener() {

        private int lastX;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;

                    int left = sliderLayout.getLeft() + dx;
                    int right = sliderLayout.getRight() + dx;

                    if (left <= 0) {
                        left = 0;
                        right = sliderLayout.getWidth();
                    }

                    if ((right + leftMargin) >= (DisplayMetricsUtil.getScreenWidth(getContext()) - rightMargin)) {
                        right = (DisplayMetricsUtil.getScreenWidth(getContext()) - leftMargin - rightMargin);
                        left = (DisplayMetricsUtil.getScreenWidth(getContext()) - leftMargin - rightMargin - sliderLayout.getWidth());
                    }

                    sliderLayout.layout(left, sliderLayout.getTop(), right, sliderLayout.getBottom());

                    if (rightLeft != 0 || rightRight != 0) {
                        rightView.layout(rightLeft, rightView.getTop(), rightRight, rightView.getBottom());
                    }

                    if (centerLeft != 0 || centerRight != 0) {
                        lineLayout.layout(centerLeft, lineLayout.getTop(), centerRight, lineLayout.getBottom());
                    }

                    if (viewLeft != 0 || viewRight != 0) {
                        view.layout(viewLeft, view.getTop(), viewRight, view.getBottom());
                    }

                    silderMethod();

                    lastX = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_UP:
                    changeStart();
                    break;
            }
            return true;
        }
    };
}