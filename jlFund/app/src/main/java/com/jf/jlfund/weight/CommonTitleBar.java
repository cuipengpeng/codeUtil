package com.jf.jlfund.weight;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.utils.ActivityManager;
import com.jf.jlfund.utils.StatusBarUtil;


/**
 * Created by 55 on 2017/10/16.
 */

public class CommonTitleBar extends RelativeLayout {

    private static final String TAG = "CommonTitleBar";

    public static final int PATTERN_LEFT_ONLY = 100;
    public static final int PATTERN_TITLE_WITH_LEFT_IMG = 110;
    public static final int PATTERN_TITLE_WITH_LEFT_TXT = 111;
    public static final int PATTERN_TITLE_WITH_RIGHT_IMG = 112;
    public static final int PATTERN_TITLE_WITH_RIGHT_TXT = 113;


    public static final int PATTERN_TITLE_WITH_TWO_IMG = 120;
    public static final int PATTERN_TITLE_WITH_TWO_TXT = 121;

    public static final int PATTERN_TITLE_WITH_LEFT_IMG_AND_RIGHT_TXT = 130;
    public static final int PATTERN_TITLE_WITH_LEFT_TXT_AND_RIGHT_IMG = 131;

    public static final int PATTERN_DOUBLE_TITLE_WITH_LEFT_IMG_RIGHT_TXT = 140;    //上下标题、左图标、右文字

    public static final int ACTION_LEFT_BACK = 0;  //点击返回上一页

    public static final int ACTION_RIGHT_NONE = -1; //点击进入基金详情
    public static final int ACTION_RIGHT_ENTER_SEARCH = 0; //点击进入搜索页面
    public static final int ACTION_RIGHT_ENTER_FUND_DETAIL = 1; //点击进入基金详情

    public static final int STATUSBAR_STYLE_LIGHT = 0; //状态栏字体颜色【默认亮色】
    public static final int STATUSBAR_STYLE_DARK = 1;  //状态栏字体颜色【暗色】

    private Context mContext;

    private View rootView;
    private RelativeLayout rlRootView;  //根布局
    private RelativeLayout rlContent;//内容根布局

    private TextView tvPrimaryTitle;   //主标题
    private TextView tvSubTitle;  //下边的小标题

    private TextView tvLeft;    //左文字
    private TextView tvRight;   //右文字

    private String primaryTitle;   //标题
    private String subTitle;    //副标题
    private String leftText;
    private String rightText;   //右标题

    private int leftImgSrc; //左图标背景
    private int rightImgSrc;   //右1背景
    private int backgroundResource; //背景资源

    private int statusbarBGColor = Color.WHITE; //状态栏颜色

    private int primaryTitleColor = Color.parseColor("#393b51");    //主标题默认字体颜色

    private int pattern = PATTERN_TITLE_WITH_LEFT_IMG;    //默认显示标题与左返回按钮模式

    private int leftAction = ACTION_LEFT_BACK;    //左点击事件【图标或文字】,默认返回上一页
    private int rightAction = ACTION_RIGHT_NONE;   //右点击事件【图标或文字】,默认无。

    private int statusBarTextColorStyle;    //默认状态栏字体颜色为暗色

    private boolean isTranslucentStatusBar = true;

    public CommonTitleBar(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CommonTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CommonTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_common_titlebar, this, true);
        //初始化控件
        rlRootView = rootView.findViewById(R.id.rl_ctb_root);
        rlContent = rootView.findViewById(R.id.rl_ctb_content);
        tvPrimaryTitle = rootView.findViewById(R.id.tv_ctb_primarytitle);
        tvSubTitle = rootView.findViewById(R.id.tv_ctb_subtitle);
        tvLeft = rootView.findViewById(R.id.tv_ctb_left);
        tvRight = rootView.findViewById(R.id.tv_ctb_right);
        //初始化属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar);
        primaryTitle = typedArray.getString(R.styleable.CommonTitleBar_title);
        subTitle = typedArray.getString(R.styleable.CommonTitleBar_subtitle);
        leftImgSrc = typedArray.getResourceId(R.styleable.CommonTitleBar_leftBg, R.drawable.icon_back);
        rightImgSrc = typedArray.getResourceId(R.styleable.CommonTitleBar_rightBg, R.drawable.ic_search);
        leftText = typedArray.getString(R.styleable.CommonTitleBar_leftTxt);
        rightText = typedArray.getString(R.styleable.CommonTitleBar_rightTxt);

