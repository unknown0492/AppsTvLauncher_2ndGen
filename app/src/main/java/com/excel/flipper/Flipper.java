package com.excel.flipper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.excel.appstvlauncher.secondgen.VirussTimer;
import com.excel.configuration.ConfigurationReader;
import com.excel.yahooweather.Weather;

import java.io.File;

import static com.excel.imagemanipulator.DigitalSignage.setImageFromPathOnView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Created by Sohail on 26-01-2017.
 */

public class Flipper {

    VirussTimer clock_weather_logo_flipper = null;
    Runnable clock_weather_logo_runnable = null;
    int INDEX_CLOCK_SHOWING = 0, INDEX_WEATHER_SHOWING = 1, INDEX_HOTEL_LOGO_SHOWING = 2;
    boolean clock_weather_logo_bool[] = { true, false, false };
    public static boolean isHotelLogoAvailable = false;
    long t_interval = -1;
    final static String TAG = "Flipper";
    ConfigurationReader configurationReader;
    View ll_clock_time, rl_weather, rl_hotel_logo, tv_temperature, tv_text;
    BroadcastReceiver hotelLogoAvailabilityReceiver;
    Context context;
    //boolean isWeatherAvailable;

    public Flipper(){}

    public Flipper( Context context, ConfigurationReader configurationReader,
                    View ll_clock_time, View rl_weather, View rl_hotel_logo,
                    View tv_temperature, View tv_text ){
        this.configurationReader = configurationReader;
        this.ll_clock_time = ll_clock_time;
        this.rl_weather = rl_weather;
        this.rl_hotel_logo = rl_hotel_logo;
        this.tv_temperature = tv_temperature;
        this.tv_text = tv_text;
        this.context = context;
    }

