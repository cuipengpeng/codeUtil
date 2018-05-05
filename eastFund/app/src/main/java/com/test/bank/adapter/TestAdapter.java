package com.test.bank.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.TestQABean;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.UIUtils;

import java.util.List;

/**
 * Created by 55 on 2018/3/12.
 * 测一测
 */

public class TestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_HEADER = 1;
    private final static int TYPE_QA = 2;

    private Context context;
    private List<TestQABean> mData;

    public TestAdapter(Context context, List<TestQABean> mData) {
        this.context = context;
        this.mData = mData;
    }

    private OnSelectedOptionListener onSelectedOptionListener;

    public interface OnSelectedOptionListener {
        void onSelectedOption(TestQABean data, int selectedOptionIndex);
    }

    public void setOnSelectedOptionListener(OnSelectedOptionListener onSelectedOptionListener) {
        this.onSelectedOptionListener = onSelectedOptionListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_HEADER == viewType) {
            return new TestQAHeader(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_test_qa, parent, false));
        }
        return new TestQAViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_test_qa, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_QA;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TestQAHeader) {
            LogUtils.e("holder instanceof TestQAHeader..." + position);
            return;
        }
        final TestQAViewHolder qaViewHolder = (TestQAViewHolder) holder;
        UIUtils.setText(qaViewHolder.tvQuestion, mData.get(position - 1).getQuestion());
        if (mData.get(position - 1).getAnswer() == -1) {
            setTextAndInitUIStatus(qaViewHolder.tvOptionA, mData.get(position - 1).getOptionA());
            setTextAndInitUIStatus(qaViewHolder.tvOptionB, mData.get(position - 1).getOptionB());
            setTextAndInitUIStatus(qaViewHolder.tvOptionC, mData.get(position - 1).getOptionC());
            if (TextUtils.isEmpty(mData.get(position - 1).getOptionD())) {
                qaViewHolder.tvOptionD.setVisibility(View.INVISIBLE);
            } else {
                setTextAndInitUIStatus(qaViewHolder.tvOptionD, mData.get(position - 1).getOptionD());
            }
            qaViewHolder.tvOptionA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshStatus(qaViewHolder, position - 1, 1, true);
                }
            });

            qaViewHolder.tvOptionB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshStatus(qaViewHolder, position - 1, 2, true);
                }
            });

            qaViewHolder.tvOptionC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshStatus(qaViewHolder, position - 1, 3, true);
                }
            });

            qaViewHolder.tvOptionD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshStatus(qaViewHolder, position - 1, 4, true);
                }
            });
        } else {
            refreshStatus(qaViewHolder, position - 1, mData.get(position - 1).getAnswer(), false);
        }
    }

    private void setTextAndInitUIStatus(TextView textView, String text) {
        if (textView == null) {
            return;
        }
        textView.setVisibility(View.VISIBLE);
        textView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_ffffff));
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.INVISIBLE);
        } else {
            textView.setText(text);
        }
    }


    private void refreshStatus(TestQAViewHolder holder, int position, int selectedOptionIndex, boolean isClickOption) {

        holder.tvOptionA.setVisibility(selectedOptionIndex == 1 ? View.VISIBLE : View.GONE);
        holder.tvOptionA.setBackgroundColor(selectedOptionIndex == 1 ? ContextCompat.getColor(context, R.color.color_e6f1ff) : ContextCompat.getColor(context, R.color.color_ffffff));
        holder.tvOptionB.setVisibility(selectedOptionIndex == 2 ? View.VISIBLE : View.GONE);
        holder.tvOptionB.setBackgroundColor(selectedOptionIndex == 2 ? ContextCompat.getColor(context, R.color.color_e6f1ff) : ContextCompat.getColor(context, R.color.color_ffffff));
        holder.tvOptionC.setVisibility(selectedOptionIndex == 3 ? View.VISIBLE : View.GONE);
        holder.tvOptionC.setBackgroundColor(selectedOptionIndex == 3 ? ContextCompat.getColor(context, R.color.color_e6f1ff) : ContextCompat.getColor(context, R.color.color_ffffff));
        holder.tvOptionD.setVisibility(selectedOptionIndex == 4 ? View.VISIBLE : View.GONE);
        holder.tvOptionD.setBackgroundColor(selectedOptionIndex == 4 ? ContextCompat.getColor(context, R.color.color_e6f1ff) : ContextCompat.getColor(context, R.color.color_ffffff));
        holder.vA.setVisibility(View.GONE);
        holder.vB.setVisibility(View.GONE);
        holder.vC.setVisibility(View.GONE);
        holder.vD.setVisibility(View.GONE);

        if (isClickOption) {
            mData.get(position).setAnswer(selectedOptionIndex);
            if (onSelectedOptionListener != null) {
                onSelectedOptionListener.onSelectedOption(mData.get(position), selectedOptionIndex);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size() + 1;
    }

    class TestQAViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion;

        TextView tvOptionA;
        TextView tvOptionB;
        TextView tvOptionC;
        TextView tvOptionD;

        View vA;
        View vB;
        View vC;
        View vD;

        public TestQAViewHolder(View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tv_holderTestQA_question);

            tvOptionA = itemView.findViewById(R.id.tv_holderTestQA_optionA);
            tvOptionB = itemView.findViewById(R.id.tv_holderTestQA_optionB);
            tvOptionC = itemView.findViewById(R.id.tv_holderTestQA_optionC);
            tvOptionD = itemView.findViewById(R.id.tv_holderTestQA_optionD);

            vA = itemView.findViewById(R.id.v_holderTestQA_splitA);
            vB = itemView.findViewById(R.id.v_holderTestQA_splitB);
            vC = itemView.findViewById(R.id.v_holderTestQA_splitC);
            vD = itemView.findViewById(R.id.v_holderTestQA_splitD);
        }
    }

    class TestQAHeader extends RecyclerView.ViewHolder {

        public TestQAHeader(View itemView) {
            super(itemView);
        }
    }
}
