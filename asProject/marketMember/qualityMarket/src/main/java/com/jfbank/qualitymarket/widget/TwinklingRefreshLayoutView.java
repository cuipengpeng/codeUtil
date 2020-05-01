package com.jfbank.qualitymarket.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.widget.refresh.RefreshFooterView;
import com.jfbank.qualitymarket.widget.refresh.RefreshHeadView;
import com.lcodecore.tkrefreshlayout.AnimProcessor;
import com.lcodecore.tkrefreshlayout.Footer.BottomProgressView;
import com.lcodecore.tkrefreshlayout.IBottomView;
import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;
import com.lcodecore.tkrefreshlayout.OverScrollProcessor;
import com.lcodecore.tkrefreshlayout.PullListener;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.RefreshProcessor;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.GoogleDotView;
import com.lcodecore.tkrefreshlayout.utils.DensityUtil;
import com.lcodecore.tkrefreshlayout.utils.ScrollingUtil;


/**
 * 功能：自定义下拉刷新组件<br>
 * 作者：赵海<br>
 * 时间： 2017/5/2 0002<br>.
 * 版本：1.2.0
 */

public class TwinklingRefreshLayoutView extends TwinklingRefreshLayout {
    TextView tvNodataDes;
    Context mContext;
    RefreshHeadView refreshHeadView;
    RefreshFooterView refreshFooterView;

    public TwinklingRefreshLayoutView(Context context) {
        super(context);
        initMoreView(context);
    }

    public TwinklingRefreshLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMoreView(context);
    }

    public TwinklingRefreshLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMoreView(context);
    }

    private void initMoreView(Context context) {
        this.mContext = context;
        refreshHeadView = new RefreshHeadView(mContext);
        setHeaderView(refreshHeadView);
        refreshFooterView = new RefreshFooterView(mContext);
        setBottomView(refreshFooterView);
    }

    @Override
    public void setBottomView(IBottomView bottomView) {

        super.setBottomView(bottomView);
    }

    @Override
    public void setHeaderView(IHeaderView headerView) {
        super.setHeaderView(headerView);
    }

    public void setHeaderView(RefreshHeadView.OnScrolListener scrolListener) {
        refreshHeadView = new RefreshHeadView(mContext, scrolListener);
        setHeaderView(refreshHeadView);
    }

    @Override
    public void setEnableLoadmore(boolean enableLoadmore1) {
        this.enableLoadmore = enableLoadmore1;
        if (enableLoadmore) {
            refreshFooterView.reSetView();
        } else {
            refreshFooterView.setNoLoadMore();
        }
    }

    public void setEnableLoadmore(boolean enableLoadmore1, boolean isShowNoadMore) {
        this.enableLoadmore = enableLoadmore1;
        if (enableLoadmore) {
            refreshFooterView.reSetView();
        } else {
            refreshFooterView.setShowNoLoadMore(isShowNoadMore);
        }

    }

    public void setEnableLoadmore(boolean enableLoadmore1, String msg) {
        this.enableLoadmore = enableLoadmore1;
        if (enableLoadmore) {
            refreshFooterView.reSetView();
        } else {
            refreshFooterView.setNoLoadMore(msg);
        }
    }

    @Override
    public void finishLoadmore() {
        super.finishLoadmore();
    }

    @Override
    public void finishRefreshing() {
        super.finishRefreshing();
    }
}