    public void startClockWeatherLogoFlipper(){
        Log.d( TAG, "startClockWeatherLogoFlipper()" );
        t_interval = Long.parseLong( configurationReader.getClockWeatherFlipInterval() );

        clock_weather_logo_flipper = new VirussTimer( t_interval );
        clock_weather_logo_runnable = new Runnable() {

            @Override
            public void run() {

                if( clock_weather_logo_bool[ INDEX_CLOCK_SHOWING ] ){

                    if( Weather.isWeatherAvailable ){
                        //Log.d( TAG, "weather is available" );
                        //ll_clock_time.animate().rotationXBy( 90f ).setDuration( 300 ).withEndAction(new Runnable() {
                        ll_clock_time.animate().alpha( 0.0f ).setDuration( 300 ).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //rl_weather.animate().rotationXBy( -90f ).setDuration( 300 ).start();
                                rl_weather.animate().alpha( 1.0f ).setDuration( 300 ).start();

                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run(){
                                        tv_temperature.animate().alpha( 1.0f ).setDuration( 300 ).start();
                                        tv_text.animate().alpha( 0.0f ).setDuration( 300 ).start();
                                        /*.withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_text.animate().alpha( 0.0f ).setDuration( 300 ).start();
                                            }
                                        }).start();*/
                                    }

                                }, Long.parseLong( configurationReader.getClockWeatherFlipInterval() )/2 );
                            }
                        }).start();
                        flipToIndex( INDEX_WEATHER_SHOWING );
                        t_interval = Long.parseLong( configurationReader.getClockWeatherFlipInterval() );


                    }
                    else if( Flipper.isHotelLogoAvailable ){
                        // Log.d( TAG, "hotel logo is available" );
                        flipToIndex( INDEX_HOTEL_LOGO_SHOWING );
                        //ll_clock_time.animate().rotationXBy( 90f ).setDuration( 300 ).withEndAction(new Runnable() {
                        ll_clock_time.animate().alpha( 0.0f ).setDuration( 300 ).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //rl_hotel_logo.animate().rotationXBy( -90f ).setDuration( 300 ).start();
                                rl_hotel_logo.animate().alpha( 1.0f ).setDuration( 300 ).start();
                            }
                        }).start();
                        t_interval = Long.parseLong( configurationReader.getHotelLogoFlipInterval() );
                    }


                }
                else if( clock_weather_logo_bool[ INDEX_WEATHER_SHOWING ] ){

                    if( Flipper.isHotelLogoAvailable ){
                        //Log.d( TAG, "hotel logo is available" );
                        flipToIndex( INDEX_HOTEL_LOGO_SHOWING );
                        //rl_weather.animate().rotationXBy( 90f ).setDuration( 300 ).withEndAction(new Runnable() {
                        rl_weather.animate().alpha( 0.0f ).setDuration( 300 ).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //rl_hotel_logo.animate().rotationXBy( -90f ).setDuration( 300 ).start();
                                rl_hotel_logo.animate().alpha( 1.0f ).setDuration( 300 ).start();
                            }
                        }).start();
                        t_interval = Long.parseLong( configurationReader.getHotelLogoFlipInterval() );
                    }
                    else{
                        flipToIndex( INDEX_CLOCK_SHOWING );
                        // rl_weather.animate().rotationXBy( 90f ).setDuration( 300 ).withEndAction(new Runnable() {
                        rl_weather.animate().alpha( 0.0f ).setDuration( 300 ).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //ll_clock_time.animate().rotationXBy( -90f ).setDuration( 300 ).start();
                                ll_clock_time.animate().alpha( 1.0f ).setDuration( 300 ).start();
                            }
                        }).start();
                        t_interval = Long.parseLong( configurationReader.getClockWeatherFlipInterval() );
                    }
                    tv_text.animate().alpha( 1.0f ).setDuration( 300 ).start();
                    tv_temperature.animate().alpha( 0.0f ).setDuration( 300 ).start();

                }
                else{
                    flipToIndex( INDEX_CLOCK_SHOWING );
                    // rl_hotel_logo.animate().rotationXBy( 90f ).setDuration( 300 ).withEndAction(new Runnable() {
                    rl_hotel_logo.animate().alpha( 0.0f ).setDuration( 300 ).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            //ll_clock_time.animate().rotationXBy( -90f ).setDuration( 300 ).start();
                            ll_clock_time.animate().alpha( 1.0f ).setDuration( 300 ).start();
                        }
                    }).start();
                    t_interval = Long.parseLong( configurationReader.getClockWeatherFlipInterval() );
                }

                clock_weather_logo_flipper.start( clock_weather_logo_runnable, t_interval );
            }
        };
        clock_weather_logo_flipper.start( clock_weather_logo_runnable );
    }

    public void pauseClockWeatherLogoFlipper(){
        Log.d( TAG, "pauseClockWeatherLogoFlipper()" );
        clock_weather_logo_flipper.stop( clock_weather_logo_runnable );
    }

    public void flipToIndex( int index ){
        clock_weather_logo_bool = new boolean[ 3 ];
        clock_weather_logo_bool[ index ] = true;
        //Log.d( TAG, String.format( "0->%s, 1->%s, 2->%s", String.valueOf( clock_weather_logo_bool[ 0 ] ), String.valueOf( clock_weather_logo_bool[ 1 ] ), String.valueOf( clock_weather_logo_bool[ 2 ] ) ) );
    }

    public void createHotelLogoAvailabilityReceiver(){
        hotelLogoAvailabilityReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent ) {
                Log.i( TAG, "Hotel Logo Availability received on Launcher" );
                Flipper.isHotelLogoAvailable = intent.getBooleanExtra( "hasHotelLogoDisplay", false );

                File hotel_logo_file = new File( configurationReader.getHotelLogoDirectoryPath() + File.separator + "hotel_logo.png" );
                setImageFromPathOnView( hotel_logo_file.getAbsolutePath(), rl_hotel_logo );

            }
        };
        LocalBroadcastManager.getInstance( context ).registerReceiver( hotelLogoAvailabilityReceiver, new IntentFilter( "update_hotel_logo_availability" ) );
    }

    public void deleteHotelLogoAvailabilityReceiver(){
        LocalBroadcastManager.getInstance( context ).unregisterReceiver( hotelLogoAvailabilityReceiver );
    }

}
