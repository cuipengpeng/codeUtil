package com.jfbank.qualitymarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jfbank.qualitymarket.ActivitysManage;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.LogisticsDetailseAdapter;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.dao.LogisticsDetailseBean;
import com.jfbank.qualitymarket.dao.LogisticsDetailseBean.DataBean;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.fragment.MyOrderFragment;
import com.jfbank.qualitymarket.fragment.PopDialogFragment;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import org.apache.http.Header;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 物流详情页面
 *
 * @author 彭爱军
 * @date 2016年8月25日
 */
public class LogisticsDetailsActivity extends BaseActivity {
    public static final String TAG = LogisticsDetailsActivity.class.getName();
    RelativeLayout rlTitle;
    /**
     * 显示标题内容
     */
    TextView tvTitle;
    /**
     * 返回
     */
    ImageView ivBack;
    /**
     * 订单编号
     */
    private TextView mLogistics_tv_indent_code;

    //物流信息
    @InjectView(R.id.ll_logisticsDetailActivity_logisticsCompany)
    LinearLayout logisticsCompanyLinearLayout;
    @InjectView(R.id.tv_logisticsDetailActivity_logisticsCompany)
    TextView logisticsCompanyTextView;
    //快递单号
    @InjectView(R.id.ll_logisticsDetailActivity_deliveryNumber)
    LinearLayout deliveryNumberLinearLayout;
    @InjectView(R.id.tv_logisticsDetailActivity_deliveryNumber)
    TextView deliveryNumberTextView;
    //发货备注
    @InjectView(R.id.ll_logisticsDetailActivity_sendRemark)
    LinearLayout sendRemarkLinearLayout;
    @InjectView(R.id.tv_logisticsDetailActivity_sendRemark)
    TextView sendRemarkTextView;

    /**
     * listview
     */
    private ListView mLogistics_details_listview;
    /**
     * 网编请求时加载框
     */
    private LoadingAlertDialog mDialog;
    private LogisticsDetailseAdapter mAdapter;
    private List<LogisticsDetailseBean.DataBean> mData;
    /**
     * 京东订单id
     */
    private String jdOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_logistics_details);
        ButterKnife.inject(this);

        ActivitysManage.getActivitysManager().addActivity(this);
        bindViews();
    }

    /**
     * 网络请求查询物流
     */
    private void requestQualityordertrack() {
        if (null == mDialog) {
            mDialog = new LoadingAlertDialog(this);
        }
        mDialog.show("正在加载中...");

        Map<String,String> params = new HashMap<>();

        params.put("ver", AppContext.getAppVersionName(this));
        params.put("Plat", ConstantsUtil.PLAT);

        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());

        params.put("orderId", jdOrderId);


        Log.e("TAG", params.toString());

        HttpRequest.qualityordertrack(mContext,params, new AsyncResponseCallBack() {

            @Override
            public void onResult(String arg2) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (null != arg2 && arg2.length()> 0) {
                    Log.e("TAG", new String(arg2));
                    try {
                        LogisticsDetailseBean bean = JSON.parseObject(new String(arg2), LogisticsDetailseBean.class);
                        if (StringUtil.isNull(bean.getExpress())) {
                            logisticsCompanyLinearLayout.setVisibility(View.GONE);
                        } else {
                            logisticsCompanyLinearLayout.setVisibility(View.VISIBLE);
                        }

                        if (StringUtil.isNull(bean.getLogisticCode())) {
                            deliveryNumberLinearLayout.setVisibility(View.GONE);
                        } else {
                            deliveryNumberLinearLayout.setVisibility(View.VISIBLE);
                        }

                        if (StringUtil.notEmpty(bean.getLogisticCode()) && StringUtil.notEmpty(bean.getExpress())) {
                            sendRemarkLinearLayout.setVisibility(View.VISIBLE);
                        } else {
                            sendRemarkLinearLayout.setVisibility(View.GONE);
                        }
                        logisticsCompanyTextView.setText(bean.getExpress());
                        deliveryNumberTextView.setText(bean.getLogisticCode());
                        sendRemarkTextView.setText(bean.getSendRemark());

                        if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
                            setData(bean.getData());
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
                                UserUtils.tokenFailDialog(mContext, bean.getStatusDetail(), null);
                        } else {
                            Toast.makeText(LogisticsDetailsActivity.this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        LogUtil.printLog("物流数据出错" + e.getMessage());
                    }
                }

            }

            @Override
            public void onFailed(String path, String msg) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                Toast.makeText(LogisticsDetailsActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * token失效时弹出对话框
     *
     * @param content
     */
    private void showDialog(String content) {
        final PopDialogFragment dialog = PopDialogFragment.newDialog(false, false, null, content, null, null, "确定");
        dialog.setOnClickListen(new PopDialogFragment.OnClickListen() {

            @Override
            public void rightClick() {
                new StoreService(LogisticsDetailsActivity.this).clearUserInfo();
                Intent loginIntent = new Intent(getApplication(), LoginActivity.class);
                loginIntent.putExtra(LoginActivity.KEY_OF_COME_FROM, LoginActivity.TOKEN_FAIL_TAG);
                startActivity(loginIntent);
                dialog.dismiss();
            }

            @Override
            public void leftClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "TAG");
    }

    /**
     * 设置数据
     *
     * @param data
     */
    private void setData(List<DataBean> data) {
        if (null == data || data.size() < 1) {
            return;
        }
        mData = data;
        mAdapter = new LogisticsDetailseAdapter<LogisticsDetailseBean.DataBean>(this, mData);
        mLogistics_details_listview.setAdapter(mAdapter);
    }


    /**
     * 初始化View以及设置监听
     */
    private void bindViews() {
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        CommonUtils.setTitle(this, rlTitle);

        tvTitle.setText(R.string.str_pagenanme_loginsticsdetail);

        mLogistics_tv_indent_code = (TextView) findViewById(R.id.logistics_tv_indent_code);
        mLogistics_details_listview = (ListView) findViewById(R.id.logistics_details_listview);

        Intent intent = getIntent();
        if (null != intent) {
            jdOrderId = intent.getStringExtra(MyOrderFragment.KEY_OF_ORDER_ID);
            mLogistics_tv_indent_code.setText(jdOrderId);
            requestQualityordertrack();
        }
        ivBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagenanme_loginsticsdetail);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivitysManage.getActivitysManager().finishActivity(this);
    }
}
