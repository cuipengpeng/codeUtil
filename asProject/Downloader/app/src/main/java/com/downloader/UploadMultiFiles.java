package com.downloader;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UploadMultiFiles {

    private float mProgress;
    private Handler mHandler;

    public UploadMultiFiles(){
        mHandler = new Handler(Looper.getMainLooper());
    }


    public void uploadFileWithPost(final UploadProgressListener progressListener){
        new Thread(new Runnable() {
            public void run() {
                try {
                    String actionUrl = "";
                    Map<String, String> headerParams = new HashMap<String, String>();
                    headerParams.put("X-token", "token");

                    Map<String, String> params = new HashMap<String, String>();

                    Map<String, File> fileMap = new HashMap<String, File>();
                    fileMap.put("file", new File("path"));




                    //----------------------------------------------------------------
                    String BOUNDARY = java.util.UUID.randomUUID().toString();
                    String PREFIX = "--", LINEND = "\r\n";
                    String MULTIPART_FROM_DATA = "multipart/form-data";
                    String CHARSET = "UTF-8";

                    URL uri = new URL(actionUrl);
                    HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
                    conn.setConnectTimeout(5 * 1000);
                    conn.setReadTimeout(10 * 1000);
                    conn.setDoInput(true);// 允许输入
                    conn.setDoOutput(true);// 允许输出
                    conn.setUseCaches(false); // 不允许使用缓存
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("connection", "keep-alive");
                    conn.setRequestProperty("Charsert", "UTF-8");
                    conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                            + ";boundary=" + BOUNDARY);
                    if(headerParams!=null){
                        for(String key : headerParams.keySet()){
                            conn.setRequestProperty(key, headerParams.get(key));
                        }
                    }

                    //发送参数数据
                    StringBuilder sb = new StringBuilder();
                    if (params!=null) {
                        // 首先组拼文本类型的参数
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            sb.append(PREFIX);
                            sb.append(BOUNDARY);
                            sb.append(LINEND);
                            sb.append("Content-Disposition: form-data; name=\""
                                    + entry.getKey() + "\"" + LINEND);
                            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                            sb.append(LINEND);
                            sb.append(entry.getValue());
                            sb.append(LINEND);
                        }
                    }
                    DataOutputStream outStream = new DataOutputStream(
                            conn.getOutputStream());
                    if (!TextUtils.isEmpty(sb.toString())) {
                        outStream.write(sb.toString().getBytes());
                    }


                    // 发送文件数据
                    int filePosition = 0;
                    int fileCount = fileMap.entrySet().size();
                    if (fileMap != null){
                        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(PREFIX);
                            stringBuilder.append(BOUNDARY);
                            stringBuilder.append(LINEND);
                            stringBuilder.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                                    + entry.getKey() + "\"" + LINEND);
                            stringBuilder.append("Content-Type: application/octet-stream; charset="
                                    + CHARSET + LINEND);
                            stringBuilder.append(LINEND);
                            outStream.write(stringBuilder.toString().getBytes());

                            if(filePosition==0){
                                mHandler.post(new Runnable() {
                                    public void run() {
                                        progressListener.onUploadStart();
                                    }
                                });
                            }
                            InputStream inputStream = new FileInputStream(entry.getValue());
                            int length = 0;
                            byte[] buffer = new byte[1024*20];
                            long currentLength = 0;
                            long totalLength = entry.getValue().length();
                            while ((length = inputStream.read(buffer)) != -1) {
                                outStream.write(buffer, 0, length);
                                currentLength += length;
                                float singleFilePercent = currentLength * 1.0f / totalLength;
                                if (singleFilePercent > 1){
                                    singleFilePercent = 1;
                                }
                                if (singleFilePercent < 0){
                                    singleFilePercent = 0;
                                }

                                mProgress = (filePosition+singleFilePercent)*100f*(1.0f/fileCount);
                                if (mProgress > 100){
                                    mProgress = 100;
                                }
                                if (mProgress < 0){
                                    mProgress = 0;
                                }
                                Log.i("HttpUtil","totalProgress="+mProgress+"--mFileCount="+fileCount+"-----mFilePosition="+filePosition+"----the current " + currentLength + "----" + totalLength + "-----" + (currentLength * 1.0f/totalLength));
                                mHandler.post(new Runnable() {
                                    public void run() {
                                        progressListener.onProgressUpdate((int) mProgress);
                                    }
                                });
                            }

                            inputStream.close();
                            filePosition++;
                            outStream.write(LINEND.getBytes());
                        }
                    }

                    // 请求结束标志
                    byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
                    outStream.write(end_data);
                    outStream.flush();

                    // 得到响应码
                    int res = conn.getResponseCode();
                    InputStream in = conn.getInputStream();
                    if (res == 200) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                        StringBuffer buffer = new StringBuffer();
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null){
                            buffer.append(line);
                        }
                        System.out.println("############ = "+buffer.toString());
                    }

                    mHandler.post(new Runnable() {
                        public void run() {
                            progressListener.onUploadFinish();
                        }
                    });
                    outStream.close();
                    conn.disconnect();
                }catch (final Exception e){
                    mHandler.post(new Runnable() {
                        public void run() {
                            progressListener.onFail("Fail to uploadload file: "+e.getMessage());
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface UploadProgressListener{
        void onUploadStart();
        void onProgressUpdate(int progress);
        void onUploadFinish();
        void onFail(String errorInfo);
    }
}
