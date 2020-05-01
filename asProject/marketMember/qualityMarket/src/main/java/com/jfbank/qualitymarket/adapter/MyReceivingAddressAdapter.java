package com.jfbank.qualitymarket.adapter;


import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.AppendAddressActivity;
import com.jfbank.qualitymarket.activity.LoginActivity;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.fragment.PopDialogFragment;
import com.jfbank.qualitymarket.model.DeleteReceiptAddressBean;
import com.jfbank.qualitymarket.model.ReceivingAddressBean.DataBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import org.apache.http.Header;

import java.util.List;

/**
 * 我的收货地址对应的Adapter
 *
 * @author 彭爱军
 * @date 2016年8月9日
 */
public class MyReceivingAddressAdapter extends BaseAdapter {

    private List<DataBean> mData;
    private FragmentActivity mContext;
    private LayoutInflater inflater;
    private ViewHolder mViewHolder;
    private NoDeleteViewHolder mNoViewHolder;
    /**
     * 记录前一个选中的下标
     */
    private int mBeforePosition = -1;
    /**
     * 标记是从我的页面查看地址还是确认订单里面查看
     */
    private boolean mIsMy;
    private MyOnClickListener mOnClickListener;
    /**
     * 网编请求时加载框
     */
    private LoadingAlertDialog mDialog;

    public interface MyOnClickListener {

        public void onClick(int beforePosition, int position);
    }

    public void setmOnClickListener(MyOnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public MyReceivingAddressAdapter(List<DataBean> mData, FragmentActivity mContext, boolean mIsMy) {
        super();
        this.mData = mData;
        this.mContext = mContext;
        this.mIsMy = mIsMy;
        inflater = LayoutInflater.from(mContext);
    }

    /**
     * 添加或者刷新数据
     *
     * @param mData
     */
    public void addOrRefreshData(List<DataBean> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        //Log.e("TAG", "mData.size:" + mData.size());
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (!mIsMy) {
            convertView = containNoDeleteItem(position, convertView);
        } else {
            convertView = containDeleteItem(position, convertView);
        }

        return convertView;
    }

    /**
     * 没有删除的item
     *
     * @param position
     * @param convertView
     * @return
     */
    private View containNoDeleteItem(final int position, View convertView) {
        final DataBean bean = mData.get(position);
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_on_contain_delete_receiving_address, null);
            mNoViewHolder = NoDeleteViewHolder.create(convertView);
            convertView.setTag(mNoViewHolder);
        } else {
            mNoViewHolder = (NoDeleteViewHolder) convertView.getTag();
        }
        mNoViewHolder.receivingTvName.setText(bean.getConsignee());
        mNoViewHolder.receivingTvPhone.setText(bean.getConsigneeMobile());
        mNoViewHolder.receivingTvAddress.setText(getAddDetail(bean));

