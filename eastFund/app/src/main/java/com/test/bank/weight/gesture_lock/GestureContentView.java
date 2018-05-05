package com.test.bank.weight.gesture_lock;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.test.bank.R;
import com.test.bank.bean.GesturePoint;
import com.test.bank.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 手势密码容器类
 *
 */
public class GestureContentView extends ViewGroup {

	private int mPadding = 34;//px
	/**
	 * 每个点区域的宽度
	 */
	private int blockWidth;
	/**
	 * 声明一个集合用来封装坐标集合
	 */
	private List<GesturePoint> list = new ArrayList<GesturePoint>();
	private Context context;
	public GestureDrawline gestureDrawline;
	private ViewGroup mParent;
	/**
	 * 包含9个ImageView的容器，初始化
	 * @param context
	 * @param isVerify 是否为校验手势密码
	 * @param passWord 用户传入密码
	 * @param callBack 手势绘制完毕的回调
	 */
	public GestureContentView(Context context,ViewGroup parent, boolean isVerify, String passWord, GestureDrawline.GestureCallBack callBack) {
		super(context);
		blockWidth = (int) DensityUtil.dip2px(101f);
		this.context = context;
		this.mParent = parent;
		// 添加9个图标
		addChild();
		// 初始化一个可以画线的view
		gestureDrawline = new GestureDrawline(context, list, isVerify, passWord, callBack);

		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.setLayoutParams(layoutParams);
		gestureDrawline.setLayoutParams(layoutParams);
		mParent.addView(this);
		mParent.addView(gestureDrawline);
	}

	private void addChild(){
		for (int i = 0; i < 9; i++) {
			ImageView image = new ImageView(context);
			image.setBackgroundResource(R.drawable.gesture_node_normal_new);
			this.addView(image);
			// 第几行
			int row = i / 3;
			// 第几列
			int col = i % 3;
			// 定义点的每个属性
			int leftX = col*blockWidth+ mPadding;
			int topY = row*blockWidth+ mPadding;
			int rightX = col*blockWidth+blockWidth- mPadding;
			int bottomY = row*blockWidth+blockWidth- mPadding;
			GesturePoint p = new GesturePoint(leftX, rightX, topY, bottomY, image,i+1);
			this.list.add(p);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			//第几行
			int row = i/3;
			//第几列
			int col = i%3;
			View v = getChildAt(i);
			v.layout(col*blockWidth+ mPadding, row*blockWidth+ mPadding,
					col*blockWidth+blockWidth- mPadding, row*blockWidth+blockWidth- mPadding);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 遍历设置每个子view的大小
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			v.measure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
