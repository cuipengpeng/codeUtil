package com.jfbank.qualitymarket.dao;

import com.jfbank.qualitymarket.model.User;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class UserDao {

	private static UserDB userDB;

	/** 单例 **/
	private UserDB getInstance(Context context) {
		if (userDB == null) {
			userDB = new UserDB(context);
		}
		return userDB;
	}

	public UserDao(Context context) {
		getInstance(context);
	}

	public User getUserById(String id){
		SQLiteDatabase sqLiteDatabase = userDB.getReadableDatabase();
		sqLiteDatabase.execSQL("");
		sqLiteDatabase.close();
		return new User();
	}
	
	
//	/**
//	 * 查询本地数据库中的用户消息表
//	 */
//	public List<UserMessageBean> selectUserMessage(String serachuserId,
//			String loginId) {
//		SQLiteDatabase db = institutionsDB.getReadableDatabase();
//		Cursor cursor = db.rawQuery("select * from userMessage where userId=?",
//				new String[] { serachuserId });
//		List<UserMessageBean> umtList = new ArrayList<UserMessageBean>();
//		UserMessageBean uMessageBean;
//		while (cursor.moveToNext()) {
//			String headId = cursor.getString(cursor.getColumnIndex("headId"));
//			String id = cursor.getString(cursor.getColumnIndex("_id"));
//			String userName = cursor.getString(cursor
//					.getColumnIndex("userName"));
//			String level = cursor.getString(cursor.getColumnIndex("level"));
//			String messageTime = cursor.getString(cursor
//					.getColumnIndex("messageTime"));
//			String message = cursor.getString(cursor.getColumnIndex("message"));
//			String isRead = cursor.getString(cursor.getColumnIndex("isRead"));
//			String userId = cursor.getString(cursor.getColumnIndex("userId"));
//			String loginId_i = cursor.getString(cursor.getColumnIndex("loginId"));
//			uMessageBean = new UserMessageBean(userId, headId, userName, level,
//					messageTime, message, isRead, loginId_i,id);
//			umtList.add(uMessageBean);
//
//		}
//
//		db.close();
//		cursor.close();
//		return umtList;
//	}

	
}
