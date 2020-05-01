package com.jfbank.qualitymarket.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.banner.widget.Banner.base.BaseBanner;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.LoginActivity;
import com.jfbank.qualitymarket.activity.MyMessageActivity;
import com.jfbank.qualitymarket.activity.SearchGoodsActivity;
import com.jfbank.qualitymarket.adapter.HomeAdapter;
import com.jfbank.qualitymarket.adapter.HomeMsAdapter;
import com.jfbank.qualitymarket.adapter.HomeNavigationAdapter;
import com.jfbank.qualitymarket.base.BaseMvpFragment;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.helper.BillHelper;
import com.jfbank.qualitymarket.helper.CountTimerHelper;
import com.jfbank.qualitymarket.model.HomePageBean;
import com.jfbank.qualitymarket.model.MsProductBean;
import com.jfbank.qualitymarket.model.NavigationHomeBean;
import com.jfbank.qualitymarket.model.QualityNewsBean;
import com.jfbank.qualitymarket.mvp.HomeMVP;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.Response;
import com.jfbank.qualitymarket.util.AndroidVersionUtil;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.JumpUtil;
import com.jfbank.qualitymarket.util.TDUtils;
import com.jfbank.qualitymarket.widget.BadgeView;
import com.jfbank.qualitymarket.widget.BannerVew;
import com.jfbank.qualitymarket.widget.BetterRecyclerView;
import com.jfbank.qualitymarket.widget.FeedRootRecyclerView;
import com.jfbank.qualitymarket.widget.ForegroundLinearLayout;
import com.jfbank.qualitymarket.widget.MarqueeView;
import com.jfbank.qualitymarket.widget.NoScrollGridView;
import com.jfbank.qualitymarket.widget.TwinklingRefreshLayoutView;
import com.jfbank.qualitymarket.widget.refresh.RefreshHeadView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollStateListener;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static me.everything.android.ui.overscroll.IOverScrollState.STATE_BOUNCE_BACK;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_IDLE;

/**
 * 首页Fragment
 */
public class HomeFragment extends BaseMvpFragment<HomeMVP.Presenter, HomeMVP.Model> implements HomeMVP.View, View.OnClickListener {
    @InjectView(R.id.recycler_view)
    FeedRootRecyclerView recyclerView;  //整个滑动View
    @InjectView(R.id.refreshLayout)
    TwinklingRefreshLayoutView refreshLayout;//滑动还算新
    @InjectView(R.id.tv_message)
    TextView tvMessage;//消息
    @InjectView(R.id.ll_title_content)
    LinearLayout llTitleContent;//标题按钮
    @InjectView(R.id.ll_title_bg)
    LinearLayout llTitleBg;//标题背景按钮
    //    @InjectView(R.id.ll_title_deline)
//    View llTitleDeline;//标题分割线
    @InjectView(R.id.ll_title_bar)
    LinearLayout llTitleBar;
    @InjectView(R.id.iv_topview)
    ImageView ivTopview;//回到顶部按钮
    private BadgeView bageNumView;//消息条数

    ViewHeadHolder viewHeadHolder;//HeadView
    CountDownTimer countMsTimer;//秒杀倒计时
    boolean isLelfDrag = false;//是否向左滑动

    HomeMsAdapter homeMsAdapter;//快报Adapter
    HomeNavigationAdapter homeNavigationAdapter;//分类Itemm
    Target navigationBgTarget = null;//分类背景设置
    HomeAdapter adapter;//首页适配器（分类）
    Response<List<MsProductBean>> mMsProductBean;//秒杀对象

