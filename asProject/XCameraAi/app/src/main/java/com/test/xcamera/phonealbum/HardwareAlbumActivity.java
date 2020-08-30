//package com.meetvr.aicamera.phonealbum;
//
//import android.app.Activity;
//import android.graphics.Rect;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.meetvr.aicamera.phonealbum.adapter.hardwareadapter.AgendaSimpleSectionAdapter;
//import com.meetvr.aicamera.phonealbum.adapter.hardwareadapter.GridSpacingItemDecoration;
//import com.meetvr.aicamera.phonealbum.adapter.hardwareadapter.SectionedSpanSizeLookup;
//import com.framwork.base.view.MOBaseActivity;
//import com.meetvr.aicamera.utils.DisplayUtils;
//import com.meetvr.aicamera.utils.DownLoadRequest;
//import com.meetvr.aicamera.widget.DialogItem;
//import com.meetvr.aicamera.widget.RingProgressBar;
//import com.meetvr.aicamera.R;
//import com.meetvr.aicamera.bean.MoAlbumItem;
//import com.meetvr.aicamera.managers.ConnectionManager;
//import com.meetvr.aicamera.mointerface.MoAlbumListCallback;
//import com.editvideo.MediaConstant;
//import com.editvideo.MediaData;
//import com.editvideo.MediaUtils;
//import com.editvideo.OnTotalNumChange;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//
//public class HardwareAlbumActivity extends MOBaseActivity implements OnTotalNumChange {
//    @BindView(R.id.left_iv_title)
//    ImageView leftIvTitle;
//    @BindView(R.id.tv_middle_title)
//    TextView tvMiddleTitle;
//    @BindView(R.id.right_tv_titlee)
//    TextView rightTvTitlee;
//    @BindView(R.id.right_iv_title)
//    ImageView rightIvTitle;
//    @BindView(R.id.recy)
//    RecyclerView recy;
//    @BindView(R.id.iv_download)
//    ImageView ivDownload;
//    @BindView(R.id.iv_like)
//    ImageView ivLike;
//    @BindView(R.id.iv_delete)
//    ImageView ivDelete;
//    @BindView(R.id.lin1)
//    LinearLayout lin1;
//    @BindView(R.id.frame_layout)
//    FrameLayout mContentContainer;
//    @BindView(R.id.progress_bar)
//    RingProgressBar progressBar;
//    @BindView(R.id.rl_progress)
//    RelativeLayout rlProgress;
//
//
//    private List<List<MediaData>> mlist = new ArrayList<>();
//    private GridLayoutManager manager;
//
//    List<MediaData> listOfOut = new ArrayList<>();
//    //    private HardwareAlbumAdapter adapter;
////    private LinearLayoutManager manager;
//
//    private int mLimitMediaCount = 1;//-1值表示无限选择视频或者图片数量，若是大于0某一具体值，则表示选中这一数值的视频或者图片
//    private AgendaSimpleSectionAdapter adapter;
////    private PreviewLayout mPreviewLayout;
//
//    List<MoAlbumItem> carmer_list = new ArrayList<>();//相册列表数据
//
//    private static final int DOWN_LOADFILE = 10000;
//
//    @Override
//    public int initView() {
//        return R.layout.activity_hardware;
//    }
//
//    @Override
//    public void initClick() {
//
//    }
//
//    @Override
//    public void initData() {
//        setTextDraw(tvMiddleTitle, R.mipmap.arrow_down);
//        initRecycle();
//        addDialogItem();
//    }
//
//    private MediaUtils.ListOfAllMedia listOfAllMedia;
//
//    public void initRecycle() {
////        adapter = new HardwareAlbumAdapter(mContext, mlist,listOfAllMedia,mContentContainer);
////        adapter.setTitleView(tvMiddleTitle);
////        manager = new LinearLayoutManager(mContext);
////        recy.setLayoutManager(manager);
////        recy.setAdapter(adapter);
//
//        manager = new GridLayoutManager(mContext, 3);
////        MediaUtils.getMediasByType((Activity) mContext, MediaConstant.ALL_MEDIA, new MediaUtils.LocalMediaCallback() {
////            @Override
////            public void onLocalMediaCallback(final List<MediaData> allMediaTemp) {
////                mhandler.post(new Runnable() {
////                    @Override
////                    public void run() {
////                        listOfAllMedia = MediaUtils.groupListByTime(allMediaTemp);
////                        mlist.addAll(listOfAllMedia.getListOfAll());
////                        listOfOut = listOfAllMedia.getListOfParent();
////
////                        adapter = new
////                                AgendaSimpleSectionAdapter(mlist, listOfOut, recy, HardwareAlbumActivity.this, MediaConstant.ALL_MEDIA, (Activity) mContext, 1, mLimitMediaCount, HardwareAlbumActivity.this);
////
////                        recy.setAdapter(adapter);
////                        SectionedSpanSizeLookup lookup = new SectionedSpanSizeLookup(adapter, manager);
////                        manager.setSpanSizeLookup(lookup);
////                        recy.setLayoutManager(manager);
////                        recy.addItemDecoration(new GridSpacingItemDecoration(mContext, 3));
////                        all_list.addAll(getAllList(mlist));
////                        setImagePrieve();
////                    }
////                });
////
////            }
////        });
//
//
////        recy.addOnItemTouchListener(new OnItemClickListener() {
////            @Override
////            public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
////
////            }
////        });
//
//        recy.addOnScrollListener(new MyRecyclerOnScrollListener());
//
//        ConnectionManager.getInstance().getAlbumList(new MoAlbumListCallback() {
//            @Override
//            public void onSuccess(ArrayList<MoAlbumItem> items) {
//                if (items == null || items.size() == 0) {
//                    show("相册数据为空");
//                    return;
//                }
//                carmer_list.addAll(items);
//                //获取到数据集合
//                for (int j = 0; j < items.size(); j++) {
//                    MoAlbumItem item = items.get(j);
//                    System.out.println("获取到的数据是" + item);
//                    MediaData data = new MediaData();
//                    data.setThumbPath(item.getmThumbnail().getmUri());
//                    data.setType("video".equals(item.getmType()) ? MediaConstant.VIDEO : MediaConstant.IMAGE);
//
//                    if (data.getType() == MediaConstant.VIDEO) {
//                        data.setDuration(item.getmVideo().getmDuration());
//                    } else {
//                        data.setPath(item.getmImage().getmUri());
//                    }
//                    data.setData(System.currentTimeMillis());
//                    listOfOut.add(data);
//                }
//
//                mlist.add(listOfOut);
//                mhandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter = new
//                                AgendaSimpleSectionAdapter(mlist, listOfOut, recy, HardwareAlbumActivity.this, MediaConstant.ALL_MEDIA, (Activity) mContext, 1, mLimitMediaCount, HardwareAlbumActivity.this);
//
//                        recy.setAdapter(adapter);
//                        SectionedSpanSizeLookup lookup = new SectionedSpanSizeLookup(adapter, manager);
//                        manager.setSpanSizeLookup(lookup);
//                        recy.setLayoutManager(manager);
//                        recy.addItemDecoration(new GridSpacingItemDecoration(mContext, 3));
////                                all_list.addAll(getAllList(mlist));
//                        all_list.addAll(listOfOut);
//                        setImagePrieve();
//                    }
//                });
//
//
//            }
//
//            @Override
//            public void onFailed() {
//
//            }
//        }, 0);
//    }
//
//    private List<MediaData> all_list = new ArrayList<>();
//
//    public List<MediaData> getAllList(List<List<MediaData>> list) {
//        List<MediaData> mmlist = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            for (int j = 0; j < list.get(i).size(); j++) {
//                mmlist.add(list.get(i).get(j));
//            }
//        }
//
//        return mmlist;
//    }
//
//    /**
//     * init Image preview
//     */
//    public void setImagePrieve() {
//        if (!adapter.isCheckFlag) {
//            adapter.setOnClickPreviewInterface(new AgendaSimpleSectionAdapter.OnClickPreviewInterface() {
//                @Override
//                public void onClickpreviewlistener(View v, int headListPosition, int childListPosition) {
////                    mPreviewLayout = new PreviewLayout(mContext);
//                    selector_pos = getSelectpos(headListPosition, childListPosition);
//                    System.out.println("获取到的位置postion" + selector_pos);
////                    mPreviewLayout.setData(all_list, selector_pos);
////                    mPreviewLayout.startScaleUpAnimation();//关闭动画
//                    mContentContainer.setVisibility(View.VISIBLE);
////                    mPreviewLayout.setMyFrame(mContentContainer);
////                    mContentContainer.addView(mPreviewLayout);
//
//
////                    Intent intent=new Intent(HardwareAlbumActivity.this, ZoomActivity.class);
////                    intent.putExtra("pos",selector_pos);
////                    intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) all_list);
////                     startActivity(intent);
//
//                }
//            });
//        }
//    }
//
//    private int selector_pos;
//
//    public int getSelectpos(int headListPosition, int childListPosition) {
//        int pos = 0;
//        MediaData mediaData = mlist.get(headListPosition).get(childListPosition);
//        pos = all_list.indexOf(mediaData);
//
//        return pos;
//    }
//
//    /**
//     * 选择的图片的个数
//     *
//     * @param selectList
//     * @param TAG
//     */
//    @Override
//    public void onTotalNumChange(@NonNull List selectList, Object TAG) {
//        selection_list.clear();
//        selection_list.addAll(selectList);
//        if (selectList.size() == 0) {
//            tvMiddleTitle.setText("请选择");
//        } else {
//            tvMiddleTitle.setText("已选择" + selectList.size() + "项");
//        }
//
//
//    }
//
//    private List<MediaData> selection_list = new ArrayList<>();
//
//    private class MyRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
//
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                assembleDataList();
//            }
//        }
//    }
//
//    private void assembleDataList() {
//        computeBoundsForward(manager.findFirstCompletelyVisibleItemPosition());
//
//        computeBoundsBackward(manager.findFirstCompletelyVisibleItemPosition());
//    }
//
//    private int[] mPadding = new int[4];
//    private int mSolidWidth = 0;
//    private int mSolidHeight = 0;
//    private Rect mRVBounds = new Rect();
//
//    /**
//     * 从第一个完整可见item顺序遍历
//     */
//    private void computeBoundsForward(int firstCompletelyVisiblePos) {
//        for (int i = firstCompletelyVisiblePos; i < all_list.size(); i++) {
//            View itemView = manager.findViewByPosition(i);
//            Rect bounds = new Rect();
//
//            if (itemView != null) {
//                ImageView thumbView = itemView.findViewById(R.id.thumb_iv);
//
//                if (thumbView == null) return;
//                thumbView.getGlobalVisibleRect(bounds);
//
//                if (mSolidWidth * mSolidHeight == 0) {
//                    mPadding[0] = thumbView.getPaddingLeft();
//                    mPadding[1] = thumbView.getPaddingTop();
//                    mPadding[2] = thumbView.getPaddingRight();
//                    mPadding[3] = thumbView.getPaddingBottom();
//                    mSolidWidth = bounds.width();
//                    mSolidHeight = bounds.height();
//                }
//
//                bounds.left = bounds.left + mPadding[0];
//                bounds.top = bounds.top + mPadding[1];
//                bounds.right = bounds.left + mSolidWidth - mPadding[2];
//                bounds.bottom = bounds.top + mSolidHeight - mPadding[3];
//            } else {
//                bounds.left = i % 3 * mSolidWidth + mPadding[0];
//                bounds.top = mRVBounds.bottom + mPadding[1];
//                bounds.right = bounds.left + mSolidWidth - mPadding[2];
//                bounds.bottom = bounds.top + mSolidHeight - mPadding[3];
//            }
//            bounds.offset(0, -DisplayUtils.dpInt2px(mContext, 50));
//
//            all_list.get(i).setBounds(bounds);
//        }
//    }
//
//    /**
//     * 从第一个完整可见item逆序遍历，如果初始位置为0，则不执行方法内循环
//     */
//    private void computeBoundsBackward(int firstCompletelyVisiblePos) {
//        for (int i = firstCompletelyVisiblePos - 1; i >= 0; i--) {
//            View itemView = manager.findViewByPosition(i);
//            Rect bounds = new Rect();
//
//            if (itemView != null) {
//                ImageView thumbView = itemView.findViewById(R.id.thumb_iv);
//                if (thumbView == null) return;
//                thumbView.getGlobalVisibleRect(bounds);
//
//                bounds.left = bounds.left + mPadding[0];
//                bounds.bottom = bounds.bottom - mPadding[3];
//                bounds.right = bounds.left + mSolidWidth - mPadding[2];
//                bounds.top = bounds.bottom - mSolidHeight + mPadding[1];
//            } else {
//                bounds.left = i % 3 * mSolidWidth + mPadding[0];
//                bounds.bottom = mRVBounds.top - mPadding[3];
//                bounds.right = bounds.left + mSolidWidth - mPadding[2];
//                bounds.top = bounds.bottom - mSolidHeight + mPadding[1];
//            }
//            bounds.offset(0, -DisplayUtils.dpInt2px(mContext, 50));
//
//            all_list.get(i).setBounds(bounds);
//        }
//    }
//
//
////    @Override
////    public void onBackPressed() {
////        if (mContentContainer.getChildAt(mContentContainer.getChildCount() - 1) instanceof PreviewLayout) {
////            mPreviewLayout.startScaleDownAnimation();
////            return;
////        }
////        super.onBackPressed();
////    }
//
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (mContentContainer.getVisibility() == View.VISIBLE) {
//                mContentContainer.setVisibility(View.GONE);
//                return true;
//            } else {
//                return super.onKeyDown(keyCode, event);
//            }
//
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @OnClick({R.id.left_iv_title, R.id.tv_middle_title, R.id.right_tv_titlee, R.id.right_iv_title, R.id.iv_download, R.id.iv_like, R.id.iv_delete})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.left_iv_title:
//                finish();
//                break;
//            case R.id.tv_middle_title:
////                HardWareDropDownDialog.showDropDialog(mContext, dialogItemList);
//
//                break;
//            case R.id.right_tv_titlee:
//                tvMiddleTitle.setClickable(true);
//                leftIvTitle.setVisibility(View.VISIBLE);
//                tvMiddleTitle.setText("APP相册");
//
//                setTextDraw(tvMiddleTitle, R.mipmap.arrow_down);
//                rightTvTitlee.setVisibility(View.GONE);
//                rightIvTitle.setVisibility(View.VISIBLE);
//                lin1.setVisibility(View.GONE);
//
//                adapter.cancleAll_check();
//                adapter.setIsCheckFlag(false);  //点击进入大图
//                break;
//            case R.id.right_iv_title:
//                tvMiddleTitle.setClickable(false);
//                leftIvTitle.setVisibility(View.GONE);
//                tvMiddleTitle.setText("请选择");
//
//                tvMiddleTitle.setCompoundDrawables(null, null, null, null);
//                rightTvTitlee.setVisibility(View.VISIBLE);
//                rightTvTitlee.setText("取消");
//                rightIvTitle.setVisibility(View.GONE);
//
//                lin1.setVisibility(View.VISIBLE);
//
//                adapter.setIsCheckFlag(true); //点击进行选择
//                break;
//            case R.id.iv_download:
//
//
//                if (selection_list.size() > 0) {
//                    rlProgress.setVisibility(View.VISIBLE);
//                    downLoad = new DownLoadRequest(mhandler);
//                    downLoad.addData(selection_list, carmer_list);
//                    sum_count = downLoad.getSumCount();
//                    downLoad.downCameraFile();
//                    rlProgress.setVisibility(View.VISIBLE);
//
//
//                } else {
//                    show("请选择要下载的内容");
//                }
//                break;
//            case R.id.iv_like:
//                break;
//            case R.id.iv_delete:
//                break;
//
//        }
//    }
//
//    private DownLoadRequest downLoad;
//    private long sum_count = 0;
//    private double progeress = 0;
//    private Handler mhandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            switch (msg.what) {
//                case DOWN_LOADFILE:
//                    int len = (int) msg.obj;
//                    double current = 0;
//                    if (sum_count != 0)
//                        current = round(100 * len, sum_count, 2);
//                    progeress += current;
//                    System.out.println("获取的长度是多少" + progeress + "len=  " + len + "  current==" + current + "-------------" + sum_count);
//                    progressBar.setProgress(progeress);
//                    if (progeress >= 100) {
//                        rlProgress.setVisibility(View.GONE);
//
//                        //点击进入大图逻辑
//                        tvMiddleTitle.setClickable(true);
//                        leftIvTitle.setVisibility(View.VISIBLE);
//                        tvMiddleTitle.setText("APP相册");
//
//                        setTextDraw(tvMiddleTitle, R.mipmap.arrow_down);
//                        rightTvTitlee.setVisibility(View.GONE);
//                        rightIvTitle.setVisibility(View.VISIBLE);
//                        lin1.setVisibility(View.GONE);
//
//                        adapter.cancleAll_check();
//                        adapter.setIsCheckFlag(false);  //点击进入大图
//
//
//                        downLoad.clear();//把数据全部清理
//
//                    }
//                    break;
//            }
//        }
//    };
//
//    public static double round(long v1, long v2, int scale) {
//        if (scale < 0) {
//            throw new IllegalArgumentException("此参数错误");
//        }
//        BigDecimal one = new BigDecimal(Double.toString(v1));
//        BigDecimal two = new BigDecimal(Double.toString(v2));
//        return one.divide(two, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
//    }
//
//
//    List<DialogItem> dialogItemList = new ArrayList<>();
//    List<MediaData> Hardware_list = new ArrayList<>();//硬件相机相册
//    List<MediaData> myApp_list = new ArrayList<>();  //app保存的相册
//
//    public void addDialogItem() {
//        dialogItemList.add(new DialogItem(R.layout.top_dialog_item, Hardware_list) {
//            @Override
//            public void onClick() {
//                super.onClick();
//                finish();
//            }
//        });
//
//        dialogItemList.add(new DialogItem(R.layout.top_dialog_item, myApp_list) {
//            @Override
//            public void onClick() {
//                super.onClick();
//                finish();
//            }
//        });
//    }
//
//
//    public void setTextDraw(TextView textView, int res) {
//        //比如drawleft设置图片大小
//        //获取图片
//        Drawable drawable = getResources().getDrawable(res);
//        //第一个0是距左边距离，第二个0是距上边距离，40分别是长宽
//        drawable.setBounds(0, 0, 50, 50);
//        //只放左边
//        textView.setCompoundDrawables(null, null, drawable, null);
//    }
//
//
//    public void setTotalSize(int totalSize) {
//        this.totalSize = totalSize;
//    }
//
//    public int getTotalSize() {
//        return totalSize;
//    }
//
//    private int totalSize;
//
//
//}
