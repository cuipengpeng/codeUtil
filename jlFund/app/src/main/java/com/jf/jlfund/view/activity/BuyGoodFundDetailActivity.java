package com.jf.jlfund.view.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.bean.BuyGoodFundBean;
import com.jf.jlfund.bean.BuyGoodFundDetailBean;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.utils.ImageUtils;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.StatusBarUtil;
import com.jf.jlfund.utils.ToastUtils;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.weight.CompatScrollView;
import com.jf.jlfund.weight.errorview.DefaultErrorPageBean;
import com.jf.jlfund.weight.errorview.ErrorBean;
import com.jf.jlfund.weight.holder.BuyGoodFundDetailHolder;
import com.jf.jlfund.weight.holder.RecommandReasonItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class BuyGoodFundDetailActivity extends BaseActivity {
    private static final String TAG = "BuyGoodFundDetailActivi";

    @BindView(R.id.tv_float_title)
    TextView tvFloatTitle;
    @BindView(R.id.tv_prime_title)
    TextView tvPrimeTitle;
    @BindView(R.id.tv_sub_title)
    TextView tvSubTitle;
    @BindView(R.id.iv_back_bgfd)
    ImageView ivBack;

    @BindView(R.id.rl_header_bgfd)
    RelativeLayout rlHeader;
    @BindView(R.id.scrollView_bgfd)
    CompatScrollView scrollView;
    @BindView(R.id.ll_content_bgfd)
    LinearLayout llRoot;
    @BindView(R.id.iv_bg_bgfd)
    ImageView ivBg;
    @BindView(R.id.ll_recommand_reason)
    LinearLayout llRecommandReason;
    @BindView(R.id.ll_bug_good_fund_detail_content)
    LinearLayout llContent;
    @BindView(R.id.tv_recommand_reason)
    TextView tvReasonSlogn;
    @BindView(R.id.tv_show_more_bgfd)
    TextView tvShowMore;

    List<BuyGoodFundBean> buyGoodFundList;  //买好基列表

    private List<String> reasonList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_buy_good_fund_detail;
    }

    String floatTitle;
    Integer id = -1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void init() {
        if (getIntent() != null) {
            floatTitle = getIntent().getStringExtra(FLOAT_TITLE);
            id = getIntent().getIntExtra(ID, 1);
        }
//        UIUtils.setQuestionText(tvFloatTitle, floatTitle);

        initView();
        initListener();
        initData();
        requestData();
    }

    private void requestData() {
        postRequest(new OnResponseListener<BuyGoodFundDetailBean>() {
            @Override
            public Observable<BaseBean<BuyGoodFundDetailBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.putLast("id", id + "");
                return NetService.getNetService().getBuyGoodFundDetail(paramMap);
            }

            @Override
            public void onResponse(BaseBean<BuyGoodFundDetailBean> result) {
                inflateData(result.getData());
            }

            @Override
            public void onError(String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }


    @Override
    protected void doBusiness() {

    }

    private void inflateData(BuyGoodFundDetailBean result) {
        ImageUtils.displayImage(this, result.getImgUrl(), ivBg);
        if (TextUtils.isEmpty(result.getTitle())) {
            UIUtils.setText(tvFloatTitle, floatTitle);
        } else {
            UIUtils.setText(tvFloatTitle, result.getTitle());
        }
        UIUtils.setText(tvPrimeTitle, result.getTitle(), "");
        UIUtils.setText(tvSubTitle, result.getSecondTitle(), "");
        UIUtils.setText(tvReasonSlogn, result.getInnerTitle(), "推荐理由");

        inflateReasonData(result.getReason());

        for (int i = 0; i < result.getFundList().size(); i++) {
            llContent.addView(new BuyGoodFundDetailHolder(result.getWealthTypeName()).inflateData(result.getFundList().get(i)));
        }
    }

    /**
     * initView: llHeight: 2820 - 2820
     * initView: scrollView.height:1731-1731-2820  imageView.height: 499-1080
     */
    private void initView() {
        StatusBarUtil.translucentStatusBar(this);
        StatusBarUtil.setStatusBarTextColorStyle(this, true);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageHeight = ivBg.getHeight();
                        floatTitleHeight = rlHeader.getHeight();
                        Log.e(TAG, "initView: llHeight: " + llRoot.getHeight() + " - " + llRoot.getMeasuredHeight() + " rlHeight: " + floatTitleHeight);
                        Log.e(TAG, "initView: scrollView.height:" + scrollView.getMeasuredHeight() + "-" + scrollView.getHeight() + "-" + scrollView.getChildAt(0).getMeasuredHeight() + "  imageView.height: " + ivBg.getHeight() + "-" + ivBg.getWidth());
                    }
                });
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        dynamicSetMarginTop();
    }

    private void dynamicSetMarginTop() {
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rlHeader.getLayoutParams();
        params.topMargin = statusBarHeight;
        rlHeader.setLayoutParams(params);
    }

    private void initData() {
        if (reasonList == null) {
            reasonList = new ArrayList<>();
        }

        if (buyGoodFundList == null) {
            buyGoodFundList = new ArrayList<>();
        }
    }

    private int currentLineCount = 0;

    //填充理由列表
    private void inflateReasonData(String reasons) {
        if (!reasons.contains("&")) {
            return;
        }
        String[] arr = reasons.split("&");
        for (String s : arr) {
            reasonList.add(s);
        }
        final boolean[] isLessThan9Lines = {true};
        for (int i = 0; i < arr.length && currentLineCount <= 9 && isLessThan9Lines[0]; i++) {
            llRecommandReason.addView(new RecommandReasonItem(new RecommandReasonItem.OnGetLineCountListener() {
                @Override
                public int onGetLineCount(int lineCount) {
                    LogUtils.e("onGetLineCount: lineCount: " + lineCount + " currentLineCount: " + currentLineCount);
                    int maxLines = -1;
                    if (currentLineCount + lineCount > 9) {
                        isLessThan9Lines[0] = false;
                        maxLines = (lineCount - (currentLineCount + lineCount - 9));
                    } else {
                        isLessThan9Lines[0] = true;
                    }
                    currentLineCount += lineCount;
                    return maxLines;
                }
            }).inflateData(arr[i]));
        }
        tvShowMore.setVisibility((currentLineCount > 9) ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.tv_show_more_bgfd, R.id.iv_back_bgfd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_bgfd:
                finish();
                break;
            case R.id.tv_show_more_bgfd:
                showOtherReasonItem();
                tvShowMore.setVisibility(View.GONE);
                break;
        }
    }

    private void showOtherReasonItem() {
        llRecommandReason.removeAllViews();
        for (int i = 0; i < reasonList.size(); i++) {
            llRecommandReason.addView(new RecommandReasonItem(null).inflateData(reasonList.get(i)));
        }
    }

    private int imageHeight;
    private int floatTitleHeight;
    private float maxRatio = 1.6f;    //最大缩放比例

    private int sY; //scrollY

    private int oldY;
    private int dyDown;
    private int dyMove;

    private void initListener() {
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewGroup.LayoutParams layoutParams = ivBg.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldY = (int) event.getRawY();
                        dyMove = oldY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dyDown = (int) (event.getRawY() - oldY);
                        Log.d(TAG, "onTouchEvent:  " + event.getRawY() + "-" + oldY + "= " + dyDown + "  sY: " + sY);
                        if (event.getRawY() - dyMove > 0) {   //下滑
                            Log.e(TAG, "onTouch: 下滑： " + event.getRawY() + " - " + dyMove);
                            if (sY == 0) {  //初始状态下滑，拉伸图片
                                if (ivBg.getLayoutParams().height + (event.getRawY() - dyMove) < imageHeight * maxRatio) {
                                    layoutParams.height += Math.sqrt(event.getRawY() - dyMove);
//                                    layoutParams.height += Math.pow((event.getRawY() - dyMove), 2 / 3);
//                                    layoutParams.height += (event.getRawY() - dyMove);
                                    ivBg.setLayoutParams(layoutParams);
//                                    ivBg.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).start();
                                }
                            } else {
                                Log.e(TAG, "onTouch: 正常向下滑动。。。");
                            }
                        } else {  //上滑
                            Log.e(TAG, "onTouch: 上滑： " + event.getRawY() + " - " + dyMove);
                            if (sY == 0 && layoutParams.height > imageHeight) { //上滑并且图片处于拉伸状态，则减小图片高度并拦截ScrollView上滑处理
                                if (layoutParams.height + (event.getRawY() - dyMove) > imageHeight) {
//                                    layoutParams.height -= Math.sqrt(Math.abs(event.getRawY() - dyMove));
//                                    layoutParams.height += Math.pow((event.getRawY() - dyMove), 2 / 3);
                                    layoutParams.height += (event.getRawY() - dyMove);
                                    ivBg.setLayoutParams(layoutParams);
                                    return true;
                                }
                            }
                        }
                        dyMove = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        Log.e(TAG, "onTouchEvent: 恢复imageview原始高度");
                        resetImageHeight(layoutParams.height);
                        break;
                }
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    handleScrollListener(scrollY);
                }
            });
        } else {
            scrollView.setOnSimpleScrollChangeListener(new CompatScrollView.OnSimpleScrollChangeListener() {
                @Override
                public void onScrollChange(int y) {
                    handleScrollListener(y);
                }
            });
        }
    }

    private void handleScrollListener(int scrollY) {
        Log.e(TAG, "onScrollChange:  scrollY: lowwwwwwwwwwwwww" + scrollY);
        sY = scrollY;
        int alpha = scrollY * 255 / (imageHeight - floatTitleHeight);
        if (alpha > 100) {
            ivBack.setImageResource(R.drawable.icon_back);
        } else {
            ivBack.setImageResource(R.mipmap.left_arrow_white);
        }
        int colorPrime; //3F51B5 63 81 181
        int colorPrimeDark;    //303F9F  48 63 159
        int titleTextColor;
        if (scrollY < (imageHeight - floatTitleHeight)) {
            titleTextColor = Color.argb(alpha, 57, 59, 81); //#393b51
            colorPrime = Color.argb(alpha, 255, 255, 255);
            colorPrimeDark = Color.argb(alpha, 255, 255, 255);
            //                    rlHeader.getBackground().mutate().setAlpha(alpha);
        } else {
            titleTextColor = Color.argb(255, 57, 59, 81);
            colorPrime = Color.argb(255, 255, 255, 255);
            colorPrimeDark = Color.argb(255, 255, 255, 255);
            //                    rlHeader.getBackground().mutate().setAlpha(255);
        }
        tvFloatTitle.setTextColor(titleTextColor);
        rlHeader.setBackgroundColor(colorPrime);
        StatusBarUtil.setColor(BuyGoodFundDetailActivity.this, colorPrimeDark);
        if (sY == 0 && ivBg.getLayoutParams().height > imageHeight) {
            Log.d(TAG, "onScrollChange: >>>>>>>>>>>>>>>>>>>>");
        }
    }

    private void resetImageHeight(int currImageHeight) {
        Log.d(TAG, "resetImageHeight: currImageHeight: " + currImageHeight);
        ValueAnimator animator = ValueAnimator.ofInt(currImageHeight, imageHeight);// 动画更新的监听
//        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                ivBg.getLayoutParams().height = (Integer) arg0.getAnimatedValue();// 获取动画当前变化的值
//                ivBg.setLayoutParams(layoutParams);
                ivBg.requestLayout();
            }
        });
        animator.setDuration(300);// 动画时间
        animator.start();// 开启动画
    }

    @Override
    protected ErrorBean getErrorBean() {
        return new DefaultErrorPageBean(scrollView);
    }

    public static String FLOAT_TITLE = "float_title";
    public static String ID = "id";

    public static void open(Context context, int id, boolean isFromBuyFoodFund) {
        Intent intent = new Intent(context, BuyGoodFundDetailActivity.class);
        intent.putExtra(FLOAT_TITLE, isFromBuyFoodFund ? "买好基" : "追热点");
        intent.putExtra(ID, id);
        context.startActivity(intent);
    }


}
