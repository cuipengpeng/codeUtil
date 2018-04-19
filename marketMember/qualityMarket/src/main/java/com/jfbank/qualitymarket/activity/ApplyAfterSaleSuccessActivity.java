package com.jfbank.qualitymarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/4/21<br>
*/
public class ApplyAfterSaleSuccessActivity extends BaseActivity {
    public static final String TAG = ApplyAfterSaleSuccessActivity.class.getName();

    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.iv_back)
    ImageView ivBack;

    @InjectView(R.id.btn_applyAfterSaleSuccessActivity_queryProgress)
    Button queryProgressButton;
    @InjectView(R.id.btn_applyAfterSaleSuccessActivity_indexPageOfAfterSale)
    Button indexPageOfAfterSaleButton;

    @Override
    protected String getPageName() {
        return "申请售后成功";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_after_sale_success);
        ButterKnife.inject(this);

        CommonUtils.setTitle(this,rlTitle);
        tvTitle.setText("申请售后成功");
        ivBack.setVisibility(View.GONE);
    }


    @OnClick({R.id.btn_applyAfterSaleSuccessActivity_queryProgress, R.id.btn_applyAfterSaleSuccessActivity_indexPageOfAfterSale})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_applyAfterSaleSuccessActivity_queryProgress:
                Intent intent = new Intent(this, ProgressCheckForGoodsRejectedActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsUtil.EXTRA_ORDERID, getIntent().getStringExtra(ConstantsUtil.EXTRA_ORDERID));
                bundle.putString(ConstantsUtil.EXTRA_RETURNEDGOODSORDERID, getIntent().getStringExtra(ConstantsUtil.EXTRA_RETURNEDGOODSORDERID));
                bundle.putString(ConstantsUtil.EXTRA_IDENTIFICATION, getIntent().getStringExtra(ConstantsUtil.EXTRA_IDENTIFICATION));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_applyAfterSaleSuccessActivity_indexPageOfAfterSale:
                startActivity(new Intent(this, AfterSalesActivity.class));
                finish();
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

}