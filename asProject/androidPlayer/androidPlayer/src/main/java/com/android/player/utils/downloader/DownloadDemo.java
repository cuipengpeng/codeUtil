package com.android.player.utils.downloader;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadDemo {

	public static void main(String[] args) throws Exception {
		URL url = new URL("http://192.168.1.240:8080/14.Web/android-sdk_r17-windows.zip");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(3000);
		
		if (conn.getResponseCode() == 200) {
			InputStream in = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream("F:/Download/android-sdk_r17-windows.zip");
			byte[] buffer = new byte[1024 * 100];
			int len;
			while ((len = in.read(buffer)) != -1)
				fos.write(buffer, 0, len);
			in.close();
			fos.close();
		}
		
		System.out.println("下载完成!");
	}

}
