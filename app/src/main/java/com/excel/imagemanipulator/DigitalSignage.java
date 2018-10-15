package com.excel.imagemanipulator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.excel.appstvlauncher.secondgen.R;
import com.excel.configuration.ConfigurationReader;

/**
 * Created by Sohail on 03-11-2016.
 */

public class DigitalSignage{

    final static String TAG = "DigitalSignage";

    DigitalSignageHolder digitalSignageHolder;
    boolean isDefaultWallpaperActive = true;
    boolean isDigitalSignageSwitcherStarted = false;
    Handler digitalSignageSwitchHandler = null;
    Runnable digitalSignageRunnable = null;
    RelativeLayout rl_launcher_bg;
    Context context;
    ConfigurationReader configurationReader;

    public DigitalSignage( Context context ){
        digitalSignageHolder = new DigitalSignageHolder( context );
        this.context = context;

    }

    public DigitalSignage(Context context, RelativeLayout rl_launcher_bg ){
        digitalSignageHolder = new DigitalSignageHolder( context );
        this.rl_launcher_bg = rl_launcher_bg;
        this.context = context;
        configurationReader = ConfigurationReader.getInstance();
    }

    public void setDefaultWallpaperOnBackground(){
        //Bitmap bmp = ImageManipulator.getDecodedBitmap( getResources(), R.drawable.default_bg, 1920, 1020 );
        //Drawable dr = new BitmapDrawable( bmp );
        Drawable dr = ImageManipulator.getDecodedDrawable( context.getResources(), R.drawable.default_bg_black, 1920, 1020 );
        rl_launcher_bg.setBackgroundDrawable( dr );
        dr = null;
    }

    public void setDigitalSignageOnBackground( final String path ){
        new AsyncTask<Void,Void,Drawable>(){

            @Override
            protected Drawable doInBackground(Void... voids) {
                Drawable dr = ImageManipulator.getDecodedDrawable( path, 1920, 1020 );
                return dr;
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);

                if( drawable == null )
                    setDefaultWallpaperOnBackground();
                else{
                    rl_launcher_bg.setBackgroundDrawable( drawable );
                }
            }
        }.execute();

        /*Drawable dr = ImageManipulator.getDecodedDrawable( path, 1920, 1020 );
        if( dr == null ){
            setDefaultWallpaperOnBackground();
            return;
        }
        rl_launcher_bg.setBackgroundDrawable( dr );
        dr = null;*/
    }



    public void startDigitalSignageSwitcher(){

        // initialize a new handler
        digitalSignageSwitchHandler = new Handler();

        // initialize a new Runnable
        digitalSignageRunnable = new Runnable() {

            @Override
            public void run() {
                setDigitalSignageAsWallpaper();
            }

        };

        // Start a postDelayed after xx seconds
        digitalSignageSwitchHandler.postDelayed( digitalSignageRunnable,
                Long.parseLong( configurationReader.getDigitalSignageInterval() ) );

    }

    public void setDigitalSignageAsWallpaper(){

        if( digitalSignageHolder.getDigitalSignageCount() == 0 )
            digitalSignageHolder.reloadDigitalSignageHolder();

        String currentDigitalSignage = null;
        if( ( currentDigitalSignage = digitalSignageHolder.getNextDigitalSignage() ) == null ){
            setDefaultWallpaperOnBackground();
            isDefaultWallpaperActive = true;
        }
        else{
            // Log.d( TAG, "Current Digital Signage to be Shown : "+currentDigitalSignage );
            setDigitalSignageOnBackground( currentDigitalSignage );
        }

        // if( !isDigitalSignageSwitcherPaused )
        if( getDigitalSignageSwitcherStatus() ) // if active
            startDigitalSignageSwitcher();

        //if( ! isDigitalSignageSwitcherStarted )
        //startDigitalSignageSwitcher();

    }

    public void pauseDigitalSignageSwitcher(){
        Log.d( TAG,  "DigitalSignageSwitcher paused" );
        setDigitalSignageSwitcherStatus( false );
    }

    public void resumeDigitalSignageSwitcher(){
        Log.d( TAG,  "DigitalSignageSwitcher resumed" );

        setDigitalSignageAsWallpaper();

        if( ! getDigitalSignageSwitcherStatus() ){ // if not already started
            setDigitalSignageSwitcherStatus( true );
            startDigitalSignageSwitcher();
        }
    }



    public void setDigitalSignageSwitcherStatus( boolean bool ){
        isDigitalSignageSwitcherStarted = bool;
        if( bool == false ){
            digitalSignageSwitchHandler.removeCallbacks( digitalSignageRunnable );
        }
    }

    public boolean getDigitalSignageSwitcherStatus(){
        return isDigitalSignageSwitcherStarted;
    }

    public static void setImageFromPathOnView( String path, View view ){
        Drawable dr = ImageManipulator.getDecodedDrawable( path, view.getWidth(), view.getHeight() );
        Log.d( TAG, "" + view.getWidth() + "," + view.getHeight() );
        if( dr == null ){
            Log.e( TAG, "Drawable returned an error !" );
            return;
        }
        view.setBackgroundDrawable( dr );
        dr = null;
    }
}
