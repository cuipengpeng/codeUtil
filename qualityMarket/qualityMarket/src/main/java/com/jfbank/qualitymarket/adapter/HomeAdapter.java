package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.flyco.banner.widget.Banner.base.BaseBanner;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.ProductFloorBean;
import com.jfbank.qualitymarket.model.PrudouctBean;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.TDUtils;
import com.jfbank.qualitymarket.widget.BannerGoodsView;
import com.jfbank.qualitymarket.widget.BetterRecyclerView;
import com.jfbank.qualitymarket.widget.ForceClickImageView;
import com.jfbank.qualitymarket.widget.NoScrollGridView;
import com.jfbank.qualitymarket.widget.NoScrollListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollStateListener;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static me.everything.android.ui.overscroll.IOverScrollState.STATE_BOUNCE_BACK;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_IDLE;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private Context mContext;
    private List<ProductFloorBean> data = new ArrayList<>();

    public void upateData(boolean isRefresh, List<ProductFloorBean> data) {
        if (isRefresh) {
            this.data.clear();
        }
        if (!CommonUtils.isEmptyList(data)) {
            this.data.addAll(data);
        }
        notifyDataSetChanged();
    }


    public HomeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return CommonUtils.isEmptyList(data) ? 0 : data.size();
    }

    @Override
    public int getItemViewType(int position) {//获取显示板式内容
        return data.get(position).getLayoutType();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {//板式1
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home_goods_one, parent, false);
            return new ViewHolder(itemView, viewType);
        } else if (viewType == 2) {//板式2
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home_goods_two, parent, false);
            return new ViewHolder(itemView, viewType);
        } else if (viewType == 3) {//板式3
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home_goods_three, parent, false);
            return new ViewHolder(itemView, viewType);
        } else if (viewType == 4) {//板式4
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home_goods_four, parent, false);
            return new ViewHolder(itemView, viewType);
        } else if (viewType == 5) {//板式5
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home_goods_five, parent, false);
            return new ViewHolder(itemView, viewType);
        } else if (viewType == 6) {//板式6
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home_goods_six, parent, false);
            return new ViewHolder(itemView, viewType);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home_goods_one, parent, false);
            return new ViewHolder(itemView, viewType);
        }


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int itemType = getItemViewType(position);
//        公共板式分类type
        setGoodsTypeData(holder, position);
        //設置Banner
        setBannerViewData(holder, position);
        //設置活動或者商品顯示
        setGoodsInfoData(holder, position, itemType);
    }

    /**
     * 設置商品顯示
     *
     * @param holder
     * @param position
     * @param itemType
     */
    private void setGoodsInfoData(ViewHolder holder, final int position, int itemType) {
        final List<PrudouctBean> list = data.get(position).getPrudouct();
        //首页板式1和2,3商品设置
        if (itemType == 1 || itemType == 2 || itemType == 3) {
            holder.produceAdapter = new HomeGoodsInfoAdapter(mContext, itemType);
            if (itemType == 1) {
                holder.nslvGoods.setAdapter(holder.produceAdapter);
                holder.nslvGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        CommonUtils.startGoodsDeteail(mContext, list.get(i).getProductNo());
                        TDUtils.onEvent(mContext, "100007", data.get(position).getName(), TDUtils.getInstance().putParams("产品名称", list.get(i).getProName()).putParams("产品编号", list.get(i).getProductNo()).buildParams());
                    }
                });
            } else {
                holder.nsgvGoods.setAdapter(holder.produceAdapter);
                holder.nsgvGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        CommonUtils.startGoodsDeteail(mContext, list.get(i).getProductNo());
                        TDUtils.onEvent(mContext, "100007", data.get(position).getName(), TDUtils.getInstance().putParams("产品名称", list.get(i).getProName()).putParams("产品编号", list.get(i).getProductNo()).buildParams());
                    }
                });
            }
            holder.produceAdapter.updateData(list);
        }
        //首页板式4部分
        else if (itemType == 4) {
            holder.goodsInfoHorAdapter.updateData(list);
            // 设置水平拉升
            IOverScrollDecor decor = OverScrollDecoratorHelper.setUpOverScroll(holder.rlGoods, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
            decor.setOverScrollStateListener(new IOverScrollStateListener() {//拉升监听
                @Override
                public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
                    boolean isLelfDrag = false;
                    if (newState == 2) {//判断是否向左拉升
                        isLelfDrag = true;
                    }
                    switch (newState) {//
                        case STATE_IDLE://拉升结束
                            if (oldState == STATE_BOUNCE_BACK && isLelfDrag) {//向左拉升，且拉升结束
//                            //跳转到秒杀
                                isLelfDrag = false;
                                if (!CommonUtils.isEmptyList(data.get(position).getBanner()))
                                    CommonUtils.startWebViewActivity(mContext, data.get(position).getBanner().get(0).getH5Url(), true, false);
                            }
                            break;
                    }
                }
            });
        }
        //首页板式5部分
        else if (itemType == 5) {//
            if (!CommonUtils.isEmptyList(list) && list.size() >= 3) {
                holder.llGoodsShow.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(list.get(0).getPicture()).placeholder(R.mipmap.ic_default_goodsimg_three1).into(holder.fivGoodsOne);
                Picasso.with(mContext).load(list.get(1).getPicture()).placeholder(R.mipmap.ic_default_goodsimg_three2).into(holder.fivGoodsTwo);
                Picasso.with(mContext).load(list.get(2).getPicture()).placeholder(R.mipmap.ic_default_goodsimg_three2).into(holder.fivGoodsThree);
                holder.fivGoodsOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startGoodsOrActivity(list.get(0).getUrlType(), list.get(0).getUrlContent());
                        TDUtils.onEvent(mContext, "100007", data.get(position).getName(), TDUtils.getInstance().putParams("产品名称", list.get(0).getProName()).putParams("产品编号", list.get(0).getUrlContent()).buildParams());

                    }
                });
                holder.fivGoodsTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startGoodsOrActivity(list.get(1).getUrlType(), list.get(1).getUrlContent());
                        TDUtils.onEvent(mContext, "100007", data.get(position).getName(), TDUtils.getInstance().putParams("产品名称", list.get(1).getProName()).putParams("产品编号", list.get(1).getUrlContent()).buildParams());

                    }
                });
                holder.fivGoodsThree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startGoodsOrActivity(list.get(2).getUrlType(), list.get(2).getUrlContent());
                        TDUtils.onEvent(mContext, "100007", data.get(position).getName(), TDUtils.getInstance().putParams("产品名称", list.get(2).getProName()).putParams("产品编号", list.get(2).getUrlContent()).buildParams());

                    }
                });
            } else {
                holder.llGoodsShow.setVisibility(View.GONE);
            }
        }
        ///首页板式6部分
        else if (itemType == 6) {
            holder.goodsImageAdapter = new HomeGoodsImageAdapter(mContext);
            holder.nsgvGoods.setAdapter(holder.goodsImageAdapter);
            holder.goodsImageAdapter.updateData(list);
            holder.nsgvGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    PrudouctBean productBean = list.get(i);
                    TDUtils.onEvent(mContext, "100007", data.get(position).getName(), TDUtils.getInstance().putParams("产品名称", productBean.getProName()).putParams("产品编号", productBean.getUrlContent()).buildParams());
                    startGoodsOrActivity(productBean.getUrlType(), productBean.getUrlContent());
                }
            });
        }
    }

    /**
     * 公共板式分类type
     *
     * @param holder
     * @param position
     */
    private void setGoodsTypeData(ViewHolder holder, final int position) {
        //公共板式分类type
        if (!TextUtils.isEmpty(data.get(position).getImage())) {
            holder.fivGoodsType.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(data.get(position).getImage()).placeholder(R.mipmap.ic_default_hometype).into(holder.fivGoodsType);
//            holder.fivGoodsType.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    TDUtils.onEvent(mContext, "100007", data.get(position).getName() + "-更多");
//                    CommonUtils.startIntent(mContext, data.get(position).getAppPage(), data.get(position).getAppParams());
//                }
//            });
        } else {
            holder.fivGoodsType.setVisibility(View.GONE);
        }
    }

    /**
     * banner显示展示
     *
     * @param holder
     * @param position
     */
    private void setBannerViewData(ViewHolder holder, final int position) {
        //banner显示展示
        if (!CommonUtils.isEmptyList(data.get(position).getBanner())) {
            if (data.get(position).getBanner().size() == 1) {
                holder.sibGoodstyebanner.setIndicatorShow(false);
                holder.sibGoodstyebanner.setAutoScrollEnable(false);
            } else {
                holder.sibGoodstyebanner.setIndicatorShow(true);
                holder.sibGoodstyebanner.setAutoScrollEnable(true);
            }
            holder.sibGoodstyebanner.setSource(data.get(position).getBanner()).startScroll();
            holder.sibGoodstyebanner.setVisibility(View.VISIBLE);
            holder.sibGoodstyebanner.setOnItemClickL(new BaseBanner.OnItemClickL() {
                @Override
                public void onItemClick(int posi) {
                    TDUtils.onEvent(mContext, "100007", data.get(position).getName() + "-广告栏", TDUtils.getInstance().putParams("活动名称", data.get(position).getBanner().get(posi).getName()).putParams("活动", data.get(position).getBanner().get(posi).getAppParams()).buildParams());
                    CommonUtils.startWebViewActivity(mContext, data.get(position).getBanner().get(posi).getH5Url(), true, false);
                }
            });
            if (getItemViewType(position)==4){
                holder.smartRecyclerAdapter.setFooterView(holder.viewMore);
                holder.viewMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!CommonUtils.isEmptyList(data.get(position).getBanner()))
                            CommonUtils.startWebViewActivity(mContext, data.get(position).getBanner().get(0).getH5Url(), true, false);
                    }
                });
            }
        } else {
            if (getItemViewType(position)==4){
                holder.smartRecyclerAdapter.removeFooterView();
            }
            holder.sibGoodstyebanner.setVisibility(View.GONE);
            holder.sibGoodstyebanner.pauseScroll();
        }
    }

    /**
     * 产品跳转
     *
     * @param urlType   PRODUCT：商品详情；WEBONLY：url网页
     * @param urContent
     */
    private void startGoodsOrActivity(String urlType, String urContent) {
        if ("PRODUCT".equals(urlType)) {//PRODUCT,商品编号;WEBONLY,页面链接
            if (!TextUtils.isEmpty(urContent))
                CommonUtils.startGoodsDeteail(mContext, urContent);
        } else if ("WEBONLY".equals(urlType)) {
            if (!TextUtils.isEmpty(urContent) && urContent.startsWith("http"))
                CommonUtils.startWebViewActivity(mContext, urContent, false, false);
        }
    }

    /**
     * 板式ViewHodler初始化View
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        ForceClickImageView fivGoodsType;//板式分类背景
        BannerGoodsView sibGoodstyebanner;//板式banner

        NoScrollListView nslvGoods;//板式1
        NoScrollGridView nsgvGoods;//板式2;3
        BetterRecyclerView rlGoods;//板式4
        View viewMore;//板式4
        HomeGoodsInfoAdapter produceAdapter;
        HomeGoodsImageAdapter goodsImageAdapter;
        HomeGoodsInfoHorAdapter goodsInfoHorAdapter;
        SmartRecyclerAdapter smartRecyclerAdapter;
        ForceClickImageView fivGoodsOne;//板式5-商品活动1
        ForceClickImageView fivGoodsTwo;//板式5-商品活动2
        ForceClickImageView fivGoodsThree;//板式5-商品活动3
        LinearLayout llGoodsShow;//板式5

        public ViewHolder(View convertView, int itemType) {
            super(convertView);
            //首页板式公共部分
            fivGoodsType = (ForceClickImageView) convertView.findViewById(R.id.fiv_goods_type);
            sibGoodstyebanner = (BannerGoodsView) convertView.findViewById(R.id.sib_goodstyebanner);
            //板式组件初始化
            if (itemType == 1) {
                nslvGoods = (NoScrollListView) convertView.findViewById(R.id.nslv_goods);
            }
            if (itemType == 2 || itemType == 3 || itemType == 6) {
                nsgvGoods = (NoScrollGridView) convertView.findViewById(R.id.nsgv_goods);
            }
            if (itemType == 4) {
                rlGoods = (BetterRecyclerView) convertView.findViewById(R.id.rl_goods);
                //设置布局管理器
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                rlGoods.setLayoutManager(linearLayoutManager);
                goodsInfoHorAdapter = new HomeGoodsInfoHorAdapter(mContext);
                smartRecyclerAdapter = new SmartRecyclerAdapter(goodsInfoHorAdapter);
                viewMore = LayoutInflater.from(mContext).inflate(R.layout.item_goods_more, rlGoods, false);
                rlGoods.setAdapter(smartRecyclerAdapter);

            }
            if (itemType == 5) {
                llGoodsShow = (LinearLayout) convertView.findViewById(R.id.ll_goods_show);
                fivGoodsOne = (ForceClickImageView) convertView.findViewById(R.id.fiv_goods_one);
                fivGoodsTwo = (ForceClickImageView) convertView.findViewById(R.id.fiv_goods_two);
                fivGoodsThree = (ForceClickImageView) convertView.findViewById(R.id.fiv_goods_three);
            }
        }
    }
}
