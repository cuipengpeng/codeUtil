package com.caishi.chaoge.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.FontBean;
import com.caishi.chaoge.utils.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class FontAdapter extends BaseQuickAdapter<FontBean, BaseViewHolder> {
    Context context;
    public FontAdapter(Context context) {
        super(R.layout.item_font);
        this.context =context;

    }

    @Override
    protected void convert(BaseViewHolder helper, FontBean item) {
        RelativeLayout rl_itemFont_layout = helper.getView(R.id.rl_itemFont_layout);
        ImageView img_itemFont_image = helper.getView(R.id.img_itemFont_image);
        TextView tv_itemFont_lib = helper.getView(R.id.tv_itemFont_lib);
        ImageView img_itemFont_isDownload = helper.getView(R.id.img_itemFont_isDownload);
        ViewGroup.LayoutParams layoutParams = rl_itemFont_layout.getLayoutParams();
        if (item.itemType == 0) {
            layoutParams.width =(int)context.getResources().getDimension(R.dimen._55dp);
            img_itemFont_image.setVisibility(View.GONE);
            tv_itemFont_lib.setVisibility(View.VISIBLE);
        } else {
            layoutParams.width =(int)context.getResources().getDimension(R.dimen._78dp);
            img_itemFont_image.setVisibility(View.VISIBLE);
            tv_itemFont_lib.setVisibility(View.GONE);
            GlideUtil.loadImg(item.isSelect ? item.selectUrl : item.unSelectUrl, img_itemFont_image);
        }
        rl_itemFont_layout.setLayoutParams(layoutParams);
        img_itemFont_isDownload.setVisibility(item.isDownload ? View.INVISIBLE : View.VISIBLE);
    }

}