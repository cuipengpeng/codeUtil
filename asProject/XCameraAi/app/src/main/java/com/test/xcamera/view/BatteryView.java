package com.test.xcamera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.test.xcamera.R;
import com.test.xcamera.utils.ViewUitls;

/**
 * Created by ms on 2020/2/4.
 * <p>
 * 电池百分比图标UI
 * 根据UI
 * layout_width="24dp"
 * layout_height="24dp"
 * <p>
 * 尺寸已经固定 后续有变更需修改
 */

public class BatteryView extends View {
    private Paint batPaint;
    private int height;
    private float progress = 0f;

    public BatteryView(Context context) {
        super(context);

        init(context);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        batPaint = new Paint();
        batPaint.setAntiAlias(true);
        batPaint.setStyle(Paint.Style.FILL);
        batPaint.setStrokeWidth(ViewUitls.dp2px(context, 8));

        this.setBackgroundResource(R.mipmap.icon_battery_wrapper);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this.height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.progress < 0.159)
            batPaint.setColor(getResources().getColor(R.color.color_ff2d2d));
        else
            batPaint.setColor(getResources().getColor(R.color.color_0ed986));

        canvas.drawLine(ViewUitls.dp2px(getContext(), 5), height / 2, ViewUitls.dp2px(getContext(), (int) (5 + 12 * progress)), height / 2, batPaint);
    }

    /*
    * 设置电池百分比
    * */
    public void setProgress(float progress) {
        this.progress = progress;
        this.invalidate();
    }
}
