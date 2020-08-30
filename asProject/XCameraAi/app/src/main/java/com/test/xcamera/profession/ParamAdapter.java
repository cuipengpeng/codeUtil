package com.test.xcamera.profession;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.test.xcamera.R;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.view.VerticalTextView;

import static com.test.xcamera.view.VerticalTextView.ORIENTATION_DOWN_TO_UP;
import static com.test.xcamera.view.VerticalTextView.ORIENTATION_LEFT_TO_RIGHT;

/**
 * Created by smz on 2020/1/19.
 * <p>
 * 自定义值得参数
 */

public class ParamAdapter extends BaseQuickAdapter<Constant.ParamMap, BaseViewHolder> {

    public ParamAdapter() {
        super(R.layout.item_rule_recycle);
    }

    @Override
    protected void convert(BaseViewHolder helper, Constant.ParamMap item) {
        VerticalTextView vtv = helper.getView(R.id.text);
        if (item.rotation == ScreenOrientationType.PORTRAIT)
            vtv.setDirection(ORIENTATION_DOWN_TO_UP);
        else if (item.rotation == ScreenOrientationType.LANDSCAPE)
            vtv.setDirection(ORIENTATION_LEFT_TO_RIGHT);
        vtv.setText(item.value);
        vtv.setSelected(item.selected);

        View line = helper.getView(R.id.line);
        line.setVisibility(item.hideImg ? View.INVISIBLE : View.VISIBLE);
        line.setSelected(item.selected);
    }
}