package com.excel.appstvlauncher.secondgen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
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
            //context.sendBroadcast( new Intent( "update_launcher_config" ) );
            LocalBroadcastManager.getInstance( context ).sendBroadcast( new Intent( "update_launcher_config" ) );

        }
    }
}
