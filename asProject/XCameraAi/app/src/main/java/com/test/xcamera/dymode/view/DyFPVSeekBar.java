package com.test.xcamera.dymode.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.editvideo.ScreenUtils;
import com.test.xcamera.R;

/**
 * Created by zll on 2020/2/28.
 */

public class DyFPVSeekBar extends RelativeLayout {
    private Context mContext;
    private SeekBar mSeekbar = null;
    private TextView moveText;

    private int screenWidth;//屏幕宽度
    private TextMoveLayout textMoveLayout;
    private ViewGroup.LayoutParams layoutParams;

    private int mLeftMargin;
    private TextPaint mPaint;
    private LinearLayout mParentView;
    private DyFPVSeekBarChangedListener mChangedListener;

    public DyFPVSeekBar(Context context) {
        super(context);

        init(context);
    }

    public DyFPVSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void setSeekBarChaneListener(DyFPVSeekBarChangedListener listener) {
        mChangedListener = listener;
    }

    private void init(Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.dy_fpv_seekbar, this, true);

        mSeekbar = view.findViewById(R.id.dy_fpv_seekbar);
        mParentView = view.findViewById(R.id.dy_fpv_parent);

        mSeekbar.getThumb().setColorFilter(0XFFFFFFFF, PorterDuff.Mode.SRC_ATOP);
        mSeekbar.getProgressDrawable().setColorFilter(0XFFFACD13, PorterDuff.Mode.SRC_ATOP);
    }

    public void init(int width) {
        screenWidth = width;
        setMoveTextView();
        getChildLayoutParams();
    }

    /**
     * 获取子view的marginLayoutParams
     */
    private void getChildLayoutParams() {
        View childAt = mParentView.getChildAt(0);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
        mLeftMargin = marginLayoutParams.leftMargin;
        mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());
    }

    /**
     * 设置moveTextView的基础参数
     */
    private void setMoveTextView() {

        moveText = new TextView(mContext);
        moveText.setText(0 + "");
        moveText.setTextColor(0XFFFFFFFF);
        moveText.setTextSize(14);

        layoutParams = new ViewGroup.LayoutParams(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        textMoveLayout = findViewById(R.id.dy_fpv_textLayout);
        textMoveLayout.addView(moveText, layoutParams);
        moveText.layout(5, 50, screenWidth, 110);
    }

    /**
     * seekbar滑动监听
     */
    private class OnSeekBarChangeListenerImp implements
            SeekBar.OnSeekBarChangeListener {

        // 触发操作，拖动
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setMoveTextLayout();
            if (mChangedListener != null) {
                mChangedListener.onProgressChanged(seekBar, progress, fromUser);
            }
        }

        // 开始拖动时候触发的操作
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        // 停止拖动时候
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    /**
     * seekbar滑动过程中改变moveTextView的位置
     */
    private void setMoveTextLayout() {
        if (mPaint == null) {
            mPaint = new TextPaint();
        }

        float measureText = mPaint.measureText(moveText.getText().toString().trim());
        Rect bounds = mSeekbar.getProgressDrawable().getBounds();
        int xFloat = (int) (bounds.width() * mSeekbar.getProgress() / mSeekbar.getMax() - measureText / 2 + ScreenUtils.px2dip(mContext, mLeftMargin));
        moveText.layout(xFloat, 50, screenWidth, 110);
        moveText.setText(mSeekbar.getProgress() + "");
    }

    public void setProgress(int progress) {
        mSeekbar.setProgress(progress);
    }

    public interface DyFPVSeekBarChangedListener {
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
    }
}
