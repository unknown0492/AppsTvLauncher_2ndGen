package com.excel;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.excel.configuration.PreinstallApps;
import com.excel.excelclasslibrary.UtilShell;

public class ResumeTaskService extends Service {

    Context context = this;
    final static String TAG = "ResumeTaskService";

    public ResumeTaskService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        Log.i( TAG, TAG + " started" );

        PreinstallApps[] paps = PreinstallApps.getPreinstallApps();
        for( int i = 0 ; i < paps.length; i++ ){

            if( paps[ i ].getForceKill().trim().equals( "force_kill" ) ) {
                String pid = UtilShell.executeShellCommandWithOp( "pidof " + paps[ i ].getPackageName() ).trim();
                UtilShell.executeShellCommandWithOp( "kill "+pid );
                Log.d( TAG, "Killed pid : "+pid+", of package " + paps[ i ].getPackageName() );
                continue;
            }
            Log.d( TAG, "Skipped : " + paps[ i ].getPackageName() + ", " + paps[ i ].getForceKill() );
        }

        String pid = UtilShell.executeShellCommandWithOp( "pidof com.android.dtv" ).trim();
        UtilShell.executeShellCommandWithOp( "kill "+pid );
        Log.d( TAG, "Killed pid : "+pid+", of package com.android.dtv" );

                /*
                pid = UtilShell.executeShellCommandWithOp( "pidof com.google.android.youtube.tv" ).trim();
                UtilShell.executeShellCommandWithOp( "kill "+pid );
                Log.d( TAG, "Killed pid : "+pid+", of package com.google.android.youtube.tv" );*/


        // Start Airplay service
        //new MainActivity().startScreenCastService();

        return START_NOT_STICKY;
    }
}