        backgroundResource = typedArray.getResourceId(R.styleable.CommonTitleBar_backgroudResource, R.color.color_ffffff);
        statusBarTextColorStyle = typedArray.getInteger(R.styleable.CommonTitleBar_statusBarTextColorStyle, STATUSBAR_STYLE_DARK);  //状态栏字体颜色风格：默认暗色风格
        //状态栏颜色，默认白
        statusbarBGColor = typedArray.getColor(R.styleable.CommonTitleBar_statusBarBGColor, -1);
        //显示模式，默认标题、左返回
        pattern = typedArray.getInteger(R.styleable.CommonTitleBar_pattern, PATTERN_TITLE_WITH_LEFT_IMG);    //默认显示标题和左返回按钮
        //左右点击事件
        leftAction = typedArray.getInteger(R.styleable.CommonTitleBar_leftAction, ACTION_LEFT_BACK);    //默认返回上一页
        rightAction = typedArray.getInteger(R.styleable.CommonTitleBar_rightAction, ACTION_RIGHT_NONE); //默认无点击事件

        primaryTitleColor = typedArray.getColor(R.styleable.CommonTitleBar_primaryTitleTextColor, primaryTitleColor);
        isTranslucentStatusBar = typedArray.getBoolean(R.styleable.CommonTitleBar_isTranslucentStatusBar, true);

