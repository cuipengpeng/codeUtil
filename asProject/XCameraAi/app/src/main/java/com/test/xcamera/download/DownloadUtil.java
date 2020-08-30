package com.test.xcamera.download;

import android.support.annotation.NonNull;


import com.test.xcamera.api.ApiService;
import com.test.xcamera.utils.AppExecutors;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.LoggerUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Author: mz
 * Time:  2019/9/27
 */
public class DownloadUtil {

    private static Call<ResponseBody> call;

    /**
     * 下载Apk
     *
     * @param appversion
     * @param path
     * @param downloadListener
     */
    public static void downloadApk(String appversion, final String path, DownloadListener downloadListener) {
        ApiService service = initRetrfit().create(ApiService.class);
        Call<ResponseBody> call = service.UpgradeApp(appversion);
        initCallBack(call, appversion, path, downloadListener);

    }

    /**
     * 下载固件版本包
     *
     * @param hardwareversion
     * @param path
     * @param downloadListener
     */
    public static void downHardWareFile(String version, String hardwareversion, final String path, final DownloadListener downloadListener) {
        ApiService service = initRetrfit().create(ApiService.class);
        call = service.UpgradeHardWareFile(version, hardwareversion);
        initCallBack(call, hardwareversion, path, downloadListener);

    }

    public static Retrofit initRetrfit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.base_url)
                //通过线程池获取一个线程，指定callback在子线程中运行。
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build();
        return retrofit;
    }


    public static void initCallBack(Call<ResponseBody> call, String hardwareversion, final String path, final DownloadListener downloadListener) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                //将Response写入到从磁盘中，详见下面分析
                //注意，这个方法是运行在子线程中的
                //todo
                writeResponseToDisk(path, response, downloadListener);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                downloadListener.onFail("网络错误～");
            }
        });
    }


    private static void writeResponseToDisk(String path, Response<ResponseBody> response, DownloadListener downloadListener) {
        //从response获取输入流以及总大小
        //todo
        if (response.body() != null)
            writeFileFromIS(new File(path), response.body().byteStream(), response.body().contentLength(), downloadListener);
        else
            downloadListener.onFail("response body is null");
    }

    private static int sBufferSize = 8192;
    private static final String TAG = "OTA_LOG";

    //将输入流写入文件
    private static void writeFileFromIS(File file, InputStream is, long totalLength, DownloadListener downloadListener) {
        //开始下载
        downloadListener.onStart();

        boolean f1 = file.exists();

        File file1 = file.getParentFile();
        if (file1 == null) {
            downloadListener.onFail("createNewFile IOException");
            return;
        }
        //创建文件
        if (!f1) {
            if (!file1.exists())
                file1.mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                downloadListener.onFail("createNewFile IOException");
            }
        }
        LoggerUtils.i(TAG, "开始下载升级文件 !");
        OutputStream os = null;
        long currentLength = 0;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[8192];
            int len;
            while ((len = is.read(data, 0, data.length)) != -1) {
                os.write(data, 0, len);
                currentLength += len;
                //计算当前下载进度
                int pro = (int) (100 * currentLength / totalLength);
                if (file.length() == totalLength) {
                    break;
                }
                downloadListener.onProgress(pro);
            }
            os.flush();
            //下载完成，并返回保存的文件路径
            downloadListener.onFinish(file);
            LoggerUtils.i(TAG, "升级文件下载成功 !");
        } catch (IOException e) {
            LoggerUtils.i(TAG, "升级文件下载失败 error = " + e.toString());
            e.printStackTrace();
            downloadListener.onFail("IOException");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    /**
     * 下载文件
     *
     * @param fileId
     * @param path
     * @param downloadListener
     */
    public static void downLoadFile(long fileId, String path, DownloadListener downloadListener) {
        ApiService service = initRetrfit().create(ApiService.class);
        Call<ResponseBody> call = service.downLoadFile(fileId);
        initCallBack(call, "", path, downloadListener);
    }

    /**
     * 下载文件
     *
     * @param path
     * @param downloadListener
     */
    public static void downLoad(String url, String path, DownloadListener downloadListener) {
        ApiService service = initRetrfit().create(ApiService.class);
        Call<ResponseBody> call = service.downLoad(url);
        initCallBack(call, "", path, downloadListener);
    }
    /**
     * 获取下载文件大小
     *
     * @param downloadListener
     */
    public static void getDownLoadLength(String url, OnDownloadCallback downloadListener) {
        ApiService service = initRetrfit().create(ApiService.class);
        Call<ResponseBody> call = service.downLoad(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                long length= response.body().contentLength();
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(downloadListener!=null){
                            downloadListener.onDownLoadLength(length);
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        downloadListener.onFail("网络错误～");
                    }
                });
            }
        });
    }
}
