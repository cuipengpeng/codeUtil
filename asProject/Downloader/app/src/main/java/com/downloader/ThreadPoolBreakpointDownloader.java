package com.downloader;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolBreakpointDownloader extends ThreadPoolExecutor{
    private static File DIR_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);    // 下载目录
    private static final int THREAD_AMOUNT = 4;                // 大文件下载总线程数
    private static final int CORE_POOL_SIZE = 5;                // 线程池核心线程数
    private static final int MAX_POOL_SIZE = 8;                // 线程池最大线程数
    private static final int TASK_SIZE = 150;

    private boolean debug = BuildConfig.DEBUG;
    private int mCounter = 0;
    private static int mTaskPosition = -1;
//    private  int[] orderArr = new int[10];
    private long[] begin = new long[TASK_SIZE];            // 用来记录开始下载时的时间
    private long[] totalFinish = new long[TASK_SIZE];    // 总共完成了多少

    private long smallFileTotalLength;
    private long smallFileTotalFinish;

    private static volatile ThreadPoolBreakpointDownloader mBreakpointDownloader;
    //页面销毁，不抛异常异常关闭线程池   ThreadPoolBreakpointDownloader.getInstance().shutdownThreadPoolNow();
    private ThreadPoolBreakpointDownloader(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        resetTaskPosition();
    }

    public static ThreadPoolBreakpointDownloader getInstance() {
        if (mBreakpointDownloader == null) {
            synchronized (ThreadPoolBreakpointDownloader.class) {
                if (mBreakpointDownloader == null) {
                    mBreakpointDownloader = new ThreadPoolBreakpointDownloader(CORE_POOL_SIZE, MAX_POOL_SIZE, 0L, TimeUnit.MICROSECONDS,
                                                                                new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(),
                                                                                new ThreadPoolExecutor.DiscardPolicy());
                }
            }
        }
        return mBreakpointDownloader;
    }


    /**
     * 多线程下载，支持断点续传
     * @param httpUrl
     */
    public File downloadBigFile(final String httpUrl, String saveDir, String fileName, final DownloadProgressCallback downloadProgressCallback) {
        if (fileName == null || "".equals(fileName)) {
			fileName = httpUrl.substring(httpUrl.lastIndexOf("/"), httpUrl.length());
//            fileName = MD5.messageDigest(httpUrl, "");
        }
        if(saveDir != null && !"".equals(saveDir)){
            DIR_PATH = new File(saveDir);
            if(!DIR_PATH.exists()){
                DIR_PATH.mkdirs();
            }
        }
        final File realDataFile = new File(DIR_PATH, fileName);    // 截取地址中的文件名, 创建本地文件
        final File dataFile = new File(realDataFile.getAbsolutePath() + ".temp");                        // 在本地文件所在文件夹中创建临时文件
        final File progressFile = new File(realDataFile.getAbsolutePath() + ".progress");                        // 在本地文件所在文件夹中记录进度的临时文件
        final Handler handler = new Handler(Looper.getMainLooper());


        if(dataFile.exists() && !progressFile.exists()){
            dataFile.delete();
        }
        if(!dataFile.exists() && progressFile.exists()){
            progressFile.delete();
        }

        if(realDataFile.exists()){
            handler.post(new Runnable() {
                public void run() {
                    downloadProgressCallback.onFinish(realDataFile);
                }
            });
            return realDataFile;
        }else {
           execute(new Runnable() {

                public void run() {
                    HttpURLConnection conn = null;
                    try {
                        URL url = new URL(httpUrl);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(3000);
                        conn.setReadTimeout(3000);
                        conn.setRequestMethod("GET");
                        //加上这句是为了防止connection.getContentLength()获取不到
                        conn.setRequestProperty("Accept-Encoding", "identity");

                        if (conn.getResponseCode() == 200) {
                            long totalLen = conn.getContentLength();                                    // 获取服务端发送过来的文件长度

                            if (!dataFile.exists()) {                                            // 如果本地文件不存在
                                RandomAccessFile raf = new RandomAccessFile(dataFile, "rws");    // 在本地创建文件
                                //setLength是先在存储设备占用一块空间,防止下载到一半空间不足, 设置文件的大小和服务端相同
                                raf.setLength(totalLen);
                                raf.close();
                            }

                            if (!progressFile.exists()) {                                            // 如果临时文件不存在
                                RandomAccessFile raf = new RandomAccessFile(progressFile, "rws");    // 创建临时文件, 用来记录每个线程已下载多少
                                for (int i = 0; i < THREAD_AMOUNT; i++) {                            // 按照线程数循环
                                    raf.writeLong(0);                                            // 写入每个线程的开始位置(都是从0开始)
                                }
                                raf.close();
                            }

                            synchronized (ThreadPoolBreakpointDownloader.this){
                                mTaskPosition++;
                                printLog("#############sendMsg--totalLen=" + totalLen + "--mTaskPosition=" + mTaskPosition);
                                if(mTaskPosition >=TASK_SIZE){
                                    return;
                                }
                                handler.post(new Runnable() {
                                    public void run() {
                                        downloadProgressCallback.onStart();
                                    }
                                });
                                // 按照线程数循环
                                for (int i = 0; i < THREAD_AMOUNT; i++) {
                                    execute(new DownloadThread(mTaskPosition, i, httpUrl, realDataFile, dataFile, progressFile, totalLen, THREAD_AMOUNT, handler, downloadProgressCallback));        // 开启线程, 每个线程将会下载一部分数据到本地文件中
                                }
                                begin[mTaskPosition] = System.currentTimeMillis();        // 记录开始时间
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (null != conn) {
                            conn.disconnect();
                        }
                    }
                }
            });
        }

        return null;
    }

    private class DownloadThread implements Runnable {
        private int id;    // 用来标记当前线程是下载任务中的第几个线程
        private int taskIndex;  // 目标下载地址
        private String httpUrl;
        private File realDataFile;        // 本地文件
        private File dataFile;        // 本地文件
        private File progressFile;        // 用来存储每个线程下载的进度的临时文件
        private long totalLen;        // 服务端文件总长度
        private long threadLen;        // 每个线程要下载的长度
        private int threadAmount;                // 大文件下载总线程数
        private DownloadProgressCallback downloadProgressCallback;
        private Handler handler;

        public DownloadThread(int taskIndex, int id, String httpUrl, File realDataFile, File dataFile, File progressFile, long totalLen, int threadAmount, Handler handler, DownloadProgressCallback downloadProgressCallback) {
            this.taskIndex = taskIndex;
            this.id = id;
            this.httpUrl = httpUrl;
            this.realDataFile = realDataFile;
            this.dataFile = dataFile;
            this.progressFile = progressFile;
            this.handler = handler;
            this.totalLen = totalLen;
            this.threadAmount = threadAmount;
            this.downloadProgressCallback = downloadProgressCallback;
        }

        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile dataRaf = null;
            RandomAccessFile tempRaf = null;
            InputStream in = null;
            threadLen = (totalLen + threadAmount - 1) / threadAmount;            // 计算每个线程要下载的长度
            printLog("totalLen=" + totalLen + "--threadLen=" + threadLen);

            try {
                tempRaf = new RandomAccessFile(progressFile, "rws");        // 用来记录下载进度的临时文件
                tempRaf.seek(id * 8);                        // 将指针移动到当前线程的位置(每个线程写1个long值, 占8字节)
                long threadFinish = tempRaf.readLong();        // 读取当前线程已完成了多少
                synchronized (ThreadPoolBreakpointDownloader.this) {    // 多个下载线程之间同步
                    totalFinish[taskIndex] += threadFinish;            // 统计所有线程总共完成了多少
                }

                long start = id * threadLen + threadFinish;        // 计算当前线程的起始位置
                long end = id * threadLen + threadLen - 1;        // 计算当前线程的结束位置
                if (id == THREAD_AMOUNT - 1) {
                    end = totalLen;        // 计算当前线程的结束位置
                }
                printLog("线程" + id + ": " + start + "-" + end);

                URL url = new URL(httpUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(10*1000);
                conn.setRequestProperty("Range", "bytes=" + start + "-" + end);        // 设置当前线程下载的范围
                printLog("线程" + id +"--taskIndex=" + taskIndex+ "--conn.getResponseCode()=" + conn.getResponseCode()+ "--httpUrl=" + httpUrl);

                //当请求部分数据成功的时候, 返回http状态码206
                if (conn.getResponseCode() == 206) {
                    in = conn.getInputStream();                                // 获取连接的输入流
                    dataRaf = new RandomAccessFile(dataFile, "rws");    // 装载数据的本地文件(可以理解为输出流)
                    dataRaf.seek(start);                               // 设置当前线程保存数据的位置

                    byte[] buffer = new byte[1024 * 200];            // 每次拷贝200KB
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        dataRaf.write(buffer, 0, len);                // 从服务端读取数据, 写到本地文件
                        threadFinish += len;                        // 每次写入数据之后, 统计当前线程完成了多少
                        tempRaf.seek(id * 8);                        // 将临时文件的指针指向当前线程的位置
                        tempRaf.writeLong(threadFinish);            // 将当前线程完成了多少写入到临时文件
                        synchronized (dataFile) {    // 多个下载线程之间同步
                        // synchronized (ThreadPoolBreakpointDownloader.this){       // 多个下载线程之间同步
                            totalFinish[taskIndex] += len;                        // 统计所有线程总共完成了多少
                            handler.post(new Runnable() {
                                public void run() {
                                    downloadProgressCallback.onProgress((int) (totalFinish[taskIndex]*100f/totalLen));
                                }
                            });
                        }
                    }

                    printLog("线程" + id + "下载完毕");
                    printLog("线程" + id + "--threadFinish=" + threadFinish + "--totalFinish=" + totalFinish[taskIndex] + "--httpUrl=" + httpUrl+ "\n");
                } else {
                    printLog("线程=" + id + "--Response Code != 206 (actualCode=" + conn.getResponseCode() + "),服务器不支持多线程下载。");
                }
            } catch (IOException e) {
                handler.post(new Runnable() {
                    public void run() {
                        downloadProgressCallback.onFail("Fail to download file "+dataFile.getAbsolutePath());
                    }
                });
                e.printStackTrace();
            } finally {
                try {
                    if (null != tempRaf) {
                        tempRaf.close();
                    }
                    if (null != conn) {
                        conn.disconnect();
                    }
                    if (null != in) {
                        in.close();
                    }
                    if (null != dataRaf) {
                        dataRaf.close();
                    }
                    if (totalFinish[taskIndex] == totalLen) {                    // 如果已完成长度等于服务端文件长度(代表下载完成)
                        printLog("下载完成, 耗时: " + (System.currentTimeMillis() - begin[taskIndex])+"--dataFile.length()="+dataFile.length()+ "--httpUrl=" + httpUrl);
                        progressFile.delete();                            // 删除临时文件
                        dataFile.renameTo(realDataFile); //文件下载完成后重命名
                        handler.post(new Runnable() {
                            public void run() {
                                downloadProgressCallback.onFinish(dataFile);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     *  单线程下载，不支持断点续传
     *
     * @param httpUrl
     * @return
     */
    public File downloadSmallFile(final String httpUrl, String savedDir, String fileName, final int itemPosition, final DownloadProgressCallback downloadProgressCallback) {
        if (httpUrl == null || "".equals(httpUrl)) {
            return null;
        }

        if (fileName == null || "".equals(fileName)) {
            fileName = MD5.messageDigest(httpUrl, "");
            String tmpFileName = httpUrl.substring(httpUrl.lastIndexOf("/"), httpUrl.length());
            String extensionName = tmpFileName.substring(tmpFileName.lastIndexOf("."), tmpFileName.length());
            fileName=fileName+extensionName;
        }

        File dir;
        if(!"".equals(savedDir) && savedDir !=null){
            dir = new File(savedDir);
        }else{
            dir = DIR_PATH;
        }
        if(!dir.exists()){
            dir.mkdirs();
        }

        final File dataFile = new File(dir, fileName);
        final File tempFile = new File(dataFile.getAbsolutePath()+".temp");
        final Handler handler = new Handler(Looper.getMainLooper());

        if (dataFile.exists()) {
            handler.post(new Runnable() {
                public void run() {
                    downloadProgressCallback.onFinish(dataFile);
                }
            });
            return dataFile;
        } else {
            if(!httpUrl.startsWith("http")){
                return null;
            }

            execute(new Runnable() {

                public void run() {
                    Message message = new Message();
                    HttpURLConnection connection = null;
                    FileOutputStream fileOutputStream = null;
                    InputStream inputStream = null;
                    if (tempFile.exists()) {
                        //上一次下载过程中，网络异常，下载失败
                        tempFile.delete();
                    }
                     smallFileTotalLength=0;
                     smallFileTotalFinish=0;

                    try {
                        URL url = new URL(httpUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);
                        //加上这句是为了防止connection.getContentLength()获取不到
                        connection.setRequestProperty("Accept-Encoding", "identity");

                        if (connection.getResponseCode() == 200) {
                            inputStream = connection.getInputStream();
                            smallFileTotalLength = connection.getContentLength();
                            printLog("smallFileTotalLength="+smallFileTotalLength);

                            if (inputStream != null) {
                                handler.post(new Runnable() {
                                    public void run() {
                                        downloadProgressCallback.onStart();
                                    }
                                });

                                fileOutputStream = new FileOutputStream(tempFile);
                                byte[] buffer = new byte[1024 * 100];  // 每次拷贝100KB
                                int length = 0;
                                while ((length = inputStream.read(buffer)) != -1) {
                                    smallFileTotalFinish += length;
                                    printLog("smallFileTotalFinish="+smallFileTotalFinish);
                                    fileOutputStream.write(buffer, 0, length);
                                    handler.post(new Runnable() {
                                        public void run() {
                                            downloadProgressCallback.onProgress((int) (smallFileTotalFinish*100f/smallFileTotalLength));
                                        }
                                    });
                                }
                                fileOutputStream.flush();
                            }
                        }
                    } catch (IOException e) {
                        handler.post(new Runnable() {
                            public void run() {
                                downloadProgressCallback.onFail("Fail to download file "+dataFile.getAbsolutePath());
                            }
                        });
                        e.printStackTrace();
                    }finally {
                        try {
                            if (null != fileOutputStream) {
                                fileOutputStream.close();
                            }
                            if (null != connection) {
                                connection.disconnect();
                            }
                            if (null != inputStream) {
                                inputStream.close();
                            }
                            if (smallFileTotalFinish == smallFileTotalLength && smallFileTotalFinish >0 && smallFileTotalLength >0 ) {
                                //文件下载完成后重命名
                                tempFile.renameTo(dataFile);
                                printLog("已经下载完成");
                                handler.post(new Runnable() {
                                    public void run() {
                                        downloadProgressCallback.onFinish(dataFile);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        return null;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        synchronized (this){
            mCounter++;
            printLog("mCounter="+ mCounter);
        }
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        synchronized (this){
            mCounter--;
            printLog("after   mCounter="+ mCounter);
            if(mCounter ==0){
                resetTaskPosition();
//              shutdownThreadPoolNow();
            }
        }
    }

    @Override
    protected void terminated() {
        synchronized (this){
            printLog("terminate    mCounter="+ mCounter);
        }
        super.terminated();
    }

    public void shutdownThreadPoolNow(){
        resetTaskPosition();
        shutdownNow();
    }

    private void resetTaskPosition(){
        mTaskPosition =-1;
        for (int i=0; i<begin.length;i++){
            begin[i]=0;
        }
        for (int i=0; i<totalFinish.length;i++){
            totalFinish[i]=0;
        }
    }


    private void printLog(String log){
        if(debug){
            System.out.println(log);
        }
    }

    public interface DownloadProgressCallback{
        void onStart();//下载开始
        void onProgress(int progress);//下载进度
        void onFinish(File path);//下载完成
        void onFail(String errorInfo);//下载失败
    }
}

