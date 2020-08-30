package com.test.xcamera.dymode.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zll on 2020/3/9.
 */

public class DyFPVCountDownView extends RelativeLayout {
    @BindView(R.id.dy_fpv_count_down_3s)
    TextView mCountDown3s;
    @BindView(R.id.dy_fpv_count_down_10s)
    TextView mCountDown10s;
    @BindView(R.id.dy_fpv_count_down_scroll)
    View mCountDownScroll;
    @BindView(R.id.dy_fpv_count_down_start)
    TextView mCountDownStart;

    private DyCountDownChangedListener mCountDownListener;

    public DyFPVCountDownView(Context context) {
        super(context);
    }

    public DyFPVCountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCountDownListener(DyCountDownChangedListener listener) {
        mCountDownListener = listener;
    }

    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.dy_fpv_count_down, this, true);

        ButterKnife.bind(this);
        mCountDown3s.setSelected(true);
        mCountDown3s.setTextColor(0XFFFFFFFF);

        mCountDown10s.setSelected(false);
        mCountDown10s.setTextColor(0XFF717171);
    }

    @OnClick({R.id.dy_fpv_count_down_3s, R.id.dy_fpv_count_down_10s, R.id.dy_fpv_count_down_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dy_fpv_count_down_3s:
                select3s();
                break;
            case R.id.dy_fpv_count_down_10s:
                select10s();
                break;
            case R.id.dy_fpv_count_down_start:
                if (mCountDownListener != null) {
                    mCountDownListener.startCountDown();
                }
                break;
        }
    }

    private void select3s() {
        mCountDown3s.setSelected(true);
        mCountDown3s.setTextColor(0XFFFFFFFF);

        mCountDown10s.setSelected(false);
        mCountDown10s.setTextColor(0XFF717171);

        if (mCountDownListener != null) {
            mCountDownListener.countDownChanged(3);
        }
    }

    private void select10s() {
        mCountDown10s.setSelected(true);
        mCountDown10s.setTextColor(0XFFFFFFFF);

        mCountDown3s.setSelected(false);
        mCountDown3s.setTextColor(0XFF717171);

        if (mCountDownListener != null) {
            mCountDownListener.countDownChanged(10);
        }
    }

    public interface DyCountDownChangedListener {
        void countDownChanged(int value);

        void startCountDown();
    }
}
