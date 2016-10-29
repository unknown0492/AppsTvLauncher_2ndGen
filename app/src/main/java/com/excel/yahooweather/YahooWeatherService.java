package com.excel.yahooweather;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.excel.appstvlauncher.secondgen.MainActivity;
import com.excel.configuration.ConfigurationReader;
import com.excel.excelclasslibrary.UtilNetwork;
import com.excel.excelclasslibrary.UtilURL;

import org.json.JSONException;
import org.json.JSONObject;

public class YahooWeatherService extends Service {
	
	Context context = this;
	final static String TAG = "YahooWeatherService";
	ConfigurationReader configurationReader;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId ) {
		configurationReader = ConfigurationReader.getInstance();
		
		/**
		 * Algorithm
		 * Retrieve Weather
		 * 
		 * 1. App turned on
		 * 2. If Box is connected to the Network
		 * 	  3. Run AsyncWeather which calls YahooWebService
		 * 	  4. If weather Retrieval Successful
		 * 		 5. Send Broadcast to Launcher to Update Weather on the Widget
		 * 		 6. Start the Clock and Weather Flipping
		 * 		 7. Set WeatherRefresh Timer of 10 MINUTES
		 * 	  8. If weather Retrieval Fails (Null is returned or Box does not have access to Internet)
		 * 		 9. [Same as Step 7]
		 * 10.If Box is not connected to the Network
		 * 	  11. Set WeatherRefresh Timer of 10 SECONDS
		 * 
		 * 
		 */
		
		// Step-2
		if( UtilNetwork.isConnectedToInternet( context ) ){
			
			// Step-3
			new AsyncWeather().execute( configurationReader.getLocation() );
		}
		else{	// Step-10
			
			// Step-11
			retryWeather();
			
		}
		
		return START_NOT_STICKY;
	}
	
	class AsyncWeather extends AsyncTask< String, Integer, String >{

		@Override
		protected String doInBackground( String... params ) {
			String location = params[ 0 ];
			String YQL = String.format( "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") AND u='c'", location );
			String url = String.format( "https://query.yahooapis.com/v1/public/yql?q=%s&format=json&u=c", Uri.encode( YQL ) );
			//Log.i(TAG,  "inside doInBackground() : "+url );
			//String response = UtilNetwork.makeRequestForData( url, "POST", "" );
			Log.d( TAG, "Webservice path : "+UtilURL.getWebserviceURL() );
			String response = UtilNetwork.makeRequestForData( UtilURL.getWebserviceURL(), "POST",
					UtilURL.getURLParamsFromPairs( new String[][]{ { "what_do_you_want", "get_weather" },
                            { "city", location },
                            { "mac_address", UtilNetwork.getMacAddress( context ) } } ) );

			return response;
		}

		@Override
		protected void onPostExecute( String result ) { 
			super.onPostExecute( result );
			
			// Log.i( TAG,  "inside onPostExecute()" );
			
			// Step-4
			if( result != null ){
                // Log.i( TAG,  result );
				try {
					JSONObject jsonObject = new JSONObject( result );
					JSONObject query	  = jsonObject.optJSONObject( "query" );
					int count 			  = query.optInt( "count" );
					if( count == 0 ){
						Log.e( TAG, "No results for the given query were returned" );
						
					}
					else{
						JSONObject results	  = query.optJSONObject( "results" );
						JSONObject channel	  = results.optJSONObject( "channel" );
						JSONObject item		  = channel.optJSONObject( "item" );
						JSONObject condition  = item.optJSONObject( "condition" );
						
						String code			= condition.getString( "code" );
						String temp			= condition.getString( "temp" );
						String text			= condition.getString( "text" );
						
						Log.i( TAG, String.format( "Code : %s, Temperature : %s, Text : %s", code, temp, text ) );
						
						// Step-5 [Step-6 in MainActivity]
						Intent in = new Intent( "update_weather" );
						in.putExtra( "code", code );
						in.putExtra( "temp", temp );
						in.putExtra( "text", text );
						LocalBroadcastManager.getInstance( context ).sendBroadcast( in );
						
					}
				}
				catch ( JSONException e ) {
					e.printStackTrace();
					
				}
			}
			else{
				Log.e( TAG, "Null was returned in Yahoo Weather Service" );
			}
			
			// Step-7
			setWeatherRefreshTimer();
		}
	}
	
	public void retryWeather(){
		new Handler().postDelayed( new Runnable() {
			
			@Override
			public void run() {
				 Intent in = new Intent( context, YahooWeatherService.class );
			     startService( in );
			     
			}
		}, Long.parseLong( configurationReader.getWeatherRetryInterval() ) );
	}
	
	public void setWeatherRefreshTimer(){
		
		if( MainActivity.isYahooWeatherServicePaused() ){
			
			Log.d( TAG, "setWeatherRefreshTimer() : "+String.valueOf( MainActivity.isYahooWeatherServicePaused() ) );
			
			new Handler().postDelayed( new Runnable() {
				
				@Override
				public void run() {
					setWeatherRefreshTimer();
				}
			}, Long.parseLong( configurationReader.getWeatherRefreshInterval() ) ); // 5 minutes
			return;
		}
		
		
		new Handler().postDelayed( new Runnable() {
			
			@Override
			public void run() {
				Log.d( TAG,  "Refreshing Weather" );
				Intent in = new Intent( context, YahooWeatherService.class );
				startService( in );
			}
		}, Long.parseLong( configurationReader.getWeatherRefreshInterval() ) );
	}

}
