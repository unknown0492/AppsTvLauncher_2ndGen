package com.excel.configuration;

import android.os.Environment;
import android.util.Log;

import com.excel.excelclasslibrary.UtilShell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static com.excel.appstvlauncher.secondgen.Constants.PATH_CONFIGURATION_FILE;
import static com.excel.appstvlauncher.secondgen.Constants.PATH_CONFIGURATION_FILE_SYSTEM;

public class ConfigurationReader {
	String country, timezone, cms_ip, location, firmware_name, 
	is_reboot_scheduled, reboot_time, digital_signage_interval, weather_retry_interval, weather_refresh_interval,
	clock_weather_flip_interval, cms_sub_directory, protocol, hotspot_enabled, ssid, hotspot_password, room_no, web_service_url;
	
	String file_md5;
	static ConfigurationReader configurationReader;
	final static String TAG = "ConfigurationReader";
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
	
	public ConfigurationReader(){}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getCmsIp() {
		return cms_ip;
	}

	public void setCmsIp(String cms_ip) {
		this.cms_ip = cms_ip;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getFirmwareName() {
		String fname = UtilShell.executeShellCommandWithOp( "getprop ro.build.display.id" );
		return fname.trim();
	}

	public void setFirmwareName(String firmware_name) {
		this.firmware_name = firmware_name;
	}

	public String getIsRebootScheduled() {
		return is_reboot_scheduled;
	}

	public void setIsRebootScheduled(String is_reboot_scheduled) {
		this.is_reboot_scheduled = is_reboot_scheduled;
	}

	public String getRebootTime() {
		return reboot_time;
	}

	public void setRebootTime(String reboot_time) {
		this.reboot_time = reboot_time;
	}

	public String getDigitalSignageInterval() {
		return digital_signage_interval;
	}

	public void setDigitalSignageInterval(String digital_signage_interval) {
		this.digital_signage_interval = digital_signage_interval;
	}
	
	public String getWeatherRetryInterval() {
		return weather_retry_interval;
	}

	public void setWeatherRetryInterval(String weather_retry_interval) {
		this.weather_retry_interval = weather_retry_interval;
	}

	public String getWeatherRefreshInterval() {
		return weather_refresh_interval;
	}

	public void setWeatherRefreshInterval(String weather_refresh_interval) {
		this.weather_refresh_interval = weather_refresh_interval;
	}

	public String getClockWeatherFlipInterval() {
		return clock_weather_flip_interval;
	}

	public void setClockWeatherFlipInterval(String clock_weather_flip_interval) {
		this.clock_weather_flip_interval = clock_weather_flip_interval;
	}
	
	public String getCmsSubDirectory() {
		return cms_sub_directory;
	}

	public void setCmsSubDirectory( String cms_sub_directory ) {
		this.cms_sub_directory = cms_sub_directory;
	}
	
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol( String protocol ) {
		this.protocol = protocol;
	}
	
	public String getHotspotEnabled() {
		return hotspot_enabled;
	}

	public void setHotspotEnabled( String hotspot_enabled ) {
		this.hotspot_enabled = hotspot_enabled;
	}

	public String getSSID() {
		return ssid;
	}

	public void setSSID( String ssid ) {
		this.ssid = ssid;
	}

	public String getHotspotPassword() {
		return hotspot_password;
	}

	public void setHotspotPassword( String hotspot_password ) {
		this.hotspot_password = hotspot_password;
	}

	public String getRoomNo() {
		return room_no;
	}

	public void setRoomNo( String room_no ) {
		this.room_no = room_no;
	}
	
	public String getWebServiceUrl() {
		web_service_url = getProtocol() + "://" + getCmsIp() + File.separator + getCmsSubDirectory() + File.separator + "webservice.php";
		return web_service_url;
	}

	public void setWebServiceUrl( String web_service_url ) {
		this.web_service_url = web_service_url;
	}
	
	public static ConfigurationReader getInstance(){
		if( configurationReader == null ){
			configurationReader = new ConfigurationReader();
			/**
			 * 
			 * 1. Check if configuration file exist on sdcard
			 * 2. If does not exist, then read the configuration file from the /system 
			 * 
			 */
			String configuration_file_path = Environment.getExternalStorageDirectory() + File.separator + PATH_CONFIGURATION_FILE;
			Log.d( TAG, configuration_file_path );
			File configuration = new File( configuration_file_path );
			
			// Step-1
			if( ! configuration.exists() ){
				// Step-2
				configuration_file_path = PATH_CONFIGURATION_FILE_SYSTEM;
				configuration = new File( configuration_file_path );
			}
			String configuration_data = getConfigurationFileData( configuration );
			processConfigurationData( configuration_data );
			
			
		}
		return configurationReader;
	}
	
	public static ConfigurationReader reInstantiate(){
		configurationReader = null;
		return getInstance();
	}
	
	public static String getConfigurationFileData( File file ){
		FileInputStream fis = null;
		BufferedReader reader = null;
		StringBuilder configuration_data;
		try{
			fis = new FileInputStream( file );
			reader = new BufferedReader( new InputStreamReader( fis ) );
		    configuration_data = new StringBuilder();
		    String line = null;
		    
		    while ( ( line = reader.readLine() ) != null ){
		    	configuration_data.append( line ).append( "\n" );
		    }
		    reader.close();
		    fis.close();
		}
		catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}
		return configuration_data.toString();
	}
	
	private static void processConfigurationData( String data ){
		
		String lines[] = data.split( "\n" );
		String line[];
		for( int i = 0 ; i < lines.length ; i++ ){
			line = lines[ i ].split( "=" );
			if( line[ KEY ].equals( COUNTRY ) ){
				configurationReader.setCountry( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( TIMEZONE ) ){
				configurationReader.setTimezone( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( CMS_IP ) ){
				configurationReader.setCmsIp( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( LOCATION ) ){
				configurationReader.setLocation( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( FIRMWARE_NAME ) ){
				configurationReader.setFirmwareName( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( IS_REBOOT_SCHEDULED ) ){
				configurationReader.setIsRebootScheduled( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( REBOOT_TIME ) ){
				configurationReader.setRebootTime( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( DIGITAL_SIGNAGE_INTERVAL ) ){
				configurationReader.setDigitalSignageInterval( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( WEATHER_RETRY_INTERVAL ) ){
				configurationReader.setWeatherRetryInterval( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( WEATHER_REFRESH_INTERVAL ) ){
				configurationReader.setWeatherRefreshInterval( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( CLOCK_WEATHER_FLIP_INTERVAL ) ){
				configurationReader.setClockWeatherFlipInterval( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( CMS_SUB_DIRECTORY ) ){
				configurationReader.setCmsSubDirectory( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( PROTOCOL ) ){
				configurationReader.setProtocol( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( HOTSPOT_ENABLED ) ){
				configurationReader.setHotspotEnabled( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( SSID ) ){
				configurationReader.setSSID( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( HOTSPOT_PASSWORD ) ){
				configurationReader.setHotspotPassword( line[ VALUE ] );
			}
			else if( line[ KEY ].equals( ROOM_NO ) ){
				configurationReader.setRoomNo( line[ VALUE ] );
			}
		}
		
		
	}
}
