
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.renderer.LineChartRenderer;

/**
 * Chart that draws lines, surfaces, circles, ...
 *
 * @author Philipp Jahoda
 */
public class LineChart extends BarLineChartBase<LineData> implements LineDataProvider {
    private static final String TAG = "LineChart";

    public LineChart(Context context) {
        super(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new LineChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    public LineData getLineData() {
        return mData;
    }

    @Override
    protected void onDetachedFromWindow() {
        // releases the bitmap in the renderer to avoid oom error
        if (mRenderer != null && mRenderer instanceof LineChartRenderer) {
            ((LineChartRenderer) mRenderer).releaseBitmap();
        }
        super.onDetachedFromWindow();
    }

    /**
     * 在drawCircle的时候，将Entry坐标点转换为屏幕中真实坐标的回调
     * 注意：该回调是在 LineChartRender 中的 drawCircle 方法中进行的回调处理。
     * 务必在有获取Entry对应的真实坐标需求的情况下设置该回调。
     * 如果用户设置了该回调，则不管是否开启 drawCircle 都会进行坐标转换。
     * 如果用户没有设置该回调，并且关闭了 dataSet.setDrawCircles(false); 则不会做任何多余的工作。
     * 如果用户没有设置该回调，并且开启了 dataSet.setDrawCircles(true); 则会做正常的工作。
     */
    public void setEntryPointFTransferListener(DataRenderer.OnTransferEntryToRealPointListener onTransferEntryToRealPointListener) {
        mRenderer.setOnTransferEntryToRealPointListener(onTransferEntryToRealPointListener);
    }

    public void setBuyPointBgLeft(Bitmap buyPointBgLeft) {
        if (mRenderer != null) {
            mRenderer.setBuyPointBgLeft(buyPointBgLeft);
        }
    }

    public void setBuyPointBgRight(Bitmap buyPointBgRight) {
        if (mRenderer != null) {
            mRenderer.setBuyPointBgRight(buyPointBgRight);
        }
    }

    public void setBuyPointCircle(Bitmap buyCirclePoint) {
        if (mRenderer != null) {
            mRenderer.setBuyPointCircle(buyCirclePoint);
        }
    }

    public void setSalePointBgLeft(Bitmap salePointBgLeft) {
        if (mRenderer != null) {
            mRenderer.setSalePointBgLeft(salePointBgLeft);
        }
    }

    public void setSalePointBgRight(Bitmap salePointBgRight) {
        if (mRenderer != null) {
            mRenderer.setSalePointBgRight(salePointBgRight);
        }
    }

    public void setSaleCirclePoint(Bitmap saleCirclePoint) {
        if (mRenderer != null) {
            mRenderer.setSaleCirclePoint(saleCirclePoint);
        }
    }

    public void setOnGetBuyInAndSaleOutPointListener(OnGetBuyInAndSaleOutPointListener onGetBuyInAndSaleOutPointListener) {
        if (mRenderer != null) {
            mRenderer.setOnGetBuyInAndSaleOutPointListener(onGetBuyInAndSaleOutPointListener);
        }
    }

    public interface OnGetBuyInAndSaleOutPointListener {
        void OnGetPoint(BuyInAndSaleOutInfo buyInPointInfo,BuyInAndSaleOutInfo saleOutPointInfo);
    }

    public static class BuyInAndSaleOutInfo {
        public float x;
        public float y;
        public boolean isLeft;

        public BuyInAndSaleOutInfo() {
        }

        public BuyInAndSaleOutInfo(float x, float y, boolean isLeft) {
            this.x = x;
            this.y = y;
            this.isLeft = isLeft;
        }

        @Override
        public String toString() {
            return "BuyInAndSaleOutBean{" +
                    "x=" + x +
                    ", y=" + y +
                    ", isLeft=" + isLeft +
                    '}';
        }
    }
}
