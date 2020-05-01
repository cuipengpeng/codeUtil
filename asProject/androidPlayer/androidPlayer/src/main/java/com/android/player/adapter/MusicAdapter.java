package com.android.player.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.player.R;
import com.android.player.model.Audio;

public class MusicAdapter extends BaseAdapter {
	private Context mContext;
	private List<Audio> mMusicList;
	private List<Boolean> checkBoxesStatus;
	private Set<Integer> checkedBoxIds;

	public MusicAdapter(Context c, List<Audio> musicList) {
		this.mContext = c;
		this.mMusicList = musicList;
		
		initCheckBoxsStaus();
//		initApplication();
//		initAudioDao();
	}

	@Override
	public int getCount() {
		return mMusicList.size();
	}

	@Override
	public Object getItem(int position) {
		return mMusicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View
					.inflate(mContext, R.layout.audio_list_item, null);
			holder.index_tv = (TextView) convertView
					.findViewById(R.id.position_icon);
			holder.name_tv = (TextView) convertView.findViewById(R.id.line1);
			holder.play_btn = (ImageButton) convertView
					.findViewById(R.id.play_btn);
			holder.check_box = (CheckBox) convertView
					.findViewById(R.id.checkBox);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.check_box.setTag(position);
		holder.check_box.setChecked(checkBoxesStatus.get(position));
		holder.check_box
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						checkBoxesStatus.set((Integer) buttonView.getTag(),
								isChecked);
						// notifyDataSetChanged();
					}
				});
		holder.index_tv.setText(String.valueOf(position + 1) + ".");
		holder.name_tv.setText(mMusicList.get(position).getName());
//		this.setOnClickListener(position, convertView);
//		this.setOnLongClickListener(position, convertView);
//		this.initPlayerEngineView(position, holder);		
		return convertView;
	}

	public final class ViewHolder {
		private TextView index_tv;
		private TextView name_tv;
		private ImageButton play_btn;
		private CheckBox check_box;
	}
	
	private void initCheckBoxsStaus() {
		checkBoxesStatus = new ArrayList<Boolean>(mMusicList.size());
		for (int i = 0; i < mMusicList.size(); i++) {
			checkBoxesStatus.add(false);
		}
		checkedBoxIds = new HashSet<Integer>();
	}

}
