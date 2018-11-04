package com.test.bank.view.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.adapter.RiskTestOptionAdapter;
import com.test.bank.base.BaseUILocalDataActivity;
import com.test.bank.bean.RiskEvaluationQuestionBean;
import com.test.bank.weight.holder.RiskEvaluationAddressHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class RiskTestOptionActivity extends BaseUILocalDataActivity {
    @BindView(R.id.tv_riskTestOptionActivity_confirmModify)
    TextView confirmModifyTextView;
    @BindView(R.id.iv_riskTestOptionActivity_title)
    TextView titleTextView;
    @BindView(R.id.rv_riskTestOptionActivity_riskTestQuestion)
    RecyclerView riskTestQuestionRecyclerView;
    @BindView(R.id.ll_riskTestOptionActivity_questionContainer)
    LinearLayout llQuestionContainer;
    @BindView(R.id.v_riskTestOptionActivity_singleOptionDivider)
    View singleOptionDividerView;
    @BindView(R.id.v_riskTestOptionActivity_titleDivider)
    View titleDividerView;

    private RiskTestOptionAdapter riskTestOptionAdapter;
    private List<RiskEvaluationQuestionBean.RiskQuestionSubject.RiskQuestionItem> mDataList = new ArrayList<RiskEvaluationQuestionBean.RiskQuestionSubject.RiskQuestionItem>();

    public static final String KEY_OF_RISK_TEST_QUESTION = "riskTestQuestionKey";
    private RiskEvaluationQuestionBean.RiskQuestionSubject riskQuestionSubject = new RiskEvaluationQuestionBean.RiskQuestionSubject();
    public static final String KEY_OF_QUESTION_NUMBER = "questionNumberKey";
    private int questionNumber;
    public static final String KEY_OF_QUESTION_TYPE = "questionTypeKey";
    private int currentQuestionType;
    public static final int ADDRESS_QUESTION = 5001;
    public static final int SINGLE_QUESTION = 5002;

    private RiskEvaluationAddressHolder riskEvaluationAddressHolder;

    @OnClick({R.id.tv_riskTestOptionActivity_confirmModify})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.tv_riskTestOptionActivity_confirmModify:
                backToRiskTestResult();
                break;
        }
    }

    public void backToRiskTestResult() {
        Intent intent = new Intent();
        intent.putExtra(KEY_OF_RISK_TEST_QUESTION, riskQuestionSubject);
        setResult(0, intent);
        this.finish();
    }

    @Override
    protected String getPageTitle() {
        return "风险测评";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_risk_test_option;
    }

    @Override
    protected void initPageData() {
        riskQuestionSubject = (RiskEvaluationQuestionBean.RiskQuestionSubject) getIntent().getSerializableExtra(KEY_OF_RISK_TEST_QUESTION);
        questionNumber = getIntent().getIntExtra(KEY_OF_QUESTION_NUMBER, -1);
        currentQuestionType = getIntent().getIntExtra(KEY_OF_QUESTION_TYPE, SINGLE_QUESTION);
        RiskEvaluationAddressHolder.testForOpenAccount = true;
        riskEvaluationAddressHolder = new RiskEvaluationAddressHolder();

        switch (currentQuestionType) {
            case SINGLE_QUESTION:
                transferContentView(true);
                break;
            case ADDRESS_QUESTION:
                transferContentView(false);
                confirmModifyTextView.setVisibility(View.GONE);
                singleOptionDividerView.setVisibility(View.GONE);
                titleDividerView.setVisibility(View.GONE);
                break;
        }
        riskEvaluationAddressHolder.setOnClickLastOrNextItemListener(new RiskEvaluationAddressHolder.OnClickLastOrNextItemListener() {
            @Override
            public void onClickLastOrNextItem(boolean isLastItem, String province, String city, String street) {
                riskQuestionSubject.setProvince(province);
                riskQuestionSubject.setCity(city);
                riskQuestionSubject.setAddress(street);
                backToRiskTestResult();
            }
        });

        riskTestOptionAdapter = new RiskTestOptionAdapter(this, riskQuestionSubject);
        riskTestQuestionRecyclerView.setAdapter(riskTestOptionAdapter);
        riskTestQuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        titleTextView.setText((questionNumber + 1) + "、" + riskQuestionSubject.getQtitle());

    }


    /**
     * 普通的单选视图与地址选择的视图的切换
     *
     * @param isTransferToSingleChoseQuestionView
     */
    private void transferContentView(boolean isTransferToSingleChoseQuestionView) {
        if (isTransferToSingleChoseQuestionView) {
            llQuestionContainer.removeAllViews();
            llQuestionContainer.addView(riskTestQuestionRecyclerView);
        } else {
            llQuestionContainer.removeAllViews();
            llQuestionContainer.addView(riskEvaluationAddressHolder.inflateData(
                    new String[]{riskQuestionSubject.getProvince(), riskQuestionSubject.getCity(), riskQuestionSubject.getAddress()}));

//            llQuestionContainer.addView(new RiskEvaluationAddressHolder().inflateData(
//                    new String[]{"111", "222", "aaaaaaaaaaaaaa"}));
        }
    }

}
