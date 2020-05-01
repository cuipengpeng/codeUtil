package com.android.player.widget.gesture_lock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.android.player.R;
import com.android.player.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 手势密码路径绘制
 *
 */
public class GestureDrawline extends View {
	private int mov_x;// 声明起点坐标
	private int mov_y;
	private Context context;
	private Paint paint;// 声明画笔
	private Canvas mCanvas;// 画布
	private Bitmap bitmap;// 位图
	private List<GesturePoint> list;// 装有各个view坐标的集合
	private List<Pair<GesturePoint, GesturePoint>> lineList;// 记录画过的线
	private Map<String, GesturePoint> autoCheckPointMap;// 自动选中的情况点
	private boolean isDrawEnable = true; // 是否允许绘制


	//*/ 手势密码点的状态
	public static final int POINT_STATE_NORMAL = 0; // 正常状态
	public static final int POINT_STATE_SELECTED = 1; // 按下状态
	public static final int POINT_STATE_WRONG = 2; // 错误状态
	public static final int POINT_STATE_CORRECT = 3; // 正确状态

	/**
	 * 屏幕的宽度和高度
	 */
	private int[] screenDispaly;

	/**
	 * 手指当前在哪个Point内
	 */
	private GesturePoint currentPoint;
	/**
	 * 用户绘图的回调
	 */
	private GestureCallBack callBack;

	/**
	 * 用户当前绘制的图形密码
	 */
	private StringBuilder drawPassWordStr;

	/**
	 * 是否为校验
	 */
	public boolean isVerify;

	/**
	 * 用户传入的passWord
	 */
	private String passWord;

	public GestureDrawline(Context context, List<GesturePoint> list, boolean isVerify,
						   String passWord, GestureCallBack callBack) {
		super(context);
		screenDispaly = ScreenUtil.getWidthHeight(context);
		this.context = context;
		paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		bitmap = Bitmap.createBitmap(screenDispaly[0], screenDispaly[0], Bitmap.Config.ARGB_8888); // 设置位图的宽高
		mCanvas = new Canvas();
		mCanvas.setBitmap(bitmap);
		paint.setStyle(Style.STROKE);// 设置非填充
		paint.setStrokeWidth(8);// 笔宽4像素
		paint.setColor(context.getResources().getColor(R.color.appRedColor));// 设置默认连线颜色
		paint.setAntiAlias(true);// 不显示锯齿

		this.list = list;
		this.lineList = new ArrayList<Pair<GesturePoint, GesturePoint>>();

		initAutoCheckPointMap();
		this.callBack = callBack;

		// 初始化密码缓存
		this.isVerify = isVerify;
		this.drawPassWordStr = new StringBuilder();
		this.passWord = passWord;
	}

	private void initAutoCheckPointMap() {
		autoCheckPointMap = new HashMap<String,GesturePoint>();
		autoCheckPointMap.put("1,3", getGesturePointByNum(2));
		autoCheckPointMap.put("1,7", getGesturePointByNum(4));
		autoCheckPointMap.put("1,9", getGesturePointByNum(5));
		autoCheckPointMap.put("2,8", getGesturePointByNum(5));
		autoCheckPointMap.put("3,7", getGesturePointByNum(5));
		autoCheckPointMap.put("3,9", getGesturePointByNum(6));
		autoCheckPointMap.put("4,6", getGesturePointByNum(5));
		autoCheckPointMap.put("7,9", getGesturePointByNum(8));
	}

	private GesturePoint getGesturePointByNum(int num) {
		for (GesturePoint point : list) {
			if (point.getNum() == num) {
				return point;
			}
		}
		return null;
	}

