package com.test.bank.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.test.bank.R;
import com.test.bank.adapter.RiskTestResultAdapter;
import com.test.bank.base.BaseUILocalDataActivity;
import com.test.bank.bean.RiskEvaluationQuestionBean;
import com.test.bank.bean.RiskEvaluationResultBean;
import com.test.bank.http.HttpRequest;
import com.test.bank.utils.Aes;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.weight.holder.RiskEvaluationResultHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;


public class RiskTestResultActivity extends BaseUILocalDataActivity {
    @BindView(R.id.tv_riskTestResultActivity_commit)
    TextView commitTextView;
    @BindView(R.id.iv_riskTestResultActivity_store)
    ImageView storeImageView;
    @BindView(R.id.rv_riskTestResultActivity_riskTestQuestion)
    RecyclerView riskTestQuestionRecyclerView;
    @BindView(R.id.ll_riskTestResultActivity_resultContainer)
    LinearLayout resultContainerLinearLayout;
    @BindView(R.id.rl_riskTestResultActivity_questionContainer)
    RelativeLayout questionContainerRelativeLayout;

    private RiskTestResultAdapter riskTestResultAdapter;
    private List<RiskEvaluationQuestionBean.RiskQuestionSubject> mDataList = new ArrayList<RiskEvaluationQuestionBean.RiskQuestionSubject>();
    private RiskEvaluationQuestionBean riskEvaluationQuestionBean = new RiskEvaluationQuestionBean();
    private RiskEvaluationQuestionBean.RiskQuestionSubject riskQuestionSubject = new RiskEvaluationQuestionBean.RiskQuestionSubject();
    public static final int LAST_QUESTION_COUNT = 4;

    private RiskEvaluationResultBean riskEvaluationResultBean = new RiskEvaluationResultBean();

