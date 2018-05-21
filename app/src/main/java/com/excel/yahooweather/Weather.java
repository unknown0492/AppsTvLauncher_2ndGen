package com.excel.yahooweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.excel.appstvlauncher.secondgen.AnimatedGifImageView;
import com.excel.configuration.ConfigurationReader;
/**
 * Created by Sohail on 26-01-2017.
 */

public class Weather {

    public static boolean isWeatherAvailable = false;
    public static boolean isYahooWeatherServicePaused = false;
    BroadcastReceiver yahooWeatherReceiver;
    final static String TAG = "Weather";
    Context context;
    ConfigurationReader configurationReader;
    View iv_weather, tv_temperature, tv_text;
    //Weather.isWeatherAvailable = true;


    public Weather(){}

    public Weather( Context context, ConfigurationReader configurationReader,
                    View iv_weather, View tv_temperature, View tv_text ){
        this.context = context;
        this.configurationReader = configurationReader;
        this.iv_weather = iv_weather;
        this.tv_temperature = tv_temperature;
        this.tv_text = tv_text;
    }


    public void startYahooWeatherService(){
        Intent in = new Intent( context, YahooWeatherService.class );
        context.startService( in );
    }

    public void createYahooWeatherReceiver(){
        yahooWeatherReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent ) {
                Log.i( TAG, "Weather Received on Launcher" );

                isWeatherAvailable = true;

                String code = intent.getStringExtra( "code" );
                String temp = intent.getStringExtra( "temp" );
                String text = intent.getStringExtra( "text" );

                Log.d( TAG, "drawable/weather_icon_"+code  );

                //iv_weather.setBackgroundResource( context.getResources().getIdentifier( "drawable/weather_icon_"+code, null, context.getPackageName() ) );
                //iv_weather.setBackgroundResource( context.getResources().getIdentifier( "drawable/anim_weather", null, context.getPackageName() ) );
                ((AnimatedGifImageView)iv_weather).setAnimatedGif( context.getResources().getIdentifier( "drawable/weather_icon_"+code , null, context.getPackageName() ), AnimatedGifImageView.TYPE.AS_IS );
                //((AnimatedGifImageView)iv_weather).setAnimatedGif( context.getResources().getIdentifier( "drawable/small_loading1" , null, context.getPackageName() ), AnimatedGifImageView.TYPE.AS_IS );

                //((AnimatedGifImageView)iv_weather).setAnimatedGif( context.getResources().getIdentifier( "drawable/anim_weather", null, context.getPackageName() ), TYPE.AS_IS );
                ((TextView)tv_temperature).setText( temp + "°C" );
                ((TextView)tv_text).setText( text );

                // Step-6 from YahooWeatherService.class
			    /*if( !isClockAndWeatherFlippingStarted )
			    	startFlippingClockAndWeather();*/

            }
        };
        LocalBroadcastManager.getInstance( context ).registerReceiver( yahooWeatherReceiver, new IntentFilter( "update_weather" ) );
    }

    public void deleteYahooWeatherReceiver(){
        LocalBroadcastManager.getInstance( context ).unregisterReceiver( yahooWeatherReceiver );
    }

    public void pauseYahooWeatherService(){
        isYahooWeatherServicePaused = true;

    }

    public void resumeYahooWeatherService(){
        isYahooWeatherServicePaused = false;

        /*if( ! configurationReader.getIsWeatherEnabled() ){
            isWeatherAvailable = false;
            Log.e( TAG, "Weather disabled !" );
        }
        else{
            isWeatherAvailable = true;
            Log.i( TAG, "Weather enabled !" );
        }*/
    }

    public void resumeYahooWeatherService( ConfigurationReader configurationReader ){
        isYahooWeatherServicePaused = false;

        if( ! configurationReader.getIsWeatherEnabled() ){
            isWeatherAvailable = false;
            Log.e( TAG, "Weather disabled !" );
        }
        else{
            isWeatherAvailable = true;
            Log.i( TAG, "Weather enabled !" );
        }
    }

    public static boolean isYahooWeatherServicePaused(){
        return isYahooWeatherServicePaused;
    }
}
