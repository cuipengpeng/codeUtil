package com.test.bank.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.test.bank.R;
import com.test.bank.adapter.RiskQuestionAdapter;
import com.test.bank.base.BaseActivity;
import com.test.bank.base.BaseBean;
import com.test.bank.bean.RiskEvaluationQuestionBean;
import com.test.bank.bean.RiskEvaluationResultBean;
import com.test.bank.bean.UserInfo;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.utils.Aes;
import com.test.bank.utils.DensityUtil;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.ToastUtils;
import com.test.bank.weight.CommonTitleBar;
import com.test.bank.weight.dialog.CommonDialogFragment;
import com.test.bank.weight.holder.RiskEvaluationAddressHolder;
import com.test.bank.weight.holder.RiskEvaluationResultHolder;
import com.test.bank.weight.holder.RiskNoEvaluationHolder;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class RiskPreferenceActivity extends BaseActivity {
    private static final String TAG = "RiskPreferenceActivity";
    @BindView(R.id.commonTitleBar_riskPreference)
    CommonTitleBar commonTitleBar;

    @BindView(R.id.ll_riskResult_container)
    LinearLayout llResultContainer;

    @BindView(R.id.progress_risk)
    View progressView;
    @BindView(R.id.tv_riskQuestion)
    TextView tvQuestion;
    @BindView(R.id.tv_riskPreference_lastQuestion)
    TextView tvLastQuestion;

    @BindView(R.id.ll_riskEvaluation_questionContainer)
    LinearLayout llQuestionContainer;
    @BindView(R.id.recyclerView_riskQuestion)
    RecyclerView recyclerView;

    @BindView(R.id.ll_risk_agree)
    LinearLayout llAgree;
    @BindView(R.id.iv_riskAgreeCheck)
    ImageView ivAgree;
    @BindView(R.id.tv_riskCheckResult)
    TextView tvCheckResult;
    @BindView(R.id.rl_riskQuestion)
    RelativeLayout rlQuestions;

    RiskQuestionAdapter adapter;

    private RiskEvaluationQuestionBean bean = new RiskEvaluationQuestionBean();

    private RiskEvaluationQuestionBean.RiskQuestionSubject currQuestion;
    private List<RiskEvaluationQuestionBean.RiskQuestionSubject.RiskQuestionItem> questionList;
    private int currQuestionIndex = 0;

    private Map<String, String> answerMap = new HashMap<>();


    String riskLevel = "";
    private boolean isFirstEvaulation = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_risk_preference;
    }

    @Override
    protected void init() {
        if (getIntent() != null) {
            riskLevel = getIntent().getStringExtra(PARAM_RISK_LEVEL);
        }
//        riskLevel = "";
        initData();
        initListener();
        progressView.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(riskLevel)) {
            commonTitleBar.setPrimaryTitle("测评结果");
            isFirstEvaulation = false;
            llResultContainer.addView(new RiskEvaluationResultHolder().inflateData(riskLevel));
        } else {
            isFirstEvaulation = true;
            commonTitleBar.setPrimaryTitle("风险测评");
            commonTitleBar.setRightText("");
            llResultContainer.addView(new RiskNoEvaluationHolder(new RiskNoEvaluationHolder.OnClickRetestRiskListener() {
                @Override
                public void onClickRetestRisk() {   //开始风险测评
                    reInitTestStatus();
                    llResultContainer.setVisibility(View.GONE);
                    progressView.setVisibility(View.VISIBLE);
                    if (!isReuqestedQuestionData)
                        requestQuestionData();
                }
            }).inflateData(""));
        }
    }

    private void initData() {
        if (currQuestion == null) {
            currQuestion = new RiskEvaluationQuestionBean.RiskQuestionSubject();
        }
        if (questionList == null) {
            questionList = new ArrayList<>();
        }
        if (adapter == null) {
            adapter = new RiskQuestionAdapter(this, questionList);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        generateRiskAddressHolder();
    }

    private void initListener() {
        commonTitleBar.setOnRightClickListener(new CommonTitleBar.onRightClickListener() {
            @Override
            public void onClickRight() {        //重测
                MobclickAgent.onEvent(RiskPreferenceActivity.this, "click_btn_riskPreferenceActivity_reTest");
                commonTitleBar.setPrimaryTitle("风险测评");
                progressView.setVisibility(View.VISIBLE);
                transferContentView(true);
                if (!isReuqestedQuestionData) {     //是否是第一次进入并且之前已经测评过的情况下点击重测【尽可能延迟请求风险测评接口数据】
                    llResultContainer.setVisibility(View.GONE);
                    requestQuestionData();
                } else {
                    progressView.setVisibility(View.VISIBLE);
                    llResultContainer.setVisibility(View.GONE);
                    rlQuestions.setVisibility(View.VISIBLE);
                    llAgree.setVisibility(View.GONE);
                    tvCheckResult.setVisibility(View.GONE);
                    reInitTestStatus();
                }
            }
        });

        adapter.setOnSelectedLisener(new RiskQuestionAdapter.OnSelectedLisener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSelect(String selectedContent) {
                llAgree.setVisibility(currQuestionIndex == bean.getSubjects().size() - 1 ? View.VISIBLE : View.GONE);
                tvCheckResult.setVisibility(currQuestionIndex == bean.getSubjects().size() - 1 ? View.VISIBLE : View.GONE);
                if (currQuestionIndex < 8) {
                    lastChoose = selectedContent;
//                    if (!answerMap.containsKey(currQuestion.getQno())) {      //加入该条件判断会造成无法重新选择
                    Log.e(TAG, "onSelect: put: " + currQuestion.getQno() + ":" + selectedContent);
                    answerMap.put(currQuestion.getQno(), selectedContent);
//                    }
                } else if (currQuestionIndex == 8) {    //职业
                    selectedProfession = selectedContent;
                } else if (currQuestionIndex == 9) {    //省份城市

                } else if (currQuestionIndex == 10) {   // 受益人
                    selectedBeneficiary = selectedContent;
                } else if (currQuestionIndex == 11) {   //诚信记录
                    hasBadRecord = selectedContent;
                }
                if (currQuestionIndex + 1 < bean.getSubjects().size()) {
                    currQuestionIndex++;
                    if (bean.getSubjects().size() == 8 && currQuestionIndex == bean.getSubjects().size() - 1) {   //当点击上一题之后有重新回到最后一题的时候，默认下边的条款和提交按钮是显示的
                        if (answerMap.containsKey(bean.getSubjects().get(bean.getSubjects().size() - 1).getQno())) {
                            llAgree.setVisibility(View.VISIBLE);
                            tvCheckResult.setVisibility(View.VISIBLE);
                        }
                    } else if (bean.getSubjects().size() == 12 && currQuestionIndex == bean.getSubjects().size() - 1) {
                        if (!TextUtils.isEmpty(hasBadRecord)) {
                            llAgree.setVisibility(View.VISIBLE);
                            tvCheckResult.setVisibility(View.VISIBLE);
                        }
                    }
                    if (currQuestionIndex == 0) {   //重测按钮从第二题开始显示，第一题不显示
                        commonTitleBar.setRightText("");
                    } else {
                        commonTitleBar.setRightText("重测");
                    }
                    refreshData();
                }
            }
        });

        commonTitleBar.setOnLeftClickListener(new CommonTitleBar.OnLeftClickListener() {
            @Override
            public void onClickLeft() {
                if (llQuestionContainer.getVisibility() == View.VISIBLE && llResultContainer.getVisibility() != View.VISIBLE) {
                    showCommonDialog("未完成风险测评将影响基金购买，确定要放弃测评吗？", "取消", "确定", null, new CommonDialogFragment.OnRightClickListener() {
                        @Override
                        public void onClickRight() {
//                            finish();
                            reInitTestStatus();
                            transferContentView(true);
                            llAgree.setVisibility(currQuestionIndex == bean.getSubjects().size() - 1 ? View.VISIBLE : View.GONE);
                            tvCheckResult.setVisibility(currQuestionIndex == bean.getSubjects().size() - 1 ? View.VISIBLE : View.GONE);
                            llResultContainer.setVisibility(View.VISIBLE);
                            llResultContainer.removeAllViews();
                            progressView.setVisibility(View.GONE);
                            if (isFirstEvaulation) {  //第一次测评 ，返回风险测评
                                commonTitleBar.setPrimaryTitle("风险测评");
                                llResultContainer.addView(new RiskNoEvaluationHolder(new RiskNoEvaluationHolder.OnClickRetestRiskListener() {
                                    @Override
                                    public void onClickRetestRisk() {   //开始风险测评
                                        llResultContainer.setVisibility(View.GONE);
                                        progressView.setVisibility(View.VISIBLE);
                                        if (!isReuqestedQuestionData)
                                            requestQuestionData();
                                    }
                                }).inflateData(""));
                            } else {        //返回测评结果
                                commonTitleBar.setPrimaryTitle("测评结果");
                                llResultContainer.addView(new RiskEvaluationResultHolder().inflateData(riskLevel));
                                commonTitleBar.setRightText("重测");
                            }

                        }
                    });
                } else {
                    finish();
                }
            }
        });
    }

    /**
     * 重测之后已经将lastChoose置空，但是bean中的数据集合中已经保留了selected=true的标记，所以还是会记住上次选择的内容
     */
    private void resetAllItemSelectedStatus() {
        if (bean == null || bean.getSubjects() == null) {
            return;
        }
        for (int i = 0; i < bean.getSubjects().size(); i++) {
            for (int j = 0; j < bean.getSubjects().get(i).getQitem().size(); j++) {
                bean.getSubjects().get(i).getQitem().get(j).setSelected(false);
            }
        }
    }

    private void reInitTestStatus() {
        if (answerMap.size() > 0) {
            answerMap.clear();
        }
        resetAllItemSelectedStatus();
        lastChoose = "";
        selectedProfession = "";
        selectedProvince = "";
        selectedCity = "";
        inputStreet = "";
        selectedBeneficiary = "";
        hasBadRecord = "";
        ivAgree.setSelected(false);
        tvCheckResult.setEnabled(false);
        currQuestionIndex = 0;
        refreshData();
    }


    private void refreshData() {
        if (bean == null || bean.getSubjects() == null) {
            return;
        }
        if (currQuestionIndex == 0) {
            commonTitleBar.setRightText("");
        } else {
            commonTitleBar.setRightText("重测");
        }
        currQuestion = bean.getSubjects().get(currQuestionIndex);
        tvQuestion.setText((currQuestionIndex + 1) + "、" + currQuestion.getQtitle());
        refreshProgress();
        tvLastQuestion.setVisibility(currQuestionIndex == 0 ? View.GONE : View.VISIBLE);    //上一题显示隐藏
        if (currQuestionIndex < bean.getSubjects().size()) {
            if (currQuestionIndex < 8) {
                if (answerMap.containsKey(currQuestion.getQno())) {
                    lastChoose = answerMap.get(currQuestion.getQno());
                } else {
                    lastChoose = "";
                }
                inflateQuestionData();
            } else {
                if (currQuestionIndex == 9) {
                    transferContentView(false);
                } else {
                    transferContentView(true);
                    for (int i = 0; i < currQuestion.getQitem().size(); i++) {
                        currQuestion.getQitem().get(i).setItemvalue(currQuestion.getQitem().get(i).getItemtitle());
                    }
                    if (currQuestionIndex == 8) {
                        lastChoose = selectedProfession;
                    } else if (currQuestionIndex == 10) {
                        lastChoose = selectedBeneficiary;
                    } else if (currQuestionIndex == 11) {
                        lastChoose = hasBadRecord;
                    }
                    inflateQuestionData();
                }
            }
        }
    }

    private String lastChoose;

    private void inflateQuestionData() {
        adapter.refreshData(currQuestion.getQitem(), lastChoose);
    }


    private String selectedProvince = "";
    private String selectedCity = "";
    private String inputStreet;

    private String selectedProfession;

    RiskEvaluationAddressHolder riskEvaluationAddressHolder;

    private void generateRiskAddressHolder() {
        riskEvaluationAddressHolder = new RiskEvaluationAddressHolder();
        riskEvaluationAddressHolder.setOnClickLastOrNextItemListener(new RiskEvaluationAddressHolder.OnClickLastOrNextItemListener() {
            @Override
            public void onClickLastOrNextItem(boolean isLastItem, String province, String city, String street) {
                LogUtils.e("onClickLastOrNextItem --> " + isLastItem + " ,," + province + "," + city + "," + street);
                selectedProvince = province;
                selectedCity = city;
                inputStreet = street;
                if (isLastItem) {
                    currQuestionIndex--;
                } else {
                    currQuestionIndex++;
                }
                refreshData();
            }
        });
    }

    @Override
    protected void doBusiness() {
//        requestQuestionData();
    }

    private boolean isReuqestedQuestionData = false;

    private void requestQuestionData() {
        isReuqestedQuestionData = true;
        postRequest(new OnResponseListener<RiskEvaluationQuestionBean>() {
            @Override
            public Observable<BaseBean<RiskEvaluationQuestionBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.putLast("token", SPUtil.getInstance().getToken());
                return NetService.getNetService().queryRiskQuestion(paramMap);
            }

            @Override
            public void onResponse(BaseBean<RiskEvaluationQuestionBean> result) {
                if (result.isSuccess() && result.getData() != null && !result.getData().getSubjects().isEmpty()) {
                    if (bean.getSubjects() == null) {
                        bean.setSubjects(new ArrayList<RiskEvaluationQuestionBean.RiskQuestionSubject>());
                    }
                    if (!isFirstEvaulation) {
                        bean.getSubjects().addAll(result.getData().getSubjects().subList(0, 8));
                    } else {
                        bean.getSubjects().addAll(result.getData().getSubjects());
                    }
                    initProgress(bean.getSubjects().size());
                    refreshData();
                }
            }

            @Override
            public void onError(String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }


    private int screenWith = DensityUtil.getScreenWidth();
    private int dx;

    private void initProgress(int total) {
        dx = screenWith / total;
//        refreshProgress();
    }

    private void refreshProgress() {
        int pro = (currQuestionIndex + 1) * dx;
        ViewGroup.LayoutParams layoutParams = progressView.getLayoutParams();
        layoutParams.width = pro;
        progressView.setLayoutParams(layoutParams);
    }

    /**
     * 普通的单选视图与地址选择的视图的切换
     *
     * @param isTransferToSingleChoseQuestionView
     */
    private void transferContentView(boolean isTransferToSingleChoseQuestionView) {
        if (isTransferToSingleChoseQuestionView) {
            llQuestionContainer.removeAllViews();
            llQuestionContainer.addView(recyclerView);
        } else {
            tvLastQuestion.setVisibility(View.GONE);
            llQuestionContainer.removeAllViews();
            llQuestionContainer.addView(riskEvaluationAddressHolder.inflateData(new String[]{selectedProvince, selectedCity, inputStreet}));
//            llQuestionContainer.addView(tvLastQuestion);
        }
    }

    @OnClick({R.id.tv_riskPreference_lastQuestion, R.id.ll_risk_agree, R.id.iv_riskAgreeCheck, R.id.tv_riskCheckResult})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_riskPreference_lastQuestion:
                if (currQuestionIndex > 0) {
                    if (currQuestionIndex == 10) {
                        transferContentView(false);
                    } else {
                        if (!(llQuestionContainer.getChildAt(0) instanceof RecyclerView)) {
                            transferContentView(true);
                        }
                    }
                    currQuestionIndex--;
                    if (currQuestionIndex == 0) {   //重测按钮从第二题开始显示，第一题不显示
                        commonTitleBar.setRightText("");
                    } else {
                        commonTitleBar.setRightText("重测");
                    }
                    llAgree.setVisibility(currQuestionIndex == bean.getSubjects().size() - 1 ? View.VISIBLE : View.GONE);
                    tvCheckResult.setVisibility(currQuestionIndex == bean.getSubjects().size() - 1 ? View.VISIBLE : View.GONE);
                    refreshData();
                }
                break;
            case R.id.ll_risk_agree:
            case R.id.iv_riskAgreeCheck:
                ivAgree.setSelected(!ivAgree.isSelected());
                tvCheckResult.setEnabled(ivAgree.isSelected());
                break;
            case R.id.tv_riskCheckResult:
                submitAnswers();
                break;
        }
    }

    private String postCode = "000000";
    private String selectedBeneficiary = "";
    private String hasBadRecord = "";

    private void submitAnswers() {
        postRequest(new OnResponseListener<RiskEvaluationResultBean>() {
            @Override
            public Observable<BaseBean<RiskEvaluationResultBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.put("qnoandanswer", Aes.encryptAES(JSON.toJSONString(answerMap)));//未base64编码前格式如：{"00001":"003","00002":"004"} 其中00001为问卷编号，003为选项value，最终结果需要base64加密
                if (isFirstEvaulation) {
                    paramMap.put("profession", selectedProfession); //职业、汉字、不签名
                    paramMap.put("province", selectedProvince);   //省份、汉字、不签名
                    paramMap.put("city", selectedCity);       //城市、汉字、不签名
                    paramMap.put("address", TextUtils.isEmpty(inputStreet) ? "北京市北京市附近街道" : inputStreet);    //地址、汉字、做签名
                    paramMap.put("postcode", postCode);   //邮编(必须6位数字)、汉字、做签名
                    paramMap.put("beneficiary", selectedBeneficiary.equals("是") ? "0" : "1");//受益人（0,1）不做签名
                    paramMap.put("hasbadrecord", hasBadRecord.equals("是") ? "0" : "1");//诚信记录（0,1）不做签名
                }
                paramMap.putLast("isYes", isFirstEvaulation ? false + "" : true + "");      //true 做过风险测评后四道题false未做过风险测评后四道题
                return NetService.getNetService().submitRiskAnswers(paramMap);
            }

            @Override
            public void onResponse(BaseBean<RiskEvaluationResultBean> result) {
                if (result.isSuccess() && result.getData() != null) {
                    riskLevel = result.getData().getCustrisk();
                    UserInfo userInfo = SPUtil.getInstance().getUserInfo();
                    userInfo.setRiskLevel(riskLevel);
                    SPUtil.getInstance().putUserInfo(userInfo);
                    rlQuestions.setVisibility(View.GONE);
                    llResultContainer.setVisibility(View.VISIBLE);
                    llResultContainer.removeAllViews();
                    llResultContainer.addView(new RiskEvaluationResultHolder().inflateData(riskLevel));
                    commonTitleBar.setPrimaryTitle("测评结果");
                    commonTitleBar.setRightText("重测");
                    progressView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (llQuestionContainer.getVisibility() == View.VISIBLE && llResultContainer.getVisibility() != View.VISIBLE) {
                showCommonDialog("未完成风险测评将影响基金购买，确定要放弃测评吗？", "取消", "确定", null, new CommonDialogFragment.OnRightClickListener() {
                    @Override
                    public void onClickRight() {
//                        finish();
                        riskEvaluationAddressHolder.hidePickerView();
                        reInitTestStatus();
                        transferContentView(true);
                        llAgree.setVisibility(currQuestionIndex == bean.getSubjects().size() - 1 ? View.VISIBLE : View.GONE);
                        tvCheckResult.setVisibility(currQuestionIndex == bean.getSubjects().size() - 1 ? View.VISIBLE : View.GONE);
                        progressView.setVisibility(View.GONE);
                        llResultContainer.setVisibility(View.VISIBLE);
                        llResultContainer.removeAllViews();
                        if (isFirstEvaulation) {  //第一次测评 ，返回风险测评
                            commonTitleBar.setPrimaryTitle("风险测评");
                            commonTitleBar.setRightText("");
                            llResultContainer.addView(new RiskNoEvaluationHolder(new RiskNoEvaluationHolder.OnClickRetestRiskListener() {
                                @Override
                                public void onClickRetestRisk() {   //开始风险测评
                                    llResultContainer.setVisibility(View.GONE);
                                    progressView.setVisibility(View.VISIBLE);
                                    if (!isReuqestedQuestionData)
                                        requestQuestionData();
                                }
                            }).inflateData(""));
                        } else {        //返回测评结果
                            commonTitleBar.setPrimaryTitle("测评结果");
                            commonTitleBar.setRightText("重测");
                            llResultContainer.addView(new RiskEvaluationResultHolder().inflateData(riskLevel));
                        }
                    }
                });
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static String PARAM_RISK_LEVEL = "riskLevel";

    public static void open(Context context, String riskLevel) {
        Intent intent = new Intent(context, RiskPreferenceActivity.class);
        intent.putExtra(PARAM_RISK_LEVEL, riskLevel);
        context.startActivity(intent);
    }
}
