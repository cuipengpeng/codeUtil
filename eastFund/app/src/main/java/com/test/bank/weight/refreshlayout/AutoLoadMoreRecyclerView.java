package com.test.bank.weight.refreshlayout;


import android.content.Context;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.test.bank.R;
import com.test.bank.utils.LogUtils;

/**
 * Created by jingbin on 2016/1/28.
 */
public class AutoLoadMoreRecyclerView extends RecyclerView {
    private WrapAdapter mWrapAdapter;
    private SparseArray<View> mHeaderViews = new SparseArray<View>();
    private SparseArray<View> mFootViews = new SparseArray<View>();
    private boolean pullRefreshEnabled = false; //默认不刷新
    private boolean loadingMoreEnabled = true;
    private YunRefreshHeader mRefreshHeader;
    public boolean isLoadingData;
    public int previousTotal;
    public boolean isnomore;
    private float mLastY = -1;
    private static final float DRAG_RATE = 1.75f;
    // 是否是额外添加FooterView
    private boolean isOther = false;
    private int offsetToLoadMore = 3;   //默认剩余两条加载更多

    public static boolean isRefreshing = true;

    public AutoLoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public AutoLoadMoreRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoLoadMoreRecyclerView);
        pullRefreshEnabled = typedArray.getBoolean(R.styleable.AutoLoadMoreRecyclerView_refreshEnable, false);
        offsetToLoadMore = typedArray.getInteger(R.styleable.AutoLoadMoreRecyclerView_offsetToLoadMore, 2);
        if (pullRefreshEnabled) {
            YunRefreshHeader refreshHeader = new YunRefreshHeader(context);
            mHeaderViews.put(0, refreshHeader);
            mRefreshHeader = refreshHeader;
        }
        LoadingMoreFooter footView = new LoadingMoreFooter(context);
        addFootView(footView, false);
        mFootViews.get(0).setVisibility(GONE);
    }

    /**
     * 改为公有。供外添加view使用,使用标识
     * 注意：使用后不能使用 上拉加载，否则添加无效
     * 使用时 isOther 传入 true，然后调用 noMoreLoading即可。
     */
    public void addFootView(final View view, boolean isOther) {
        mFootViews.clear();
        mFootViews.put(0, view);
        this.isOther = isOther;
    }

    /**
     * 设置脚布局背景颜色
     *
     * @param footerBgColor：#ffffff
     */
    public void setFooterBgColor(String footerBgColor) {
        if (mFootViews.size() == 0 || mFootViews.get(0) == null) {
            return;
        }
        if (mFootViews.get(0) instanceof LoadingMoreFooter) {
            LoadingMoreFooter moreFooter = (LoadingMoreFooter) mFootViews.get(0);
            moreFooter.setBgColor(footerBgColor);
        }
    }

    /**
     * 相当于加一个空白头布局：
     * 只有一个目的：为了滚动条显示在最顶端
     * 因为默认加了刷新头布局，不处理滚动条会下移。
     * 和 setPullRefreshEnabled(false) 一块儿使用
     * 使用下拉头时，此方法不应被使用！
     */
    public void clearHeader() {
        mHeaderViews.clear();
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int height = (int) (1.0f * scale + 0.5f);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        View view = new View(getContext());
        view.setLayoutParams(params);
        mHeaderViews.put(0, view);
    }

    public void addHeaderView(View view) {
        if (pullRefreshEnabled && !(mHeaderViews.get(0) instanceof YunRefreshHeader)) {
            YunRefreshHeader refreshHeader = new YunRefreshHeader(getContext());
            mHeaderViews.put(0, refreshHeader);
            mRefreshHeader = refreshHeader;
        }
        mHeaderViews.put(mHeaderViews.size(), view);
    }

    private void loadMoreComplete() {
        isLoadingData = false;
        View footView = mFootViews.get(0);
        if (previousTotal <= getLayoutManager().getItemCount()) {
            if (footView instanceof LoadingMoreFooter) {
                ((LoadingMoreFooter) footView).setState(LoadingMoreFooter.STATE_COMPLETE);
            } else {
                footView.setVisibility(View.GONE);
            }
        } else {
            if (footView instanceof LoadingMoreFooter) {
                ((LoadingMoreFooter) footView).setState(LoadingMoreFooter.STATE_NOMORE);
            } else {
                footView.setVisibility(View.GONE);
            }
            isnomore = true;
        }
        previousTotal = getLayoutManager().getItemCount();
    }

    public void noMoreLoading() {
        LogUtils.e("AutoLoadMore.noMoreLoading......");
        isLoadingData = false;
        final View footView = mFootViews.get(0);
        isnomore = true;
        if (footView instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) footView).setState(LoadingMoreFooter.STATE_NOMORE);
        } else {
            footView.setVisibility(View.GONE);
        }
        // 额外添加的footView
        if (isOther) {
            footView.setVisibility(View.VISIBLE);
        }
    }

    public void refreshComplete() {
        //  mRefreshHeader.refreshComplate();
        if (isLoadingData) {
            loadMoreComplete();
        } else {
            isRefreshing = false;
            if (mRefreshHeader != null)
                mRefreshHeader.refreshComplate();
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(mHeaderViews, mFootViews, adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
    }

    public WrapAdapter getWrapAdapter() {
        return mWrapAdapter;
    }

    private static final String TAG = "XRecyclerView";

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        if (state == RecyclerView.SCROLL_STATE_IDLE && onLoadMoreListener != null && !isLoadingData && loadingMoreEnabled) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
//            Log.e(TAG, "onScrollStateChanged: lastVisibleItemPosition: " + lastVisibleItemPosition + " itemCount: " + layoutManager.getItemCount() + " offsetToLoadMore: " + offsetToLoadMore);
            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - offsetToLoadMore
//                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1
                    && layoutManager.getItemCount() > layoutManager.getChildCount()
                    && !isnomore
                    && isBeforeRefreshing()) {

                View footView = mFootViews.get(0);
                isLoadingData = true;
                if (footView instanceof LoadingMoreFooter) {
                    ((LoadingMoreFooter) footView).setState(LoadingMoreFooter.STATE_LOADING);
                } else {
                    footView.setVisibility(View.VISIBLE);
                }
                if (isNetWorkConnected(getContext())) {
                    Log.e(TAG, "onScrollStateChanged: 加载更多。。。。。。。。。。。。。");
                    onLoadMoreListener.onLoadMore();
                } else {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "onScrollStateChanged: 加载更多。。。。。。。。。。。。。");
                            onLoadMoreListener.onLoadMore();
                        }
                    }, 1000);
                }
            }
        }
    }

    private boolean isBeforeRefreshing() {
        if (pullRefreshEnabled) {
            return mRefreshHeader.getState() < YunRefreshHeader.STATE_REFRESHING;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop() && pullRefreshEnabled) {
                    mRefreshHeader.onMove(deltaY / DRAG_RATE);
                    if (mRefreshHeader.getVisiableHeight() > 0 && mRefreshHeader.getState() < YunRefreshHeader.STATE_REFRESHING) {
                        return false;
                    }
                }
                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && pullRefreshEnabled) {
                    if (mRefreshHeader.releaseAction()) {
                        if (onRefreshListener != null) {
                            isRefreshing = true;
                            onRefreshListener.onRefresh();
                            isnomore = false;
                            previousTotal = 0;
                            final View footView = mFootViews.get(0);
                            if (footView instanceof LoadingMoreFooter) {
                                if (footView.getVisibility() != View.GONE) {
                                    footView.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private int findMin(int[] firstPositions) {
        int min = firstPositions[0];
        for (int value : firstPositions) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public boolean isOnTop() {
        if (mHeaderViews == null || mHeaderViews.size() == 0) {
            return false;
        }

        View view = mHeaderViews.get(0);
        if (view.getParent() != null) {
            return true;
        } else {
            return false;
        }
    }

    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            mWrapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };

    public void setPullRefreshEnabled(boolean pullRefreshEnabled) {
        this.pullRefreshEnabled = pullRefreshEnabled;
    }

    public void setLoadingMoreEnabled(boolean loadingMoreEnabled) {
        this.loadingMoreEnabled = loadingMoreEnabled;
        if (!loadingMoreEnabled) {
            if (mFootViews != null) {
                mFootViews.remove(0);
            }
        } else {
            if (mFootViews != null) {
                LoadingMoreFooter footView = new LoadingMoreFooter(getContext());
                addFootView(footView, false);
            }
        }
    }


    public void setLoadMoreGone() {
        if (mFootViews == null) {
            return;
        }
        View footView = mFootViews.get(0);
        if (footView != null && footView instanceof LoadingMoreFooter) {
            mFootViews.remove(0);
        }
    }

    private OnRefreshListener onRefreshListener;

    private OnLoadMoreListener onLoadMoreListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public void reset() {
        isnomore = false;
        previousTotal = 0;
        final View footView = mFootViews.get(0);
        if (footView instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) footView).reSet();
        }
    }
}
