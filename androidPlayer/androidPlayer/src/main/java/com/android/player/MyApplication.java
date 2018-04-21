package com.android.player;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.android.player.media.IPlayerEngine;
import com.android.player.media.PlayerEngineImpl;

public class MyApplication extends Application {
	public static final String TAG = "MyApplication";

	private IPlayerEngine playerEngine;

	public IPlayerEngine getPlayerEngine() {
		if (null == playerEngine) {
			playerEngine = new PlayerEngineImpl();
		}
		return playerEngine;
	}

	private static MyApplication instance;

	@Override
	public void onCreate() {
		instance = this;
		super.onCreate();
	}

	public static MyApplication getInstance() {
		return instance;
	}

	/**
	 * Retrieves application's version number from the manifest
	 * 
	 * @return
	 */
	public String getVersion() {
		String version = "0.0.0";

		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return version;
	}
}
