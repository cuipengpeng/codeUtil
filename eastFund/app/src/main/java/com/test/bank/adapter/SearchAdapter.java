package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.base.BaseBean;
import com.test.bank.base.BaseBusiness;
import com.test.bank.bean.OptionalFundBean;
import com.test.bank.bean.SearchResultBean;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.UIUtils;
import com.test.bank.view.activity.LoginActivity;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by 55 on 2017/12/13.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Context context;
    private List<SearchResultBean.SearchResultItem> mDatas;
    private String searchKeyWord;   //搜索关键词

    public SearchAdapter(Context context, List<SearchResultBean.SearchResultItem> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    public void setSearchKeyWord(String searchKeyWord) {
        this.searchKeyWord = searchKeyWord;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {
        UIUtils.setText(holder.tvName, UIUtils.highlight(mDatas.get(position).getFundname(), searchKeyWord, 0, 0), "---");
        UIUtils.setText(holder.tvCode, UIUtils.highlight(mDatas.get(position).getFundresCode(), searchKeyWord, 0, 0), "---");
        holder.ivAdd.setSelected(mDatas.get(position).getCollected() == 1);

        //富文本
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickSearchItemListener != null) {
                    onClickSearchItemListener.onClickSearchItem(mDatas.get(position));
                }
            }
        });

        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SPUtil.getInstance().isLogin()) {
                    LoginActivity.open(context);
                    return;
                }
                if (holder.ivAdd.isSelected() && mDatas.get(position).getCollected() == 1) {
                    deCollFund(mDatas.get(position).getFundresCode(), holder.ivAdd, position);
                } else {
                    collFund(mDatas.get(position).getFundresCode(), holder.ivAdd, position);
                }
            }
        });
    }

    private void collFund(final String fundCode, final ImageView ivColl, final int position) {
        BaseBusiness.postRequest(new OnResponseListener<OptionalFundBean>() {
            @Override
            public Observable<BaseBean<OptionalFundBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.putLast("sid", fundCode);
                return NetService.getNetService().collectFund(paramMap);
            }

            @Override
            public void onResponse(BaseBean<OptionalFundBean> result) {
                if (result.isSuccess()) {
                    ivColl.setSelected(true);
                    mDatas.get(position).setCollected(1);
                }
            }

            @Override
            public void onError(String errorMsg) {
            }
        });
    }

    private void deCollFund(final String fundCode, final ImageView ivColl, final int position) {
        BaseBusiness.postRequest(new OnResponseListener<OptionalFundBean>() {
            @Override
            public Observable<BaseBean<OptionalFundBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.putLast("sid", fundCode);
                return NetService.getNetService().deCollectFund(paramMap);
            }

            @Override
            public void onResponse(BaseBean<OptionalFundBean> result) {
                if (result.isSuccess()) {
                    ivColl.setSelected(false);
                    mDatas.get(position).setCollected(0);
                }
            }

            @Override
            public void onError(String errorMsg) {
            }
        });
    }


    public OnClickSearchItemListener onClickSearchItemListener;

    public void setOnClickSearchItemListener(OnClickSearchItemListener onClickSearchItemListener) {
        this.onClickSearchItemListener = onClickSearchItemListener;
    }

    public interface OnClickSearchItemListener {
        void onClickSearchItem(SearchResultBean.SearchResultItem item);
    }

    @Override
    public int getItemCount() {
        return null == mDatas ? 0 : mDatas.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvCode;
        ImageView ivAdd;

        public SearchViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_search_result_content);
            tvCode = itemView.findViewById(R.id.tv_item_search_result_code);
            ivAdd = itemView.findViewById(R.id.iv_item_search_result_selecte);
        }
    }
}
