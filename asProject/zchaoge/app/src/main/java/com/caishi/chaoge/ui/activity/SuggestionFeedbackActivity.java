package com.caishi.chaoge.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;


public class SuggestionFeedbackActivity extends BaseUILocalDataActivity {
    @BindView(R.id.et_suggestionFeedbackActivity_suggestion)
    EditText etSuggestionFeedbackActivitySuggestion;
    @BindView(R.id.tv_suggestionFeedbackActivity_textCount)
    TextView tvSuggestionFeedbackActivityTextCount;
    @BindView(R.id.et_suggestionFeedbackActivity_contactWay)
    EditText etSuggestionFeedbackActivityContactWay;
    @BindView(R.id.tv_suggestionFeedbackActivity_customerServicePhone)
    TextView tvSuggestionFeedbackActivityCustomerServicePhone;

    @OnClick({R.id.tv_suggestionFeedbackActivity_customerServicePhone})
    public void OnClickView(View v) {
        switch (v.getId()) {
            case R.id.tv_suggestionFeedbackActivity_customerServicePhone:
                break;

        }
    }

    @Override
    protected String getPageTitle() {
        return "意见反馈";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_suggestion_feedback;
    }

    @Override
    protected void initPageData() {
        etSuggestionFeedbackActivitySuggestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.length() + "/400";
                tvSuggestionFeedbackActivityTextCount.setText(str);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etSuggestionFeedbackActivityContactWay.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
    }

    protected void setRightMenu() {
        baseRightMenuTextView.setVisibility(View.VISIBLE);
        baseRightMenuTextView.setText("保存");
        baseRightMenuTextView.setTextColor(Color.parseColor("#fe5175"));
        baseRightMenuTextView.setTextSize(14);
        baseRightMenuTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = etSuggestionFeedbackActivitySuggestion.getText().toString().trim();
                String contactWay = etSuggestionFeedbackActivityContactWay.getText().toString() + "";
                if (content.length() <= 0) {
                    Toast.makeText(SuggestionFeedbackActivity.this, "请输入您的意见或问题", Toast.LENGTH_SHORT).show();
                    return;
                }
                postUserFeedback(content, contactWay);
            }
        });
    }

    private void postUserFeedback(String content, String contactWay) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("content", content);
        paramsMap.put("contact", contactWay);

        HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.POST_USER_FEEDBACK, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(SuggestionFeedbackActivity.this, "您的反馈已提交", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String t) {
            }
        });
    }

    public static void open(Context context) {
        context.startActivity(new Intent(context, SuggestionFeedbackActivity.class));
    }
}
