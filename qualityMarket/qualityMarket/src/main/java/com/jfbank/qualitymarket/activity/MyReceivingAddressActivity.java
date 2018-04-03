package com.jfbank.qualitymarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jfbank.qualitymarket.ActivitysManage;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.MyReceivingAddressAdapter;
import com.jfbank.qualitymarket.adapter.MyReceivingAddressAdapter.MyOnClickListener;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.fragment.PopDialogFragment;
import com.jfbank.qualitymarket.model.ReceivingAddressBean;
import com.jfbank.qualitymarket.model.ReceivingAddressBean.DataBean;
import com.jfbank.qualitymarket.model.SaveBaseInfoBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.widget.TwinklingRefreshLayoutView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 彭爱军
 * @date 2016年8月9日
 * <p>
 * 我的收货地址页面
 */

public class MyReceivingAddressActivity extends BaseActivity implements OnClickListener {

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
     * 新增地址页面
     */
    private Button mMy_receing_btn_add_address;
    /**
     * 没有数据显示的图标
     */
    private ImageView mMy_receing_iv_no_data;
    /**
     * list
     */
    private ListView mMy_receiving_address_listview;
    /**
     * 自定义一个布局
     */
    private TwinklingRefreshLayoutView refreshLayout;
    private MyReceivingAddressAdapter mAdapter;
    /**
     * 标记是从我的页面查看地址还是确认订单里面查看
     */
    private boolean mIsMy = false;
    /**
     * 当前页码
     */
    private int pageNo = 1;

    /**
     * 是否正在网络请求中
     */
    private boolean isRefreshOrDown = true;
    private List<DataBean> mData;
    /**
     * 网编请求时加载框
     */
    private LoadingAlertDialog mDialog;

