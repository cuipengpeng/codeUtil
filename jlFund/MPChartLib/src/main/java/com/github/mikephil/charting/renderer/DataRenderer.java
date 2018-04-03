
package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Superclass of all render classes for the different data types (line, bar, ...).
 *
 * @author Philipp Jahoda
 */
public abstract class DataRenderer extends Renderer {

    private static final String TAG = "DataRenderer";

    /**
     * the animator object used to perform animations on the chart data
     */
    protected ChartAnimator mAnimator;

    /**
     * main paint object used for rendering
     */
    protected Paint mRenderPaint;

    /**
     * paint used for highlighting values
     */
    protected Paint mHighlightPaint;

    protected Paint mDrawPaint;

    /**
     * paint object for drawing values (text representing values of chart
     * entries)
     */
    protected Paint mValuePaint;

    protected Paint saleAndBuyPointPaint;

    public DataRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(viewPortHandler);
        this.mAnimator = animator;

        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Style.FILL);

        mDrawPaint = new Paint(Paint.DITHER_FLAG);

        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setColor(Color.rgb(63, 63, 63));
        mValuePaint.setTextAlign(Align.CENTER);
        mValuePaint.setTextSize(Utils.convertDpToPixel(9f));

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));

        saleAndBuyPointPaint = new Paint();
        saleAndBuyPointPaint.setAntiAlias(true);
        saleAndBuyPointPaint.setColor(Color.WHITE);
        saleAndBuyPointPaint.setTextSize(Utils.convertDpToPixel(12));
        saleAndBuyPointPaint.setFakeBoldText(true);
    }

    protected boolean isDrawingValuesAllowed(ChartInterface chart) {
        return chart.getData().getEntryCount() < chart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX();
    }

    /**
     * Returns the Paint object this renderer uses for drawing the values
     * (value-text).
     *
     * @return
     */
    public Paint getPaintValues() {
        return mValuePaint;
    }

    /**
     * Returns the Paint object this renderer uses for drawing highlight
     * indicators.
     *
     * @return
     */
    public Paint getPaintHighlight() {
        return mHighlightPaint;
    }

    /**
     * Returns the Paint object used for rendering.
     *
     * @return
     */
    public Paint getPaintRender() {
        return mRenderPaint;
    }

    /**
     * Applies the required styling (provided by the DataSet) to the value-paint
     * object.
     *
     * @param set
     */
    protected void applyValueTextStyle(IDataSet set) {

        mValuePaint.setTypeface(set.getValueTypeface());
        mValuePaint.setTextSize(set.getValueTextSize());
    }

    /**
     * Initializes the buffers used for rendering with a new size. Since this
     * method performs memory allocations, it should only be called if
     * necessary.
     */
    public abstract void initBuffers();

    /**
     * Draws the actual data in form of lines, bars, ... depending on Renderer subclass.
     *
     * @param c
     */
    public abstract void drawData(Canvas c);

    /**
     * Loops over all Entrys and draws their values.
     *
     * @param c
     */
    public abstract void drawValues(Canvas c);

    /**
     * Draws the value of the given entry by using the provided IValueFormatter.
     *
     * @param c            canvas
     * @param formatter    formatter for custom value-formatting
     * @param value        the value to be drawn
     * @param entry        the entry the value belongs to
     * @param dataSetIndex the index of the DataSet the drawn Entry belongs to
     * @param x            position
     * @param y            position
     * @param color
     */
    public void drawValue(Canvas c, IValueFormatter formatter, float value, Entry entry, int dataSetIndex, float x, float y, int color) {
        mValuePaint.setColor(color);
        c.drawText(formatter.getFormattedValue(value, entry, dataSetIndex, mViewPortHandler), x, y, mValuePaint);
    }

    /**
     * Draws any kind of additional information (e.g. line-circles).
     *
     * @param c
     */
    public abstract void drawExtras(Canvas c);

    /**
     * Draws all highlight indicators for the values that are currently highlighted.
     *
     * @param c
     * @param indices the highlighted values
     */
    public abstract void drawHighlighted(Canvas c, Highlight[] indices);


    protected OnTransferEntryToRealPointListener onTransferEntryToRealPointListener;

    public void setOnTransferEntryToRealPointListener(OnTransferEntryToRealPointListener onTransferEntryToRealPointListener) {
        this.onTransferEntryToRealPointListener = onTransferEntryToRealPointListener;
    }

    /**
     * 在drawLinear的时候，将Entry坐标点转换为屏幕中真实坐标的回调
     * 注意：该回调是在 LineChartRender 中的 drawCircle 方法中进行的回调处理。
     * 务必在有获取Entry对应的真实坐标需求的情况下设置该回调。
     * 如果用户设置了该回调，则不管是否开启 drawCircle 都会进行坐标转换。
     * 如果用户没有设置该回调，并且关闭了 dataSet.setDrawCircles(false); 则不会做任何多余的工作。
     * 如果用户没有设置该回调，并且开启了 dataSet.setDrawCircles(true); 则会做正常的工作。
     */
    public interface OnTransferEntryToRealPointListener {
        void onTransferToPoint(Entry entry, PointF pointF);

        void onTransferFinished();
    }

    Bitmap buyPointBgLeftBitmap;
    Bitmap buyPointBgRightBitmap;
    Bitmap buyCirlcePointBitmap;

    public void setBuyPointBgLeft(Bitmap buyPointBgLeftBitmap) {
        this.buyPointBgLeftBitmap = buyPointBgLeftBitmap;
    }

    public void setBuyPointBgRight(Bitmap buyPointBgRightBitmap) {
        this.buyPointBgRightBitmap = buyPointBgRightBitmap;
    }

    public void setBuyPointCircle(Bitmap buyCirclePointBitmap) {
        this.buyCirlcePointBitmap = buyCirclePointBitmap;
    }


    Bitmap salePointBgLeftBitmap;
    Bitmap salePointBgRightBitmap;
    Bitmap saleCirlcePointBitmap;

    public void setSalePointBgLeft(Bitmap salePointBgLeftBitmap) {
        this.salePointBgLeftBitmap = salePointBgLeftBitmap;
    }

    public void setSalePointBgRight(Bitmap salePointBgRightBitmap) {
        this.salePointBgRightBitmap = salePointBgRightBitmap;
    }

    public void setSaleCirclePoint(Bitmap saleCirclePointBitmap) {
        this.saleCirlcePointBitmap = saleCirclePointBitmap;
    }

    protected LineChart.BuyInAndSaleOutInfo buyInPointInfo;
    protected LineChart.BuyInAndSaleOutInfo saleOutPointInfo;

    public LineChart.OnGetBuyInAndSaleOutPointListener onGetBuyInAndSaleOutPointListener;

    public void setOnGetBuyInAndSaleOutPointListener(LineChart.OnGetBuyInAndSaleOutPointListener onGetBuyInAndSaleOutPointListener) {
        this.onGetBuyInAndSaleOutPointListener = onGetBuyInAndSaleOutPointListener;
    }
}
