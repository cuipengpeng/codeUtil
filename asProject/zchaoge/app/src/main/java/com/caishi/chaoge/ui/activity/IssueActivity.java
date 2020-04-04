package com.caishi.chaoge.ui.activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.bean.EventBusBean.IssueBean;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.Utils;
import com.gyf.barlibrary.ImmersionBar;

import java.io.File;

public class IssueActivity extends BaseActivity {


    private TextView tv_issue_textNum;
    private EditText ed_issue_inputInfo;
    private ImageView img_issue_image;
    private String bitmapPath;
    private IssueBean issueBean;
    private String musicId;
    private String scriptId;
    private String backGroundId;
    private String modelId;
    private int classFlag;//区分过来的页面 0 文字朗读 1视频配音


    @Override
    public void initBundle(Bundle bundle) {
        bitmapPath = bundle.getString("firstSnapshot", "");
        musicId = bundle.getString("musicId", "");
        modelId = bundle.getString("modelId", "");
        scriptId = bundle.getString("scriptId", "");
        backGroundId = bundle.getString("backGroundId", "");
        classFlag = bundle.getInt("classFlag");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_issue;
    }

    @Override
    public void initView(final View view) {
        ImmersionBar.with(this).titleBar(R.id.view_issue)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
        setBaseTitle("发布", Color.WHITE, "发布", Color.WHITE, false);
        issueBean = new IssueBean();
        ed_issue_inputInfo = $(R.id.ed_issue_inputInfo);
        tv_issue_textNum = $(R.id.tv_issue_textNum);
        img_issue_image = $(R.id.img_issue_image);
        GlideUtil.loadImg(Uri.fromFile(new File(bitmapPath)), img_issue_image);
        issueBean.musicId = musicId;
        issueBean.scriptId = scriptId;
        issueBean.backGroundId = backGroundId;
        issueBean.cover = bitmapPath;
        issueBean.modelId = modelId;
        issueBean.userId = getCGUserId();
    }

    @Override
    public void setListener() {
        ed_issue_inputInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.length() + "/50字";
                tv_issue_textNum.setText(str);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void doBusiness() {
        GlideUtil.loadRoundImg(bitmapPath, img_issue_image);
    }

    @Override
    public void onSaveClick(View v) {
        super.onSaveClick(v);
        issueBean.content = ed_issue_inputInfo.getText().toString().trim();
        if (isLogin()) {
            Utils.umengStatistics(mContext, classFlag == 0 ? "lz0005" : "lz0009");//0文字朗读  1视频配音
            MainActivity.open(this, issueBean);
        } else {
            LoginActivity.open(this, -1);
        }
    }

    @Override
    public void widgetClick(View v) {


    }


}

