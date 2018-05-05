package com.test.bank.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.adapter.SearchAdapter;
import com.test.bank.base.BaseBean;
import com.test.bank.base.BaseBusiness;
import com.test.bank.bean.HotSearchBean;
import com.test.bank.bean.SearchResultBean;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.utils.CommonUtil;
import com.test.bank.utils.DensityUtil;
import com.test.bank.utils.FileUtils;
import com.test.bank.utils.LRULinkedList;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StatusBarUtil;
import com.test.bank.view.activity.SingleFundDetailActivity;
import com.test.bank.weight.FlowLayout;
import com.test.bank.weight.holder.SearchHistoryItemViewHolder;
import com.test.bank.weight.refreshlayout.AutoLoadMoreRecyclerView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;

public class FundSearchFragment extends DialogFragment implements View.OnClickListener {
    EditText etSearch;
    AutoLoadMoreRecyclerView recyclerView;
    FlowLayout flowLayout;
    LinearLayout llSearchHistory;
    ScrollView scrollView;

    DialogCancelListener mDialogCancelListener;
    TextView tvIconRealTimeHotList;
    RelativeLayout rlSearchHistoryTitle;
    LinearLayout llSearchEmpty;
    TextView tvSearchEmpty;

    SearchAdapter adapter;
    List<SearchResultBean.SearchResultItem> searchResultList;   //所搜结果列表

    LRULinkedList<SearchResultBean.SearchResultItem> historySearchList;  //历史搜索列表

    ImageView ivClearInpuContent;

