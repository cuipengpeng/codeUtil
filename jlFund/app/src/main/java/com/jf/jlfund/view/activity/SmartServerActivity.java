package com.jf.jlfund.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.adapter.SmartServerAdapter;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.bean.SmartQABean;
import com.jf.jlfund.bean.SuggestQuestion;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.utils.Aes;
import com.jf.jlfund.utils.DensityUtil;
import com.jf.jlfund.utils.KeyboardUtils;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.StatusBarUtil;
import com.jf.jlfund.utils.ToastUtils;
import com.jf.jlfund.weight.CommonTitleBar;
import com.jf.jlfund.weight.CustomInsetsFrameLayout;
import com.jf.jlfund.weight.holder.SmartQAHolder;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class SmartServerActivity extends BaseActivity {
    @BindView(R.id.rootView_smartServer)
    CustomInsetsFrameLayout flRootView;
    @BindView(R.id.commonTitleBar_smartServer)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.recyclerView_smartServer)
    RecyclerView recyclerView;
    @BindView(R.id.rl_smartServer_bottom)
    RelativeLayout rlBottom;
    @BindView(R.id.et_smartServer)
    EditText etContent;
    @BindView(R.id.ll_cover_smartServer)
    LinearLayout llCover;
    @BindView(R.id.tv_smartServer_send)
    TextView tvSend;

    SmartServerAdapter adapter;
    List<SmartQABean> mQAList;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    @Override
    protected void init() {

        mQAList = new ArrayList<>();
        adapter = new SmartServerAdapter(this, mQAList);
        adapter.setOnClicksuggestQuestionListener(new SmartQAHolder.OnClickSuggestQuestionListener() {
            @Override
            public void onClickSuggestQuestion(SuggestQuestion suggestQuestion) {
                askQuestion(suggestQuestion.getQuestion_str());
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        KeyboardUtils.setListener(this, new KeyboardUtils.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                LogUtils.e("keyBoardShow: " + height);
                keyboardHeight = height;
                if (!isSetStackFromEnd) {
                    int totalHeight = (commonTitleBarHeight - statusBarHeight) + recyclerViewRange + height + rlBottomHeight;
                    LogUtils.e("totalHeight: " + totalHeight + " , displayHeightOnKeyboradHide: " + displayHeightOnKeyboradHide);
                    if (totalHeight > displayHeightOnKeyboradHide) {
//                        setRecyclerViewPadingBottom(totalHeight - displayHeightOnKeyboradHide);
//                        adapter.setPaddingBottomOnLastItem((totalHeight - displayHeightOnKeyboradHide) / 2);
                        adapter.setPaddingBottomOnLastItem(DensityUtil.dip2px(10));
                        recyclerView.smoothScrollToPosition(mQAList.size() - 1);
                    }
                }
            }

            @Override
            public void keyBoardHide(int height) {
                LogUtils.e("keyBoardHide: " + height);
                keyboardHeight = height;
                if (!isSetStackFromEnd) {       //如果没有设置则动态添加padding
                    adapter.setPaddingBottomOnLastItem(0);
                    recyclerView.smoothScrollToPosition(mQAList.size() - 1);
                }
            }
        });
    }

    private int keyboardHeight;     //键盘高度
    private int devicesHeight;      //设备高度 = isplayHeight + statusBarHeight + naviHeight
    private int displayHeight;      //内容展示高度：不包含顶部状态栏与底部虚拟键盘导航栏的内容展示高度
    private int displayHeightOnKeyboradHide = -1;   //键盘隐藏时的展示内容展示高度【键盘弹起时=displayHeight+keyboardHeight】
    private int recyclerViewHeightOnKeyboardHide = -1;      //键盘隐藏时的recyclerView高度
    private int statusBarHeight;        //状态栏高度
    private int commonTitleBarHeight;   //CommonTitleBar高度【包含状态栏】
    private int rlBottomHeight;     //底部输入框的高度
    private int naviHeight;     //底部虚拟导航栏的高度

    private int recyclerViewHeight;     //recyclerView控件的高度
    private int recyclerViewOffset;     //滑过去的内容的高度
    private int recyclerViewExtent;     //当前显示的列表内容的高度
    private int recyclerViewRange;      //列表内容的高度 = recyclerViewOffset + recyclerViewExtent

    /**
     * 计算recyclerView相关的高度【内容改变后高度也会改变】
     */
    private void calculateRecyclerViewHeight() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerViewHeight = recyclerView.getHeight();
                if (recyclerViewHeightOnKeyboardHide == -1) {
                    recyclerViewHeightOnKeyboardHide = recyclerView.getHeight();
                }
                LogUtils.e("recyclerViewHeight: " + recyclerViewHeight + " , recyclerViewHeightOnKeyboardHide: " + recyclerViewHeightOnKeyboardHide);


                recyclerViewOffset = recyclerView.computeVerticalScrollOffset();
                recyclerViewExtent = recyclerView.computeVerticalScrollExtent();
                recyclerViewRange = recyclerView.computeVerticalScrollRange();

                LogUtils.e("recyclerViewOffset: " + recyclerViewOffset);
                LogUtils.e("recyclerViewExtent: " + recyclerViewExtent);
                LogUtils.e("recyclerViewRange: " + recyclerViewRange);
                //如果内容高度大于recyclerView高度并且没有设置过stackFromEnd
                if (!isSetStackFromEnd && recyclerViewHeightOnKeyboardHide > 0) {
                    if (recyclerViewRange >= recyclerViewHeightOnKeyboardHide) {
                        linearLayoutManager.setStackFromEnd(true);
                        isSetStackFromEnd = true;
                        adapter.setPaddingBottomOnLastItem(-1);
                    } else {
                        int totalHeight = (commonTitleBarHeight - statusBarHeight) + recyclerViewRange + keyboardHeight + rlBottomHeight;
                        if (totalHeight >= displayHeightOnKeyboradHide) {
                            adapter.setPaddingBottomOnLastItem(DensityUtil.dip2px(10));
                            recyclerView.smoothScrollToPosition(mQAList.size() - 1);
                        }
                    }
                }
            }
        });
    }

    /**
     * 计算一些不变的高度
     */
    private void calculateViewsHeight() {
        if (isSetStackFromEnd) {
            return;
        }
        devicesHeight = DensityUtil.getScreenHeight();
        statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        naviHeight = StatusBarUtil.getNavigationBarHeight(this);


        LogUtils.e("devicesHeight: " + devicesHeight);
        LogUtils.e("statusBarHeight: " + statusBarHeight);
        LogUtils.e("naviHeight: " + naviHeight);

        commonTitleBar.post(new Runnable() {
            @Override
            public void run() {
                commonTitleBarHeight = commonTitleBar.getHeight();
                LogUtils.e("commonTitleBarHeight: " + commonTitleBarHeight);
            }
        });

        rlBottom.post(new Runnable() {
            @Override
            public void run() {
                rlBottomHeight = rlBottom.getHeight();
                LogUtils.e("rlBottomHeight: " + rlBottomHeight);
            }
        });

        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            displayHeight = rect.bottom - rect.top;
            if (displayHeightOnKeyboradHide == -1) {
                displayHeightOnKeyboradHide = displayHeight;
            }
            LogUtils.e("displayHeight: " + displayHeight + " , displayHeightOnKeyboradHide: " + displayHeightOnKeyboradHide);
            if (isSetStackFromEnd) {
                LogUtils.e("removeOnGlobalLayoutListener......");
                getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_smart_server;
    }

    @OnClick({R.id.tv_smartServer_send})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_smartServer_send:
                askQuestion(etContent.getText().toString().trim());
                break;
        }
    }

    String userMobile = Aes.decryptAES(SPUtil.getInstance().getMobile());

    private void askQuestion(final String question) {
        if (TextUtils.isEmpty(question)) {
            ToastUtils.showShort("内容不能为空");
            return;
        }

        //添加问题
        SmartQABean qBean = new SmartQABean();
        qBean.setQuestion(question);
        qBean.setTime(System.currentTimeMillis());
        setViewTypeAndShowDate(qBean, true);
        adapter.addQuestion(qBean);
        if (mQAList.size() >= 4) {
            linearLayoutManager.setStackFromEnd(true);
            adapter.setPaddingBottomOnLastItem(-1);
        }
        calculateRecyclerViewHeight();
        qBean.save();       //保存到数据库
        postRequest(new OnResponseListener<SmartQABean>() {
            @Override
            public Observable<BaseBean<SmartQABean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.putLast("question", question);
                return NetService.getNetService().getSmartAnswer(paramMap);
            }

            @Override
            public void onResponse(BaseBean<SmartQABean> result) {
                if (result != null && result.getData() != null) {
                    SmartQABean answerBean = result.getData();
                    setViewTypeAndShowDate(answerBean, false);
                    adapter.addAnswer(answerBean);
                    calculateRecyclerViewHeight();
                    answerBean.save();
                }
                etContent.setText("");
                recyclerView.scrollToPosition(mQAList.size() - 1);
            }

            @Override
            public void onError(String errorMsg) {

            }
        }, false);
    }

    private static final long DOUBLE_INPUT_INTERVAL = 5 * 60 * 1000;    //两次输入超过5分钟则显示上次输入时间

    private void setViewTypeAndShowDate(SmartQABean smartQABean, boolean isAddQuestion) {
        if (isAddQuestion) { //只在添加问题的时候判断是否显示时间，否则在判断答案的时候会进行覆盖
            if (mQAList.size() == 0) {
                smartQABean.isShowDate = true;
            } else {
                long currTime = System.currentTimeMillis();
                long lastInputTime = mQAList.get(mQAList.size() - 1).getTime();
                if (currTime - lastInputTime >= DOUBLE_INPUT_INTERVAL) {        //间隔大于5分钟或者初次使用
                    smartQABean.isShowDate = true;
                } else {      //间隔小于5分钟
                    smartQABean.isShowDate = false;
                }
            }
            smartQABean.viewType = SmartServerAdapter.TYPE_SHOW_QUESTION_ONLY;
        } else {
            smartQABean.viewType = SmartServerAdapter.TYPE_SHOW_ALL;
        }
    }

    private boolean isSetStackFromEnd = false;

    @Override
    protected void doBusiness() {
        LogUtils.e("doBusiness...");
        mQAList.clear();
        mQAList.addAll(DataSupport.where("mobile=?", userMobile).find(SmartQABean.class));       //查询当前用户的问答记录
        if (mQAList.size() >= 4) {
            isSetStackFromEnd = true;
            linearLayoutManager.setStackFromEnd(true);
            adapter.setPaddingBottomOnLastItem(-1);
        } else {
            calculateViewsHeight();
            calculateRecyclerViewHeight();
        }
        adapter.refreshQAList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public static void open(Context context) {
        context.startActivity(new Intent(context, SmartServerActivity.class));
    }
}
