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
    private static final File DIR_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);    // 下载目录
    private static final int THREAD_AMOUNT = 3;                // 大文件下载总线程数
    private static final int CORE_POOL_SIZE = 5;                // 线程池核心线程数
    private static final int MAX_POOL_SIZE = 20;                // 线程池最大线程数

    private static int taskCount = -1;
//    private  int[] orderArr = new int[10];
    private long[] begin = new long[10];            // 用来记录开始下载时的时间
    private long[] totalFinish = new long[10];    // 总共完成了多少

    private static volatile BreakpointDownloader mBreakpointDownloader;
    private  ThreadPoolExecutor threadPoolExecutor;

    private BreakpointDownloader() {
//        for (int i=0; i<orderArr.length;i++){
//            orderArr[i]=i;
//        }
        for (int i=0; i<begin.length;i++){
            begin[i]=0;
        }
        for (int i=0; i<totalFinish.length;i++){
            totalFinish[i]=0;
        }
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

    /**
     * 多线程下载，支持断点续传
     * @param address
     * @param handler
     */
    public File downloadBigFile(final String address, String fileName, final Handler handler) {
        if (fileName == null || "".equals(fileName)) {
			fileName = address.substring(address.lastIndexOf("/"), address.length());
//            fileName = MD5.messageDigest(address, "");
        }
        final File dataFile = new File(DIR_PATH, fileName);    // 截取地址中的文件名, 创建本地文件
        final File tempFile = new File(dataFile.getAbsolutePath() + ".temp");                        // 在本地文件所在文件夹中创建临时文件

        if(dataFile.exists() && !tempFile.exists()){
            if(handler!=null){
                Message message = handler.obtainMessage();
                message.what = 1000;
                message.obj = dataFile.getAbsolutePath();
                handler.sendMessage(message);
            }
            return dataFile;
        }else {
            new Thread(new Runnable() {

                public void run() {
                    HttpURLConnection conn = null;
                    try {
                        URL url = new URL(address);
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

                            if (!tempFile.exists()) {                                            // 如果临时文件不存在
                                RandomAccessFile raf = new RandomAccessFile(tempFile, "rws");    // 创建临时文件, 用来记录每个线程已下载多少
                                for (int i = 0; i < THREAD_AMOUNT; i++) {                            // 按照线程数循环
                                    raf.writeLong(0);                                            // 写入每个线程的开始位置(都是从0开始)
                                }
                                raf.close();
                            }

                            Message msg = new Message();
                            msg.getData().putLong("totalLen", totalLen);
                            msg.arg1 = 1;
                            synchronized (BreakpointDownloader.this){
                                taskCount++;
                                System.out.println("#############sendMsg--totalLen=" + totalLen + "--taskCount=" + taskCount);
                                msg.what = taskCount;
                                if(handler!=null){
                                    handler.sendMessage(msg);                                            // 发送文件总长度
                                }

                                // 按照线程数循环
                                for (int i = 0; i < THREAD_AMOUNT; i++) {
        //                            new Thread(new DownloadThread(i, url, dataFile, tempFile, handler)).start();        // 开启线程, 每个线程将会下载一部分数据到本地文件中
                                    execute(new DownloadThread(taskCount, i, address, dataFile, tempFile, totalLen, THREAD_AMOUNT, handler));        // 开启线程, 每个线程将会下载一部分数据到本地文件中
                                }
                                begin[taskCount] = System.currentTimeMillis();        // 记录开始时间
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
            }).start();
        }

        return null;
    }

    private class DownloadThread implements Runnable {
        private int id;    // 用来标记当前线程是下载任务中的第几个线程
        private int taskIndex;  // 目标下载地址
        private String address;
        private File dataFile;        // 本地文件
        private File tempFile;        // 用来存储每个线程下载的进度的临时文件
        private long totalLen;        // 服务端文件总长度
        private long threadLen;        // 每个线程要下载的长度
        private int threadAmount;                // 大文件下载总线程数

        private Handler handler;

        public DownloadThread(int taskIndex, int id, String address, File dataFile, File tempFile, long totalLen, int threadAmount, Handler handler) {
            this.taskIndex = taskIndex;
            this.id = id;
            this.address = address;
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
                    totalFinish[taskIndex] += threadFinish;            // 统计所有线程总共完成了多少
                }

                long start = id * threadLen + threadFinish;        // 计算当前线程的起始位置
                long end = id * threadLen + threadLen - 1;        // 计算当前线程的结束位置
                if (id == THREAD_AMOUNT - 1) {
                    end = totalLen;        // 计算当前线程的结束位置
                }
                System.out.println("线程" + id + ": " + start + "-" + end);

                URL url = new URL(address);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestProperty("Range", "bytes=" + start + "-" + end);        // 设置当前线程下载的范围
                System.out.println("线程" + id +"--taskIndex=" + taskIndex+ "--conn.getResponseCode()=" + conn.getResponseCode()+ "--address=" + address);

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
                        synchronized (dataFile) {    // 多个下载线程之间同步
                        // synchronized (BreakpointDownloader.this){       // 多个下载线程之间同步
                            totalFinish[taskIndex] += len;                        // 统计所有线程总共完成了多少
                            Message msg = new Message();
                            msg.getData().putLong("totalFinish", totalFinish[taskIndex]);
                            msg.what = taskIndex;
                            msg.arg1 = 2;
                            if(handler!=null){
                                handler.sendMessage(msg);                // 发送当前进度
                            }
                        }
                    }

                    System.out.println("线程" + id + "下载完毕");
                    System.out.println("线程" + id + "--threadFinish=" + threadFinish + "--totalFinish=" + totalFinish[taskIndex] + "--address=" + address+ "\n");
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
                    if (totalFinish[taskIndex] == totalLen) {                    // 如果已完成长度等于服务端文件长度(代表下载完成)
                        System.out.println("下载完成, 耗时: " + (System.currentTimeMillis() - begin[taskIndex])+"--dataFile.length()="+dataFile.length()+ "--address=" + address);
                        tempFile.delete();                            // 删除临时文件
//                        dataFile.renameTo(new File(dataFile.getAbsolutePath()+".aaaaaa")); //文件下载完成后重命名
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
    public File downloadSmallFile(final String httpUrl, String fileName, final int itemPosition, final Handler handler) {
        if (httpUrl == null || "".equals(httpUrl)) {
            if(handler!=null){
                Message message = handler.obtainMessage();
                message.what = 2;
                handler.sendMessage(message);
            }
            return null;
        }

        if (fileName == null || "".equals(fileName)) {
//			fileName = httpUrl.substring(httpUrl.lastIndexOf("/"), httpUrl.length());
            fileName = MD5.messageDigest(httpUrl, "");
        }
        final File dataFile = new File(DIR_PATH, fileName);
        final File tempFile = new File(dataFile.getAbsolutePath()+".temp");

        if (dataFile.exists()) {
            if(handler!=null){
                Message message = handler.obtainMessage();
                message.what = 3;                //1代码成功，2代表失败 3代表文件已存在
                message.obj = dataFile.getAbsolutePath();
                message.arg1 = itemPosition;
                handler.sendMessage(message);
            }
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
                    long totalLen=0;
                    long totalFinish=0;

                    try {
                        URL url = new URL(httpUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        //加上这句是为了防止connection.getContentLength()获取不到
                        connection.setRequestProperty("Accept-Encoding", "identity");

                        if (connection.getResponseCode() == 200) {
                            inputStream = connection.getInputStream();
                            totalLen = connection.getContentLength();
                            System.out.println("totalLen="+totalLen);

                            if (inputStream != null) {
                                fileOutputStream = new FileOutputStream(tempFile);
                                byte[] buffer = new byte[1024 * 100];
                                int length = 0;
                                while ((length = inputStream.read(buffer)) != -1) {
                                    totalFinish += length;
                                    System.out.println("totalFinish="+totalFinish);
                                    fileOutputStream.write(buffer, 0, length);
                                }
                                fileOutputStream.flush();
                            }
                        }
                    } catch (IOException e) {
                        message.what = 2;
                        if(handler!=null){
                            handler.sendMessage(message);
                        }
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
                            if (totalFinish == totalLen && totalFinish >0 && totalLen >0 ) {
                                //文件下载完成后重命名
                                tempFile.renameTo(dataFile);

                                System.out.println("已经下载完成");
                                message.what = 1;                //1代码成功，2代表失败 3代表文件已存在
                                message.obj = dataFile.getAbsolutePath();
                                message.arg1 = itemPosition;
                                if(handler!=null){
                                    handler.sendMessage(message);
                                 }
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
}

