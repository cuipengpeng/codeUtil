package com.downloader;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    private EditText addressET;
	private ProgressBar downloadPB;
	private TextView percentTV;
	private long totalLen;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:		// 如果收到总长度
					totalLen = msg.getData().getLong("totalLen");
					downloadPB.setMax((int) totalLen);							// 设置进度条最大刻度
					break;
				case 2:		// 如果收到当前进度
					long totalFinish = msg.getData().getLong("totalFinish");
					downloadPB.setProgress((int) totalFinish);					// 更新进度条
					percentTV.setText(totalFinish * 100 / totalLen + "%");		// 更新百分比
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        addressET = (EditText) findViewById(R.id.addressET);
        downloadPB = (ProgressBar) findViewById(R.id.downloadPB);
        percentTV = (TextView) findViewById(R.id.percentTV);
    }
    
    public void download(View view) throws IOException {
//		https://blog.csdn.net/MyLoveyaqiong/article/details/53142521
//		https://github.com/ljie/java-downFile/blob/master/downFile/src/main/java/com/softdu/utils/DownUtil.java


//    	final String address = addressET.getText().toString().trim();
    	final String address = "http://down.360safe.com/se/360se9.1.0.426.exe";
//    	final String address = "http://ftp.yz.yamagata-u.ac.jp/pub/eclipse/oomph/epp/2018-09/Ra/eclipse-inst-win64.exe";
//    	final String address = "http://video.chaogevideo.com/download/chaoge/01/chaoge/v1.0/chaoge_v1.0_yueming.apk";
    	new Thread(new Runnable() {
			
			public void run() {
//				new BreakpointDownloader(address, handler).download();
				BreakpointDownloader.getInstance().download(address, handler);
			}
		}).start();
    }
}