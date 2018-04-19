package com.jfbank.qualitymarket.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.util.Log;

import com.jfbank.qualitymarket.model.ContactBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 贵平
 * @category 读取通讯录联系人 和 sim卡联系人信息
 */
public class PhoneTools {
    public static ContactBean getPhoneContact(Context mContext, Intent data) {
        String[] contact = new String[2];
//得到ContentResolver对象
        ContentResolver cr = mContext.getContentResolver();
//取得电话本中开始一项的光标
        Cursor cursor = cr.query(data.getData(), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
//取得联系人姓名
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
//取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(Phone.CONTENT_URI, null,
                    Phone.CONTACT_ID + "=" + ContactId, null, null);
            if (phone != null && phone.moveToFirst()) {
                try {
                    String phoneNum = phone.getString(phone.getColumnIndex(Phone.NUMBER));
                    if (!TextUtils.isEmpty(phoneNum)) {
                        phoneNum = phoneNum.replace(" ", "");
                        phoneNum = phoneNum.replace("-", "");
                        phoneNum = phoneNum.replace("+86", "");
                    }
                    contact[1] = phoneNum;
                } catch (Exception e) {
                    Log.e("getPhoneContact", e.getMessage() + " ");
                }

            }
            phone.close();
            cursor.close();
        } else {
            return null;
        }
        ContactBean item = new ContactBean(contact[0], null, contact[1], null, null, null, IDCard.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        return item;
    }

    public static List<ContactBean> getPhoneContracts(Context mContext) {
        List<ContactBean> list = new ArrayList<ContactBean>();
        ContentResolver resolver = mContext.getContentResolver();
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, null, null, null, null);
        if (phoneCursor != null) {
            list.clear();
            while (phoneCursor.moveToNext()) {
                int nameIndex = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME); //
                String contactName = phoneCursor.getString(nameIndex);
                String mobilePhone = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER)); //
                if (TextUtils.isEmpty(mobilePhone)) {
                    continue;
                }
                ContactBean item = new ContactBean(contactName, null, mobilePhone, null, null, null, IDCard.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                list.add(item);
            }
            phoneCursor.close();
        }

        return list;
    }

    public static List<ContactBean> getContactPhone(Context context) {
        Cursor phone = context.getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);
        // ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        // + " = " + contactId
        if (phone == null) {
            return null;
        }
        List<ContactBean> list = new ArrayList<ContactBean>();
        list.clear();
        String homePhone = null, mobilePhone = null, workMobile = null;
        ContactBean contactBean;
        // 取得电话号码(可能存在多个号码)
        while (phone.moveToNext()) {
            String number = phone
                    .getString(phone
                            .getColumnIndex(Phone.NUMBER));
            Log.e("TAG", "number:" + number + "::" + number.contains("+86") + "::" + CommonUtils.isMobilePhoneVerify(number));
            number = number.replace(" ", "");
            number = number.replace("-", "");
            number = number.replace("+86", "");
            Log.e("TAG", "number:" + number);
            if (TextUtils.isEmpty(number) || !CommonUtils.isMobilePhoneVerify(number)) {
                continue;
            }
            int nameIndex = phone.getColumnIndex(Phone.DISPLAY_NAME); //
            String contactName = phone.getString(nameIndex);
            mobilePhone = number;
            contactBean = new ContactBean(contactName, null, mobilePhone,
                    homePhone, workMobile, null, IDCard.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            list.add(contactBean);
        }
        phone.close();
        return list;
    }

    public static List<ContactBean> getSimContracts(Context mContext) {
        List<ContactBean> list = new ArrayList<ContactBean>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, null, null, null, null);
        list.clear();
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                String contactName = phoneCursor.getString(phoneCursor
                        .getColumnIndex("name"));
                String mobilePhone = phoneCursor.getString(phoneCursor
                        .getColumnIndex("number"));
                mobilePhone = mobilePhone.replace(" ", "");
                mobilePhone = mobilePhone.replace("-", "");
                mobilePhone = mobilePhone.replace("+86", "");
                if (TextUtils.isEmpty(mobilePhone) || !CommonUtils.isMobilePhoneVerify(mobilePhone)) {
                    continue;
                }
                ContactBean item = new ContactBean(contactName, null,
                        mobilePhone, null, null, null, IDCard.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                list.add(item);
            }
            phoneCursor.close();
        }
        return list;
    }


}
