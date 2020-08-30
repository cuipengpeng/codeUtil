package com.test.xcamera.phonealbum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.editvideo.NvAsset;
import com.editvideo.dataInfo.CompoundCaptionInfo;
import com.framwork.base.view.MOBaseActivity;
import com.google.gson.reflect.TypeToken;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.adapter.CaptionFontRecyclerAdaper;
import com.test.xcamera.phonealbum.adapter.CompoundCaptionColorAdaper;
import com.test.xcamera.phonealbum.adapter.SpaceItemDecoration;
import com.test.xcamera.phonealbum.bean.AssetItem;
import com.test.xcamera.phonealbum.bean.BackupData;
import com.test.xcamera.phonealbum.bean.FontInfo;
import com.test.xcamera.phonealbum.widget.OnItemClickListener;
import com.test.xcamera.utils.CenterLayoutManager;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.utils.ParseJsonFile;
import com.meicam.sdk.NvsStreamingContext;


import java.util.ArrayList;
import java.util.List;

import static com.editvideo.Constants.CaptionColors;
import static com.test.xcamera.phonealbum.MyVideoEditActivity.RESULT_OK_IN_CAPTION_STYLE;


public class VideoCompoundCaptionStyleActivity extends MOBaseActivity implements View.OnClickListener {
    private static final String TAG = "CompoundCaptionStyleActivity";
    public static final String select_caption_index = "select_caption_index";
    public static final String select_caption_text = "select_caption_text";
    private ImageView mCaptionFontDownload;
    private RecyclerView mFontRecyclerList;
    private EditText mCaptionInput;
    private RecyclerView mColorRecyclerList;
    private ImageView mCancel;
    private ImageView mFinish;

    private CompoundCaptionColorAdaper mCaptionColorRecycleAdapter;
    private CaptionFontRecyclerAdaper mCaptionFontRecycleAdapter;
    private ArrayList<String> mCaptionColorInfolist = new ArrayList<>();
    private ArrayList<AssetItem> mCaptionFontInfolist = new ArrayList<>();
    private NvsStreamingContext mStreamingContext;

    private int mCatpionIndex = -1;
    private String mCatpionText = "";

    private ArrayList<CompoundCaptionInfo> mCaptionDataListClone;
    private int mSelectFontPos = 0;
    private int mSelectColorPos = -1;
    private CompoundCaptionInfo.CompoundCaptionAttr mCurCatpionAttr;
    private ArrayList<FontInfo> infoList;
    @Override
    public int initView() {
        mStreamingContext = NvsStreamingContext.getInstance();

        return R.layout.activity_compound_caption_style;
    }
    @Override
    public void initData() {
        initViews();
        initTitle();
        initListener();
        initCaptionFontInfoList();
        initCaptionFontRecycleAdapter();
        initCaptionColorRecycleAdapter();
        updateViewUI();
    }
    protected void initViews() {
        mCaptionFontDownload = findViewById(R.id.captionFontDownload);
        mFontRecyclerList = findViewById(R.id.fontRecyclerList);
        mCaptionInput = findViewById(R.id.captionInput);
        mColorRecyclerList = findViewById(R.id.colorRecyclerList);
        mCancel = findViewById(R.id.cancel);
        mFinish = findViewById(R.id.finish);
    }

