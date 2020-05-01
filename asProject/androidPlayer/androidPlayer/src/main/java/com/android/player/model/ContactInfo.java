package com.android.player.model;

import java.io.Serializable;

/** �绰�� */
public class ContactInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public ContactInfo(){}
	
	public ContactInfo(String index, String pinyin,String name, String phoneNumber,
			String contactId, Boolean checked, String sort_key) {
		super();
		this.setIndex(index);
		this.pinyin = pinyin;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.contactId = contactId;
		this.checked = checked;
		this.sort_key = sort_key;
	}

	/** ������ĸ */
	private String index;
	/** ��ȡƴ����������ϵ�˱Ƚ�����*/
	private String pinyin;
	/** ���� */
	private String name;
	private String phoneNumber;
	private String contactId;
	private Boolean checked;
	private String sort_key;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public String getSort_key() {
		return sort_key;
	}

	public void setSort_key(String sort_key) {
		this.sort_key = sort_key;
	}


	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	@Override
	public String toString() {
		return "NoteBookItem [ name=" + name + ", phoneNumber=" + phoneNumber
				+ "]";
	}
}
