package com.test.bank.weight.holder;

import android.view.View;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.SearchResultBean;
import com.test.bank.utils.UIUtils;

/**
 * Created by 55 on 2017/12/13.
 */

public class SearchHistoryItemViewHolder extends BaseHolder<SearchResultBean.SearchResultItem> {

    TextView textView;

    @Override
    protected void initView(View rootView) {
        textView = rootView.findViewById(R.id.tv_item_search_history);
    }

    private OnClickSearchHistoryItemListener onClickSearchHistoryItemListener;

    public void setOnClickSearchHistoryItemListener(OnClickSearchHistoryItemListener onClickSearchHistoryItemListener) {
        this.onClickSearchHistoryItemListener = onClickSearchHistoryItemListener;
    }

    public interface OnClickSearchHistoryItemListener {
        void onClickHistorySearchItem(SearchResultBean.SearchResultItem searchResultItem);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_search_history;
    }

    @Override
    protected void updateView() {
        UIUtils.setText(textView, data.getFundname(), "--");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickSearchHistoryItemListener != null) {
                    onClickSearchHistoryItemListener.onClickHistorySearchItem(data);
                }
            }
        });
    }
}
