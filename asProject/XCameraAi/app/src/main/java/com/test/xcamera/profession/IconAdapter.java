package com.test.xcamera.profession;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.test.xcamera.R;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.utils.ViewUitls;

import java.io.Serializable;

/**
 * Created by smz on 2020/1/15.
 */

public class IconAdapter extends BaseQuickAdapter<IconAdapter.SettingItem, BaseViewHolder> {
    public boolean mEnableSele = true;

    public IconAdapter() {
        super(R.layout.item_profession_icon);
    }

    @Override
    protected void convert(BaseViewHolder helper, IconAdapter.SettingItem item) {
        ImageView imageView = helper.getView(R.id.icon);
        imageView.setImageResource(item.icon);
        imageView.setSelected(item.selected);

        TextView textView = helper.getView(R.id.text);
        textView.setText(item.text);

        if (item.flag == 1)
            textView.setTextColor(mContext.getResources().getColor(R.color.color_404040));
        else if (mEnableSele) {
            textView.setTextColor(item.selected ? mContext.getResources().getColor(R.color.color_ff7700) : Color.WHITE);
        } else {
            textView.setTextColor(Color.WHITE);
        }

        View content = helper.getView(R.id.item_content);
        int rotation = 0;
        if (item.rotation == ScreenOrientationType.PORTRAIT)
            rotation = -90;
        Log.e("=====", "rotation==>" + rotation);
        content.setRotation(rotation);

        //TODO 更多设置 暂时隐藏
        int margin = 0;
        switch (getData().size()) {
            case 3:
                margin = ViewUitls.dp2px(imageView.getContext(), 20);
                break;
            case 2:
                margin = ViewUitls.dp2px(imageView.getContext(), 25);
                break;
            default:
                margin = ViewUitls.dp2px(imageView.getContext(), 10);
                break;
        }

        RecyclerView.LayoutParams ll = (RecyclerView.LayoutParams) content.getLayoutParams();
        ll.setMargins(0, margin, 0, margin);
        content.setLayoutParams(ll);
    }

    public static class SettingItem implements Serializable{
        public int icon;
        public String text;
        public boolean selected;
        public int extra;
        public int flag;
        public ScreenOrientationType rotation = ScreenOrientationType.LANDSCAPE;

        public SettingItem(int icon, String text, int extra, boolean selected) {
            this(icon, text, extra, selected, 0);
        }

        public SettingItem(int icon, String text, int extra, boolean selected, int flag) {
            this.icon = icon;
            this.text = text;
            this.extra = extra;
            this.selected = selected;
            this.flag = flag;
        }

        @Override
        public String toString() {
            return "SettingItem{" +
                    "icon=" + icon +
                    ", text='" + text + '\'' +
                    ", selected=" + selected +
                    '}';
        }
    }
}
