package com.test.xcamera.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.test.xcamera.R;
import com.test.xcamera.adapter.FilterViewAdapter;
import com.effect_opengl.EffectManager;
import com.test.xcamera.bean.ItemBean;
//import com.meetvr.aicamera.glview.RenderCoreManager;
import com.test.xcamera.utils.FilterAndBeautyJson;

import java.util.ArrayList;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/2/15
 * e-mail zhouxuecheng1991@163.com
 */

public class FilterView extends RelativeLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, VerticalSeekBarFilter.TouchSeekBar {

    private View filterView;
    private Gson gson;
    private ArrayList<ItemBean.ItemData> filterData;
    private ArrayList<ItemBean.ItemData> beautyData;
    private boolean isFilter;
    private TextView titleFilter;
    private TextView titleBeauty;
    private ItemBean.ItemData currentFilter;
    private VerticalSeekBarFilter beautySeekBar;
    private ItemBean.ItemData currentBeauty;
    private Context context;
    private boolean isTouchSeekBar = false;
    private FilterViewAdapter filterDialogAdapter;
    private int position;
    //    private NumberPop numberPop;
    private VerticalTextView numberTextView;
    private LayoutParams layoutParams;
    private RecyclerView recyclerView;

    public FilterView(Context context) {
        super(context);
        initView(context);
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    private void initView(Context context) {
        this.context = context;
        filterView = View.inflate(context, R.layout.filter_view_layout, this);
        recyclerView = filterView.findViewById(R.id.myRV);
        LinearLayoutManager drag_manger = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(drag_manger);
        filterDialogAdapter = new FilterViewAdapter(context);
        recyclerView.setAdapter(filterDialogAdapter);
        gson = new Gson();
        String filterResource = FilterAndBeautyJson.getFilterResourceNew(context);
        String beautyResource = FilterAndBeautyJson.getBeautyResourceNew(context);
        ItemBean filterBean = gson.fromJson(filterResource, ItemBean.class);
        ItemBean beautyBean = gson.fromJson(beautyResource, ItemBean.class);
        filterData = filterBean.list;
        beautyData = beautyBean.list;

        filterDialogAdapter.updataData(filterBean.list);
        recyclerView.scrollToPosition(filterData.size() - 1);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 30;
                outRect.right = 100;
                outRect.top = 10;
                outRect.bottom = 10;
            }
        });

        titleFilter = filterView.findViewById(R.id.titleFilter);
        titleBeauty = filterView.findViewById(R.id.titleBeauty);
        numberTextView = filterView.findViewById(R.id.number);
        layoutParams = (LayoutParams) numberTextView.getLayoutParams();
        layoutParams.setMargins(0, 0, 30, 0);
        numberTextView.setLayoutParams(layoutParams);

        titleFilter.setOnClickListener(this);
        titleBeauty.setOnClickListener(this);
        beautySeekBar = filterView.findViewById(R.id.beautySeekBar);
        beautySeekBar.setVisibility(View.INVISIBLE);
        beautySeekBar.setOnSeekBarChangeListener(this);
        beautySeekBar.setPadding(0, 0, 0, 0);
        beautySeekBar.setThumbOffset(0);

        isFilter = true;
        titleFilter.setTextColor(context.getResources().getColor(R.color.appThemeColor));

        filterDialogAdapter.setItemListener(new FilterViewAdapter.ItemListener() {
            @Override
            public void onClick(int position) {
                FilterView.this.position = position;
                if (!isFilter) {
                    selecteBeautyItem(position);
                    filterDialogAdapter.updataData(beautyData);
                } else {
                    selecteFilterItem(position);
                    filterDialogAdapter.updataData(filterData);
                }
            }
        });

        View closeDialog = filterView.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = FilterView.this.getVisibility();
                if (visibility == View.VISIBLE) {
                    FilterView.this.setVisibility(GONE);
                }
            }
        });

        beautySeekBar.setTouchSeekBar(this);
        beautySeekBar.measure(0, 0);

    }

    public void initBeauty() {
        for (ItemBean.ItemData itemData : beautyData) {
            int id = itemData.id;
            if (id == 1 || id == 2 || id == 3) {
                EffectManager.instance().addBeautyIntensity(itemData.modelType, 0.5f);
                itemData.seekBarPosition = 50;
                itemData.isOperation = true;
            }
        }
    }

    private void selecteFilterItem(int position) {
        currentFilter = filterData.get(position);
        EffectManager.instance().addCommonFilter(currentFilter.commonFilterId);
        for (ItemBean.ItemData item : filterData) {
            item.isSlect = item.name.equals(currentFilter.name);
        }
        if (currentFilter.commonFilterId.equals("")) {
            showSeekBar(currentFilter, View.GONE);
            beautySeekBar.setSeekBarProgress(0);
        } else {
            showSeekBar(currentFilter, View.VISIBLE);
            beautySeekBar.setMax(100);
            beautySeekBar.setSeekBarProgress(currentFilter.seekBarPosition == 0 ? 100 : currentFilter.seekBarPosition);
        }
    }

    private void selecteBeautyItem(int position) {
        currentBeauty = beautyData.get(position);
        showSeekBar(currentBeauty, View.VISIBLE);

        for (ItemBean.ItemData item : beautyData) {
            item.isSlect = item.name.equals(currentBeauty.name);
        }

        if (currentBeauty.id == 1 || currentBeauty.id == 2 || currentBeauty.id == 3) {
            beautySeekBar.setSeekBarProgress(currentBeauty.seekBarPosition);
            beautySeekBar.setMax(100);
        } else {
            beautySeekBar.setMax(200);
            beautySeekBar.setSeekBarProgress(currentBeauty.seekBarPosition == 0
                    ? 100 : currentBeauty.seekBarPosition);
        }
    }

    private void showSeekBar(ItemBean.ItemData itemData, int visibille) {
        currentBeauty = itemData;
        numberTextView.setVisibility(visibille);
        beautySeekBar.setVisibility(visibille);
        if (TextUtils.isEmpty(itemData.modelType)) {
            //滤镜
            layoutParams.setMargins(0, itemData.location, 30, 0);
            numberTextView.setText(itemData.location == 0 ? "100" : itemData.number + "");
        } else {
            //美颜
            int height = beautySeekBar.getMeasuredHeight();
            if (currentBeauty.id == 1 || currentBeauty.id == 2 || currentBeauty.id == 3) {
                layoutParams.setMargins(0, itemData.location == 0 ? height / 2 : itemData.location, 30, 0);
                numberTextView.setText(itemData.location == 0 ? "50" : itemData.number + "");
            } else {
                layoutParams.setMargins(0, itemData.location == 0 ? height / 2 : itemData.location, 30, 0);
                numberTextView.setText(itemData.location == 0 ? "0" : itemData.number + "");
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.titleFilter:

                if (isFilter) {
                    return;
                }
                if (currentFilter == null) {
                    recyclerView.scrollToPosition(filterData.size() - 1);
                }
                titleFilter.setTextColor(context.getResources().getColor(R.color.appThemeColor));
                titleBeauty.setTextColor(Color.parseColor("#666666"));
                if (filterData == null)
                    filterData = gson.fromJson(FilterAndBeautyJson.getFilterResourceNew(context), ItemBean.class).list;
                isFilter = true;
                filterDialogAdapter.updataData(filterData);
                seekBarSetting(filterData);
                break;
            case R.id.titleBeauty:

                if (!isFilter) {
                    return;
                }

                EffectManager.instance().addBeautyArScene();

                if (currentBeauty == null) {
                    recyclerView.scrollToPosition(beautyData.size() - 1);
                }
                titleFilter.setTextColor(Color.parseColor("#666666"));
                titleBeauty.setTextColor(context.getResources().getColor(R.color.appThemeColor));
                if (beautyData == null)
                    beautyData = gson.fromJson(FilterAndBeautyJson.getBeautyResourceNew(context), ItemBean.class).list;
                isFilter = false;
                filterDialogAdapter.updataData(beautyData);
                seekBarSetting(beautyData);
                break;
        }
    }

    private void seekBarSetting(ArrayList<ItemBean.ItemData> list) {
        for (ItemBean.ItemData itemData : list) {
            if (!isFilter) {
                if (itemData.isSlect) {
                    showSeekBar(itemData, View.VISIBLE);
                    if (currentBeauty.id == 1 || currentBeauty.id == 2 || currentBeauty.id == 3) {
                        beautySeekBar.setSeekBarProgress(currentBeauty.seekBarPosition);
                    } else {
                        beautySeekBar.setMax(200);
                        beautySeekBar.setSeekBarProgress(currentBeauty.seekBarPosition == 0
                                ? 100 : currentBeauty.seekBarPosition);
                    }
                }
            } else {
                if (itemData.isSlect) {
                    if (currentFilter.commonFilterId.equals("")) {
                        showSeekBar(itemData, View.GONE);
                        beautySeekBar.setSeekBarProgress(0);
                    } else {
                        showSeekBar(itemData, View.VISIBLE);
                        beautySeekBar.setMax(100);
                        beautySeekBar.setSeekBarProgress(currentFilter.seekBarPosition == 0 ? 100 : currentFilter.seekBarPosition);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!isTouchSeekBar) {
            return;
        }
        float intensity = progress / (float) 100;
        if (!isFilter && currentBeauty != null) {
            currentBeauty.seekBarPosition = progress;
            if (currentBeauty.id == 1 || currentBeauty.id == 2 || currentBeauty.id == 3) {
                EffectManager.instance().addBeautyIntensity(currentBeauty.modelType, intensity);
                Log.i("BEAUTY_SHAPE_LOG", "currentBeauty.modelType: " + currentBeauty.modelType + "   intensity = " + intensity);
            }
            currentBeauty.isOperation = progress > 0;
            filterDialogAdapter.notifyItemChanged(position);
        } else {
            if (currentFilter != null)
                currentFilter.seekBarPosition = progress;
            EffectManager.instance().commonFilterIntensity(intensity);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTouchSeekBar = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isTouchSeekBar = false;
    }

    @Override
    public void move(int offSet, int number) {
        if (isFilter && currentFilter != null) {
            if (number < 0 || number > 100) {
                return;
            }
            currentFilter.location = offSet;
            currentFilter.number = number;
        } else {
            if (currentBeauty.id == 1 || currentBeauty.id == 2 || currentBeauty.id == 3) {
                if (number > 100 || number < 0) {
                    return;
                }
            } else {
                number = number - 100;
                if (number > 100 || number < -100) {
                    return;
                }
            }
            currentBeauty.location = offSet;
            currentBeauty.number = number;
        }

        layoutParams.setMargins(0, offSet, 30, 0);
        numberTextView.setText(number + "");
        numberTextView.setLayoutParams(layoutParams);

        if (currentBeauty != null) {
            float intensity = ((float) number / 100.0f);
            if (currentBeauty.id != 1 || currentBeauty.id != 2 || currentBeauty.id != 3) {
                EffectManager.instance().beautyShape(currentBeauty.modelType, intensity);
                Log.i("BEAUTY_SHAPE_LOG", "currentBeauty.modelType: " + currentBeauty.modelType + "   intensity = " + intensity);
            }
        }
    }

    public void setRotate(int i) {
        if (filterDialogAdapter != null)
            filterDialogAdapter.setRotate(i);
    }

    /**
     * 当前是否使用 美颜或者 滤镜效果
     *
     * @return
     */
    public boolean getFilterOrBeauty() {
        return currentFilter != null || currentBeauty != null;
    }
}
