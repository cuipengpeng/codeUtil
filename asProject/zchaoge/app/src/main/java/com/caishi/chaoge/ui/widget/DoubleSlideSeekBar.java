package com.caishi.chaoge.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.caishi.chaoge.R;
import com.caishi.chaoge.utils.LogUtil;

import java.text.SimpleDateFormat;

public class DoubleSlideSeekBar extends View {
    private int lineWidth;
    private int lineLength=getWidth();
    private int textHeight;
    private int imageWidth;
    private int imageHeight;
    private boolean hasRule;
    private boolean isLowerMoving;
    private boolean isUpperMoving;

    private int textSize;
    private int textColor;
    private int inColor = Color.BLUE;
    private int outColor = Color.BLUE;
    private int ruleColor = Color.BLUE;
    private int ruleTextColor = Color.BLUE;

    private Bitmap bitmapLow;
    private Bitmap bitmapBig;
    private Bitmap bitmapBackGround;
    private int slideLowX;
    private int slideBigX;
    private int bitmapHeight;
    private int bitmapWidth;

    private int paddingLeft = 30;
    private int paddingRight = 30;
    private int paddingTop = 0;
    private int paddingBottom = 0;

    private int lineStart = paddingLeft;
    private int lineY;
    private int lineEnd = lineLength + paddingLeft;
    private int bigValue = 100;
    private int smallValue = 0;
    private float smallRange;
    private float bigRange;

    private String unit = " ";
    private int equal = 20;
    private String ruleUnit = " ";
    private int ruleTextSize = 20;
    private int ruleLineHeight = 20;

    private Paint linePaint;
    private Paint bitmapPaint;
    private Paint textPaint;
    private Paint paintRule;
    private Paint curcorProgressPaint;

    public DoubleSlideSeekBar(Context context) {
        this(context, null);
    }

