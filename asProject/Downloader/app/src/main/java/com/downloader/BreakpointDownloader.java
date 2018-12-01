package com.downloader;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BreakpointDownloader {
    private static final String DIR_PATH = "/mnt/sdcard/";    // 下载目录
    private static final int THREAD_AMOUNT = 5;                // 大文件下载总线程数
    private static final int CORE_POOL_SIZE = 5;                // 线程池核心线程数
    private static final int MAX_POOL_SIZE = 20;                // 线程池最大线程数


    private long begin;            // 用来记录开始下载时的时间
    private long totalFinish = 0;    // 总共完成了多少

    private static volatile BreakpointDownloader mBreakpointDownloader;
    private  ThreadPoolExecutor threadPoolExecutor;

    private BreakpointDownloader() {
    }

    public static BreakpointDownloader getInstance() {
        if (mBreakpointDownloader == null) {
            synchronized (BreakpointDownloader.class) {
                if (mBreakpointDownloader == null) {
                    mBreakpointDownloader = new BreakpointDownloader();
                }
            }
        }
        return mBreakpointDownloader;
    }

    public  void execute(Runnable r){
        if(threadPoolExecutor == null){
            threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 0L, TimeUnit.MICROSECONDS,
                            new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(),
                            new ThreadPoolExecutor.AbortPolicy());
        }
        if(threadPoolExecutor != null){
            threadPoolExecutor.execute(r);
        }
    }

    public void downloadBigFile(final String address, final Handler handler) {
        new Thread(new Runnable() {

            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(address);                                                            // 记住下载地址
                    File dataFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), address.substring(address.lastIndexOf("/") + 1));    // 截取地址中的文件名, 创建本地文件
                    File tempFile = new File(dataFile.getAbsolutePath() + ".temp");                        // 在本地文件所在文件夹中创建临时文件

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);
                    conn.setRequestMethod("GET");

                    if (conn.getResponseCode() == 200) {
                        long totalLen = conn.getContentLength();                                    // 获取服务端发送过来的文件长度
                        Message msg = new Message();
                        msg.getData().putLong("totalLen", totalLen);
                        msg.what = 1;
                        handler.sendMessage(msg);                                            // 发送文件总长度

                        if (!dataFile.exists()) {                                            // 如果本地文件不存在
                            RandomAccessFile raf = new RandomAccessFile(dataFile, "rws");    // 在本地创建文件
                            //setLength是先在存储设备占用一块空间,防止下载到一半空间不足, 设置文件的大小和服务端相同
                            raf.setLength(totalLen);
                            raf.close();
                        }

                        if (!tempFile.exists()) {                                            // 如果临时文件不存在
                            RandomAccessFile raf = new RandomAccessFile(tempFile, "rws");    // 创建临时文件, 用来记录每个线程已下载多少
                            for (int i = 0; i < THREAD_AMOUNT; i++)                            // 按照线程数循环
                                raf.writeLong(0);                                            // 写入每个线程的开始位置(都是从0开始)
                            raf.close();
                        }

                        // 按照线程数循环
                        for (int i = 0; i < THREAD_AMOUNT; i++) {
//                            new Thread(new DownloadThread(i, url, dataFile, tempFile, handler)).start();        // 开启线程, 每个线程将会下载一部分数据到本地文件中
                            execute(new DownloadThread(i, url, dataFile, tempFile, totalLen, THREAD_AMOUNT, handler));        // 开启线程, 每个线程将会下载一部分数据到本地文件中
                        }

                        begin = System.currentTimeMillis();        // 记录开始时间
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (null != conn) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    private class DownloadThread implements Runnable {
        private int id;    // 用来标记当前线程是下载任务中的第几个线程
        private URL url;            // 目标下载地址
        private File dataFile;        // 本地文件
        private File tempFile;        // 用来存储每个线程下载的进度的临时文件
        private long totalLen;        // 服务端文件总长度
        private long threadLen;        // 每个线程要下载的长度
        private int threadAmount;                // 大文件下载总线程数

        private Handler handler;

        public DownloadThread(int id, URL url, File dataFile, File tempFile, long totalLen, int threadAmount, Handler handler) {
            this.id = id;
            this.url = url;
            this.dataFile = dataFile;
            this.tempFile = tempFile;
            this.handler = handler;
            this.totalLen = totalLen;
            this.threadAmount = threadAmount;
        }

        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile dataRaf = null;
            RandomAccessFile tempRaf = null;
            InputStream in = null;
            threadLen = (totalLen + threadAmount - 1) / threadAmount;            // 计算每个线程要下载的长度
            System.out.println("totalLen=" + totalLen + "--threadLen=" + threadLen);

            try {
                tempRaf = new RandomAccessFile(tempFile, "rws");        // 用来记录下载进度的临时文件
                tempRaf.seek(id * 8);                        // 将指针移动到当前线程的位置(每个线程写1个long值, 占8字节)
                long threadFinish = tempRaf.readLong();        // 读取当前线程已完成了多少
                synchronized (BreakpointDownloader.this) {    // 多个下载线程之间同步
                    totalFinish += threadFinish;            // 统计所有线程总共完成了多少
                }

                long start = id * threadLen + threadFinish;        // 计算当前线程的起始位置
                long end = id * threadLen + threadLen - 1;        // 计算当前线程的结束位置
                if (id == THREAD_AMOUNT - 1) {
                    end = totalLen;        // 计算当前线程的结束位置
                }
                System.out.println("线程" + id + ": " + start + "-" + end);

                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestProperty("Range", "bytes=" + start + "-" + end);        // 设置当前线程下载的范围
                System.out.println("线程" + id + "--conn.getResponseCode()=" + conn.getResponseCode());

                //当请求部分数据成功的时候, 返回http状态码206
                if (conn.getResponseCode() == 206) {
                    in = conn.getInputStream();                                // 获取连接的输入流
                    dataRaf = new RandomAccessFile(dataFile, "rws");    // 装载数据的本地文件(可以理解为输出流)
                    dataRaf.seek(start);                               // 设置当前线程保存数据的位置

                    byte[] buffer = new byte[1024 * 100];            // 每次拷贝100KB
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        dataRaf.write(buffer, 0, len);                // 从服务端读取数据, 写到本地文件
                        threadFinish += len;                        // 每次写入数据之后, 统计当前线程完成了多少
                        tempRaf.seek(id * 8);                        // 将临时文件的指针指向当前线程的位置
                        tempRaf.writeLong(threadFinish);            // 将当前线程完成了多少写入到临时文件
                        synchronized (BreakpointDownloader.this) {    // 多个下载线程之间同步
                            totalFinish += len;                        // 统计所有线程总共完成了多少
                            Message msg = new Message();
                            msg.getData().putLong("totalFinish", totalFinish);
                            msg.what = 2;
                            handler.sendMessage(msg);                // 发送当前进度
                        }
                    }

                    System.out.println("线程" + id + "下载完毕");
                    System.out.println("线程" + id + "--threadFinish=" + threadFinish + "--totalFinish=" + totalFinish + "\n");
                } else {
                    System.out.println("线程=" + id + "--Response Code != 206 (actualCode=" + conn.getResponseCode() + "),服务器不支持多线程下载。");
                }
            } catch (IOException e) {
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
                    if (totalFinish == totalLen) {                    // 如果已完成长度等于服务端文件长度(代表下载完成)
                        System.out.println("下载完成, 耗时: " + (System.currentTimeMillis() - begin));
                        tempFile.delete();                            // 删除临时文件
//                        dataFile.renameTo(new File(dataFile.getAbsolutePath()+".aaaaaa"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 文件下载
     *
     * @param httpUrl
     * @return
     */
    public void downloadSmallFile(final String httpUrl, String fileName, final int itemPosition, final Handler handler) {
        if (fileName == null || "".equals(fileName)) {
//			fileName = httpUrl.substring(httpUrl.lastIndexOf("/"), httpUrl.length());
            fileName = MD5.messageDigest(httpUrl, "");
        }
        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        if (file.exists()) {
            Message message = handler.obtainMessage();
            message.what = 3;                //1代码成功，2代表失败 3代表文件已存在
            message.obj = file.getAbsolutePath();
            message.arg1 = itemPosition;
            handler.sendMessage(message);
        } else {
            execute(new Runnable() {

                public void run() {
                    Message message = handler.obtainMessage();
                    try {
                        URL url = new URL(httpUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        FileOutputStream fileOutputStream = null;
                        InputStream inputStream;
                        if (connection.getResponseCode() == 200) {
                            inputStream = connection.getInputStream();

                            if (inputStream != null) {
                                fileOutputStream = new FileOutputStream(file);
                                byte[] buffer = new byte[1024 * 100];
                                int length = 0;

                                while ((length = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, length);
                                }
                                fileOutputStream.flush();
                                fileOutputStream.close();
                            }
                            inputStream.close();
                        }

                        System.out.println("已经下载完成");

                        message.what = 1;                //1代码成功，2代表失败 3代表文件已存在
                        message.obj = file.getAbsolutePath();
                        message.arg1 = itemPosition;
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        message.what = 2;
                        handler.sendMessage(message);
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}