    protected int mBeforePosition; // 之前选中的item的position
    protected int mPosition; // 当前选中的item
    /**
     * 总页数
     */
    private int mPageCount;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_receiving_address);
        ActivitysManage.getActivitysManager().addActivity(this);
        Intent intent = getIntent();
        if (null != intent) {
            mIsMy = intent.getBooleanExtra(ConfirmOrderActivity.KEY_OF_SET_CONSIGNEE_ADDRESS, false);
        }
        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mData != null && mData.size() > 0) {
            mData.clear();
            mAdapter.notifyDataSetChanged();
        }
        pageNo = 1;
        requestQueryReceiptAddress(true, true);
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_myreceivingaddress);
    }

    /**
     * 初始化View以及设置监听
     */
    private void bindViews() {
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        CommonUtils.setTitle(this, rlTitle);

        mMy_receiving_address_listview = (ListView) findViewById(R.id.my_receiving_address_listview);
        refreshLayout = (TwinklingRefreshLayoutView) findViewById(R.id.refreshLayout);
        mMy_receing_btn_add_address = (Button) findViewById(R.id.my_receing_btn_add_address);
        mMy_receing_iv_no_data = (ImageView) findViewById(R.id.my_receing_iv_no_data);

        tvTitle.setText(R.string.str_pagename_myreceivingaddress);

        ivBack.setOnClickListener(this);
        mMy_receing_btn_add_address.setOnClickListener(this);
        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setEnableLoadmore(true);
        refreshLayout.setOverScrollBottomShow(true);

        //下拉刷新和上啦加载更多
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {//下拉刷新


                isRefreshOrDown = true;
                pageNo = 1;
                requestQueryReceiptAddress(true, false);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {//加载更多
                pageNo++;
                if (pageNo > mPageCount) {
                    refreshLayout.finishLoadmore();            //必须加
                    Toast.makeText(MyReceivingAddressActivity.this, "已经没有更多数据了！", Toast.LENGTH_SHORT).show();
                    return;
                }
                isRefreshOrDown = false;
                requestQueryReceiptAddress(false, false);
            }
        });

        mData = new ArrayList<ReceivingAddressBean.DataBean>();
    }

    /**
     * 返回详细地址
     *
     * @param bean
     * @return
     */
    public static String getAddDetail(DataBean bean) {
        String str = "";
        str += bean.getAddProvince() + bean.getAddCity();
        if (null != bean.getAddCounty()) {
            str += bean.getAddCounty();
        }
        if (null != bean.getAddTown()) {
            str += bean.getAddTown();
        }
        if (null != bean.getAddArea()) {
            str += bean.getAddArea();
        }
        str += bean.getAddDetail();
        return str;
    }

    /**
     * 网络请求，查询收货地址
     */
    private void requestQueryReceiptAddress(final boolean isRefresh, boolean dialog) {
        if (null == mDialog) {
            mDialog = new LoadingAlertDialog(this);
        }
        if (dialog)
            mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
        Map<String,String> params = new HashMap<>();

        params.put("ver", AppContext.getAppVersionName(this));
        params.put("Plat", ConstantsUtil.PLAT);

        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        //params.put("mobile", AppContext.user.getMobile()); // 手机
        params.put("defaultAdd", "0"); // 查询所有地址
        params.put("pageNo", pageNo+""); // 当前页码

        Log.e("TAG", params.toString());

        HttpRequest.queryReceiptAddress(mContext, params, new AsyncResponseCallBack() {

            @Override
            public void onResult(String arg2) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (null != mData && mData.size() > 0&&isRefresh) {
                    mData.clear();
                    if (null != mAdapter) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
                if (null != arg2 && arg2.length() > 0) {
                    Log.e("TAG", new String(arg2));
                    explainJson(new String(arg2));
                }
                if (isRefresh) {
                    refreshLayout.finishRefreshing();
                } else {
                    refreshLayout.finishLoadmore();
                }
            }

            @Override
            public void onFailed(String path, String msg) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (pageNo == 1) {
                    refreshLayout.finishRefreshing();
                } else {
                    refreshLayout.finishLoadmore();
                }
                Toast.makeText(MyReceivingAddressActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 解释json数据
     *
     * @param json
     */
    protected void explainJson(String json) {
        ReceivingAddressBean bean = JSON.parseObject(json, ReceivingAddressBean.class);
        if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
            mPageCount = Integer.parseInt(bean.getPageCount());
            setData(bean.getData());
        } else if ("11".equals(bean.getStatus())) {        //无任何提示。  即没有数据时
            if (isRefreshOrDown) {
                mMy_receing_iv_no_data.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
            }
        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
                UserUtils.tokenFailDialog(mContext, bean.getStatusDetail(), null);
        } else {
            Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * 给listView设置数据
     *
     * @param data
     */
    private void setData(List<DataBean> data) {
        if (null == data || data.size() < 1) {
            if (isRefreshOrDown) {
                Toast.makeText(this, "没有查询到数据", Toast.LENGTH_SHORT).show();
                mMy_receing_iv_no_data.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "已经没有更多数据了", Toast.LENGTH_SHORT).show();
            }

            return;
        }
        //mMy_receiving_address_listview.clearAnimation();
        mMy_receing_iv_no_data.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        mData.addAll(data);
        if (null == mAdapter) {
            mAdapter = new MyReceivingAddressAdapter(mData, this, mIsMy);
            mMy_receiving_address_listview.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter.addOrRefreshData(mData); // isDownOrRefresh=true,加载更多，
            // false刷新。
        }
        mAdapter.setmOnClickListener(new MyOnClickListener() {

            @Override
            public void onClick(int beforePosition, int position) {
                Log.e("TAG", beforePosition + "::" + position);
                if (mIsMy) {
                    mBeforePosition = beforePosition;
                    mPosition = position;
                    if (beforePosition != position) {
                        popDialog();
                    } else {
                        if (!"1".equals(mData.get(position).getAddDefault())) {        //可以直接使用这段代码即可。
                            popDialog();
                        }
                    }
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_CONSIGNEE_NAME, mData.get(position).getConsignee());
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_CONSIGNEE_MOBILE,
                            mData.get(position).getConsigneeMobile());
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_CONSIGNEE_ADDRESS, getAddDetail(mData.get(position)));
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_CONSIGNEE_ADDRESS_CODE,
                            mData.get(position).getAddressNo());
                    setResult(200, intent);
                    finish();
                }

            }
        });
    }

    /**
     * 弹出对话框
     */
    protected void popDialog() {
        final PopDialogFragment dialog = PopDialogFragment.newDialog(true, true, null, "确定要修改默认地址吗？", "取消", "确定", null);
        dialog.setOnClickListen(new PopDialogFragment.OnClickListen() {

            @Override
            public void rightClick() {
                dialog.dismiss();
                requestUpdateAddDefault();
            }

            @Override
            public void leftClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "TAG");
    }

    /**
     * 网络请求设置收货默认地址
     */
    protected void requestUpdateAddDefault() {
        if (null == mDialog) {
            mDialog = new LoadingAlertDialog(this);
        }
        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
        Map<String,String> params = new HashMap<>();

        params.put("ver", AppContext.getAppVersionName(this));
        params.put("Plat", ConstantsUtil.PLAT);

        params.put("uid", AppContext.user.getUid()); // 手机
        params.put("token", AppContext.user.getToken());
        params.put("addressNo", mData.get(mPosition).getAddressNo()); // 是否为默认地址

        Log.e("TAG", mData.get(mPosition).getAddressNo());

        Log.e("TAG", params.toString());

        HttpRequest.updateAddDefault(mContext, params, new AsyncResponseCallBack() {

            @Override
            public void onResult(String arg2) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (null != arg2 && arg2.length() > 0) {
                    Log.e("TAG", new String(arg2));
                    explainUpdateDefaultJson(new String(arg2));
                }

            }

            @Override
            public void onFailed(String path, String msg) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                Toast.makeText(MyReceivingAddressActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER,
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * 解释修改默认地址的json
     *
     * @param json
     */
    protected void explainUpdateDefaultJson(String json) {
        SaveBaseInfoBean bean = JSON.parseObject(json, SaveBaseInfoBean.class);
        if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
            if (-1 != mBeforePosition && mBeforePosition < mData.size()) {
                mData.get(mBeforePosition).setAddDefault("2");
            }
            mData.get(mPosition).setAddDefault("1");
            DataBean javaBean = mData.get(mPosition);        //需要将默认地址置顶
            mData.remove(mPosition);
            mData.add(0, javaBean);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL ==Integer.parseInt(bean.getStatus())) {
            UserUtils.tokenFailDialog(mContext, bean.getStatusDetail(), null);
        }else {
            Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.my_receing_btn_add_address: // 新增
                // Toast.makeText(this, "点击了新增地址", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AppendAddressActivity.class);
                intent.putExtra("ADD_ADDRESS", true);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivitysManage.getActivitysManager().finishActivity(this);
    }

}
