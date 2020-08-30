package com.test.xcamera.profession;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.test.xcamera.R;
import com.test.xcamera.view.VerticalTextView;

/**
 * Created by smz on 2020/1/15.
 */

public class SelectButton extends LinearLayout {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    private VerticalTextView left, right;
    private LinearLayout content;
    public int status = LEFT;
    private SelectListener mSelectListener;

    /**
     * <enum name="vertical" value="0" />
     * <enum name="landscape" value="1" />
     */
    private int direction;

    public SelectButton(Context context) {
        super(context);
        init();
    }

    public SelectButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.select_btn);
        direction = a.getInt(R.styleable.select_btn_direct, 0);
        a.recycle();

        init();
    }

    /**
     * 设置控件的方向
     *
     * @param orien 1 横向
     */
    public void setOrientation(int orien) {
        this.direction = orien;
        if (this.direction == 1) {
            content.setOrientation(HORIZONTAL);
            left.setDirection(VerticalTextView.ORIENTATION_LEFT_TO_RIGHT);
            LayoutParams lp = (LayoutParams) left.getLayoutParams();
            lp.width = 0;
            lp.height = LayoutParams.MATCH_PARENT;
            left.setLayoutParams(lp);

            right.setDirection(VerticalTextView.ORIENTATION_LEFT_TO_RIGHT);
            lp = (LayoutParams) left.getLayoutParams();
            lp.width = 0;
            lp.height = LayoutParams.MATCH_PARENT;
            right.setLayoutParams(lp);
        } else {
            content.setOrientation(VERTICAL);
            left.setDirection(VerticalTextView.ORIENTATION_DOWN_TO_UP);
            right.setDirection(VerticalTextView.ORIENTATION_DOWN_TO_UP);

            LayoutParams lp = (LayoutParams) left.getLayoutParams();
            lp.height = 0;
            lp.width = LayoutParams.MATCH_PARENT;
            left.setLayoutParams(lp);
            lp = (LayoutParams) left.getLayoutParams();
            lp.height = 0;
            lp.width = LayoutParams.MATCH_PARENT;
            right.setLayoutParams(lp);
        }

        setStatus(this.status, true);
    }

    private void init() {
        View.inflate(getContext(), R.layout.layout_select_button, this);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        content = findViewById(R.id.content);

        left.setOnClickListener((v) -> {
            if (mSelectListener != null) {
                if (mSelectListener.onSelect(LEFT))
                    setStatus(LEFT);
            } else
                setStatus(LEFT);
        });
        right.setOnClickListener((v) -> {
            if (mSelectListener != null) {
                if (mSelectListener.onSelect(RIGHT))
                    setStatus(RIGHT);
            } else
                setStatus(RIGHT);
        });

        setOrientation(direction);
    }

    public void reset() {
        setStatus(LEFT, true);
    }

    /**
     * 设置控件文本
     */
    public void setText(String left, String right) {
        this.left.setText(left);
        this.right.setText(right);
    }

    public void setStatus(int status) {
        setStatus(status, false);
    }

    public void setStatus(int status, boolean force) {
        if (this.status == status && !force)
            return;
        int leftRes = R.drawable.shape_profession_left_sele, rightRes = R.drawable.shape_profession_right;
        if (direction == 1) {
            leftRes = status == LEFT ? R.drawable.shape_profession_left_sele : R.drawable.shape_profession_left;
            rightRes = status == RIGHT ? R.drawable.shape_profession_right_sele : R.drawable.shape_profession_right;
        } else {
            leftRes = status == LEFT ? R.drawable.shape_profession_top_sele : R.drawable.shape_profession_top;
            rightRes = status == RIGHT ? R.drawable.shape_profession_bottom_sele : R.drawable.shape_profession_bottom;
        }

        this.status = status;
        left.setBackgroundResource(leftRes);
        left.setTextColor(status == LEFT ? getResources().getColor(R.color.color_ff7700) : getResources().getColor(R.color.color_666666));
        right.setBackgroundResource(rightRes);
        right.setTextColor(status == RIGHT ? getResources().getColor(R.color.color_ff7700) : getResources().getColor(R.color.color_666666));
    }

    public void setSelectListener(SelectListener selectListener) {
        this.mSelectListener = selectListener;
    }

    public interface SelectListener {
        /**
         * 点击回调
         *
         * @return true 会切换按钮状态
         */
        boolean onSelect(int status);
    }
}
