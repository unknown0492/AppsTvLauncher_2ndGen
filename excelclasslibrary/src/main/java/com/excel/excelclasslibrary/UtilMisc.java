package com.excel.excelclasslibrary;

import android.content.Context;
import android.content.Intent;

public class UtilMisc {
	
	/**
	 * 
	 * @param Start an application using its package name
	 * @param Context application context
	 * @param String The name of the package  
	 * @return true if no exception is raised, false otherwise with stack trace of the exception occurred in the Logs
	 * 
	 */
	
	public static boolean startApplicationUsingPackageName( Context context, String package_name ){
		try{
			Intent app_intent = context.getPackageManager().getLaunchIntentForPackage( package_name );
			context.startActivity( app_intent );
			return true;
		}
		catch( Exception e ){
			e.printStackTrace();
		}
		return false;
	}
	
}
