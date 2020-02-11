package com.downloader;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends Activity {
    private EditText addressET;

	private ProgressBar downloadPB;
	private TextView percentTV;
	private ProgressBar downloadPB01;
	private TextView percentTV01;
	private ProgressBar downloadPB02;
	private TextView percentTV02;
	private ProgressBar downloadPB03;
	private TextView percentTV03;

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
//        https://blog.csdn.net/zengrenyuan/article/details/78117559
//		https://github.com/ljie/java-downFile/blob/master/downFile/src/main/java/com/softdu/utils/DownUtil.java
//		https://download.csdn.net/download/mlj1668956679/6669915
//		https://blog.csdn.net/u013256816/article/details/50403962
//      https://blog.csdn.net/guoshengkai373/article/details/78457928

//    	String address = addressET.getText().toString().trim();
    	String address = "http://down.360safe.com/se/360se9.1.0.426.exe";
    	String address01 = "http://ftp.yz.yamagata-u.ac.jp/pub/eclipse/oomph/epp/2018-09/Ra/eclipse-inst-win64.exe";
		String address02 = "http://dldir1.qq.com/weixin/android/weixin657android1040.apk";
		String address03 = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
		String address04 = "http://ftp.yz.yamagata-u.ac.jp/pub/eclipse/oomph/epp/2018-09/Ra/eclipse-inst-linux64.tar.gz";
		String address05 = "http://ftp.yz.yamagata-u.ac.jp/pub/eclipse/oomph/epp/2018-09/Ra/eclipse-inst-mac64.tar.gz";
    	String address08 = "http://video.chaogevideo.com/download/chaoge/01/chaoge/v1.0/chaoge_v1.0_yueming.apk";
    	String address09 = "https://movies.ds.9wuli.com/movies/20190104/a150e1493a545c7a6160dd698958c20b?sign=2deffe6d417e9c1267afa7241ec30223&t=5c354791";

		ThreadPoolBreakpointDownloader.getInstance().downloadSmallFile(address, null, "", 1, new ThreadPoolBreakpointDownloader.DownloadProgressCallback() {
			public void onStart() {
				downloadPB.setMax(100);
			}

			public void onProgress(int progress) {
				downloadPB.setProgress(progress);
				percentTV.setText(progress+"%");
			}

			public void onFinish(File path) {
			}

			public void onFail(String errorInfo) {
			}
		});

//		ThreadPoolBreakpointDownloader.getInstance().downloadBigFile(address02, "", "", new ThreadPoolBreakpointDownloader.DownloadProgressCallback() {
//			public void onStart() {
//				downloadPB.setMax(100);
//			}
//
//			public void onProgress(int progress) {
//				downloadPB.setProgress(progress);
//				percentTV.setText(progress+"%");
//			}
//
//			public void onFinish(File path) {
//			}
//
//			public void onFail(String errorInfo) {
//			}
//		});
		ThreadPoolBreakpointDownloader.getInstance().downloadBigFile(address, "", "", new ThreadPoolBreakpointDownloader.DownloadProgressCallback(){
			public void onStart() {
				downloadPB01.setMax(100);
			}

			public void onProgress(int progress) {
				downloadPB01.setProgress(progress);
				percentTV01.setText(progress+"%");
			}
			public void onFinish(File path) {
			}

			public void onFail(String errorInfo) {
			}
		});
		ThreadPoolBreakpointDownloader.getInstance().downloadBigFile(address01, "", "",  new ThreadPoolBreakpointDownloader.DownloadProgressCallback(){
			public void onStart() {
				downloadPB02.setMax(100);
			}

			public void onProgress(int progress) {
				downloadPB02.setProgress(progress);
				percentTV02.setText(progress+"%");
			}

			public void onFinish(File path) {
			}

			public void onFail(String errorInfo) {
			}
		});
		ThreadPoolBreakpointDownloader.getInstance().downloadBigFile(address03, "", "", new ThreadPoolBreakpointDownloader.DownloadProgressCallback() {
			public void onStart() {
				downloadPB03.setMax(100);
			}

			public void onProgress(int progress) {
				downloadPB03.setProgress(progress);
				percentTV03.setText(+progress+"%");
			}
			public void onFinish(File path) {

			}

			public void onFail(String errorInfo) {

			}
		});
    }


}