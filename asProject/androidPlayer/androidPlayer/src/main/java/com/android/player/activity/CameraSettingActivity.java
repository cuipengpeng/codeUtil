package com.android.player.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;

import com.android.player.R;
import com.android.player.utils.CameraDebug;

@SuppressLint("NewApi")
public class CameraSettingActivity extends PreferenceActivity {
	private static final String TAG = "MyPreferenceActivity";
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if( CameraDebug.LOG )
			Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences); // n.b., deprecated because we're not using a fragment...

		final int cameraId = getIntent().getExtras().getInt("cameraId");
		if( CameraDebug.LOG )
			Log.d(TAG, "cameraId: " + cameraId);
		
		final boolean supports_auto_stabilise = getIntent().getExtras().getBoolean("supports_auto_stabilise");
		if( CameraDebug.LOG )
			Log.d(TAG, "supports_auto_stabilise: " + supports_auto_stabilise);

		if( !supports_auto_stabilise ) {
			Preference pref = (Preference)findPreference("preference_auto_stabilise");
			PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_category_camera_effects");
        	pg.removePreference(pref);
		}

		readFromIntent("color_effects", CameraPreview.getColorEffectPreferenceKey(), Camera.Parameters.EFFECT_NONE, "preference_category_camera_effects");
		readFromIntent("scene_modes", CameraPreview.getSceneModePreferenceKey(), Camera.Parameters.SCENE_MODE_AUTO, "preference_category_camera_effects");
		readFromIntent("white_balances", CameraPreview.getWhiteBalancePreferenceKey(), Camera.Parameters.WHITE_BALANCE_AUTO, "preference_category_camera_effects");
		readFromIntent("isos", CameraPreview.getISOPreferenceKey(), "auto", "preference_category_camera_effects");
		//readFromIntent("exposures", "preference_exposure", "0", "preference_category_camera_effects");

		final boolean supports_face_detection = getIntent().getExtras().getBoolean("supports_face_detection");
		if( CameraDebug.LOG )
			Log.d(TAG, "supports_face_detection: " + supports_face_detection);

		if( !supports_face_detection ) {
			Preference pref = (Preference)findPreference("preference_face_detection");
			PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_category_camera_effects");
        	pg.removePreference(pref);
		}

		final int [] preview_widths = getIntent().getExtras().getIntArray("preview_widths");
		final int [] preview_heights = getIntent().getExtras().getIntArray("preview_heights");
		final int [] video_widths = getIntent().getExtras().getIntArray("video_widths");
		final int [] video_heights = getIntent().getExtras().getIntArray("video_heights");

		final int [] widths = getIntent().getExtras().getIntArray("resolution_widths");
		final int [] heights = getIntent().getExtras().getIntArray("resolution_heights");
		if( widths != null && heights != null ) {
			CharSequence [] entries = new CharSequence[widths.length];
			CharSequence [] values = new CharSequence[widths.length];
			for(int i=0;i<widths.length;i++) {
				entries[i] = widths[i] + " x " + heights[i] + " " + CameraPreview.getAspectRatioMPString(widths[i], heights[i]);
				values[i] = widths[i] + " " + heights[i];
			}
			ListPreference lp = (ListPreference)findPreference("preference_resolution");
			lp.setEntries(entries);
			lp.setEntryValues(values);
			String resolution_preference_key = CameraPreview.getResolutionPreferenceKey(cameraId);
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			String resolution_value = sharedPreferences.getString(resolution_preference_key, "");
			if( CameraDebug.LOG )
				Log.d(TAG, "resolution_value: " + resolution_value);
			lp.setValue(resolution_value);
			// now set the key, so we save for the correct cameraId
			lp.setKey(resolution_preference_key);
		}
		else {
			Preference pref = (Preference)findPreference("preference_resolution");
			PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_screen_camera_quality");
        	pg.removePreference(pref);
		}

		{
			final int n_quality = 100;
			CharSequence [] entries = new CharSequence[n_quality];
			CharSequence [] values = new CharSequence[n_quality];
			for(int i=0;i<n_quality;i++) {
				entries[i] = "" + (i+1) + "%";
				values[i] = "" + (i+1);
			}
			ListPreference lp = (ListPreference)findPreference("preference_quality");
			lp.setEntries(entries);
			lp.setEntryValues(values);
		}

		final String [] video_quality = getIntent().getExtras().getStringArray("video_quality");
		final String [] video_quality_string = getIntent().getExtras().getStringArray("video_quality_string");
		if( video_quality != null && video_quality_string != null ) {
			CharSequence [] entries = new CharSequence[video_quality.length];
			CharSequence [] values = new CharSequence[video_quality.length];
			for(int i=0;i<video_quality.length;i++) {
				entries[i] = video_quality_string[i];
				values[i] = video_quality[i];
			}
			ListPreference lp = (ListPreference)findPreference("preference_video_quality");
			lp.setEntries(entries);
			lp.setEntryValues(values);
			String video_quality_preference_key = CameraPreview.getVideoQualityPreferenceKey(cameraId);
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			String video_quality_value = sharedPreferences.getString(video_quality_preference_key, "");
			if( CameraDebug.LOG )
				Log.d(TAG, "video_quality_value: " + video_quality_value);
			lp.setValue(video_quality_value);
			// now set the key, so we save for the correct cameraId
			lp.setKey(video_quality_preference_key);
		}
		else {
			Preference pref = (Preference)findPreference("preference_video_quality");
			PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_screen_camera_quality");
        	pg.removePreference(pref);
		}

		final boolean supports_force_video_4k = getIntent().getExtras().getBoolean("supports_force_video_4k");
		if( CameraDebug.LOG )
			Log.d(TAG, "supports_force_video_4k: " + supports_force_video_4k);
		if( !supports_force_video_4k || video_quality == null || video_quality_string == null ) {
			Preference pref = (Preference)findPreference("preference_force_video_4k");
			PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_screen_camera_quality");
        	pg.removePreference(pref);
		}

