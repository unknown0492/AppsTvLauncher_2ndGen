package com.excel.appstvlauncher.secondgen;

public class Constants {
	final static long TEN_SECONDS_MILLIS = 10 * 1000;
	final static long TEN_SECONDS = 10;
	final static long TEN_MINUTES_MILLIS = 10 * 60 * 1000;
	final static long TEN_MINUTES_SECONDS = 10 * 60;
    
	final static long LAUNCHER_IDLE_TIMEOUT_MILLIS = TEN_MINUTES_MILLIS;
    final static long LAUNCHER_IDLE_TIMEOUT_SECONDS = TEN_MINUTES_SECONDS;
    
    // Read the following values from the file configuration.txt , this is only for test purposes
    public final static int DIGITAL_SIGNAGE_SWITCH_INTERVAL_MILLIS = 10000;

	public static final String WELCOME_SCREEN_SHOWN = "welcome_screen_shown";

	public static String PERMISSION_SPFS = "permission";

	public static String IS_PERMISSION_GRANTED = "is_permission_granted";
	public static String PERMISSION_GRANTED_YES = "yes";
	public static String PERMISSION_GRANTED_NO = "no";

	public static String IS_BOX_BOOTUP_TIME_UPDATED = "box_bootup_updated";
    
}
