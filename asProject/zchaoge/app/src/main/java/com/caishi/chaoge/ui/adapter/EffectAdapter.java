package com.caishi.chaoge.ui.adapter;

import android.graphics.Color;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.EffectBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class EffectAdapter extends BaseQuickAdapter<EffectBean, BaseViewHolder> {
    public EffectAdapter() {
        super(R.layout.item_effect);
    }

    @Override
    protected void convert(BaseViewHolder helper, EffectBean item) {
        TextView tv_itemEffect_text = helper.getView(R.id.tv_itemEffect_text);
        tv_itemEffect_text.setText(item.value);
        if (item.isSelect) {
            tv_itemEffect_text.setTextColor(Color.parseColor("#000000"));
            tv_itemEffect_text.setBackgroundResource(R.drawable.shape_bg_item_select);

        } else {
            tv_itemEffect_text.setTextColor(Color.parseColor("#ffffff"));
            tv_itemEffect_text.setBackgroundResource(R.drawable.shape_bg_item_unselect);
        }


    }

}