    float scale = 1f;//屏幕密度
    CustomLLLayoutManager linearLayoutManager;//布局模式
    boolean isInit = false;
    int floorcount;
    int floor;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);//事件总线注册
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_home;
    }

    /**
     * 初始化组件
     */
    @Override
    protected void initView() {
        //屏幕密度
        scale = mContext.getResources().getDisplayMetrics().density;

        //设置数据布局格式--线性布局
        linearLayoutManager = new CustomLLLayoutManager(getContext());
        linearLayoutManager.setSpeedRatio(0.68f);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        adapter = new HomeAdapter(mContext);
        SmartRecyclerAdapter smartRecyclerAdapter = new SmartRecyclerAdapter(adapter);
        smartRecyclerAdapter.setHeaderView(initHeadView());//添加Head

        //添加没有更多
        //设置适配器
        recyclerView.setAdapter(smartRecyclerAdapter);
        recyclerView.setHasFixedSize(true);
        //添加滑动事件监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                getScollYDistance();
//                onScrollUpdate();//改变标题透明度
            }
        });
        if (AndroidVersionUtil.hasM()) {
            LinearLayout.LayoutParams ivMsgParams = new LinearLayout.LayoutParams(-1, CommonUtils.dipToPx(mContext, 50));
            ivMsgParams.topMargin = CommonUtils.getStatusBarHeight(mContext);
            ivMsgParams.gravity = Gravity.RIGHT | Gravity.TOP;
            llTitleContent.setLayoutParams(ivMsgParams);
        }
        CommonUtils.setTitleColor(getActivity(), llTitleBar);
//        llSearch.getBackground().setAlpha(110);
        tvMessage.setSelected(false);//显示白色

        //消息提示
        bageNumView = new BadgeView(getContext());
        bageNumView.setBadgeCount(0);
        bageNumView.setVisibility(View.INVISIBLE);
        bageNumView.setBackground(8, getResources().getColor(R.color.c_FF3431));
        bageNumView.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
        bageNumView.setBadgeMargin(0, 0, 5, 10);
        bageNumView.setTextColor(getResources().getColor(R.color.white));
        bageNumView.setTargetView(tvMessage);
        bageNumView.setTextSize(8);

        //回调顶部按钮设置
        ivTopview.setVisibility(View.GONE);

        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setOverScrollBottomShow(true);

        //下拉标题隐藏监听动画效果
        refreshLayout.setHeaderView(new RefreshHeadView.OnScrolListener() {
            @Override
            public void onScrolltoUpY(float fraction, float headHeight) {//下拉隐藏标题
//                pulldownSetTitle(fraction, headHeight);
            }
        });
//        //下拉加载更多
//        refreshLayout.setBottomView(new RefreshFooterView(mContext));

        //下拉刷新和上啦加载更多
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {//下拉刷新
                refreshData(false, true);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {//加载更多
                isInit = false;
                mPresenter.getIndexData(false, false, floor + 1);
            }
        });
    }

    /**
     * 初始化headView组件
     *
     * @return
     */
    private View initHeadView() {
        //HeadView组件（banner、分类、秒杀、爆品抢购、快报）
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.item_home_head, null);
        viewHeadHolder = new ViewHeadHolder(headView);
        viewHeadHolder.llNotice.setVisibility(View.GONE);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        viewHeadHolder.rlList.setLayoutManager(linearLayoutManager);

        //设置适配器数据
        homeMsAdapter = new HomeMsAdapter(mContext);
        SmartRecyclerAdapter smartRecyclerAdapter = new SmartRecyclerAdapter(homeMsAdapter);
        smartRecyclerAdapter.setFooterView(LayoutInflater.from(getContext()).inflate(R.layout.item_ms_more, null));
        viewHeadHolder.rlList.setAdapter(smartRecyclerAdapter);
        homeNavigationAdapter = new HomeNavigationAdapter(mContext);
        viewHeadHolder.nsgvNavigation.setAdapter(homeNavigationAdapter);
        return headView;
    }

    @Override
    protected void initData() {
        //初始化消息数目
        updateMsgNumView(0);
        refreshData(true, false);
    }

    /**
     * 更新数据
     */
    private void refreshData(boolean isInit, boolean isFresh) {
        this.isInit = isInit;
        mPresenter.getIndexData(isInit, isFresh, 1);
        mPresenter.getMsgNumData();
        mPresenter.getMSData();
        refreshLayout.setEnableLoadmore(true);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);////取消事件总线
        if (countMsTimer != null) {
            countMsTimer.cancel();
        }
        super.onDestroy();
    }

