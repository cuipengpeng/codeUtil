package com.test.xcamera.home.activation;

import android.os.Handler;
import android.widget.ImageView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.home.ActivationHelper;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.widget.RingProgressBar;

import butterknife.BindView;

/**
 * Author: mz
 * Time:  2019/10/22
 */
public class ActivatingActivity extends MOBaseActivity implements ActivatingViewInterface {
    @BindView(R.id.iv_web)
    ImageView ivWeb;
    @BindView(R.id.iv_hard)
    ImageView ivHard;
    @BindView(R.id.progress)
    RingProgressBar progress;

    private ActivationHelper helper;
    @Override
    public int initView() {
        return R.layout.activity_activating;
    }

    @Override
    public void initData() {
      helper=new ActivationHelper(this,"ActivatingActivity",this);
      helper.setHandler(new Handler());
      helper.getActivateStatue();
    }


    @Override
    public ImageView getWebImage() {
        return ivWeb;
    }

    @Override
    public ImageView getIvHardImage() {
        return ivHard;
    }

    @Override
    public RingProgressBar getProgressBar() {
        return progress;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }


    @Override
    public void onBackPressed() {
        CameraToastUtil.show("解禁流程不能退出" ,mContext);
    }
}
