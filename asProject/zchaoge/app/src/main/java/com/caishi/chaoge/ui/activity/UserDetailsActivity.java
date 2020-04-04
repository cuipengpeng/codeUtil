package com.caishi.chaoge.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.MineDataBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.ui.adapter.SelectTempletFragmentPagerAdapter;
import com.caishi.chaoge.ui.fragment.LikeFragment;
import com.caishi.chaoge.ui.fragment.ProductionFragment;
import com.caishi.chaoge.ui.dialog.HomeDialog;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.NetConnectUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.StatusBarUtil;
import com.caishi.chaoge.utils.ToastUtils;
import com.caishi.chaoge.utils.Utils;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

import net.moyokoo.diooto.Diooto;
import net.moyokoo.diooto.DragDiootoView;
import net.moyokoo.diooto.config.DiootoConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.panpf.sketch.SketchImageView;

import static com.caishi.chaoge.http.RequestURL.SHARE_HOME_PAGE;

public class UserDetailsActivity extends BaseUILocalDataActivity implements HttpRequest.HttpResponseCallBank {
    @BindView(R.id.img_userDetails_bg)
    ImageView img_userDetails_bg;
    @BindView(R.id.tv_userDetails_userName)
    TextView tv_userDetails_userName;
    @BindView(R.id.tv_userDetails_userId)
    TextView tv_userDetails_userId;
    @BindView(R.id.ll_userDetails_flag)
    LinearLayout ll_userDetails_flag;
    @BindView(R.id.ll_userDetails_back)
    LinearLayout ll_userDetails_back;
    @BindView(R.id.tv_userDetails_sign)
    TextView tv_userDetails_sign;
    @BindView(R.id.tv_userDetails_praise)
    TextView tv_userDetails_praise;
    @BindView(R.id.tv_userDetails_comment)
    TextView tv_userDetails_comment;
    @BindView(R.id.ll_userDetailsActivity_myFollow)
    LinearLayout ll_userDetailsActivity_myFollow;
    @BindView(R.id.tv_userDetails_share)
    TextView tv_userDetails_share;
    @BindView(R.id.ll_userDetailsActivity_myFans)
    LinearLayout ll_userDetailsActivity_myFans;
    @BindView(R.id.img_userDetails_userPhoto)
    ImageView img_userDetails_userPhoto;
    @BindView(R.id.ll_userDetails_more)
    LinearLayout ll_userDetails_more;
    @BindView(R.id.btn_userDetails_share)
    Button btn_userDetails_share;
    @BindView(R.id.ll_userDetails_inAttention)
    LinearLayout ll_userDetails_inAttention;
    @BindView(R.id.btn_userDetails_attention)
    Button btn_userDetails_attention;
    @BindView(R.id.ll_userDetails_share)
    LinearLayout ll_userDetails_share;
    @BindView(R.id.ll_userDetails_attention)
    LinearLayout ll_userDetails_attention;
    @BindView(R.id.rl_userDetails_layout)
    RelativeLayout rl_userDetails_layout;
    @BindView(R.id.abl_userDetails_layout)
    AppBarLayout abl_userDetails_layout;
    @BindView(R.id.stl_userDetails_tabLayout)
    SlidingTabLayout stl_userDetails_tabLayout;
    @BindView(R.id.vp_userDetails_viewPager)
    ViewPager vp_userDetails_viewPager;
    @BindView(R.id.srl_userDetails_layout)
    SmartRefreshLayout srl_userDetails_layout;


    private SelectTempletFragmentPagerAdapter selectTempletFragmentPagerAdapter;
    ArrayList<Fragment> fragmentList = new ArrayList<>();
    private MineDataBean results;
    int followStatus = 1;//关注状态
    private ProductionFragment productionFragment;
    private LikeFragment likeFragment;
    String userFlag = "";
    String userID = "";
    public static final String  KEY_OF_USER_ID = "userIdKey";

