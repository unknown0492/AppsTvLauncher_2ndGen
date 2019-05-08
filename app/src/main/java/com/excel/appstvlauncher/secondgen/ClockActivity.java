package com.excel.appstvlauncher.secondgen;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.GridView;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class ClockActivity extends Activity {

	final static String TAG = "WorldClock";
	Timer time_updater;
	ClockAdapter ca;
	Context context = this;
	String[] location_names, clock_times, timezones, clock_dates;
	String data[][];
	GridView clock_container;
	
	final static int NUMBER_OF_LOCATIONS = 8;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock );
        
        init();
        
        createTime( "GMT+10:00", "Sydney, Australia" );
        createTime( "GMT+08:00", "Beijing, China" );
        createTime( "GMT+05:30", "New Delhi, India" );
        createTime( "GMT-04:00", "Washington DC, USA" );
        createTime( "GMT+01:00", "London, UK" );
        createTime( "GMT+07:00", "Jakarta, Indonesia" );
        createTime( "GMT+09:00", "Seoul, south Korea" );
        createTime( "GMT+09:00", "Tokyo, Japan" );
        
        tickTime();
        
    }
    
    private void init(){
    //	ActionBar ab = getActionBar();
    	//ab.hide();
    	
    	clock_container = (GridView) findViewById( R.id.clock_container );
    	
    	location_names = new String[]{
    			"Sydney, Australia",
    	        "Beijing, China",
    	        "New Delhi, India",
    	        "Washington DC, USA",
    	        "London, UK",
    	        "Jakarta, Indonesia",
    	        "Seoul, South Korea",
    	        "Tokyo, Japan"
    								 };
    	
        timezones	   = new String[]{
        		"GMT+10:00",
                "GMT+08:00",
                "GMT+05:30",
                "GMT-04:00",
                "GMT+01:00",
                "GMT+07:00",
                "GMT+09:00",
                "GMT+09:00"
        							};
        
        clock_times = new String[ timezones.length ];
        clock_dates = new String[ timezones.length ];
        
        reInitializeTimes();
    }
    
    private void reInitializeTimes(){
    	String str[] = new String[ 2 ];
    	for( int i = 0 ; i < timezones.length ; i++ ){
    		str = createTimeAndDate( timezones[ i ], location_names[ i ] );
    		clock_times[ i ] = str[ 0 ];
    		clock_dates[ i ] = str[ 1 ];
        }
    }
    
    public static String createTime( String timezone, String location_name ){
    	Calendar c = Calendar.getInstance();
        c.setTimeZone( TimeZone.getTimeZone( timezone ) );
        String hours = singleDigitToDoubleDigit( c.get( Calendar.HOUR_OF_DAY ) );
        String mins  = singleDigitToDoubleDigit( c.get( Calendar.MINUTE ) );
        String secs  = singleDigitToDoubleDigit( c.get( Calendar.SECOND ) );
        //Log.i( TAG, location_name+" - "+hours+":"+mins );
        return hours+":"+mins+":"+secs;
    }
    
    public static String[] createTimeAndDate( String timezone, String location_name ){
    	Calendar c = Calendar.getInstance();
        c.setTimeZone( TimeZone.getTimeZone( timezone ) );
        String hours = singleDigitToDoubleDigit( c.get( Calendar.HOUR_OF_DAY ) );
        String mins  = singleDigitToDoubleDigit( c.get( Calendar.MINUTE ) );
        String date = numberToDay( c.get( Calendar.DAY_OF_WEEK ) ) + ", " + numberToMonth( c.get( Calendar.MONTH ) ) + " " + c.get( Calendar.DAY_OF_MONTH ) + ", " + c.get( Calendar.YEAR );
        String secs  = singleDigitToDoubleDigit( c.get( Calendar.SECOND ) );
        //Log.i( TAG, location_name+" - "+hours+":"+mins );
        return new String[]{ hours+":"+mins+":"+secs, date };
    }
    
    public static String numberToDay( int day_of_week ){
    	switch( day_of_week ){
	    	case 1: 
				return "Sun";
	    	case 2:
				return "Mon";
	    	case 3:
				return "Tue";
	    	case 4:
				return "Wed";
	    	case 5:
				return "Thu";
	    	case 6:
				return "Fri";
	    	case 7:
				return "Sat";
    	}
    	return "";
    }
    
    public static String numberToMonth( int month_of_year ){
    	switch( month_of_year ){
    		case 0:
    			return "Jan";
    		case 1:
				return "Feb";
	    	case 2:
				return "Mar";
	    	case 3:
				return "Apr";
	    	case 4:
				return "May";
	    	case 5:
				return "June";
	    	case 6:
				return "Jul";
	    	case 7:
				return "Aug";
	    	case 8:
				return "Sept";
	    	case 9:
				return "Oct";
	    	case 10:
				return "Nov";
	    	case 11:
				return "Dec";
			
    	}
    	return "";
    }
    
    public static String singleDigitToDoubleDigit( int num ){
    	String n = String.valueOf( num );
    	if( n.length() == 1 ){
    		return "0" + n;
    	}
    	else
    		return n;
    }

	@Override
	protected void onPause() {
		super.onPause();
		time_updater.cancel();
	}

	private void tickTime(){
		time_updater = new Timer();
		time_updater.scheduleAtFixedRate( new TimerTask() {
			
			@Override
			public void run(){
				runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                       updateTime();
                    }
                });
			}
		}, 0, 1000 );
	}

    private void updateTime(){
    	reInitializeTimes();
    	ca = new ClockAdapter( context, R.layout.clock, location_names, clock_times, clock_dates );
    	clock_container.setAdapter( ca );
    }
}

