package com.test.xcamera.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.editvideo.MediaData;
import com.editvideo.MediaUtils;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.google.gson.Gson;
import com.test.xcamera.R;
import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.AlbumListBean;
import com.test.xcamera.bean.FeedList;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoVideo;
import com.test.xcamera.bean.UserInfo;
import com.test.xcamera.login.LoginActivty;
import com.test.xcamera.moalbum.activity.MyAlbumPreview;
import com.test.xcamera.personal.adapter.MyAdapter;
import com.test.xcamera.personal.bean.UserInformationParam;
import com.test.xcamera.personal.usecase.GetUserInfo;
import com.test.xcamera.personal.usecase.OnGetUserInfoCallBack;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.GlideUtils;
import com.test.xcamera.utils.MediaScannerUtil;
import com.test.xcamera.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mz on 2019/7/2.
 */

public class MyFragment extends Fragment {
    @BindView(R.id.iv_myFragment_profile)
    ImageView profileImageView;
    @BindView(R.id.tv_myFragment_name)
    TextView userNameTextView;
    @BindView(R.id.tv_myFragment_content)
    TextView contentTextView;
    @BindView(R.id.iv_myFragment_rightArrow)
    ImageView rightArrowImageView;
    @BindView(R.id.rl_myFragment_bg)
    RelativeLayout backgroundRelativeLayout;
    @BindView(R.id.rl_myFragment_userInfo)
    RelativeLayout userInfoRelativeLayout;
    @BindView(R.id.tv_myFragment_publish)
    TextView publishTextView;
    @BindView(R.id.rv_myFragment_list)
    RecyclerView gridRecyclerView;
    @BindView(R.id.rl_myFragment_noData)
    RelativeLayout noDataRelativeLayout;
    @BindView(R.id.ll_myFragment_headView)
    LinearLayout headViewLinearLayout;
    @BindView(R.id.ctl_myFragment_collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.abl_myFragment_appbarLayout)
    AppBarLayout appbarLayout;
    @BindView(R.id.cl_myFragment_conntentLayout)
    CoordinatorLayout conntentLayout;
    @BindView(R.id.tv_vision_name)
    TextView tvVisionName;

    private MyAdapter gridAdapter;
    private int pn = 0;
    private int ps = 20;
    private boolean isrequest;

