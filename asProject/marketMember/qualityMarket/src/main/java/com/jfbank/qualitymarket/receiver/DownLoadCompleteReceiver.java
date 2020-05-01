package com.jfbank.qualitymarket.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.File;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/3/22 0022<br>.
 * 版本：1.2.0
 */

public class DownLoadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {//下载完成
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            //在广播中取出下载任务的id
            query.setFilterById(id);
            Cursor c = manager.query(query);
            if (c.moveToFirst()) {//
                //获取文件下载路径
                String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                //如果文件名不为空，说明已经存在了，拿到文件名想干嘛都好
                if (filename != null && filename.contains("PingZhiShangCheng_V") && filename.contains(".apk")) {//是否为下载安装包
                    Log.d("=====", "下载完成的文件名为：" + filename);
                    try{
                        Intent intent_ins = new Intent(Intent.ACTION_VIEW);
                        intent_ins.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent_ins.setDataAndType(Uri.parse("file://" + filename), "application/vnd.android.package-archive");
                        context.getApplicationContext().startActivity(intent_ins);
                    }catch (Exception e){
                        Log.e("downLoad","安装包异常");
                    }
                    //执行安装

//                    Log.d("=====", "截取后的文件名onReceive: "+filename);
                }
            }
            c.close();
            } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            }

    }
}