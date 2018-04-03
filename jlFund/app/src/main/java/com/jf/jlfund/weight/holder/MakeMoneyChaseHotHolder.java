package com.jf.jlfund.weight.holder;

import android.view.View;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.bean.ChaseHotBean;
import com.jf.jlfund.utils.ActivityManager;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.view.activity.BuyGoodFundDetailActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by 55 on 2017/12/8.
 */

public class MakeMoneyChaseHotHolder extends BaseHolder<ChaseHotBean> {
    TextView tvContent;

    private int index;

    public MakeMoneyChaseHotHolder(int index) {
        super();
        this.index = index;
    }

    @Override
    protected void initView(View rootView) {
        tvContent = rootView.findViewById(R.id.tv_item_chase_hot);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("fundCode", data.getFundsCode());
                map.put("fundName", data.getFundsName());
                map.put("clickIndex", index + "");
                MobclickAgent.onEvent(mContext, "client_makeMoneyFragment_chaseHotItem",map);
                BuyGoodFundDetailActivity.open(ActivityManager.getInstance().currentActivity(), data.getId(), false);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_chase_hot_home;
    }

    @Override
    protected void updateView() {
        UIUtils.setText(tvContent, data.getTitle(), "--");
        int bg = getBackgroundByIndex(index);
        if (bg != -1) {
            tvContent.setBackgroundResource(bg);
        }
    }

    private int getBackgroundByIndex(int index) {
        int bg = -1;
        switch (index) {
            case 0:
                bg = R.drawable.bg_chase_hot_1;
                break;
            case 1:
                bg = R.drawable.bg_chase_hot_2;
                break;
            case 2:
                bg = R.drawable.bg_chase_hot_3;
                break;
            case 3:
                bg = R.drawable.bg_chase_hot_4;
                break;
            case 4:
                bg = R.drawable.bg_chase_hot_5;
                break;
            case 5:
                bg = R.drawable.bg_chase_hot_6;
                break;
        }
        return bg;
    }
}
