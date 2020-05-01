package com.jfbank.qualitymarket.adapter;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/1/12<br>
*/
public class SearchGoodsHistoryAdapter extends BaseAdapter {
        private List<String> orderList;
        private Activity activity;
        private boolean searchRecommend = false;
        private  String searchText;

        public SearchGoodsHistoryAdapter(Activity activity, List<String> orderList, boolean searchRecommend) {
            super();
            this.orderList = orderList;
            this.activity = activity;
            this.searchRecommend = searchRecommend;
        }

        public void setSearchText(String searchText){
            this.searchText = searchText;
        }

        public void setData(List<String> orderList){
            this.orderList = orderList;
        }

        @Override
        public int getCount() {
            return orderList.size();
        }

        @Override
        public Object getItem(int position) {
            return orderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(activity, R.layout.search_goods_search_history_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if(searchRecommend){
                String serverMatchString = orderList.get(position);
                int changeColorIndex = serverMatchString.indexOf(searchText);
                int changeColorLastIndex = changeColorIndex + searchText.length();

                if(changeColorIndex == -1){
                    viewHolder.searchNameTextView.setText( orderList.get(position));
                }else if(changeColorIndex == 0) {
                    SpannableString styledText = new SpannableString(serverMatchString);
                    styledText.setSpan(new TextAppearanceSpan(activity, R.style.style_heavy_gray_13sp), 0, changeColorLastIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledText.setSpan(new TextAppearanceSpan(activity, R.style.style_light_gray_13sp), changeColorLastIndex,
                            serverMatchString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.searchNameTextView.setText(styledText, TextView.BufferType.SPANNABLE);
                }else {

                    SpannableString styledText = new SpannableString(serverMatchString);
                    styledText.setSpan(new TextAppearanceSpan(activity, R.style.style_light_gray_13sp), 0, changeColorIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledText.setSpan(new TextAppearanceSpan(activity, R.style.style_heavy_gray_13sp), changeColorIndex, changeColorLastIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledText.setSpan(new TextAppearanceSpan(activity, R.style.style_light_gray_13sp), changeColorLastIndex,
                            serverMatchString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.searchNameTextView.setText(styledText, TextView.BufferType.SPANNABLE);
                }

            }else {
                viewHolder.searchNameTextView.setText( orderList.get(position));
            }

            return convertView;
        }

     class ViewHolder {
        @InjectView(R.id.iv_searchGoodsActivity_searchRecommendItem_searchName)
        TextView searchNameTextView;

        public ViewHolder(View v) {
            super();
            ButterKnife.inject(this, v);
        }
    }
}