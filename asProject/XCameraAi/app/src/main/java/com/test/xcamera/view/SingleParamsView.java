package com.test.xcamera.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.test.xcamera.R;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.utils.ViewUitls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by smz on 2019/12/20.\
 * <p>
 * fpv相机参数单行列表
 */

public class SingleParamsView extends RelativeLayout {
    private Context mContext;
    private TextView mDesc;
    private RecyclerView mRecyclerView;
    public Adapter mAdapter;

    private List<Entity> data = new ArrayList<>();

    public SingleParamsView(Context context) {
        super(context);
        init(context);
    }

    public SingleParamsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SingleParamsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.setBackgroundResource(R.color.color_202222);

        mDesc = new TextView(this.mContext);
        mDesc.setId(R.id.desc);
        LayoutParams lpDesc = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lpDesc.addRule(RelativeLayout.CENTER_VERTICAL);
        lpDesc.setMargins(ViewUitls.dp2px(context, 20), 0, ViewUitls.dp2px(context, 20), 0);
        mDesc.setLayoutParams(lpDesc);
        mDesc.setTextColor(context.getResources().getColor(R.color.color_ff7700));
        mDesc.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        mDesc.setGravity(Gravity.CENTER);

        mRecyclerView = new RecyclerView(this.mContext);
        LayoutParams lpRV = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lpRV.addRule(RelativeLayout.CENTER_VERTICAL);
        lpRV.setMargins(0, 0, ViewUitls.dp2px(context, 15), 0);
        lpRV.addRule(RelativeLayout.RIGHT_OF, R.id.desc);
        mRecyclerView.setLayoutParams(lpRV);

        this.addView(mDesc);
        this.addView(mRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new Adapter(context, data);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void orientation(ScreenOrientationType type) {
        if (type == ScreenOrientationType.PORTRAIT) {
            LayoutParams lpDesc = (LayoutParams) mDesc.getLayoutParams();
            LayoutParams lpRv = (LayoutParams) mRecyclerView.getLayoutParams();
            lpDesc.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lpRv.removeRule(RelativeLayout.LEFT_OF);
            lpDesc.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lpRv.addRule(RelativeLayout.RIGHT_OF, R.id.desc);
            lpRv.setMargins(0, 0, ViewUitls.dp2px(this.mContext, 15), 0);

            mDesc.setLayoutParams(lpDesc);
            mRecyclerView.setLayoutParams(lpRv);
        } else if (type == ScreenOrientationType.LANDSCAPE) {
            LayoutParams lpDesc = (LayoutParams) mDesc.getLayoutParams();
            LayoutParams lpRv = (LayoutParams) mRecyclerView.getLayoutParams();
            lpDesc.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lpRv.removeRule(RelativeLayout.RIGHT_OF);
            lpDesc.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lpRv.addRule(RelativeLayout.LEFT_OF, R.id.desc);
            lpRv.setMargins(ViewUitls.dp2px(this.mContext, 15), 0, 0, 0);

            mDesc.setLayoutParams(lpDesc);
            mRecyclerView.setLayoutParams(lpRv);
        }

        //更改横竖屏时 更改参数展示的顺序 通过翻转数组实现
        int itemType = type == ScreenOrientationType.PORTRAIT ? Entity.PORTRAIT : Entity.LAND;
        int pos = 0;
        if (this.data.get(0).itemType != itemType) {
            Collections.reverse(this.data);
            for (int i = 0; i < this.data.size(); i++) {
                this.data.get(i).itemType = itemType;
                if (this.data.get(i).selectedFlag)
                    pos = i;
            }
            this.mAdapter.notifyDataSetChanged();

            //尽量让选中的item居中
            if (type == ScreenOrientationType.LANDSCAPE && pos + 1 < this.data.size())
                pos++;
            else if (type == ScreenOrientationType.PORTRAIT && pos - 1 > 0)
                pos--;
            mRecyclerView.scrollToPosition(pos);
        }

        mDesc.setRotation(type == ScreenOrientationType.PORTRAIT ? 0 : 90);
    }

    public void changeItem(int pos) {
        for (int i = 0; i < this.data.size(); i++)
            this.data.get(i).selectedFlag = pos == i;
        this.mAdapter.notifyDataSetChanged();
    }

    public void setData(String info, List<Entity> data) {
        this.mDesc.setText(info);

        this.data.clear();
        this.data.addAll(data);
        this.mAdapter.notifyDataSetChanged();
    }
}


class Adapter extends BaseQuickAdapter<Entity, BaseViewHolder> {
    private Context mContext;

    public Adapter(Context context, List<Entity> data) {
        super(R.layout.item_mode_param_view, data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Entity entity) {
        helper.addOnClickListener(R.id.text);

        TextView textView = helper.getView(R.id.text);
        textView.setText(entity.text);
        textView.setTextColor(entity.selectedFlag ?
                mContext.getResources().getColor(R.color.white)
                : mContext.getResources().getColor(R.color.color_666666));
        textView.setRotation(entity.itemType == Entity.PORTRAIT ? 0 : 90);
    }
}

class Entity {
    //竖屏
    public static final int PORTRAIT = 0;
    //横屏
    public static final int LAND = 1;

    /**
     * 布局类型 可以为PORTRAIT / LAND
     */
    public int itemType;

    public String text;
    /**
     * 具体的参数值
     * 横竖屏会打乱源数据的顺序
     */
    public Object source;

    /**
     * 被选中的标记
     */
    public boolean selectedFlag;

    public Entity(int itemType, String text, Object source, boolean selectedFlag) {
        this.itemType = itemType;
        this.text = text;
        this.source = source;
        this.selectedFlag = selectedFlag;
    }

    public Entity(int itemType, String text, Object source) {
        this(itemType, text, source, false);
    }

    public Entity(int itemType, String text) {
        this(itemType, text, null, false);
    }
}