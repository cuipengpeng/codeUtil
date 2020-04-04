package com.caishi.chaoge.ui.adapter;


import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.TextBean;
import com.caishi.chaoge.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.othershe.library.NiceImageView;

public class TextColorEditAdapter extends BaseQuickAdapter<TextBean, BaseViewHolder> {
    public TextColorEditAdapter() {
        super(R.layout.item_text_edit);
    }

    @Override
    protected void convert(BaseViewHolder helper, TextBean item) {
        NiceImageView img_itemText_image = helper.getView(R.id.img_itemText_image);
        img_itemText_image.setImageBitmap(Utils.RGB2Bitmap(item.color));
//        if (item.isSelect) {
//            img_itemText_image.setBorderColor(Color.parseColor("#ffffff"));
//            img_itemText_image.setBorderWidth(2);
//        }else{
//            img_itemText_image.setBorderWidth(0);
//        }
//        img_itemText_select.setVisibility(item.isSelect? View.VISIBLE:View.INVISIBLE);
    }

}