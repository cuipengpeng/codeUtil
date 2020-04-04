package com.caishi.chaoge.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.BackgroundBean;
import com.caishi.chaoge.bean.MainFragmentBean;
import com.caishi.chaoge.bean.TextBean;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.othershe.library.NiceImageView;

public class BackgroundAdapter extends BaseQuickAdapter<BackgroundBean, BaseViewHolder> {
    public BackgroundAdapter() {
        super(R.layout.item_background);
    }

    @Override
    protected void convert(BaseViewHolder helper, BackgroundBean item) {
        NiceImageView img_itemBackground_image = helper.getView(R.id.img_itemBackground_image);
        ImageView img_itemBackground_select = helper.getView(R.id.img_itemBackground_select);
        NiceImageView img_itemBackground_cover = helper.getView(R.id.img_itemBackground_cover);//蒙层 未下载的显示
        ImageView img_itemBackground_state = helper.getView(R.id.img_itemBackground_state);//下载，以及选择的显示状态
        if (item.itemType == 0) {
            img_itemBackground_image.setImageResource(item.imgRes);
        } else {
            if (item.backGroundClass == 0) {
                GlideUtil.loadImg(item.imageUrl, img_itemBackground_image, R.drawable.im_pic_loading);
            }
            if (item.backGroundClass == 2) {
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.im_pic_loading)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                Glide.with(mContext).load(item.cover).apply(options).into(img_itemBackground_image);
            }
        }
        img_itemBackground_cover.setImageBitmap(Utils.RGB2Bitmap("#80000000"));
        img_itemBackground_state.setVisibility(item.isDownload ? View.INVISIBLE : View.VISIBLE);
        img_itemBackground_cover.setVisibility(item.isDownload ? View.INVISIBLE : View.VISIBLE);
        img_itemBackground_select.setVisibility(item.isSelect ? View.VISIBLE : View.INVISIBLE);
    }

}