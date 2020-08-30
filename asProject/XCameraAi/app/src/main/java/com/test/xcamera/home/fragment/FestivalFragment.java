package com.test.xcamera.home.fragment;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.framwork.base.view.MOBaseFragment;
import com.google.gson.Gson;
import com.test.xcamera.R;
import com.test.xcamera.bean.MoVideoByDate;
import com.test.xcamera.bean.MoVideoSegment;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.home.ActivationHelper;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoGetVideoByDateCallback;
import com.test.xcamera.mointerface.MoGetVideoSegmentCallback;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.LoggerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Author: mz
 * Time:  2019/10/15
 * <p>
 * 活动页面
 */
public class FestivalFragment extends MOBaseFragment {

    @BindView(R.id.tv_test)
    TextView tvTest;

    @BindView(R.id.tv_test1)
    TextView tvTest1;
    @BindView(R.id.tv_test3)
    TextView tvTest3;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    Unbinder unbinder;
    private ActivationHelper helper;

    private Handler mhandle = new Handler();
    private VideoTemplete   template;

    @Override
    public int initView() {
        return R.layout.fragment_festival;
    }

    @Override
    public void initClick() {

    }

    @Override
    public void initData() {
        jsonToBean();
      //"file:///android_asset/"+
       String img_url= Constants.template_img_url+Constants.template_path+template.getIcon();
//        GlideUtils.GlideLoader(getActivity(), ,ivImg);
//
//        try {
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Glide.with(getActivity()).load(img_url ).into(ivImg);


    }

    //segmenttemplate/template0/
    private List<VideoTemplete> mlist = new ArrayList<>();

    public void jsonToBean() {
        StringBuilder newstringBuilder = new StringBuilder();
        InputStream inputStream = null;
        try {
            inputStream = getResources().getAssets().open("segmenttemplate/template0/Template0.json1");
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            String jsonLine;
            while ((jsonLine = reader.readLine()) != null) {
                newstringBuilder.append(jsonLine);
            }
            reader.close();
            isr.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = newstringBuilder.toString();
        Log.d("json", result);
        Gson gson = new Gson();
//        mlist.addAll((Collection<? extends Template>) gson.fromJson(result, new TypeToken<List<Template>>() {
//        }.getType()));
//        Log.d("获取的解析结合的大小", mlist.size() + "");

        template = gson.fromJson(result,VideoTemplete.class);

    }

    @OnClick({R.id.tv_test, R.id.tv_test1, R.id.tv_test3, R.id.tv_test4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_test:
                CameraToastUtil.show("开始获取固件状态", getActivity());
                helper = new ActivationHelper(getActivity());
                helper.getActivateStatue();
                break;
            case R.id.tv_test1:
//                UploadCameraFile uploadCameraFile = new UploadCameraFile(mhandle, getActivity(), new File(Constants.hard_down_filedetail));
//                uploadCameraFile.uploadCheckcond();
                break;
            case R.id.tv_test3:
                ConnectionManager.getInstance().getVideoByDate(0, new MoGetVideoByDateCallback() {
                    @Override
                    public void onSuccess(final ArrayList<MoVideoByDate> videoByDates) {
                        mhandle.post(new Runnable() {
                            @Override
                            public void run() {
                                CameraToastUtil.show("获取精彩推荐数据成功", getActivity());
                                LoggerUtils.printLog("获取精彩推荐数据" + videoByDates + "");
                            }
                        });

                    }

                    @Override
                    public void onFailed() {
                        CameraToastUtil.show("获取精彩推荐数据失败", getActivity());
                    }
                });
                break;
            case R.id.tv_test4:
                long data = System.currentTimeMillis();
                ConnectionManager.getInstance().getVideoSegment(data, new MoGetVideoSegmentCallback() {
                    @Override
                    public void onSuccess(final ArrayList<MoVideoSegment> videoSegments) {
                        mhandle.post(new Runnable() {
                            @Override
                            public void run() {
                                CameraToastUtil.show("获取素材数据成功", getActivity());
                                LoggerUtils.printLog("获取素材数据成功" + videoSegments + "");
                            }
                        });
                    }

                    @Override
                    public void onFailed() {

                    }
                });
                break;
        }
    }


}
