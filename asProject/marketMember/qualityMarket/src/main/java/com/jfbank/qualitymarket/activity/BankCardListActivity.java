package com.jfbank.qualitymarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.model.BankCard;
import com.jfbank.qualitymarket.model.BankCardListBean;
import com.jfbank.qualitymarket.model.HomePageBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.Response;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.widget.MyPopupDialog;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 银行卡列表页面
 *
 * @author 崔朋朋
 */

public class BankCardListActivity extends BaseActivity {
    public static final String TAG = BankCardListActivity.class.getName();
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.btn_bankCardListActivity_addBankCard)
    Button addBankCardButton;
    @InjectView(R.id.iv_bankCardListActivity_noData)
    ImageView noDataImageView;
    @InjectView(R.id.lv_bankCardListActivity_productList)
    ListView plv_bankcard;
    private List<BankCard> bankCardList = new ArrayList<BankCard>();
    private MyOrderAdapter bankCardAdapter;
    public static final String KEY_OF_BANK_CARD_COME_FROM = "bankCardComeFromKey";
    public static final String BANK_CARD_CHANNEL_WAN_KA = "wanka";

    /**
     * 网编请求时加载框
     */
    private LoadingAlertDialog mDialog;

    @OnClick({R.id.iv_back, R.id.btn_bankCardListActivity_addBankCard})
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_bankCardListActivity_addBankCard:
                if (TextUtils.equals(addBankCardButton.getText().toString().trim(), "管理")) {
                    String content = "请选择要进行的银行卡操作";
                    final MyPopupDialog dialog = new MyPopupDialog(this, null, content, "取消", "更换", null, true);
                    dialog.setOnClickListen(new MyPopupDialog.OnClickListen() {
                        @Override
                        public void leftClick() {
                            dialog.dismiss();
                        }

                        @Override
                        public void rightClick() {
                            dialog.dismiss();
                            Intent intent = new Intent(BankCardListActivity.this, AddBankCardActivity.class);
                            intent.putExtra(AddBankCardActivity.ADD_BANK_CARD_TITLE, "更换银行卡");
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                } else if (TextUtils.equals(addBankCardButton.getText().toString().trim(), "去绑卡")) {
                    Intent intent = new Intent(BankCardListActivity.this, AddBankCardActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_list);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        if (PayActivity.TAG.equals(intent.getStringExtra(KEY_OF_BANK_CARD_COME_FROM))) {
            plv_bankcard.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BankCard bankCard = bankCardList.get(position);
                    Intent intent = new Intent();
                    intent.putExtra(PayActivity.KEY_OF_BANK_NAME, bankCard.getBankName());
                    intent.putExtra(PayActivity.KEY_OF_BANK_CARD_NUM, bankCard.getBankCardNum());
                    setResult(100, intent);
                    finish();
                }
            });
        }
        bankCardAdapter = new MyOrderAdapter(bankCardList);
        plv_bankcard.setAdapter(bankCardAdapter);

        hideDataView();
        tvTitle.setText(R.string.str_pagename_bankcardlist);
        CommonUtils.setTitle(this, rlTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBankCardList();
    }

    @Override

    protected String getPageName() {
        return getString(R.string.str_pagename_bankcardlist);
    }

    /**
     * 获取银行卡列表
     */
    public void getBankCardList() {
        if (mDialog == null) {
            mDialog = new LoadingAlertDialog(this);
        }
        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);

        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("rates", "");

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_BANK_CARD_LIST, params, new AsyncResponseCallBack() {

            @Override
            public void onResult(String arg2) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                String jsonStr = new String(arg2);
                LogUtil.printLog("获取银行卡列表: " + jsonStr);
                JSONObject jsonObject = JSON.parseObject(jsonStr);
                BankCardListBean bankCardListBean = JSON.parseObject(jsonStr, new TypeReference<BankCardListBean>() {
                });
                if (ConstantsUtil.RESPONSE_SUCCEED == bankCardListBean.getStatus()) {
                    bankCardList.clear();
                    if (bankCardListBean.getData()!=null){//不為空，
                        for (int i = 0; i < bankCardListBean.getData().size(); i++) {
                            if (!BANK_CARD_CHANNEL_WAN_KA.equals(bankCardListBean.getData().get(i).getChannel())) {
                                bankCardList.add(bankCardListBean.getData().get(i));
                            }
                        }
                    }
                    if (bankCardList.size() > 0) {
                        bankCardAdapter.notifyDataSetChanged();
                        noDataImageView.setVisibility(View.GONE);
                        plv_bankcard.setVisibility(View.VISIBLE);
                        addBankCardButton.setVisibility(View.VISIBLE);
                        addBankCardButton.setText("管理");
                    } else {
                        setIsToBindBank(bankCardListBean.getCreditline());
                    }

                } else if (ConstantsUtil.RESPONSE_NO_DATA == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    setIsToBindBank(bankCardListBean.getCreditline());
                } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    UserUtils.tokenFailDialog(BankCardListActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                } else {
                    Toast.makeText(BankCardListActivity.this, "获取银行卡列表失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(String path, String msg) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                Toast.makeText(BankCardListActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "获取银行卡列表失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setIsToBindBank(String creditline) {
        if (TextUtils.equals("1", creditline)) {
            noDataImageView.setVisibility(View.VISIBLE);
            plv_bankcard.setVisibility(View.GONE);
            addBankCardButton.setVisibility(View.VISIBLE);
            addBankCardButton.setText("去绑卡");
        } else {
            showNoDataView();
        }
    }

    /**
     * 显示无数据的view
     */
    private void showNoDataView() {
        noDataImageView.setVisibility(View.VISIBLE);
        plv_bankcard.setVisibility(View.GONE);
        addBankCardButton.setVisibility(View.GONE);
    }

    /**
     * 隐藏所有的dataView
     */
    private void hideDataView() {
        noDataImageView.setVisibility(View.GONE);
        plv_bankcard.setVisibility(View.GONE);
        addBankCardButton.setVisibility(View.GONE);
    }


    class MyOrderAdapter extends BaseAdapter {
        private List<BankCard> orderList;

        public MyOrderAdapter(List<BankCard> orderList) {
            super();
            this.orderList = orderList;
        }

        @Override
        public int getCount() {
            return orderList.size();
        }

        @Override
        public Object getItem(int position) {
            return orderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(BankCardListActivity.this, R.layout.add_bank_card_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            BankCard bankCard = orderList.get(position);

            String bankCardNum = bankCard.getBankCardNum();
            bankCardNum = "**** **** **** " + bankCardNum.substring(bankCardNum.length() - 4);
            viewHolder.bankNameTextView.setText(bankCard.getBankName());
            viewHolder.bankCardNumTextView.setText(bankCardNum);
            viewHolder.selectedBankImageView.setVisibility(View.GONE);
            return convertView;
        }

    }

    static class ViewHolder {
        @InjectView(R.id.iv_bankCardListActivity_selectedBank)
        ImageView selectedBankImageView;
        @InjectView(R.id.tv_bankCardListActivity_bankName)
        TextView bankNameTextView;
        @InjectView(R.id.tv_bankCardListActivity_bankCardNum)
        TextView bankCardNumTextView;

        public ViewHolder(View v) {
            super();
            ButterKnife.inject(this, v);
        }
    }
}
