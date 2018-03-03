package com.excel.appstvlauncher.secondgen;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.excel.configuration.ConfigurationReader;

public class LoadingActivity extends Activity {

    TextView tv_loading_text;
    ProgressBar progress_bar;
    public int progress = 0;
    public int seconds = 10 * 10;
    public int maxProgress = 200;
    public int step = 1;

    Handler handler = new Handler();
    ConfigurationReader configurationReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        init();
    }

    private void init(){
        initViews();

        configurationReader = ConfigurationReader.reInstantiate();
        maxProgress = Integer.parseInt( configurationReader.getLoadingScreenTime() ) * 10;

        setProgressBarConfig();
        startLoading();
    }

    private void initViews(){
        tv_loading_text = (TextView) findViewById( R.id.tv_loading_text );
        progress_bar    = (ProgressBar) findViewById( R.id.progress_bar );

    }


    private void setProgressBarConfig(){
        progress_bar.setIndeterminate( false );
        progress_bar.setProgress( progress );
        progress_bar.setMax( maxProgress );
    }

    /*@Override
    public boolean onKeyDown( int keyCode, KeyEvent event ) {

        String key_name = KeyEvent.keyCodeToString( keyCode );
        Log.d( null, "KeyPressed : "+keyCode+","+key_name );

        return true;
    }*/

    private void startLoading(){
        Log.d( "LoadingActivity", "startLoading()" );

        new AsyncTask< Void, Void, Void >(){

            @Override
            protected Void doInBackground( Void... params ) {
                new Thread( new Runnable() {
                    public void run() {
                        for( int i = 0 ; i < maxProgress ; i++ ){

                            handler.post( new Runnable() {
                                public void run() {
                                    progress_bar.setProgress( progress++ );
                                    // Log.d( null, "" + progress );
                                    if( progress == maxProgress ) {
                                        MainActivity.setIsLoadingCompleted( true );
                                        // Log.d( "LoadingActivity", "Monkey executing now !" );
                                        // UtilShell.executeShellCommandWithOp( "monkey -p com.excel.appstvlauncher.secondgen -c android.intent.category.LAUNCHER 1" );
                                        finish();
                                        overridePendingTransition( 0, 0 );
                                    }
                                }
                            });
                            try {
                                Thread.sleep( 100 );
                            } catch ( InterruptedException e ) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                return null;
            }

        }.execute();
    }
}