    @OnClick({R.id.tv_riskTestResultActivity_commit})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.tv_riskTestResultActivity_commit:
                submitRiskTestAnswers();
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "评测结果";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_risk_test_result;
    }

    @Override
    protected void initPageData() {
        riskTestResultAdapter = new RiskTestResultAdapter(this);
        riskTestQuestionRecyclerView.setAdapter(riskTestResultAdapter);
        riskTestQuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        questionContainerRelativeLayout.setVisibility(View.VISIBLE);
        resultContainerLinearLayout.setVisibility(View.GONE);
        getFundCompanyInfo();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            riskQuestionSubject = (RiskEvaluationQuestionBean.RiskQuestionSubject) data.getSerializableExtra(RiskTestOptionActivity.KEY_OF_RISK_TEST_QUESTION);

            for (int i = 0; i < riskTestResultAdapter.mDataList.size(); i++) {
                if (i == requestCode) {
                    riskTestResultAdapter.mDataList.remove(i);
                    riskTestResultAdapter.mDataList.add(i, riskQuestionSubject);
                    break;
                }
            }
            riskTestResultAdapter.notifyDataSetChanged();
        }
        LogUtils.printLog(riskQuestionSubject.toString());
    }


    private void getFundCompanyInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", SPUtil.getInstance().getToken());

        HttpRequest.post(this, HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.RISK_TEST_QUESTIONS, params, new HttpRequest.HttpResponseCallBack() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                riskEvaluationQuestionBean = JSON.parseObject(response.body(), RiskEvaluationQuestionBean.class);

                List<RiskEvaluationQuestionBean.RiskQuestionSubject.RiskQuestionItem> riskQuestionItemList = null;
                for (int i = 0; i < riskEvaluationQuestionBean.getSubjects().size(); i++) {
                    riskQuestionItemList = riskEvaluationQuestionBean.getSubjects().get(i).getQitem();
                    for (int j = 0; j < riskQuestionItemList.size(); j++) {
                        riskQuestionItemList.get(j).setSelected(false);

                        //前8道题用itemvalue
                        if (StringUtil.notEmpty(riskQuestionItemList.get(j).getItemvalue())
                                && riskQuestionItemList.get(j).getItemvalue().equals(riskEvaluationQuestionBean.getSubjects().get(i).getMyanswer())) {
                            riskQuestionItemList.get(j).setSelected(true);
                        }
                        //后4道题用itemTitle
                        if (i >= (riskEvaluationQuestionBean.getSubjects().size() - LAST_QUESTION_COUNT)
                                && riskQuestionItemList.get(j).getItemtitle().equals(riskEvaluationQuestionBean.getSubjects().get(i).getMyanswer())) {
                            riskQuestionItemList.get(j).setSelected(true);
                        }
                    }
                }

                riskTestResultAdapter.upateData(true, riskEvaluationQuestionBean.getSubjects());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void submitRiskTestAnswers() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", SPUtil.getInstance().getToken());
        Map<String, String> treeMap = new TreeMap<String, String>();
        for (int i = 0; i < (riskTestResultAdapter.mDataList.size() - LAST_QUESTION_COUNT); i++) {
            if (StringUtil.notEmpty(riskTestResultAdapter.mDataList.get(i).getQno())) {
                treeMap.put(riskTestResultAdapter.mDataList.get(i).getQno(), riskTestResultAdapter.mDataList.get(i).getMyanswer());
            }
        }
        LogUtils.printLog(JSON.toJSONString(treeMap));
        params.put("qnoandanswer", Aes.encryptAES(JSON.toJSONString(treeMap)));//未base64编码前格式如：{"00001":"003","00002":"004"} 其中00001为问卷编号，003为选项value，最终结果需要base64加密

//        if (isFirstEvaulation) {
        RiskEvaluationQuestionBean.RiskQuestionSubject riskQuestionSubject = riskTestResultAdapter.mDataList.get(RiskTestResultAdapter.ADDRESS_QUESTION_POSITION);
        params.put("profession", riskTestResultAdapter.mDataList.get(RiskTestResultAdapter.CAREER_QUESTION_POSITION).getMyanswer()); //职业、汉字、不签名
        params.put("province", riskQuestionSubject.getProvince());   //省份、汉字、不签名
        params.put("city", riskQuestionSubject.getCity());       //城市、汉字、不签名
//        params.put("address", riskQuestionSubject.getAddress());    //地址、汉字、做签名
        params.put("address", "北京市北京市附近街道");    //地址、汉字、做签名
//        params.put("postcode", riskQuestionSubject.getPostcode());   //邮编(必须6位数字)、汉字、做签名
        params.put("postcode", "000000");   //邮编(必须6位数字)、汉字、做签名
        if ("是".equals(riskTestResultAdapter.mDataList.get(RiskTestResultAdapter.BENIFIT_QUESTION_POSITION).getMyanswer())) {
            params.put("beneficiary", "0");//受益人（0,1）不做签名
        } else {
            params.put("beneficiary", "1");//受益人（0,1）不做签名
        }
        if ("是".equals(riskTestResultAdapter.mDataList.get(RiskTestResultAdapter.TRUST_QUESTION_POSITION).getMyanswer())) {
            params.put("hasbadrecord", "0");//诚信记录（0,1）不做签名
        } else {
            params.put("hasbadrecord", "1");//诚信记录（0,1）不做签名
        }
//        }
        params.put("isYes", false + "");      //true 做过风险测评后四道题false未做过风险测评后四道题

        HttpRequest.post(this, HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.SUBMIT_RISK_TEST_ANSWERS, params, new HttpRequest.HttpResponseCallBack() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                riskEvaluationResultBean = JSON.parseObject(response.body(), RiskEvaluationResultBean.class);

                questionContainerRelativeLayout.setVisibility(View.GONE);
                resultContainerLinearLayout.removeAllViews();
                resultContainerLinearLayout.setVisibility(View.VISIBLE);
                resultContainerLinearLayout.addView(new RiskEvaluationResultHolder().inflateData(riskEvaluationResultBean.getCustrisk()));
                baseTitleTextView.setText("测评结果");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public static void open(Context context){
        Intent intent = new Intent(context, RiskTestResultActivity.class) ;
        context.startActivity(intent);
    }

}
