package com.jfbank.qualitymarket.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.UserUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/5/11<br>
*/
public abstract class BaseUIHandlerActivity extends BaseActivity {
    public static final String TAG = BaseUIHandlerActivity.class.getName();


    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;

    @InjectView(R.id.sl_progressCheckForGoodsRejectedActivity_returnGoodsToSeller)
    ScrollView returnGoodsToSellerScrollView;
    @InjectView(R.id.sl_progressCheckForGoodsRejectedActivity_allViewsExceptReturnGoodsToSeller)
    ScrollView allViewsExceptReturnGoodsToSellerScrollView;

    @InjectView(R.id.ll_progressCheckForGoodsRejectedActivity_allDataView)
    LinearLayout allDataViewLinearLayout;
    @InjectView(R.id.ll_progressCheckForGoodsRejectedActivity_netwrokErrorView)
    LinearLayout netwrokErrorViewLinearLayout;


    @OnClick({ R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBack();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(getLayoutResID() == -1){
            throw new IllegalArgumentException("layoutResId error: "+getLayoutResID());
        }

        View screenView = LayoutInflater.from(this).inflate(R.layout.activity_base_ui_handler, null);
        View contentView = LayoutInflater.from(this).inflate(getLayoutResID(), null);
        LinearLayout replaceView = (LinearLayout) screenView.findViewById(R.id.ll_progressCheckForGoodsRejectedActivity_returnGoodsToSeller);
        replaceView.addView(contentView);
        super.onCreate(savedInstanceState);
        setContentView(screenView);
        ButterKnife.inject(this, screenView);
        tvTitle.setText(getPageTitle());
        CommonUtils.setTitle(this,rlTitle);
        netwrokErrorViewLinearLayout.setVisibility(View.GONE);
        returnGoodsToSellerScrollView.setVisibility(View.VISIBLE);
        allViewsExceptReturnGoodsToSellerScrollView.setVisibility(View.GONE);
    }





    /**
     * 设置页面标题
     *
     * @return
     */
    protected abstract String getPageTitle();

    protected abstract int getLayoutResID();


    public void onBack(){
        Toast.makeText(this, TAG+"------------onBack()", Toast.LENGTH_SHORT).show();

    }

    public void onSuccessTokenFail(Activity activity, String tokenFailMsg, String TAG){
        UserUtils.tokenFailDialog(activity, tokenFailMsg, TAG);
    }

    public void onSuccessNoData(){
        Toast.makeText(this, TAG+"------------ onSuccessNoData()", Toast.LENGTH_SHORT).show();
    }

    public void onSuccessUntreated(Activity activity, String msg){

        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public void onHttpRequestFail(){
        netwrokErrorViewLinearLayout.setVisibility(View.VISIBLE);
        returnGoodsToSellerScrollView.setVisibility(View.GONE);
        allViewsExceptReturnGoodsToSellerScrollView.setVisibility(View.GONE);
    }
}
