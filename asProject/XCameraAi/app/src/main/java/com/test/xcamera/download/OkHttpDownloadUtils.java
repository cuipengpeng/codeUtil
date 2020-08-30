package com.test.xcamera.download;

import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.LoggerUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/3/20
 * e-mail zhouxuecheng1991@163.com
 * <p>
 * 使用 okhttp 下载固件和apk
 */
public class OkHttpDownloadUtils {

    private final OkHttpClient okHttpClient;
    private final String TAG = "OTA_LOG";
    private Call requestCall = null;
    private final int WRITE_BUFFER = 1024 * 1024;//8192;//1024 * 1024;
    private final int CONNECT_TIMEOUT = 10;//设置连接超时
    private final int READ_TIMEOUT = 30;//设置读取超时

    private OkHttpDownloadUtils() {
        okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder().
                readTimeout(READ_TIMEOUT, TimeUnit.SECONDS).
                connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
    }

    private static OkHttpDownloadUtils okHttpDownloadFile = new OkHttpDownloadUtils();

    public static OkHttpDownloadUtils instance() {
        return okHttpDownloadFile;
    }

    public Call getRequestCall() {
        return requestCall;
    }

    /**
     * download  file
     *
     * @param version
     * @param did              下载apk文件的时候,这个值可以随便给
     * @param path
     * @param downloadListener
     */
    public void startDownload(String version, String did, String path, boolean isApk, DownloadListener downloadListener) {
        String url = null;
        if (!isApk) {
            url = Constants.base_url + "api/firmware/package/" + version + "?did=" + did;
        } else {
            url = Constants.base_url + "api/app/package/" + version;
        }

        Request request = new Request.Builder().url(url).build();
        requestCall = okHttpClient.newCall(request);
        requestCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (downloadListener != null) {
                    downloadListener.onFail(e.toString());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    writeFileFromIS(new File(path), response.body().byteStream(), response.body().contentLength(), downloadListener);
                } else {
                    if (downloadListener != null) {
                        downloadListener.onFail("response is null");
                    }
                }
            }
        });
    }


    //将输入流写入文件
    private void writeFileFromIS(File localApkFile, InputStream is, long totalLength, DownloadListener downloadListener) {
        //开始下载
        downloadListener.onStart();

        boolean apkFileExists = localApkFile.exists();

        File fileDir = localApkFile.getParentFile();
        if (fileDir == null) {
            downloadListener.onFail("createNewFile IOException");
            return;
        }
        //创建文件
        if (!apkFileExists) {
            if (!fileDir.exists())
                fileDir.mkdir();
            try {
                localApkFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                downloadListener.onFail("createNewFile IOException");
            }
        }
        LoggerUtils.i(TAG, "开始下载升级文件 !");
        OutputStream os = null;
        long currentLength = 0;
        try {
            os = new BufferedOutputStream(new FileOutputStream(localApkFile));
            byte data[] = new byte[WRITE_BUFFER];
            int len;
            while ((len = is.read(data, 0, data.length)) != -1) {
                os.write(data, 0, len);
                currentLength += len;
                //计算当前下载进度
                int pro = (int) (100 * currentLength / totalLength);
                if (localApkFile.length() == totalLength) {
                    break;
                }
                downloadListener.onProgress(pro);
            }
            os.flush();
            //下载完成，并返回保存的文件路径
            downloadListener.onFinish(localApkFile);
            LoggerUtils.i(TAG, "升级文件下载成功 !");
        } catch (IOException e) {
            LoggerUtils.i(TAG, "升级文件下载失败 error = " + e.toString());
            e.printStackTrace();
            downloadListener.onFail(e.toString());
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                    os = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
