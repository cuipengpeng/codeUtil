package com.caishi.chaoge.ui.fragment;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseApplication;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.EventBusBean.IssueBean;
import com.caishi.chaoge.bean.MessageNumBean;
import com.caishi.chaoge.bean.MineDataBean;
import com.caishi.chaoge.bean.ShareBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.listener.OnUploadFileListener;
import com.caishi.chaoge.manager.UploadManager;
import com.caishi.chaoge.manager.VideoEditManager;
import com.caishi.chaoge.request.MessageNumRequest;
import com.caishi.chaoge.ui.activity.MainActivity;
import com.caishi.chaoge.ui.activity.MessageActivity;
import com.caishi.chaoge.ui.activity.MyFansActivity;
import com.caishi.chaoge.ui.activity.MyFollowsActivity;
import com.caishi.chaoge.ui.activity.SettingActivity;
import com.caishi.chaoge.ui.adapter.MyShareAdapter;
import com.caishi.chaoge.ui.adapter.SelectTempletFragmentPagerAdapter;
import com.caishi.chaoge.ui.dialog.HomeDialog;
import com.caishi.chaoge.ui.widget.CircleImageView;
import com.caishi.chaoge.ui.widget.CircleProgressbar;
import com.caishi.chaoge.ui.widget.MineDialog;
import com.caishi.chaoge.ui.widget.dialog.DialogUtil;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.StatusBarUtil;
import com.caishi.chaoge.utils.Utils;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import net.moyokoo.diooto.Diooto;
import net.moyokoo.diooto.DragDiootoView;
import net.moyokoo.diooto.config.DiootoConfig;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.model.TResult;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.panpf.sketch.SketchImageView;

public class MyFragment extends Fragment implements TakePhoto.TakeResultListener {

    @BindView(R.id.tv_mine_name)
    TextView tv_mine_name;
    @BindView(R.id.tv_mine_userName)
    TextView tv_mine_userName;
    @BindView(R.id.tv_mine_id)
    TextView tv_mine_id;
    @BindView(R.id.ll_mine_flag)
    LinearLayout ll_mine_flag;
    @BindView(R.id.tv_mine_sign)
    TextView tv_mine_sign;
    @BindView(R.id.tv_itemHome_praise)
    TextView tv_itemHome_praise;
    @BindView(R.id.tv_itemHome_comment)
    TextView tv_itemHome_comment;
    @BindView(R.id.tv_itemHome_share)
    TextView tv_itemHome_share;
    @BindView(R.id.ll_itemHome_praise)
    LinearLayout ll_itemHome_praise;
    @BindView(R.id.ll_mine_layout)
    LinearLayout ll_mine_layout;
    @BindView(R.id.rl_myActivity_bg)
    RelativeLayout rl_myActivity_bg;
    @BindView(R.id.iv_myActivity_bg)
    ImageView iv_myActivity_bg;
    @BindView(R.id.img_mine_userPhoto)
    CircleImageView img_mine_userPhoto;
    @BindView(R.id.iv_myActivity_closeShareDialog)
    ImageView closeShareDialogImageView;
    @BindView(R.id.rl_mine_layout)
    RelativeLayout rl_mine_layout;
    @BindView(R.id.ctl_mine_layout)
    CollapsingToolbarLayout ctl_mine_layout;
    @BindView(R.id.abl_mine_layout)
    AppBarLayout abl_mine_layout;
    @BindView(R.id.stl_mine_tabLayout)
    SlidingTabLayout stl_mine_tabLayout;
    @BindView(R.id.vp_mine_viewPager)
    ViewPager vp_mine_viewPager;
    @BindView(R.id.ll_mineFragment_myFollow)
    LinearLayout ll_mine_myFollow;
    @BindView(R.id.ll_mineFragment_myFans)
    LinearLayout ll_mine_myFans;
    @BindView(R.id.cdl_my_layout)
    CoordinatorLayout cdl_my_layout;
    @BindView(R.id.ll_mine_share)
    LinearLayout shareLinearLayout;
    @BindView(R.id.rv_my_share)
    RecyclerView myShareRecyclerView;
    @BindView(R.id.tv_my_countDown)
    CircleProgressbar countDownTextView;
    @BindView(R.id.srl_userDetails_layout)
    SmartRefreshLayout srl_userDetails_layout;
    @BindView(R.id.btn_myActivity_shareHomePage)
    Button btn_myActivity_shareHomePage;
    @BindView(R.id.rl_myFragment_uploadLayout)
    RelativeLayout uploadLayout;
    @BindView(R.id.iv_myFragment_uploadImage)
    ImageView uploadImageView;
    @BindView(R.id.pb_myFragment_uploadProgressBar)
    ProgressBar uploadProgressBar;
    @BindView(R.id.rl_myFragment_msg)
    RelativeLayout rl_myFragment_msg;
    @BindView(R.id.view_myFragment_msg)
    View view_myFragment_msg;

