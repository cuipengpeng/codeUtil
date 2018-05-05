package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.bank.R;
import com.test.bank.bean.SmartQABean;
import com.test.bank.bean.SuggestQuestion;
import com.test.bank.utils.DensityUtil;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.UIUtils;
import com.test.bank.weight.holder.SmartQAHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 55 on 2018/2/8.
 * 智能客服聊天adapter
 */

public class SmartServerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_SHOW_QUESTION_ONLY = 1;     //只显示问题，不显示时间和答案
    public static final int TYPE_SHOW_ALL = 2;       //显示时间、问题和答案

    public static final int TYPE_FIRST_HELLO = 11;  //欢迎使用元小基提示语
    public static final int TYPE_QA = 12;


    private Context context;
    private List<SmartQABean> mData;

    public SmartServerAdapter(Context context, List<SmartQABean> mData) {
        this.context = context;
        this.mData = mData;
    }

    public void addQuestion(SmartQABean smartQABean) {
        if (mData == null) {
            return;
        }
        mData.add(smartQABean);
        refreshQAList();
    }

    public void addAnswer(SmartQABean smartQABean) {
        if (mData == null) {
            return;
        }
        smartQABean.isShowDate = mData.get(mData.size() - 1).isShowDate;
        mData.remove(mData.size() - 1);
        mData.add(smartQABean);
        refreshQAList();
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0)
//            return TYPE_FIRST_HELLO;
//        return TYPE_QA;
//    }

    public void refreshQAList() {
        notifyDataSetChanged();
    }

    private int lastItemPaddingBottom = 0;  //该值设置为-1之后说明recyclerView已经设置了setStackFromEnd。则不再设置最后一个条目的paddingBottom

    public void setPaddingBottomOnLastItem(int paddingBottom) {
        this.lastItemPaddingBottom = paddingBottom;
        LogUtils.e("setPaddingBottomOnLastItem >> " + paddingBottom);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == TYPE_FIRST_HELLO) {
//            return new HelloGuysViewHolder(LayoutInflater.from(context).inflate(R.layout.item_smart_server_hello, parent, false));
//        }
        return new QuestionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_smart_server, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (position == 0) {
//            return;
//        }
        if (holder == null)
            return;
        QuestionViewHolder questionViewHolder = (QuestionViewHolder) holder;
        if (mData.get(position).isShowDate) {
            questionViewHolder.tvDate.setVisibility(View.VISIBLE);
            setDate(questionViewHolder.tvDate, position);
        } else {
            questionViewHolder.tvDate.setVisibility(View.GONE);
        }

        int viewType = mData.get(position).viewType;

        if (viewType == TYPE_SHOW_ALL) {
            questionViewHolder.llAnswer.setVisibility(View.VISIBLE);
            UIUtils.setText(questionViewHolder.tvQuestion, mData.get(position).getQuestion());
            if (TextUtils.isEmpty(mData.get(position).getAnswer())) {
                questionViewHolder.tvAnswer.setText("没有找到您的需求。\n您要找的问题是？");
            } else {
                UIUtils.setText(questionViewHolder.tvAnswer, mData.get(position).getAnswer());
            }
            inflateSuggestQuestions(questionViewHolder.llSuggestQuestion, mData.get(position).getSuggestQuestion());
        } else if (viewType == TYPE_SHOW_QUESTION_ONLY) {       //只显示问题
            questionViewHolder.llAnswer.setVisibility(View.GONE);
            UIUtils.setText(questionViewHolder.tvQuestion, mData.get(position).getQuestion());
        }

        //动态设置paddingBottom.
        if (position == mData.size() - 1 && lastItemPaddingBottom != -1) {
            int padding = DensityUtil.dip2px(10);
            questionViewHolder.llAnswer.setPadding(padding, padding, padding, padding + lastItemPaddingBottom);
        }
    }

    /**
     * 时间显示格式：当天只显示 时：分 隔天显示 月日时分  隔年显示 年月日时分
     *
     * @param textView
     * @param currPos
     */
    private void setDate(TextView textView, int currPos) {
        long currTime = mData.get(currPos).getTime();
        long lastTime;
        if (currPos == 0) {
            lastTime = System.currentTimeMillis();
        } else {
            lastTime = mData.get(currPos - 1).getTime();
        }
        String currDay = StringUtil.transferTimeStampToDate(currTime, "yyyy-MM-dd");       //"yyyy-MM-dd HH:mm:ss"
        String lastDay = StringUtil.transferTimeStampToDate(lastTime, "yyyy-MM-dd");
        LogUtils.e("currDay: " + currDay + " lastDay: " + lastDay);
        String[] currArr = currDay.split("-");
        String[] lastArr = lastDay.split("-");

        StringBuilder pattern = new StringBuilder();
        if (!currArr[0].equals(lastArr[0])) { //年份不等显示年份
            pattern.append("yyyy--");
        }
        if (!currArr[2].equals(lastArr[2])) {
            pattern.append("MM-dd ");
        }
        pattern.append("HH:mm");
        LogUtils.e("pattern: " + pattern);
        String result = StringUtil.transferTimeStampToDate(currTime, pattern.toString());
        UIUtils.setText(textView, result);
    }

    List<SuggestQuestion> questions;

    private void inflateSuggestQuestions(LinearLayout container, String suggestQuestionJson) {
        if (TextUtils.isEmpty(suggestQuestionJson)) {
            container.setVisibility(View.GONE);
            return;
        }
        container.setVisibility(View.VISIBLE);
        if (questions == null) {
            questions = new ArrayList<>();
        }
        Type listType = new TypeToken<List<SuggestQuestion>>() {
        }.getType();
        questions = new Gson().fromJson(suggestQuestionJson, listType);
        container.removeAllViews();
        for (int i = 0; questions != null && i < questions.size(); i++) {
            container.addView(new SmartQAHolder(generateListener()).inflateData(questions.get(i)));
        }
    }

    private SmartQAHolder.OnClickSuggestQuestionListener onClickSuggestQuestionListener;

    public void setOnClicksuggestQuestionListener(SmartQAHolder.OnClickSuggestQuestionListener listener) {
        this.onClickSuggestQuestionListener = listener;
    }

    private SmartQAHolder.OnClickSuggestQuestionListener generateListener() {
        return new SmartQAHolder.OnClickSuggestQuestionListener() {
            @Override
            public void onClickSuggestQuestion(SuggestQuestion suggestQuestion) {
                if (onClickSuggestQuestionListener != null)
                    onClickSuggestQuestionListener.onClickSuggestQuestion(suggestQuestion);
            }
        };
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion;
        TextView tvDate;

        LinearLayout llAnswer;
        TextView tvAnswer;
        LinearLayout llSuggestQuestion;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_item_SmartServer_date);

            tvQuestion = itemView.findViewById(R.id.tv_item_smartServer_question);

            llAnswer = itemView.findViewById(R.id.ll_item_smart_answer);
            tvAnswer = itemView.findViewById(R.id.tv_item_smartServer_answerContent);
            llSuggestQuestion = itemView.findViewById(R.id.ll_item_smartServer_answerContainer);
        }
    }

    class HelloGuysViewHolder extends RecyclerView.ViewHolder {

        public HelloGuysViewHolder(View itemView) {
            super(itemView);
        }
    }
}
