package com.excel.excelclasslibrary;

import android.content.Context;
import android.content.SharedPreferences;

public class UtilSharedPreferences {
	
	// ----- Creating SharedPreference
	public static SharedPreferences createSharedPreference( Context ct, String name ){
		SharedPreferences spfs = ct.getSharedPreferences( name, Context.MODE_WORLD_READABLE );
		return spfs;
	}
	// ----- /Creating SharedPreference

	// ----- Editing SharedPreference
	public static void editSharedPreference( SharedPreferences spfs, String key, String value ){
		SharedPreferences.Editor spe = spfs.edit();
		spe.putString( key, value );
		spe.commit();
	}
	// ----- /Editing SharedPreference

	// ----- Retrieving From SharedPreference Starts Here
	public static Object getSharedPreference( SharedPreferences spfs, String name, String default_value ){
		Object value = spfs.getString( name, default_value );
		return value;
	}
	// ----- Accessing SharedPreference Ends Here
}