    private MyShareAdapter myShareAdapter;
    private List<ShareBean> shareBeanList = new ArrayList<>();
    private SelectTempletFragmentPagerAdapter selectTempletFragmentPagerAdapter;
    List<Fragment> fragmentList = new ArrayList<>();
    MineDataBean result;
    private TakePhotoImpl takePhoto;
    private String filePath;
    private ProductionFragment productionFragment;
    private LikeFragment likeFragment;
    private boolean autoUpdateUserInfo = true;
    private static final String KEY_OF_SHOW_SHARE_DIALOG = "showShareDialogKey";
    private static final String KEY_OF_LOCAL_VIDEO_PATH = "localVideoKey";
    private static final String KEY_OF_URL_VIDEO_PATH = "urlVideoKey";
    public boolean mUploadVideo = false;
    public IssueBean issueBean;
    public static String videoThumbPath = "";
    public static String urlVideoPath = "";
    public static String shareTitle = "我分享的这个内容一定要看下";

    @OnClick({R.id.ll_myActivity_modifyBg, R.id.iv_mine_more, R.id.ll_mine_layout, R.id.iv_myActivity_closeShareDialog,
            R.id.img_mine_userPhoto, R.id.ll_mine_flag, R.id.ll_mineFragment_myFollow, R.id.ll_mineFragment_myFans,
            R.id.ll_itemHome_praise, R.id.btn_myActivity_shareHomePage, R.id.rl_myFragment_msg})
    public void onClickView(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.rl_myFragment_msg://消息
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.iv_myActivity_closeShareDialog:
                shareLinearLayout.setVisibility(View.GONE);
                break;
            case R.id.btn_myActivity_shareHomePage:
                String str = BaseApplication.loginBean.nickname;
                String title = str.length() > 10 ? str.substring(0, 10) + "..." : str;
                String url = RequestURL.SHARE_HOME_PAGE + "?iu=" + BaseApplication.loginBean.userId;
                HomeDialog.newInstance().showShareDialog(getActivity(), 1, title + "的朝歌主页，快来看看吧!",
                        Utils.isUrl(BaseApplication.loginBean.avatar), url, null, null, null, null, null, -1);
                break;
            case R.id.iv_mine_more:
                if (result != null) {
                    bundle.putSerializable("MineDataBean", result);
                }
                Intent intentResult = new Intent();
                intentResult.setClass(getActivity(), SettingActivity.class);
                intentResult.putExtras(bundle);
                startActivity(intentResult);
                break;
            case R.id.ll_mineFragment_myFollow:
                Intent intent = new Intent(getActivity(), MyFollowsActivity.class);
                intent.putExtra(MyFollowsActivity.KEY_OF_USER_ID, SPUtils.readCurrentLoginUserInfo(getActivity()).userId);
                getActivity().startActivity(intent);
                break;
            case R.id.ll_mineFragment_myFans:
                Intent in = new Intent(getActivity(), MyFansActivity.class);
                in.putExtra(MyFollowsActivity.KEY_OF_USER_ID, SPUtils.readCurrentLoginUserInfo(getActivity()).userId);
                getActivity().startActivity(in);
                break;
            case R.id.ll_itemHome_praise:
                break;
            case R.id.ll_myActivity_modifyBg:
                showPhotoDialog();
                break;
            case R.id.img_mine_userPhoto:
                try {
                    Diooto diooto = new Diooto(getActivity())
                            .urls(Utils.isUrl(result.avatar))
                            .type(DiootoConfig.PHOTO)
                            .position(0)
                            .views(img_mine_userPhoto)
                            .loadPhotoBeforeShowBigImage(new Diooto.OnLoadPhotoBeforeShowBigImageListener() {

                                public void loadView(final DragDiootoView dragDiootoView, SketchImageView sketchImageView, int position) {
                                    StatusBarUtil.translucentStatusBar(getActivity());
                                    StatusBarUtil.setStatusBarTextColorStyle(getActivity(), true);
                                    sketchImageView.displayImage(Utils.isUrl(result.avatar));
                                    sketchImageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dragDiootoView.backToMin();
                                        }
                                    });
                                }
                            }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View subScreenView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my, null);
        ButterKnife.bind(this, subScreenView);
        return subScreenView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_itemHome_praise.getPaint().setFakeBoldText(true);
        tv_itemHome_comment.getPaint().setFakeBoldText(true);
        tv_itemHome_share.getPaint().setFakeBoldText(true);
        shareLinearLayout.setVisibility(View.GONE);
        uploadLayout.setVisibility(View.GONE);
        LinearLayoutManager searchLlinearLayoutManager = new LinearLayoutManager(getActivity());
        searchLlinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        myShareRecyclerView.setLayoutManager(searchLlinearLayoutManager);
        myShareAdapter = new MyShareAdapter(getActivity());
        myShareRecyclerView.setAdapter(myShareAdapter);
        shareBeanList.add(new ShareBean(R.drawable.pengyouquan, "朋友圈"));
        shareBeanList.add(new ShareBean(R.drawable.weixin, "微信"));
        shareBeanList.add(new ShareBean(R.drawable.qq, "QQ"));
        shareBeanList.add(new ShareBean(R.drawable.douyin, "抖音"));
        shareBeanList.add(new ShareBean(R.drawable.weibo, "微博"));
        shareBeanList.add(new ShareBean(R.drawable.kongjian, "QQ空间"));
        myShareAdapter.upateData(true, shareBeanList);

        productionFragment = ProductionFragment.newInstance(SPUtils.readCurrentLoginUserInfo(getActivity()).userId);
        likeFragment = LikeFragment.newInstance(SPUtils.readCurrentLoginUserInfo(getActivity()).userId);
        fragmentList.add(productionFragment);
        fragmentList.add(likeFragment);
        selectTempletFragmentPagerAdapter = new SelectTempletFragmentPagerAdapter(getChildFragmentManager());
        vp_mine_viewPager.setAdapter(selectTempletFragmentPagerAdapter);
        selectTempletFragmentPagerAdapter.upateData(true, fragmentList);
        vp_mine_viewPager.setCurrentItem(0);
        likeFragment.setOnDisLikeListener(new LikeFragment.OnDisLikeListener() {
            @Override
            public void disLike() {
                getUserInfo();
            }
        });
        productionFragment.setOnDeleteProduvtionListener(new ProductionFragment.OnDeleteProductionListener() {
            @Override
            public void deleteProduction() {
                getUserInfo();
            }
        });
        tv_mine_userName.setVisibility(View.GONE);
        abl_mine_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == DensityUtil.dp2px(380)) {//关闭
                    ctl_mine_layout.setContentScrimResource(R.color.white);
                } else {  //展开
                    ctl_mine_layout.setContentScrimResource(R.color.transparent);
                }

                //350是abl_mine_layout的高度
                if (Math.abs(verticalOffset) > DensityUtil.dp2px(350)) {
                    tv_mine_userName.setVisibility(View.VISIBLE);
                } else {
                    tv_mine_userName.setVisibility(View.GONE);
                }
            }
        });
        srl_userDetails_layout.setHeaderHeight(DisplayMetricsUtil.getScreenHeight(getActivity()));
        srl_userDetails_layout.setRefreshHeader(new RefreshHeader() {
            @NonNull
            @Override
            public View getView() {
                return View.inflate(getActivity(), R.layout.view_mine_headview, null);
            }

            @NonNull
            @Override
            public SpinnerStyle getSpinnerStyle() {
                return SpinnerStyle.Scale;
            }

            @Override
            public void setPrimaryColors(int... colors) {

            }

            @Override
            public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

            }

            @Override
            public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
                int mZoomViewWidth = iv_myActivity_bg.getMeasuredWidth();
                int mZoomViewHeight = iv_myActivity_bg.getMeasuredHeight();
                ViewGroup.LayoutParams lp = iv_myActivity_bg.getLayoutParams();
                lp.width = (mZoomViewWidth + offset);
                lp.height = (mZoomViewHeight * ((mZoomViewWidth + offset) / mZoomViewWidth));
                ((ViewGroup.MarginLayoutParams) lp).setMargins(-(lp.width - mZoomViewWidth) / 2, 0, -(lp.width - mZoomViewWidth) / 2, 0);
                iv_myActivity_bg.setLayoutParams(lp);
            }

            @Override
            public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
            }

            @Override
            public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

            }

            @Override
            public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
                return 10;
            }

            @Override
            public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

            }

            @Override
            public boolean isSupportHorizontalDrag() {
                return false;
            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

            }
        });
    }

    /**
     * 获取个人作品和喜欢
     */
    private void getProductionAndLikesData() {
        productionFragment.userID = SPUtils.readCurrentLoginUserInfo(getActivity()).userId;
        likeFragment.userID = SPUtils.readCurrentLoginUserInfo(getActivity()).userId;
        productionFragment.getData(true, -1, "DOWN");
        likeFragment.getData(true, -1, "DOWN");
    }

    private void showShareDialog() {
        uploadLayout.setVisibility(View.GONE);
        shareLinearLayout.setVisibility(View.VISIBLE);
        countDownTextView.setOutLineColor(Color.TRANSPARENT);
        countDownTextView.setInCircleColor(Color.TRANSPARENT);
        countDownTextView.setProgressColor(Color.parseColor("#C4C4C4"));
        countDownTextView.setProgressLineWidth(5);
        countDownTextView.setProgressType(CircleProgressbar.ProgressType.COUNT_BACK);
        countDownTextView.setTimeMillis(5000);
        countDownTextView.reStart();

        countDownTextView.setCountdownProgressListener(1, new CircleProgressbar.OnCountdownProgressListener() {

            @Override
            public void onProgress(int what, int progress) {
                if (what == 1 && progress == 0) {
                    ObjectAnimator shareTranslationY = new ObjectAnimator().ofFloat(shareLinearLayout, "translationY", 0, -DisplayMetricsUtil.dip2px(getActivity(), 160));
                    shareTranslationY.setDuration(1300);
                    shareTranslationY.setRepeatCount(0);
                    shareTranslationY.setRepeatMode(ValueAnimator.RESTART);
                    shareTranslationY.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            shareLinearLayout.setVisibility(View.GONE);
                            ObjectAnimator translationY = new ObjectAnimator().ofFloat(shareLinearLayout, "translationY", -DisplayMetricsUtil.dip2px(getActivity(), 160), 0);
                            translationY.setDuration(0);
                            translationY.setRepeatCount(0);
                            translationY.setRepeatMode(ValueAnimator.RESTART);
                            translationY.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    ObjectAnimator contentTranslationY = new ObjectAnimator().ofFloat(cdl_my_layout, "translationY", 0, -DisplayMetricsUtil.dip2px(getActivity(), 160));
                    contentTranslationY.setDuration(1300);
                    contentTranslationY.setRepeatCount(0);
                    contentTranslationY.setRepeatMode(ValueAnimator.RESTART);
                    contentTranslationY.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            shareLinearLayout.setVisibility(View.GONE);
                            ObjectAnimator translationY = new ObjectAnimator().ofFloat(cdl_my_layout, "translationY", -DisplayMetricsUtil.dip2px(getActivity(), 160), 0);
                            translationY.setDuration(0);
                            translationY.setRepeatCount(0);
                            translationY.setRepeatMode(ValueAnimator.RESTART);
                            translationY.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    shareTranslationY.start();
//                            contentTranslationY.start();
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && SPUtils.isLogin(getActivity())) {
            getProductionAndLikesData();
            getUserInfo();
            queryMessageNum();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        queryMessageNum();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUploadVideo && issueBean != null) {
            Glide.with(getActivity()).load(issueBean.cover).into(uploadImageView);
            uploadLayout.setVisibility(View.VISIBLE);
            //TODO   开始上传文件
            uploadProgressBar.setProgress(0);
            issueBean.userId = SPUtils.readCurrentLoginUserInfo(getActivity()).userId;
            VideoEditManager videoEditManager = VideoEditManager.newInstance();
            videoEditManager.setOnPublishListener(new VideoEditManager.OnPublishListener() {
                @Override
                public void onFinish(String fileLocalPath, String title, String coverPath) {
                    urlVideoPath = fileLocalPath;
                    videoThumbPath = coverPath;
                    shareTitle = title;
                    showShareDialog();
                }

                @Override
                public void onFailed(String msg) {
                    DialogUtil.createDefaultDialog(getActivity(), "提示", "视频发布失败",
                            "重试", new IDialog.OnClickListener() {
                                @Override
                                public void onClick(IDialog dialog) {
                                    onResume();
                                    dialog.dismiss();
                                }
                            },
                            "取消", new IDialog.OnClickListener() {
                                @Override
                                public void onClick(IDialog dialog) {
                                    uploadLayout.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }
                            });

                }
            });
            videoEditManager.saveVideo(issueBean, uploadProgressBar, (MainActivity) getActivity());
        }

        if (SPUtils.isLogin(getActivity())) {
            getProductionAndLikesData();
        }

        if (SPUtils.isLogin(getActivity()) && autoUpdateUserInfo) {
            getUserInfo();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mUploadVideo = false;
    }

    private void getUserInfo() {
        Map<String, String> paramsMap = new HashMap<String, String>();
        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.MY_INFO, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                MineDataBean mineDataBean = gson.fromJson(response, MineDataBean.class);
                //背景图
                GlideUtil.loadImg(Utils.isUrl(mineDataBean.userBackGround), iv_myActivity_bg);
                SPUtils.writeThirdAccountBind(getActivity(), mineDataBean);
                result = mineDataBean;

                ArrayList<String> tableList = new ArrayList<>();
                String sex = "";
                if ("M".equals(result.sex)) {
                    sex = "男";
                } else if ("F".equals(result.sex)) {
                    sex = "女";
                }
                if (!Utils.isEmpty(sex))
                    tableList.add(sex);
                tableList.add(result.age + "");
                if (result.area != null)
                    tableList.add(result.area);
                ll_mine_flag.removeAllViews();
                //头像
                if (result.avatar.length() < 16) {
                    GlideUtil.loadCircleImg(R.drawable.app_icon, img_mine_userPhoto);//头像
                } else {
                    GlideUtil.loadCircleImg(Utils.isUrl(result.avatar), img_mine_userPhoto);
                }
                String nickName = result.nickname;
                String subniname;
                if (nickName.length() > 8) {
                    subniname = nickName.substring(0, 8) + "...";
                } else {
                    subniname = nickName;
                }
                tv_mine_name.setText(subniname);
                tv_mine_userName.setText(subniname);
                tv_mine_id.setText("ID " + result.userNo);
                tv_itemHome_share.setText("" + result.fans);
                tv_itemHome_comment.setText("" + result.follows);
                tv_itemHome_praise.setText(result.numberOfPraise + "");
                if (result.remark != null)
                    tv_mine_sign.setText(result.remark);
                for (int i = 0; i < tableList.size(); i++) {
                    TextView textView = new TextView(getActivity());
                    textView.setText(tableList.get(i) + "");
                    textView.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_bg_text));
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    textView.setTextSize(getResources().getDimension(R.dimen._3dp));
                    textView.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            Utils.dp2px(getActivity(), 20 + tableList.get(i).length() * 10),
                            Utils.dp2px(getActivity(), 20));
                    params.rightMargin = Utils.dp2px(getActivity(), 5);
                    ll_mine_flag.addView(textView, params);
                }

                String[] strings = new String[]{"作品 " + result.moments, "喜欢 " + result.likes};
                stl_mine_tabLayout.setViewPager(vp_mine_viewPager, strings);

            }

            @Override
            public void onFailure(String t) {
            }
        });
    }

    /**
     * 显示图片选择的弹框
     */
    private void showPhotoDialog() {

        MineDialog.newInstance().showSelectDialog(getActivity(), new IDialog.OnBuildListener() {
            @Override
            public void onBuildChildView(final IDialog dialog, View view, int layoutRes, FragmentManager fragmentManager) {
                Button btn_select_first = view.findViewById(R.id.btn_select_first);
                Button btn_select_second = view.findViewById(R.id.btn_select_second);
                Button btn_select_cancel = view.findViewById(R.id.btn_select_cancel);
                btn_select_first.setText("拍照");
                btn_select_second.setText("相册选择");
                btn_select_first.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XXPermissions.with(getActivity())
                                //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                                .permission(Permission.CAMERA)
                                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                                .request(new OnPermission() {
                                    @Override
                                    public void hasPermission(List<String> granted, boolean isAll) {
                                        if (isAll) {
                                            File file = new File(Environment.getExternalStorageDirectory(), "/ChaoGe/Image/" + System.currentTimeMillis() + ".jpg");
                                            if (!file.getParentFile().exists()) {
                                                file.getParentFile().mkdirs();
                                            }
                                            Uri imageUri = Uri.fromFile(file);

                                            takePhoto.onPickFromCapture(imageUri);//相机
                                            dialog.dismiss();

                                        } else {
                                            Toast.makeText(getActivity(), "部分权限未正常授予", Toast.LENGTH_SHORT).show();
                                            XXPermissions.gotoPermissionSettings(getActivity());
                                        }
                                    }

                                    @Override
                                    public void noPermission(List<String> denied, boolean quick) {
                                        Toast.makeText(getActivity(), "被永久拒绝授权，请手动授予权限", Toast.LENGTH_SHORT).show();
                                        XXPermissions.gotoPermissionSettings(getActivity());
                                    }
                                });


                    }
                });
                btn_select_second.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePhoto.onPickFromPicture();//相册
                        dialog.dismiss();
                    }
                });
                btn_select_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });


            }
        });

    }

    private void queryMessageNum() {
        if (!SPUtils.isLogin(getContext())) {
          return;
        }
        MessageNumRequest.newInstance((RxAppCompatActivity) getActivity()).getMessageNum(SPUtils.readCurrentLoginUserInfo(getActivity()).userId, new BaseRequestInterface<MessageNumBean>() {
            @Override
            public void success(int state, String msg, MessageNumBean messageNumBean) {
                if (messageNumBean.newComment != 0 || messageNumBean.newFans != 0 || messageNumBean.newlike != 0) {
                    view_myFragment_msg.setVisibility(View.VISIBLE);
                } else
                    view_myFragment_msg.setVisibility(View.GONE);
            }

            @Override
            public void error(int state, String msg) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = new TakePhotoImpl(this, this);
        }
        return takePhoto;
    }

    @Override
    public void takeSuccess(TResult result) {
        autoUpdateUserInfo = false;
        Log.i("MyFragment", "takeSuccess：" + result.getImage().getOriginalPath());
        String originalPath = result.getImage().getOriginalPath();
        if (originalPath == null)
            return;
        GlideUtil.loadImg("file://" + originalPath, iv_myActivity_bg);
        UploadManager uploadManager = new UploadManager(getActivity());
        filePath = uploadManager.getObjKey(UploadManager.JPG);

        uploadManager.upload(filePath, new UploadManager.Body(originalPath), new OnUploadFileListener() {
            @Override
            public void success() {
                Map<String, String> paramsMap = new HashMap<String, String>();
                paramsMap.put("userBackGround", filePath);

                HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.USER_INFO_UPDATE, paramsMap, new HttpRequest.HttpResponseCallBank() {
                    @Override
                    public void onSuccess(String response) {
                        autoUpdateUserInfo = true;
                    }

                    @Override
                    public void onFailure(String t) {
                    }
                });
            }

            @Override
            public void error(String msg) {
                LogUtil.i("上传———失败——原因---" + msg);
            }

            @Override
            public void progress(int progress, long currentSize, long totalSize) {
                LogUtil.i("上传中:  总大小=" + totalSize + "--进度=" + progress + "—当前大小=" + currentSize);
            }
        });


    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }
}
