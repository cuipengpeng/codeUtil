package com.jfbank.qualitymarket.adapter;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.LoginActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.config.CacheKeyConfig;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.fragment.DiscoverFragment;
import com.jfbank.qualitymarket.fragment.MyAccountFragment;
import com.jfbank.qualitymarket.model.DiscoverBean;
import com.jfbank.qualitymarket.model.User;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.DensityUtil;
import com.jfbank.qualitymarket.helper.DiskLruCacheHelper;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.squareup.picasso.Picasso;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DiscoverAdapter extends BaseAdapter implements View.OnClickListener {
    private List<DiscoverBean> discoverList = new ArrayList<DiscoverBean>();
    private Activity activity;
    private Map<Integer, DiscoverBean.QualityDiscoveryBanner> clickImageViewMap = new HashMap<Integer, DiscoverBean.QualityDiscoveryBanner>();

    public DiscoverAdapter(Activity activity) {
        super();
        this.activity = activity;
    }

    public void updateData(List<DiscoverBean> discoverList) {
        clickImageViewMap.clear();
        this.discoverList = discoverList;
        DiskLruCacheHelper.put(CacheKeyConfig.CACHE_DISCOVERFRAGMENT, discoverList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return discoverList.size();
    }

    @Override
    public Object getItem(int position) {
        return discoverList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @TargetApi(23)
    public View getView(int position, View convertView, ViewGroup parent) {
        final DiscoverBean discover = discoverList.get(position);

        final List<DiscoverBean.QualityDiscoveryBanner> bannerArrayList = discover.getQualityDiscoveryBanner();
        ImageView[] imageViews = new ImageView[bannerArrayList.size()];

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.item_discover, null);
            viewHolder = new ViewHolder(convertView);
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.tv_discoverFragment_orderItem_title);
            viewHolder.discoverLinearLayout = (LinearLayout) convertView.findViewById(R.id.ll_discoverFragment_orderItem_discover);
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[i] = newViewForViewHolder(viewHolder);
                imageViews[i].setOnClickListener(this);
            }
            ;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.titleTextView.setText(discover.getQualityDiscoveryName());

        //若addImageViewsCount大于0,则应该添加imageview。 若addImageViewsCount小于0, 则应该移除imageview
        int addImageViewsCount = imageViews.length - (viewHolder.discoverLinearLayout.getChildCount() - 1);
        ImageView imageView = new ImageView(activity);
        for (int i = 0; i < imageViews.length; i++) {
            imageView = (ImageView) viewHolder.discoverLinearLayout.getChildAt(i + 1);
            if (imageView == null && addImageViewsCount > 0) {
                imageView = newViewForViewHolder(viewHolder);
            }

            if (i == 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(activity, 255));
                layoutParams.setMargins(0, DensityUtil.dip2px(activity, 10), 0, 0);
                imageView.setLayoutParams(layoutParams);
            }

            imageView.setOnClickListener(this);
            clickImageViewMap.put(imageView.hashCode(), bannerArrayList.get(i));
            CommonUtils.loadCacheImage(activity, imageView, bannerArrayList.get(i).getPicture(), R.mipmap.discover_item_imageview_placeholder);
        }

        if (addImageViewsCount < 0) {
            addImageViewsCount = Math.abs(addImageViewsCount);
            for (int i = (viewHolder.discoverLinearLayout.getChildCount() - 1); addImageViewsCount > 0; addImageViewsCount--, i--) {
                viewHolder.discoverLinearLayout.removeViewAt(i);
            }
        }

        return convertView;
    }

    /**
     * 往viewHolder中添加imageview
     *
     * @param viewHolder
     * @return
     */
    private ImageView newViewForViewHolder(ViewHolder viewHolder) {
        ImageView imageView = new ImageView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(activity, 255));
        layoutParams.setMargins(0, DensityUtil.dip2px(activity, 15), 0, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setId(imageView.hashCode());
        viewHolder.discoverLinearLayout.addView(imageView);
        return imageView;
    }


    @Override
    public void onClick(View v) {
        DiscoverBean.QualityDiscoveryBanner discoveryBanner = clickImageViewMap.get(v.getId());
        if (discoveryBanner != null) {

            String url = discoveryBanner.getUrl();
            //0商品详情, 商品编号放在url字段中   1 普通网址
            //和项目经理沟通，type字段除0外，其余都按普通网址处理
            if ("0".equals(discoveryBanner.getType())) {
                CommonUtils.startGoodsDeteail(activity, url);
            } else{
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + discoveryBanner.getUrl();
                }

                //要求登录
                if ("1".equals(discoveryBanner.getIsLogin())) {

                    //要求登录，但用户没有登录
                    if (!AppContext.isLogin) {
                        Intent loginIntent = new Intent(activity, LoginActivity.class);
                        loginIntent.putExtra(LoginActivity.KEY_OF_COME_FROM, DiscoverFragment.TAG);
                        activity.startActivity(loginIntent);
                        return;
                    }
                    url = url + "?mobilepz=" + AppContext.user.getMobile() + "&tokenpz=" + AppContext.user.getToken();
                }

                //要求实名认证
                if ("1".equals(discoveryBanner.getRealName())) {

                    realNameClick(url);

                    return;
                }

                CommonUtils.startWebViewActivity(activity, url, true, false);
            }
        }
    }

    /**
     * 实名认证点击事件
     *
     * @param url
     */
    private void realNameClick(String url) {
        if (AppContext.isLogin) {// 已经登录
            String userKey = AppContext.user.getUserKey();
            if (TextUtils.isEmpty(userKey) || "null".equalsIgnoreCase(userKey)) {// 未实名认证
                qualityUserShow(url);
            } else {//选中借钱花
                url = url + "&userKey=" + userKey;
                CommonUtils.startWebViewActivity(activity, url, true, false);
            }
        } else {// 先登录
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.putExtra(LoginActivity.KEY_OF_COME_FROM, DiscoverFragment.TAG);
            activity.startActivity(intent);
            return;
        }

    }

    /**
     * 刷新userKey，用于判断是否显示借钱花
     */
    private void qualityUserShow(final String url) {
        if (AppContext.user == null || !AppContext.isLogin) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        HttpRequest.post(activity, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.QUALITYUSERSHOW, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog(jsonStr);
                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            User userInfoBean = JSON.parseObject(jsonObject
                                            .getJSONObject(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME).toJSONString(),
                                    User.class);
                            if (AppContext.user != null && AppContext.isLogin) {//不为空，则做处理
                                AppContext.user.setUserKey(userInfoBean.getUserKey());
                                AppContext.user.setImmediateRepayment(userInfoBean.getImmediateRepayment());//是否有提前还款内容
                                AppContext.user.setRealNameUrl(userInfoBean.getRealNameUrl());
                                selectBorrow(url);
                                EventBus.getDefault().post(new Object(), EventBusConstants.EVENTT_UPDATE_BILLENTER_STATUS);
                            }
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(activity, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), null);
                        } else {
                            Toast.makeText(activity, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        Toast.makeText(activity, R.string.error_params, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 判断是否能选中借钱花，并初始化
     */
    private void selectBorrow(String url) {
        String userKey = AppContext.user.getUserKey();
        if (TextUtils.isEmpty(userKey) || "null".equalsIgnoreCase(userKey)) {// 未实名认证
            Toast.makeText(activity, R.string.str_toast_before_authentication, Toast.LENGTH_SHORT).show();
            CommonUtils.startWebViewActivity(activity, MyAccountFragment.getOneCardUrlParamter(activity, AppContext.user.getRealNameUrl()), true, false, false);
        } else {// 实名认证通过
            new StoreService(activity).saveUserInfo(AppContext.user);//退出应用前保存数据
            url = url + "&userKey=" + userKey;
            CommonUtils.startWebViewActivity(activity, url, true, false);
        }
        return;
    }

    static class ViewHolder {
        @InjectView(R.id.tv_discoverFragment_orderItem_title)
        TextView titleTextView;
        @InjectView(R.id.ll_discoverFragment_orderItem_discover)
        LinearLayout discoverLinearLayout;

        public ViewHolder(View v) {
            super();
            ButterKnife.inject(this, v);
        }

    }
}