//        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 ) {
//        	// Camera.enableShutterSound requires JELLY_BEAN_MR1 or greater
//        	CheckBoxPreference cbp = (CheckBoxPreference)findPreference("preference_shutter_sound");
//        	PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_screen_camera_controls_more");
//        	pg.removePreference(cbp);
//        }

        {
            final Preference pref = (Preference) findPreference("preference_online_help");
            pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                	if( pref.getKey().equals("preference_online_help") ) {
                		if( CameraDebug.LOG )
                			Log.d(TAG, "user clicked online help");
            	        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://opencamera.sourceforge.net/"));
            	        startActivity(browserIntent);
                		return false;
                	}
                	return false;
                }
            });
        }

        {
        	EditTextPreference edit = (EditTextPreference)findPreference("preference_save_location");
        	InputFilter filter = new InputFilter() { 
        		// whilst Android seems to allow any characters on internal memory, SD cards are typically formatted with FAT32
        		String disallowed = "|\\?*<\":>";
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) { 
                    for(int i=start;i<end;i++) { 
                    	if( disallowed.indexOf( source.charAt(i) ) != -1 ) {
                            return ""; 
                    	}
                    } 
                    return null; 
                }
        	}; 
        	edit.getEditText().setFilters(new InputFilter[]{filter});         	
        }

        {
            final Preference pref = (Preference) findPreference("preference_donate");
            pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                	if( pref.getKey().equals("preference_donate") ) {
                		if( CameraDebug.LOG )
                			Log.d(TAG, "user clicked to donate");
            	        /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.getDonateMarketLink()));
            	        try {
            	        	startActivity(browserIntent);
            	        }
            			catch(ActivityNotFoundException e) {
            				// needed in case market:// not supported
            				if( MyDebug.LOG )
            					Log.d(TAG, "can't launch market:// intent");
                	        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.getDonateLink()));
            	        	startActivity(browserIntent);
            			}*/
            	        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CameraActivity.getDonateLink()));
        	        	startActivity(browserIntent);
                		return false;
                	}
                	return false;
                }
            });
        }

        {
            final Preference pref = (Preference) findPreference("preference_about");
            pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                	if( pref.getKey().equals("preference_about") ) {
                		if( CameraDebug.LOG )
                			Log.d(TAG, "user clicked about");
            	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CameraSettingActivity.this);
                        alertDialog.setTitle("About");
                        final StringBuilder about_string = new StringBuilder();
                        String version = "UNKNOWN_VERSION";
						try {
	                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
	                        version = pInfo.versionName;
						}
						catch(NameNotFoundException e) {
	                		if( CameraDebug.LOG )
	                			Log.d(TAG, "NameNotFoundException exception trying to get version number");
							e.printStackTrace();
						}
                        about_string.append("Open Camera v");
                        about_string.append(version);
                        about_string.append("\n(c) 2013-2014 Mark Harman");
                        about_string.append("\nReleased under the GPL v3 or later");
                        about_string.append("\nAndroid API version: ");
                        about_string.append(Build.VERSION.SDK_INT);
                        about_string.append("\nDevice manufacturer: ");
                        about_string.append(Build.MANUFACTURER);
                        about_string.append("\nDevice model: ");
                        about_string.append(Build.MODEL);
                        about_string.append("\nDevice code-name: ");
                        about_string.append(Build.HARDWARE);
                        about_string.append("\nDevice variant: ");
                        about_string.append(Build.DEVICE);
                        {
                    		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                            about_string.append("\nStandard max heap? (MB): ");
                            about_string.append(activityManager.getMemoryClass());
                            about_string.append("\nLarge max heap? (MB): ");
                            about_string.append(activityManager.getLargeMemoryClass());
                        }
                        {
                            Point display_size = new Point();
                            Display display = CameraSettingActivity.this.getWindowManager().getDefaultDisplay();
                            display.getSize(display_size);
                            about_string.append("\nDisplay size: ");
                            about_string.append(display_size.x);
                            about_string.append("x");
                            about_string.append(display_size.y);
                        }
                        about_string.append("\nCurrent camera ID: ");
                        about_string.append(cameraId);
                        if( preview_widths != null && preview_heights != null ) {
                            about_string.append("\nPreview resolutions: ");
                			for(int i=0;i<preview_widths.length;i++) {
                				if( i > 0 ) {
                    				about_string.append(", ");
                				}
                				about_string.append(preview_widths[i]);
                				about_string.append("x");
                				about_string.append(preview_heights[i]);
                			}
                        }
                        if( widths != null && heights != null ) {
                            about_string.append("\nPhoto resolutions: ");
                			for(int i=0;i<widths.length;i++) {
                				if( i > 0 ) {
                    				about_string.append(", ");
                				}
                				about_string.append(widths[i]);
                				about_string.append("x");
                				about_string.append(heights[i]);
                			}
                        }
                        if( video_quality != null ) {
                            about_string.append("\nVideo quality: ");
                			for(int i=0;i<video_quality.length;i++) {
                				if( i > 0 ) {
                    				about_string.append(", ");
                				}
                				about_string.append(video_quality[i]);
                			}
                        }
                        if( video_widths != null && video_heights != null ) {
                            about_string.append("\nVideo resolutions: ");
                			for(int i=0;i<video_widths.length;i++) {
                				if( i > 0 ) {
                    				about_string.append(", ");
                				}
                				about_string.append(video_widths[i]);
                				about_string.append("x");
                				about_string.append(video_heights[i]);
                			}
                        }
                        about_string.append("\nAuto-stabilise?: ");
                        about_string.append(getString(supports_auto_stabilise ? R.string.about_available : R.string.about_not_available));
                        about_string.append("\nFace detection?: ");
                        about_string.append(getString(supports_face_detection ? R.string.about_available : R.string.about_not_available));
                        about_string.append("\nFlash modes: ");
                		String [] flash_values = getIntent().getExtras().getStringArray("flash_values");
                		if( flash_values != null && flash_values.length > 0 ) {
                			for(int i=0;i<flash_values.length;i++) {
                				if( i > 0 ) {
                    				about_string.append(", ");
                				}
                				about_string.append(flash_values[i]);
                			}
                		}
                		else {
                            about_string.append("None");
                		}
                        about_string.append("\nFocus modes: ");
                		String [] focus_values = getIntent().getExtras().getStringArray("focus_values");
                		if( focus_values != null && focus_values.length > 0 ) {
                			for(int i=0;i<focus_values.length;i++) {
                				if( i > 0 ) {
                    				about_string.append(", ");
                				}
                				about_string.append(focus_values[i]);
                			}
                		}
                		else {
                            about_string.append("None");
                		}
                        about_string.append("\nColor effects: ");
                		String [] color_effects_values = getIntent().getExtras().getStringArray("color_effects");
                		if( color_effects_values != null && color_effects_values.length > 0 ) {
                			for(int i=0;i<color_effects_values.length;i++) {
                				if( i > 0 ) {
                    				about_string.append(", ");
                				}
                				about_string.append(color_effects_values[i]);
                			}
                		}
                		else {
                            about_string.append("None");
                		}
                        about_string.append("\nScene modes: ");
                		String [] scene_modes_values = getIntent().getExtras().getStringArray("scene_modes");
                		if( scene_modes_values != null && scene_modes_values.length > 0 ) {
                			for(int i=0;i<scene_modes_values.length;i++) {
                				if( i > 0 ) {
                    				about_string.append(", ");
                				}
                				about_string.append(scene_modes_values[i]);
                			}
                		}
                		else {
                            about_string.append("None");
                		}
                        about_string.append("\nWhite balances: ");
                		String [] white_balances_values = getIntent().getExtras().getStringArray("white_balances");
                		if( white_balances_values != null && white_balances_values.length > 0 ) {
                			for(int i=0;i<white_balances_values.length;i++) {
                				if( i > 0 ) {
                    				about_string.append(", ");
                				}
                				about_string.append(white_balances_values[i]);
                			}
                		}
                		else {
                            about_string.append("None");
                		}
                        about_string.append("\nISOs: ");
                		String [] isos = getIntent().getExtras().getStringArray("isos");
                		if( isos != null && isos.length > 0 ) {
                			for(int i=0;i<isos.length;i++) {
                				if( i > 0 ) {
                    				about_string.append(", ");
                				}
                				about_string.append(isos[i]);
                			}
                		}
                		else {
                            about_string.append("None");
                		}
                		String iso_key = getIntent().getExtras().getString("iso_key");
                		if( iso_key != null ) {
                			about_string.append("\nISO key: " + iso_key);
                		}

                		about_string.append("\nParameters: ");
                		String parameters_string = getIntent().getExtras().getString("parameters_string");
                		if( parameters_string != null ) {
                			about_string.append(parameters_string);
                		}
                		else {
                            about_string.append("None");
                		}
                        
                        alertDialog.setMessage(about_string);
                        alertDialog.setPositiveButton(R.string.about_ok, null);
                        alertDialog.setNegativeButton(R.string.about_copy_to_clipboard, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                        		if( CameraDebug.LOG )
                        			Log.d(TAG, "user clicked copy to clipboard");
							 	ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
							 	ClipData clip = ClipData.newPlainText("OpenCamera About", about_string);
							 	clipboard.setPrimaryClip(clip);
                            }
                        });
                        alertDialog.show();
                		return false;
                	}
                	return false;
                }
            });
        }
	}
	
	private void readFromIntent(String intent_key, String preference_key, String default_value, String preference_category_key) {
		String [] values = getIntent().getExtras().getStringArray(intent_key);
		if( values != null && values.length > 0 ) {
			if( CameraDebug.LOG ) {
				Log.d(TAG, intent_key + " values:");
				for(int i=0;i<values.length;i++) {
					Log.d(TAG, values[i]);
				}
			}
			@SuppressWarnings("deprecation")
			ListPreference lp = (ListPreference)findPreference(preference_key);
			lp.setEntries(values);
			lp.setEntryValues(values);
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			String value = sharedPreferences.getString(preference_key, default_value);
			if( CameraDebug.LOG )
				Log.d(TAG, "    value: " + values);
			lp.setValue(value);
		}
		else {
			if( CameraDebug.LOG )
				Log.d(TAG, "remove preference " + preference_key + " from category " + preference_category_key);
			@SuppressWarnings("deprecation")
			ListPreference lp = (ListPreference)findPreference(preference_key);
        	@SuppressWarnings("deprecation")
        	PreferenceGroup pg = (PreferenceGroup)this.findPreference(preference_category_key);
        	pg.removePreference(lp);
		}
	}
}
