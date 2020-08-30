package com.test.xcamera.upload;


import com.framwork.http.retrofit.RetrofitUtil;
import com.test.xcamera.api.ApiService;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.UploadBean;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.LoggerUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Author: mz
 * Time:  2019/11/6
 */
public class UploadUtil {
    private static String Tag = "UploadUtil";

    /**
     * 上传文件
     *
     * @param files
     */

    public static void uploadFiles(List<String> files, final UploadListener uploadListener) {

        List<MultipartBody.Part> list = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            File file = new File(files.get(i));
            String mediatype = "";
            if (files.get(i).contains(".mp4")) {
                mediatype = Constants.MediaTypeVideo;
            } else {
                mediatype = Constants.MediaTypeImag;
            }
            final String finalMediatype = mediatype;
            ProgressRequestBody requestFile = new ProgressRequestBody(file, mediatype, new UploadCallbacks() {
                @Override
                public void onProgressUpdate(int percentage) {

                    if (finalMediatype == Constants.MediaTypeVideo) {
                        uploadListener.onProgress(percentage);
                    }

                }

                @Override
                public void onError(Throwable e) {
                    LoggerUtils.d(Tag, "onError: " + e.getMessage());
                }

                @Override
                public void onFinish() {
                    LoggerUtils.d(Tag, "onFinish: ");
                }
            });
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", files.get(i), requestFile);
            list.add(part);
        }
        Call<UploadBean> call = RetrofitUtil.getInstance().build().create(ApiService.class).UploadFile(list);

        call.enqueue(new Callback<UploadBean>() {
            @Override
            public void onResponse(Call<UploadBean> call, Response<UploadBean> response) {
                if (response.isSuccessful()) {
                    if (response.body().getData() != null) {
                        LoggerUtils.d("123456 onResponse  ", response.body().getData().size() + "");
                    }
                    UploadBean bean = response.body();
                    uploadListener.onSucess(bean);
                } else {
                    CameraToastUtil.show(response.message(), AiCameraApplication.mApplication);
                }

            }

            @Override
            public void onFailure(Call<UploadBean> call, Throwable e) {
                LoggerUtils.d("123456  onFailure", e.getMessage() + "");
                uploadListener.onFail(e);
            }
        });


    }


}