//
//    /**
//     * 下拉隐藏标题
//     *
//     * @param fraction   下拉比列
//     * @param headHeight 总高度
//     */
//    private void pulldownSetTitle(float fraction, float headHeight) {
//        int dxScrollY = CommonUtils.pxToDip(mContext, fraction * headHeight);
//        if (dxScrollY > 5) {//距离大于5dp，实现渐变
//            if (dxScrollY > 20) {//大于20dp,透明度为0
//                dxScrollY = 20;
//            }
//            float alp = (20 - dxScrollY) / 15f;
//            llSearch.setAlpha(alp);
//            tvMessage.setAlpha(alp);
//            bageNumView.setAlpha(alp);
//        } else {//透明度为1
//            llSearch.setAlpha(1);
//            tvMessage.setAlpha(1);
//            bageNumView.setAlpha(1);
//        }
//    }

    @Override
    public void onNetFailure(String url, String msg) {
        if (!TextUtils.equals(HttpRequest.HOME_PAGE_INDEX, url)) {
            super.onNetFailure(url, msg);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getMsgNumData();//回到界面自动刷新数据
    }

    @Override
    public String getPageName() {
        return getString(R.string.str_pagename_home);
    }


    @Override
    @OnClick({R.id.tv_search, R.id.tv_message, R.id.iv_topview})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search://跳转到商品搜索页
                JumpUtil.GotoActivity(this, SearchGoodsActivity.class);
                break;
            case R.id.tv_message://跳转到我的消息
                startMyMsgActivity();
                break;

            case R.id.iv_topview://回到顶部
                recyclerView.smoothScrollToPosition(0);
                ivTopview.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 跳转到我的消息里
     */
    private void startMyMsgActivity() {
        if (AppContext.isLogin) {//登录后查看消息列表
            JumpUtil.GotoActivity(this, MyMessageActivity.class);
        } else {//跳转到我的登录
            JumpUtil.GotoActivity(this, LoginActivity.class);
        }
    }

    @Override
    public void onFinishReFreshView() {
        refreshLayout.finishRefreshing();
    }

    @Override
    public void onFinishLoadMoreView() {
        refreshLayout.finishLoadmore();
        if (floor >= floorcount) {
            refreshLayout.setEnableLoadmore(false);
        } else {
            refreshLayout.setEnableLoadmore(true);
        }
    }


    /**
     * 滑动速度控制
     */

    public class CustomLLLayoutManager extends LinearLayoutManager {
        private double speedRatio;

        public CustomLLLayoutManager(Context context) {
            super(context);
        }

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
            int a = super.scrollHorizontallyBy((int) (speedRatio * dx), recycler, state);//屏蔽之后无滑动效果，证明滑动的效果就是由这个函数实现
            if (a == (int) (speedRatio * dx)) {
                return dx;
            }
            return a;
        }

        public void setSpeedRatio(double speedRatio) {
            this.speedRatio = speedRatio;
        }
    }


    /**
     * 滚动高度计算
     *
     * @return
     */
    private void getScollYDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();//当前显示显示第一个View的位置

        // 更新回到顶部按钮显示
        updateTopViewData(position);
        //计算滚动高度
//        View firstVisiableChildView = layoutManager.findViewByPosition(position);
//        int itemHeight = firstVisiableChildView.getHeight();
//        return (position) * itemHeight - firstVisiableChildView.getTop();
    }

    /**
     * 更新回到顶部按钮显示
     *
     * @param position 第几页显示
     */
    private void updateTopViewData(int position) {
        synchronized (ivTopview) {//回到顶部按钮设置
            if (position > 3) {//滚动到第三页(商品分类)显示TopView
                if (ivTopview.getVisibility() == View.GONE)
                    BillHelper.setBottomViewVisible(ivTopview, View.VISIBLE, null);

            } else {//小于第三个隐藏
                if (ivTopview.getVisibility() == View.VISIBLE)
                    BillHelper.setBottomViewVisible(ivTopview, View.GONE, null);
            }
        }
    }

