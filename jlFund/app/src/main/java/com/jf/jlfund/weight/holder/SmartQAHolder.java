package com.jf.jlfund.weight.holder;

import android.view.View;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.bean.SuggestQuestion;
import com.jf.jlfund.utils.UIUtils;

/**
 * Created by 55 on 2018/2/9.
 */

public class SmartQAHolder extends BaseHolder<SuggestQuestion> {
    TextView tvQuestion;
    private OnClickSuggestQuestionListener onClickSuggestQuestionListener;

    public SmartQAHolder(OnClickSuggestQuestionListener onClickSuggestQuestionListener) {
        this.onClickSuggestQuestionListener = onClickSuggestQuestionListener;
    }

    @Override
    protected void initView(View rootView) {
        tvQuestion = rootView.findViewById(R.id.tv_holderSmartQA);
        tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickSuggestQuestionListener != null)
                    onClickSuggestQuestionListener.onClickSuggestQuestion(data);
            }
        });
    }

    public interface OnClickSuggestQuestionListener {
        void onClickSuggestQuestion(SuggestQuestion suggestQuestion);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.holder_smart_qa_question;
    }

    @Override
    protected void updateView() {
        UIUtils.setText(tvQuestion, data.getQuestion_str());
    }
}
