package com.test.xcamera.album;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import com.test.xcamera.R;


/**
 * 自定义View继承SwipeRefreshLayout，添加上拉加载更多的布局属性,添加对RecyclerView的支持
 * Created by Pinger on 2016/9/26.
 */
public class SwipeRefreshView extends SwipeRefreshLayout {

    private static final String TAG = SwipeRefreshView.class.getSimpleName();
    private final int mScaledTouchSlop;
    private final View mFooterView;
    private ListView mListView;
    private OnLoadMoreListener mListener;

    /**
     * 正在加载状态
     */
    private boolean isLoading;
    private RecyclerView mRecyclerView;
    private int mItemCount;

    public SwipeRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 填充底部加载布局
        mFooterView = View.inflate(context, R.layout.view_footer, null);
        // 表示控件移动的最小距离，手移动的距离大于这个距离才能拖动控件
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //this.addView(mFooterView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 获取ListView,设置ListView的布局位置
        if (mListView == null || mRecyclerView == null) {
            // 判断容器有多少个孩子
            if (getChildCount() > 0) {
                // 判断第一个孩子是不是ListView
                if (getChildAt(0) instanceof ListView) {
                    // 创建ListView对象
                    mListView = (ListView) getChildAt(0);

                    // 设置ListView的滑动监听
                    setListViewOnScroll();
                } else if (getChildAt(0) instanceof RecyclerView) {
//                    testScrollView = (TestScrollView) getChildAt(0);

//                    mRecyclerView = (RecyclerView) ((LinearLayout) ((TestScrollView) getChildAt(0)).getChildAt(0)).getChildAt(0);
                    // 创建ListView对象
                    mRecyclerView = (RecyclerView) getChildAt(0);
                    // 设置RecyclerView的滑动监听
                    setRecyclerViewOnScroll();
                }
            }
        }
    }


    /**
     * 在分发事件的时候处理子控件的触摸事件
     */
    private float mDownY, mUpY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 移动的起点
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 移动过程中判断时候能下拉加载更多
                mUpY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                // 移动的终点
                Log.i("ZHOUXUECHENGTestSc", "已经抬起了");
                mUpY = ev.getY();
                if (canLoadMore()) {
                    Log.i("ZHOUXUECHENGTestSc", "现在应该是加载的");
                    // 加载数据
                    loadData();
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判断是否满足加载更多条件
     */
    private boolean canLoadMore() {
        // 1. 是上拉状态
        boolean condition1 = (mDownY - mUpY) >= mScaledTouchSlop;
        if (condition1) {

        }

        // 2. 当前页面可见的item是最后一个条目,一般最后一个条目位置需要大于第一页的数据长度
        boolean condition2 = false;
//        Log.d(TAG, "------->  3333333333333   " + testScrollView.isScrolledToBottom());
        if (mListView != null && mListView.getAdapter() != null) {
            if (mItemCount > 0) {
                if (mListView.getAdapter().getCount() < mItemCount) {
                    // 第一页未满，禁止下拉
                    condition2 = false;
                } else {
                    condition2 = mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
                }
            } else {
                // 未设置数据长度，则默认第一页数据不满时也可以上拉
                condition2 = mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
            }

        } else if (mRecyclerView != null && mRecyclerView.getAdapter() != null && mRecyclerView.getLayoutManager() != null /*&& testScrollView.isScrolledToBottom()*/) {

            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

            if (lastVisibleItemPosition + 1 == mRecyclerView.getAdapter().getItemCount()) {
                Log.d(TAG, "------->  4444444444444444");
                if (!isLoading) {
                    Log.d(TAG, "------->  4444444444444444");
                    condition2 = true;
                }
            }
        }

        if (condition2) {
//            Log.d(TAG, "------->  是最后一个条目");
        }
        // 3. 正在加载状态
        boolean condition3 = !isLoading;
        if (condition3) {
//            Log.d(TAG, "------->  不是正在加载状态");
        }
        return condition1 && condition2 && condition3;
    }

    public void setItemCount(int itemCount) {
        this.mItemCount = itemCount;
    }

    /**
     * 处理加载数据的逻辑
     */
    private void loadData() {
        if (mListener != null) {
            // 设置加载状态，让布局显示出来
//            setLoading(true);
            mListener.onLoadMore();
        }
    }

    /**
     * 设置加载状态，是否加载传入boolean值进行判断
     *
     * @param loading
     */
    public void setLoading(boolean loading) {
        // 修改当前的状态
        this.isLoading = loading;
        if (isLoading) {
            // 显示布局
        } else {
            // 隐藏布局
            // 重置滑动的坐标
            mDownY = 0;
            mUpY = 0;
        }
    }


    /**
     * 设置ListView的滑动监听
     */
    private void setListViewOnScroll() {

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 移动过程中判断时候能下拉加载更多
                Log.i("ZHOUXUECHENG", "jiazaigenduo 1111111111");
                if (canLoadMore()) {
                    // 加载数据
                    Log.i("ZHOUXUECHENG", "jiazaigenduo 222222222");
                    loadData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }


    /**
     * 设置RecyclerView的滑动监听
     */
    private void setRecyclerViewOnScroll() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 移动过程中判断时候能下拉加载更多
//                if (canLoadMore()) {
//                    // 加载数据
//                    loadData();
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    /**
     * 上拉加载的接口回调
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mListener = listener;
    }
}