//    /**
//     * 滚动标题设置
//     *
//     * @param scollY
//     */
//    public void onScrollUpdate(int scollY) {
//        int scollHeight = (int) (scollY / scale + 0.5f);//转化为dp
//
//        int alpha = (int) ((scollHeight - 20) * 1.9);
//        if (alpha > 255)//最大透明度设置
//            alpha = 255;
//        if (alpha < 0)//透明度最小值判定和设置
//            alpha = 0;
//        if (alpha > 110) {//搜索框透明度色设置
//            llSearch.getBackground().setAlpha(alpha);
//        } else {//最小透明度为100；
//            llSearch.getBackground().setAlpha(110);
//        }
//        if (alpha > 127) {//显示消息颜色：灰色
//            tvMessage.setSelected(true);
//        } else {//白色
//            tvMessage.setSelected(false);
//        }
//        if (alpha > 240) {//标题分割线透明度设置
//            llTitleDeline.setBackgroundColor(Color.argb(alpha, 238, 238, 238));
//        } else {
//            llTitleDeline.setBackgroundColor(Color.argb(alpha, 255, 255, 255));
//        }
//        //标题背景透明度设置
//        llTitleBar.getBackground().setAlpha(alpha);
//    }

    /**
     * 更新分类推荐点击事件
     */
    @Override
    public void updateTypeView(final List<NavigationHomeBean> navigation) {
        homeNavigationAdapter.updateData(navigation);//设置推荐分类按钮

        if (!CommonUtils.isEmptyList(navigation) && !TextUtils.isEmpty(navigation.get(0).getBackground())) {
            navigationBgTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                    viewHeadHolder.nsgvNavigation.setBackground(new BitmapDrawable(mContext.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable drawable) {
                    viewHeadHolder.nsgvNavigation.setBackground(ContextCompat.getDrawable(mContext, R.color.white));
                }

                @Override
                public void onPrepareLoad(Drawable drawable) {
                    viewHeadHolder.nsgvNavigation.setBackground(ContextCompat.getDrawable(mContext, R.color.white));
                }
            };
            Picasso.with(mContext).load(navigation.get(0).getBackground()).into(navigationBgTarget);
        } else {
            viewHeadHolder.nsgvNavigation.setBackground(ContextCompat.getDrawable(mContext, R.color.white));
        }
    }

    /**
     * 更新token失效问题
     *
     * @param params
     */
    @Subscriber(tag = EventBusConstants.EVENTT_UPDATE_TOKAN_RESET, mode = ThreadMode.MAIN)
    public void updateTokenFaild(Object params) {
        updateMsgNumView(0);
    }

    /**
     * 刷新消息条数
     *
     * @param params
     */
    @Subscriber(tag = EventBusConstants.EVENTT_UPDATE_MESSAGE_NUM, mode = ThreadMode.MAIN)
    public void updateMsgUpdate(Object params) {
        mPresenter.getMsgNumData();
    }

    /**
     * 点击进入秒杀列表，刷新秒杀数据
     *
     * @param params
     */
    @Subscriber(tag = EventBusConstants.EVENTT_UPDATE_MS_DATA, mode = ThreadMode.MAIN)
    public void getMSData(Object params) {
        mPresenter.getMSData();
    }

    /**
     * 更新秒杀数据
     *
     * @param msProductBean
     */
    @Override
    public void updateMSView(final Response<List<MsProductBean>> msProductBean) {
        if (msProductBean == null) {//秒杀对象为空
            viewHeadHolder.llMs.setVisibility(View.GONE);
        } else {
            if ((ConstantsUtil.RESPONSE_SUCCEED == msProductBean.getStatus())) {//秒杀成功返回数据，并显示秒杀模块
                this.mMsProductBean = msProductBean;
                viewHeadHolder.llMs.setVisibility(View.VISIBLE);
                MsProductBean dataModel = msProductBean.getData().get(msProductBean.getBuynum() - 1);
                String activityName[] = dataModel.getActivityName().split(",");
                homeMsAdapter.updateData(dataModel.getProductList(), msProductBean.getBuynum());
                // 设置水平拉升
                IOverScrollDecor decor = OverScrollDecoratorHelper.setUpOverScroll(viewHeadHolder.rlList, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
                decor.setOverScrollStateListener(new IOverScrollStateListener() {//拉升监听
                    @Override
                    public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
                        if (newState == 2) {//判断是否向左拉升
                            isLelfDrag = true;
                        }
                        switch (newState) {//
                            case STATE_IDLE://拉升结束
                                if (oldState == STATE_BOUNCE_BACK && isLelfDrag) {//向左拉升，且拉升结束
//                            //跳转到秒杀
                                    isLelfDrag = false;
                                    CommonUtils.startMsHtmlList(mContext, msProductBean.getBuynum(), null);

                                }
                                break;
                        }
                    }
                });
                viewHeadHolder.llMs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转到秒杀
                        CommonUtils.startMsHtmlList(mContext, msProductBean.getBuynum(), null);
                    }
                });

                if (countMsTimer != null) {//取消之前的秒杀对象
                    countMsTimer.cancel();
                    countMsTimer = null;
                }
                viewHeadHolder.tvActivityName.setText(activityName[1].split(":")[0] + "点场");
                countMsTimer = new CountTimerHelper(msProductBean.getDiffSeconds() * 1000, 1000) {//新建新的秒杀对象
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if (mMsProductBean.getDiffSeconds() * 1000 == millisInFuture) {
                            String[] formatTime = CommonUtils.formatTimeToSfm(millisUntilFinished);
                            viewHeadHolder.tvActivityTimeofhour.setText(formatTime[0] + "");
                            viewHeadHolder.tvActivityTimeofmunite.setText(formatTime[1] + "");
                            viewHeadHolder.tvActivityTimeofsecend.setText(formatTime[2] + "");
                        }
                    }

                    @Override
                    public void onFinish() {//完成秒杀后重新刷新数据
                        mPresenter.getMSData();
                    }
                }.start();
            } else {//秒杀返回数据失败，并
                viewHeadHolder.llMs.setVisibility(View.GONE);
            }
        }

    }


    /**
     * 更新数据
     *
     * @param homePageBean 首页返回结果对象
     */
    @Override
    public void upHomeView(final boolean isInit, final boolean isRefresh, int floor, final HomePageBean homePageBean) {
        this.floor = homePageBean.getFloor();
        this.floorcount = homePageBean.getFloorcount();
        if (floor == 1) {
            if (!CommonUtils.isEmptyList(homePageBean.getBannerhome())) {//设置幻灯片
                if (homePageBean.getBannerhome().size() == 1) {//1张不显示Indicator和不滚动
                    viewHeadHolder.sibBanner.setAutoScrollEnable(false);//不滚动
                    viewHeadHolder.sibBanner.setIndicatorShow(false);
                } else {//显示Indicator和循环滚动
                    viewHeadHolder.sibBanner.setAutoScrollEnable(true);
                    viewHeadHolder.sibBanner.setIndicatorShow(true);
                }
                viewHeadHolder.sibBanner.setSource(homePageBean.getBannerhome()).startScroll();//设置数据开始滚动
                viewHeadHolder.sibBanner.setOnItemClickL(new BaseBanner.OnItemClickL() {
                    @Override
                    public void onItemClick(int position) {//点击
                        String url = homePageBean.getBannerhome().get(position).getUrl();
                        if (!TextUtils.isEmpty(url) && url.startsWith("http")) {//空判断和http链接地址判断
                            CommonUtils.startWebViewActivity(getContext(), url, true, false);
                            TDUtils.onEvent(mContext, "100003", "banner主图", TDUtils.getInstance().putParams("活动名称", homePageBean.getBannerhome().get(position).getName()).putParams("活动", url).buildParams());
                        }
                    }
                });
            }

            if (!CommonUtils.isEmptyList(homePageBean.getBannerhome2())) {//设置幻灯片活动
                if (homePageBean.getBannerhome2().size() == 1) {//1张不显示Indicator和不滚动
                    viewHeadHolder.sibActivityBanner.setAutoScrollEnable(false);//不滚动
                    viewHeadHolder.sibActivityBanner.setIndicatorShow(false);
                } else {//显示Indicator和循环滚动
                    viewHeadHolder.sibActivityBanner.setAutoScrollEnable(true);
                    viewHeadHolder.sibActivityBanner.setIndicatorShow(true);
                }
                viewHeadHolder.sibActivityBanner.setSource(homePageBean.getBannerhome2()).startScroll();//设置数据开始滚动
                viewHeadHolder.sibActivityBanner.setOnItemClickL(new BaseBanner.OnItemClickL() {
                    @Override
                    public void onItemClick(int position) {//点击
                        String url = homePageBean.getBannerhome2().get(position).getUrl();
                        if (!TextUtils.isEmpty(url) && url.startsWith("http")) {//空判断和http链接地址判断
                            CommonUtils.startWebViewActivity(getContext(), url, true, false);//跳网页
                        }
                    }
                });

                viewHeadHolder.sibActivityBanner.setVisibility(View.VISIBLE);
            } else {
                viewHeadHolder.sibActivityBanner.setVisibility(View.GONE);
            }

            updateNoticeView(homePageBean.getNews());//更新快报
            updateTypeView(homePageBean.getNavigation());//更新Type图标
        }
        adapter.upateData(this.floor == 1, homePageBean.getProductfloor());//更新分类
    }

    /**
     * 更新消息条数
     *
     * @param data
     */
    @Override
    public void updateMsgNumView(Integer data) {
        if (data > 0) {//消息条数大于1
            bageNumView.setVisibility(View.VISIBLE);
            bageNumView.setBadgeCount(data);
        } else {
            bageNumView.setBadgeCount(0);
            bageNumView.setVisibility(View.INVISIBLE);
        }
        if (data > 10) {
            bageNumView.setBadgeMargin(0, 0, 5, 7);
        } else {
            bageNumView.setBadgeMargin(0, 0, 5, 15);
        }
    }

    /**
     * 修改通知
     *
     * @param list 通知列表
     */
    @Override
    public void updateNoticeView(final List<QualityNewsBean> list) {
        if (CommonUtils.isEmptyList(list)) {//没有数据不显示快报
            viewHeadHolder.llNotice.setVisibility(View.GONE);
        } else {//显示快报
            viewHeadHolder.llNotice.setVisibility(View.VISIBLE);
            ArrayList<String> noticeList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {//快报UI赋值
                noticeList.add(list.get(i).getTitle());
            }
            viewHeadHolder.mvNotice.startWithList(noticeList);
//            if (noticeList.size() == 1) {//1张不滚动
//                viewHeadHolder.mvNotice.stopFlipping();
//            }
            viewHeadHolder.mvNotice.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
                @Override
                public void onItemClick(int position, TextView textView) {////点击快报跳转
                    String url = list.get(viewHeadHolder.mvNotice.getDisplayedChild()).getUrl();
                    if (!TextUtils.isEmpty(url) && url.startsWith("http")) {//空判断和http链接地址判断
                        TDUtils.onEvent(mContext, "100005", "公告消息");
                        CommonUtils.startWebViewActivity(mContext, url, false, false);
                    }
                }
            });
        }
    }

    /**
     * 首页Item Head
     */
    class ViewHeadHolder {
        @InjectView(R.id.sib_banner)
        BannerVew sibBanner;//首页banner
        @InjectView(R.id.nsgv_navigation)
        NoScrollGridView nsgvNavigation;
        @InjectView(R.id.sib_activity_banner)
        BannerVew sibActivityBanner;//活动banner
        @InjectView(R.id.mv_notice)
        MarqueeView mvNotice;//快报View
        @InjectView(R.id.ll_notice)
        LinearLayout llNotice;//快报Item
        @InjectView(R.id.tv_activity_name)
        TextView tvActivityName;//秒杀Item
        @InjectView(R.id.tv_activity_timeofhour)
        TextView tvActivityTimeofhour;//秒杀h
        @InjectView(R.id.tv_activity_timeofmunite)
        TextView tvActivityTimeofmunite;//秒杀min
        @InjectView(R.id.tv_activity_timeofsecend)
        TextView tvActivityTimeofsecend;//秒杀sec
        @InjectView(R.id.rl_list)
        BetterRecyclerView rlList;//秒杀List
        @InjectView(R.id.ll_ms)
        ForegroundLinearLayout llMs;////秒杀场次

        ViewHeadHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}