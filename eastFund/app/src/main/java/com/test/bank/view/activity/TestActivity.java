package com.test.bank.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.adapter.TestAdapter;
import com.test.bank.base.BaseActivity;
import com.test.bank.base.BaseBean;
import com.test.bank.base.BaseFragment;
import com.test.bank.bean.TestQABean;
import com.test.bank.bean.TestResultBean;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.utils.DensityUtil;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.SpaceItemDecoration;
import com.test.bank.utils.StatusBarUtil;
import com.test.bank.utils.ToastUtils;
import com.test.bank.view.fragment.TestResultFragment;
import com.youth.banner.transformer.ZoomOutSlideTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class TestActivity extends BaseActivity {

    @BindView(R.id.rl_testActivity_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_testActivity_reset)
    TextView tvReset;
    @BindView(R.id.ll_testActivity_qa)
    LinearLayout llQA;
    @BindView(R.id.recyclerView_testActivity)
    RecyclerView recyclerView;

    @BindView(R.id.rl_testActivity_testResult)
    RelativeLayout rlResult;
    @BindView(R.id.viewPager_testActivity)
    ViewPager viewPager;
    @BindView(R.id.ll_testActivity_indicator)
    LinearLayout llIndicatorContainer;

    List<TestQABean> qaList;
    TestAdapter adapter;
    TestAdapter.OnSelectedOptionListener onSelectedOptionListener;


    List<BaseFragment> fragmentList;
    TestResultPagerAdapter pagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void init() {
        StatusBarUtil.setColor(this, Color.WHITE);
        StatusBarUtil.setStatusBarTextColorStyle(this, true);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) rlTitle.getLayoutParams();
        layoutParams.topMargin += StatusBarUtil.getStatusBarHeight(this);
        rlTitle.setLayoutParams(layoutParams);

        requestTestResult();
    }

    //请求测一测结果
    private void requestTestResult() {
        postRequest(new OnResponseListener<List<TestResultBean>>() {
            @Override
            public Observable<BaseBean<List<TestResultBean>>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.putLast("token", SPUtil.getInstance().getToken());
                return NetService.getNetService().getTestResult(paramMap);
            }

            @Override
            public void onResponse(BaseBean<List<TestResultBean>> result) {
                if (result.isSuccess() && !result.getData().isEmpty()) {     //填充测试结果
                    inflatePagerData(result.getData());
                } else {    //未测试过，开始测试
                    initRecyclerViewData();
                }
            }

            @Override
            public void onError(String errorMsg) {
            }
        });
    }

    private void inflatePagerData(List<TestResultBean> resultBeanList) {
        tvReset.setVisibility(View.VISIBLE);
        llQA.setVisibility(View.GONE);
        rlResult.setVisibility(View.VISIBLE);
        if (fragmentList == null || pagerAdapter == null) {
            initViewPagerData();
        }
        fragmentList.clear();
        llIndicatorContainer.removeAllViews();
        for (int i = 0; i < resultBeanList.size(); i++) {
            fragmentList.add(TestResultFragment.newInstance(resultBeanList.get(i)));
            llIndicatorContainer.addView(generateViewPagerIndicator(i == 0));
        }
        pagerAdapter.notifyDataSetChanged();
    }

    private View generateViewPagerIndicator(boolean isFirst) {
        View indicatorView = new View(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.width = DensityUtil.dip2px(8);
        layoutParams.height = DensityUtil.dip2px(8);
        if (!isFirst) {
            layoutParams.setMargins(DensityUtil.dip2px(7), 0, 0, 0);
            indicatorView.setSelected(false);
        } else {
            indicatorView.setSelected(true);
        }
        indicatorView.setLayoutParams(layoutParams);
        indicatorView.setBackgroundResource(R.drawable.selector_circle_ffffff_80a7ef);
        return indicatorView;
    }

    private void initViewPagerData() {
        tvReset.setVisibility(View.VISIBLE);
        llQA.setVisibility(View.GONE);
        rlResult.setVisibility(View.VISIBLE);

        if (fragmentList == null) {
            fragmentList = new ArrayList<>();
        }

        if (pagerAdapter == null) {
            pagerAdapter = new TestResultPagerAdapter(getSupportFragmentManager());
        }

        viewPager.setPageMargin(15);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < llIndicatorContainer.getChildCount(); i++) {
                    llIndicatorContainer.getChildAt(i).setSelected(i == position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private String answers = "";

    private void submitAnswer() {
        postRequest(new OnResponseListener<List<TestResultBean>>() {
            @Override
            public Observable<BaseBean<List<TestResultBean>>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                if (answers.startsWith(",")) {
                    answers = answers.substring(1, answers.length());
                }
                paramMap.putLast("answers", answers);
                return NetService.getNetService().submitTestAnswer(paramMap);
            }

            @Override
            public void onResponse(BaseBean<List<TestResultBean>> result) {
                if (result.isSuccess() && !result.getData().isEmpty()) {
                    inflatePagerData(result.getData());
                } else {
                    ToastUtils.showShort("数据为空。。。");
                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    private void appendAnswers(int selectedIndex) {
        if (selectedIndex == 1) {
            answers = answers + ",A";
        } else if (selectedIndex == 2) {
            answers += ",B";
        } else if (selectedIndex == 3) {
            answers += ",C";
        } else if (selectedIndex == 4) {
            answers += ",D";
        }
    }


    private void reInitRecyclerView() {
        if (qaList == null || adapter == null) {
            initRecyclerViewData();
        } else {

            rlResult.setVisibility(View.GONE);
            llQA.setVisibility(View.VISIBLE);
            tvReset.setVisibility(View.GONE);

            qaList.clear();
            TestQABean q1 = new TestQABean(1);
            q1.setQuestion("您准备用于基金投资的资金大概有多少：");
            q1.setOptionA("5万元以下");
            q1.setOptionB("5-10万元");
            q1.setOptionC("11-30万元");
            q1.setOptionD("30万元以上");
            q1.setAnswer(-1);
            qaList.add(q1);
            adapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerViewData() {
        rlResult.setVisibility(View.GONE);
        llQA.setVisibility(View.VISIBLE);
        tvReset.setVisibility(View.GONE);

        if (qaList == null) {
            qaList = new ArrayList<>();
        }

        TestQABean q1 = new TestQABean(1);
        q1.setQuestion("您准备用于基金投资的资金大概有多少：");
        q1.setOptionA("5万元以下");
        q1.setOptionB("5-10万元");
        q1.setOptionC("11-30万元");
        q1.setOptionD("30万元以上");
        qaList.add(q1);

        if (adapter == null) {
            adapter = new TestAdapter(this, qaList);
        }
        onSelectedOptionListener = generateOnSelectedOptionListener();
        adapter.setOnSelectedOptionListener(onSelectedOptionListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpaceItemDecoration(30));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mShouldScroll) {
                    mShouldScroll = false;
                    smoothMoveToPosition(mToPosition);
                }
            }
        });
    }


    private int currSize = 1;

    private TestAdapter.OnSelectedOptionListener generateOnSelectedOptionListener() {
        return new TestAdapter.OnSelectedOptionListener() {
            @Override
            public void onSelectedOption(TestQABean data, int selectedOptionIndex) {
                LogUtils.e("onSelectedOption: " + data.toString() + " , " + selectedOptionIndex);
                appendAnswers(selectedOptionIndex);
                if (data.getQaNo() == 5) {
                    submitAnswer();
                    return;
                }
                if (data.getQaNo() == 1) {  //选中第一题，则展示第二题
                    currSize = 2;
                    if (qaList.size() >= 2) {
                        return;
                    }
                    TestQABean q2 = new TestQABean(2);
                    q2.setQuestion("您计划的投资期限是多久：");
                    q2.setOptionA("1年以下 ");
                    q2.setOptionB("1-3年");
                    q2.setOptionC("3-5年 ");
                    q2.setOptionD("5年以上");
                    qaList.add(q2);
                    LogUtils.e("add........" + currSize);
                } else if (data.getQaNo() == 2) {
                    if (qaList.size() >= 3) {
                        return;
                    }
                    TestQABean q3 = new TestQABean(3);
                    q3.setQuestion("你的投资目标是：");
                    q3.setOptionA("资金保值");
                    q3.setOptionB("资金稳健增值 ");
                    q3.setOptionC("资金迅速增长");
                    q3.setOptionD("");
                    qaList.add(q3);
                    currSize = 3;
                    LogUtils.e("add........" + currSize);
                } else if (data.getQaNo() == 3) {
                    if (qaList.size() >= 4) {
                        return;
                    }
                    TestQABean q4 = new TestQABean(4);
                    q4.setQuestion("以下的投资模式，您更倾向于哪一种：");
                    q4.setOptionA("本金不亏损，目标年收益5%");
                    q4.setOptionB("本金最多亏损5%，目标年收益10%");
                    q4.setOptionC("本金最多亏损10%，目标年收益20% ");
                    q4.setOptionD("本金可亏损20%，目标年收益40%");
                    qaList.add(q4);
                    currSize = 4;
                    LogUtils.e("add........" + currSize);
                } else if (data.getQaNo() == 4) {
                    if (qaList.size() >= 5) {
                        return;
                    }
                    if (selectedOptionIndex == 1) {
                        submitAnswer();
                        return;
                    }

                    TestQABean q5 = new TestQABean(5);
                    q5.setOptionA("把基金全部赎回");
                    q5.setOptionB("赎回部分基金，剩下的再观望一下");
                    q5.setOptionC("先不赎回，再观望一下");
                    q5.setOptionD("继续加仓");
                    if (selectedOptionIndex == 2) {
                        q5.setQuestion("当您的本金亏损5%时，您会采取下列哪种做法：");
                    } else if (selectedOptionIndex == 3) {
                        q5.setQuestion("当您的本金亏损10%时，您会采取下列哪种做法：");
                    } else if (selectedOptionIndex == 4) {
                        q5.setQuestion("当您的本金亏损20%时，您会采取下列哪种做法：");
                    }
                    qaList.add(q5);
                    currSize = 5;
                    LogUtils.e("add........" + currSize);
                }
                if (recyclerView != null && recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.isComputingLayout()) {
                    adapter.notifyDataSetChanged();
//                    recyclerView.smoothScrollToPosition(qaList.size());
//                    if (floatTop == -1) {
                    smoothMoveToPosition(qaList.size() - 1);
//                    } else {
//                        recyclerView.scrollTo(0, floatTop);
//                    }
                }
                printParam();
            }
        };
    }

    private int mToPosition;
    private boolean mShouldScroll = false;

    private void smoothMoveToPosition(final int position) {
        // 第一个可见位置
        int firstItem = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));

        if (position < firstItem) {
            // 如果跳转位置在第一个可见位置之前，就smoothScrollToPosition可以直接跳转
            recyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 跳转位置在第一个可见项之后，最后一个可见项之前
            // smoothScrollToPosition根本不会动，此时调用smoothScrollBy来滑动到指定位置
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < recyclerView.getChildCount()) {
                int top = recyclerView.getChildAt(movePosition).getTop();
                recyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 如果要跳转的位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            recyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }


    private int floatTop = -1;

    private void printParam() {
        if (floatTop == -1) {
            floatTop = (int) recyclerView.getChildAt(0).getY();
            LogUtils.e("floatTop: " + floatTop);
        }
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            LogUtils.e("[" + i + "]y: " + recyclerView.getChildAt(i).getY() + " , scrollY: " + recyclerView.getChildAt(i).getScaleY() + " , top: " + recyclerView.getChildAt(i).getTop());
        }
    }

    class TestResultPagerAdapter extends FragmentPagerAdapter {

        public TestResultPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    @OnClick({R.id.iv_testActivity_back, R.id.tv_testActivity_reset})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_testActivity_back:
                finish();
                break;
            case R.id.tv_testActivity_reset:
                reInitRecyclerView();
                break;
        }
    }

    @Override
    protected void doBusiness() {

    }

    @Override
    protected boolean isCountPage() {
        return true;
    }

    public static void open(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }
}
