package com.caishi.chaoge.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.MessageNumBean;
import com.caishi.chaoge.request.MessageNumRequest;
import com.gyf.barlibrary.ImmersionBar;

public class MessageActivity extends BaseActivity {

    private RelativeLayout rl_msg_fans, rl_msg_praise, rl_msg_comments, rl_msg_systemMsg;
    private TextView tv_msg_fansNum, tv_msg_praiseNum, tv_msg_commentsNum;

    @Override
    public int bindLayout() {
        return R.layout.activity_message;
    }

    @Override
    public void initView(View view) {
        ImmersionBar.with(this).titleBar(R.id.view_msg)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
        setBaseTitle("消息", false);
        rl_msg_fans = $(R.id.rl_msg_fans);
        rl_msg_praise = $(R.id.rl_msg_praise);
        rl_msg_comments = $(R.id.rl_msg_comments);
        rl_msg_systemMsg = $(R.id.rl_msg_systemMsg);
        tv_msg_fansNum = $(R.id.tv_msg_fansNum);
        tv_msg_praiseNum = $(R.id.tv_msg_praiseNum);
        tv_msg_commentsNum = $(R.id.tv_msg_commentsNum);

    }

    @Override
    public void doBusiness() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        MessageNumRequest.newInstance(this).getMessageNum(getCGUserId(), new BaseRequestInterface<MessageNumBean>() {
            @Override
            public void success(int state, String msg, MessageNumBean messageNumBean) {
                tv_msg_fansNum.setVisibility(messageNumBean.newFans != 0 ? View.VISIBLE : View.GONE);
                tv_msg_fansNum.setText(messageNumBean.newFans + "");
                tv_msg_praiseNum.setVisibility(messageNumBean.newlike != 0 ? View.VISIBLE : View.GONE);
                tv_msg_praiseNum.setText(messageNumBean.newlike + "");
                tv_msg_commentsNum.setVisibility(messageNumBean.newComment != 0 ? View.VISIBLE : View.GONE);
                tv_msg_commentsNum.setText(messageNumBean.newComment + "");
            }

            @Override
            public void error(int state, String msg) {

            }
        });
    }

    @Override
    public void setListener() {
        rl_msg_fans.setOnClickListener(this);
        rl_msg_praise.setOnClickListener(this);
        rl_msg_comments.setOnClickListener(this);
        rl_msg_systemMsg.setOnClickListener(this);
    }

    @Override
    public void widgetClick(View v) {
        Bundle bundle = new Bundle();
        String title = "";
        int pageFlag = -1;
        switch (v.getId()) {
            case R.id.rl_msg_fans:
                title = "粉丝";
                pageFlag = 0;
                break;
            case R.id.rl_msg_praise:
                title = "赞";
                pageFlag = 1;
                break;
            case R.id.rl_msg_comments:
                title = "评论";
                pageFlag = 2;
                break;
            case R.id.rl_msg_systemMsg:
                title = "系统通知";
                pageFlag = 3;
                break;
        }
        bundle.putString("title", title);
        bundle.putInt("pageFlag", pageFlag);
        startActivity(MessageDetailsActivity.class, bundle);
    }
}
