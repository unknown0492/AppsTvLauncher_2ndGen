package com.excel.appstvlauncher.secondgen;

public class Constants {
	final static long TEN_SECONDS_MILLIS = 10 * 1000;
	final static long TEN_SECONDS = 10;
	final static long TEN_MINUTES_MILLIS = 10 * 60 * 1000;
	final static long TEN_MINUTES_SECONDS = 10 * 60;
    
	final static long LAUNCHER_IDLE_TIMEOUT_MILLIS = TEN_MINUTES_MILLIS;
    final static long LAUNCHER_IDLE_TIMEOUT_SECONDS = TEN_MINUTES_SECONDS;
    
    // Read the following values from the file configuration.txt , this is only for test purposes
    public final static String DIR_DIGITAL_SIGNAGE = "appstv_data/graphics/digital_signage";
    public final static int DIGITAL_SIGNAGE_SWITCH_INTERVAL_MILLIS = 10000;
    public final static String PATH_CONFIGURATION_FILE = "appstv_data/configuration";
    public final static String PATH_CONFIGURATION_FILE_SYSTEM = "/system/appstv_data/configuration";
    public final static String PATH_LAUNCHER_CONFIG_FILE = "appstv_data/launcher_config.json";
    public final static String PATH_LAUNCHER_CONFIG_FILE_SYSTEM = "/system/appstv_data/launcher_config.json";
    
}
