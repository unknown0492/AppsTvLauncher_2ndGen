package com.excel.welcome;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.widget.AppCompatImageView;

import com.excel.appstvlauncher.secondgen.AnimatedGifImageView;
import com.excel.appstvlauncher.secondgen.R;
import com.excel.configuration.ConfigurationReader;
import com.excel.excelclasslibrary.UtilNetwork;
import com.excel.excelclasslibrary.UtilShell;
import com.excel.excelclasslibrary.UtilURL;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class WelcomeScreenProducer {

    RelativeLayout rl_webview, rl_video_screen, rl_native_welcome, activity_main, rl_custom_welcome;
    TextView tv_native_welcome_text, tv_native_welcome_text_vs;
    WebView wv_welcome_screen;
    ConfigurationReader configurationReader;
    Context context;
    AppCompatImageView iv_native_bg, iv_hotel_logo, iv_hotel_logo_vs;
    Button bt_native_language_en, bt_native_language_zh, bt_native_language_vs_en, bt_native_language_vs_zh, bt_native_language_ja;

    VideoView vv_video_bg;

    public static final String TAG = "WelcomeGuestApp";
    AnimatedGifImageView loading;

    boolean timeout;
    long timeout_interval = 10000;

    YouTubePlayerView youTubePlayerView;
    View parentView;

    public View produce( Context context ){
        configurationReader = ConfigurationReader.reInstantiate();
        initViews( context );

        return checkConfiguration();

    }

    private void initViews( Context context ){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        parentView = layoutInflater.inflate( R.layout.welcome_screen, null );
        this.context = context;

        rl_webview = (RelativeLayout) parentView.findViewById( R.id.rl_webview );
        rl_video_screen = (RelativeLayout) parentView.findViewById( R.id.rl_video_screen );
        rl_native_welcome = (RelativeLayout) parentView.findViewById( R.id.rl_native_welcome );
        rl_custom_welcome = (RelativeLayout) parentView.findViewById( R.id.rl_custom_welcome );
        activity_main = (RelativeLayout) parentView.findViewById( R.id.activity_main );
        iv_native_bg = (AppCompatImageView) parentView.findViewById( R.id.iv_native_bg );
        iv_hotel_logo = (AppCompatImageView) parentView.findViewById( R.id.iv_hotel_logo );
        iv_hotel_logo_vs = (AppCompatImageView) parentView.findViewById( R.id.iv_hotel_logo_vs );
        tv_native_welcome_text = (TextView) parentView.findViewById( R.id.tv_native_welcome_text );
        tv_native_welcome_text_vs = (TextView) parentView.findViewById( R.id.tv_native_welcome_text_vs );
        bt_native_language_en = (Button) parentView.findViewById( R.id.bt_native_language_en );
        bt_native_language_vs_en = (Button) parentView.findViewById( R.id.bt_native_language_vs_en );
        bt_native_language_zh = (Button) parentView.findViewById( R.id.bt_native_language_zh );;
        bt_native_language_vs_zh = (Button) parentView.findViewById( R.id.bt_native_language_vs_zh );;
        bt_native_language_ja = (Button) parentView.findViewById( R.id.bt_native_language_ja );;
        wv_welcome_screen = (WebView) parentView.findViewById( R.id.wv_welcome_screen );
        vv_video_bg = (VideoView) parentView.findViewById( R.id.vv_video_bg );

        loading = (AnimatedGifImageView) parentView.findViewById( R.id.loading );
        loading.setAnimatedGif( context.getResources().getIdentifier( "drawable/small_loading1" , null, context.getPackageName() ), AnimatedGifImageView.TYPE.AS_IS );

        context.deleteDatabase("webview.db");
        context.deleteDatabase("webviewCache.db");

        /*parentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d( TAG, "WelcomeScreen is catching the keys !" );
                return true;
            }
        });*/

    }

    private View checkConfiguration(){

        if( configurationReader.getWelcomeScreenType().equals( "webview" ) )
            configureWebView();
        else if( configurationReader.getWelcomeScreenType().equals( "videoview" ) )
            configureVideoView();
        else if( configurationReader.getWelcomeScreenType().equals( "native" ) )
            configureNativeView();
        else if( configurationReader.getWelcomeScreenType().equals( "custom" ) )
            configureCustomView();

        return parentView;
    }

    private void configureWebView(){
        Log.d( TAG, "configureWebView()" );
        showWebViewScreen();
        //wv_welcome_screen = new WebView( context );//
        //wv_welcome_screen = (WebView) findViewById( R.id.wv_welcome_screen );
        wv_welcome_screen.clearCache(true);
        wv_welcome_screen.setFocusable( false );
        wv_welcome_screen.getSettings().setJavaScriptEnabled( true );
        wv_welcome_screen.getSettings().setAppCacheEnabled( false );

        wv_welcome_screen.setWebViewClient(new WebViewClient() {

            public void onPageFinished( WebView webview, String url ){
                super.onPageFinished( webview, url );

                Log.d( TAG, "onPageFinished()" );

                loading.setVisibility( View.GONE );

                timeout = false;
            }

            public void onPageStarted( WebView webview, String s, Bitmap bitmap ){
                super.onPageStarted( webview, s, bitmap );

                Log.d( TAG, "onPageStarted()" );
                loading.setVisibility( View.VISIBLE );

                Runnable run = new Runnable() {
                    public void run() {

                        Log.e( TAG, "Timeout" );
                        if( timeout ) {

                            wv_welcome_screen.loadUrl( "file:///android_asset/local_welcome/index.html" );

                        }
                    }
                };
                Handler myHandler = new Handler( Looper.myLooper() );
                myHandler.postDelayed( run, timeout_interval );

            }

            public void onReceivedError( WebView view, int errorCode, String description, String failingUrl ) {
                Log.e( TAG, "error "+description );
                wv_welcome_screen.loadUrl( "file:///android_asset/local_welcome/index.html" );
            }

        });
        wv_welcome_screen.loadUrl( UtilURL.getWebserviceURL() + "?what_do_you_want=url_forward&url_type=welcome_screen&mac_address="+ UtilNetwork.getMacAddress( context ) );
        //Log.d( null, UtilURL.getWebserviceURL() + "?what_do_you_want=url_forward&url_type=welcome_screen&mac_address="+ UtilNetwork.getMacAddress( context ) );
        //setContentView( wv_welcome_screen );

        //setIsWelcomeScreenShown( true );
    }

    private void configureVideoView(){
        Log.d( TAG, "configureVideoView()" );
        showVideoViewScreen();

        // Pull the Welcome Screen data from the CMS
        AsyncFetchWelcomeTextVideo fetchWelcomeText = new AsyncFetchWelcomeTextVideo();
        fetchWelcomeText.execute();

    }

    private void configureNativeView(){
        Log.d( TAG, "configureNativeView()" );

        // Pull the Welcome Screen data from the CMS
        AsyncFetchWelcomeTextNative fetchWelcomeText = new AsyncFetchWelcomeTextNative();
        fetchWelcomeText.execute();


    }











    private void showWebViewScreen(){
        hideEverything();

        rl_webview.setVisibility( View.VISIBLE );
    }

    private void showNativeWelcomeScreen(){
        hideEverything();

        rl_native_welcome.setVisibility( View.VISIBLE );
    }

    private void showVideoViewScreen(){
        hideEverything();

        rl_video_screen.setVisibility( View.VISIBLE );
    }

    private void showCustomScreen(){
        hideEverything();

        rl_custom_welcome.setVisibility( View.VISIBLE );
    }

    private void hideEverything(){
        rl_webview.setVisibility( View.GONE );
        rl_native_welcome.setVisibility( View.GONE );
        rl_video_screen.setVisibility( View.GONE );
        rl_custom_welcome.setVisibility( View.GONE );
    }


    class AsyncFetchWelcomeTextNative extends AsyncTask< String, Integer, String >{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility( View.VISIBLE );
        }

        @Override
        protected String doInBackground( String... params ) {
            String url = UtilURL.getWebserviceURL();
            Log.d( TAG, "Webservice path : "+url );
            String response = UtilNetwork.makeRequestForData( url, "POST",
                    // UtilURL.getURLParamsFromPairs( new String[][]{ { "what_do_you_want", "get_native_welcome_text" },
                    UtilURL.getURLParamsFromPairs( new String[][]{ { "what_do_you_want", "get_welcome_screen_meta" },
                            { "mac_address", UtilNetwork.getMacAddress( context ) } } ) );

            return response;
        }

        String zh, ja;

        @Override
        protected void onPostExecute( String result ) {
            super.onPostExecute( result );

            Log.d( TAG,  "inside onPostExecute()" );

            if( result != null ){
                Log.i( TAG,  result );
                try {
                    JSONArray jsonArray = new JSONArray( result );
                    JSONObject jsonObject = jsonArray.getJSONObject( 0 );
                    String type = jsonObject.getString( "type" );
                    String info = jsonObject.getString( "info" );
                    if( type.equals( "error" ) ){
                        Log.e( TAG, info );
                        return;
                    }

                    JSONObject welcomeTexts = new JSONObject( info );
                    String background = welcomeTexts.getString( "background_url" ) + "?x=" + Math.random();
                    String logo = welcomeTexts.getString( "logo_url" );
                    Log.d( TAG, background );

                    Picasso.get()
                            .load( UtilURL.getCMSRootPath() + File.separator + background )
                            .resize( 1920, 1080 )
                            .into( iv_native_bg );

                    Picasso.get()
                            .load( UtilURL.getCMSRootPath() + File.separator + logo )
                            .resize( 183, 150 )
                            .into( iv_hotel_logo );

                    JSONObject languages = new JSONObject( welcomeTexts.getString( "languages" ) );
                    // Log.d( TAG, languages.toString() );

                    final String en = languages.getString( "en" );
                    boolean have_chinese = true;
                    boolean have_japanese = true;
                    try {
                        zh = languages.getString("zh");
                    }
                    catch ( Exception e1 ){
                        have_chinese = false;
                    }

                    try {
                        ja = languages.getString("ja");
                    }
                    catch ( Exception e1 ){
                        have_japanese = false;
                    }

                    String defaultLanguage = UtilShell.executeShellCommandWithOp( "getprop language_code" );
                    defaultLanguage = defaultLanguage.trim();
                    if( defaultLanguage.equals( "" ) ){
                        defaultLanguage = "en";
                    }

                    String defaultMessage = "";

                    try{
                        defaultMessage = languages.getString( defaultLanguage );
                    }
                    catch ( Exception e ){
                        defaultMessage = languages.getString( "en" );
                    }

                    tv_native_welcome_text.setText( defaultMessage );

                    Log.d( TAG, String.valueOf( have_chinese ) );
                    if( !have_chinese ){
                        bt_native_language_en.setVisibility( View.GONE );
                        bt_native_language_zh.setVisibility( View.GONE );
                    }

                    Log.d( TAG, "en : " + en );
                    Log.d( TAG, "zh : " + zh );

                    if( defaultLanguage.equals( "en" ) )
                        bt_native_language_en.requestFocus();
                    else if ( defaultLanguage.equals( "zh" ) )
                        bt_native_language_zh.requestFocus();
                    else if ( defaultLanguage.equals( "ja" ) )
                        bt_native_language_ja.requestFocus();


                    bt_native_language_en.setOnFocusChangeListener( new View.OnFocusChangeListener() {

                        @Override
                        public void onFocusChange( View v, boolean hasFocus ) {
                            if( hasFocus ){
                                tv_native_welcome_text.setText( en );
                            }
                        }

                    });
                    bt_native_language_zh.setOnFocusChangeListener( new View.OnFocusChangeListener() {

                        @Override
                        public void onFocusChange( View v, boolean hasFocus ) {
                            if( hasFocus ){
                                tv_native_welcome_text.setText( zh );
                            }
                        }

                    });
                    bt_native_language_ja.setOnFocusChangeListener( new View.OnFocusChangeListener() {

                        @Override
                        public void onFocusChange( View v, boolean hasFocus ) {
                            if( hasFocus ){
                                tv_native_welcome_text.setText( ja );
                            }
                        }

                    });

                    View.OnClickListener buttonClick = new View.OnClickListener(){

                        @Override
                        public void onClick( View v ) {
                            if( v.getId() == R.id.bt_native_language_en ){
                                UtilShell.executeShellCommandWithOp( "setprop language_code en" );
                            }
                            else{
                                UtilShell.executeShellCommandWithOp( "setprop language_code zh" );
                            }
                            configurationReader = ConfigurationReader.reInstantiate();
                            //recreate();
                            context.sendBroadcast( new Intent( "receive_outside_update_launcher_config " ) );
                            context.sendBroadcast( new Intent( "receive_update_launcher_config" ) );

                            //UtilShell.executeShellCommandWithOp( "input keyevent " + KeyEvent.KEYCODE_DPAD_CENTER );
                            hideEverything();
                            loading.setVisibility( View.VISIBLE );
                            /*
                            * Code to exit the Welcome Screen
                            *
                            * */
                        }
                    };
                    bt_native_language_en.setOnClickListener( buttonClick );
                    bt_native_language_zh.setOnClickListener( buttonClick );

                    showNativeWelcomeScreen();
                    loading.setVisibility( View.GONE );

                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
            else{
                Log.e( TAG, "Null was returned !" );
            }

        }
    }

    class AsyncFetchWelcomeTextVideo extends AsyncTask< String, Integer, String >{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility( View.VISIBLE );
        }

        @Override
        protected String doInBackground( String... params ) {
            String url = UtilURL.getWebserviceURL();
            Log.d( TAG, "Webservice path : "+url );
            String response = UtilNetwork.makeRequestForData( url, "POST",
                    UtilURL.getURLParamsFromPairs( new String[][]{ { "what_do_you_want", "get_native_welcome_text" },
                            { "mac_address", UtilNetwork.getMacAddress( context ) } } ) );

            return response;
        }

        @Override
        protected void onPostExecute( String result ) {
            super.onPostExecute( result );

            Log.d( TAG,  "inside onPostExecute()" );

            if( result != null ){
                //Log.i( TAG,  result );
                try {
                    JSONArray jsonArray = new JSONArray( result );
                    JSONObject jsonObject = jsonArray.getJSONObject( 0 );
                    String type = jsonObject.getString( "type" );
                    String info = jsonObject.getString( "info" );
                    if( type.equals( "error" ) ){
                        Log.e( TAG, info );
                        return;
                    }

                    JSONObject welcomeTexts = new JSONObject( info );

                    final String en = welcomeTexts.getString( "en" );
                    final String zh = welcomeTexts.getString( "zh" );

                    tv_native_welcome_text_vs.setText( en );

                    bt_native_language_vs_en.setOnFocusChangeListener( new View.OnFocusChangeListener() {

                        @Override
                        public void onFocusChange( View v, boolean hasFocus ) {
                            if( hasFocus ){
                                tv_native_welcome_text_vs.setText( en );
                            }
                        }

                    });
                    bt_native_language_vs_zh.setOnFocusChangeListener( new View.OnFocusChangeListener() {

                        @Override
                        public void onFocusChange( View v, boolean hasFocus ) {
                            if( hasFocus ){
                                tv_native_welcome_text_vs.setText( zh );
                            }
                        }

                    });

                    Picasso.get()
                            .load( UtilURL.getCMSRootPath() + File.separator + "templates/mulia/images/logo.png" )
                            .resize( 159, 100 )
                            .into( iv_hotel_logo_vs );

                    View.OnClickListener buttonClick = new View.OnClickListener(){

                        @Override
                        public void onClick( View v ) {
                            if( v.getId() == R.id.bt_native_language_vs_en ){
                                UtilShell.executeShellCommandWithOp( "setprop language_code en" );
                            }
                            else{
                                UtilShell.executeShellCommandWithOp( "setprop language_code zh" );
                            }
                            configurationReader = ConfigurationReader.reInstantiate();
                            //recreate();
                            context.sendBroadcast( new Intent( "receive_outside_update_launcher_config " ) );
                            context.sendBroadcast( new Intent( "receive_update_launcher_config" ) );

                            //UtilShell.executeShellCommandWithOp( "input keyevent " + KeyEvent.KEYCODE_DPAD_CENTER );
                            hideEverything();
                            loading.setVisibility( View.VISIBLE );
                            /*
                             * Code to exit the Welcome Screen
                             *
                             * */
                        }
                    };
                    bt_native_language_vs_en.setOnClickListener( buttonClick );
                    bt_native_language_vs_zh.setOnClickListener( buttonClick );

                    showVideoViewScreen();

                    Uri uri = Uri.fromFile( new File( "/mnt/sdcard/welcome.mp4" ) );
                    vv_video_bg.setVideoURI( uri );
                    vv_video_bg.setOnPreparedListener( new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared( MediaPlayer mediaPlayer ) {
                            mediaPlayer.setLooping( true );
                        }

                    });
                    vv_video_bg.start();

                    bt_native_language_vs_en.requestFocus();

                    loading.setVisibility( View.GONE );

                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
            else{
                Log.e( TAG, "Null was returned !" );
            }

        }
    }

    class AsyncFetchWelcomeMeta extends AsyncTask< String, Integer, String >{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility( View.VISIBLE );
        }

        @Override
        protected String doInBackground( String... params ) {
            String url = UtilURL.getWebserviceURL();
            Log.d( TAG, "Webservice path : "+url );
            String response = UtilNetwork.makeRequestForData( url, "POST",
                    UtilURL.getURLParamsFromPairs( new String[][]{ { "what_do_you_want", "get_welcome_screen_meta" },
                            { "mac_address", UtilNetwork.getMacAddress( context ) } } ) );

            return response;
        }

        @Override
        protected void onPostExecute( String result ) {
            super.onPostExecute( result );

            Log.d( TAG,  "inside onPostExecute()" );

            if( result != null ){
                Log.i( TAG,  result );
                try {
                    JSONArray jsonArray = new JSONArray( result );
                    JSONObject jsonObject = jsonArray.getJSONObject( 0 );
                    String type = jsonObject.getString( "type" );
                    String info = jsonObject.getString( "info" );
                    if( type.equals( "error" ) ){
                        Log.e( TAG, info );
                        return;
                    }

                    JSONObject welcomeMeta = new JSONObject( info );

                    //setCustomXML( welcomeMeta );




                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
            else{
                Log.e( TAG, "Null was returned !" );
            }

        }
    }

    private void configureCustomView(){
        Log.d( TAG, "configureCustomView()" );
        showCustomScreen();

        // Pull the Welcome Screen data from the CMS
        //AsyncFetchWelcomeTextNative fetchWelcomeText = new AsyncFetchWelcomeTextNative();
        //fetchWelcomeText.execute();

        // Pull the Welcome Screen Metadata from the CMS
        AsyncFetchWelcomeMeta fetchWelcomeMeta = new AsyncFetchWelcomeMeta();
        fetchWelcomeMeta.execute();
    }





}
