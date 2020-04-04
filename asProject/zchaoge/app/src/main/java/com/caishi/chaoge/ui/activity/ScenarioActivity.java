package com.caishi.chaoge.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.ClassListBean;
import com.caishi.chaoge.bean.ScenarioBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.ui.adapter.ScenarioPagerAdapter;
import com.caishi.chaoge.ui.adapter.SearchResultAdapter;
import com.caishi.chaoge.ui.adapter.SelectTempletFragmentPagerAdapter;
import com.caishi.chaoge.ui.fragment.ModelScenarioFragment;
import com.caishi.chaoge.ui.fragment.ScenarioFragment;
import com.caishi.chaoge.ui.widget.ClearEditText;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.ui.widget.dialog.SYDialog;
import com.caishi.chaoge.ui.widget.refreshlayout.AutoLoadMoreRecyclerView;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.StringUtil;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;


public class ScenarioActivity extends BaseUILocalDataActivity {
    @BindView(R.id.ll_scenarioActivity_searchBar)
    LinearLayout searchBarLinearLayout;
    @BindView(R.id.ll_scenarioActivity_content)
    LinearLayout contentLinearLayout;
    @BindView(R.id.rv_scenarioActivity_searchReesultList)
    AutoLoadMoreRecyclerView searchReesultListRecyclerView;
    @BindView(R.id.ll_scenarioActivity_netwrokError)
    LinearLayout netwrokErrorLinearLayout;
    @BindView(R.id.btn_networkError_refresh)
    Button networkErrorRefreshButon;
    @BindView(R.id.btn_scenarioActivity_noScenario)
    Button noScenarioButon;
    @BindView(R.id.ll_scenarioActivity_noData)
    LinearLayout noDataLinearLayout;
    @BindView(R.id.tv_scenarioActivity_cancelSearch)
    TextView cancelSearchTextView;
    @BindView(R.id.et_scenarioActivity_search)
    ClearEditText searchEditText;
    @BindView(R.id.vp_scenario_list)
    ViewPager vp_scenario_list;
    @BindView(R.id.stl_scenarioActivity_tabLayout)
    SlidingTabLayout stl_scenarioActivity_tabLayout;

    private SelectTempletFragmentPagerAdapter scenarioPagerAdapter;
    private SearchResultAdapter searchResultAdapter;
    private InputMethodManager imm;
    private String content = "";
    private ArrayList<Fragment> fragmentList = new ArrayList<>();;
    public static String KEY_OF_SCENERIO_TYPE="scenerioTypeKey";
    public static String KEY_OF_SHOW_NO_SCENERIO="showNoScenerioKey";
    public static String KEY_OF_SCENERIO_RESULT_DATA="scenerioResultDataKey";
    private String mScenerioType = "";
    private ScenarioBean scenarioBean;
    private boolean showNoScenatio = true;

