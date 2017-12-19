package com.excel.imagemanipulator;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.excel.configuration.DigitalSignageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Stack;

import static com.excel.configuration.Constants.DIR_DIGITAL_SIGNAGE;

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

		JSONArray jsonArray = null;
		JSONObject jsonObject = null;
		WallpaperInfo wpi[] = null;
		try {
			jsonArray = new JSONArray( DigitalSignageManager.getDigitalSignageConfig() );
			wpi = new WallpaperInfo[ jsonArray.length() ];
			int k = 0;
			for( int i = jsonArray.length()-1 ; i >=0 ; i-- ){
				jsonObject = jsonArray.getJSONObject( i );
				wpi[ k ] = new WallpaperInfo( jsonObject.getString( "file_name" ),
						jsonObject.getString( "file_path" ),
						jsonObject.getString( "md5" ),
						jsonObject.getInt( "sequence" ) );
				k++;
				// Log.d( TAG, wpi[ i ].getFileName() + " - " + wpi[ i ].getFilePath() + " - " +wpi[ i ].getMD5() );
			}

			for ( int i = 0; i < wpi.length; i++ ){
				File ff = new File( path + File.separator + wpi[ i ].getFileName() );
				if( ff.isDirectory() )
					continue;
				// Log.d( "Files", "FileName : " + file[ i ].getName() );
				//main_stack.add( path + File.separator + file[ i ].getName() );
				main_stack.add( ff.getAbsolutePath() );
			}


		} catch ( JSONException e ) {
			e.printStackTrace();


			for ( int i = 0; i < file.length; i++ ){
				File ff = new File( path + File.separator + file[ i ].getName() );
				if( ff.isDirectory() )
					continue;
				// Log.d( "Files", "FileName : " + file[ i ].getName() );
				//main_stack.add( path + File.separator + file[ i ].getName() );
				main_stack.add( path + File.separator + file[ i ].getName() );
			}

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

	class WallpaperInfo{
		private String file_name;
		private String file_path;
		private String md5;
		private int sequence;

		public WallpaperInfo( String file_name, String file_path, String md5, int sequence ){
			this.setFileName(file_name);
			this.setFilePath(file_path);
			this.setMD5(md5);
			this.setSequence(sequence);
		}


		public String getFileName() {
			return file_name;
		}

		public void setFileName(String file_name) {
			this.file_name = file_name;
		}

		public String getFilePath() {
			return file_path;
		}

		public void setFilePath(String file_path) {
			this.file_path = file_path;
		}

		public String getMD5() {
			return md5;
		}

		public void setMD5(String md5) {
			this.md5 = md5;
		}

		public int getSequence() {
			return sequence;
		}

		public void setSequence(int sequence) {
			this.sequence = sequence;
		}
	}
	
}
