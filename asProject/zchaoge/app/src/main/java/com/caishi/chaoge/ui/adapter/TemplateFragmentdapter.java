package com.caishi.chaoge.ui.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.RecommendBean;
import com.caishi.chaoge.utils.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.othershe.library.NiceImageView;

public class TemplateFragmentdapter extends BaseQuickAdapter<RecommendBean, BaseViewHolder> {
    public TemplateFragmentdapter() {
        super(R.layout.item_template_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecommendBean item) {
        NiceImageView img_itemTemplate_image = helper.getView(R.id.img_itemTemplate_image);
            GlideUtil.loadImg(item.modelCover, img_itemTemplate_image, R.drawable.im_pic_loading);
            if (item.isSelect) {
                img_itemTemplate_image.setBorderColor(Color.parseColor("#FE5175"));
                img_itemTemplate_image.setBorderWidth(1);
            }else{
                img_itemTemplate_image.setBorderWidth(0);
            }
    }

}