    public DoubleSlideSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleSlideSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paddingLeft = dip2px(getContext(), 10);
        paddingRight = dip2px(getContext(), 10);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DoubleSlideSeekBar, defStyleAttr, 0);
        int size = typedArray.getIndexCount();
        for (int i = 0; i < size; i++) {
            int type = typedArray.getIndex(i);
            switch (type) {
                case R.styleable.DoubleSlideSeekBar_inColor:
                    inColor = typedArray.getColor(type, Color.BLACK);
                    break;
                case R.styleable.DoubleSlideSeekBar_lineHeight:
                    lineWidth = (int) typedArray.getDimension(type, dip2px(getContext(), 10));
                    break;
                case R.styleable.DoubleSlideSeekBar_outColor:
                    outColor = typedArray.getColor(type, Color.YELLOW);
                    break;
                case R.styleable.DoubleSlideSeekBar_textColor:
                    textColor = typedArray.getColor(type, Color.BLUE);
                    break;
                case R.styleable.DoubleSlideSeekBar_textSize:
                    textSize = typedArray.getDimensionPixelSize(type, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.DoubleSlideSeekBar_imageLow:
                    bitmapLow = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(type, 0));
                    break;
                case R.styleable.DoubleSlideSeekBar_imageBig:
                    bitmapBig = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(type, 0));
                    break;
                case R.styleable.DoubleSlideSeekBar_imageheight:
                    imageHeight = (int) typedArray.getDimension(type, dip2px(getContext(), 20));
                    break;
                case R.styleable.DoubleSlideSeekBar_imagewidth:
                    imageWidth = (int) typedArray.getDimension(type, dip2px(getContext(), 20));
                    break;
                case R.styleable.DoubleSlideSeekBar_hasRule:
                    hasRule = typedArray.getBoolean(type, false);
                    break;
                case R.styleable.DoubleSlideSeekBar_ruleColor:
                    ruleColor = typedArray.getColor(type, Color.BLUE);
                    break;
                case R.styleable.DoubleSlideSeekBar_ruleTextColor:
                    ruleTextColor = typedArray.getColor(type, Color.BLUE);
                    break;
                case R.styleable.DoubleSlideSeekBar_unit:
                    unit = typedArray.getString(type);
                    break;
                case R.styleable.DoubleSlideSeekBar_equal:
                    equal = typedArray.getInt(type, 10);
                    break;
                case R.styleable.DoubleSlideSeekBar_ruleUnit:
                    ruleUnit = typedArray.getString(type);
                    break;
                case R.styleable.DoubleSlideSeekBar_ruleTextSize:
                    ruleTextSize = typedArray.getDimensionPixelSize(type, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.DoubleSlideSeekBar_ruleLineHeight:
                    ruleLineHeight = (int) typedArray.getDimension(type, dip2px(getContext(), 10));
                    break;
                case R.styleable.DoubleSlideSeekBar_bigValue:
                    bigValue = typedArray.getInteger(type, 100);
                    break;
                case R.styleable.DoubleSlideSeekBar_smallValue:
                    smallValue = typedArray.getInteger(type, 100);
                    break;


                default:
                    break;
            }
        }
        typedArray.recycle();
        init();
    }

    private void init() {
        if (bitmapBackGround == null) {
            bitmapBackGround = BitmapFactory.decodeResource(getResources(), R.drawable.sound_wave);
        }

        /**�α�ͼƬ����ʵ�߶� ֮��ͨ�����ű������԰�ͼƬ���ó���Ҫ�Ĵ�С*/
        bitmapHeight = bitmapLow.getHeight();
        bitmapWidth = bitmapLow.getWidth();
        // ������Ҫ�Ĵ�С
        int newWidth = imageWidth;
        int newHeight = imageHeight;
        // �������ű���
        float scaleWidth = ((float) newWidth) / bitmapWidth;
        float scaleHeight = ((float) newHeight) / bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        /**����ͼƬ*/
        bitmapLow = Bitmap.createBitmap(bitmapLow, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        bitmapBig = Bitmap.createBitmap(bitmapBig, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        /**���»�ȡ�α�ͼƬ�Ŀ��*/
        bitmapHeight = bitmapLow.getHeight();
        bitmapWidth = bitmapLow.getWidth();
        /**��ʼ�������α��λ��*/
        slideLowX = lineStart;
        slideBigX = lineEnd;
        smallRange = smallValue;
        bigRange = bigValue;
        if (hasRule) {
            //�п̶�ʱ paddingTop Ҫ���ϣ�text�߶ȣ��ͣ��̶��߸߶ȼӿ̶����ϱ����ֵĸ߶Ⱥͣ� ֮������ֵ
            paddingTop = paddingTop + Math.max(textSize, ruleLineHeight + ruleTextSize);
        } else {
            //û�п̶�ʱ paddingTop ���� text�ĸ߶�
            paddingTop = paddingTop + textSize;
        }

        bitmapPaint = new Paint();

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);

        curcorProgressPaint = new Paint();
        curcorProgressPaint.setAntiAlias(true);
        curcorProgressPaint.setStrokeWidth(3);
        curcorProgressPaint.setStyle(Paint.Style.FILL);
        curcorProgressPaint.setColor(Color.RED);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMyMeasureWidth(widthMeasureSpec);
        int height = getMyMeasureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int getMyMeasureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            // matchparent ���� �̶���С view��СӦΪ paddingBottom + paddingTop + bitmapHeight + 10 ������ʾ��ȫ
//            size = Math.max(size, paddingBottom + paddingTop + bitmapHeight + 10);
            size = Math.max(size, paddingBottom + paddingTop + bitmapHeight);
        } else {
            //wrap content
//            int height = paddingBottom + paddingTop + bitmapHeight + 10;
            int height = paddingBottom + paddingTop + bitmapHeight;
            size = Math.min(size, height);
        }
        return size;
    }

    private int getMyMeasureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {

            size = Math.max(size, paddingLeft + paddingRight + bitmapWidth * 2);

        } else {
            //wrap content
            int width = paddingLeft + paddingRight + bitmapWidth * 2;
            size = Math.min(size, width);
        }
        // match parent ���� �̶���С ��ʱ���Ի�ȡ�ߣ����������ĳ���
        lineStart = paddingLeft + bitmapWidth;
        lineLength = size - paddingLeft - paddingRight - bitmapWidth*2;
        //�ߣ����������Ľ���λ��
        lineEnd = lineLength + lineStart;
        //�ߣ����������Ŀ�ʼλ��
        //��ʼ�� �α�λ��
        slideLowX = lineStart;
        slideBigX = lineEnd;

        float scaleWidth = ((float) lineLength) / bitmapBackGround.getWidth();
        float scaleHeight = ((float) bitmapHeight) / bitmapBackGround.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        /**����ͼƬ*/
        bitmapBackGround = Bitmap.createBitmap(bitmapBackGround, 0, 0, bitmapBackGround.getWidth(), bitmapBackGround.getHeight(), matrix, true);
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        lineY = getHeight() - paddingBottom - bitmapHeight / 2;
        textHeight = lineY - bitmapHeight / 2 - 10;
        //�Ƿ񻭿̶�
        if (hasRule) {
            drawRule(canvas);
        }
