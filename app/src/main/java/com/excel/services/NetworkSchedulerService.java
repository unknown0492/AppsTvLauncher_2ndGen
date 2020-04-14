package com.excel.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.excel.appstvlauncher.secondgen.Receiver;
import com.excel.excelclasslibrary.UtilMisc;
import com.excel.receivers.ConnectivityReceiver;

import java.util.List;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.excel.excelclasslibrary.Constants.DATADOWNLOADER_PACKAGE_NAME;
import static com.excel.excelclasslibrary.Constants.DATADOWNLOADER_RECEIVER_NAME;
import static com.excel.excelclasslibrary.Constants.DATAGRAMMONITOR_PACKAGE_NAME;
import static com.excel.excelclasslibrary.Constants.DATAGRAMMONITOR_RECEIVER_NAME;
import static com.excel.excelclasslibrary.Constants.DISPLAYPROJECT_PACKAGE_NAME;
import static com.excel.excelclasslibrary.Constants.DISPLAYPROJECT_RECEIVER_NAME;
import static com.excel.excelclasslibrary.Constants.ONETIMESETUP_PACKAGE_NAME;
import static com.excel.excelclasslibrary.Constants.ONETIMESETUP_RECEIVER_NAME;
import static com.excel.excelclasslibrary.Constants.REMOTELYCONTROL_PACKAGE_NAME;
import static com.excel.excelclasslibrary.Constants.REMOTELYCONTROL_RECEIVER_NAME;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkSchedulerService extends JobService implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = NetworkSchedulerService.class.getSimpleName();
    private ConnectivityReceiver mConnectivityReceiver = null;
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        Log.i( TAG, "Service created" );
        mConnectivityReceiver = new ConnectivityReceiver( this );
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
        if( mConnectivityReceiver != null ){
            try {
                unregisterReceiver(mConnectivityReceiver);
            }
            catch ( Exception e ){

                e.printStackTrace();
            }
            mConnectivityReceiver = null;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if( mConnectivityReceiver != null ){
            try {
                unregisterReceiver(mConnectivityReceiver);
            }
            catch ( Exception e ){
                e.printStackTrace();
            }
            mConnectivityReceiver = null;
        }
    }

    @Override
    public void onNetworkConnectionChanged( boolean isConnected ) {
        String message = isConnected ? "Good! Connected to Internet baby" : "Sorry! Not connected to internet baba";
        Log.i( TAG, message );
        //Toast.makeText( getApplicationContext(), message, Toast.LENGTH_SHORT ).show();

        // Send broadcast to RemotelyControlAppsTv
        /*Intent in = new Intent( "connectivity_change" );        // Implicit Intent
        in.setPackage( "com.excel.remotelycontrolappstv.secondgen" );   // Explicit intent
        sendBroadcast( in );*/

        //LocalBroadcastManager.getInstance( context ).sendBroadcast( new Intent( "trigger_welcome_screen" ) );

        Intent intent = new Intent( "connectivity_change" );
        PackageManager pm = getPackageManager();
        List<ResolveInfo> infos = pm.queryBroadcastReceivers( intent, 0 );

        for( ResolveInfo info : infos ){
            ComponentName cn = new ComponentName( info.activityInfo.packageName, info.activityInfo.name );
            Log.d( TAG, info.activityInfo.packageName + "," + info.activityInfo.name );
            intent.setComponent( cn );
            sendBroadcast( intent );
        }

        UtilMisc.sendExplicitInternalBroadcast( context, "connectivity_change", Receiver.class );
        /*
        Intent in0 = new Intent();
        UtilMisc.sendExplicitExternalBroadcast( context, in0, "connectivity_change", ONETIMESETUP_PACKAGE_NAME, ONETIMESETUP_RECEIVER_NAME );

        Intent in = new Intent();
        UtilMisc.sendExplicitExternalBroadcast( context, in, "connectivity_change", REMOTELYCONTROL_PACKAGE_NAME, REMOTELYCONTROL_RECEIVER_NAME );

        Intent in1 = new Intent();
        UtilMisc.sendExplicitExternalBroadcast( context, in1, "connectivity_change", DATADOWNLOADER_PACKAGE_NAME, DATADOWNLOADER_RECEIVER_NAME );

        Intent in2 = new Intent();
        UtilMisc.sendExplicitExternalBroadcast( context, in2, "connectivity_change", DISPLAYPROJECT_PACKAGE_NAME, DISPLAYPROJECT_RECEIVER_NAME );

        Intent in3 = new Intent();
        UtilMisc.sendExplicitExternalBroadcast( context, in3, "connectivity_change", DATAGRAMMONITOR_PACKAGE_NAME, DATAGRAMMONITOR_RECEIVER_NAME );
*/
    }
}
