package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.RiskEvaluationQuestionBean;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RiskTestOptionAdapter extends RecyclerView.Adapter<RiskTestOptionAdapter.RiskTestResultViewHolder> {

    private Context mContext;
    private List<RiskEvaluationQuestionBean.RiskQuestionSubject.RiskQuestionItem> mDataList = new ArrayList<RiskEvaluationQuestionBean.RiskQuestionSubject.RiskQuestionItem>();
    private RiskEvaluationQuestionBean.RiskQuestionSubject riskQuestionSubject = new RiskEvaluationQuestionBean.RiskQuestionSubject();

    public RiskTestOptionAdapter(Context context, List<RiskEvaluationQuestionBean.RiskQuestionSubject.RiskQuestionItem> mData, RiskEvaluationQuestionBean.RiskQuestionSubject riskQuestionSubject) {
        this.mContext = context;
        this.mDataList.clear();
        this.mDataList.addAll(mData);
        this.riskQuestionSubject = riskQuestionSubject;
    }

    public void upateData(boolean isRefresh, List<RiskEvaluationQuestionBean.RiskQuestionSubject.RiskQuestionItem> questionItems) {
        if(isRefresh){
            this.mDataList.clear();
        }
        this.mDataList.addAll(questionItems);
        LogUtils.e("mDataList: " + mDataList.toString());
        notifyDataSetChanged();
    }

    @Override
    public RiskTestResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RiskTestResultViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_risk_question, parent, false));
    }


    @Override
    public void onBindViewHolder(final RiskTestResultViewHolder holder, final int position) {
        UIUtils.setText(holder.tvContent, mDataList.get(position).getItemtitle());
        holder.ivCheck.setBackgroundResource(R.drawable.selector_radiobutton_blue);
        if (mDataList.get(position).isSelected()) {
            holder.ivCheck.setSelected(true);
        } else {
            holder.ivCheck.setSelected(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(riskQuestionSubject == null){
                    riskQuestionSubject = new RiskEvaluationQuestionBean.RiskQuestionSubject();
                    riskQuestionSubject.setQitem(mDataList);
                }

                for (int i = 0; i < mDataList.size(); i++) {
                    riskQuestionSubject.getQitem().get(i).setSelected(false);
                    mDataList.get(i).setSelected(false);
                }
                riskQuestionSubject.getQitem().get(position).setSelected(true);
                mDataList.get(position).setSelected(true);
                notifyDataSetChanged();

                if(StringUtil.notEmpty(mDataList.get(position).getItemvalue())){
                    riskQuestionSubject.setMyanswer(mDataList.get(position).getItemvalue());
                }else {
                    riskQuestionSubject.setMyanswer(mDataList.get(position).getItemtitle());
                }
            }
        });

        if(mDataList.size()>4 && position == mDataList.size()-1){
            holder.dividerView.setVisibility(View.GONE);
        }else {
            holder.dividerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class RiskTestResultViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_singleChoseCheck)
        ImageView ivCheck;
        @BindView(R.id.tv_singleChoseText)
        TextView tvContent;
        @BindView(R.id.v_riskTestOptionActivity_item_divider)
        View dividerView;

        public RiskTestResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
