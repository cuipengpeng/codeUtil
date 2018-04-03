package com.jf.jlfund.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jf.jlfund.R;
import com.jf.jlfund.adapter.OptionalFundAdapter;
import com.jf.jlfund.base.BaseUIFragment;
import com.jf.jlfund.bean.FundInfoBean;
import com.jf.jlfund.bean.OptionalFundBean;
import com.jf.jlfund.http.HttpRequest;
import com.jf.jlfund.utils.DensityUtil;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.StatusBarUtil;
import com.jf.jlfund.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/11<br>
*/
public class OptionalFragment extends BaseUIFragment {

    @BindView(R.id.rv_optionalFragment_optionalFundList)
    RecyclerView optionalFundListRecyclerView;

    @BindView(R.id.rl_optionalFragment_addOptional)
    RelativeLayout addOptionalRelativeLayout;
    @BindView(R.id.tv_optionalFragment_addOptional)
    TextView addOptionalTextView;

    @BindView(R.id.iv_optionalFragment_netValueSort)
    ImageView netValueSortImageView;
    @BindView(R.id.ll_optionalFragment_netValueSort)
    LinearLayout netValueSortLinearLayout;
    @BindView(R.id.iv_optionalFragment_changeSort)
    ImageView changeSortImageView;
    @BindView(R.id.ll_optionalFragment_changeSort)
    LinearLayout changeSortLinearLayout;

    @BindView(R.id.iv_optionalFragment_no_data)
    ImageView noDataImageView;
    @BindView(R.id.rl_optionalFragment_no_data)
    RelativeLayout noDataRelativeLayout;
    @BindView(R.id.rl_optionalFragment_data)
    RelativeLayout dataRelativeLayout;

    //    private FundInfoBean mFundInfoBean = new FundInfoBean();
    private List<FundInfoBean.Divlist> dividendInfoList = new ArrayList<FundInfoBean.Divlist>();
    private List<OptionalFundBean> mFundInfoBeanList = new ArrayList<OptionalFundBean>();
    private OptionalFundAdapter optionalFundAdapter;
//    private FundInfoBean mFundTrendBean = new FundInfoBean();

    public final int SORT_TIME = 500;
    public final int SORT_NET_VALUE_UP = 501;
    public final int SORT_NET_VALUE_DOWN = 502;
    public final int SORT_YIELD_UP = 503;
    public final int SORT_YIELD_DOWN = 504;
    public int currentSort = SORT_TIME;

    public int currentText = -1;
    public final int EDIT_TEXT = 100;
    public final int FINISH_TEXT = 101;
    private FundSearchFragment fundSearchFragment;

