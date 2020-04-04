package com.caishi.chaoge.ui.adapter;

import android.graphics.Color;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.VideoBgColorBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.othershe.library.NiceImageView;

public class VideoBgColorAdapter extends BaseQuickAdapter<VideoBgColorBean, BaseViewHolder> {

    public VideoBgColorAdapter() {
        super(R.layout.item_videobg_color);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoBgColorBean item) {
        helper.setImageResource(R.id.nImg_itemVideoBgColor_iv, item.intColor);
        NiceImageView nImg_itemVideoBgColor_iv = helper.getView(R.id.nImg_itemVideoBgColor_iv);
        if (item.isSelect) {
            nImg_itemVideoBgColor_iv.setBorderColor(Color.parseColor("#ffffff"));
            nImg_itemVideoBgColor_iv.setBorderWidth(2);
        }else{
            nImg_itemVideoBgColor_iv.setBorderWidth(0);
        }

    }
}
