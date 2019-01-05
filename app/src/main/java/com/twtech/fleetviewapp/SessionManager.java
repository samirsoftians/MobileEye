package com.twtech.fleetviewapp;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

/**
 * Created by Deepali Shinde on 15/4/18.
 */

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	// Editor for Shared preferences
	Editor editor;
	// Context
	Context _context;
	// Shared pref mode
	int PRIVATE_MODE = 0;
	// Sharedpref file name
	private static final String PREF_NAME = "AndroidHivePref";
	private static final String IS_REGISTER = "IsRegistered";
	private static final String IS_LOGIN = "IsLoggedIn";
	public static final String KEY_NAME = " name";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PHONE = "phone";
	public static final String KEY_UNAME = "username";
	public static final String KEY_UPASS = "userpass";
	
	// Constructor
	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	/**
	 * Create login session
	 * */
	public void createRegisterSession(String name, String email,String phone){
		//Log.e("createRegisterSession ","method called");
		// Storing login value as TRUE
		editor.putBoolean(IS_REGISTER, true);
		editor.putString(KEY_NAME, name);
		editor.putString(KEY_EMAIL, email);
		editor.putString(KEY_PHONE, phone);
		editor.commit();
	}

	public void createLoginSession(String uname, String pass){
		//Log.e("createLoginSession ","method called");
		editor.putBoolean(IS_LOGIN, true);
		editor.putString(KEY_UNAME, uname);
		editor.putString(KEY_UPASS, pass);
		editor.commit();
	}

	public void checkRegister(){
		//Log.e("Entered in ","checkRegister");
		if(!this.isRegistered()){

			//addAutoStartup();

			String uDB= Environment.getExternalStorageDirectory().getPath() + "/UserInfo.db";
			File fileDB=new File(uDB);
			if (fileDB.exists()){
				//Log.e("Database ","Exists");
				String uData=new DatabaseOperation(_context).retrieveUserDetailsData();
				//Log.e("User Data","" +uData);
				String allUData[]=uData.split("%");
				String nm=allUData[0];
				String email=allUData[1];
				String phone=allUData[2];
				String address=allUData[3];
				//Log.e("Data After ","after splitting "+nm+", "+email+", "+phone);
				Intent i = new Intent(_context, RegisterActivity.class);
				i.putExtra("name",nm);
				i.putExtra("email",email);
				i.putExtra("phone",phone);
				i.putExtra("address",address);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				_context.startActivity(i);
			}else {
				//Log.e("Entered in", " isRegistered");
				Intent i = new Intent(_context, RegisterActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				_context.startActivity(i);
			}

		//	addAutoStartup();

		}else {
			//Log.e("Entered in ","isRegistered else");
			checkLogin();
		}
	}

	public void checkLogin(){
		//Log.e("Entered in ","checkLogin");
		// Check login status
		if(!this.isLoggedIn()){
			//Log.e("Entered in ","isLoggedIn");
			Intent i = new Intent(_context, MyLoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			_context.startActivity(i);


		}else {
			//Log.e("Entered in ","checkLoginElse");
			Intent inMain=new Intent(_context,DashboardActivity.class);
			inMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			inMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			_context.startActivity(inMain);
		}
	}
	
	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(KEY_NAME, pref.getString(KEY_NAME, null));
		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
		user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));
		return user;
	}
	// Get Login State
	public boolean isRegistered(){
		return pref.getBoolean(IS_REGISTER, false);
	}
	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}
}
