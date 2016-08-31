package com.excel.imagemanipulator;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Stack;

import static com.excel.appstvlauncher.secondgen.Constants.DIR_DIGITAL_SIGNAGE;

public class DigitalSignageHolder {
	
	Context context;
	Stack<String> main_stack = new Stack<String>();
	String TAG = "DigitalSignageHolder";
	
	public DigitalSignageHolder(){}
	
	public DigitalSignageHolder( Context context ){
		this.context = context;
	}
	
	/**
	 * 
	 * Reload all the Absolute paths of Digital Signage images into the Stack
	 * 
	 */
	public void reloadDigitalSignageHolder(){
		main_stack.removeAllElements();
		
		String path = Environment.getExternalStorageDirectory().toString() + File.separator + DIR_DIGITAL_SIGNAGE;
		Log.d( TAG, "Path: " + path );
		File f = new File( path );        
		File file[] = f.listFiles();
		if( file == null ){
			Log.e( TAG, "Probably, digital signage path does not exist --> "+path );
			return;
		}
		
		Log.d( TAG, "Size: "+ file.length );
		for ( int i = 0; i < file.length; i++ ){
			if( file[ i ].isDirectory() )
				continue;
			// Log.d( "Files", "FileName : " + file[ i ].getName() );
			main_stack.add( path + File.separator + file[ i ].getName() );
		}
	}
	
	public int getDigitalSignageCount(){
		return main_stack.size();
	}
	
	public String getNextDigitalSignage(){
		if( main_stack.size() == 0 )
			return null;
		
		return main_stack.pop();
	}
	
}