    @OnClick({R.id.tv_scenarioActivity_cancelSearch, R.id.et_scenarioActivity_search, R.id.btn_networkError_refresh,
                R.id.btn_scenarioActivity_noScenario})
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.tv_scenarioActivity_cancelSearch:
                contentLinearLayout.setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                cancelSearchTextView.setVisibility(View.GONE);
                searchReesultListRecyclerView.setVisibility(View.GONE);
                noDataLinearLayout.setVisibility(View.GONE);
                netwrokErrorLinearLayout.setVisibility(View.GONE);
                searchEditText.setText("");
                break;
            case R.id.et_scenarioActivity_search:
                showSearchPage();
                break;
            case R.id.btn_scenarioActivity_noScenario:
                Intent intent = new Intent();
                intent.putExtra(ScenarioActivity.KEY_OF_SCENERIO_RESULT_DATA, scenarioBean);
                mContext.setResult(RESULT_OK, intent);
                mContext.finish();
                break;
            case R.id.btn_networkError_refresh:
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                searchScenario(true, searchEditText.getText().toString(), 0, "UP");
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "剧本";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_scenario;
    }

    @Override
    public void initPageData() {
        v_base_statusBar.setBackgroundColor(Color.WHITE);
        baseTitleBarRelativeLayout.setBackgroundColor(Color.WHITE);
        baseBackImageView.setBackgroundResource(R.drawable.ic_cancel);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) baseBackImageView.getLayoutParams();
        layoutParams.height = DisplayMetricsUtil.dip2px(this, 15);
        layoutParams.width = DisplayMetricsUtil.dip2px(this, 15);
        baseBackImageView.setLayoutParams(layoutParams);

        cancelSearchTextView.setVisibility(View.GONE);
        searchReesultListRecyclerView.setVisibility(View.GONE);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        searchReesultListRecyclerView.setLayoutManager(linearLayoutManager);
        searchResultAdapter = new SearchResultAdapter(mContext);
        searchReesultListRecyclerView.setAdapter(searchResultAdapter);

        scenarioPagerAdapter = new SelectTempletFragmentPagerAdapter(getSupportFragmentManager());
        vp_scenario_list.setAdapter(scenarioPagerAdapter);

        mScenerioType = getIntent().getStringExtra(KEY_OF_SCENERIO_TYPE);
        showNoScenatio = getIntent().getBooleanExtra(KEY_OF_SHOW_NO_SCENERIO, true);
        if(showNoScenatio){
            noScenarioButon.setVisibility(View.VISIBLE);
        }else {
            noScenarioButon.setVisibility(View.GONE);
        }
        if(StringUtil.isNull(mScenerioType)){
            getScenarioList();
        }else{
            searchBarLinearLayout.setVisibility(View.GONE);
            stl_scenarioActivity_tabLayout.setVisibility(View.GONE);
            fragmentList.add(ModelScenarioFragment.newInstance(mScenerioType));
            scenarioPagerAdapter.upateData(true, fragmentList);
            vp_scenario_list.setCurrentItem(0);
        }

        baseBackRelativeLayout.setFocusable(true);
        baseBackRelativeLayout.setFocusableInTouchMode(true);
        baseBackRelativeLayout.requestFocus();  // 初始不让EditText得焦点
        baseBackRelativeLayout.requestFocusFromTouch();
        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showSearchPage();
                }
            }
        });
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//搜索按键action
                    content = searchEditText.getText().toString();
                    if (TextUtils.isEmpty(content)) {
                        Toast.makeText(ScenarioActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    if (content.equals("chaoge6")) {//切换环境的暗门   liugebaozichibuwan666
                        showSwitchIPDialog();

                        return true;
                    }
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                    searchScenario(true, content, 0, "UP");
                    return true;
                }
                return false;
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchEditText.setClearIconVisible(s.length() > 0);
                if (s.length() <= 0) {
                    searchResultAdapter.upateData(true, new ArrayList<ScenarioBean>());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchReesultListRecyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                long targetTime = searchResultAdapter.mDataList.get(searchResultAdapter.mDataList.size() - 1).targetTime;
                searchScenario(false, content, targetTime, "UP");
            }
        });
    }

    /**
     * 获取剧本列表
     */
    private void getScenarioList() {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("type", "script");

        HttpRequest.post(true , HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.GET_CLASS_LIST, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                List<ClassListBean> classListBeanList = gson.fromJson(response, new TypeToken<List<ClassListBean>>(){}.getType());
                ArrayList<String> strings = new ArrayList<>();
                for (int i = 0; i < classListBeanList.size(); i++) {
                    fragmentList.add(ScenarioFragment.newInstance(0, classListBeanList.get(i).num));
                    strings.add(classListBeanList.get(i).name);
                }
//                ScenarioPagerAdapter scenarioPagerAdapter = new ScenarioPagerAdapter(getSupportFragmentManager(), fragmentList);
//                vp_scenario_list.setAdapter(scenarioPagerAdapter);
                scenarioPagerAdapter.upateData(true, fragmentList);
                vp_scenario_list.setCurrentItem(0);
                vp_scenario_list.setOffscreenPageLimit(classListBeanList.size());
                stl_scenarioActivity_tabLayout.setViewPager(vp_scenario_list, strings.toArray(new String[strings.size()]));
            }

            @Override
            public void onFailure(String t) {
            }
        });
    }

    private void showSearchPage() {
        cancelSearchTextView.setVisibility(View.VISIBLE);
        searchReesultListRecyclerView.setVisibility(View.VISIBLE);
        contentLinearLayout.setVisibility(View.GONE);
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_FORCED); //强制显示键盘
    }

    /**
     * 搜素剧本
     * @param refresh
     * @param content
     * @param startTime
     * @param direction
     */
    private void searchScenario(final boolean refresh, String content, long startTime, String direction) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("content", content);
        paramsMap.put("pageSize", 10 + "");
        paramsMap.put("since", startTime + "");
        paramsMap.put("slipTyp0e", direction);

        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.SEARCH_CONTENT, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                searchReesultListRecyclerView.loadMoreComplete();

                Gson gson = new Gson();
                List<ScenarioBean> scenarioBeanList = gson.fromJson(response, new TypeToken<List<ScenarioBean>>(){}.getType());
                if (refresh && (scenarioBeanList == null || scenarioBeanList.size() <= 0)) {
                    noDataLinearLayout.setVisibility(View.VISIBLE);
                    netwrokErrorLinearLayout.setVisibility(View.GONE);
                    searchReesultListRecyclerView.setVisibility(View.GONE);
                } else {
                    searchReesultListRecyclerView.setVisibility(View.VISIBLE);
                    noDataLinearLayout.setVisibility(View.GONE);
                    netwrokErrorLinearLayout.setVisibility(View.GONE);
                    if (scenarioBeanList.size() > 0) {
                        searchResultAdapter.upateData(refresh, scenarioBeanList);
                    } else {
                        searchReesultListRecyclerView.noMoreLoading();
                    }
                }
            }

            @Override
            public void onFailure(String t) {
                searchReesultListRecyclerView.loadMoreComplete();
                netwrokErrorLinearLayout.setVisibility(View.VISIBLE);
                noDataLinearLayout.setVisibility(View.GONE);
                searchReesultListRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void showSwitchIPDialog() {
        new SYDialog.Builder(mContext)
                .setDialogView(R.layout.dialog_switch_ip)
                .setWindowBackgroundP(0.5f)
                .setScreenWidthP(0.7f)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setCancelableOutSide(true)
                .setAnimStyle(R.style.AnimUp)
                .setBuildChildListener(new IDialog.OnBuildListener() {
                    @Override
                    public void onBuildChildView(final IDialog dialog, View view, int layoutRes, FragmentManager fragmentManager) {
                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add("测试服");
                        arrayList.add("正式服");
                        ListView lv_switchIp = view.findViewById(R.id.lv_switchIp);
                        lv_switchIp.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_expandable_list_item_1, arrayList));
                        lv_switchIp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                switch (position) {
                                    case 0:
                                        RequestURL.BASE_URL = RequestURL.BASE_URL_TEST;
                                        RequestURL.BASE_URL_SHARE = RequestURL.BASE_URL_SHARE_TEST;
                                        RequestURL.BASE_URL_V2 = RequestURL.BASE_URL_V2_TEST;
                                        break;
                                    case 1:
                                        RequestURL.BASE_URL = RequestURL.BASE_URL_ONLINE;
                                        RequestURL.BASE_URL_SHARE = RequestURL.BASE_URL_SHARE_ONLINE;
                                        RequestURL.BASE_URL_V2 = RequestURL.BASE_URL_V2_ONLINE;
                                        break;
                                }
                                HttpRequest.APP_INTERFACE_WEB_URL = RequestURL.BASE_URL + "/";
                                RequestURL.SHARE_VIDEO = RequestURL.BASE_URL_SHARE + "static/share/indexVideo.html";
                                RequestURL.SHARE_HOME_PAGE = RequestURL.BASE_URL_SHARE + "static/share/Individual.html";
                                SPUtils.clearLoginInfo(ScenarioActivity.this);
                                finish();


                            }
                        });

                    }
                }).show();
    }
}
