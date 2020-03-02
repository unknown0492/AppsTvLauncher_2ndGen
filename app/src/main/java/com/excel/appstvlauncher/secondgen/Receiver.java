package com.excel.appstvlauncher.secondgen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by Sohail on 03-11-2016.
 */

public class Receiver extends BroadcastReceiver {

    final static String TAG = "Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d( TAG, "action : "+action );

        if( action.equals( "receive_update_launcher_config" ) ){
            LocalBroadcastManager.getInstance( context ).sendBroadcast( new Intent( "update_launcher_config" ) );
        }
        if( action.equals( "receive_outside_update_launcher_config" ) ){ // To update time/day for language change from outside of launcher
            LocalBroadcastManager.getInstance( context ).sendBroadcast( new Intent( "update_time_on_clock" ) );
        }
        else if( action.equals( "receive_update_hotspot_info" ) ){
            LocalBroadcastManager.getInstance( context ).sendBroadcast( new Intent( "update_hotspot_info" ) );
        }
        else if( action.equals( "receive_update_time_on_clock" ) ){
            LocalBroadcastManager.getInstance( context ).sendBroadcast( new Intent( "send_update_to_clock" ) );
        }
        else if( action.equals( "receive_get_hotel_logo" ) ){ // Broadcasted from DataDownloader when the Logo is downloaded
            Intent in = new Intent( "update_hotel_logo_availability" );
            in.putExtra( "hasHotelLogoDisplay", intent.getBooleanExtra( "hasHotelLogoDisplay", false ) );
            LocalBroadcastManager.getInstance( context ).sendBroadcast( in ); // Catch this Local Broadcast inside the AppsTvLauncher
        }
        else if( action.equals( "connectivity_change" ) ){
            // Trigger Welcome Screen
            LocalBroadcastManager.getInstance( context ).sendBroadcast( new Intent( "trigger_welcome_screen" ) );


        }
    }
}
