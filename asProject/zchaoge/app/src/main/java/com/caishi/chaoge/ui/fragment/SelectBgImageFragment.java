package com.caishi.chaoge.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataFragment;
import com.caishi.chaoge.bean.GetBackGroundBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.ui.activity.SelectBgImageActivity;
import com.caishi.chaoge.utils.FileUtils;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.http.RequestURL;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.othershe.library.NiceImageView;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static com.caishi.chaoge.utils.ConstantUtils.DOWNLOAD_BG_PATH;

public class SelectBgImageFragment extends BaseUILocalDataFragment {
    @BindView(R.id.rv_selectBgImage_list)
    RecyclerView rv_selectBgImage_list;

    private static String BACKGROUND_TYPE = "backGroundType";
    private static String PAGE_POSITION = "position";
    private static String ARG_PARAM2;
    private String backGroundType;
    private int pageSize = 9;
    final String UP = "UP";
    final String DOWN = "DOWN";
    long since = -1;
    private SelectBgImageAdapter selectBgImageAdapter;
    public boolean isAdd = false;
    public List<GetBackGroundBean> data;
    private GridLayoutManager gridLayoutManager;
    private int scrollY = -20;
    private int Y = 1;
    private int pagePosition = -1;
    private OnSelectImageListener onSelectImageListener;
    private int lastPosition = -1;

    public static SelectBgImageFragment newInstance(String param1, int param2) {
        SelectBgImageFragment fragment = new SelectBgImageFragment();
        Bundle args = new Bundle();
        args.putString(BACKGROUND_TYPE, param1);
        args.putInt(PAGE_POSITION, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.fragment_select_bg_image;
    }

    @Override
    protected void initPageData() {
        if (getArguments() != null) {
            backGroundType = getArguments().getString(BACKGROUND_TYPE);
            pagePosition = getArguments().getInt(PAGE_POSITION);
        }
        gridLayoutManager = new GridLayoutManager(mContext, 3);
        rv_selectBgImage_list.setLayoutManager(gridLayoutManager);
        selectBgImageAdapter = new SelectBgImageAdapter();
        rv_selectBgImage_list.setAdapter(selectBgImageAdapter);
        selectBgImageAdapter.bindToRecyclerView(rv_selectBgImage_list);


        rv_selectBgImage_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (videoRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
//                    Y = scrollY;
//                    scrollY = 0;
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollY += dy;
//                if (videoRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
//                    Y = scrollY;
//                    scrollY = 0;
//                }
                LogUtil.d("dy=====" + dy);
                LogUtil.d("iResult=====" + scrollY);
                RelativeLayout.LayoutParams albumLp = (RelativeLayout.LayoutParams) ((SelectBgImageActivity) getActivity()).img_SelectBgImage_album.getLayoutParams();
                RelativeLayout.LayoutParams photoLp = (RelativeLayout.LayoutParams) ((SelectBgImageActivity) getActivity()).img_SelectBgImage_photo.getLayoutParams();
//                if (dy >= 0) {
//
//                    LogUtil.d("albumLp=====" + albumLp.getMarginEnd());
//                    if (scrollY < 180) {
//                        albumLp.rightMargin = -scrollY;
//                        photoLp.rightMargin = -scrollY;
//                    } else {
//                        albumLp.rightMargin = -180;
//                        photoLp.rightMargin = -180;
//                    }
//                } else {
//                    if (Y - scrollY > 180) {
//                        albumLp.rightMargin = 20;
//                        photoLp.rightMargin = 20;
//                    } else {
//                        albumLp.rightMargin = -(180 - (Y - scrollY));
//                        photoLp.rightMargin = -(180 - (Y - scrollY));
//                    }
//                }
                albumLp.rightMargin = -scrollY;
                photoLp.rightMargin = -scrollY;

                ((SelectBgImageActivity) getActivity()).img_SelectBgImage_album.setLayoutParams(albumLp);
                ((SelectBgImageActivity) getActivity()).img_SelectBgImage_photo.setLayoutParams(photoLp);
            }
        });

        selectBgImageAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isAdd = true;
                ArrayList<GetBackGroundBean> resultsArrayList = (ArrayList<GetBackGroundBean>) selectBgImageAdapter.getData();
                getData(resultsArrayList.
                        get(resultsArrayList.size() - 1).targetTime, UP);
            }
        }, rv_selectBgImage_list);

        selectBgImageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                ProgressBar pb_progress = (ProgressBar) selectBgImageAdapter.getViewByPosition(position, R.id.pb_progress);
                data = selectBgImageAdapter.getData();
                if (lastPosition == position && !data.get(position).isDownload) {
                    return;
                }
                if (data.get(position).isDownload) {
                    if (onSelectImageListener != null) {
                        String path = "";
                        int bgType = selectBgImageAdapter.getData().get(position).backGroundClass;
                        if (bgType == 0)
                            path = data.get(position).imageUrl;
                        else if (bgType == 2) {
                            path = data.get(position).videoUrl;
                        }
                        onSelectImageListener.onSelectImage(pagePosition, path, data.get(position).backGroundId);
                    }
                    for (int i = 0; i < data.size(); i++) {
                        selectBgImageAdapter.getData().get(i).isSelect = (position == i);
                    }
                    selectBgImageAdapter.notifyDataSetChanged();
                } else {
                    String url = "";
                    int bgType = selectBgImageAdapter.getData().get(position).backGroundClass;
                    if (bgType == 0)
                        url = data.get(position).imageUrl;
                    else if (bgType == 2) {
                        url = data.get(position).videoUrl;
                    }
                    downloadImage(url, pb_progress, position);
                }
                lastPosition = position;
            }
        });

        getData(since, DOWN);

    }

    public void upData() {
        if (null != selectBgImageAdapter) {
            for (int i = 0; i < selectBgImageAdapter.getData().size(); i++) {
                if (selectBgImageAdapter.getData().get(i).isSelect)
                    selectBgImageAdapter.getData().get(i).isSelect = false;
            }
            selectBgImageAdapter.notifyDataSetChanged();
        }
    }

    private void downloadImage(String url, final ProgressBar pb_progress, final int position) {
        String name = url.substring(url.lastIndexOf("/") + 1);
        EasyHttp.downLoad(url)
                .savePath(DOWNLOAD_BG_PATH)
                .saveName(name)//不设置默认名字是时间戳生成的
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        int progress = (int) (bytesRead * 100 / contentLength);
                        pb_progress.setProgress(progress);
                    }

                    @Override
                    public void onStart() {
                        pb_progress.setProgress(0);
                        //开始下载
                        pb_progress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onComplete(String path) {
                        pb_progress.setVisibility(View.GONE);
                        data.get(position).isDownload = true;
                        int bgType = data.get(position).backGroundClass;
                        if (bgType == 0)
                            data.get(position).imageUrl = path;
                        else if (bgType == 2) {
                            data.get(position).videoUrl = path;
                        }
                        selectBgImageAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onError(ApiException e) {
                        //下载失败
                    }
                });
    }

    private void getData(long since, String slipType) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("backGroundType", backGroundType);
        paramsMap.put("pageSize", 9 + "");
        paramsMap.put("since", since + "");
        paramsMap.put("slipType", slipType);

        HttpRequest.post(false, RequestURL.BASE_URL_V2 + "/" + RequestURL.GET_BACK_GROUND_IMAGE_LIST, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                List<GetBackGroundBean> getBackGroundBeanList = gson.fromJson(response, new TypeToken<List<GetBackGroundBean>>() {
                }.getType());
                selectBgImageAdapter.setEnableLoadMore(true);
                final List<File> fontFiles = FileUtils.getFontFiles(DOWNLOAD_BG_PATH);
                List<GetBackGroundBean> bgImageList = getBackGroundBeanList;
                if (fontFiles.size() > 0) {
                    for (int j = 0; j < fontFiles.size(); j++) {
                        for (int i = 0; i < bgImageList.size(); i++) {
                            String path = "";
                            int bgType = bgImageList.get(i).backGroundClass;
                            if (bgType == 0)
                                path = bgImageList.get(i).imageUrl;
                            else if (bgType == 2) {
                                path = bgImageList.get(i).videoUrl;
                            }
                            String name = path.substring(path.lastIndexOf("/") + 1);
                            if (fontFiles.get(j).getName().equals(name)) {
                                bgImageList.get(i).isDownload = true;
                                if (bgType == 0)
                                    bgImageList.get(i).imageUrl = fontFiles.get(j).getAbsolutePath();
                                else if (bgType == 2) {
                                    bgImageList.get(i).videoUrl = fontFiles.get(j).getAbsolutePath();
                                }
                            }
                        }
                    }
                }
                if (isAdd) {
//                    rv_like_list.setVisibility(View.VISIBLE);
//                    ll_data_null.setVisibility(View.GONE);
                    selectBgImageAdapter.addData(bgImageList);
                } else {
//                    rv_like_list.setVisibility(View.VISIBLE);
//                    ll_data_null.setVisibility(View.GONE);
                    selectBgImageAdapter.setNewData(bgImageList);
                }
                if (selectBgImageAdapter.getData().size() == 0) {
//                    ll_data_null.setVisibility(View.VISIBLE);
//                    rv_like_list.setVisibility(View.GONE);
                }
                if (getBackGroundBeanList.size() < pageSize) {
                    //第一页如果不够一页就不显示没有更多数据布局
                    selectBgImageAdapter.loadMoreEnd(false);
                } else {
                    selectBgImageAdapter.loadMoreComplete();
                }


            }

            @Override
            public void onFailure(String t) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    /**
     * 选择图片的adapter
     */
    class SelectBgImageAdapter extends BaseQuickAdapter<GetBackGroundBean, BaseViewHolder> {
        public SelectBgImageAdapter() {
            super(R.layout.item_select_bg_image);
        }

        @Override
        protected void convert(BaseViewHolder helper, GetBackGroundBean item) {
            NiceImageView img_item_img = helper.getView(R.id.img_item);
            NiceImageView img_item_cover = helper.getView(R.id.img_item_cover);//蒙层 未下载的显示
            ImageView img_item_state = helper.getView(R.id.img_item_state);//下载，以及选择的显示状态
            ProgressBar pb_progress = helper.getView(R.id.pb_progress);//下载进度条
            if (item.backGroundClass == 0) {
                GlideUtil.loadImg(item.imageUrl, img_item_img, R.drawable.im_pic_loading);
            }
            if (item.backGroundClass == 2) {
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.im_pic_loading)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                Glide.with(mContext).load(item.cover).apply(options).into(img_item_img);
            }
            if (item.isDownload) {
                img_item_cover.setVisibility(View.GONE);
                if (item.isSelect) {
                    img_item_state.setVisibility(View.VISIBLE);
                    GlideUtil.loadImg(R.drawable.ic_selected1, img_item_state);
                } else {
                    img_item_state.setVisibility(View.GONE);
                }
            } else {
                img_item_state.setVisibility(View.VISIBLE);
                img_item_cover.setVisibility(View.VISIBLE);
                GlideUtil.loadImg(R.drawable.im_download, img_item_state);
            }


        }
    }

    public void setOnSelectImageListener(OnSelectImageListener onSelectImageListener) {
        this.onSelectImageListener = onSelectImageListener;
    }

    public interface OnSelectImageListener {
        void onSelectImage(int position, String path, String backGroundId);
    }
}
