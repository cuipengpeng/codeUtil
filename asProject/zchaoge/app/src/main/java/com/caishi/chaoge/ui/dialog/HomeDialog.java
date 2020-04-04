package com.caishi.chaoge.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.CommentListBean;
import com.caishi.chaoge.bean.ShareBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.Product;
import com.caishi.chaoge.ui.activity.LoginActivity;
import com.caishi.chaoge.ui.activity.VideoActivity;
import com.caishi.chaoge.ui.adapter.HomeRecycleViewAdapter;
import com.caishi.chaoge.ui.widget.CustomCircleProgressBar;
import com.caishi.chaoge.ui.widget.dialog.DialogUtil;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.ui.widget.dialog.SYDialog;
import com.caishi.chaoge.utils.CustomLoadMoreView;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.utils.MD5Util;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.ToastUtils;
import com.caishi.chaoge.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caishi.chaoge.utils.ConstantUtils.DOWNLOAD_MP4_PATH;

public class HomeDialog {
    private SYDialog dialog;
    private static HomeDialog homeDialog;
    private final String UP = "UP";
    private final String DOWN = "DOWN";
    private String momentId;
    private String desUserId = "";
    private int pageSize = 10;
    private int commentNum;
    private long since = -1;
    private boolean isAdd = false;
    private ArrayList<CommentListBean> commentListBeans;
    FragmentActivity baseActivity;
    private CommentAdapter commentAdapter;
    private RecyclerView rv_dialogComment_commentList;
    private TextView tv_dialogComment_hint;
    private OnDeleteVideoListener onDeleteVideoListener;

    public static HomeDialog newInstance() {
        if (homeDialog == null) {
            homeDialog = new HomeDialog();
        }
        return homeDialog;
    }


    /**
     * 评论
     *
     * @param context
     */
    public void showCommentDialog(final FragmentActivity context, final RecyclerView.Adapter recycleViewAdapter, final String momentId, final int commentNum, final TextView tv) {
        this.baseActivity = context;
        this.momentId = momentId;
        this.commentNum = commentNum;
        commentListBeans = new ArrayList<>();
        dialog = new SYDialog.Builder(context)
                .setDialogView(R.layout.dialog_comment)//设置dialog布局
                .setAnimStyle(R.style.AnimUp)//设置动画 默认没有动画
                .setScreenWidthP(1.0f) //设置屏幕宽度比例 0.0f-1.0f
                .setGravity(Gravity.BOTTOM)//设置Gravity
                .setScreenHeightP(0.55f)//设置屏幕高度比例 0.0f-1.0f
                .setWindowBackgroundP(0.1f)//设置背景透明度 0.0f-1.0f 1.0f完全不透明
                .setCancelable(true)//设置是否屏蔽物理返回键 true不屏蔽  false屏蔽
                .setCancelableOutSide(true)//设置dialog外点击是否可以让dialog消失
                .setBuildChildListener(new IDialog.OnBuildListener() {

                    //设置子View
                    @Override
                    public void onBuildChildView(final IDialog dialog, View view, int layoutRes, FragmentManager fragmentManager) {
                        final EditText et_dialogComment_comment = view.findViewById(R.id.et_dialogComment_comment);
                        final ImageView img_dialogComment_send = view.findViewById(R.id.img_dialogComment_send);
                        final LinearLayout ll_dialogComment_layout = view.findViewById(R.id.ll_dialogComment_layout);
                        LinearLayout ll_dialogComment_send = view.findViewById(R.id.ll_dialogComment_send);//发送评论
                        LinearLayout ll_dialogComment_close = view.findViewById(R.id.ll_dialogComment_close);//关闭弹窗
                        final TextView tv_dialogComment_commentNum = view.findViewById(R.id.tv_dialogComment_commentNum);//评论数
                        tv_dialogComment_commentNum.setText(commentNum + " 条评论");
                        // et_dialogComment_comment.setInputType(InputType.TYPE_NULL);
                        if (!SPUtils.isLogin(context)) {
                            //用户未登陆
                            et_dialogComment_comment.setFocusable(false);
                        }
                        et_dialogComment_comment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!SPUtils.isLogin(context)) {
                                    //用户未登陆
                                    //评论框
                                    LoginActivity.open(context, -1);
//                                    dialog.dismiss();
                                }
                            }
                        });
//                        ll_dialogComment_layout.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (!context.isLogin()) {
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString(LOGIN_FLAG, "");
//                                    context.startActivity(LoginActivity.class, bundle);
//                                    dialog.dismiss();
//                                } else {
//                                    et_dialogComment_comment.setEnabled(true);
//                                   // et_dialogComment_comment.setInputType(InputType.TYPE_CLASS_TEXT);
//                                }
//                            }
//                        });

