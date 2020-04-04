package com.caishi.chaoge.ui.adapter;


import android.widget.ImageView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.SpecialBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class SpecialAdapter extends BaseQuickAdapter<SpecialBean, BaseViewHolder> {
    public SpecialAdapter() {
        super(R.layout.item_special);
    }

    @Override
    protected void convert(BaseViewHolder helper, SpecialBean item) {
        ImageView img_itemSpecial_img = helper.getView(R.id.img_itemSpecial_img);
        img_itemSpecial_img.setImageResource((item.isSelect ? item.selected : item.unSelected));
    }

}