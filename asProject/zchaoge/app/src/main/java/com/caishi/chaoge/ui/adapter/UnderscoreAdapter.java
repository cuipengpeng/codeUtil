package com.caishi.chaoge.ui.adapter;

import android.view.View;
import android.widget.ImageView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.UnderscoreBean;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.othershe.library.NiceImageView;

public class UnderscoreAdapter extends BaseQuickAdapter<UnderscoreBean, BaseViewHolder> {
    public UnderscoreAdapter() {
        super(R.layout.item_underscore);
    }

    @Override
    protected void convert(BaseViewHolder helper, UnderscoreBean item) {
        NiceImageView img_itemUnderscore_image = helper.getView(R.id.img_itemUnderscore_image);
        ImageView img_itemUnderscore_select = helper.getView(R.id.img_itemUnderscore_select);
        ImageView img_itemUnderscore_play = helper.getView(R.id.img_itemUnderscore_play);
        ImageView img_itemUnderscore_state = helper.getView(R.id.img_itemUnderscore_state);//下载，以及选择的显示状态
        if (item.itemType == 0) {
            img_itemUnderscore_play.setVisibility(View.GONE);
            img_itemUnderscore_image.setImageResource(item.imgRes);
        } else {
            img_itemUnderscore_play.setVisibility(View.VISIBLE);
            GlideUtil.loadImg(item.imageUrl, img_itemUnderscore_image, R.drawable.im_pic_loading);
        }
        img_itemUnderscore_state.setVisibility(item.isDownload ? View.INVISIBLE : View.VISIBLE);
        if (item.isPlay >= 0)
            img_itemUnderscore_play.setImageResource(item.isPlay == 0 ? R.drawable.ic_suspended_music : R.drawable.ic_play_music);
        else
            img_itemUnderscore_play.setImageResource(0);
        img_itemUnderscore_select.setVisibility(item.isSelect ? View.VISIBLE : View.INVISIBLE);
    }

}