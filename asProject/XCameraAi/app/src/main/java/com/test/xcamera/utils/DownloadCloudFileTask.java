package com.test.xcamera.utils;

import android.os.AsyncTask;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/4/14
 * e-mail zhouxuecheng1991@163.com
 * 下载资源,支持断点续传功能
 */

public class DownloadCloudFileTask extends AsyncTask<String, Integer, Integer> {
    public static final String ERROR_MESSAGE_1 = "cloud file length is 0";
    public static final String ERROR_MESSAGE_2 = "local FileNotFoundException ";
    public static final String ERROR_MESSAGE_3 = "java.net.SocketException";
    public static final String ERROR_MESSAGE_4 = "java.net.SocketTimeoutException";
    public static final String ERROR_MESSAGE_5 = "response is null";
    public static final String HARDWARE_DID = "hardware did not exist";

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;
    private DownloadFileTaskListener listener;
    private final int ERITE_BUFFER_SIZE = 1024 * 1024;
    private final int CONNECT_TIME = 10;
    private final int READ_TIME = 30;

    private int lastProgress;
    private OkHttpClient client;

    public DownloadCloudFileTask(DownloadFileTaskListener listener) {
        this.listener = listener;
        if (client == null)
            client = new OkHttpClient();
        client.newBuilder().
                connectTimeout(CONNECT_TIME, TimeUnit.SECONDS).
                readTimeout(READ_TIME, TimeUnit.SECONDS);
    }

    /**
     * 开始下载,
     *
     * @param url           下载地址
     * @param localFilepath 下载文件存放路劲
     * @param localFileName 本地文件名
     */
    public void startDownload(String url, String localFilepath, String localFileName) {
        execute(url, localFilepath, localFileName);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (listener != null) {
            listener.startTask();
        }
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        long downloadLength = 0;   //记录已经下载的文件长度
        //文件下载地址
        String downloadUrl = params[0];
        //本地文件夹
        String apkLocalFileDir = params[1];
        //本地文件名称
        String apkLocalFileName = params[2];
        //创建一个文件
        file = new File(apkLocalFileDir + apkLocalFileName);
        if (file.exists()) {
            //如果文件存在的话，得到文件的大小
            downloadLength = file.length();
        }
        //得到下载内容的大小
        long contentLength = getContentLength(downloadUrl);
        if (contentLength == 0) {
            return -1;
        } else if (contentLength == 400) {
            return 400;
        }
        if(downloadLength > contentLength){
            downloadLength=0;
            file.delete();
        }
        if (downloadLength == contentLength) {
            publishProgress(100);
            if (listener != null) {
                listener.onSuccess(file);
            }
            return TYPE_SUCCESS;
        }
//        else if (contentLength == downloadLength) {
//            //todo 两个文件是不是一样,不能通过只判断其长度,还需要对内容进行判断,已下载字节和文件总字节相等，说明已经下载完成了
//            return TYPE_SUCCESS;
//        }

        /**
         * HTTP请求是有一个Header的，里面有个Range属性是定义下载区域的，它接收的值是一个区间范围，
         * 比如：Range:bytes=0-10000。这样我们就可以按照一定的规则，将一个大文件拆分为若干很小的部分，
         * 然后分批次的下载，每个小块下载完成之后，再合并到文件中；这样即使下载中断了，重新下载时，
         * 也可以通过文件的字节长度来判断下载的起始点，然后重启断点续传的过程，直到最后完成下载过程。
         */
        Request request = new Request.Builder()
                .addHeader("RANGE", "bytes=" + downloadLength + "-")  //断点续传要用到的，指示下载的区间
                .url(downloadUrl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadLength);//跳过已经下载的字节
                byte[] b = new byte[ERITE_BUFFER_SIZE];
                int total = 0;
                int len;
                while ((len = is.read(b, 0, b.length)) != -1) {
                    total += len;
                    savedFile.write(b, 0, len);
                    //计算已经下载的百分比
                    int progress = (int) ((total + downloadLength) * 100 / contentLength);
                    //注意：在doInBackground()中是不可以进行UI操作的，如果需要更新UI,比如说反馈当前任务的执行进度，
                    //可以调用publishProgress()方法完成。
                    publishProgress(progress);
                }
                response.body().close();
                if (listener != null) {
                    listener.onSuccess(file);
                }
                return TYPE_SUCCESS;
            }
        } catch (Exception e) {
            String errorInfo = e.toString();
            if (errorInfo.contains(ERROR_MESSAGE_2)) {
                return -2;
            } else if (errorInfo.contains(ERROR_MESSAGE_3)) {
                return -3;
            } else if (errorInfo.contains(ERROR_MESSAGE_4)) {
                return -4;
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -5;
    }

    /**
     * 当在后台任务中调用了publishProgress(Progress...)方法之后，onProgressUpdate()方法
     * 就会很快被调用，该方法中携带的参数就是在后台任务中传递过来的。在这个方法中可以对UI进行操作，利用参数中的数值就可以
     * 对界面进行相应的更新。
     *
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }


    /**
     * 当后台任务执行完毕并通过Return语句进行返回时，这个方法就很快被调用。返回的数据会作为参数
     * 传递到此方法中，可以利用返回的数据来进行一些UI操作。
     *
     * @param status
     */
    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_SUCCESS:

                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
                String errorInfo = "";
                switch (status) {
                    case -1:
                        errorInfo = ERROR_MESSAGE_1;
                        break;
                    case -2:
                        errorInfo = ERROR_MESSAGE_2;
                        break;
                    case -3:
                        errorInfo = ERROR_MESSAGE_3;
                        break;
                    case -4:
                        errorInfo = ERROR_MESSAGE_4;
                        break;
                    case -5:
                        errorInfo = ERROR_MESSAGE_5;
                        break;
                    case 400:
                        errorInfo = HARDWARE_DID;
                        break;
                }
                listener.onFailed(errorInfo);
                break;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        listener.onCanceled();
    }

    /**
     * 得到下载内容的完整大小
     *
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) {
        Request request = new Request.Builder().url(downloadUrl).build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful() && response.code() == 200) {
                long contentLength = response.body().contentLength();
                response.body().close();
                return contentLength;
            } else if (response != null && response.code() == 400) {
                return response.code();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //这个地方为什么改成类,不用接口呢,是因为是不是所有的方法,对于别人都是想用的,这个样的话,想复写哪一个方法自定确定就好
    public interface DownloadFileTaskListener {
        /**
         * 开始执行任务
         */
        void startTask();

        /**
         * 通知当前的下载进度
         *
         * @param progress
         */
        void onProgress(int progress);

        /**
         * 通知下载成功
         */
        void onSuccess(File file);

        /**
         * 通知下载失败
         */
        void onFailed(String error);

        /**
         * 通知下载暂停
         */
        void onPaused();

        /**
         * 通知下载取消事件
         */
        void onCanceled();

    }
}