                        et_dialogComment_comment.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                img_dialogComment_send.setImageResource((s.length() != 0 ?
                                        R.drawable.ic_send_enabled : R.drawable.ic_send));
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        getData(since, DOWN);
                        //设置评论列表内容
                        //评论内容
                        rv_dialogComment_commentList = view.findViewById(R.id.rv_dialogComment_commentList);
                        tv_dialogComment_hint = view.findViewById(R.id.tv_dialogComment_hint);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        rv_dialogComment_commentList.setLayoutManager(linearLayoutManager);
                        commentAdapter = new CommentAdapter();
                        rv_dialogComment_commentList.setAdapter(commentAdapter);
                        commentAdapter.setLoadMoreView(new CustomLoadMoreView());
                        View inflate = View.inflate(baseActivity, R.layout.view_no_data, null);
                        commentAdapter.setEmptyView(inflate);
                        commentAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                            @Override
                            public void onLoadMoreRequested() {
                                isAdd = true;
                                getData(commentListBeans.get(commentListBeans.size() - 1).targetTime, UP);
                            }
                        }, rv_dialogComment_commentList);
                        commentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                if (!SPUtils.isLogin(context)) {
                                    LoginActivity.open(context, -1);
                                    dialog.dismiss();
                                    return;
                                }
                                desUserId = commentListBeans.get(position).userId;
                                et_dialogComment_comment.setHint("回复 " + commentListBeans.get(position).nickname);
                            }
                        });


                        ll_dialogComment_send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String commentInfo = et_dialogComment_comment.getText().toString().trim();
                                if (!TextUtils.isEmpty(commentInfo)) {

                                    Map<String, String> paramsMap = new HashMap<>();
                                    paramsMap.put("momentId", momentId);
                                    paramsMap.put("content", commentInfo);
                                    paramsMap.put("desUserId", desUserId != null ? desUserId : "");

                                    HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.COMMENT_POST, paramsMap, new HttpRequest.HttpResponseCallBank() {
                                        @Override
                                        public void onSuccess(String response) {
//                                            Gson gson = new Gson();
//                                            MsgBean msgBean  = gson.fromJson(response, MsgBean.class);
                                            if (Boolean.valueOf(response)) {
                                                commentListBeans.clear();
                                                isAdd = false;
                                                et_dialogComment_comment.setText("");
                                                HomeDialog.this.commentNum++;
                                                tv_dialogComment_commentNum.setText(HomeDialog.this.commentNum + " 条评论");
                                                if (tv != null)
                                                    tv.setText(HomeDialog.this.commentNum + "");
                                                MobclickAgent.onEvent(baseActivity, "comment", Product.sAppChannel);//评论统计
                                                getData(since, DOWN);
                                            }
                                        }

                                        @Override
                                        public void onFailure(String t) {
                                        }
                                    });

                                    closeInputSoft(context);
                                }

                            }
                        });

                        ll_dialogComment_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                desUserId = null;
                                closeInputSoft(context);
                                dialog.dismiss();
                                if (recycleViewAdapter instanceof HomeRecycleViewAdapter) {
                                    ((HomeRecycleViewAdapter) recycleViewAdapter).showCommentDialog = false;
                                }
                            }
                        });


                    }
                }).show();

        if (recycleViewAdapter instanceof HomeRecycleViewAdapter) {
            ((HomeRecycleViewAdapter) recycleViewAdapter).showCommentDialog = true;
        }
    }


    /**
     * 分享dialog
     */
    public void showShareDialog(final FragmentActivity context, final int shareFlag, final String shareTitle, final String imgPath,
                                final String shareUrl, final String mp4Path,
                                final String userId, final String momentId, final String desUserId, final TextView textView, final int itemPosition) {
        XXPermissions.with(context)
                //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                .permission(Permission.RECORD_AUDIO, Permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            dialog = new SYDialog.Builder(context)
                                    .setDialogView(R.layout.dialog_share)
                                    .setWindowBackgroundP(0.5f)
                                    .setScreenWidthP(1.0f)
                                    .setGravity(Gravity.BOTTOM)
                                    .setCancelable(true)
                                    .setCancelableOutSide(true)
                                    .setAnimStyle(R.style.AnimUp)
                                    .setBuildChildListener(new IDialog.OnBuildListener() {
                                        @Override
                                        public void onBuildChildView(final IDialog dialog, View view, int layoutRes, FragmentManager fragmentManager) {
                                            RecyclerView recyclerView = view.findViewById(R.id.rv_dialogShare_shareList);
                                            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
                                            recyclerView.setLayoutManager(gridLayoutManager);
                                            ShareAdapter shareAdapter = new ShareAdapter();
                                            recyclerView.setAdapter(shareAdapter);
                                            ArrayList<ShareBean> shareBeanList = new ArrayList<>();
                                            shareBeanList.add(new ShareBean(R.drawable.pengyouquan, "朋友圈"));
                                            shareBeanList.add(new ShareBean(R.drawable.weixin, "微信"));
                                            shareBeanList.add(new ShareBean(R.drawable.qq, "QQ"));
                                            shareBeanList.add(new ShareBean(R.drawable.kongjian, "QQ空间"));
                                            if (null != mp4Path) {
                                                shareBeanList.add(new ShareBean(R.drawable.ic_home_download, "保存视频"));
                                            }

//                                            if (modelID != null && shootSame) {
//                                                //只有视频流主页和喜欢视频有拍同款，自己的作品没有拍同框
//                                                shareBeanList.add(new ShareBean(R.drawable.ic_shoot_same, "拍同款"));
//                                            }else
                                            if (null != userId && null != desUserId && userId.equals(desUserId) && context instanceof VideoActivity) {
                                                //只有自己的作品可以删除
                                                shareBeanList.add(new ShareBean(R.drawable.ic_delete1, "删除"));
                                            }
                                            //shareBeanList.add(new ShareBean(R.drawable.weibo, "微博"));
                                            shareAdapter.setNewData(shareBeanList);
                                            shareAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                                                    SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
                                                    switch (position) {
                                                        case 0:
                                                            platform = SHARE_MEDIA.WEIXIN_CIRCLE;
                                                            break;
                                                        case 1:
                                                            platform = SHARE_MEDIA.WEIXIN;
                                                            break;
                                                        case 2:
                                                            platform = SHARE_MEDIA.QQ;
                                                            break;
                                                        case 3:
                                                            platform = SHARE_MEDIA.QZONE;
                                                            break;
                                                        case 4:
                                                            downloadMp4(context, momentId, mp4Path,userId,desUserId);
                                                            return;
                                                        case 5:
//                                                            if (modelID != null && shootSame) {
//                                                                //只有视频流主页和喜欢视频有拍同款，自己的作品没有拍同框
//                                                                Intent intent = new Intent(context, TemplateEditActivity.class);
//                                                                        Bundle bundle = new Bundle();
//                                                                bundle.putInt("editFlag", 0);
//                                                                bundle.putString("modelId", modelID);
//                                                                intent.putExtras(bundle);
//                                                                context.startActivity(intent);
//                                                                dismissDialog();
//                                                            }else {
                                                            DialogUtil.createDefaultDialog(context, "提示", "确定删除该视频吗？",
                                                                    "确定", new IDialog.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(IDialog dialog) {
                                                                            deleteVideo(context, momentId, itemPosition);
                                                                            dialog.dismiss();
                                                                        }
                                                                    },
                                                                    "取消", new IDialog.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(IDialog dialog) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    });

//                                                            }
                                                            return;
                                                    }
                                                    dismissDialog();
                                                    String url = "";
                                                    String title = "";
                                                    String info = "";
                                                    UMImage umImage = new UMImage(context, R.drawable.ic_launcher);
                                                    switch (shareFlag) {
                                                        case 0://分享视频
//                                                            url = "http://api.chaogevideo.com/static/share/download.html";
                                                            url = shareUrl;
                                                            title = shareTitle;
                                                            info = "www.chaogevideo.com";
                                                            umImage = new UMImage(context, imgPath);
                                                            break;
                                                        case 1://分享主页
//                                                            url = "http://api.chaogevideo.com/static/share/download.html";
                                                            url = shareUrl;
                                                            title = shareTitle;
                                                            info = "www.chaogevideo.com";
                                                            umImage = new UMImage(context, imgPath);
                                                            break;

                                                        case 2://分享下载链接
                                                            url = "http://api.chaogevideo.com/static/share/download.html";
                                                            title = "和我一起来玩朝歌";
                                                            info = "专注音乐朗读短视频社区";
                                                            umImage = new UMImage(context, R.drawable.ic_launcher);
                                                            break;
                                                    }

                                                    Map<String, String> paramsMap = new HashMap<String, String>();
                                                    paramsMap.put("userId", userId);
                                                    paramsMap.put("momentId", momentId);
                                                    paramsMap.put("desUserId", desUserId);
                                                    paramsMap.put("likeStatus", 2 + "");
                                                    HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.FORWARD, paramsMap, new HttpRequest.HttpResponseCallBank() {
                                                        @Override
                                                        public void onSuccess(String response) {
                                                        }

                                                        @Override
                                                        public void onFailure(String t) {
                                                        }
                                                    });

                                                    if (textView != null) {
                                                        String shareNum = Integer.parseInt(textView.getText().toString()) + 1 + "";
                                                        textView.setText(shareNum);
                                                    }
                                                    UMWeb web = new UMWeb(url);
                                                    web.setTitle(title);//标题
                                                    web.setThumb(umImage);  //缩略图
                                                    web.setDescription(info);//描述
                                                    new ShareAction(context)
                                                            .setPlatform(platform)
                                                            .withMedia(web)
                                                            .setCallback(new UMShareListener() {
                                                                /**
                                                                 * @descrption 分享开始的回调
                                                                 * @param platform 平台类型
                                                                 */
                                                                @Override
                                                                public void onStart(SHARE_MEDIA platform) {

                                                                }

                                                                /**
                                                                 * @descrption 分享成功的回调
                                                                 * @param platform 平台类型
                                                                 */
                                                                @Override
                                                                public void onResult(SHARE_MEDIA platform) {
                                                                    ToastUtils.show(context, "分享成功");
                                                                }

                                                                /**
                                                                 * @descrption 分享失败的回调
                                                                 * @param platform 平台类型
                                                                 * @param t 错误原因
                                                                 */
                                                                @Override
                                                                public void onError(SHARE_MEDIA platform, Throwable t) {
                                                                    ToastUtils.show(context, t.getMessage());
                                                                }

                                                                /**
                                                                 * @descrption 分享取消的回调
                                                                 * @param platform 平台类型
                                                                 */
                                                                @Override
                                                                public void onCancel(SHARE_MEDIA platform) {
                                                                    ToastUtils.show(context, "分享取消");
                                                                }
                                                            })
                                                            .share();


                                                }
                                            });
                                            Button btn_cancel_dialog = view.findViewById(R.id.btn_dialogShare_cancel);
                                            btn_cancel_dialog.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    }).show();


                        } else {
                            ToastUtils.show(context, "部分权限未正常授予");
                            XXPermissions.gotoPermissionSettings(context);
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        ToastUtils.show(context, "被永久拒绝授权，请手动授予权限");
                        XXPermissions.gotoPermissionSettings(context);
                    }
                });
    }

    private void deleteVideo(final FragmentActivity context, String momentId, final int itemPosition) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("momentId", momentId);

        HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.DELETE, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                if (Boolean.valueOf(response)) {
                    if (onDeleteVideoListener != null) {
                        onDeleteVideoListener.deleteVideo(-1);
                    }

                    ToastUtils.show(context, "删除成功");
                    if (context instanceof VideoActivity) {
                        ((VideoActivity) context).videoRecyclerView.scrollToPosition(itemPosition - 1);
                        ((VideoActivity) context).videoRecyclerView.smoothScrollToPosition(itemPosition);//这句调用是为了触发onScrollStateChanged，从而播放视频
//                        context.finish();
                    }
                    dismissDialog();

                }

            }

            @Override
            public void onFailure(String t) {
                if (onDeleteVideoListener != null)
                    onDeleteVideoListener.deleteVideo(-1);
            }
        });
    }

    public void setOnDeleteVideoListener(OnDeleteVideoListener onDeleteVideoListener) {
        this.onDeleteVideoListener = onDeleteVideoListener;

    }

    public interface OnDeleteVideoListener {
        void deleteVideo(int state);

    }


    private void downloadMp4(final Context context,String momentId, String mp4Path, String userId, String desUserId) {
        final File videoSavePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/");
        final String fileName;
        if (null != userId && null != desUserId && userId.equals(desUserId)) {//自己的下载无水印的
            fileName = mp4Path.substring(mp4Path.lastIndexOf("/") + 1, mp4Path.length());
        } else {//别的下载有水印的
            String mp4StampName = mp4Path.substring(mp4Path.lastIndexOf("/") + 1, mp4Path.length());
            fileName = MD5Util.md5(mp4StampName) + ".mp4";
            final String mp4StampPath = mp4Path.substring(0, mp4Path.lastIndexOf("/") + 1);
            LogUtil.i("mp4StampPath + s=====" + mp4StampPath + fileName);
            mp4Path = mp4StampPath + fileName;
        }

        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("userId", userId);
        paramsMap.put("momentId", momentId);
        paramsMap.put("desUserId", desUserId);
        paramsMap.put("likeStatus", "2");
        HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.FORWARD, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
            }

            @Override
            public void onFailure(String t) {
            }
        });

        EasyHttp.downLoad(mp4Path)
                .savePath(videoSavePath.getAbsolutePath())
                .saveName(fileName)//不设置默认名字是时间戳生成的
                .execute(new DownloadProgressCallBack<String>() {

                    private SYDialog show;
                    private CustomCircleProgressBar cpb_progress;

                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        int progress = (int) (bytesRead * 100 / contentLength);
                        cpb_progress.setProgress((int) bytesRead);
                        cpb_progress.setMaxProgress((int) contentLength);

                        LogUtil.i("mp4进度====" + progress);
                    }

                    @Override
                    public void onStart() {
                        show = new SYDialog.Builder(context)
                                .setDialogView(R.layout.dialog_progress)
//                                .setWindowBackgroundP(0.5f)
//                                .setScreenWidthP(1.0f)
                                .setGravity(Gravity.CENTER)
                                .setCancelable(true)
                                .setCancelableOutSide(false)
                                .setBuildChildListener(new IDialog.OnBuildListener() {
                                    @Override
                                    public void onBuildChildView(final IDialog dialog, View view, int layoutRes, FragmentManager fragmentManager) {
                                        cpb_progress = view.findViewById(R.id.cpb_progress);
                                    }
                                }).show();
                        //开始下载
                    }

                    @Override
                    public void onComplete(String path) {
                        LogUtil.i("mp4进度====下载成功");
//                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(path)));
                        ToastUtils.show(context, "已保存至系统相册  " + videoSavePath + "/" + fileName);
                        Utils.scanFile(context, path);
                        show.dismiss();
                    }

                    @Override
                    public void onError(ApiException e) {
                        ToastUtils.show(context, "下载失败" + e.getCode());
                        LogUtil.i("mp4进度====下载失败==" + e.getMessage());
                        //下载失败
                        show.dismiss();
                    }
                });


    }


    private void getData(long since, String slipType) {

        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("pageSize", pageSize + "");
        paramsMap.put("since", since + "");
        paramsMap.put("slipType", slipType);
        paramsMap.put("momentId", momentId);

        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.LIST, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                List<CommentListBean> commentListBeanList = gson.fromJson(response, new TypeToken<List<CommentListBean>>() {
                }.getType());
                int size = commentListBeanList.size();
                commentListBeans.addAll(commentListBeanList);
                commentAdapter.setEnableLoadMore(true);
                if (isAdd) {
                    rv_dialogComment_commentList.setVisibility(View.VISIBLE);
                    tv_dialogComment_hint.setVisibility(View.GONE);
                    commentAdapter.addData(commentListBeanList);
                } else {
                    rv_dialogComment_commentList.setVisibility(View.VISIBLE);
                    tv_dialogComment_hint.setVisibility(View.GONE);
                    commentAdapter.setNewData(commentListBeanList);
                }
                if (commentAdapter.getData().size() == 0) {
                    tv_dialogComment_hint.setVisibility(View.VISIBLE);
                    rv_dialogComment_commentList.setVisibility(View.GONE);
                }
                if (size < pageSize) {
                    //第一页如果不够一页就不显示没有更多数据布局
                    commentAdapter.loadMoreEnd(false);
                } else {
                    commentAdapter.loadMoreComplete();
                }
            }

            @Override
            public void onFailure(String t) {

            }
        });
    }

    /**
     * 分享的adapter
     */
    class ShareAdapter extends BaseQuickAdapter<ShareBean, BaseViewHolder> {
        public ShareAdapter() {
            super(R.layout.item_dialog_share);
        }

        @Override
        protected void convert(BaseViewHolder helper, ShareBean item) {
            helper.setImageResource(R.id.img_dialogShare_img, item.imgRes);
            helper.setText(R.id.tv_dialogShare_title, item.title);
        }
    }

    /**
     * 评论的adapter
     */
    class CommentAdapter extends BaseQuickAdapter<CommentListBean, BaseViewHolder> {

        @Override
        public void setLoadMoreView(LoadMoreView loadingView) {
            super.setLoadMoreView(new CustomLoadMoreView());
        }

        public CommentAdapter() {
            super(R.layout.item_dialog_comment);
        }

        int status = 1;// 0点赞 1未点赞

        @Override
        protected void convert(final BaseViewHolder helper, final CommentListBean item) {
            ImageView img_comment_head = helper.getView(R.id.img_comment_head);
            final ImageView img_comment_praise = helper.getView(R.id.img_comment_praise);
            LinearLayout ll_comment_praise = helper.getView(R.id.ll_comment_praise);
            GlideUtil.loadCircleImg(Utils.isUrl(item.avatar), img_comment_head);
            String nickName = item.nickname;
            String subniname;
            if (nickName.length() > 10) {
                subniname = nickName.substring(0, 10) + "...";
            } else {
                subniname = nickName;
            }
            helper.setText(R.id.tv_comment_name, subniname);
            helper.setText(R.id.tv_comment_time, item.createTime);
            if (item.desUserId != null) {
                String str = (item.desNickname == null ? "" : "回复:@" + item.desNickname)
                        + " " + item.content;
                SpannableString builder = new SpannableString(str);
                if (!TextUtils.isEmpty(item.desNickname)) {
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FE5175")),
                            3, 3 + item.desNickname.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    helper.setText(R.id.tv_comment_info, builder);
                }
            } else {
                helper.setText(R.id.tv_comment_info, item.content);
            }
            final TextView img_comment_praiseNum = helper.getView(R.id.img_comment_praiseNum);

            img_comment_praiseNum.setText(item.likeCount + "");
            if (item.isLike == 1) {
                img_comment_praise.setImageResource(R.drawable.ic_praise_later1);
            } else {
                img_comment_praise.setImageResource(R.drawable.ic_praise1);
            }

            ll_comment_praise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//评论点赞
                    if (!SPUtils.isLogin(mContext)) {
                        LoginActivity.open(mContext, -1);
                        return;
                    }

                    if (item.isLike == 1) {
                        status = 1;
                    } else {
                        status = 0;
                    }

                    Map<String, String> paramsMap = new HashMap<String, String>();
                    paramsMap.put("momentId", momentId);
                    paramsMap.put("commentId", item.commentId);
                    paramsMap.put("commentUserId", item.userId);
                    paramsMap.put("status", status + "");

                    HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.LIKEORDISLIKE, paramsMap, new HttpRequest.HttpResponseCallBank() {
                        @Override
                        public void onSuccess(String response) {
                            if (status == 1) {
                                item.isLike = 0;
                                int likeCount = Integer.parseInt(img_comment_praiseNum.getText().toString().trim()) - 1;
                                img_comment_praise.setImageResource(R.drawable.ic_praise1);
                                helper.setText(R.id.img_comment_praiseNum, likeCount + "");
                            } else {
                                item.isLike = 1;
                                img_comment_praise.setImageResource(R.drawable.ic_praise_later1);
                                int likeCount = Integer.parseInt(img_comment_praiseNum.getText().toString().trim()) + 1;
                                helper.setText(R.id.img_comment_praiseNum, likeCount + "");
                            }
                        }

                        @Override
                        public void onFailure(String t) {
                        }
                    });
                }
            });


        }


    }


    /**
     * 关闭弹窗 注意dialog=null;防止内存泄漏
     */
    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    // 键盘关闭
    public void closeInputSoft(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
