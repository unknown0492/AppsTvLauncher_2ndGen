package com.excel.appstvlauncher.secondgen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.excel.configuration.ConfigurationReader;
import com.excel.excelclasslibrary.UtilNetwork;
import com.excel.excelclasslibrary.UtilShell;

import java.io.File;

public class WebViewActivity extends Activity {

	WebView wv_open_page;
	Context context = this;
	SharedPreferences spfs;
	ConfigurationReader cr;
	
	final static String TAG = "WebViewActivity";

	boolean timeout = true;
	long timeout_interval = 10000;

	RelativeLayout rl;
	AnimatedGifImageView loading;

	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_web_view );
		
		init();
		
		Intent in = getIntent();
		String web_view_url = in.getStringExtra( "web_view_url" );
		String params = in.getStringExtra( "params" );
		
		String URL = cr.getProtocol() + "://" + cr.getCmsIp() + File.separator + cr.getCmsSubDirectory() + File.separator + "webservice.php?";
		// String url_params = String.format( "what_do_you_want=%s&web_view_url=%s&", "cms_template", web_view_url );
		String url_params = String.format( "what_do_you_want=%s&url_type=%s&", "url_forward", web_view_url );

		String params_arr[] = params.split( "," );
		for( int i = 0 ; i < params_arr.length ; i++ ){
			
			if( params_arr[ i ].equals( "mac_address" ) ){
				url_params += "mac_address=" + UtilNetwork.getMacAddress( context ) + "&";
			}
			else if( params_arr[ i ].equals( "language_code" ) ){
				String language_code = UtilShell.executeShellCommandWithOp( "getprop language_code" ).trim();
				url_params += "language_code=" + language_code + "&";
			}
			
		}
		url_params = url_params.substring( 0, url_params.length() - 1 );
		URL = URL + url_params;
		Log.i( TAG, URL );
		
		wv_open_page.loadUrl( URL );
				
		//setContentView( wv_open_page );
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		wv_open_page.clearCache( true );
		
		context.deleteDatabase( "webview.db" );
	    context.deleteDatabase( "webviewCache.db" );
	    
	    File dir = getCacheDir();

        if ( dir != null && dir.isDirectory() ){
            try{
                File[] children = dir.listFiles();
                if ( children.length > 0 ){
                    for ( int i = 0; i < children.length; i++ ){
                        File[] temp = children[ i ].listFiles();
                        for ( int x = 0; x < temp.length; x++ ){
                            temp[ x ].delete();
                        }
                    }
                }
            }
            catch ( Exception e ){
                Log.e( TAG, "failed cache clean" );
            }
        }
		Log.i( TAG, "onDestroy()" );
	}

	private void init(){

	    //rl = new RelativeLayout( this );

	    loading = (AnimatedGifImageView) findViewById( R.id.loading );
		loading.setAnimatedGif( context.getResources().getIdentifier( "drawable/small_loading1" , null, context.getPackageName() ), AnimatedGifImageView.TYPE.AS_IS );

		//wv_open_page = new WebView( this );
        wv_open_page = (WebView) findViewById( R.id.wv_open_page );

        wv_open_page.getSettings().setJavaScriptEnabled( true );
		wv_open_page.getSettings().setAppCacheEnabled( false );
		
		cr = ConfigurationReader.getInstance();
		
		final Activity activity = this;

		wv_open_page.setWebViewClient( new WebViewClient() {
        	
        	ProgressDialog p;
        	
            public void onReceivedError( WebView view, int errorCode, String description, String failingUrl ) {
            	wv_open_page.loadUrl( "file:///android_asset/maintenance/maintenance.html" );
            }

			@Override
			public void onPageStarted( WebView view, String url, Bitmap favicon ) {
				super.onPageStarted( view, url, favicon );

				Log.d( TAG, "onPageStarted()" );
				loading.setVisibility( View.VISIBLE );

				Runnable run = new Runnable() {
					public void run() {

						Log.e( TAG, "Timeout" );
						if( timeout ) {

							wv_open_page.loadUrl( "file:///android_asset/maintenance/maintenance.html" );

						}
					}
				};
				Handler myHandler = new Handler( Looper.myLooper() );
				myHandler.postDelayed( run, timeout_interval );
			}

			@Override
			public void onPageFinished( WebView view, String url ) {
				super.onPageFinished( view, url );

				Log.d( TAG, "onPageFinished()" );

				loading.setVisibility( View.GONE );

				timeout = false;
			}
            
        });
	}


	@Override
	protected void onPause() {
		super.onPause();

		Log.i( TAG, "onPause()" );

		finish();
	}

}