    @OnClick({R.id.tv_myFragment_name, R.id.tv_myFragment_content, R.id.rl_myFragment_userInfo,R.id.iv_setting})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.rl_myFragment_userInfo:
                if (!AiCameraApplication.isLogin()) {
                    LoginActivty.startLoginActivity(getContext());
                    return;
                }
                PersonInformationActivity.startPersonInformationActivity(getActivity());
                break;
            case R.id.tv_myFragment_name:
                if (!AiCameraApplication.isLogin()) {
                    LoginActivty.startLoginActivity(getContext());
                    return;
                }
                PersonInformationActivity.startPersonInformationActivity(getActivity());
                break;
            case R.id.tv_myFragment_content:
                if (!AiCameraApplication.isLogin()) {
                    LoginActivty.startLoginActivity(getContext());
                    return;
                }
                PersonInformationActivity.startPersonInformationActivity(getActivity());
                break;
            case R.id.iv_setting:
                SettingActivity.startSettingActivity(getContext());
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MediaScannerUtil.scanFileAsync(getActivity(), Constants.myGalleryLocalPath);
        View view = inflater.inflate(R.layout.fragment_my, null);
        ButterKnife.bind(this, view);
        initRecycleView();
        tvVisionName.setVisibility(View.GONE);
        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -headViewLinearLayout.getHeight() / 2) {
                    collapsingToolbar.setTitle(AiCameraApplication.userDetail.getNickname());
                } else {
                    collapsingToolbar.setTitle(" ");
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLocalMediaList();

        if (AiCameraApplication.isLogin()) {
            userNameTextView.setText(AiCameraApplication.userDetail.getNickname());
            if (!TextUtils.isEmpty(AiCameraApplication.userDetail.getDescription())) {
                contentTextView.setText(AiCameraApplication.userDetail.getDescription());
            }
            GlideUtils.GlideLoaderHeader(getActivity(), Constants.getFileIdToUrl(AiCameraApplication.userDetail.getAvatarFileId()), profileImageView);

//            getNetMediaList(pn, ps);
        } else {
            userNameTextView.setText(getResources().getString(R.string.loginCamera));
            contentTextView.setText(getResources().getString(R.string.shareWonderfulMoment));
            profileImageView.setImageResource(R.mipmap.icon_header);

        }
        getUserInfo();
    }

    public void getLocalMediaList() {
        MediaUtils.getInstance().getAllVideoInfos(getActivity(), new MediaUtils.LocalMediaCallback() {
            @Override
            public void onLocalMediaCallback(List<MediaData> allMediaTemp) {
                List<MediaData> mediaDataList = new ArrayList<>();
                for (int i = 0; i < allMediaTemp.size(); i++) {
                    if (Constants.myGalleryDir.equalsIgnoreCase(allMediaTemp.get(i).getBucketName())) {
                        mediaDataList.add(allMediaTemp.get(i));
                    }
                }
                 LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) conntentLayout.getLayoutParams();
                MediaUtils.sortListByTime(mediaDataList);
                if (mediaDataList.size() > 0) {
                    layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
                    conntentLayout.setLayoutParams(layoutParams);
                    gridAdapter.updateData(true, mediaDataList);
                    gridRecyclerView.setVisibility(View.VISIBLE);
                    publishTextView.setVisibility(View.VISIBLE);
                    noDataRelativeLayout.setVisibility(View.GONE);
                } else {
                    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    conntentLayout.setLayoutParams(layoutParams);
                    noDataRelativeLayout.setVisibility(View.VISIBLE);
                    gridRecyclerView.setVisibility(View.GONE);
                    publishTextView.setVisibility(View.GONE);
                }
            }
        });
    }


    public void initRecycleView() {
        gridRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        gridAdapter = new MyAdapter(getActivity());
        gridRecyclerView.setAdapter(gridAdapter);
        gridAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArrayList<MoAlbumItem> albumItemList = new ArrayList<>();
                MoAlbumItem moAlbumItem;
                for(int i=0; i<gridAdapter.mData.size();i++){
                     moAlbumItem = new MoAlbumItem();
                    MoVideo moVideo = new MoVideo();
                    moVideo.setmDuration(gridAdapter.mData.get(i).getDuration());
                    moVideo.setmUri(gridAdapter.mData.get(i).getPath());
                    MoImage moImage = new MoImage();
                    moImage.setmUri(gridAdapter.mData.get(i).getPath());
                    moAlbumItem.setmVideo(moVideo);
                    moAlbumItem.setmThumbnail(moImage);
                    moAlbumItem.setmIsCamrea(false);
                    moAlbumItem.setmType("video");
                    albumItemList.add(moAlbumItem);
                }

                AlbumListBean albumListBean = new AlbumListBean();
                albumListBean.albumItems = albumItemList;
                Gson gson = new Gson();
                String json = gson.toJson(albumListBean);
                SharedPreferencesUtil.instance().saveString("album_list", json);
                Intent intent = new Intent(getActivity(), MyAlbumPreview.class);
                intent.putExtra("index", position);
                intent.putExtra("iscamera", false);
                intent.putExtra("fragment", true);
                startActivity(intent);
            }
        });
    }

    private void startUserEdit(int type) {
        UserInformationParam param = new UserInformationParam();
        if (type == UserInformationParam.USER_INFORMATION_TYPE_NAME) {
            param.setName(getString(R.string.person_edit_name));
            param.setTile(getString(R.string.person_edit_name));
            param.setValue(AiCameraApplication.userDetail.getNickname());
        } else if (type == UserInformationParam.USER_INFORMATION_INTRODUCE) {
            param.setName(getString(R.string.person_edit_introduce));
            param.setTile(getString(R.string.person_edit_introduce));
            param.setValue(AiCameraApplication.userDetail.getDescription());
        }
        param.setType(type);
        PersonInformationEditActivity.startPersonInformationEditActivity(getActivity(), param,3);
    }

    public void getNetMediaList(int pn, int ps) {
        isrequest = true;
        ApiImpl.getInstance().getMyOPusList(pn, ps, new CallBack<FeedList>() {
            @Override
            public void onSuccess(FeedList feedList) {
                isrequest = false;
                if (feedList.isSucess()) {
                    if (feedList.getData().size() > 0) {
                        publishTextView.setVisibility(View.VISIBLE);
                        List<MediaData> mediaDataList = new ArrayList<>();
                        MediaData mediaData;
                        for (int i = 0; i < feedList.getData().size(); i++) {
                            mediaData = new MediaData();
                            mediaData.setRemoteData(true);
                            mediaData.setPath(feedList.getData().get(i).getCoverFileId() + "");
                            mediaData.setDuration(feedList.getData().get(i).getDuration());
                            mediaDataList.add(mediaData);
                        }
                        gridAdapter.updateData(true, mediaDataList);
                    } else {
                        publishTextView.setVisibility(View.GONE);
                        noDataRelativeLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    CameraToastUtil.show(feedList.getMessage(), getActivity());
                }
            }

            @Override
            public void onFailure(Throwable e) {
                isrequest = false;
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        MediaScannerUtil.scanFileAsync(getActivity(), Constants.myGalleryLocalPath);
        if (!hidden) {
            getLocalMediaList();
            getUserInfo();
        }
        super.onHiddenChanged(hidden);
    }
    public void getUserInfo(){
        GetUserInfo userInfo=GetUserInfo.getInstance();
        if (AiCameraApplication.isLogin()) {
            userInfo.setCallBack(new OnGetUserInfoCallBack() {
                @Override
                public void onUserInfoCallBack(UserInfo userInfo) {
                    if(userInfo.getCode()==0&&userNameTextView!=null){
                        userNameTextView.setText(AiCameraApplication.userDetail.getNickname());
                        if (!TextUtils.isEmpty(AiCameraApplication.userDetail.getDescription())) {
                            contentTextView.setText(AiCameraApplication.userDetail.getDescription());
                        }
                        GlideUtils.GlideLoaderHeader(getActivity(), Constants.getFileIdToUrl(AiCameraApplication.userDetail.getAvatarFileId()), profileImageView);

                    }
                }
            });
            userInfo.getUserInfo();
        }
    }

}
