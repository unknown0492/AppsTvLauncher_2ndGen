package com.excel.perfecttime;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.excel.configuration.ConfigurationReader;
import com.excel.excelclasslibrary.UtilNetwork;
import com.excel.excelclasslibrary.UtilShell;
import com.excel.excelclasslibrary.UtilURL;

import java.util.Calendar;

public class PerfectTime {
	String hours, minutes, seconds, date, day, month, year, time_zone;
	Context context;
	long milliseconds;
	final static String TAG = "PerfectTime";
	int retryCounter = 1;
	boolean wasInternetTimeSyncSuccessful;
	int maxRetries = 3;
	
	public static PerfectTime pt = null;
	ConfigurationReader configurationReader;
	
	public int getMaxRetries() {
		return maxRetries;
	}
	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}
	
	
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public int getRetryCounter() {
		return retryCounter;
	}
	public void setRetryCounter(int retryCounter) {
		this.retryCounter = retryCounter;
	}
	public String getTimezone() {
		return time_zone;
	}
	public void setTimezone(String time_zone) {
		this.time_zone = time_zone;
	}

	
	public String getHours() {
		return hours;
	}
	public void setHours(String hours) {
		this.hours = (hours.length()==1)?"0"+hours:hours;
	}
	public String getMinutes() {
		return minutes;
	}
	public void setMinutes(String minutes) {
		this.minutes = (minutes.length()==1)?"0"+minutes:minutes;
	}
	public String getSeconds() {
		return seconds;
	}
	public void setSeconds(String seconds) {
		this.seconds = (seconds.length()==1)?"0"+seconds:seconds;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = (date.length()==1)?"0"+date:date;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = (day.length()==1)?"0"+day:day;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = (month.length()==1)?"0"+month:month;;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	
	public String getWasInternetTimeSyncSuccessful() {
		String d = UtilShell.executeShellCommandWithOp( "getprop wasInternetTimeSyncSuccessful" );
		return d.trim();
	}
	public void setWasInternetTimeSyncSuccessful( boolean wasInternetTimeSyncSuccessful ) {
		this.wasInternetTimeSyncSuccessful = wasInternetTimeSyncSuccessful;
		
		int no = (wasInternetTimeSyncSuccessful)?1:0;
		
		UtilShell.executeShellCommandWithOp( "setprop wasInternetTimeSyncSuccessful "+no );
	}
	
	public PerfectTime(){
	}
	
	public PerfectTime( Context context ) {
		this.context = context;
	}
	
	public static PerfectTime getInstance( Context context ){
		if( pt == null )
			return new PerfectTime( context );
		return pt;
	}
	
	public PerfectTime( String time_zone ) {
		this.time_zone = time_zone;
	}
	
	public String getMillisFromInternet(){
		configurationReader = ConfigurationReader.reInstantiate();
		String data = UtilNetwork.makeRequestForData( UtilURL.getWebserviceURL( configurationReader.getCmsIp() ), "POST", "what_do_you_want=get_millis&time_zone="+configurationReader.getTimezone()+"&mac_address="+UtilNetwork.getMacAddress( context ) );
		//if( data == null )
		//	return getMillisFromSystem();
		
		return data;
	}
	
	public long getMillisFromSystem(){
		return System.currentTimeMillis();
	}
	
	public void setTimeFromSystem(){
		long millis = getMillisFromSystem();
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( millis );	
		
		setHours( String.valueOf( cal.get( Calendar.HOUR_OF_DAY ) ) );
		setMinutes( String.valueOf( cal.get( Calendar.MINUTE ) ) );
		setSeconds( String.valueOf( cal.get( Calendar.SECOND ) ) );
		setDate( String.valueOf( cal.get( Calendar.DATE ) ) );
		setMonth( String.valueOf( cal.get( Calendar.MONTH ) + 1 ) );
		setDay( String.valueOf( cal.get( Calendar.DAY_OF_MONTH ) ) );
		setYear( String.valueOf( cal.get( Calendar.YEAR ) ) );
		
	}
	
	public void setTimeFromInternet( long milliseconds ){
		long millis = milliseconds;
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( millis );	
		
		setHours( String.valueOf( cal.get( Calendar.HOUR_OF_DAY ) ) );
		setMinutes( String.valueOf( cal.get( Calendar.MINUTE ) ) );
		setSeconds( String.valueOf( cal.get( Calendar.SECOND ) ) );
		setDate( String.valueOf( cal.get( Calendar.DATE ) ) );
		setMonth( String.valueOf( cal.get( Calendar.MONTH ) + 1 ) );
		setDay( String.valueOf( cal.get( Calendar.DAY_OF_MONTH ) ) );
		setYear( String.valueOf( cal.get( Calendar.YEAR ) ) );
		
		String data;
		// Permissions for Android 6.0
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
			data = String.format( "date -s %s%s%s%s%s.%s", getYear(), getMonth(), getDate(), getHours(), getMinutes(), getSeconds() );
		}
		else
			data = String.format( "date -s %s%s%s.%s%s%s", getYear(), getMonth(), getDate(), getHours(), getMinutes(), getSeconds() );

		Log.d( TAG, data );

		UtilShell.executeShellCommandWithOp( data );
	}
	
	public void resetRetryTimer(){
		setRetryCounter( 1 );
	}
}
