package com.test.xcamera.cameraclip;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.bean.SideKeyBean;
import com.test.xcamera.cameraclip.adapter.SideKeyAdapter;
import com.test.xcamera.home.GoUpActivity;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoGetSideKeyCallback;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: mz
 * Time:  2019/10/28
 */
public class CameraSideClipActivity extends MOBaseActivity{
    @BindView(R.id.rv_view)
    RecyclerView rvView;
    @BindView(R.id.left_iv_title)
    ImageView leftIvTitle;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
//    @BindView(R.id.rl_progress)
//    RelativeLayout rlProgress;
    @BindView(R.id.progress1)
    ProgressBar progressBar;
    @BindView(R.id.tv_todayVideoListActivity_content)
    TextView contentTextView;
    @BindView(R.id.tv_todayVideoListActivity_loadingContent)
    TextView loadingContentTextView;

    CommonDownloadDialog rlProgress;
    public SideKeyAdapter adapter;
    private ProgressDialog dialog;
    private OneKeyMakeVideoHelper oneKeyMakeVideoHelper;
    private DownloadVideoTempleteDataUtil downloadVideoTempleteDataUtil;
    private boolean loading = false;
    private boolean pullUp = false;
    private int mPageCount=0;

    @OnClick({R.id.left_iv_title, R.id.rl_progress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_iv_title:
                onBackPressed();
                break;
        }
    }

    @Override
    public int initView() {
        return R.layout.activity_camera_clip;
    }

    @Override
    public void initData() {
        oneKeyMakeVideoHelper = new OneKeyMakeVideoHelper(this, OneKeyMakeVideoHelper.MakeVideoType.SIDE_KEY);
        downloadVideoTempleteDataUtil = new DownloadVideoTempleteDataUtil();

        rlProgress = new CommonDownloadDialog(this);
        rlProgress.setBackKeyListener(new CommonDownloadDialog.BackKeyListener() {
            @Override
            public void onBack(Dialog mDialog) {
                if (rlProgress.isShowing()) {
//            ConnectionManager.getInstance().stopPlay(null);
                    Toast.makeText(CameraSideClipActivity.this, getResources().getString(R.string.cannotCancelWhenDownload), Toast.LENGTH_SHORT).show();
                }
            }
        });
        leftIvTitle.setImageResource(R.mipmap.backicon);
        tvMiddleTitle.setText(getResources().getString(R.string.wondelfulTime));
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_ff7700), PorterDuff.Mode.SRC_IN);
        contentTextView.setVisibility(View.GONE);
        initRecycleView();
        if (AccessoryManager.getInstance().mIsRunning) {
            getSideKeyVideo(true, 0);//获取精彩数据
        }
    }

    private void initRecycleView() {
        GridLayoutManager layoutManage = new GridLayoutManager(mContext, 2);
        rvView.setLayoutManager(layoutManage);
        adapter = new SideKeyAdapter(mContext);
        rvView.setAdapter(adapter);
        rvView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!loading  && newState== RecyclerView.SCROLL_STATE_IDLE && pullUp) {
                    //避免卡顿
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if(gridLayoutManager.findLastVisibleItemPosition() + 2 >= gridLayoutManager.getItemCount()){
                        mPageCount += 1;
                        getSideKeyVideo(false, mPageCount);
                    }
                }
            }
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                pullUp = dy > 0;
            }
        });
        adapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                for (int i = 0; i < adapter.mData.size(); i++) {
                    adapter.mData.get(i).setSelected(false);
                }
                adapter.mData.get(position).setSelected(true);
                adapter.notifyDataSetChanged();

                GoUpActivity.downloadSideKeyItem(mContext, adapter.mData.get(position), downloadVideoTempleteDataUtil, oneKeyMakeVideoHelper, rlProgress, loadingContentTextView);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AccessoryManager.getInstance().mIsRunning) {
            rvView.setVisibility(View.GONE);
            CameraToastUtil.show(getResourceToString(R.string.disconenct_usb), this);
        }
    }

    public void getSideKeyVideo(final boolean refresh, int pageIndex) {
        loading = true;
        rlProgress.showDialog(false, false);
        ConnectionManager.getInstance().getVideoSideKey(pageIndex, new MoGetSideKeyCallback() {
            @Override
            public void onSuccess(final List<SideKeyBean> sideKeyList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hiddenLoadingView();
                        //CameraToastUtil.show("获取侧面精彩推荐数据成功", CameraSideClipActivity.this);
                        LoggerUtils.printLog("获取侧面精彩推荐数据" + sideKeyList + "");
                        if (sideKeyList!=null) {
                            if(sideKeyList.size()>0){
                                adapter.updateData(refresh, sideKeyList);
                            }else {
                                Toast.makeText(CameraSideClipActivity.this, getResources().getString(R.string.loadToBottom), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }

            @Override
            public void onFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hiddenLoadingView();
                    }
                });
            }
        });
    }

    public void hiddenLoadingView() {
        loading = false;
        rlProgress.dismissDialog();
        loadingContentTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void disconnectedUSB() {
        super.disconnectedUSB();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(rlProgress!=null ){
                    rlProgress.dismissDialog();
                }
                CameraToastUtil.show(getResourceToString(R.string.disconenct_usb), mContext);
                CameraSideClipActivity.this.finish();
            }
        });
    }

}
