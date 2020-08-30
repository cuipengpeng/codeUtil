package com.test.xcamera.phonealbum;

import android.widget.FrameLayout;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.zoom.PreviewLayout;
import com.test.xcamera.R;
import com.editvideo.MediaData;

import java.util.List;

import butterknife.BindView;

public class ZoomActivity extends MOBaseActivity {
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    PreviewLayout mPreviewLayout;

    int pos=0;
    @Override
    public int initView() {
        return R.layout.activity_zoom;
    }

    @Override
    public void initClick() {

    }

    @Override
    public void initData() {
          pos=getIntent().getIntExtra("pos",0);

          List<MediaData> list1=getIntent().getParcelableArrayListExtra("list");
          mPreviewLayout = new PreviewLayout(mContext);
//          mPreviewLayout.setData(list1, pos);
          mPreviewLayout.setMyFrame(frameLayout);
          frameLayout.addView(mPreviewLayout);
    }


}
