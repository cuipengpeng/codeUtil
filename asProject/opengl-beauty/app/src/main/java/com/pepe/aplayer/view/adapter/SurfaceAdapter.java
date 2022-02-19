package com.pepe.aplayer.view.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pepe.aplayer.R;
import com.pepe.aplayer.opengl.egl.EGLUtils;
import com.pepe.aplayer.opengl.egl.GLRenderer;
import com.pepe.aplayer.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 */

public class SurfaceAdapter extends RecyclerView.Adapter<SurfaceAdapter.ViewHolder> {
    Context mContext;
    List<String> mDataList = new ArrayList<>();
    public EGLUtils[] eGLUtilsArray;
    public GLRenderer[] gLRendererArray;
    public SurfaceView[] surfaceViewArray;
    public int[] widthArray;
    public int[] heightArray;
    public static final int COUNT = 5;

    public SurfaceAdapter(Context context) {
        this.mContext = context;
        this.mDataList.clear();
        eGLUtilsArray = new EGLUtils[COUNT];
        gLRendererArray = new GLRenderer[COUNT];
        surfaceViewArray = new SurfaceView[COUNT];
        widthArray = new int[COUNT];
        heightArray = new int[COUNT];
    }

    public void upateData(boolean isRefresh, List<String> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_activity_layout, parent, false);
        LogUtil.printLog("");

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        LogUtil.printLog("position = "+position);

        surfaceViewArray[position] = holder.sv_item_testActivity_rv;
    }

    @Override
    public int getItemCount() {
//        return  mDataList.size();
        return  COUNT;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public SurfaceView sv_item_testActivity_rv ;

        ViewHolder(View view) {
            super(view);
            sv_item_testActivity_rv = view.findViewById(R.id.sv_item_testActivity_rv);
            sv_item_testActivity_rv.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    LogUtil.printLog("");

                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                    LogUtil.printLog("screenWidth2="+i1+"--screenHeight2="+i2);

//                screenWidth2 = i1;
//                screenHeight2 = i2;
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    LogUtil.printLog("");

                }
            });
        }
    }
}
