package com.excel.configuration;

import static com.excel.appstvlauncher.secondgen.Constants.PATH_CONFIGURATION_FILE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;

import com.excel.customitems.CustomItems;
import com.excel.excelclasslibrary.UtilFile;

public class ConfigurationWriter {
	
	static ConfigurationWriter configurationWriter;
	final static String TAG = "ConfigurationWriter";
	
	String country, timezone, cms_ip, location, firmware_name, 
	is_reboot_scheduled, reboot_time, digital_signage_interval, weather_retry_interval, weather_refresh_interval,
	clock_weather_flip_interval, cms_sub_directory, protocol, hotspot_enabled, ssid, hotspot_password, room_no;
	String file_md5;
	static int KEY = 0;
	static int VALUE = 1;
	final static String COUNTRY = "country";
	final static String TIMEZONE = "timezone";
	final static String CMS_IP = "cms_ip";
	final static String LOCATION = "location";
	final static String FIRMWARE_NAME = "firmware_name";
	final static String IS_REBOOT_SCHEDULED = "is_reboot_scheduled";
	final static String REBOOT_TIME = "reboot_time";
	final static String DIGITAL_SIGNAGE_INTERVAL = "digital_signage_interval";
	final static String WEATHER_RETRY_INTERVAL = "weather_retry_interval";
	final static String WEATHER_REFRESH_INTERVAL = "weather_refresh_interval";
	final static String CLOCK_WEATHER_FLIP_INTERVAL = "clock_weather_flip_interval";
	final static String CMS_SUB_DIRECTORY = "cms_sub_directory";
	final static String PROTOCOL = "protocol";
	final static String HOTSPOT_ENABLED = "hotspot_enabled";
	final static String SSID = "ssid";
	final static String HOTSPOT_PASSWORD = "hotspot_password";
	final static String ROOM_NO = "room_no";
	
	public static ConfigurationWriter getInstance( Context context ){
		if( configurationWriter == null ){
			configurationWriter = new ConfigurationWriter();
			/**
			 * 
			 * 1. Check if configuration file exist on sdcard
			 * 2. If does not exist, then show error and do not proceed 
			 * 
			 */
			
			File configuration = new File( configurationWriter.getConfigurationFilePath() );
			
			// Step-1
			if( ! configuration.exists() ){
				// Step-2
				CustomItems.showCustomToast( context, "error", "Configuration file does not exist. Reboot the box and try again !", 6000 );
				return null;
			}
			
		}
		return configurationWriter;
	}
	
	private String getConfigurationFilePath(){
		String configuration_file_path = Environment.getExternalStorageDirectory() + File.separator + PATH_CONFIGURATION_FILE;
		// Log.d( TAG, configuration_file_path );
		return configuration_file_path;
	}
	
	public boolean setRoomNumber( String room_no ){
		return amendConfigurationFile( ROOM_NO, room_no );
	}
	
	private boolean amendConfigurationFile( String key, String value ){
		FileInputStream fis = null;
		BufferedReader reader = null;
		StringBuilder configuration_data;
		try{
			fis = new FileInputStream( new File( configurationWriter.getConfigurationFilePath() ) );
			reader = new BufferedReader( new InputStreamReader( fis ) );
		    configuration_data = new StringBuilder();
		    String line = null;
		    
		    while ( ( line = reader.readLine() ) != null ){
		    	
		    	if( line.contains( key ) ){
		    		line = key + "=" + value;
		    	}
		    	
		    	configuration_data.append( line ).append( "\n" );
		    }
		    
		    UtilFile.saveDataToFile( new File( configurationWriter.getConfigurationFilePath() ), configuration_data.toString().trim() );
		    
		    reader.close();
		    fis.close();
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
}
