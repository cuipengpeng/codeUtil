package com.android.player.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.player.R;
import com.android.player.model.ContactInfo;
import com.android.player.utils.Chinese2PinyinUtil;

/** ʵ��Filterable�ӿ�,��д���˹��� */
public class ContactListAdapter extends BaseAdapter implements SectionIndexer{
	private Context mContext;
	private ViewHolder holder;
	List<ContactInfo> mContactInfoList;
	private Map<String, ContactInfo> mSearchContactMap = new HashMap<String, ContactInfo>();
	private List<ContactInfo> mSearchResultContactList = new ArrayList<ContactInfo>();
	private boolean mIsSearching = false;
	public ContactListAdapter(Context context, List<ContactInfo> list) {
		this.mContext = context;
		this.mContactInfoList = list;	
		
		StringBuilder builder;
		for(ContactInfo contactInfo : mContactInfoList){
			builder = new StringBuilder();
			builder.append(Chinese2PinyinUtil.converterToSpell(contactInfo.getName())+", ");
			builder.append(Chinese2PinyinUtil.converterToFirstSpell(contactInfo.getName())+", ");
			builder.append(contactInfo.getName());
			mSearchContactMap.put(builder.toString(), contactInfo);
		}
		
	}
	
	public int searchContact(String userName){
		if(TextUtils.isEmpty(userName)){
			mIsSearching = false;
		}else {
			mIsSearching = true;
			
			mSearchResultContactList.clear();
			for(String searchKey : mSearchContactMap.keySet()){
				Log.e("", "@...searchKey = "+searchKey);
				if(searchKey.contains(userName)){
					mSearchResultContactList.add(((ContactInfo)mSearchContactMap.get(searchKey)));
				}
			}
			for(ContactInfo contactInfo : mSearchResultContactList){
				Log.e("", "@...userName = "+contactInfo.getName()+"   number = "+contactInfo.getPhoneNumber());
			}
			//��  mSearchResultContactList �е���ݽ������� 
			Collections.sort(mSearchResultContactList, new Comparator<ContactInfo>() {
				
				@Override
				public int compare(ContactInfo c1, ContactInfo c2) {
					String index1 = c1.getIndex();
					String index2 = c2.getIndex();
					return index1.compareTo(index2);
				}
			});
		}
		
		notifyDataSetChanged();
		
		return mSearchResultContactList.size();
	}

	@Override
	public int getCount() {
		if(mIsSearching){
			return mSearchResultContactList.size();
		}else {
			return mContactInfoList.size();
		}
	}

	@Override
	public Object getItem(int arg0) {
		if(mIsSearching){
			return mSearchResultContactList.get(arg0);
		}else {
			return mContactInfoList.get(arg0);
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.contact_list_activity_item, null);
				holder.contactName = (TextView) convertView.findViewById(R.id.tv1);
				holder.contactPhoneNumber = (TextView) convertView.findViewById(R.id.tv2);
				holder.phoneIconImageView = (ImageView) convertView.findViewById(R.id.iv_phone);
				holder.seperatorLine =convertView.findViewById(R.id.v_contact_list_activity_item_seperator_line); 
				holder.indexTextView = (TextView) convertView.findViewById(R.id.tv_index);
				holder.callNumberLinearLayout = (LinearLayout) convertView.findViewById(R.id.ll_contactListActivityItem_callPhone);
				holder.userInfoLinearLayout = (LinearLayout) convertView.findViewById(R.id.ll_contactListActivityItem_userInfo);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// �����
			final ContactInfo item ;
			if(mIsSearching){
				item = mSearchResultContactList.get(position);
			}else {
				item = mContactInfoList.get(position);
			}
			holder.contactName.setText(item.getName());
			holder.contactPhoneNumber.setText(item.getPhoneNumber());
			holder.callNumberLinearLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				    //�˴�Ӧ�öԵ绰���������֤����
				     Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+item.getPhoneNumber()));
				     mContext.startActivity(intent);
					Toast.makeText(mContext, "�Ⲧ�绰", Toast.LENGTH_SHORT).show();
				}
			});
			holder.userInfoLinearLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("content://com.android.contacts/contacts/"+ item.getContactId()));
					mContext.startActivity(intent);
				}
			});
			
			// ��һ��index
			String previewIndex;
			if(mIsSearching){
				previewIndex = (position - 1) >= 0 ? mSearchResultContactList.get(position - 1).getIndex() : " ";
			}else {
				previewIndex = (position - 1) >= 0 ? mContactInfoList.get(position - 1).getIndex() : " ";
			}
					
			// ��ǰҪ��ʾ��index
			String currentIndex = item.getIndex();
			/**
			 * �ж�����һ���Ƿ���ͬ
			 */
			if (!previewIndex.equals(currentIndex)) {
				holder.indexTextView.setVisibility(View.VISIBLE);
				holder.indexTextView.setText(currentIndex);
				holder.seperatorLine.setBackgroundColor(Color.parseColor("#ED7E00"));
			} else {
				holder.indexTextView.setVisibility(View.GONE);
				holder.seperatorLine.setBackgroundColor(Color.parseColor("#f0f0f0"));
			}
		} catch (OutOfMemoryError e) {
			Runtime.getRuntime().gc();
		} catch (Exception ex) {
			// handler.sendEmptyMessage(CommonMessage.PARSE_ERROR);
			ex.printStackTrace();
		}
		return convertView;
	}

	class ViewHolder {
		
		LinearLayout callNumberLinearLayout;
		LinearLayout userInfoLinearLayout;
		ImageView phoneIconImageView;
		TextView contactName;
		TextView contactPhoneNumber;
		View seperatorLine;
		/** ������ĸ */
		TextView indexTextView;
	}

	@Override
	public int getPositionForSection(int section) {
		if(section == '#'){
			return 0;
		}
		
		if(mIsSearching){
			for(int i=0;i<mSearchResultContactList.size();i++){
				ContactInfo contactInfo = mSearchResultContactList.get(i);
				if(String.valueOf(((char)section)).equalsIgnoreCase(contactInfo.getIndex())){
					return i;
				}
			}
		}else {
			for(int i=0;i<mContactInfoList.size();i++){
				ContactInfo contactInfo = mContactInfoList.get(i);
				if(String.valueOf(((char)section)).equalsIgnoreCase(contactInfo.getIndex())){
					return i;
				}
			}
		}
		
		//ListView�в����ڴ���ĸ��ͷ����ϵ��
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}
