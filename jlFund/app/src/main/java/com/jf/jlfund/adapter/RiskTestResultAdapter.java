package com.jf.jlfund.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.bean.RiskEvaluationQuestionBean;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.view.activity.RiskTestOptionActivity;
import com.jf.jlfund.view.activity.RiskTestResultActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RiskTestResultAdapter extends RecyclerView.Adapter<RiskTestResultAdapter.RiskTestResultViewHolder> {

    private Context mContext;
    public List<RiskEvaluationQuestionBean.RiskQuestionSubject> mDataList = new ArrayList<RiskEvaluationQuestionBean.RiskQuestionSubject>();
    public static final int CAREER_QUESTION_POSITION = 8;
    public static final int ADDRESS_QUESTION_POSITION = 9;
    public static final int BENIFIT_QUESTION_POSITION = 10;
    public static final int TRUST_QUESTION_POSITION = 11;

    public RiskTestResultAdapter(Context context, List<RiskEvaluationQuestionBean.RiskQuestionSubject> mData) {
        this.mContext = context;
        this.mDataList.clear();
        this.mDataList.addAll(mData);
    }

    public void upateData(boolean isRefresh, List<RiskEvaluationQuestionBean.RiskQuestionSubject> questionItems) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(questionItems);
        notifyDataSetChanged();
    }

    @Override
    public RiskTestResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RiskTestResultViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_risk_test_result, parent, false));
    }

    @Override
    public void onBindViewHolder(final RiskTestResultViewHolder holder, final int position) {
        holder.titleTextView.setText((position + 1) + "、" + mDataList.get(position).getQtitle());
        List<RiskEvaluationQuestionBean.RiskQuestionSubject.RiskQuestionItem> riskQuestionItemsList = mDataList.get(position).getQitem();
        try {
            holder.answerTextView.setText("");
            for (int i = 0; i < riskQuestionItemsList.size(); i++) {
                //前8道题用itemvalue
                if(riskQuestionItemsList.get(i).isSelected() && StringUtil.notEmpty(riskQuestionItemsList.get(i).getItemvalue())){
//                    holder.answerTextView.setText(riskQuestionItemsList.get(i).getItemtitle().substring(2));
                    holder.answerTextView.setText(riskQuestionItemsList.get(i).getItemtitle());
                    break;
                }

                //后4道题用itemTitle
                if (position>=(mDataList.size() - RiskTestResultActivity.LAST_QUESTION_COUNT) && riskQuestionItemsList.get(i).isSelected()) {
                    holder.answerTextView.setText(riskQuestionItemsList.get(i).getItemtitle());
                    break;
                }
            }

            if (position == ADDRESS_QUESTION_POSITION) {
                RiskEvaluationQuestionBean.RiskQuestionSubject riskQuestionSubject = mDataList.get(position);
                String detailAddress = "";
                if(StringUtil.notEmpty(riskQuestionSubject.getProvince())){
                    detailAddress+=riskQuestionSubject.getProvince();
                }
                if(StringUtil.notEmpty(riskQuestionSubject.getCity())){
                    detailAddress+=riskQuestionSubject.getCity();
                }
                if(StringUtil.notEmpty(riskQuestionSubject.getAddress())){
                    detailAddress+=riskQuestionSubject.getAddress();
                }
                holder.answerTextView.setText(detailAddress);
            }

            holder.itemLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, RiskTestOptionActivity.class);
                    intent.putExtra(RiskTestOptionActivity.KEY_OF_RISK_TEST_QUESTION, mDataList.get(position));
                    intent.putExtra(RiskTestOptionActivity.KEY_OF_QUESTION_NUMBER, position);
                    if (position == ADDRESS_QUESTION_POSITION) {
                        intent.putExtra(RiskTestOptionActivity.KEY_OF_QUESTION_TYPE, RiskTestOptionActivity.ADDRESS_QUESTION);
                    } else {
                        intent.putExtra(RiskTestOptionActivity.KEY_OF_QUESTION_TYPE, RiskTestOptionActivity.SINGLE_QUESTION);
                    }
                    ((RiskTestResultActivity) mContext).startActivityForResult(intent, position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(position == mDataList.size()-1){
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
        @BindView(R.id.tv_riskTestResultActivity_item_title)
        TextView titleTextView;
        @BindView(R.id.v_riskTestResultActivity_item_divider)
        View dividerView;
        @BindView(R.id.tv_riskTestResultActivity_item_answer)
        TextView answerTextView;
        @BindView(R.id.ll_riskTestResultActivity_item)
        LinearLayout itemLinearLayout;

        public RiskTestResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
