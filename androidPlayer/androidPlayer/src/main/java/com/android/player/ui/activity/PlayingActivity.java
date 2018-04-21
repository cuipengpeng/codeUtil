package com.android.player.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.player.MyApplication;
import com.android.player.R;
import com.android.player.media.PlayerEngineImpl.PlaybackMode;
import com.android.player.utils.Constants;

public class PlayingActivity extends Activity {
	private MyApplication myApplication;
	private ImageButton back_btn;
	private Intent intent;
	private TextView playback_audio_name_tv;

	private ImageButton playback_mode_btn;
	private TextView playback_current_time_tv;
	private TextView playback_total_time_tv;
	private SeekBar seek_bar;

	private ImageButton playback_pre_btn;

	private ImageButton playback_next_btn;

	private ImageButton playback_toggle_btn;

	private Handler seek_bar_handler = new Handler();

	private Runnable refresh = new Runnable() {
		public void run() {
			int currently_Progress = seek_bar.getProgress() + 1000;// 加1秒
			seek_bar.setProgress(currently_Progress);
			playback_current_time_tv.setText(myApplication.getPlayerEngine()
					.getCurrentTime());// 每1000m刷新歌曲音轨
			seek_bar_handler.postDelayed(refresh, 1000);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = this.getIntent();
		if (null == myApplication) {
			myApplication = MyApplication.getInstance();
		}
		setContentView(R.layout.playback_activity);
		back_btn = (ImageButton) findViewById(R.id.playback_list);
		back_btn.setOnTouchListener(back_btn_listener);

		playback_audio_name_tv = (TextView) findViewById(R.id.playback_audio_name);
		playback_audio_name_tv.setText(myApplication.getPlayerEngine()
				.getPlayingPath().split("/")[myApplication.getPlayerEngine()
				.getPlayingPath().split("/").length - 1]);

		playback_mode_btn = (ImageButton) findViewById(R.id.playback_mode);
		playback_mode_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				myApplication.getPlayerEngine().setPlaybackMode(
						PlaybackMode.SHUFFLE);
			}
		});

		playback_current_time_tv = (TextView) findViewById(R.id.playback_current_time);
		playback_total_time_tv = (TextView) findViewById(R.id.playback_total_time);

		if (myApplication.getPlayerEngine().getPlayingPath() != ""
				&& null != myApplication.getPlayerEngine().getPlayingPath()) {
			playback_current_time_tv.setText(myApplication.getPlayerEngine()
					.getCurrentTime());
			playback_total_time_tv.setText(myApplication.getPlayerEngine()
					.getDurationTime());
		}

		seek_bar = (SeekBar) findViewById(R.id.playback_seeker);
		seek_bar.setOnSeekBarChangeListener(seekbarListener);

		playback_pre_btn = (ImageButton) findViewById(R.id.playback_pre);
		playback_pre_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				myApplication.getPlayerEngine().previous();

			}
		});
		playback_next_btn = (ImageButton) findViewById(R.id.playback_next);
		playback_next_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				myApplication.getPlayerEngine().next();

			}
		});

		playback_toggle_btn = (ImageButton) findViewById(R.id.playback_toggle);

		playback_toggle_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				play();

			}
		});
		if (myApplication.getPlayerEngine().isPlaying()) {
			seek_bar.setMax(Integer.valueOf(myApplication.getPlayerEngine()
					.getDuration()));
			seek_bar_handler.postDelayed(refresh, 1000);
			playback_toggle_btn
					.setBackgroundResource(R.drawable.play_button_default);
		} else {
			playback_toggle_btn
					.setBackgroundResource(R.drawable.pause_button_default);
		}
	}

	OnSeekBarChangeListener seekbarListener = new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				if (myApplication.getPlayerEngine().getPlayingPath() != ""
						&& null != myApplication.getPlayerEngine()
								.getPlayingPath()) {
					seek_bar_handler.removeCallbacks(refresh);
					playback_current_time_tv.setText(myApplication
							.getPlayerEngine().getCurrentTime());
				}
			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (myApplication.getPlayerEngine().getPlayingPath() != ""
					&& null != myApplication.getPlayerEngine().getPlayingPath()) {
				myApplication.getPlayerEngine().forward(seekBar.getProgress());
				seek_bar_handler.postDelayed(refresh, 1000);
			} else {
				seek_bar.setProgress(0);
			}
		}
	};

	private OnTouchListener back_btn_listener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {

			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				setResult(Constants.MENU_TO_PLAYER_RESULT_CODE, intent);
				finish();
			}
			return false;
		}
	};

	@Override
	protected void onResume() {
		if (myApplication.getPlayerEngine().getPlayingPath() != ""
				&& null != myApplication.getPlayerEngine().getPlayingPath()) {
			seek_bar.setProgress(myApplication.getPlayerEngine()
					.getCurrentPosition());
		}
		super.onResume();
	}

	private void play() {
		if (myApplication.getPlayerEngine().isPlaying()) {
			myApplication.getPlayerEngine().pause();
			seek_bar_handler.removeCallbacks(refresh);
			playback_toggle_btn
					.setBackgroundResource(R.drawable.play_button_default);
		} else if (myApplication.getPlayerEngine().isPause()) {
			myApplication.getPlayerEngine().start();
			seek_bar_handler.postDelayed(refresh, 1000);
			playback_toggle_btn
					.setBackgroundResource(R.drawable.pause_button_default);
		}

	}
}
