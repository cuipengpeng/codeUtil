package com.caishi.chaoge.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.ClassListBean;
import com.caishi.chaoge.bean.EventBusBean.EventMusicBean;
import com.caishi.chaoge.bean.MusicBean;
import com.caishi.chaoge.bean.SelectMusicTitleBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.ui.adapter.SelectTempletFragmentPagerAdapter;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.MediaPlayerUtil;
import com.caishi.chaoge.ui.adapter.SelectMusicAdapter;
import com.caishi.chaoge.ui.fragment.SelectMusicFragment;
import com.caishi.chaoge.ui.widget.ClearEditText;
import com.caishi.chaoge.ui.widget.refreshlayout.AutoLoadMoreRecyclerView;
import com.caishi.chaoge.utils.DownloadUtil;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectMusicActivity extends BaseUILocalDataActivity {

    @BindView(R.id.tv_selectMusicActivity_cancelSearch)
    TextView tvSelectMusicActivityCancelSearch;
    @BindView(R.id.iv_selectMusicActivity_icon)
    ImageView ivSelectMusicActivityIcon;
    @BindView(R.id.et_selectMusicActivity_search)
    ClearEditText etSelectMusicActivitySearch;
    @BindView(R.id.rv_selectMusicActivity_searchReesultList)
    AutoLoadMoreRecyclerView searchResultListRecyclerView;
    @BindView(R.id.iv_basePage_noData)
    ImageView ivBasePageNoData;
    @BindView(R.id.tv_basePage_noData)
    TextView tvBasePageNoData;
    @BindView(R.id.iv_networkError)
    ImageView ivNetworkError;
    @BindView(R.id.tv_networkError)
    TextView tvNetworkError;
    @BindView(R.id.ll_selectMusicActivity_netwrokError)
    LinearLayout netwrokErrorLinearLayout;
    @BindView(R.id.ll_selectMusicActivity_noData)
    LinearLayout noDataLinearLayout;
    @BindView(R.id.vp_selectMusicActivity_musicList)
    public ViewPager vpSelectMusicActivityMusicList;
    @BindView(R.id.stl_selectMusicActivity_tabLayout)
    SlidingTabLayout stl_selectMusicActivity_tabLayout;

    private SelectMusicAdapter searchResultAdapter;
    private List<SelectMusicTitleBean> mTitleDataList = new ArrayList<>();
    public SelectMusicTitleBean selectMusicTitleBean;
    private SelectMusicFragment selectMusicFragment;
    private InputMethodManager imm;
    private boolean searchMode = false;
    private String searchContent = "";
    public MediaPlayerUtil mediaPlayerManager;
    private int defaultSelectedIndex = 0;
    public static final String KEY_OF_MUSIC_RESULT = "musicResultKey";
    private EventMusicBean musicBean;

    private SelectTempletFragmentPagerAdapter selectTempletPagerAdapter;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();


    @OnClick({R.id.tv_selectMusicActivity_cancelSearch, R.id.et_selectMusicActivity_search, R.id.btn_networkError_refresh,
            R.id.tv_base_rightMenu})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.tv_selectMusicActivity_cancelSearch:
                showMusicCategoryPage();
                break;
            case R.id.et_selectMusicActivity_search:
                showSearchPage();
                break;
            case R.id.tv_base_rightMenu:
                Intent intent = new Intent();
                intent.putExtra(SelectMusicActivity.KEY_OF_MUSIC_RESULT, musicBean);
                mContext.setResult(mContext.RESULT_OK, intent);
                mContext.finish();
                break;
            case R.id.btn_networkError_refresh:
                imm.hideSoftInputFromWindow(etSelectMusicActivitySearch.getWindowToken(), 0);
                if (searchMode) {
                    searchMusic(true, etSelectMusicActivitySearch.getText().toString(), -1, "UP");
                } else {
                    for (int i = 0; i < mTitleDataList.size(); i++) {
                        if (selectMusicTitleBean.getMusicType() == mTitleDataList.get(i).getMusicType()) {
                            vpSelectMusicActivityMusicList.setCurrentItem(i);
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "选音乐";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_select_music;
    }

    @Override
    protected void initPageData() {
        v_base_statusBar.setBackgroundColor(Color.WHITE);
        baseTitleBarRelativeLayout.setBackgroundColor(Color.WHITE);
        baseBackImageView.setBackgroundResource(R.drawable.ic_cancel);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) baseBackImageView.getLayoutParams();
        layoutParams.height = DisplayMetricsUtil.dip2px(this, 15);
        layoutParams.width = DisplayMetricsUtil.dip2px(this, 15);
        baseBackImageView.setLayoutParams(layoutParams);

        baseRightMenuTextView.setVisibility(View.VISIBLE);
        baseRightMenuTextView.setText("无配乐");
        baseRightMenuTextView.setTextColor(Color.parseColor("#FE5074"));
        baseRightMenuTextView.setBackgroundResource(R.drawable.circle_corner_pink_border_normal_14dp);
        RelativeLayout.LayoutParams layoutParams02 = (RelativeLayout.LayoutParams) baseRightMenuTextView.getLayoutParams();
        layoutParams02.width = DisplayMetricsUtil.dip2px(this, 71);
        layoutParams02.height = DisplayMetricsUtil.dip2px(this, 28);
        baseRightMenuTextView.setLayoutParams(layoutParams02);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        netwrokErrorLinearLayout.setBackgroundColor(Color.WHITE);
        noDataLinearLayout.setBackgroundColor(Color.WHITE);
        etSelectMusicActivitySearch.mClearDrawable = getResources().getDrawable(R.drawable.ic_delete_black);
        etSelectMusicActivitySearch.mClearDrawable.setBounds(0, 0, etSelectMusicActivitySearch.mClearDrawable.getIntrinsicWidth(), etSelectMusicActivitySearch.mClearDrawable.getIntrinsicHeight());
        mediaPlayerManager = MediaPlayerUtil.newInstance(mContext);

        selectTempletPagerAdapter = new SelectTempletFragmentPagerAdapter(getSupportFragmentManager());
        vpSelectMusicActivityMusicList.setAdapter(selectTempletPagerAdapter);

        initMusicTitleAndMusicList();

        showMusicCategoryPage();

        LinearLayoutManager searchLlinearLayoutManager = new LinearLayoutManager(mContext);
        searchLlinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        searchResultListRecyclerView.setLayoutManager(searchLlinearLayoutManager);
        searchResultAdapter = new SelectMusicAdapter(mContext);
        searchResultListRecyclerView.setAdapter(searchResultAdapter);

        etSelectMusicActivitySearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showSearchPage();
                }
            }
        });
        etSelectMusicActivitySearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//搜索按键action
                    searchContent = etSelectMusicActivitySearch.getText().toString();
                    if (TextUtils.isEmpty(searchContent)) {
                        Toast.makeText(mContext, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    imm.hideSoftInputFromWindow(etSelectMusicActivitySearch.getWindowToken(), 0);
                    searchMusic(true, searchContent, 0, "UP");
                    return true;
                }
                return false;
            }
        });
        etSelectMusicActivitySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etSelectMusicActivitySearch.setClearIconVisible(s.length() > 0);
                if (s.length() <= 0) {

                    baseBackRelativeLayout.setFocusable(true);
                    baseBackRelativeLayout.setFocusableInTouchMode(true);
                    baseBackRelativeLayout.requestFocus();  // 初始不让EditText得焦点
                    baseBackRelativeLayout.requestFocusFromTouch();

                    searchResultAdapter.upateData(true, new ArrayList<MusicBean>());
                    searchResultListRecyclerView.reset();
                    if (null != mediaPlayerManager && mediaPlayerManager.isPlay()) {
                        mediaPlayerManager.pause();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchResultListRecyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                long targetTime = searchResultAdapter.mDataList.get(searchResultAdapter.mDataList.size() - 1).targetTime;
                searchMusic(false, searchContent, targetTime, "UP");
            }
        });
    }

    /**
     * 初始化音乐标题和第一列的音乐列表
     */
    private void initMusicTitleAndMusicList() {
        getMusicTitleList();
    }

    /**
     * 获取列表
     */
    private void getMusicTitleList() {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("type", "music");
        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.GET_CLASS_LIST, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                List<ClassListBean> classListBeans  = gson.fromJson(response, new TypeToken<List<ClassListBean>>(){}.getType());
                ArrayList<String> strings = new ArrayList<>();
                if (classListBeans != null && classListBeans.size() >= 1) {
                    for (int i = 0; i < classListBeans.size(); i++) {
                        SelectMusicTitleBean selectMusicTitleBean = new SelectMusicTitleBean();
                        selectMusicTitleBean.setMusicType(Integer.parseInt(classListBeans.get(i).num));
                        selectMusicTitleBean.setSelected(i == defaultSelectedIndex);
                        selectMusicTitleBean.setNormalDrawable(classListBeans.get(i).unSelectUrl);
                        selectMusicTitleBean.setSelectedDrawable(classListBeans.get(i).selectUrl);
                        mTitleDataList.add(selectMusicTitleBean);
                        strings.add(classListBeans.get(i).name);
                    }

                    for (int i = 0; i < mTitleDataList.size(); i++) {
                        fragmentList.add(SelectMusicFragment.newInstance(mTitleDataList.get(i).getMusicType() + ""));
                    }
                    selectTempletPagerAdapter.upateData(true, fragmentList);
                    vpSelectMusicActivityMusicList.setCurrentItem(defaultSelectedIndex);
                    selectMusicFragment = (SelectMusicFragment) fragmentList.get(defaultSelectedIndex);

                    selectMusicTitleBean = mTitleDataList.get(defaultSelectedIndex);
                    stl_selectMusicActivity_tabLayout.setViewPager(vpSelectMusicActivityMusicList, strings.toArray(new String[strings.size()]));
                }

            }

            @Override
            public void onFailure(String t) {
            }
        });
    }

    /**
     * 搜索音乐
     *
     * @param refresh
     * @param content
     * @param startTime
     * @param direction
     */
    private void searchMusic(final boolean refresh, String content, long startTime, String direction) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("content", content);
        paramsMap.put("pageSize", 10 + "");
        paramsMap.put("since", startTime + "");
        paramsMap.put("slipType", direction);

        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.SEARCH_MUSIC, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                searchResultListRecyclerView.loadMoreComplete();
                if (refresh) {
                    mediaPlayerManager.pause();
                    searchResultListRecyclerView.reset();
                }

                Gson gson = new Gson();
                List<MusicBean> musicBeanList = gson.fromJson(response, new TypeToken<List<MusicBean>>(){}.getType());
                if (refresh && (musicBeanList == null || musicBeanList.size() <= 0)) {
                    noDataLinearLayout.setVisibility(View.VISIBLE);
                    netwrokErrorLinearLayout.setVisibility(View.GONE);
                    searchResultListRecyclerView.setVisibility(View.GONE);
                } else {
                    searchResultListRecyclerView.setVisibility(View.VISIBLE);
                    noDataLinearLayout.setVisibility(View.GONE);
                    netwrokErrorLinearLayout.setVisibility(View.GONE);
                    if (musicBeanList.size() > 0) {
                        searchResultAdapter.upateData(refresh, musicBeanList);
                    } else {
                        searchResultListRecyclerView.noMoreLoading();
                    }
                }
            }

            @Override
            public void onFailure(String t) {
                mediaPlayerManager.pause();
                searchResultListRecyclerView.loadMoreComplete();
                netwrokErrorLinearLayout.setVisibility(View.VISIBLE);
                noDataLinearLayout.setVisibility(View.GONE);
                searchResultListRecyclerView.setVisibility(View.GONE);
            }
        });
    }


    /**
     * 显示搜索页面
     */
    private void showSearchPage() {
        if (null != mediaPlayerManager && mediaPlayerManager.isPlay()) {
            mediaPlayerManager.pause();
            for (int i = 0; i < selectMusicFragment.selectMusicListAdapter.mDataList.size(); i++) {
                selectMusicFragment.selectMusicListAdapter.mDataList.get(i).isClick = false;
            }
            selectMusicFragment.selectMusicListAdapter.notifyDataSetChanged();
        }

        searchMode = true;
        tvSelectMusicActivityCancelSearch.setVisibility(View.VISIBLE);
        searchResultListRecyclerView.setVisibility(View.VISIBLE);
        stl_selectMusicActivity_tabLayout.setVisibility(View.GONE);
        vpSelectMusicActivityMusicList.setVisibility(View.GONE);
        noDataLinearLayout.setVisibility(View.GONE);
        netwrokErrorLinearLayout.setVisibility(View.GONE);
        imm.showSoftInput(etSelectMusicActivitySearch, InputMethodManager.SHOW_FORCED); //强制显示键盘
    }

    /**
     * 显示音乐分类页面
     */
    private void showMusicCategoryPage() {
        if (null != mediaPlayerManager && mediaPlayerManager.isPlay()) {
            mediaPlayerManager.pause();
            for (int i = 0; i < searchResultAdapter.mDataList.size(); i++) {
                searchResultAdapter.mDataList.get(i).isClick = false;
            }
            searchResultAdapter.notifyDataSetChanged();
        }

        baseBackRelativeLayout.setFocusable(true);
        baseBackRelativeLayout.setFocusableInTouchMode(true);
        baseBackRelativeLayout.requestFocus();  // 初始不让EditText得焦点
        baseBackRelativeLayout.requestFocusFromTouch();

        searchMode = false;
        tvSelectMusicActivityCancelSearch.setVisibility(View.GONE);
        searchResultListRecyclerView.setVisibility(View.GONE);
        stl_selectMusicActivity_tabLayout.setVisibility(View.VISIBLE);
        vpSelectMusicActivityMusicList.setVisibility(View.VISIBLE);
        noDataLinearLayout.setVisibility(View.GONE);
        netwrokErrorLinearLayout.setVisibility(View.GONE);
        imm.hideSoftInputFromWindow(etSelectMusicActivitySearch.getWindowToken(), 0); //强制隐藏键盘
        etSelectMusicActivitySearch.setText("");
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayerManager.isPrepare && !mediaPlayerManager.isPlay() && !mediaPlayerManager.mPauseByHand) {
            mediaPlayerManager.continuePlay();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayerManager.isPlay()) {
            mediaPlayerManager.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadUtil.getInstance().threadPoolExecutor.shutdownNow();
        mediaPlayerManager.releaseMediaPlayer();
        mediaPlayerManager = null;
    }

    public static void open(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SelectMusicActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

}
