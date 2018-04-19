package com.jfbank.qualitymarket.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.lcodecore.tkrefreshlayout.IBottomView;
import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/3/7 0007<br>.
 * 版本：1.2.0
 */

public class RefreshFooterView implements IBottomView {
    View rootView = null;
    Context mContext;
    ProgressBar pbLoadmore;
    TextView tvLoadmore;

    public RefreshFooterView(Context context) {
        this.mContext = context;
        init();
    }


    void init() {
        if (rootView == null) {
            rootView = View.inflate(mContext, R.layout.refresh_footer, null);
            pbLoadmore = (ProgressBar) rootView.findViewById(R.id.pb_loadmore);
            tvLoadmore = (TextView) rootView.findViewById(R.id.tv_loadmore);
        }
    }

    public void reSetView() {
        pbLoadmore.setVisibility(View.VISIBLE);
        tvLoadmore.setVisibility(View.VISIBLE);
        tvLoadmore.setText("正在加载更多...");
    }

    public void setNoLoadMore(String msg) {
        pbLoadmore.setVisibility(View.GONE);
        tvLoadmore.setVisibility(View.VISIBLE);
        tvLoadmore.setText(msg);
    }

    public void setShowNoLoadMore(boolean isShow) {
        if (isShow) {
            setNoLoadMore();
        } else {
            pbLoadmore.setVisibility(View.GONE);
            tvLoadmore.setVisibility(View.GONE);
        }
    }

    public void setNoLoadMore() {
        setNoLoadMore("没有更多了");
    }

    @Override
    public View getView() {
        return rootView;
    }

    @Override
    public void onPullingUp(float fraction, float maxHeadHeight, float headHeight) {

    }

    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {

    }

    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void reset() {
    }
}
