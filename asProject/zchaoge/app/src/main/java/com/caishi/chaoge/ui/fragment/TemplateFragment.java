package com.caishi.chaoge.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseFragment;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.RecommendBean;
import com.caishi.chaoge.request.TemplateListRequest;
import com.caishi.chaoge.ui.activity.TemplateActivity;
import com.caishi.chaoge.ui.activity.TemplateEditActivity;
import com.caishi.chaoge.utils.ConstantUtils;
import com.caishi.chaoge.utils.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.othershe.library.NiceImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateFragment extends BaseFragment {
    private static final String MODEL_TYPE = "modelType";

    private String modelType;
    private RecyclerView rv_templateFragment_template;
    private SmartRefreshLayout refreshLayout;
    private TemplateFragmentAdapter templateFragmentAdapter;
    private int pageSize = 10;
    private boolean isAddData = false;
    private ArrayList<RecommendBean> templateList = new ArrayList<>();

    public static TemplateFragment newInstance(String modelType) {
        TemplateFragment fragment = new TemplateFragment();
        Bundle args = new Bundle();
        args.putString(MODEL_TYPE, modelType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modelType = getArguments().getString(MODEL_TYPE);
        }
    }

    @Override
    protected int initContentView() {
        return R.layout.fragment_template;
    }

    @Override
    protected void initView(View view) {
        rv_templateFragment_template = $(R.id.rv_templateFragment_template);
        refreshLayout = $(R.id.refreshLayout);
    }

    @Override
    protected void doBusiness() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        rv_templateFragment_template.setLayoutManager(gridLayoutManager);
        templateFragmentAdapter = new TemplateFragmentAdapter();
        rv_templateFragment_template.setAdapter(templateFragmentAdapter);
        getData(-1, ConstantUtils.DOWN);

    }

    @Override
    public void setListener() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                templateList.clear();
                isAddData = false;
                getData(-1, ConstantUtils.DOWN);
            }
        });
        templateFragmentAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isAddData = true;
                getData(templateList.get(templateList.size() - 1).targetTime, ConstantUtils.UP);
            }
        }, rv_templateFragment_template);

        templateFragmentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<RecommendBean> data = templateFragmentAdapter.getData();
                Bundle bundle = new Bundle();
                bundle.putString("photoPath", ((TemplateActivity) mContext).photoPath);
                bundle.putString("modelId", data.get(position).modelId);
                bundle.putInt("editFlag", 0);
                startActivity(TemplateEditActivity.class, bundle);


            }
        });
    }

    @Override
    public void widgetClick(View v) {

    }


    private void getData(long since, String slipType) {
        Map<String, Object> map = new HashMap<>();
        map.put("modelType", modelType);
        map.put("pageSize", pageSize);
        map.put("since", since);
        map.put("slipType", slipType);
        TemplateListRequest.newInstance((BaseActivity) mContext).getModelList(map, new BaseRequestInterface<ArrayList<RecommendBean>>() {
            @Override
            public void success(int state, String msg, ArrayList<RecommendBean> recommendBeans) {
                refreshLayout.finishRefresh();
                templateFragmentAdapter.setEnableLoadMore(true);
                templateList.addAll(recommendBeans);
                if (!isAddData) {
                    templateFragmentAdapter.setNewData(templateList);
                } else {
                    if (recommendBeans.size() > 0) {
                        templateFragmentAdapter.addData(recommendBeans);
                    }
                }
                if (recommendBeans.size() < pageSize) {
                    //第一页如果不够一页就不显示没有更多数据布局
                    templateFragmentAdapter.loadMoreEnd(false);
                } else {
                    templateFragmentAdapter.loadMoreComplete();
                }


            }

            @Override
            public void error(int state, String msg) {
                refreshLayout.finishRefresh();
            }
        });
    }


    /**
     * 选择模板的adapter
     */
    class TemplateFragmentAdapter extends BaseQuickAdapter<RecommendBean, BaseViewHolder> {
        public TemplateFragmentAdapter() {
            super(R.layout.item_template_fragment);
        }

        @Override
        protected void convert(BaseViewHolder helper, RecommendBean item) {
            NiceImageView img_templateFragment_cover = helper.getView(R.id.img_templateFragment_cover);
//            ImageView img_templateFragment_state = helper.getView(R.id.img_templateFragment_state);
//            RequestOptions options = new RequestOptions()
//                    .placeholder(R.drawable.im_pic_loading)
//                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
//            Glide.with(mContext).load(item.modelCover).apply(options).into(img_templateFragment_cover);
            GlideUtil.loadImg(item.modelCover, img_templateFragment_cover, R.drawable.im_pic_loading);
            img_templateFragment_cover.setBorderColor(Color.parseColor("#FFFFFF"));
            img_templateFragment_cover.setBorderWidth(2);
//            img_templateFragment_state.setVisibility(item.isSelect ? View.VISIBLE : View.GONE);

        }
    }
}