        mNoViewHolder.mOn_contain_delete_iv_edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AppendAddressActivity.startActivity(mContext, bean.getConsignee(), bean.getConsigneeMobile(), bean.getAddDetail(), bean.getAddProvince(),
                        bean.getAddProvinceCode(), bean.getAddCity(), bean.getAddCityCode(), bean.getAddCounty(), bean.getAddCountyCode(), bean.getAddTown(),
                        bean.getAddTownCode(), bean.getAddArea(), bean.getAddAreaCode(), bean.getAddressNo(), bean.getAddDefault(), bean.getAddresslabel());
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mOnClickListener.onClick(mBeforePosition, position);
            }
        });

        if (null != bean.getAddresslabel() && !"".equals(bean.getAddresslabel())) {
            mNoViewHolder.no_contain_delete_btn_label.setText(bean.getAddresslabel());
            mNoViewHolder.no_contain_delete_btn_label.setVisibility(View.VISIBLE);
        } else {
            mNoViewHolder.no_contain_delete_btn_label.setVisibility(View.GONE);
        }


        if ("1".equals(bean.getAddDefault())) {
            mNoViewHolder.mOn_contain_delete_iv_select.setImageResource(R.drawable.xuanzhong);
            mNoViewHolder.mNo_contain_delete_btn_select.setVisibility(View.VISIBLE);
            mNoViewHolder.no_contain_delete_btn_label.setBackgroundResource(R.drawable.label);
        } else {
            mNoViewHolder.mOn_contain_delete_iv_select.setImageResource(R.drawable.weixuanz);
            mNoViewHolder.mNo_contain_delete_btn_select.setVisibility(View.GONE);
            mNoViewHolder.no_contain_delete_btn_label.setBackgroundResource(R.drawable.grey_label);
        }
        Log.e("TAG", (View.GONE == mNoViewHolder.no_contain_delete_btn_label.getVisibility()) + "::" + (View.GONE == mNoViewHolder.mNo_contain_delete_btn_select.getVisibility()));

        if ((View.GONE == mNoViewHolder.no_contain_delete_btn_label.getVisibility()) && (View.GONE == mNoViewHolder.mNo_contain_delete_btn_select.getVisibility())) {
            RelativeLayout.LayoutParams lp = (LayoutParams) mNoViewHolder.no_contain_include.getLayoutParams();
            lp.bottomMargin = 16;
            mNoViewHolder.no_contain_include.setLayoutParams(lp);
        }
        return convertView;
    }

    /**
     * 返回详细地址
     *
     * @param bean
     * @return
     */
    private String getAddDetail(DataBean bean) {
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
     * 有删除功能的item
     *
     * @param position
     * @param convertView
     * @return
     */
    private View containDeleteItem(final int position, View convertView) {
        final DataBean bean = mData.get(position);
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_contain_delete_receiving_address, null);
            mViewHolder = ViewHolder.create(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.receivingTvName.setText(bean.getConsignee());
        mViewHolder.receivingTvPhone.setText(bean.getConsigneeMobile());
        mViewHolder.receivingTvAddress.setText(getAddDetail(bean));

        mViewHolder.mContain_delete_rl_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popDialog(bean.getAddressNo(), position);

            }
        });
        mViewHolder.mContain_delete_rl_redact.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Toast.makeText(mContext, "点击了编辑", Toast.LENGTH_SHORT).show();
                AppendAddressActivity.startActivity(mContext, bean.getConsignee(), bean.getConsigneeMobile(), bean.getAddDetail(), bean.getAddProvince(),
                        bean.getAddProvinceCode(), bean.getAddCity(), bean.getAddCityCode(), bean.getAddCounty(), bean.getAddCountyCode(), bean.getAddTown(),
                        bean.getAddTownCode(), bean.getAddArea(), bean.getAddAreaCode(), bean.getAddressNo(), bean.getAddDefault(), bean.getAddresslabel());
            }
        });
        mViewHolder.mContain_delete_iv_selection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mOnClickListener.onClick(mBeforePosition, position);
            }
        });
        if (null != bean.getAddresslabel() && !"".equals(bean.getAddresslabel())) {
            mViewHolder.contain_delete_btn_label.setText(bean.getAddresslabel());
            mViewHolder.contain_delete_btn_label.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.contain_delete_btn_label.setVisibility(View.GONE);
        }
        //contain_delete_btn_label
        if ("1".equals(bean.getAddDefault())) {
            mViewHolder.mContain_delete_iv_selection.setBackgroundResource(R.drawable.xuanzhong);
            mViewHolder.mContain_delete_tv_selection.setBackgroundResource(R.drawable.mor2_button);
            mViewHolder.contain_delete_btn_label.setBackgroundResource(R.drawable.label);
            mBeforePosition = position;
        } else {
            mViewHolder.mContain_delete_iv_selection.setBackgroundResource(R.drawable.weixuanz);
            mViewHolder.mContain_delete_tv_selection.setBackgroundResource(R.drawable.mor1_button);
            mViewHolder.contain_delete_btn_label.setBackgroundResource(R.drawable.grey_label);
        }
        return convertView;
    }

    /**
     * 弹出对话框
     *
     * @param addressNo
     * @param position
     */
    protected void popDialog(final String addressNo, final int position) {
        //
        final PopDialogFragment dialog = PopDialogFragment.newDialog(true, true, null, "确认删除地址？", "取消", "确认", null);
        dialog.setOnClickListen(new PopDialogFragment.OnClickListen() {

            @Override
            public void rightClick() {
                requestDeleteAddress(addressNo, position);
                dialog.dismiss();
            }

            @Override
            public void leftClick() {
                dialog.dismiss();
            }
        });
        dialog.show(mContext.getSupportFragmentManager(), "dialog");
    }

    /**
     * 网络请求删除收货地址
     *
     * @param position
     */
    protected void requestDeleteAddress(String addressNo, final int position) {
        if (null == mDialog) {
            mDialog = new LoadingAlertDialog(mContext);
        }
        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
        Map<String,String> params = new HashMap<>();

        params.put("ver", AppContext.getAppVersionName(mContext));
        params.put("Plat", ConstantsUtil.PLAT);

        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());

        params.put("addressNo", addressNo); // 收货地址


        Log.e("TAG", params.toString());
        Log.e("TAG", addressNo);

        HttpRequest.deleteReceiptAddress(mContext,params, new AsyncResponseCallBack() {

            @Override
            public void onResult(String arg2) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (null != arg2 && arg2.length() > 0) {
                    Log.e("TAG", new String(arg2));
                    explainJson(new String(arg2), position);
                }

            }

            @Override
            public void onFailed(String path, String msg) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                Log.e("TAG", "arg0:"+ msg);
                Toast.makeText(mContext, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();

            }
        });

    }

    /**
     * 解释删除收货地址的json
     *
     * @param position
     */
    protected void explainJson(String resultJson, int position) {
        DeleteReceiptAddressBean bean = JSON.parseObject(resultJson, DeleteReceiptAddressBean.class);
        if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
            mData.remove(position);
            Toast.makeText(mContext, "删除收货地址成功", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
                UserUtils.tokenFailDialog(mContext, bean.getStatusDetail(), null);
        } else {
            Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * ViewHolder
     *
     * @author 彭爱军
     * @date 2016年8月9日
     */
    private static class ViewHolder {
        /**
         * 名字
         */
        private final TextView receivingTvName;
        /**
         * 电话
         */
        private final TextView receivingTvPhone;
        /**
         * 详细地址
         */
        private final TextView receivingTvAddress;
        /**
         * 是否选择为默认地址显示的图标
         */
        private final TextView mContain_delete_tv_selection;
        /**
         * 是否选择为默认地址
         */
        private ImageView mContain_delete_iv_selection;
        /**
         * 删除
         */
        private RelativeLayout mContain_delete_rl_delete;
        /**
         * 编辑
         */
        private RelativeLayout mContain_delete_rl_redact;
        /**
         * item
         */
        private RelativeLayout contain_delete_include;
        private TextView contain_delete_btn_label;

        private ViewHolder(TextView receivingTvName, TextView receivingTvPhone, TextView receivingTvAddress,
                           TextView mContain_delete_tv_selection, ImageView mContain_delete_iv_selection, RelativeLayout mContain_delete_rl_delete,
                           RelativeLayout mContain_delete_rl_redact, RelativeLayout contain_delete_include, TextView contain_delete_btn_label) {
            this.receivingTvName = receivingTvName;
            this.receivingTvPhone = receivingTvPhone;
            this.receivingTvAddress = receivingTvAddress;
            this.mContain_delete_tv_selection = mContain_delete_tv_selection;
            this.mContain_delete_iv_selection = mContain_delete_iv_selection;
            this.mContain_delete_rl_delete = mContain_delete_rl_delete;
            this.mContain_delete_rl_redact = mContain_delete_rl_redact;
            this.contain_delete_include = contain_delete_include;
            this.contain_delete_btn_label = contain_delete_btn_label;
        }

        public static ViewHolder create(View rootView) {
            TextView receivingTvName = (TextView) rootView.findViewById(R.id.receiving_tv_name);
            TextView receivingTvPhone = (TextView) rootView.findViewById(R.id.receiving_tv_phone);
            TextView receivingTvAddress = (TextView) rootView.findViewById(R.id.receiving_tv_address);
            ImageView mContain_delete_iv_selection = (ImageView) rootView
                    .findViewById(R.id.contain_delete_iv_selection);
            RelativeLayout mContain_delete_rl_delete = (RelativeLayout) rootView
                    .findViewById(R.id.contain_delete_rl_delete);
            TextView mContain_delete_tv_selection = (TextView) rootView.findViewById(R.id.contain_delete_tv_selection);
            RelativeLayout mContain_delete_rl_redact = (RelativeLayout) rootView
                    .findViewById(R.id.contain_delete_rl_redact);
            RelativeLayout contain_delete_include = (RelativeLayout) rootView
                    .findViewById(R.id.contain_item_rl);
            TextView contain_delete_btn_label = (TextView) rootView
                    .findViewById(R.id.contain_delete_btn_label);

            return new ViewHolder(receivingTvName, receivingTvPhone, receivingTvAddress, mContain_delete_tv_selection,
                    mContain_delete_iv_selection, mContain_delete_rl_delete, mContain_delete_rl_redact, contain_delete_include, contain_delete_btn_label);
        }
    }

    /**
     * 没有删除选项的item对应的ViewHolder
     *
     * @author 彭爱军
     * @date 2016年8月11日
     */
    private static class NoDeleteViewHolder {
        /**
         * 名字
         */
        private TextView receivingTvName;
        /**
         * 电话
         */
        private TextView receivingTvPhone;
        /**
         * 详细地址
         */
        private TextView receivingTvAddress;
        /**
         * 是否选择为默认地址显示的图标
         */
        private ImageView mOn_contain_delete_iv_select;
        /**
         * 编辑
         */
        private ImageView mOn_contain_delete_iv_edit;
        /**
         * 显示选择的图标
         */
        private TextView mNo_contain_delete_btn_select;
        private View no_contain_delete_view2;
        private TextView no_contain_delete_btn_label;
        private View no_contain_include;

        public NoDeleteViewHolder(TextView receivingTvName, TextView receivingTvPhone, TextView receivingTvAddress,
                                  ImageView mOn_contain_delete_iv_select, ImageView mOn_contain_delete_iv_edit,
                                  TextView mNo_contain_delete_btn_select, View no_contain_delete_view2, TextView no_contain_delete_btn_label, View no_contain_include) {

            this.receivingTvName = receivingTvName;
            this.receivingTvPhone = receivingTvPhone;
            this.receivingTvAddress = receivingTvAddress;
            this.mOn_contain_delete_iv_select = mOn_contain_delete_iv_select;
            this.mOn_contain_delete_iv_edit = mOn_contain_delete_iv_edit;
            this.mNo_contain_delete_btn_select = mNo_contain_delete_btn_select;
            this.no_contain_delete_view2 = no_contain_delete_view2;
            this.no_contain_delete_btn_label = no_contain_delete_btn_label;
            this.no_contain_include = no_contain_include;

        }

        public static NoDeleteViewHolder create(View rootView) {
            TextView receivingTvName = (TextView) rootView.findViewById(R.id.receiving_tv_name);
            TextView receivingTvPhone = (TextView) rootView.findViewById(R.id.receiving_tv_phone);
            TextView receivingTvAddress = (TextView) rootView.findViewById(R.id.receiving_tv_address);

            ImageView mOn_contain_delete_iv_select = (ImageView) rootView
                    .findViewById(R.id.on_contain_delete_iv_select);
            ImageView mOn_contain_delete_iv_edit = (ImageView) rootView.findViewById(R.id.on_contain_delete_iv_edit);
            TextView mNo_contain_delete_btn_select = (TextView) rootView.findViewById(R.id.no_contain_delete_btn_select);
            TextView no_contain_delete_btn_label = (TextView) rootView.findViewById(R.id.no_contain_delete_btn_label);
            View no_contain_delete_view2 = (View) rootView.findViewById(R.id.no_contain_delete_view2);
            View no_contain_include = (View) rootView.findViewById(R.id.no_contain_include);

            return new NoDeleteViewHolder(receivingTvName, receivingTvPhone, receivingTvAddress, mOn_contain_delete_iv_select,
                    mOn_contain_delete_iv_edit, mNo_contain_delete_btn_select, no_contain_delete_view2, no_contain_delete_btn_label, no_contain_include);
        }
    }

}
