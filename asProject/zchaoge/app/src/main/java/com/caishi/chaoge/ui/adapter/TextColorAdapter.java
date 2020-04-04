package com.caishi.chaoge.ui.adapter;

import android.view.View;
import android.widget.ImageView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.TextBean;
import com.caishi.chaoge.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.othershe.library.NiceImageView;

public class TextColorAdapter extends BaseQuickAdapter<TextBean, BaseViewHolder> {
    public TextColorAdapter() {
        super(R.layout.item_text);
    }

    @Override
    protected void convert(BaseViewHolder helper, TextBean item) {
        NiceImageView img_itemText_image = helper.getView(R.id.img_itemText_image);
        ImageView img_itemText_select = helper.getView(R.id.img_itemText_select);
        if (item.itemType==0){
            img_itemText_image.setImageResource(item.imgRes);
        }else{
            img_itemText_image.setImageBitmap(Utils.RGB2Bitmap(item.color));
        }

        img_itemText_select.setVisibility(item.isSelect? View.VISIBLE:View.INVISIBLE);
    }

}