    @OnClick({R.id.img_userDetails_userPhoto, R.id.ll_userDetailsActivity_myFollow, R.id.ll_userDetailsActivity_myFans,
            R.id.ll_userDetails_more, R.id.btn_userDetails_attention, R.id.btn_userDetails_share,
            R.id.ll_userDetails_share, R.id.ll_userDetails_back})
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.ll_userDetails_back:
                finish();
                break;
            case R.id.img_userDetails_userPhoto:
                try {
                    Diooto diooto = new Diooto(this)
                            .urls(Utils.isUrl(results.avatar))
                            .type(DiootoConfig.PHOTO)
                            .position(0)
                            .views(img_userDetails_userPhoto)
                            .loadPhotoBeforeShowBigImage(new Diooto.OnLoadPhotoBeforeShowBigImageListener() {

                                public void loadView(final DragDiootoView dragDiootoView, SketchImageView sketchImageView, int position) {
                                    StatusBarUtil.translucentStatusBar(UserDetailsActivity.this);
                                    StatusBarUtil.setStatusBarTextColorStyle(UserDetailsActivity.this, true);
                                    sketchImageView.displayImage(Utils.isUrl(results.avatar));
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
            case R.id.ll_userDetailsActivity_myFollow:
                if(SPUtils.isLogin(this) && SPUtils.readCurrentLoginUserInfo(mContext).userId.equals(userID)){
                    Intent intent = new Intent(this, MyFollowsActivity.class);
                    intent.putExtra(MyFollowsActivity.KEY_OF_USER_ID, userID);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                }
                break;
            case R.id.ll_userDetailsActivity_myFans:
                if(SPUtils.isLogin(this) && SPUtils.readCurrentLoginUserInfo(mContext).userId.equals(userID)){
                    Intent in = new Intent(this, MyFansActivity.class);
                    in.putExtra(MyFollowsActivity.KEY_OF_USER_ID, userID);
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                }
                break;
            case R.id.ll_userDetails_more://关注
            case R.id.btn_userDetails_attention:
                if (!SPUtils.isLogin(this)) {
                    LoginActivity.open(this, -1);
                    return;
                }
                if (!NetConnectUtil.isNetConnected(mContext)) {
                    ToastUtils.showCentreToast(mContext, "请检查网络连接是否正常");
                    return;
                }
                if (results.followStatus == 1) {
                    followStatus = 0;
                } else {
                    followStatus = 1;
                }

                Map<String, String> paramsMap = new HashMap<String, String>();
                paramsMap.put("userId", results.userId);
                paramsMap.put("status", followStatus + "");

                HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.FOLLOW, paramsMap, new HttpRequest.HttpResponseCallBank() {
                    @Override
                    public void onSuccess(String response) {
                        if (followStatus == 0) {
                            results.followStatus = 0;
                            ll_userDetails_inAttention.setVisibility(View.GONE);
                            ll_userDetails_attention.setVisibility(View.VISIBLE);
                        } else {
                            results.followStatus = 1;
                            ll_userDetails_inAttention.setVisibility(View.VISIBLE);
                            ll_userDetails_attention.setVisibility(View.GONE);
                        }

                        EventBus.getDefault().post(results);
                    }

                    @Override
                    public void onFailure(String t) {
                    }
                });
                break;
            case R.id.btn_userDetails_share://分享
            case R.id.ll_userDetails_share:
                String str = results.nickname;
                String title = str.length() > 10 ? str.substring(0, 10) + "..." : str;
                String url = SHARE_HOME_PAGE + "?iu=" + results.userId;
                HomeDialog.newInstance().showShareDialog(mContext, 1, title + "的朝歌主页，快来看看吧!",
                        Utils.isUrl(results.avatar), url,null, null, null, null, null, -1);
                break;

        }

    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_user_details;
    }

    @Override
    protected void initPageData() {

        showBaseUITitle  = false;
        userID = getIntent().getStringExtra(KEY_OF_USER_ID);
        fragmentList.clear();
        productionFragment = ProductionFragment.newInstance(userID);
        likeFragment = LikeFragment.newInstance(userID);
        fragmentList.add(productionFragment);
        fragmentList.add(likeFragment);
        selectTempletFragmentPagerAdapter = new SelectTempletFragmentPagerAdapter(getSupportFragmentManager());
        vp_userDetails_viewPager.setAdapter(selectTempletFragmentPagerAdapter);
        selectTempletFragmentPagerAdapter.upateData(true, fragmentList);
        vp_userDetails_viewPager.setCurrentItem(0);
        srl_userDetails_layout.setHeaderHeight(DisplayMetricsUtil.getScreenHeight(mContext));
        srl_userDetails_layout.setRefreshHeader(new RefreshHeader() {
            @NonNull
            @Override
            public View getView() {
                return View.inflate(mContext, R.layout.view_mine_headview, null);
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
                int mZoomViewWidth = img_userDetails_bg.getMeasuredWidth();
                int mZoomViewHeight = img_userDetails_bg.getMeasuredHeight();
                ViewGroup.LayoutParams lp = img_userDetails_bg.getLayoutParams();
                lp.width = (mZoomViewWidth + offset);
                lp.height = (mZoomViewHeight * ((mZoomViewWidth + offset) / mZoomViewWidth));
                ((ViewGroup.MarginLayoutParams) lp).setMargins(-(lp.width - mZoomViewWidth) / 2, 0, -(lp.width - mZoomViewWidth) / 2, 0);
                img_userDetails_bg.setLayoutParams(lp);
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

        vp_userDetails_viewPager.setCurrentItem(0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        userID = intent.getExtras().getString("userID");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
        if (TextUtils.isEmpty(userID))
            return;
        if (SPUtils.isLogin(mContext) && userID.equals(SPUtils.readCurrentLoginUserInfo(mContext).userId)) {
            userFlag = "self";
        } else {
            userFlag = "other";
        }
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("userId", userID);
        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.USER_INFO, paramsMap, this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSuccess(String response) {
        Gson gson = new Gson();
        MineDataBean mineDataBean = gson.fromJson(response, MineDataBean.class);
        results = mineDataBean;
        ArrayList<String> tableList = new ArrayList<>();
        String sex = "";
        if ("M".equals(mineDataBean.sex)) {
            sex = "男";
        } else if ("F".equals(mineDataBean.sex)) {
            sex = "女";
        }
        if (!Utils.isEmpty(sex))
            tableList.add(sex);
        tableList.add(mineDataBean.age + "");
        if (mineDataBean.area != null)
            tableList.add(mineDataBean.area);

        ll_userDetails_flag.removeAllViews();
        //头像
        GlideUtil.loadCircleImg(Utils.isUrl(mineDataBean.avatar), img_userDetails_userPhoto);

        String nickName = mineDataBean.nickname;
        String subniname;
        if (nickName.length() > 8) {
            subniname = nickName.substring(0, 8) + "...";
        } else {
            subniname = nickName;
        }
        GlideUtil.loadImg(Utils.isUrl(mineDataBean.userBackGround), img_userDetails_bg);
//        fastBlurUtil.blurUrlImage(Utils.isUrl(mineDataBean.userBackGround), img_userDetails_bg);
        tv_userDetails_userName.setText(subniname);
        tv_userDetails_userId.setText("ID " + mineDataBean.userNo);
        tv_userDetails_share.setText("" + mineDataBean.fans);
        tv_userDetails_comment.setText("" + mineDataBean.follows);
        tv_userDetails_praise.setText(mineDataBean.numberOfPraise + "");

        if (mineDataBean.followStatus == 1) {
            ll_userDetails_inAttention.setVisibility(View.VISIBLE);
            ll_userDetails_attention.setVisibility(View.GONE);
        } else {
            ll_userDetails_attention.setVisibility(View.VISIBLE);
            ll_userDetails_inAttention.setVisibility(View.GONE);
        }


        if (mineDataBean.remark != null)
            tv_userDetails_sign.setText(mineDataBean.remark);

        for (int i = 0; i < tableList.size(); i++) {
            TextView textView = new TextView(mContext);
            textView.setText(tableList.get(i) + "");
            textView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_bg_text));
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setTextSize(Utils.dp2px(mContext, 3));
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    Utils.dp2px(mContext, 20 + tableList.get(i).length() * 10),
                    Utils.dp2px(mContext, 20));
            params.rightMargin = Utils.dp2px(mContext, 5);
            ll_userDetails_flag.addView(textView, params);
        }

        String[] strings = new String[]{"作品 " + mineDataBean.moments, "喜欢 " + mineDataBean.likes};
        stl_userDetails_tabLayout.setViewPager(vp_userDetails_viewPager, strings);
    }

    @Override
    public void onFailure(String t) {
    }

    public static void open(Context context, String userId) {
        Intent intent = new Intent(context, UserDetailsActivity.class);
        intent.putExtra(KEY_OF_USER_ID, userId);
        context.startActivity(intent);
    }
}
