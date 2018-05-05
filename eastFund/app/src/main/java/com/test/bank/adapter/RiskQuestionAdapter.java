package com.test.bank.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.RiskEvaluationQuestionBean;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.UIUtils;

import java.util.List;

/**
 * Created by 55 on 2018/1/5.
 */

public class RiskQuestionAdapter extends RecyclerView.Adapter<RiskQuestionAdapter.RiskQuestionViewHolder> {

    private Context context;
    private List<RiskEvaluationQuestionBean.RiskQuestionSubject.RiskQuestionItem> mData;

    public RiskQuestionAdapter(Context context, List<RiskEvaluationQuestionBean.RiskQuestionSubject.RiskQuestionItem> mData) {
        this.context = context;
        this.mData = mData;
    }

    private String lastChoose;

    public void refreshData(List<RiskEvaluationQuestionBean.RiskQuestionSubject.RiskQuestionItem> questionItems, String lastChoose) {
        this.mData.clear();
        this.mData.addAll(questionItems);
        this.lastChoose = lastChoose;
        LogUtils.e("refreshData.lastChoose: " + lastChoose);
        LogUtils.e("mData: " + mData.toString());
        notifyDataSetChanged();
    }

    @Override
    public RiskQuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RiskQuestionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_risk_question, parent, false));
    }


    @Override
    public void onBindViewHolder(final RiskQuestionViewHolder holder, final int position) {
        UIUtils.setText(holder.tvContent, mData.get(position).getItemtitle());
        if ((!TextUtils.isEmpty(lastChoose) && lastChoose.equals(mData.get(position).getItemvalue())) || mData.get(position).isSelected()) {
            holder.ivCheck.setSelected(true);
        } else {
            holder.ivCheck.setSelected(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshOtherCheckStatus(position);
                lastChoose = mData.get(position).getItemvalue();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onSelectedLisener != null) {
                            onSelectedLisener.onSelect(mData.get(position).getItemvalue());
                        }
                    }
                }, 200);
            }
        });
    }

    private void refreshOtherCheckStatus(int pos) {
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setSelected(i == pos);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class RiskQuestionViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivCheck;
        private TextView tvContent;

        public RiskQuestionViewHolder(View itemView) {
            super(itemView);
            ivCheck = itemView.findViewById(R.id.iv_singleChoseCheck);
            tvContent = itemView.findViewById(R.id.tv_singleChoseText);
        }
    }

    private OnSelectedLisener onSelectedLisener;

    public void setOnSelectedLisener(OnSelectedLisener onSelectedLisener) {
        this.onSelectedLisener = onSelectedLisener;
    }


    public interface OnSelectedLisener {
        void onSelect(String questionTag);
    }
}
