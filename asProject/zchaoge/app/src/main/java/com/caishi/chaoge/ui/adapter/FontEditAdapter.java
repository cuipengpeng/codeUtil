package com.caishi.chaoge.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.FontBean;
import com.caishi.chaoge.utils.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class FontEditAdapter extends BaseQuickAdapter<FontBean, BaseViewHolder> {
    Context context;

    public FontEditAdapter(Context context) {
        super(R.layout.item_font_edit);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, FontBean item) {
        ImageView img_itemFont_image = helper.getView(R.id.img_itemFont_image);
        ImageView img_itemFont_isDownload = helper.getView(R.id.img_itemFont_isDownload);
        img_itemFont_image.setVisibility(View.VISIBLE);
        GlideUtil.loadImg(item.isSelect ? item.selectUrl : item.unSelectUrl, img_itemFont_image);
        img_itemFont_isDownload.setVisibility(item.isDownload ? View.INVISIBLE : View.VISIBLE);
    }

}