    String searchKeyWord = "";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.SearchFragmentDialog);
        dialog.setContentView(R.layout.fragment_search);
        init(dialog);
        return dialog;
    }

    private void init(Dialog dialog) {
        initView(dialog);
        initData();
        initListener(dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    private void initView(Dialog dialog) {
        if (dialog != null && dialog.getWindow() != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout(dm.widthPixels, dm.heightPixels);
        }
        etSearch = dialog.findViewById(R.id.et_search);
        ivClearInpuContent = dialog.findViewById(R.id.iv_search_content_clear);
        recyclerView = dialog.findViewById(R.id.recyclerView_search);
        flowLayout = dialog.findViewById(R.id.flowLayout_search);
        tvIconRealTimeHotList = dialog.findViewById(R.id.tv_icon_realTimeHotList);
        rlSearchHistoryTitle = dialog.findViewById(R.id.rl_hsitory_search_title);
        llSearchHistory = dialog.findViewById(R.id.ll_search_history);
        scrollView = dialog.findViewById(R.id.scrollView_search);
        llSearchEmpty = dialog.findViewById(R.id.ll_search_result_empty);
        tvSearchEmpty = dialog.findViewById(R.id.tv_search_empty);
        setCancelable(true);
        int naviHeight = StatusBarUtil.getNavigationBarHeight(dialog.getContext());
        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());// + naviHeight);
        llSearchHistory.setPadding(llSearchHistory.getPaddingLeft(), llSearchHistory.getPaddingTop(), llSearchHistory.getPaddingRight(), llSearchHistory.getPaddingBottom() + naviHeight);
        recyclerView.setFooterBgColor("#ffffff");
//        flowLayout.requestLayout();
    }

    private void initData() {
        if (searchResultList == null) {
            searchResultList = new ArrayList<>();
        }
        if (adapter == null) {
            adapter = new SearchAdapter(getContext(), searchResultList);
        }

        if (historySearchList == null) {
            historySearchList = new LRULinkedList<>(10);
        }

        countDownTimer = new MyCountDownTimer(DELAY_OF_SEARCH, 50);

//        refreshHistorySearchList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setItemPrefetchEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        getHotSearchData();
    }

    private String lastKeyWord;
    private long lastInputMillis;
    private static long DELAY_OF_SEARCH = 600;

    private void initListener(Dialog dialog) {
        dialog.findViewById(R.id.tv_search_cancel).setOnClickListener(this);
        dialog.findViewById(R.id.iv_search_content_clear).setOnClickListener(this);
        dialog.findViewById(R.id.iv_clear_search_history).setOnClickListener(this);

        adapter.setOnClickSearchItemListener(new SearchAdapter.OnClickSearchItemListener() {
            @Override
            public void onClickSearchItem(SearchResultBean.SearchResultItem item) {
                if (historySearchList == null) {
                    return;
                }

                HashMap<String, String> map = new HashMap<>();
                map.put("fundCode", item.getFundresCode());
                MobclickAgent.onEvent(getContext(), "click_searchFragment_itemSearch", map);

                historySearchList.put(item);
                CommonUtil.startActivity(getContext(), SingleFundDetailActivity.class, item.getFundresCode());
//                FileUtils.saveSearchHistoryList(historySearchList);   不在此处进行序列化，而是在退出搜索之后一起初始化
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastKeyWord = s.toString().trim();
                LogUtils.e("beforeTextChanged: lastKeyWord [" + lastKeyWord + "]");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newKeyWord = s.toString().trim();
                LogUtils.e("onTextChanged: newKeyWord[" + newKeyWord + "]");
                if (newKeyWord.equals(lastKeyWord)) {
                    return;
                }

                scrollView.setVisibility(newKeyWord.length() > 0 ? View.GONE : View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
//                recyclerView.setVisibility(newKeyWord.length() > 0 ? View.VISIBLE : View.GONE);
                ivClearInpuContent.setVisibility(newKeyWord.length() > 0 ? View.VISIBLE : View.GONE);
                if (newKeyWord.length() > 0) {
                    searchKeyWord = newKeyWord;
                    adapter.setSearchKeyWord(searchKeyWord);
                    long currMillis = System.currentTimeMillis();
                    LogUtils.e("currMillis: " + currMillis + " lastInputMillis: " + lastInputMillis + " dm: " + (currMillis - lastInputMillis));
                    if (newKeyWord.length() == 1 || lastInputMillis == 0 || currMillis - lastInputMillis < DELAY_OF_SEARCH) {
                        LogUtils.e("hang....<600, ,, do not search....." + searchKeyWord);
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer.start();
                        }
                    } else {
                        LogUtils.e("began search......" + searchKeyWord);
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }
                        beginSearch(true);
                    }
                    lastInputMillis = System.currentTimeMillis();
                } else {
                    searchKeyWord = "";
                    searchResultList.clear();
                    adapter.setSearchKeyWord(searchKeyWord);
                    adapter.notifyDataSetChanged();
                    llSearchEmpty.setVisibility(View.GONE);
                    refreshHistorySearchList();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        recyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                beginSearch(false);
            }
        });
    }

    private boolean isReadedHistoryRecord = false;  //是否已经读取了历史搜索记录

    //读取本地历史记录
    private void refreshHistorySearchList() {
        if (isReadedHistoryRecord) {  //如果已经读取过一次了
            llSearchHistory.removeAllViews();
            inflateSearchHistoryDataOnUIThread();
            return;
        }
        //第一次读取
        /**
         *  isReadedHistoryRecord: 如果没有读取过，则读取后直接赋值，即进入该页面只在初始化的时候读取一次。
         *  在搜索内容并点击进入后添加到 historySearchList 集合中。返回之后如果清除搜索框内容重显历史搜索页面，则直接使用 historySearchList 集合中的内容【因为可能已经包含了新的而没有序列化到本地的搜索记录【
         *  如果点击搜索一次就序列化到本地一次开销较大，性能不高，并且可能存在线程读写异步问题。
         */
        FileUtils.getSearchHistoryList(new FileUtils.OnReadFileFinishedListener<LRULinkedList<SearchResultBean.SearchResultItem>>() {
            @Override
            public void onReadFinished(LRULinkedList<SearchResultBean.SearchResultItem> result) {
                if (result == null) {
                    return;
                }
                if (!isReadedHistoryRecord) {
                    historySearchList = result;
                    isReadedHistoryRecord = true;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inflateSearchHistoryDataOnUIThread();
                    }
                });
            }

            @Override
            public void onReadFiled(String errorMsg) {
                LogUtils.e("onReadFiled: " + errorMsg);
            }
        });
    }

    private void inflateSearchHistoryDataOnUIThread() {
        rlSearchHistoryTitle.setVisibility(historySearchList.size() == 0 ? View.GONE : View.VISIBLE);
        llSearchHistory.setVisibility(historySearchList.size() > 0 ? View.VISIBLE : View.GONE);
        for (int i = 0; i < historySearchList.size() && llSearchHistory != null; i++) {
            SearchHistoryItemViewHolder viewHolder = new SearchHistoryItemViewHolder();
            viewHolder.setOnClickSearchHistoryItemListener(new SearchHistoryItemViewHolder.OnClickSearchHistoryItemListener() {
                @Override
                public void onClickHistorySearchItem(SearchResultBean.SearchResultItem searchResultItem) {
                    if (historySearchList == null) {
                        return;
                    }
                    MobclickAgent.onEvent(getContext(), "click_searchFragment_historyItem");
                    historySearchList.put(searchResultItem);
                    CommonUtil.startActivity(getContext(), SingleFundDetailActivity.class, searchResultItem.getFundresCode());
                }
            });
            llSearchHistory.addView(viewHolder.inflateData(historySearchList.get(i)));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("onDestory.....");
        if (historySearchList != null) {
            FileUtils.saveSearchHistoryList(historySearchList);
        }
    }


    private static final String TAG = "FundSearchFragment";

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ......;" + historySearchList.toString());
        if (historySearchList != null) {
            refreshHistorySearchList();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onResume: ......;" + historySearchList.toString());
    }

    private void getHotSearchData() {
        BaseBusiness.postRequest(new OnResponseListener<HotSearchBean>() {
            @Override
            public Observable<BaseBean<HotSearchBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                if (SPUtil.getInstance().isLogin())
                    paramMap.putLast("token", SPUtil.getInstance().getToken());
                return NetService.getNetService().getHotSearchList(paramMap);
            }

            @Override
            public void onResponse(BaseBean<HotSearchBean> result) {
                inflateHotSearchData(result);
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    private void inflateHotSearchData(final BaseBean<HotSearchBean> result) {
        if (result == null || result.getData() == null || result.getData().getHeats().isEmpty()) {
            tvIconRealTimeHotList.setVisibility(View.GONE);
            flowLayout.setVisibility(View.GONE);
            return;
        }
        if (result.getData() == null || result.getData().getHeats() == null || result.getData().getHeats().isEmpty()) {
            tvIconRealTimeHotList.setVisibility(View.GONE);
            flowLayout.setVisibility(View.GONE);
        } else {
            tvIconRealTimeHotList.setVisibility(View.VISIBLE);
            flowLayout.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < result.getData().getHeats().size(); i++) {
            final SearchResultBean.SearchResultItem item = result.getData().getHeats().get(i);
            TextView textView;
            if (i < 3) {
                textView = generateTextView(item.getFundname(), true);
            } else {
                textView = generateTextView(item.getFundname(), false);
            }
            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        historySearchList.put(item);
                        CommonUtil.startActivity(getContext(), SingleFundDetailActivity.class, item.getFundresCode());
                    }
                });
                flowLayout.addView(textView);
            }
        }
    }

    private int pageNo = 1;
    private int pageSize = 20;

    private void beginSearch(final boolean isRefresh) {
        if (TextUtils.isEmpty(etSearch.getText().toString().trim())) {
//            ToastUtils.showShort("请输入搜索内容");
            return;
        }
        pageNo = isRefresh ? 1 : pageNo + 1;
        if (isRefresh) {
            recyclerView.reset();
        }
        BaseBusiness.postRequest(new OnResponseListener<SearchResultBean>() {
            @Override
            public Observable<BaseBean<SearchResultBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                if (SPUtil.getInstance().isLogin())
                    paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.put("pageNo", pageNo + "");
                paramMap.put("rowsSize", pageSize + "");
                paramMap.putLast("kw", searchKeyWord);
                LogUtils.e("kw: " + searchKeyWord + " pageNo: " + pageNo);
                return NetService.getNetService().search(paramMap);
            }

            @Override
            public void onResponse(BaseBean<SearchResultBean> result) {
                LogUtils.e("onResponse: searchKeyWord: " + searchKeyWord + "  etText: " + etSearch.getText().toString());
                if (result != null && result.isSuccess()) {
//                    recyclerView.setVisibility(result.getData().getResults().isEmpty() ? View.GONE : View.VISIBLE);
                    llSearchEmpty.setVisibility(result.getData().getResults().isEmpty() ? View.VISIBLE : View.GONE);
                    if (!result.getData().getResults().isEmpty()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.refreshComplete();
                        LogUtils.e("onResponse: resultSize: " + result.getData().getResults().size());
                        if (isRefresh) {
                            recyclerView.scrollToPosition(0);
                            searchResultList.clear();
                        }
                        searchResultList.addAll(result.getData().getResults());
                        adapter.notifyDataSetChanged();
                        if (result.getData().getResults().size() < pageSize) {
                            LogUtils.e("No more loading.... kw: " + searchKeyWord + " ,pageNo: " + pageNo);
                            recyclerView.noMoreLoading();
                        }
                    } else {
                        pageNo = pageNo > 1 ? pageNo - 1 : 1;
                        String emptyTxt = "没有搜索到 " + searchKeyWord.trim();
                        SpannableString spannableString = new SpannableString(emptyTxt);
                        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#0084ff"));
                        spannableString.setSpan(span, "没有搜索到".length(), emptyTxt.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvSearchEmpty.setText(spannableString);
                    }
                }
            }

            @Override
            public void onError(String errorMsg) {
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                mDialogCancelListener.onCancel();
                dismiss();
                break;
            case R.id.iv_clear_search_history:  //清除搜索历史记录
                historySearchList.clear();
                FileUtils.saveSearchHistoryList(historySearchList);
                rlSearchHistoryTitle.setVisibility(View.GONE);
                llSearchHistory.setVisibility(View.GONE);
                break;
            case R.id.iv_search_content_clear:
                if (etSearch != null) {
                    etSearch.setText("");
                }
                llSearchEmpty.setVisibility(View.GONE);
                rlSearchHistoryTitle.setVisibility(historySearchList.size() == 0 ? View.GONE : View.VISIBLE);
                break;
        }
    }


    private TextView generateTextView(String content, boolean selected) {
        if (getContext() == null)
            return null;
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.select_hot_search_item);
        textView.setTextColor(Color.parseColor("#393b51"));
        textView.setPadding(DensityUtil.dip2px(15), DensityUtil.dip2px(8), DensityUtil.dip2px(15), DensityUtil.dip2px(8));
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setSelected(selected);
        if (selected) {
            Drawable drawable = getResources().getDrawable(R.drawable.icon_search_hot);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawablePadding(15);
            textView.setCompoundDrawables(drawable, null, null, null);
        }
        textView.setText(content);
        return textView;
    }


    MyCountDownTimer countDownTimer;

    class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            LogUtils.e("onTick: " + millisUntilFinished);
        }

        @Override
        public void onFinish() {
            LogUtils.e("onFinish...>>> beginSearch...");
            beginSearch(true);
        }

        //获取验证码失败，【重置倒计时，但是显示的是获取验证码，而不是重新获取验证码】
//        if (countDownTimer != null) {
//            countDownTimer.onFinish();
//            countDownTimer.cancel();
//        }
    }

    public interface DialogCancelListener{
        void onCancel();
    }

    public void setDialogCancelListener(DialogCancelListener dialogCancelListener){
        this.mDialogCancelListener = dialogCancelListener;
    }

}
