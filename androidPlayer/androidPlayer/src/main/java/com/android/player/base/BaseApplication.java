package com.android.player.base;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.android.player.media.IPlayerEngine;
import com.android.player.media.PlayerEngineImpl;

public class BaseApplication extends Application {
	public static final String TAG = "BaseApplication";

	private IPlayerEngine playerEngine;

	public IPlayerEngine getPlayerEngine() {
		if (null == playerEngine) {
			playerEngine = new PlayerEngineImpl();
		}
		return playerEngine;
	}

	private static BaseApplication instance;

	@Override
	public void onCreate() {
		instance = this;
		super.onCreate();
	}

	public static BaseApplication getInstance() {
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