    protected void initTitle() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mCatpionIndex = bundle.getInt(select_caption_index);
                mCatpionText = bundle.getString(select_caption_text);
            }
        }
        if (!TextUtils.isEmpty(mCatpionText)) {
            mCaptionInput.setHint(mCatpionText);
        }
        mCaptionInput.setText(mCatpionText);
        mCaptionInput.setSelection(mCaptionInput.getText().length());
        showInputMethod();
        mCaptionDataListClone = BackupData.instance().cloneCompoundCaptionData();
    }


    protected void initListener() {
        mCaptionFontDownload.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.captionFontDownload:
                break;
            case R.id.cancel:
                finishActivity();
                break;
            case R.id.finish:
                String captionText = mCaptionInput.getText().toString();
                if (captionText.isEmpty()) {
                    /*captionText = mCatpionText;*/
                    show(getString(R.string.video_edit_subtitle_null_toast));
                    return;
                }
                mCurCatpionAttr.setCaptionText(captionText);
                BackupData.instance().setCompoundCaptionList(mCaptionDataListClone);
                Intent intent = new Intent();
                intent.putExtra(select_caption_index, mCatpionIndex);
                setResult(RESULT_OK_IN_CAPTION_STYLE, intent);
                finishActivity();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity();
    }

    private void finishActivity() {
        hideInputMethod();
        finish();
    }

    private CompoundCaptionInfo.CompoundCaptionAttr getCaptionAttr() {
        CompoundCaptionInfo captionInfo = getCurCaptionInfo();
        if (captionInfo == null) {
            return null;
        }
        ArrayList<CompoundCaptionInfo.CompoundCaptionAttr> captionAttributeList = captionInfo.getCaptionAttributeList();
        if (captionAttributeList == null) {
            return null;
        }
        int captionAttrCount = captionAttributeList.size();
        if (captionAttrCount == 0 || mCatpionIndex < 0 || mCatpionIndex >= captionAttrCount) {
            return null;
        }
        return captionAttributeList.get(mCatpionIndex);
    }

    private void updateViewUI() {
        mCurCatpionAttr = getCaptionAttr();
        if(mCurCatpionAttr==null){
            mCurCatpionAttr=new CompoundCaptionInfo.CompoundCaptionAttr();
            mCurCatpionAttr.setCaptionText(mCatpionText);
        }
        String fontName = mCurCatpionAttr.getCaptionFontName();
        int fontCount = mCaptionFontInfolist.size();
        for (int idx = 0; idx < fontCount; idx++) {
            AssetItem assetItem = mCaptionFontInfolist.get(idx);
            if (assetItem == null) {
                continue;
            }
            NvAsset asset = assetItem.getAsset();
            if (asset == null) {
                continue;
            }
            if (!TextUtils.isEmpty(asset.name)
                    && !TextUtils.isEmpty(fontName)
                    && asset.name.equals(fontName)) {
                mSelectFontPos = idx;
                break;
            }
        }

        String captionColor = mCurCatpionAttr.getCaptionColor();
        int colorCount = mCaptionColorInfolist.size();
        for (int idx = 0; idx < colorCount; idx++) {
            String colorStr = mCaptionColorInfolist.get(idx);
            if (colorStr == null) {
                continue;
            }
            if (!TextUtils.isEmpty(colorStr)
                    && !TextUtils.isEmpty(captionColor)
                    && colorStr.equals(captionColor)) {
                mSelectColorPos = idx;
                break;
            }
        }

        fontNotifyDataSetChanged(mSelectFontPos);
        colorNotifyDataSetChanged(mSelectColorPos);
        if(mSelectFontPos>0){
            setEditTypefaceDef(mSelectFontPos);
        }
        if(mSelectColorPos>0) {
            setEditColor(mCaptionColorInfolist.get(mSelectColorPos));
        }
    }

    private void fontNotifyDataSetChanged(int selectPos) {
        if (mCaptionFontRecycleAdapter != null) {
            mCaptionFontRecycleAdapter.setSelectedPos(selectPos);
            mCaptionFontRecycleAdapter.notifyDataSetChanged();
        }
    }

    private void colorNotifyDataSetChanged(int selectPos) {
        if (mCaptionColorRecycleAdapter != null) {
            mCaptionColorRecycleAdapter.setSelectedPos(selectPos);
            mCaptionColorRecycleAdapter.notifyDataSetChanged();
        }
    }

    private CompoundCaptionInfo getCurCaptionInfo() {
        int curCaptionZVal = BackupData.instance().getCaptionZVal();
        int captionCount = mCaptionDataListClone.size();
        for (int idx = 0; idx < captionCount; idx++) {
            CompoundCaptionInfo captionInfo = mCaptionDataListClone.get(idx);
            if (captionInfo == null) {
                continue;
            }
            int captionZVal = captionInfo.getCaptionZVal();
            if (curCaptionZVal == captionZVal) {
                return captionInfo;
            }
        }

        return null;
    }

    private void showInputMethod() {
        //弹出键盘
        mCaptionInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCaptionInput.requestFocus();//获取焦点
                InputMethodManager inputManager = (InputMethodManager) mCaptionInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mCaptionInput, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
    }

    private void hideInputMethod() {
        //隐藏键盘
        InputMethodManager inputManager = (InputMethodManager) mCaptionInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mCaptionInput.getWindowToken(), 0);
    }

    private void initCaptionFontInfoList() {
        String fontJsonPath = "font/info.json";
        String fontJsonText = ParseJsonFile.readAssetJsonFile(this, fontJsonPath);
        if (TextUtils.isEmpty(fontJsonText)) {
            return;
        }
        infoList = ParseJsonFile.fromJson(fontJsonText, new TypeToken<List<FontInfo>>() {
        }.getType());
        if (infoList == null) {
            return;
        }
        int fontCount = infoList.size();
        for (int idx = 0; idx < fontCount; idx++) {
            FontInfo fontInfo = infoList.get(idx);
            if (fontInfo == null) {
                continue;
            }
            String fontAssetPath = "assets:/font/" + fontInfo.getFontFileName();
            String fontName = mStreamingContext.registerFontByFilePath(fontAssetPath);
            AssetItem assetItem = new AssetItem();
            NvAsset asset = new NvAsset();
            String fontCoverPath =  fontInfo.getImageName();
            asset.coverUrl = fontCoverPath;
            asset.isReserved = true;
            asset.bundledLocalDirPath = fontAssetPath;
            asset.name = fontName;
            assetItem.setAsset(asset);
            assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
            mCaptionFontInfolist.add(assetItem);
        }
        AssetItem assetItem = new AssetItem();
        NvAsset asset = new NvAsset();
        asset.coverUrl=getResourceToString(R.string.app_name);
        assetItem.setImageRes(R.mipmap.comp_caption_default);
        assetItem.setAssetMode(AssetItem.ASSET_NONE);
        assetItem.setAsset(asset);
        mCaptionFontInfolist.add(0, assetItem);
    }

    private void initCaptionFontRecycleAdapter() {
        CenterLayoutManager layoutManager = new CenterLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mFontRecyclerList.setLayoutManager(layoutManager);
        mCaptionFontRecycleAdapter = new CaptionFontRecyclerAdaper(this);
        mCaptionFontRecycleAdapter.setAssetInfoList(mCaptionFontInfolist);
        mFontRecyclerList.setAdapter(mCaptionFontRecycleAdapter);
        mFontRecyclerList.addItemDecoration(new SpaceItemDecoration(0, DensityUtils.dp2px(this, 8)));
        mCaptionFontRecycleAdapter.setOnItemClickListener(new CaptionFontRecyclerAdaper.OnFontItemClickListener() {
            @Override
            public void onItemDownload(View view, int position) {

            }

            @Override
            public void onItemClick(View view, int position) {
                layoutManager.smoothScrollToPosition(mFontRecyclerList,new RecyclerView.State(),position);
                int fontCount = mCaptionFontInfolist.size();
                if (position < 0 || position >= fontCount) {
                    return;
                }
                if (mSelectFontPos == position) {
                    return;
                }
                mSelectFontPos = position;
                AssetItem assetItem = mCaptionFontInfolist.get(position);
                if (assetItem == null) {
                    return;
                }
                NvAsset asset = assetItem.getAsset();
                if (asset == null) {
                    return;
                }
                //设置字体
                String assetLocalDirPath = asset.bundledLocalDirPath;
                setEditTypeface(assetLocalDirPath);
                if (mCurCatpionAttr != null) {
                    mCurCatpionAttr.setCaptionFontName(asset.name);
                }
            }
        });
    }

    private void setEditTypefaceDef(int position){
        AssetItem assetItem = mCaptionFontInfolist.get(position);
        if (assetItem == null) {
            return;
        }
        NvAsset asset = assetItem.getAsset();
        if (asset == null) {
            return;
        }
        //设置字体
        String assetLocalDirPath = asset.bundledLocalDirPath;
        setEditTypeface(assetLocalDirPath);
    }
    private void setEditTypeface(String assetLocalDirPath){
        Typeface newTypeface = null;
        if (!TextUtils.isEmpty(assetLocalDirPath)) {
            int index = assetLocalDirPath.indexOf('/');
            String fontPath = assetLocalDirPath.substring(index + 1);
            newTypeface = Typeface.createFromAsset(getAssets(), fontPath);
        }
        mCaptionInput.setTypeface(newTypeface);
    }
    private void initCaptionColorRecycleAdapter() {
        for (int index = 0; index < CaptionColors.length; ++index) {
            mCaptionColorInfolist.add(CaptionColors[index]);
        }
        CenterLayoutManager layoutManager = new CenterLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mColorRecyclerList.setLayoutManager(layoutManager);
        mCaptionColorRecycleAdapter = new CompoundCaptionColorAdaper(this);
        mCaptionColorRecycleAdapter.setCaptionColorList(mCaptionColorInfolist);
        mColorRecyclerList.setAdapter(mCaptionColorRecycleAdapter);
        mColorRecyclerList.addItemDecoration(new SpaceItemDecoration(0, DensityUtils.dp2px(this, 30)));
        mCaptionColorRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos,boolean isAdd) {
                layoutManager.smoothScrollToPosition(mFontRecyclerList,new RecyclerView.State(),pos);

                int colorCount = mCaptionColorInfolist.size();
                if (pos < 0 || pos >= colorCount) {
                    return;
                }
                if (mSelectColorPos == pos) {
                    return;
                }
                mSelectColorPos = pos;
                String color = mCaptionColorInfolist.get(pos);
                setEditColor(color);
                if (mCurCatpionAttr != null) {
                    mCurCatpionAttr.setCaptionColor(color);
                }
            }
        });
    }

    public void setEditColor(String color) {
        mCaptionInput.setTextColor(Color.parseColor(color));
    }
}