//        canvas.drawBitmap(bitmapBackGround, lineStart, getHeight()-bitmapHeight, null);
        canvas.drawBitmap(bitmapBackGround, paddingLeft+bitmapWidth, getHeight()-bitmapHeight, new Paint());
        if (linePaint == null) {
            linePaint = new Paint();
            linePaint.setAntiAlias(true);
            linePaint.setStrokeWidth(bitmapHeight);
        }

        linePaint.setColor(inColor);
        canvas.drawLine(slideLowX, lineY, slideBigX, lineY, linePaint);

        linePaint.setColor(outColor);
        canvas.drawLine(lineStart, lineY, slideLowX, lineY, linePaint);
        canvas.drawLine(slideBigX, lineY, lineEnd, lineY, linePaint);

        canvas.drawBitmap(bitmapLow, slideLowX - bitmapWidth / 2, lineY - bitmapHeight / 2, bitmapPaint);
        canvas.drawBitmap(bitmapBig, slideBigX - bitmapWidth / 2, lineY - bitmapHeight / 2, bitmapPaint);

        canvas.drawText(formatTime((int)smallRange*1000), slideLowX - bitmapWidth*3/2 , textHeight, textPaint);
        canvas.drawText(formatTime((int)bigRange*1000), slideBigX - bitmapWidth / 4, textHeight, textPaint);
