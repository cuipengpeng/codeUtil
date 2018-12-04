package com.downloader;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    private EditText addressET;
	private ProgressBar downloadPB;
	private TextView percentTV;
	private long totalLen;

	private ProgressBar downloadPB01;
	private TextView percentTV01;
	private long totalLen01;

	private ProgressBar downloadPB02;
	private TextView percentTV02;
	private long totalLen02;

	private ProgressBar downloadPB03;
	private TextView percentTV03;
	private long totalLen03;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
				case 0:
					switch (msg.arg1) {
						case 1:        // 如果收到总长度
							totalLen = msg.getData().getLong("totalLen");
							downloadPB.setMax((int) totalLen);                            // 设置进度条最大刻度
							break;
						case 2:        // 如果收到当前进度
							long totalFinish = msg.getData().getLong("totalFinish");
							downloadPB.setProgress((int) totalFinish);                    // 更新进度条
							percentTV.setText(totalFinish * 100 / totalLen + "%");        // 更新百分比
							break;
					}
					break;
				case 1:
					switch (msg.arg1) {
						case 1:        // 如果收到总长度
							totalLen01 = msg.getData().getLong("totalLen");
							downloadPB01.setMax((int) totalLen01);                            // 设置进度条最大刻度
							break;
						case 2:        // 如果收到当前进度
							long totalFinish = msg.getData().getLong("totalFinish");
							downloadPB01.setProgress((int) totalFinish);                    // 更新进度条
							percentTV01.setText(totalFinish * 100 / totalLen01 + "%");        // 更新百分比
							break;
					}
					break;
				case 2:
					switch (msg.arg1) {
						case 1:        // 如果收到总长度
							totalLen02 = msg.getData().getLong("totalLen");
							downloadPB02.setMax((int) totalLen02);                            // 设置进度条最大刻度
							break;
						case 2:        // 如果收到当前进度
							long totalFinish = msg.getData().getLong("totalFinish");
							downloadPB02.setProgress((int) totalFinish);                    // 更新进度条
							percentTV02.setText(totalFinish * 100 / totalLen02 + "%");        // 更新百分比
							break;
					}
					break;
				case 3:
					switch (msg.arg1) {
						case 1:        // 如果收到总长度
							totalLen03 = msg.getData().getLong("totalLen");
							downloadPB03.setMax((int) totalLen03);                            // 设置进度条最大刻度
							break;
						case 2:        // 如果收到当前进度
							long totalFinish = msg.getData().getLong("totalFinish");
							downloadPB03.setProgress((int) totalFinish);                    // 更新进度条
							percentTV03.setText(totalFinish * 100 / totalLen03 + "%");        // 更新百分比
							break;
					}
					break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        addressET = (EditText) findViewById(R.id.addressET);
        downloadPB = (ProgressBar) findViewById(R.id.downloadPB);
        percentTV = (TextView) findViewById(R.id.percentTV);

		downloadPB01 = (ProgressBar) findViewById(R.id.downloadPB01);
		percentTV01 = (TextView) findViewById(R.id.percentTV01);

		downloadPB02 = (ProgressBar) findViewById(R.id.downloadPB02);
		percentTV02 = (TextView) findViewById(R.id.percentTV02);

		downloadPB03 = (ProgressBar) findViewById(R.id.downloadPB03);
		percentTV03 = (TextView) findViewById(R.id.percentTV03);

		if(Build.VERSION.SDK_INT>=23){
			String[] mPermissionList = new String[]{
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.CALL_PHONE,
					Manifest.permission.READ_LOGS,
					Manifest.permission.READ_PHONE_STATE,
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.SET_DEBUG_APP,
					Manifest.permission.SYSTEM_ALERT_WINDOW,
					Manifest.permission.GET_ACCOUNTS,
					Manifest.permission.WRITE_APN_SETTINGS};
			ActivityCompat.requestPermissions(this,mPermissionList,123);
		}
    }


	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {

	}

    public void download(View view){
//		https://blog.csdn.net/MyLoveyaqiong/article/details/53142521
//		https://github.com/ljie/java-downFile/blob/master/downFile/src/main/java/com/softdu/utils/DownUtil.java
//		https://download.csdn.net/download/mlj1668956679/6669915
//		https://blog.csdn.net/u013256816/article/details/50403962

//    	String address = addressET.getText().toString().trim();
    	String address = "http://down.360safe.com/se/360se9.1.0.426.exe";
    	String address01 = "http://ftp.yz.yamagata-u.ac.jp/pub/eclipse/oomph/epp/2018-09/Ra/eclipse-inst-win64.exe";
		String address02 = "http://dldir1.qq.com/weixin/android/weixin657android1040.apk";
		String address03 = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
		String address04 = "http://ftp.yz.yamagata-u.ac.jp/pub/eclipse/oomph/epp/2018-09/Ra/eclipse-inst-linux64.tar.gz";
		String address05 = "http://ftp.yz.yamagata-u.ac.jp/pub/eclipse/oomph/epp/2018-09/Ra/eclipse-inst-mac64.tar.gz";
//    	String address = "http://video.chaogevideo.com/download/chaoge/01/chaoge/v1.0/chaoge_v1.0_yueming.apk";

		BreakpointDownloader.getInstance().downloadBigFile(address02, handler);
		BreakpointDownloader.getInstance().downloadBigFile(address, handler);
		BreakpointDownloader.getInstance().downloadBigFile(address01, handler);
		BreakpointDownloader.getInstance().downloadBigFile(address03, handler);
//		BreakpointDownloader.getInstance().downloadSmallFile(address, null, 1, new Handler());
    }
}