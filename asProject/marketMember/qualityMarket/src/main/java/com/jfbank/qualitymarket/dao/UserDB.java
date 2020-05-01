package com.jfbank.qualitymarket.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDB extends SQLiteOpenHelper {

	private static String NAME = "institutions.db";
	private static int VERSION = 12;

	
	public UserDB(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table institution(_id INTEGER primary key autoincrement ,instCode String,instName String,instAddr String,instLongitude String,instLatitude String,notEnabled String)");
		// 存储机构数据的表
		db.execSQL("create table updateTime(_id INTEGER primary key autoincrement,updateTime String)");
		// 存储更新列表时间的表
		/** 二期增加表 */
		db.execSQL("create table userLoginInfo(_id INTEGER primary key autoincrement,userName String)");
		// 存储登录过用户的信息
		db.execSQL("create table userPhone(_id INTEGER primary key autoincrement,contactName String,company String,mobilePhone String, homePhone String,companyPhone String,email String)"); // 存储联系人表
		db.execSQL("create table userMessage(_id INTEGER primary key autoincrement, userId String,headId String,userName String,level String, messageTime String,message String,isRead String,loginId String NOT NULL)"); // 存储用户消息表
		db.execSQL("create table userMessageId(_id INTEGER primary key autoincrement, userId String  NOT NULL UNIQUE)"); // 存储用户消息表
		/** 三期增加表 */
		db.execSQL("create table userCallLog(_id INTEGER primary key autoincrement,name String,number String,type String,duration String,time Long,lasttime Long)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			String sql = "DROP TABLE IF EXISTS  userMessageId";
			db.execSQL(sql);
			String sql1 = "DROP TABLE IF EXISTS  userMessage";
			db.execSQL(sql1);
			String sql2 = "DROP TABLE IF EXISTS  userPhone";
			db.execSQL(sql2);
			String sql3 = "DROP TABLE IF EXISTS  userLoginInfo";
			db.execSQL(sql3);
			String sql4 = "DROP TABLE IF EXISTS  updateTime";
			db.execSQL(sql4);
			String sql5 = "DROP TABLE IF EXISTS  institution";
			db.execSQL(sql5);
			String sql6 = "DROP TABLE IF EXISTS  userCallLog";
			db.execSQL(sql6);
			onCreate(db);
		}
	}

}
