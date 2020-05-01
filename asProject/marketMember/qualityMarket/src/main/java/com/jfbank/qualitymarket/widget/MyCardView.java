package com.jfbank.qualitymarket.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 简单实现边缘凹凸电子票效果
 * 
 * @author 彭爱军
 * @date 2016年9月19日
 */
public class MyCardView extends LinearLayout {
	/**
	 * 圆的半径
	 */
	private int radius = 8;
	/**
	 * 圆之间的间距
	 */
	private int gap = 8;

	private Paint mPaint; // 画笔

	public MyCardView(Context context) {
		super(context);
		init();
	}

	public MyCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	/**
	 * 初始化数据
	 */
	private void init() {
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setDither(true);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		   //圆的个数  
        int roundNum = getWidth() / (radius * 2 + gap * 2);  //圆的个数我们可以用总宽度/（2倍的radius+2倍的gap）即可。
        
        for (int i = 1; i <= roundNum; i++) {  
            canvas.drawCircle((gap + radius) * (2 * i - 1), 0, radius, mPaint);  
            canvas.drawCircle((gap + radius) * (2 * i - 1), getHeight(), radius, mPaint);  
        }  
	}

}