//        canvas.drawText(DateTimeUtils.stringForTime(smallRange), slideLowX - bitmapWidth / 2, textHeight, textPaint);
//        canvas.drawText(DateTimeUtils.stringForTime(bigRange), slideBigX - bitmapWidth / 2, textHeight, textPaint);
//        canvas.drawText(String.format("%.0f" + unit, smallRange), slideLowX - bitmapWidth / 2, textHeight, textPaint);
//        canvas.drawText(String.format("%.0f" + unit, bigRange), slideBigX - bitmapWidth / 2, textHeight, textPaint);

        if(mDrawProgress){
            canvas.drawLine(slideLowX+bitmapWidth/2+(slideBigX - slideLowX-bitmapWidth)*mProgress,getHeight()-bitmapHeight,
                    slideLowX+bitmapWidth/2+(slideBigX - slideLowX-bitmapWidth)*mProgress, getHeight(),curcorProgressPaint);
        }
    }

    private String formatTime(int second) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式。
        return formatter.format((long) second);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //�¼�����
        super.onTouchEvent(event);
        float nowX = event.getX();
        float nowY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDrawProgress = false;
                if (onRangeListener != null) {
                    onRangeListener.onRangeTouchDown();
                }
                //���� ���α귶Χ��
                boolean rightY = Math.abs(nowY - lineY) < bitmapHeight / 2;
                //���� ������α���
                boolean lowSlide = Math.abs(nowX - slideLowX) < bitmapWidth / 2;
                //���� ���ұ��α���
                boolean bigSlide = Math.abs(nowX - slideBigX) < bitmapWidth / 2;
                if (rightY && lowSlide) {
                    isLowerMoving = true;
                } else if (rightY && bigSlide) {
                    isUpperMoving = true;
                    //������α��ⲿ ������
                } else if (nowX >= lineStart && nowX <= slideLowX - bitmapWidth / 2 && rightY) {
                    slideLowX = (int) nowX;
                    updateRange();
                    postInvalidate();
                } else if (nowX <= lineEnd && nowX >= slideBigX + bitmapWidth / 2 && rightY) {
                    slideBigX = (int) nowX;
                    updateRange();
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //����α����˶�״̬
                if (isLowerMoving) {
                    //��ǰ X���������� �����ұ��α�����
                    if (nowX <= slideBigX - bitmapWidth && nowX >= lineStart - bitmapWidth / 2) {
                        slideLowX = (int) nowX;
                        if (slideLowX < lineStart) {
                            slideLowX = lineStart;
                        }
                        //���½���
                        updateRange();
                        postInvalidate();
                    }

                } else if (isUpperMoving) {
                    //��ǰ X���������� ��������α���ұ�
                    if (nowX >= slideLowX + bitmapWidth && nowX <= lineEnd + bitmapWidth / 2) {
                        slideBigX = (int) nowX;
                        if (slideBigX > lineEnd) {
                            slideBigX = lineEnd;
                        }
                        //���½���
                        updateRange();
                        postInvalidate();

                    }
                }
                break;
            //��ָ̧��
            case MotionEvent.ACTION_UP:
                isUpperMoving = false;
                isLowerMoving = false;
                smallRange = computRange(slideLowX);
                bigRange = computRange(slideBigX);
                if (onRangeListener != null) {
                    onRangeListener.onRangeTouchUp(smallRange, bigRange);
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void updateRange() {
        //��ǰ ����α���ֵ
        smallRange = computRange(slideLowX);
        //��ǰ �ұ��α���ֵ
        bigRange = computRange(slideBigX);
        //�ӿ� ʵ��ֵ�Ĵ���
        if (onRangeListener != null) {
            onRangeListener.onRange(smallRange, bigRange);
        }
    }

    /**
     * ��ȡ��ǰֵ
     */
    private float computRange(float range) {
        return (range - lineStart) * (bigValue - smallValue) / lineLength + smallValue;
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * ���̶�
     */
    protected void drawRule(Canvas canvas) {
        if (paintRule == null) {
            paintRule = new Paint();
        }
        paintRule.setStrokeWidth(1);
        paintRule.setTextSize(ruleTextSize);
        paintRule.setTextAlign(Paint.Align.CENTER);
        paintRule.setAntiAlias(true);
        //���� equal��,���̶�
        for (int i = smallValue; i <= bigValue; i += (bigValue - smallValue) / equal) {
            float degX = lineStart + i * lineLength / (bigValue - smallValue);
            int degY = lineY - ruleLineHeight;
            paintRule.setColor(ruleColor);
            canvas.drawLine(degX, lineY, degX, degY, paintRule);
            paintRule.setColor(ruleTextColor);
            canvas.drawText(String.valueOf(i) + ruleUnit, degX, degY, paintRule);
        }
    }

    /**
     * д���ӿ� �������������Сֵ
     */
    public interface onRangeListener {
        void onRangeTouchDown();
        void onRange(float low, float big);
        void onRangeTouchUp(float low, float big);
    }

    private onRangeListener onRangeListener;

    public void setOnRangeListener(DoubleSlideSeekBar.onRangeListener onRangeListener) {
        this.onRangeListener = onRangeListener;
    }
    //����ѡ���������ֵ 

	public void setBigValue(int bigValue) {
		this.bigValue = bigValue;
	}
	//����ѡ����������ֵ

	public void setBigRange(float bigRange) {
		this.bigRange = bigRange;
	}

	private boolean mDrawProgress = false;
	private float mProgress;
	public void setPlayProgress(float progress){
        mProgress = progress;
        mDrawProgress = true;
        postInvalidate();
    }

}

