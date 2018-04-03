package com.jf.jlfund.weight.holder;

import android.graphics.Paint;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.utils.UIUtils;

/**
 * Created by 55 on 2018/1/22.
 */

public class RateItemHolder extends BaseHolder<String[]> {
    public static String TYPE_SALE_OUT_HIDE_DISCOUNT_RATE = "隐藏优惠费率一栏"; //赎回的费率中没有优惠费率一项
    TextView tvDesc;
    TextView tvRealRate;    //原本费率
    TextView tvShowRate;    //优惠费率
    View bottomView;    //底部分割线

    private boolean isLastItem = false;

    public RateItemHolder(boolean isLastItem) {
        super();
        this.isLastItem = isLastItem;
    }

    @Override
    protected void initView(View rootView) {
        tvDesc = rootView.findViewById(R.id.tv_holderItemRate_desc);
        tvRealRate = rootView.findViewById(R.id.tv_holderItemRate_realRate);
        tvShowRate = rootView.findViewById(R.id.tv_holderItemRate_showRate);
        bottomView = rootView.findViewById(R.id.v_hoderItemRate);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.holder_item_rate;
    }

    @Override
    protected void updateView() {
        UIUtils.setText(tvDesc, data[0]);
        if (data[2].equals(TYPE_SALE_OUT_HIDE_DISCOUNT_RATE)) {
            tvRealRate.setVisibility(View.GONE);
            tvShowRate.setGravity(Gravity.CENTER);
        } else {
            UIUtils.setText(tvRealRate, data[2]);
            tvShowRate.setGravity(Gravity.CENTER);
            tvRealRate.setGravity(Gravity.RIGHT);
            tvShowRate.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线
        }
        UIUtils.setText(tvShowRate, data[1]);
        UIUtils.setText(tvRealRate, data[2]);
        bottomView.setVisibility(isLastItem ? View.GONE : View.VISIBLE);
    }
}