        typedArray.recycle();
        initView();
        initListener();
        if (isTranslucentStatusBar)
            setStatusBar();
        if (statusbarBGColor != -1) {
            setStatusbarBGColor(statusbarBGColor);
        }
    }

    private void setStatusBar() {
        StatusBarUtil.translucentStatusBar((Activity) getContext());
        dynamicSetMarginTop();
    }

    private void dynamicSetMarginTop() {
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(getContext());
        MarginLayoutParams params = (MarginLayoutParams) rlContent.getLayoutParams();
        params.topMargin = statusBarHeight;
        rlContent.setLayoutParams(params);
    }


    private void initListener() {
        if (tvLeft != null) {   //左图标与左文字不同时显示
            tvLeft.setOnClickListener(new MyLeftClickListener());
        }
        if (tvRight != null) {
            tvRight.setOnClickListener(new MyRightClickListener());
        }
    }

    private void initView() {
        setBackground(backgroundResource);
        setStatusBarTextColorSytle(statusBarTextColorStyle);
        initPattern(pattern);
        tvPrimaryTitle.setTextColor(primaryTitleColor);
    }


    private void initPattern(int pattern) {
        setPrimaryTitle(primaryTitle);
        switch (pattern) {
            case PATTERN_LEFT_ONLY:     //通过代码动态设置背景
                tvLeft.setVisibility(VISIBLE);

                tvPrimaryTitle.setVisibility(INVISIBLE);
                tvSubTitle.setVisibility(GONE);
                tvRight.setVisibility(INVISIBLE);
                break;
            case PATTERN_TITLE_WITH_LEFT_IMG:
                tvPrimaryTitle.setVisibility(VISIBLE);
                tvLeft.setVisibility(VISIBLE);

                tvSubTitle.setVisibility(GONE);
                tvRight.setVisibility(INVISIBLE);
                setLeftImgSrc(leftImgSrc);
                break;
            case PATTERN_TITLE_WITH_LEFT_TXT:
                tvPrimaryTitle.setVisibility(VISIBLE);
                tvLeft.setVisibility(VISIBLE);

                tvSubTitle.setVisibility(GONE);
                tvRight.setVisibility(INVISIBLE);
                setLeftText(leftText);
                break;
            case PATTERN_TITLE_WITH_RIGHT_IMG:
                tvPrimaryTitle.setVisibility(VISIBLE);
                tvRight.setVisibility(VISIBLE);

                tvSubTitle.setVisibility(GONE);
                tvLeft.setVisibility(INVISIBLE);
                setRightImgSrc(rightImgSrc);
                break;
            case PATTERN_TITLE_WITH_RIGHT_TXT:
                tvPrimaryTitle.setVisibility(VISIBLE);
                tvRight.setVisibility(VISIBLE);

                tvSubTitle.setVisibility(GONE);
                tvLeft.setVisibility(INVISIBLE);
                setRightText(rightText);
                break;
            case PATTERN_TITLE_WITH_TWO_IMG:
                tvPrimaryTitle.setVisibility(VISIBLE);
                tvLeft.setVisibility(VISIBLE);
                tvRight.setVisibility(VISIBLE);

                tvSubTitle.setVisibility(GONE);
                setLeftImgSrc(leftImgSrc);
                setRightImgSrc(rightImgSrc);
                break;
            case PATTERN_TITLE_WITH_TWO_TXT:
                tvPrimaryTitle.setVisibility(VISIBLE);
                tvLeft.setVisibility(VISIBLE);
                tvRight.setVisibility(VISIBLE);

                tvSubTitle.setVisibility(GONE);
                setLeftText(leftText);
                setRightText(rightText);
                break;
            case PATTERN_TITLE_WITH_LEFT_IMG_AND_RIGHT_TXT:
                tvPrimaryTitle.setVisibility(VISIBLE);
                tvLeft.setVisibility(VISIBLE);
                tvRight.setVisibility(VISIBLE);

                tvSubTitle.setVisibility(GONE);
                setLeftImgSrc(leftImgSrc);
                setRightText(rightText);
                break;
            case PATTERN_TITLE_WITH_LEFT_TXT_AND_RIGHT_IMG:
                tvPrimaryTitle.setVisibility(VISIBLE);
                tvLeft.setVisibility(VISIBLE);
                tvRight.setVisibility(VISIBLE);

                tvSubTitle.setVisibility(GONE);
                setLeftText(leftText);
                setRightImgSrc(rightImgSrc);
                break;
            case PATTERN_DOUBLE_TITLE_WITH_LEFT_IMG_RIGHT_TXT:
                tvPrimaryTitle.setVisibility(VISIBLE);
                tvLeft.setVisibility(VISIBLE);
                tvRight.setVisibility(VISIBLE);
                tvSubTitle.setVisibility(VISIBLE);

                setLeftImgSrc(leftImgSrc);
                setRightText(rightText);
                setSubTitle(subTitle);
                break;
        }
    }


    /**
     * 设置状态栏字体颜色风格【亮 or 暗】
     */
    private void setStatusBarTextColorSytle(int statusbarTextColorStyle) {
        setStatusBarTextColorSytle(statusbarTextColorStyle == STATUSBAR_STYLE_DARK);
    }

    public void setStatusBarTextColorSytle(boolean isDark) {
        try {
            StatusBarUtil.setStatusBarTextColorStyle((Activity) getContext(), isDark);
        } catch (Exception e) {
            Log.e(TAG, "setStatusBarColorSytle: EEEEEEEEEEEEEE < " + e.toString() + " >");
        }
    }

    /**
     * 设置根布局背景
     *
     * @param backgroundResource
     */
    public void setBackground(int backgroundResource) {
        this.backgroundResource = backgroundResource;
        if (rlRootView != null) {
            rlRootView.setBackgroundResource(backgroundResource);
        }
    }

    public void setStatusbarBGColor(@ColorInt int color) {
        StatusBarUtil.setColor((Activity) mContext, color);
    }

    public void setPattern(int pattern) {
        if (this.pattern != pattern) {
            this.pattern = pattern;
            initPattern(pattern);
        }
    }

    public void setPrimaryTitle(String primaryTitle) {
        if (tvPrimaryTitle != null && !TextUtils.isEmpty(primaryTitle)) {
            tvPrimaryTitle.setText(primaryTitle);
        }
    }

    public void setSubTitle(String subTitle) {
        if (tvSubTitle != null && !TextUtils.isEmpty(subTitle)) {
            tvSubTitle.setText(subTitle);
        }
    }

    public void setLeftText(String leftText) {
        if (tvLeft != null) {
            clearTextDrawable(tvLeft);
            tvLeft.setText(leftText);
        }
    }

    /**
     * 清除左图标、显示左文本
     */
    private void clearTextDrawable(TextView textView) {
        if (textView != null) {
            textView.setCompoundDrawables(null, null, null, null);
        }
    }

    public void setRightText(String rightText) {
        if (tvRight != null) {
            clearTextDrawable(tvRight);
            tvRight.setText(rightText);
        }
    }

    public void setLeftImgSrc(int leftImgSrc) {
        this.leftImgSrc = leftImgSrc;
        if (tvLeft != null) {
            setTextViewDrawable(tvLeft, leftImgSrc);
        }
    }

    /**
     * 给textView设置左按钮
     *
     * @param textView
     * @param imgSrc
     */
    private void setTextViewDrawable(TextView textView, int imgSrc) {
        textView.setText("");
        Drawable drawable = getResources().getDrawable(imgSrc);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    public void setRightImgSrc(int rightImgSrc) {
        this.rightImgSrc = rightImgSrc;
        if (tvRight != null) {
            setTextViewDrawable(tvRight, rightImgSrc);
        }
    }

    /**
     * 左图标、文字点击事件
     */
    private class MyLeftClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (onLeftClickListener != null) {      //代码设置点击事件则会覆盖xml中的配置点击事件
                onLeftClickListener.onClickLeft();
            } else {
                if (leftAction == ACTION_LEFT_BACK) {
                    ActivityManager.getInstance().removeAndFinishLastActivity();
                }
            }
        }
    }

    /**
     * 右图标、文字点击事件
     */
    private class MyRightClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (onRightClickListener != null) {
                onRightClickListener.onClickRight();
            } else {
                if (rightAction == ACTION_RIGHT_ENTER_FUND_DETAIL) { //进入基金详情
                } else if (rightAction == ACTION_RIGHT_ENTER_SEARCH) {    //进入搜索页面
                }
            }
        }
    }

    /**
     * 左点击事件回调
     */
    public interface OnLeftClickListener {
        void onClickLeft();
    }

    private OnLeftClickListener onLeftClickListener;

    public void setOnLeftClickListener(OnLeftClickListener onLeftClickListener) {
        this.onLeftClickListener = onLeftClickListener;
    }

    /**
     * 右点击事件回调
     */
    public interface onRightClickListener {
        void onClickRight();
    }

    private onRightClickListener onRightClickListener;

    public void setOnRightClickListener(onRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
    }

}