    @OnClick({R.id.iv_optionalFragment_no_data, R.id.rl_optionalFragment_addOptional, R.id.ll_optionalFragment_netValueSort,
            R.id.ll_optionalFragment_changeSort})
    public void onViewClick(View view) {
        OptionalFundBean optionalFundBean0 = null;
        OptionalFundBean optionalFundBean1 = null;

        switch (view.getId()) {
            case R.id.iv_optionalFragment_no_data:
            case R.id.rl_optionalFragment_addOptional:
                launchSearchFundPage();
                break;
            case R.id.ll_optionalFragment_netValueSort:
                switch (currentSort) {
                    case SORT_NET_VALUE_UP:
                        currentSort = SORT_NET_VALUE_DOWN;
                        netValueSortImageView.setBackgroundResource(R.mipmap.optional_sort_down);
                        for (int i = 0; i < optionalFundAdapter.mDataList.size() - 1; i++) {
                            for (int j = i + 1; j < optionalFundAdapter.mDataList.size(); j++) {
                                if (StringUtil.doubleValue(optionalFundAdapter.mDataList.get(i).getNav()) > StringUtil.doubleValue(optionalFundAdapter.mDataList.get(j).getNav())) {
                                    optionalFundBean0 = optionalFundAdapter.mDataList.get(j);
                                    optionalFundBean1 = optionalFundAdapter.mDataList.get(i);
                                    optionalFundAdapter.mDataList.remove(i);
                                    optionalFundAdapter.mDataList.add(i, optionalFundBean0);
                                    optionalFundAdapter.mDataList.remove(j);
                                    optionalFundAdapter.mDataList.add(j, optionalFundBean1);
                                }
                            }
                        }
                        break;
                    case SORT_NET_VALUE_DOWN:
                        netValueSortImageView.setBackgroundResource(R.mipmap.optional_sort_no);
                        sortByTime();
                        break;
                    case SORT_TIME:
                    default:
                        currentSort = SORT_NET_VALUE_UP;
                        netValueSortImageView.setBackgroundResource(R.mipmap.optional_sort_up);
                        for (int i = 0; i < optionalFundAdapter.mDataList.size() - 1; i++) {
                            for (int j = i + 1; j < optionalFundAdapter.mDataList.size(); j++) {
                                if (StringUtil.doubleValue(optionalFundAdapter.mDataList.get(i).getNav()) < StringUtil.doubleValue(optionalFundAdapter.mDataList.get(j).getNav())) {
                                    optionalFundBean0 = optionalFundAdapter.mDataList.get(j);
                                    optionalFundBean1 = optionalFundAdapter.mDataList.get(i);
                                    optionalFundAdapter.mDataList.remove(i);
                                    optionalFundAdapter.mDataList.add(i, optionalFundBean0);
                                    optionalFundAdapter.mDataList.remove(j);
                                    optionalFundAdapter.mDataList.add(j, optionalFundBean1);
                                }
                            }
                        }

                        break;
                }
                changeSortImageView.setBackgroundResource(R.mipmap.optional_sort_no);
                optionalFundAdapter.notifyDataSetChanged();
                break;
            case R.id.ll_optionalFragment_changeSort:
                switch (currentSort) {
                    case SORT_YIELD_UP:
                        currentSort = SORT_YIELD_DOWN;
                        changeSortImageView.setBackgroundResource(R.mipmap.optional_sort_down);
                        for (int i = 0; i < optionalFundAdapter.mDataList.size() - 1; i++) {
                            for (int j = i + 1; j < optionalFundAdapter.mDataList.size(); j++) {
                                if (StringUtil.doubleValue(optionalFundAdapter.mDataList.get(i).getDaygrowth()) > StringUtil.doubleValue(optionalFundAdapter.mDataList.get(j).getDaygrowth())) {
                                    optionalFundBean0 = optionalFundAdapter.mDataList.get(j);
                                    optionalFundBean1 = optionalFundAdapter.mDataList.get(i);
                                    optionalFundAdapter.mDataList.remove(i);
                                    optionalFundAdapter.mDataList.add(i, optionalFundBean0);
                                    optionalFundAdapter.mDataList.remove(j);
                                    optionalFundAdapter.mDataList.add(j, optionalFundBean1);
                                }
                            }
                        }
                        break;
                    case SORT_YIELD_DOWN:
                        changeSortImageView.setBackgroundResource(R.mipmap.optional_sort_no);
                        sortByTime();
                        break;
                    case SORT_TIME:
                    default:
                        currentSort = SORT_YIELD_UP;
                        changeSortImageView.setBackgroundResource(R.mipmap.optional_sort_up);
                        for (int i = 0; i < optionalFundAdapter.mDataList.size() - 1; i++) {
                            for (int j = i + 1; j < optionalFundAdapter.mDataList.size(); j++) {
                                if (StringUtil.doubleValue(optionalFundAdapter.mDataList.get(i).getDaygrowth()) < StringUtil.doubleValue(optionalFundAdapter.mDataList.get(j).getDaygrowth())) {
                                    optionalFundBean0 = optionalFundAdapter.mDataList.get(j);
                                    optionalFundBean1 = optionalFundAdapter.mDataList.get(i);
                                    optionalFundAdapter.mDataList.remove(i);
                                    optionalFundAdapter.mDataList.add(i, optionalFundBean0);
                                    optionalFundAdapter.mDataList.remove(j);
                                    optionalFundAdapter.mDataList.add(j, optionalFundBean1);
                                }
                            }
                        }
                        break;
                }
                netValueSortImageView.setBackgroundResource(R.mipmap.optional_sort_no);
                optionalFundAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void sortByTime() {
        OptionalFundBean optionalFundBean0;
        OptionalFundBean optionalFundBean1;
        currentSort = SORT_TIME;
        for (int i = 0; i < optionalFundAdapter.mDataList.size() - 1; i++) {
            for (int j = i + 1; j < optionalFundAdapter.mDataList.size(); j++) {
                if (optionalFundAdapter.mDataList.get(i).getCollectTime() < optionalFundAdapter.mDataList.get(j).getCollectTime()) {
                    optionalFundBean0 = optionalFundAdapter.mDataList.get(j);
                    optionalFundBean1 = optionalFundAdapter.mDataList.get(i);
                    optionalFundAdapter.mDataList.remove(i);
                    optionalFundAdapter.mDataList.add(i, optionalFundBean0);
                    optionalFundAdapter.mDataList.remove(j);
                    optionalFundAdapter.mDataList.add(j, optionalFundBean1);
                }
            }
        }
    }

    private void launchSearchFundPage() {
        fundSearchFragment = (FundSearchFragment) getChildFragmentManager().findFragmentByTag("FundSearchFragment");
        if (fundSearchFragment == null) {
            fundSearchFragment = new FundSearchFragment();
        }
        fundSearchFragment.setDialogCancelListener(new FundSearchFragment.DialogCancelListener(){

            @Override
            public void onCancel() {
                getOptionalFundList(true);
            }
        });
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
//                fragmentTransaction.setCustomAnimations(R.anim.bottom_in, R.anim.bottom_out, R.anim.bottom_in, R.anim.bottom_out);
        if (!fundSearchFragment.isAdded()) {
            fragmentTransaction.add(fundSearchFragment, "FundSearchFragment");
            fragmentTransaction.commit();
        }
    }

    public static Fragment newInstance(int ruleType) {
        OptionalFragment optionalFragment = new OptionalFragment();
//        optionalFragment. = ruleType;
        return optionalFragment;
    }

    @Override
    protected String getPageTitle() {
        return "自选基金";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.fragment_optional;
    }

    @Override
    protected void initPageData() {
        showBaseUITitle = true;

        optionalFundAdapter = new OptionalFundAdapter(getActivity(), mFundInfoBeanList, this);
        optionalFundListRecyclerView.setAdapter(optionalFundAdapter);
        optionalFundListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) titleBarRelativeLayout.getLayoutParams();
        layoutParams.topMargin = StatusBarUtil.getStatusBarHeight(getActivity());
        titleBarRelativeLayout.setLayoutParams(layoutParams);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!SPUtil.getInstance().isLogin()) {
            showContentView();
            showNoDataView();
        }
        LogUtils.printLog("==================---------");
        getOptionalFundList(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        LogUtils.printLog("==================");
        if (SPUtil.getInstance().isLogin() && isVisibleToUser) {
            if (StringUtil.notEmpty(optionalFundAdapter.delFundCodeStr.toString())) {
                deleteOptionalFund();
            } else {
                getOptionalFundList(true);
            }
            showListViewDefaultStatus();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


    protected void setBackListener() {
        baseBackRelativeLayout.setVisibility(View.GONE);
        baseBackTextView.setVisibility(View.VISIBLE);
        baseBackTextView.setText("编辑");
        currentText = EDIT_TEXT;
        baseBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SPUtil.getInstance().isLogin()) {

                } else {

                }

                switch (currentText) {
                    case EDIT_TEXT:
                        currentText = FINISH_TEXT;
                        baseBackTextView.setText("完成");
                        optionalFundAdapter.notifyDataSetChanged();
                        break;
                    case FINISH_TEXT:
                        showListViewDefaultStatus();
                        if (StringUtil.notEmpty(optionalFundAdapter.delFundCodeStr.toString())) {
                            deleteOptionalFund();
                        }
                        break;
                }
            }
        });
    }

    private void showListViewDefaultStatus() {
        currentText = EDIT_TEXT;
        baseBackTextView.setText("编辑");
        optionalFundAdapter.notifyDataSetChanged();
    }

    @Override
    protected void setRightMenu() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) baseRightMenuImageView.getLayoutParams();
        layoutParams.width = DensityUtil.dip2px(18);
        layoutParams.height = DensityUtil.dip2px(18);
        baseRightMenuImageView.setLayoutParams(layoutParams);
        baseRightMenuImageView.setBackgroundResource(R.mipmap.icon_search_common_title);
        baseRightMenuImageView.setVisibility(View.VISIBLE);
        baseRightMenuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSearchFundPage();
            }
        });
    }

    @Override
    public void showNoDataView() {
        noDataRelativeLayout.setVisibility(View.VISIBLE);
        dataRelativeLayout.setVisibility(View.GONE);
        baseBackTextView.setVisibility(View.INVISIBLE);
    }

    private void getOptionalFundList(final boolean initRequest) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", SPUtil.getInstance().getToken());

        HttpRequest.post(HttpRequest.JL_FUND_TEST_ENV_WEB_URL + HttpRequest.GET_OPTIONAL_FUND_LIST, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mFundInfoBeanList = JSON.parseArray(response.body(), OptionalFundBean.class);

                if (mFundInfoBeanList != null && !mFundInfoBeanList.isEmpty()) {
                    baseBackTextView.setVisibility(View.VISIBLE);
                    dataRelativeLayout.setVisibility(View.VISIBLE);
                    noDataRelativeLayout.setVisibility(View.GONE);
                    optionalFundAdapter.upateData(initRequest, mFundInfoBeanList);
                } else {
                    showNoDataView();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }


    /**
     * 删除自选
     */
    private void deleteOptionalFund() {
        Map<String, String> params = new HashMap<String, String>();
//        params.put("fundcode", fundCode);
        String delFundCode = optionalFundAdapter.delFundCodeStr.toString();
        params.put("sid", delFundCode.substring(0, delFundCode.length() - 1));
        params.put("token", SPUtil.getInstance().getToken());

        HttpRequest.post(HttpRequest.JL_FUND_TEST_ENV_WEB_URL + HttpRequest.DELETE_OPTIONAL_FUND, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                getOptionalFundList(true);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    protected boolean isCountPage() {
        return true;
    }
}
