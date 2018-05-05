package com.test.bank.weight.holder;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.TestQABean;
import com.test.bank.utils.UIUtils;

/**
 * Created by 55 on 2018/3/12.
 */

public class TestQAViewHolder extends BaseHolder<TestQABean> {

    TextView tvQuestion;
    TextView tvOptionA;
    TextView tvOptionB;
    TextView tvOptionC;
    TextView tvOptionD;

    View vA;
    View vB;
    View vC;
    View vD;

    public TestQAViewHolder(OnSelectedOptionListener onSelectedOptionListener) {
        super();
        this.onSelectedOptionListener = onSelectedOptionListener;
    }

    @Override
    protected void initView(View rootView) {
        tvQuestion = rootView.findViewById(R.id.tv_holderTestQA_question);

        tvOptionA = rootView.findViewById(R.id.tv_holderTestQA_optionA);
        tvOptionB = rootView.findViewById(R.id.tv_holderTestQA_optionB);
        tvOptionC = rootView.findViewById(R.id.tv_holderTestQA_optionC);
        tvOptionD = rootView.findViewById(R.id.tv_holderTestQA_optionD);

        vA = rootView.findViewById(R.id.v_holderTestQA_splitA);
        vB = rootView.findViewById(R.id.v_holderTestQA_splitB);
        vC = rootView.findViewById(R.id.v_holderTestQA_splitC);
        vD = rootView.findViewById(R.id.v_holderTestQA_splitD);

        tvOptionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStatus(1);
            }
        });

        tvOptionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStatus(2);
            }
        });

        tvOptionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStatus(3);
            }
        });

        tvOptionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStatus(4);
            }
        });
    }

    private void refreshStatus(int selectedOptionIndex) {
        data.setAnswer(selectedOptionIndex);
        if (onSelectedOptionListener != null) {
            onSelectedOptionListener.onSelectedOptioin(data, selectedOptionIndex);
        }
        tvOptionA.setVisibility(selectedOptionIndex == 1 ? View.VISIBLE : View.GONE);
        tvOptionA.setBackgroundColor(selectedOptionIndex == 1 ? ContextCompat.getColor(mContext, R.color.color_e6f1ff) : ContextCompat.getColor(mContext, R.color.color_ffffff));
        tvOptionB.setVisibility(selectedOptionIndex == 2 ? View.VISIBLE : View.GONE);
        tvOptionB.setBackgroundColor(selectedOptionIndex == 2 ? ContextCompat.getColor(mContext, R.color.color_e6f1ff) : ContextCompat.getColor(mContext, R.color.color_ffffff));
        tvOptionC.setVisibility(selectedOptionIndex == 3 ? View.VISIBLE : View.GONE);
        tvOptionC.setBackgroundColor(selectedOptionIndex == 3 ?ContextCompat.getColor(mContext, R.color.color_e6f1ff) : ContextCompat.getColor(mContext, R.color.color_ffffff));
        tvOptionD.setVisibility(selectedOptionIndex == 4 ? View.VISIBLE : View.GONE);
        tvOptionD.setBackgroundColor(selectedOptionIndex == 4 ?ContextCompat.getColor(mContext, R.color.color_e6f1ff) : ContextCompat.getColor(mContext, R.color.color_ffffff));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.holder_test_qa;
    }

    @Override
    protected void updateView() {
        UIUtils.setText(tvQuestion, data.getQuestion());
        UIUtils.setText(tvOptionA, data.getOptionA());
        UIUtils.setText(tvOptionB, data.getOptionB());
        UIUtils.setText(tvOptionC, data.getOptionC());
        if (TextUtils.isEmpty(data.getOptionD())) {
            tvOptionD.setVisibility(View.GONE);
        } else {
            UIUtils.setText(tvOptionD, data.getOptionD());
        }
    }

    private OnSelectedOptionListener onSelectedOptionListener;

    public interface OnSelectedOptionListener {
        void onSelectedOptioin(TestQABean data, int selectedOptionIndex);
    }
}
