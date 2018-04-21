package com.android.player.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.player.R;
import com.android.player.domain.ContactInfo;
import com.android.player.ui.adapter.ContactListAdapter;
import com.android.player.utils.Chinese2PinyinUtil;


public class ContactListActivity extends Activity implements OnClickListener{

	private ContentResolver mContentResolver;
	private ListView mContactListView;
	private TextView mNoResultTextView;
	private ContactListAdapter mAdapter;
	private ImageButton mSearchImageButton;
	private ImageButton mBackImageButton;
	private ImageButton mClearSearchTextImageButton;
	private EditText mSearchEditText;
	private LinearLayout mSearchBarLinearLayout;
	private RelativeLayout mTitleRelativeLayout;

	/** ��ĸ����� */
	private LinearLayout layoutIndex;
	private String[] mIndexLetterStrArray = { "#", "A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "U",
			"V", "W", "X", "Y", "Z" };
	private TextView mLetterShowTextView;// �м���ʾ�ѵ������ĸ
	int singleLetterHeight;// ÿ����ĸ�ĸ߶�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list_activity);

		mNoResultTextView = (TextView) findViewById(R.id.tv_contactListActivity_noResult);
		mNoResultTextView.setVisibility(View.GONE);
		mTitleRelativeLayout = (RelativeLayout) findViewById(R.id.rl_contactListActivity_title);
		mTitleRelativeLayout.setVisibility(View.VISIBLE);
		mSearchBarLinearLayout =  (LinearLayout) findViewById(R.id.ll_contactListActivity_searchBar);
		mSearchBarLinearLayout.setVisibility(View.GONE); 
		mClearSearchTextImageButton = (ImageButton) findViewById(R.id.ib_contactListActivity_clearText);
		mClearSearchTextImageButton.setVisibility(View.INVISIBLE);
		mClearSearchTextImageButton.setOnClickListener(this);
		mSearchImageButton = (ImageButton) findViewById(R.id.ib_contactListActivity_search);
		mSearchImageButton.setOnClickListener(this);
		mBackImageButton = (ImageButton) findViewById(R.id.ib_contactListActivity_back);
		mBackImageButton.setOnClickListener(this); 
		mSearchEditText  = (EditText) findViewById(R.id.et_contactListActivity_search);
		mSearchEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mSearchEditText.getText().toString().length() > 0){
					mClearSearchTextImageButton.setVisibility(View.VISIBLE);
				}else {
					mClearSearchTextImageButton.setVisibility(View.INVISIBLE);
				}
				int searchCount =mAdapter.searchContact(mSearchEditText.getText().toString());
				if(searchCount > 0){
					showContactListView();
				}else {
					mNoResultTextView.setVisibility(View.VISIBLE);
					mContactListView.setVisibility(View.GONE);
				} 
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		layoutIndex = (LinearLayout) findViewById(R.id.ll_contact_list_index);
		layoutIndex.setBackgroundColor(Color.parseColor("#00ffffff"));

		mLetterShowTextView = (TextView) findViewById(R.id.tv);
		mLetterShowTextView.setVisibility(View.GONE);
		
		mContactListView = (ListView) findViewById(R.id.lv_contactListActivity_contactInfo);
		mAdapter = new ContactListAdapter(this, getContactList());
		mContactListView.setAdapter(mAdapter);
	}
	
	/**
	 * ��ȡ��ϵ������
	 */
	public List<ContactInfo> getContactList() {
		List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();

		ContactInfo contactInfo = new ContactInfo();

		mContentResolver = getContentResolver();
		Cursor cursor = mContentResolver
				.query(ContactsContract.Contacts.CONTENT_URI,
						new String[] { ContactsContract.Contacts._ID,
								ContactsContract.Contacts.DISPLAY_NAME,
								ContactsContract.Contacts.HAS_PHONE_NUMBER,
								"sort_key" }, null, null,
						"sort_key COLLATE LOCALIZED asc");

		String contactId = "";
		String name = "";
		String phoneNumber = "";
		String sort_key = "";
		String index = "";
		String pinyin = "";

		while (cursor.moveToNext()) {

			name = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			if (name != null) {
				pinyin = Chinese2PinyinUtil.converterToSpell(name);
				index = String.valueOf(Chinese2PinyinUtil
						.converterToFirstSpell(name).charAt(0));
				Log.e("", "@...name = " + name + "  pinyin = " + pinyin
						+ "   index = " + index);

				contactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));

				sort_key = cursor.getString(cursor.getColumnIndex("sort_key"));

				contactInfo = new ContactInfo(index, pinyin, name, phoneNumber,
						contactId, false, sort_key);
				contactInfoList.add(contactInfo);
			}
		}
		cursor.close();

		setPhoneNumber(contactInfoList);

		return contactInfoList;
	}

	/**
	 * ��ȡ��ϵ�˵绰����
	 */
	private void setPhoneNumber(List<ContactInfo> list_ShowData) {
		Cursor phones = mContentResolver
				.query(ContactsContract.Data.CONTENT_URI,
						new String[] { ContactsContract.Data.CONTACT_ID,
								ContactsContract.CommonDataKinds.Phone.NUMBER },
						Data.MIMETYPE
								+ "='"
								+ ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
								+ "'", null, null);

		String contactId = "";
		String phoneNumber = "";

		while (phones.moveToNext()) {
			contactId = phones.getString(phones
					.getColumnIndex(ContactsContract.Data.CONTACT_ID));

			for (ContactInfo showData : list_ShowData) {
				if (showData.getContactId().equals(contactId)) {
					phoneNumber = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					showData.setPhoneNumber(phoneNumber);
					break;
				}
			}
		}
		phones.close();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		drawIndex();

	}	
	
	private void drawIndex() {
		// ��onResume����ִ������Ĵ���û��Ӧ����ΪonResume����layoutIndex.getHeight=0
		// singleLetterHeight = layoutIndex.getHeight()/ mIndexLetterStrArray.length;
		Log.v("", "@...layoutIndex.getHeight() = " + layoutIndex.getHeight());

		/** ���������б� */
		singleLetterHeight = layoutIndex.getHeight()
				/ mIndexLetterStrArray.length;
		for (int i = 0; i < mIndexLetterStrArray.length; i++) {
			final TextView tv = new TextView(this);
			tv.setText(mIndexLetterStrArray[i]);
			tv.setTextSize(14);
			tv.setTextColor(Color.parseColor("#ff858c94"));
			tv.setTypeface(null, Typeface.BOLD);
			tv.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, singleLetterHeight));
			tv.setPadding(10, 0, 10, 0);
			layoutIndex.addView(tv);
		}

		/** ���ô����¼�������*/
		layoutIndex.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event)

			{
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					
					float y = event.getY();
					int letterPosition = (int) (y / singleLetterHeight);
					if (letterPosition > -1 && letterPosition < mIndexLetterStrArray.length) {// ����λ���ں��?Χ�ڣ���ֹԽ��
						String letter = mIndexLetterStrArray[letterPosition];
						layoutIndex.setBackgroundResource(R.drawable.scrollbar_bg);
						
						int positionAtListView = mAdapter.getPositionForSection(letter.charAt(0));
						if (positionAtListView != -1) {
							mLetterShowTextView.setVisibility(View.VISIBLE);
							mLetterShowTextView.setText(mIndexLetterStrArray[letterPosition]);
							if (mContactListView.getHeaderViewsCount() > 0) {// ��ֹListView�б�������������û�С�
								mContactListView.setSelection(positionAtListView + mContactListView.getHeaderViewsCount());
							} else {
								mContactListView.setSelection(positionAtListView);// ��������һ��
							}
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					layoutIndex.setBackgroundColor(Color.parseColor("#00ffffff"));
					mLetterShowTextView.setVisibility(View.GONE);
					break;
				}
				return true;
			}
		});
	}
	
	private void showContactListView() {
		mContactListView.setVisibility(View.VISIBLE);
		mNoResultTextView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_contactListActivity_search:
			mSearchBarLinearLayout.setVisibility(View.VISIBLE);
			mTitleRelativeLayout.setVisibility(View.GONE);
			//��ʾ�����
			mSearchEditText.requestFocus();
			InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm1.showSoftInput(mSearchEditText, InputMethodManager.SHOW_IMPLICIT);
//			if(!imm1.isActive()){
//			imm1.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
//			}
			break;
		case R.id.ib_contactListActivity_back:
			mTitleRelativeLayout.setVisibility(View.VISIBLE);
			mSearchBarLinearLayout.setVisibility(View.GONE);
			//���������
			InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm2.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);   
//			if(imm2.isActive()){
//			imm2.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
//			}
			break;
		case R.id.ib_contactListActivity_clearText:
			mSearchEditText.setText("");
			mClearSearchTextImageButton.setVisibility(View.INVISIBLE);
			showContactListView();
			break;
		}
	}
}
