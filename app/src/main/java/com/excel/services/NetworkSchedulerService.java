package com.excel.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.excel.receivers.ConnectivityReceiver;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkSchedulerService extends JobService implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = NetworkSchedulerService.class.getSimpleName();
    private ConnectivityReceiver mConnectivityReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i( TAG, "Service created" );
        mConnectivityReceiver = new ConnectivityReceiver(this );
    }

    /**
     * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        Log.i( TAG, "onStartCommand" );
        return START_STICKY;
    }


    @Override
    public boolean onStartJob( JobParameters params ) {
        Log.i( TAG, "onStartJob" + mConnectivityReceiver );
        registerReceiver( mConnectivityReceiver, new IntentFilter( CONNECTIVITY_ACTION ) );
        return true;
    }

    @Override
    public boolean onStopJob( JobParameters params ){
        Log.i( TAG, "onStopJob" );
        //unregisterReceiver(mConnectivityReceiver);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged( boolean isConnected ) {
        String message = isConnected ? "Good! Connected to Internet baby" : "Sorry! Not connected to internet baba";
        Toast.makeText( getApplicationContext(), message, Toast.LENGTH_SHORT ).show();

        // Send broadcast to RemotelyControlAppsTv
        /*Intent in = new Intent( "connectivity_change" );        // Implicit Intent
        in.setPackage( "com.excel.remotelycontrolappstv.secondgen" );   // Explicit intent
        sendBroadcast( in );*/

        Intent in = new Intent();
        ComponentName cn = new ComponentName( "com.excel.remotelycontrolappstv.secondgen",
                "com.excel.remotelycontrolappstv.secondgen.Receiver" );
        in.setComponent( cn );
        in.setAction( "connectivity_change" );
        sendBroadcast( in );

    }
}
