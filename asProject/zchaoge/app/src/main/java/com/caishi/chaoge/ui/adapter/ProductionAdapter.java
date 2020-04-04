package com.caishi.chaoge.ui.adapter;

import android.widget.ImageView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.HomeDataBean;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.StringUtil;
import com.caishi.chaoge.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class ProductionAdapter extends BaseQuickAdapter<HomeDataBean,BaseViewHolder> {
    public ProductionAdapter() {
        super(R.layout.item_like);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeDataBean item) {
        ImageView img_item_img = helper.getView(R.id.img_item_img);
        if(StringUtil.notEmpty(item.getPoster())){
            GlideUtil.loadImg(Utils.isUrl(item.getPoster()),img_item_img, R.drawable.im_pic_loading);
        }else {
            GlideUtil.loadImg(Utils.isUrl(item.getCover()),img_item_img, R.drawable.im_pic_loading);
        }


    }
}
