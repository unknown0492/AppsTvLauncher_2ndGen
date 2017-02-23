package com.excel.appstvlauncher.secondgen;


import android.os.Handler;

/**
 * Created by Sohail on 24-01-2017.
 */

public class VirussTimer {

    private long interval = -1;
    private Handler handler = null;
    private boolean isRunning = false;

    public VirussTimer(){
        this.interval = 5000;
    }

    public VirussTimer( long interval ){
        this.interval = interval;
    }

    public void start( Runnable runnable ){
        //if( handler == null ) {
        handler = new Handler();
        //}
        handler.postDelayed( runnable, interval );
        setIsRunning( true );

    }

    public void start( Runnable runnable, long inte ){
        //if( handler == null ) {
        handler = new Handler();
        //}
        handler.postDelayed( runnable, inte );
        setIsRunning( true );
        //Log.d( null, ""+ Math.random() );
    }

    public void stop( Runnable runnable ){
        handler.removeCallbacks( runnable );
        setIsRunning( false );
    }

    public void setInterval( long interval ){

    }

    private void setIsRunning( boolean isRunning ){
        this.isRunning = isRunning;
    }

    public boolean isRunning(){
        return isRunning;
    }
}
