package com.caishi.chaoge.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.CityBean;
import com.caishi.chaoge.ui.adapter.CityAdapter;
import com.caishi.chaoge.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SelectProvinceActivity extends BaseUILocalDataActivity {
    @BindView(R.id.rv_selectProvince_list)
    RecyclerView rv_production_list;

    public CityBean cityBeanList;
    public static SelectProvinceActivity Context;
    private CityAdapter cityAdapter;
    private List<String> provinceList = new ArrayList<>();
    private List<String> cityList = new ArrayList<>();
    public static final int PROVINCE_LIST = 0;
    public static final int CITY_LIST = 1;
    public int mCurrentList = PROVINCE_LIST;

    @Override
    protected String getPageTitle() {
        return "选择省份";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_select_province;
    }

    @Override
    protected void initPageData() {
        Context = this;
        cityBeanList = new Gson().fromJson(Utils.getJson("CityJson.json", mContext), CityBean.class);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_production_list.setLayoutManager(linearLayoutManager);
        cityAdapter = new CityAdapter(this);
        rv_production_list.setAdapter(cityAdapter);
        cityAdapter.setOnProvinceItemClickListener(new CityAdapter.OnProvinceItemClickListener() {
            @Override
            public void onClick(String provinceName) {
                baseTitleTextView.setText(provinceName);
            }
        });
        for (CityBean.Provinces provinces : cityBeanList.provinces) {
            provinceList.add(provinces.name);
        }
        cityAdapter.upateData(true, provinceList);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mCurrentList ==CITY_LIST) {
            cityAdapter.upateData(true, provinceList);
            mCurrentList = PROVINCE_LIST;
            baseTitleTextView.setText("选择省份");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