	// 画位图
	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(mCanvas);
		canvas.drawBitmap(bitmap, 0, 0, null);
	}

	// 触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isDrawEnable == false) {
			// 当期不允许绘制
			return true;
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mov_x = (int) event.getX();
				mov_y = (int) event.getY();
				// 判断当前点击的位置是处于哪个点之内
				currentPoint = getPointAt(mov_x, mov_y);
				if (currentPoint != null) {
					currentPoint.setPointState(POINT_STATE_SELECTED);
					drawPassWordStr.append(currentPoint.getNum());
				}
				// mCanvas.drawPoint(mov_x, mov_y, paint);// 画点
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				clearDrawLine();

				paint.setColor(context.getResources().getColor(R.color.appViewFullTextColor));// 设置默认连线颜色
				// 得到当前移动位置是处于哪个点内
				GesturePoint pointAt = getPointAt((int) event.getX(), (int) event.getY());
				// 代表当前用户手指处于点与点之前
				if (currentPoint == null && pointAt == null) {
					return true;
				} else {// 代表用户的手指移动到了点上
					if (currentPoint == null) {// 先判断当前的point是不是为null
						// 如果为空，那么把手指移动到的点赋值给currentPoint
						currentPoint = pointAt;
						// 把currentPoint这个点设置选中为true;
						currentPoint.setPointState(POINT_STATE_SELECTED);
						drawPassWordStr.append(currentPoint.getNum());
					}
				}
				if (pointAt == null || currentPoint.equals(pointAt) || POINT_STATE_SELECTED == pointAt.getPointState()) {
					// 点击移动区域不在圆的区域，或者当前点击的点与当前移动到的点的位置相同，或者当前点击的点处于选中状态
					// 那么以当前的点中心为起点，以手指移动位置为终点画线
					mCanvas.drawLine(currentPoint.getCenterX(), currentPoint.getCenterY(), event.getX(), event.getY(), paint);// 画线
				} else {
					// 如果当前点击的点与当前移动到的点的位置不同
					// 那么以前前点的中心为起点，以手移动到的点的位置画线
					mCanvas.drawLine(currentPoint.getCenterX(), currentPoint.getCenterY(), pointAt.getCenterX(), pointAt.getCenterY(), paint);// 画线
					pointAt.setPointState(POINT_STATE_SELECTED);

					// 判断是否中间点需要选中
					GesturePoint betweenPoint = getBetweenCheckPoint(currentPoint, pointAt);
					if (betweenPoint != null && POINT_STATE_SELECTED != betweenPoint.getPointState()) {
						// 存在中间点并且没有被选中
						Pair<GesturePoint, GesturePoint> pair1 = new Pair<GesturePoint, GesturePoint>(currentPoint, betweenPoint);
						lineList.add(pair1);
						drawPassWordStr.append(betweenPoint.getNum());
						Pair<GesturePoint, GesturePoint> pair2 = new Pair<GesturePoint, GesturePoint>(betweenPoint, pointAt);
						lineList.add(pair2);
						drawPassWordStr.append(pointAt.getNum());
						// 设置中间点选中
						betweenPoint.setPointState(POINT_STATE_SELECTED);
						// 赋值当前的point;
						currentPoint = pointAt;
					} else {
						Pair<GesturePoint, GesturePoint> pair = new Pair<GesturePoint, GesturePoint>(currentPoint, pointAt);
						lineList.add(pair);
						drawPassWordStr.append(pointAt.getNum());
						// 赋值当前的point;
						currentPoint = pointAt;
					}
				}
				invalidate();
				break;
			case MotionEvent.ACTION_UP:// 当手指抬起的时候
				if (isVerify) {
					// 手势密码校验
					// 清掉屏幕上所有的线，只画上集合里面保存的线
					if (passWord.equals(drawPassWordStr.toString())) {
						// 代表用户绘制的密码手势与传入的密码相同
						callBack.checkedSuccess();
					} else if (!TextUtils.isEmpty(drawPassWordStr.toString())){
						// 用户绘制的密码与传入的密码不同。
						callBack.checkedFail();
					}
				} else {

					if(drawPassWordStr.toString().length()>3){
						clearDrawlineState(1000l, true);
					}else {
						clearDrawlineState(1000l, false);
					}
					callBack.onGestureCodeInput(drawPassWordStr.toString());
				}
				break;
			default:
				break;
		}
		return true;
	}

	/**
	 * 指定时间去清除绘制的状态
	 * @param delayTime 保留路径delayTime时间长
	 */
	public void clearDrawlineState(long delayTime, boolean correctPpassword) {
		if (delayTime > 0) {
			// 绘制红色提示路线
			isDrawEnable = false;
			drawErrorPasswordPath(correctPpassword);
		}
		new Handler().postDelayed(new ClearDrawBitmapRunnable(), delayTime);
	}

	/**
	 * 清除绘制状态的线程
	 */
	final class ClearDrawBitmapRunnable implements Runnable {
		public void run() {
			// 重置passWordSb
			drawPassWordStr = new StringBuilder();
			//所有点置为normal
			for (GesturePoint p : list) {
				p.setPointState(POINT_STATE_NORMAL);
			}
			// 清空保存点的集合
			lineList.clear();
			// 重新绘制界面
			clearDrawLine();

			invalidate();
			isDrawEnable = true;
		}
	}

	/**
	 * 通过点的位置去集合里面查找这个点是包含在哪个Point里面的
	 *
	 * @param x
	 * @param y
	 * @return 如果没有找到，则返回null，代表用户当前移动的地方属于点与点之间
	 */
	private GesturePoint getPointAt(int x, int y) {

		for (GesturePoint point : list) {
			// 先判断x
			int leftX = point.getLeftX();
			int rightX = point.getRightX();
			if (!(x >= leftX && x < rightX)) {
				// 如果为假，则跳到下一个对比
				continue;
			}

			int topY = point.getTopY();
			int bottomY = point.getBottomY();
			if (!(y >= topY && y < bottomY)) {
				// 如果为假，则跳到下一个对比
				continue;
			}

			// 如果执行到这，那么说明当前点击的点的位置在遍历到点的位置这个地方
			return point;
		}

		return null;
	}

	private GesturePoint getBetweenCheckPoint(GesturePoint pointStart, GesturePoint pointEnd) {
		int startNum = pointStart.getNum();
		int endNum = pointEnd.getNum();
		String key = null;
		if (startNum < endNum) {
			key = startNum + "," + endNum;
		} else {
			key = endNum + "," + startNum;
		}
		return autoCheckPointMap.get(key);
	}

	/**
	 * 清掉屏幕上所有的线，然后画出集合里面的线
	 */
	private void clearDrawLine() {
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		for (Pair<GesturePoint, GesturePoint> pair : lineList) {
			mCanvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
					pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
		}
	}

	/**
	 * 校验错误/两次绘制不一致提示
	 */
	private void drawErrorPasswordPath(boolean correctPpassword) {
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		for (Pair<GesturePoint, GesturePoint> pair : lineList) {
			if(correctPpassword){
				paint.setColor(context.getResources().getColor(R.color.appViewFullTextColor));
				pair.first.setPointState(POINT_STATE_CORRECT);
				pair.second.setPointState(POINT_STATE_CORRECT);
			}else {
				paint.setColor(context.getResources().getColor(R.color.appRedColor));
				pair.first.setPointState(POINT_STATE_WRONG);
				pair.second.setPointState(POINT_STATE_WRONG);
			}
			mCanvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
					pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
		}
		invalidate();
	}


	public interface GestureCallBack {

		/**
		 * 用户设置/输入了手势密码
		 */
		  void onGestureCodeInput(String inputCode);

		/**
		 * 代表用户绘制的密码与传入的密码相同
		 */
		 void checkedSuccess();

		/**
		 * 代表用户绘制的密码与传入的密码不相同
		 */
		void checkedFail();
	}

}
