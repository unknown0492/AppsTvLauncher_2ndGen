package com.excel.appstvlauncher.secondgen;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.excel.configuration.ConfigurationReader;
import com.excel.configuration.LauncherJSONReader;
import com.excel.customitems.CustomItems;
import com.excel.excelclasslibrary.UtilFile;
import com.excel.excelclasslibrary.UtilMisc;
import com.excel.excelclasslibrary.UtilShell;
import com.excel.flipper.Flipper;
import com.excel.imagemanipulator.DigitalSignage;
import com.excel.imagemanipulator.DigitalSignageHolder;
import com.excel.perfecttime.PerfectTimeService;
import com.excel.yahooweather.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

import static com.excel.configuration.Constants.PATH_LAUNCHER_CONFIG_FILE;
import static com.excel.configuration.Constants.PATH_LAUNCHER_CONFIG_FILE_SYSTEM;

public class MainActivity extends Activity {

	final static String TAG = "MainActivity";
	TextView tv_first;
	ScrollTextView tv_collar_text;
	TextView tv_collar_text1;
	HorizontalScrollView hsv_menu, hsv_sub_menu;
	LinearLayout ll_main_menu_items, ll_sub_menu_items, ll_clock_time;

	ImageView iv_menu_left_fade, iv_menu_right_fade;

	String main_menu_values[], sub_menu_values[];
	MenuAdapter ma;
	SubMenuAdapter sma;

	Context context = this;

	boolean main_menu_last_element_reached = false;
	boolean main_menu_first_element_reached = false;
	boolean sub_menu_last_element_reached = false;
	boolean sub_menu_first_element_reached = false;

	int last_index_of_main_menu = 0;

	long current_timestamp;

	RelativeLayout rl_elements, rl_launcher_bg, rl_tethering_info;

	boolean areLauncherElementsHidden = false;

	BroadcastReceiver perfectTimeReceiver;

	TextView tv_clock_hours, tv_clock_minutes, tv_date, tv_day_name, tv_ssid, tv_tethering_password;

	String clock_hours, clock_minutes, clock_seconds, clock_date, clock_month, clock_year;

	boolean isDateShowingOnClock = true;

	VirussTimer launcher_idle_timer;
	Runnable launcher_idle_runnable;


	//ImageView iv_weather;
	AnimatedGifImageView iv_weather;
	TextView tv_temperature, tv_text;
	Weather weather;
	RelativeLayout rl_clock;
	LinearLayout rl_weather, rl_hotel_logo;

	DigitalSignageHolder digitalSignageHolder;
	/* boolean isDefaultWallpaperActive = true;
    boolean isDigitalSignageSwitcherStarted = false;
    boolean isDigitalSignageSwitcherPaused = false;*/
	DigitalSignage ds;

	//Timer digitalSignageSwitcher;
	VirussTimer ssid_password_flipper;
	Runnable ssid_password_runnable;
	boolean isSSIDShowing = true;

	ConfigurationReader configurationReader;

	String launcher_config_json = "";

	LauncherJSONReader ljr;
	BroadcastReceiver launcherConfigUpdateReceiver;
	Flipper clock_weather_hotel_logo_flipper;

	Stack<String> key_combination = new Stack<String>();
	static String Z = "KEYCODE_Z";
	static String K = "KEYCODE_K";
	static String X = "KEYCODE_X";
	static String P = "KEYCODE_P";
	static String O = "KEYCODE_O";
	static String ONE = "KEYCODE_1";
	static String THREE = "KEYCODE_3";
	static String NINE = "KEYCODE_9";
	static String DOT = "KEYCODE_PERIOD";
	String ALPHABET = "KEYCODE_";

	RelativeLayout activity_main;
	LinearLayout first_main_item = null;
	LinearLayout current_main_item = null;
	LinearLayout prev_main_item = null;
	Handler test_handler = new Handler();


	@Override
	protected void onCreate( Bundle savedInstanceState )  {
		super.onCreate( savedInstanceState );
		hideActionBar();

		setContentView( R.layout.activity_main );

		init();
	}

    /* Launcher Menu Items Related Functions */

    AsyncTask<Void, Void, Void> initLauncherMenuItems = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... voids) {
            Handler hh = new Handler(Looper.getMainLooper() );
            //hh.getLooper().prepare();
            hh.post(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLauncherMenuItems();
                        }
                    });

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            setMainMenuAdapter(ma);
            setSubMenuAdapter(sma);
        }
    };


	@SuppressWarnings("deprecation")
	public void init(){

		configurationReader = ConfigurationReader.getInstance();

		initViews();

		/*new Handler().post(new Runnable() {
			@Override
			public void run() {
				setLauncherMenuItems();
				setMainMenuAdapter(ma);
				setSubMenuAdapter(sma);
			}
		});*/


        initLauncherMenuItems.execute();


		createLauncheritemsUpdateBroadcast();
		startPerfectTimeService();
		createPerfectTimeReceiver();
		startClockTicker();
		startDateAndDayNameSwitcher();
		initializeWeatherFeatures();
		initializeClockWeatherHotelLogoFlipper();
		checkIfHotelLogoToBeDisplayed();
		restoreTvChannels();
		startScreenCastService();

		ds.resumeDigitalSignageSwitcher();

        /*startTetheringInfoSwitcher();
		weather.resumeYahooWeatherService();
		clock_weather_hotel_logo_flipper.startClockWeatherLogoFlipper();*/


		//restoreYoutubeSettings();

	}

	final Handler unzipTVHandler = new Handler( Looper.getMainLooper() );
	AsyncTask<Void, Void, Void> unzipTVAsync = new AsyncTask<Void, Void, Void>() {

	    @Override
        protected Void doInBackground(Void... voids) {
            unzipTvChannelsZip();
            String pid = UtilShell.executeShellCommandWithOp( "pidof com.android.dtv" );
            UtilShell.executeShellCommandWithOp( "kill "+pid );
            startScreenCastService();
            return null;
        }
    };


	public void setMainMenuAdapter(final MenuAdapter adapter) {
		for (int i = 0; i < ma.getCount(); i++) {
			//Log.d( TAG, "Setting main adapter " + i );
			LinearLayout v = (LinearLayout) ma.getView( i, null, null );
			ll_main_menu_items.addView( v );
			if ( i == 0 ) {
				this.first_main_item = v;
			}
			v.setOnFocusChangeListener( new View.OnFocusChangeListener() {

			    public void onFocusChange( View v, boolean hasFocus ) {
                    LinearLayout ll = (LinearLayout) v;
                    TextView tv = (TextView) ll.findViewById( R.id.tv_menu_item_name );

                    scrollCenter( ll, hsv_menu );

                    main_menu_last_element_reached = (Integer.parseInt( v.getTag().toString() ) == adapter.getCount() - 1)?true:false;
                    main_menu_first_element_reached = (Integer.parseInt( v.getTag().toString() ) == 0)?true:false;
                    sub_menu_first_element_reached = false;
                    sub_menu_last_element_reached = false;

                    prev_main_item = current_main_item;

                    if( hasFocus ){
                        // This is to prevent animation when going back to main menu parent from its child sub menu
                        if( prev_main_item == ll )
                            return;

                        Log.d( null, "focus gained on "+tv.getText().toString() );
                        tv.setTextColor( context.getResources().getColor( R.color.light_blue ) );
                        tv.setScaleX( 1.45f );
                        tv.setScaleY( 1.45f );

                        current_main_item = ll;

                        // Hide its sub-menu
                        ObjectAnimator oa = ObjectAnimator.ofFloat( hsv_sub_menu, "translationY", 0 );
                        oa.setDuration( 0 );
                        oa.start();

                        ObjectAnimator oa1 = ObjectAnimator.ofFloat( hsv_sub_menu, "alpha", 1.0f, 0.0f );
                        oa1.setDuration( 0 );
                        oa1.start();


                        new AsyncTask<Void, Void, Void>(){

                            @Override
                            protected Void doInBackground(Void... voids) {
                                sub_menu_values = ljr.getSubMenuItemNames( last_index_of_main_menu );
                                for( int i = 0 ; i < sub_menu_values.length ; i++ ){
                                    //String item_name = ljr.getSubItemValue( last_index_of_main_menu, i , "item_name_translated" );
                                    String item_name_json = ljr.getSubItemValue( last_index_of_main_menu, i, "item_name_translated" );
                                    try {
                                        JSONObject jso = new JSONObject( item_name_json );
                                        //Log.d( TAG, "display name : "+UtilMisc.getCustomLocaleLanguageConstant().getLanguage() );
                                        sub_menu_values[ i ] = jso.getString( UtilMisc.getCustomLocaleLanguageConstant().getLanguage() );
                                    }
                                    catch ( Exception e ){
                                        //e.printStackTrace();
                                    }
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                new Handler().postDelayed( new Runnable() {

                                    @Override
                                    public void run() {

                                        sma = new SubMenuAdapter( R.layout.sub_menu_items, context, sub_menu_values );
                                        setSubMenuAdapter( sma );

                                        // Show its Sub-Menu
                                        ObjectAnimator oaa = ObjectAnimator.ofFloat( hsv_sub_menu, "translationY", 40 );
                                        //oaa.setStartDelay( 250 );
                                        oaa.setDuration( 0 );
                                        oaa.start();

                                        ObjectAnimator oaa1 = ObjectAnimator.ofFloat( hsv_sub_menu, "alpha", 0.0f, 1.0f );
                                        //oaa1.setStartDelay( 250 );
                                        oaa1.setDuration( 0 );
                                        oaa1.start();

                                    }
                                }, 250 );

                                super.onPostExecute(aVoid);
                            }
                        }.execute();

                        last_index_of_main_menu = Integer.parseInt( v.getTag().toString() );
                    }
                    else{
                        // Log.d( null, "focus lost from "+tv.getText().toString() );
                        tv.setTextColor( context.getResources().getColor( R.color.white ) );
                        tv.setScaleX( 1.0f );
                        tv.setScaleY( 1.0f );
                        //tv.setBackground( context.getResources().getDrawable( R.drawable.submenu_bg ) );
                        tv.setBackground( null );
                        // Save the state of this View

                    }

				}
			});
			v.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
                    processMainMenuItemClick( Integer.parseInt( v.getTag().toString() ) );
				}
			});
		}
        first_main_item.requestFocus();
        TextView tv = (TextView) first_main_item.findViewById( R.id.tv_menu_item_name );
        tv.setTextColor( context.getResources().getColor( R.color.light_blue ) );
        tv.setScaleX( 1.45f );
        tv.setScaleY( 1.45f );
	}

	public void setSubMenuAdapter(final SubMenuAdapter adapter) {

        ll_sub_menu_items.removeAllViews();
        for( int i = 0 ; i < adapter.getCount(); i++ ){
            LinearLayout v = (LinearLayout) adapter.getView( i, null, null );
            ll_sub_menu_items.addView( v );

            v.setOnFocusChangeListener( new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange( View v, boolean hasFocus ) {
                    LinearLayout ll = (LinearLayout) v;
                    TextView tv = (TextView) ll.findViewById( R.id.tv_sub_menu_item_name );

                    // scrollCenter( ll, hsv_sub_menu );

                    sub_menu_last_element_reached = (Integer.parseInt( v.getTag().toString() ) == adapter.getCount() - 1)?true:false;
                    sub_menu_first_element_reached = (Integer.parseInt( v.getTag().toString() ) == 0)?true:false;
                    main_menu_first_element_reached = false;
                    main_menu_last_element_reached = false;

                    if( hasFocus ){
                        Log.d( null, "focus gained on "+tv.getText().toString() );
                        tv.setBackground( context.getResources().getDrawable( R.drawable.button_focus ) );
                    }
                    else{
                        // Log.d( null, "focus lost from "+tv.getText().toString() );
                        //tv.setTextColor( context.getResources().getColor( R.color.white ) );
                        tv.setBackground( context.getResources().getDrawable( R.drawable.submenu_bg1 ) );

                    }
                }
            });

            v.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View v ) {
                    int sub_menu_index = Integer.parseInt( v.getTag().toString() );
                    processSubMenuItemClick( last_index_of_main_menu, sub_menu_index );
                }
            });
        }
	}

	public void scrollCenter( LinearLayout ll, View viewToScroll ) {
		// Source : http://stackoverflow.com/questions/8642677/reduce-speed-of-smooth-scroll-in-scroll-view
		int endPos    = (int) ll.getX();
		int halfWidth = (int) ll.getWidth() / 2;

		ObjectAnimator.ofInt( viewToScroll, "scrollX",  endPos + halfWidth - viewToScroll.getWidth() / 2 ).setDuration( 500 ).start();
	}

	public void setLauncherMenuItems() {
        /**
         *
         * Algorithm
         *
         * 1. If /mnt/sdcard/appstv_data/launcher_config.json exist
         *    2. Read content of launcher_config.json into String variable
         *    3. Initialize LauncherJSONReader instance from the launcher_config.json content
         *    4. Read main_items_count i.e. Total number of main menu items in the JSON
         *    5. Run a Loop main_items_count times
         *       6.  Read sub_items_count i.e. Total number of sub items under main item specified by loop iteration count
         *       7.  If sub_items_count == 0 i.e. Main Menu Item does not have Sub Items
         *           8. If item_type == "app"
         *              9. { ~~~ }
         *       10. If sub_items_count > 0
         *           11.
         *
         *
         *
         *
         */
        File configuration_file = new File( Environment.getExternalStorageDirectory() + File.separator + PATH_LAUNCHER_CONFIG_FILE );
        String launcher_config_json = "";
        // Step-1
        if( configuration_file.exists() ) {
            // Step-2
            launcher_config_json = UtilFile.readData(configuration_file);
        }
        else{
            launcher_config_json = UtilFile.readData( new File( PATH_LAUNCHER_CONFIG_FILE_SYSTEM ) );
        }

        // Step-3
        ljr = new LauncherJSONReader( launcher_config_json );

        // Step-4
        int main_items_count = ljr.getMainItemsCount();

        setCollarText( ljr );

        // Step-5
        int sub_items_count;
		/*configurationReader = ConfigurationReader.reInstantiate();
		String hotspot_enabled = configurationReader.getHotspotEnabled();
		if( hotspot_enabled.equals( "0" ) )
			main_menu_values = new String[ main_items_count-1 ];
		else*/
        main_menu_values = new String[ main_items_count ];

        for( int i = 0, j=0 ; i < main_items_count ; i++ ){
            //sub_items_count = ljr.getSubItemsCount( i );
            // Log.d( TAG, "sub_items_count : "+sub_items_count );
            //j = i;
            // Step-6
            sub_items_count = ljr.getSubItemsCount( i );
            //Log.d( TAG, "sub items count : "+sub_items_count );

            // Step-7
            if( sub_items_count == 0 ){

                // Step-8
                String item_type = ljr.getMainItemValue( i, "item_type" );
                if( item_type.equals( "app" ) ){

                }
            }
			/*else if( ljr.getMainItemValue( i, "item_type" ).equals( "expandable-hotspot" ) ){

				if( hotspot_enabled.equals( "0" ) ){
					continue;
				}
			}*/
            String item_name = ljr.getMainItemValue( i, "item_name" );
            String item_name_json = ljr.getMainItemValue( i, "item_name_translated" );
            try {
                Log.d( TAG, item_name_json );
                JSONObject jso = new JSONObject( item_name_json );
                //Log.d( TAG, "display name : "+UtilMisc.getCustomLocaleLanguageConstant().getLanguage() );
                item_name = jso.getString( UtilMisc.getCustomLocaleLanguageConstant().getLanguage() );
            }
            catch ( Exception e ){
                item_name = ljr.getMainItemValue( i, "item_name" );
                e.printStackTrace();
            }
            main_menu_values[ i ] = item_name;

        }
        ma = new MenuAdapter( R.layout.main_menu_items, this, main_menu_values );
		/*setMainMenuAdapter(this.ma);
		setSubMenuAdapter(this.sma);*/
	}

	public void createLauncheritemsUpdateBroadcast(){

		launcherConfigUpdateReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive( Context context, Intent intent ) {
				Log.i( TAG, "New Launcher JSON Downloaded" );
				//setLauncherMenuItems();
				//PerfectTimeService pts = new PerfectTimeService();
				//pts.sendUpdateToClock();
				//startPerfectTimeService();
				// System.gc();

				recreate();
				/*Intent in = getIntent();
				in.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finish();
				startActivity( in );*/
			}
		};

		LocalBroadcastManager.getInstance( context ).registerReceiver( launcherConfigUpdateReceiver, new IntentFilter( "update_launcher_config" ) );
	}

	public void deleteLauncheritemsUpdateBroadcast(){
		LocalBroadcastManager.getInstance( this).unregisterReceiver( launcherConfigUpdateReceiver );
	}

	public void processMainMenuItemClick( int index ){
		// Check item_type for this index
		String item_type = ljr.getMainItemValue( index, "item_type" );

		if( item_type.equals( "app" ) ){
			if( !UtilMisc.startApplicationUsingPackageName( context, ljr.getMainItemValue( index, "package_name" ) ) ){
				//showCustomToast( "error", "Failed to Open Application !", 3000 );
				return;
			}
			else{
				//showCustomToast( "success", "Launching information, please wait...", 3000 );
				return;
			}
		}
		else if( item_type.equals( "part" ) ){
			String metadata = ljr.getMainItemValue( index, "metadata" );
			try {
				JSONArray jsa = new JSONArray( metadata );
				JSONObject jso = jsa.getJSONObject( 0 );
				String activity = jso.getString( "activity_name" ).trim();
				Intent in = new Intent( context, Class.forName( getPackageName() + "." + activity )  );
				startActivity( in );
			}
			catch ( Exception e ){
				CustomItems.showCustomToast( context, "error", "Cant open World Clock !", 3000 );
				//e.printStackTrace();
			}
		}
		if( item_type.equals( "web_view" ) ){
			Log.d( TAG, ljr.getMainItemValue( index, "web_view_url" ) + "-" +ljr.getMainItemValue( index, "params" ) );
			startWebViewActivity( ljr.getMainItemValue( index, "web_view_url" ),
					ljr.getMainItemValue( index, "params" ) );
			return;
		}
    	/*else if( item_type.equals( "expandable-hotspot" ) ){
    		configurationReader = ConfigurationReader.reInstantiate();
    		String is_hotspot_enabled = configurationReader.getHotspotEnabled();
    		if( is_hotspot_enabled.equals( "0" ) ){
    			showCustomToast( "error", "Hotspot is Unavailable !", 3000 );
    			return;
    		}
    	}*/
	}

	public void processSubMenuItemClick( int main_menu_item_index, int sub_item_index ){
		// Check item_type for this main_menu_item_index, sub_item_index
		String item_type = ljr.getSubItemValue( main_menu_item_index, sub_item_index, "item_type" );
		String clickable = ljr.getSubItemValue( main_menu_item_index, sub_item_index, "clickable" );

		if( item_type.equals( "app" ) ){
			if( !UtilMisc.startApplicationUsingPackageName( context, ljr.getSubItemValue( main_menu_item_index, sub_item_index, "package_name" ) ) ){
				//showCustomToast( "error", "Failed to Open Application !", 3000 );
				return;
			}
			else{
				//showCustomToast( "success", "Launching application, please wait...", 3000 );
				return;
			}
		}
		else if( clickable.equals( "true" ) ){
			// Log.d( TAG, "Item is clickable" );
			if( item_type.equals( "web_view" ) ){
				startWebViewActivity( ljr.getSubItemValue( main_menu_item_index, sub_item_index, "web_view_url" ),
						ljr.getSubItemValue( main_menu_item_index, sub_item_index, "params" ) );
				return;
			}
			else if( item_type.equals( "language" ) ){
				String metadata = ljr.getSubItemValue( main_menu_item_index, sub_item_index, "metadata" );
				try {
					JSONArray jsa = new JSONArray( metadata );
					JSONObject jso = jsa.getJSONObject( 0 );
					String language_code = jso.getString( "language_code" ).trim();
					UtilShell.executeShellCommandWithOp( "setprop language_code "+language_code );
					recreate();
				} catch ( JSONException e ) {
					e.printStackTrace();
				}

				//Log.d( TAG, "metadata : "+metadata );
				return;
			}
            else if( item_type.equals( "part" ) ){
                String metadata = ljr.getSubItemValue( main_menu_item_index, sub_item_index, "metadata" );
                try {
                    JSONArray jsa = new JSONArray( metadata );
                    JSONObject jso = jsa.getJSONObject( 0 );
                    String activity = jso.getString( "activity_name" ).trim();
                    Intent in = new Intent( context, Class.forName( getPackageName() + "." + activity )  );
                    startActivity( in );
                }
                catch ( Exception e ){
                    CustomItems.showCustomToast( context, "error", "Cant open World Clock !", 3000 );
                    //e.printStackTrace();
                }
            }
		}
	}

	public void showCustomToast( String type, String text, int duration ){
		Toast t = new Toast( context );
		LayoutInflater lf = (LayoutInflater) context.getSystemService( context.LAYOUT_INFLATER_SERVICE );
		int resId = -1;

		if( type.equals( "success" ) ){
			resId = R.layout.toast_success;
		}
		else if( type.equals( "error" ) ){
			resId = R.layout.toast_error;
		}
		else if( type.equals( "warning" ) ){
			resId = R.layout.toast_warning;
		}

		RelativeLayout rl = (RelativeLayout) lf.inflate( resId, null );
		TextView tv = (TextView) rl.findViewById( R.id.tv_toast_text );

		tv.setText( text );
		t.setView( rl );
		t.setDuration( duration );
		t.setGravity( Gravity.BOTTOM | Gravity.END, 20, 0 );
		t.show();
	}

	public void startWebViewActivity( String web_view_url, String params ){
		Intent in = new Intent( context, WebViewActivity.class );
		in.putExtra( "web_view_url", web_view_url );

		in.putExtra( "params", params + ",language_code" );
		Log.d( TAG, params );
		startActivity( in );
	}

	public void setCollarText( LauncherJSONReader ljr ){
		// Set Collar Text
		String collar_text = ljr.getCollarText();
		String collar_text_translated = ljr.getCollarTextTranslated();

		try{
			JSONObject jso = new JSONObject( collar_text_translated );
			collar_text = jso.getString( UtilMisc.getCustomLocaleLanguageConstant().getLanguage() );
		}
		catch ( Exception e ){
			e.printStackTrace();
			collar_text = ljr.getCollarText();
		}

		//tv_collar_text1.setText( "          " + collar_text );
		//tv_collar_text1.setText( "          Welcome to the James Cook Hotel Grand Chancellor" );
		//Animation marquee = AnimationUtils.loadAnimation( this, R.anim.marquee );
		//tv_collar_text1.startAnimation(marquee);
		//tv_collar_text1.setSelected( true );
		tv_collar_text.setText( collar_text );
		tv_collar_text.setSpeed( new Double( configurationReader.getCollarTextSpeed() ) );
		tv_collar_text.startScroll();
		//tv_collar_text.setMovementMethod(new ScrollingMovementMethod());
	}

    /* Launcher Menu Items Related Functions */




	@Override
	public boolean onKeyDown( int i, KeyEvent keyevent ){
		String key_name = KeyEvent.keyCodeToString( i );
		Log.i( TAG, "KeyPressed : "+i+","+key_name );
		//if( ( i == 19 ) || ( i == 20 ) || ( i == 21 ) || ( i == 22 ) )
		//	return true;

		// Handle the Overflow left and right key movements for MAIN menu
		//if( handleMainMenuOverflow( i, keyevent ) ) return true;

		// Handle the Overflow left and right key movements for SUB menu
		//if( handleSubMenuOverflow( i, keyevent ) ) return true;

		// When on Sub menu, and Up is pressed, Move the focus back to Sub-Menu's Parent
		if( handleSubMenuToMainMenuFocus( i, keyevent ) ) return true;

		// When on Main menu, and Down is pressed, Move the focus back to Sub-Menu's First Item
		if( handleMainMenuToSubMenuFocus( i, keyevent ) ) return true;

		// Short-Cut key toggling
		shortCutKeyMonitor( key_name );

		if (i != 4) {
			return super.onKeyDown(i, keyevent);
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "insde onPause()");


		pauseTetheringInfoFlipper();
		weather.pauseYahooWeatherService();
		clock_weather_hotel_logo_flipper.pauseClockWeatherLogoFlipper();

		pauseLauncherIdleTimer();
	}


	long access_onresume_time = -1;

	@Override
	protected void onResume() {
		super.onResume();

		startTetheringInfoSwitcher();
		weather.resumeYahooWeatherService();
		clock_weather_hotel_logo_flipper.startClockWeatherLogoFlipper();

        if ( ! isLoadingCompleted() ) {
            //setIsLoadingCompleted( true );
            showLoadingActivity();
        }
		else {

			if (access_onresume_time == -1) {
				access_onresume_time = System.currentTimeMillis();
			} else {
				long now = System.currentTimeMillis();
				long diff = now - access_onresume_time;
				int sec = (int) diff / 1000;
				Log.d(TAG, "sec : " + sec);
				if (sec <= 10) {
					access_onresume_time = now;
					//return;
				} else {
					access_onresume_time = now;

                    unzipTvChannelsZip();
                    String pid = UtilShell.executeShellCommandWithOp( "pidof com.android.dtv" );
                    UtilShell.executeShellCommandWithOp( "kill "+pid );

                    startScreenCastService();

					onUserInteraction();




					// UtilShell.executeShellCommandWithOp( "am force-stop com.google.android.youtube.tv" );
				}
			}
		}
        //onUserInteraction();






		/*startTetheringInfoSwitcher();
		//weather.resumeYahooWeatherService();
		clock_weather_hotel_logo_flipper.startClockWeatherLogoFlipper();

		if ( ! isLoadingCompleted() ) {
			//setIsLoadingCompleted( true );
			showLoadingActivity();
		}
		else {

			if (access_onresume_time == -1) {
				access_onresume_time = System.currentTimeMillis();
			} else {
				long now = System.currentTimeMillis();
				long diff = now - access_onresume_time;
				int sec = (int) diff / 1000;
				Log.d(TAG, "sec : " + sec);
				if (sec <= 15) {
					access_onresume_time = now;
					//return;
				} else {
					access_onresume_time = now;


					// restoreTvChannels();

					onUserInteraction();

					startScreenCastService();

					// UtilShell.executeShellCommandWithOp( "am force-stop com.google.android.youtube.tv" );
				}
			}
		}*/
		Log.d( TAG, "insde onResume()" );


		//configurationReader = ConfigurationReader.reInstantiate();


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		/*deletePerfectTimeReceiver();
		this.weather.deleteYahooWeatherReceiver();
		deleteLauncheritemsUpdateBroadcast();
		this.clock_weather_hotel_logo_flipper.deleteHotelLogoAvailabilityReceiver();*/
	}


	/**********************************************************
	 * Not so important functions, or used only for one time
	 *
	 */

	public void hideActionBar(){
		//android.support.v7.app.ActionBar ab = getSupportActionBar();
        /*ActionBar ab = getActionBar();
        ab.hide();*/
	}

	public void initViews(){
		this.hsv_menu = (HorizontalScrollView) findViewById(R.id.hsv_menu);
		this.iv_menu_left_fade = (ImageView) findViewById(R.id.iv_menu_left_fade);
		this.iv_menu_right_fade = (ImageView) findViewById(R.id.iv_menu_right_fade);
		this.ll_main_menu_items = (LinearLayout) findViewById(R.id.ll_main_menu_items);
		this.main_menu_values = new String[]{"Live TV", "Information", "Settings", "Movies", "Games", "WiFi"};
		this.sub_menu_values = new String[]{""};
		this.sma = new SubMenuAdapter(R.layout.sub_menu_items, context, this.sub_menu_values);
		this.tv_collar_text = (ScrollTextView) findViewById(R.id.tv_collar_text);
		//tv_collar_text1 = (TextView) findViewById(R.id.tv_collar_text1 );
		this.hsv_sub_menu = (HorizontalScrollView) findViewById(R.id.hsv_sub_menu);
		this.ll_sub_menu_items = (LinearLayout) findViewById(R.id.ll_sub_menu_items);
		this.rl_elements = (RelativeLayout) findViewById(R.id.rl_elements);
		this.tv_clock_hours = (TextView) findViewById(R.id.tv_clock_hours);
		this.tv_clock_minutes = (TextView) findViewById(R.id.tv_clock_minutes);
		this.tv_date = (TextView) findViewById(R.id.tv_date);
		this.tv_day_name = (TextView) findViewById(R.id.tv_day_name);
		this.iv_weather = (AnimatedGifImageView) findViewById(R.id.iv_weather);
		this.tv_temperature = (TextView) findViewById(R.id.tv_temperature);
		this.tv_text = (TextView) findViewById(R.id.tv_text);
		this.rl_weather = (LinearLayout) findViewById(R.id.rl_weather);
		this.rl_clock = (RelativeLayout) findViewById(R.id.rl_clock);
		this.ll_clock_time = (LinearLayout) findViewById(R.id.ll_clock_time);
		this.rl_launcher_bg = (RelativeLayout) findViewById(R.id.rl_launcher_bg);
		this.tv_ssid = (TextView) findViewById(R.id.tv_ssid);
		this.tv_tethering_password = (TextView) findViewById(R.id.tv_tethering_password);
		this.rl_tethering_info = (RelativeLayout) findViewById(R.id.rl_tethering_info);
		this.rl_hotel_logo = (LinearLayout) findViewById(R.id.rl_hotel_logo);
		this.ds = new DigitalSignage(context, this.rl_launcher_bg);
	}

	public boolean handleMainMenuOverflow( int i, KeyEvent keyevent ){
		// When Last Child in the MAIN menu is reached, and right is pressed, then focus moves back to First child
		if( ( main_menu_last_element_reached ) && ( i == 22 ) ){  // 22 -> Right
			int endPos    = (int) ll_main_menu_items.getChildAt( 0 ).getX();
			int halfWidth = (int) ll_main_menu_items.getChildAt( 0 ).getWidth() / 2;

			ObjectAnimator.ofInt( hsv_menu, "scrollX",  endPos + halfWidth - hsv_menu.getWidth() / 2 ).setDuration( 750 ).start();
			new Handler().postDelayed( new Runnable() {

				@Override
				public void run() {
					ll_main_menu_items.getChildAt( 0 ).requestFocus();
				}
			}, 750 );

			return true;
		}
		// When First Child in the MAIN menu is reached, and left is pressed, then focus moves back to Last child
		if( ( main_menu_first_element_reached ) && ( i == 21 ) ){  // 21 -> Left
			int endPos    = (int) ll_main_menu_items.getChildAt( ma.getCount() - 1 ).getX();
			int halfWidth = (int) ll_main_menu_items.getChildAt( ma.getCount() - 1 ).getWidth() / 2;

			ObjectAnimator.ofInt( hsv_menu, "scrollX",  endPos + halfWidth - hsv_menu.getWidth() / 2 ).setDuration( 750 ).start();
			new Handler().postDelayed( new Runnable() {

				@Override
				public void run() {
					ll_main_menu_items.getChildAt( ma.getCount() - 1 ).requestFocus();
				}
			}, 750 );

			return true;
		}

		return false;
	}

	public boolean handleSubMenuOverflow( int i, KeyEvent keyevent ){
		// When Last Child in the Submenu is reached, and right is pressed, then focus moves back to First child
		if( ( sub_menu_last_element_reached ) && ( i == 22 ) ){  // 22 -> Right
			ll_sub_menu_items.getChildAt( 0 ).requestFocus();
			return true;
		}
		// When First Child in the Submenu is reached, and left is pressed, then focus moves back to Last child
		if( ( sub_menu_first_element_reached ) && ( i == 21 ) ){  // 21 -> Left
			ll_sub_menu_items.getChildAt( sma.getCount() - 1 ).requestFocus();
			return true;
		}

		return false;
	}

	public boolean handleSubMenuToMainMenuFocus( int i, KeyEvent keyevent ){
		if( i == 19 ){
			ll_main_menu_items.getChildAt( last_index_of_main_menu ).requestFocus();
			return false;
		}
		return false;
	}

	public boolean handleMainMenuToSubMenuFocus( int i, KeyEvent keyevent ){
		if( i == 20 ){
			try {
				ll_sub_menu_items.getChildAt(0).requestFocus();

				TextView tv = (TextView) current_main_item.findViewById( R.id.tv_menu_item_name );
				tv.setTextColor( context.getResources().getColor( R.color.light_blue ) );
				tv.setScaleX( 1.45f );
				tv.setScaleY( 1.45f );
			}
			catch ( Exception e ){
				Log.e( TAG, "This main menu has no Sub Menu :-((" );
				//current_main_item.requestFocus();
				//UtilShell.executeShellCommandWithOp( "input keyevent 19" );
				dispatchKeyEvent(new KeyEvent( KeyEvent.ACTION_UP, KeyEvent.ACTION_UP));
				return true;
			}
			return false;
		}
		return false;
	}


	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		Log.d( null, "onUserInteraction()" );

		current_timestamp = System.currentTimeMillis();

		pauseLauncherIdleTimer();

		if (areLauncherElementsHidden) {
			ObjectAnimator.ofFloat(rl_elements, "alpha", 0.0f, 1.0f).setDuration(500).start();
			areLauncherElementsHidden = false;
			// startLauncherIdleTimer();
		} else {
			startLauncherIdleTimer();
		}
	}



	public void startLauncherIdleTimer(){
		Log.d( null, "startLauncherIdleTimer()" );

		launcher_idle_timer = new VirussTimer( 10000 );
		launcher_idle_runnable = new Runnable() {

			@Override
			public void run() {
				// Log.d( null, "startLauncherIdleTimer() inside run() every "+(TEN_SECONDS_MILLIS/1000)+" seconds" );

				long now = System.currentTimeMillis();
				long difference = ( now - current_timestamp )/1000;

				Log.d( TAG, String.format( "now : %d, current : %d, difference : %d, n-c : %d", now, current_timestamp, difference, ( now - current_timestamp ) ) );

				if( difference > Integer.parseInt( configurationReader.getIdleTimeoutInterval() )/1000 ){
					Log.d( null, "difference > " );
					ObjectAnimator.ofFloat( rl_elements, "alpha", 1.0f, 0.0f ).setDuration( 500 ).start();
					areLauncherElementsHidden = true;
				}
				else{
					startLauncherIdleTimer();
				}

			}
		};


    	//new Handler().postDelayed( launcher_idle_runnable, TEN_SECONDS_MILLIS );
		launcher_idle_timer.start( launcher_idle_runnable );
	}

	public void pauseLauncherIdleTimer(){
		if( launcher_idle_timer != null )
			launcher_idle_timer.stop( launcher_idle_runnable );
		Log.d( TAG, "pauseLauncherIdleTimer()" );
	}

	public void startPerfectTimeService(){
		Intent in = new Intent( context, PerfectTimeService.class );
		startService( in );
	}

	public void createPerfectTimeReceiver(){
		perfectTimeReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive( Context context, Intent intent ) {
				// Get extra data included in the Intent
				clock_hours = intent.getStringExtra( "getHours()" );
				clock_minutes = intent.getStringExtra( "getMinutes()" );
				clock_seconds = intent.getStringExtra( "getSeconds()" );
				clock_date = intent.getStringExtra( "getDate()" );
				clock_month = intent.getStringExtra( "getMonth()" );
				clock_year = intent.getStringExtra( "getYear()" );

				Log.d( "receiver", "Got message: " );
				tv_clock_hours.setText( clock_hours );
				tv_clock_minutes.setText( clock_minutes );
				String date_string = "";
				//date_string = clock_date + " " + (new SimpleDateFormat( "MMM" )).format( Calendar.getInstance().getTime() ) + ", " + clock_year;
				date_string = clock_date + " " + Calendar.getInstance().getDisplayName( Calendar.MONTH, Calendar.SHORT, UtilMisc.getCustomLocaleLanguageConstant() ) + ", " + clock_year;
				//Toast.makeText( context, "" + Calendar.getInstance().getDisplayName( Calendar.MONTH, Calendar.SHORT, Locale.CHINESE ) + "" , Toast.LENGTH_SHORT ).show();
				tv_date.setText( date_string );
				//tv_day_name.setText( ( new SimpleDateFormat( "EEEE" )).format( Calendar.getInstance().getTime() ) );
				tv_day_name.setText( Calendar.getInstance().getDisplayName( Calendar.DAY_OF_WEEK, Calendar.LONG, UtilMisc.getCustomLocaleLanguageConstant() ) );
			}

		};

		LocalBroadcastManager.getInstance( this ).registerReceiver( perfectTimeReceiver, new IntentFilter( "send_update_to_clock" ) );
	}

	public void deletePerfectTimeReceiver(){
		LocalBroadcastManager.getInstance( this).unregisterReceiver( perfectTimeReceiver );
	}

	public void startClockTicker(){
		new Handler().postDelayed( new Runnable() {

			@Override
			public void run(){
				Calendar cal = Calendar.getInstance();

				if( clock_seconds == null )
					clock_seconds = "0";

				int minute, hours, seconds, month, year;
				String date_string = "";
				String date = "";

				/*if( ( seconds = cal.get( Calendar.SECOND ) ) != Integer.parseInt( tv_clock_minutes.getText().toString() ) )
					tv_clock_minutes.setText( (seconds<10)?"0"+seconds:seconds+"" );*/
				if( ( minute = cal.get( Calendar.MINUTE ) ) != Integer.parseInt( tv_clock_minutes.getText().toString() ) )
					tv_clock_minutes.setText( (minute<10)?"0"+minute:minute+"" );
				if( ( hours = cal.get( Calendar.HOUR_OF_DAY ) ) != Integer.parseInt( tv_clock_hours.getText().toString() ) )
					tv_clock_hours.setText( (hours<10)?"0"+hours:hours+"" );
				date = (cal.get( Calendar.DATE )<10)?"0"+cal.get( Calendar.DATE ):cal.get( Calendar.DATE )+"";
				year = cal.get( Calendar.YEAR );
				//date_string = date + " " + (new SimpleDateFormat( "MMM" )).format( cal.getTime() ) + ", " + year;
				date_string = date + " " + Calendar.getInstance().getDisplayName( Calendar.MONTH, Calendar.SHORT, UtilMisc.getCustomLocaleLanguageConstant() ) + ", " + year;
				tv_date.setText( date_string );
				//tv_day_name.setText( ( new SimpleDateFormat( "EEEE" )).format( cal.getTime() ) );
				tv_day_name.setText( Calendar.getInstance().getDisplayName( Calendar.DAY_OF_WEEK, Calendar.LONG, UtilMisc.getCustomLocaleLanguageConstant() ) );

				startClockTicker();

			}
		}, 1000*60 );
	}

	public void startDateAndDayNameSwitcher(){
		// configurationReader = ConfigurationReader.reInstantiate();
		new Handler().postDelayed( new Runnable() {

			@Override
			public void run() {
				if( isDateShowingOnClock ){
					//tv_date.animate().rotationXBy( 90f ).setDuration( 300 ).withEndAction(new Runnable() {

					tv_date.animate().alpha( 0.0f ).setDuration( 300 ).start();
					tv_day_name.animate().alpha(1.0f).setDuration(300).start();

					//ObjectAnimator.ofFloat( tv_date, "rotationX", 0.0f, 90f ).setDuration( 1500 ).start();

				}
				else{
					// ObjectAnimator.ofFloat( tv_day_name, "rotationX", 0.0f, 90f ).setDuration( 1500 ).start();
					//tv_day_name.animate().rotationXBy( 90f ).setDuration( 300 ).withEndAction(new Runnable() {
					tv_day_name.animate().alpha( 0.0f ).setDuration( 300 ).start();
					tv_date.animate().alpha( 1.0f ).setDuration( 300 ).start();

					// ObjectAnimator.ofFloat( tv_date, "rotationX", 270f, 360f ).setDuration( 1500 ).start();
				}
				isDateShowingOnClock = !isDateShowingOnClock;
				startDateAndDayNameSwitcher();
			}
		}, Long.parseLong( configurationReader.getDateTimeFlipInterval() ) );


	}

	public void startTetheringInfoSwitcher(){
		Log.d( TAG, "startTetheringInfoSwitcher()" );
		tv_tethering_password.setRotationX( 0f );
		tv_tethering_password.setAlpha( 0.0f );
		//tv_text.setTextSize( 18 );

		String hotspot_enabled = configurationReader.getHotspotEnabled();
		Log.d( TAG, "Hotspot Enabled : "+hotspot_enabled );
		if( hotspot_enabled.equals( "1" ) ) {
			tv_ssid.setText("WiFi : " + configurationReader.getSSID());
			tv_tethering_password.setText("Password : " + configurationReader.getHotspotPassword());

			rl_tethering_info.setVisibility( View.VISIBLE );

			ssid_password_flipper = new VirussTimer( Long.parseLong( configurationReader.getTetheringInfoFlipInterval() ) );
			ssid_password_runnable = new Runnable() {

				@Override
				public void run() {

					if (isSSIDShowing) {
						tv_ssid.animate().alpha( 0.0f ).setDuration(300).withEndAction(new Runnable() {
							//tv_ssid.animate().rotationXBy(90f).setDuration(300).withEndAction(new Runnable() {
							@Override
							public void run() {
								//ObjectAnimator.ofFloat( tv_day_name, "rotationX", 270f, 360f ).setDuration( 300 ).start();
								//tv_tethering_password.animate().rotationXBy(-90f).setDuration(300);
								tv_tethering_password.animate().alpha( 1.0f ).setDuration( 300 );
							}
						}).start();
						//ObjectAnimator.ofFloat( tv_date, "rotationX", 0.0f, 90f ).setDuration( 1500 ).start();

					} else {
						tv_tethering_password.animate().alpha( 0.0f ).setDuration( 300 ).withEndAction(new Runnable() {
							//tv_tethering_password.animate().rotationXBy(90f).setDuration(300).withEndAction(new Runnable() {
							@Override
							public void run() {
								// ObjectAnimator.ofFloat( tv_date, "rotationX", 270f, 360f ).setDuration( 300 ).start();
								//tv_ssid.animate().rotationXBy(-90f).setDuration(300).start();
								tv_ssid.animate().alpha( 1.0f ).setDuration( 300 );
							}
						}).start();
						// ObjectAnimator.ofFloat( tv_day_name, "rotationX", 0.0f, 90f ).setDuration( 1500 ).start();
						// ObjectAnimator.ofFloat( tv_date, "rotationX", 270f, 360f ).setDuration( 1500 ).start();
					}

					isSSIDShowing = !isSSIDShowing;
					ssid_password_flipper.start(ssid_password_runnable);

				}

			};
			ssid_password_flipper.start( ssid_password_runnable );
		}
		else{
			rl_tethering_info.setVisibility( View.INVISIBLE );
		}


	}

	public void pauseTetheringInfoFlipper(){
		if( ssid_password_flipper != null )
			ssid_password_flipper.stop( ssid_password_runnable );
		Log.d( TAG, "pauseTetheringInfoFlipper()" );
	}

	public void initializeWeatherFeatures(){
		weather = new Weather( context, configurationReader, iv_weather, tv_temperature, tv_text );

		weather.startYahooWeatherService();
		weather.createYahooWeatherReceiver();
	}

	public void initializeClockWeatherHotelLogoFlipper(){
		clock_weather_hotel_logo_flipper = new Flipper( context, configurationReader, ll_clock_time, rl_weather, rl_hotel_logo, tv_temperature, tv_text );
		clock_weather_hotel_logo_flipper.createHotelLogoAvailabilityReceiver();
	}


	public void shortCutKeyMonitor( String key_name ){
		key_combination.push( key_name );

		if( key_combination.size() == 3 ){
			String key_3 = key_combination.pop();
			String key_2 = key_combination.pop();
			String key_1 = key_combination.pop();

			// Z-K-Z
			if( key_1.equals( Z ) && key_2.equals( K ) && key_3.equals( Z ) ){
				Intent in = new Intent( context, ShortcutsActivity.class );
				in.putExtra( "who", "zkz" );
				startActivity( in );
			}
			// X-K-X
			else if( key_1.equals( X ) && key_2.equals( K ) && key_3.equals( X ) ){
				Intent in = new Intent( context, ShortcutsActivity.class );
				in.putExtra( "who", "xkx" );
				startActivity( in );
			}

			// 1-.-1  -> ZKZ
			if( key_1.equals( ONE ) && key_2.equals( DOT ) && key_3.equals( ONE ) ){
				Intent in = new Intent( context, ShortcutsActivity.class );
				in.putExtra( "who", "zkz" );
				startActivity( in );
			}
			// 3-1-3  -> XKX
			else if( key_1.equals( THREE ) && key_2.equals( ONE ) && key_3.equals( THREE ) ){
				Intent in = new Intent( context, ShortcutsActivity.class );
				in.putExtra( "who", "xkx" );
				startActivity( in );
			}
			// 9-1-9
			else if( key_1.equals( NINE ) && key_2.equals( ONE ) && key_3.equals( NINE ) ){
				UtilShell.executeShellCommandWithOp( "reboot" );
			}
			// P-O-P  -> Refresh Launcher
			else if( key_1.equals( P ) && key_2.equals( O ) && key_3.equals( P ) ){
				recreate();
			}
			// 9.9  -> Refresh Launcher
			else if( key_1.equals( NINE ) && key_2.equals( DOT ) && key_3.equals( NINE ) ){
				recreate();
			}
			key_combination.removeAllElements();
		}
	}
    
    
    /*
     * Not so important functions, or used only for one time
     * 
     *********************************************************/

	public void startScreenCastService(){
		if( configurationReader.getAirplayEnabled().equals( "1" ) )
			UtilShell.executeShellCommandWithOp( "am startservice -n com.waxrain.airplaydmr/com.waxrain.airplaydmr.WaxPlayService" );
	}

	private void checkIfHotelLogoToBeDisplayed(){

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				Log.d( TAG, "checkIfHotelLogoToBeDisplayed()" );
				String hasHotelLogoDisplay = configurationReader.getHasHotelLogoDisplay();
				File hotel_logo_file = new File( configurationReader.getHotelLogoDirectoryPath() + File.separator + "hotel_logo.png" );
				if( hasHotelLogoDisplay.equals( "1" ) ){

					if( hotel_logo_file.exists() ){
						Log.d( TAG, "Hotel logo exist and is displayable" );
						Flipper.isHotelLogoAvailable = true;
						DigitalSignage.setImageFromPathOnView( hotel_logo_file.getAbsolutePath(), rl_hotel_logo );
					}

				}
			}
		});

	}

	public static void setIsLoadingCompleted( boolean is_it ){
		String s = (is_it)?"1":"0";
		UtilShell.executeShellCommandWithOp( "setprop is_loading_complete " + s );
	}

	public static boolean isLoadingCompleted(){
		String s = UtilShell.executeShellCommandWithOp( "getprop is_loading_complete" ).trim();
		return s.equals( "1" )?true:false;
	}

	private void showLoadingActivity(){
		Intent intent = new Intent( context, LoadingActivity.class );
		startActivity( intent );
		overridePendingTransition( 0, 0 );
	}

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open("com.google.android.youtube.tv.zip");
            File outFile = new File("/mnt/sdcard/com.google.android.youtube.tv.zip");
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + "com.google.android.youtube.tv.zip", e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
        }
        /*if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(getExternalFilesDir(null), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }*/
        /*InputStream stream = null;
        OutputStream output = null;

        try {
            for (String fileName : this.getAssets().list( "youtube_new" ) ) {
                Log.d( "ADA", fileName );
                stream = this.getAssets().open( "youtube_new/" + fileName);
                output = new BufferedOutputStream( new FileOutputStream( this.getFilesDir() + "/newDirectory/" + fileName));

                byte data[] = new byte[1024];
                int count;

                while ((count = stream.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                stream.close();

                //stream = null;
                //output = null;
            }
        }
        catch ( Exception e ){
            e.printStackTrace();
        }*/
    }
    public void moveAssetToStorageDir(String path){
        File file = getExternalFilesDir(null);
        String rootPath = file.getPath() + "/" + path;
        try{
            String [] paths = getAssets().list(path);
            for(int i=0; i<paths.length; i++){
                if(paths[i].indexOf(".")==-1){
                    File dir = new File(rootPath + paths[i]);
                    dir.mkdir();
                    moveAssetToStorageDir(paths[i]);
                }else {
                    File dest = null;
                    InputStream in = null;
                    if(path.length() == 0) {
                        dest = new File(rootPath + paths[i]);
                        in = getAssets().open(paths[i]);
                    }else{
                        dest = new File(rootPath + "/" + paths[i]);
                        in = getAssets().open(path + "/" + paths[i]);
                    }
                    dest.createNewFile();
                    FileOutputStream out = new FileOutputStream(dest);
                    byte [] buff = new byte[in.available()];
                    in.read(buff);
                    out.write(buff);
                    out.close();
                    in.close();
                }
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

	public void restoreYoutubeSettings(){
		// This works differently  for 5.1 and 6.0, so we differentiate it
		// Permissions for Android 6.0
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
			Log.d( TAG, "Android is 6+" );

			// Kill Youtube running in background
			String pid = UtilShell.executeShellCommandWithOp("pidof com.google.android.youtube.tv").trim();
			UtilShell.executeShellCommandWithOp("kill " + pid);

			// Kill again, as it starts automatically after first killing
			pid = UtilShell.executeShellCommandWithOp("pidof com.google.android.youtube.tv").trim();
			UtilShell.executeShellCommandWithOp("kill " + pid);

			// 1. Check if youtube package exist com.google.android.youtube.tv
			File file = new File( "/data/data/com.google.android.youtube.tv" );
			String s_file = UtilShell.executeShellCommandWithOp("[ -d \"/data/data/com.google.android.youtube.tv\" ] && echo \"yes\"");
			Log.d( TAG, s_file );
			//if( ! file.exists() )
			if (!s_file.trim().equals("yes"))
				return;
			Log.i(TAG, "/data/data/com.google.android.youtube.tv : exist");

			// 2. Remove all data from /data/data/com.google.android.youtube.tv
			UtilShell.executeShellCommandWithOp( "rm -r /data/data/com.google.android.youtube.tv/*" );


			// Copy the youtube settings from assets folder to sdcard
            copyAssets();
            // Unzip the youtube backup file
            UtilShell.executeShellCommandWithOp( "mkdir /mnt/sdcard", "rm -r /mnt/sdcard/com.google.android.youtube.tv/*",
                    "unzip -o /mnt/sdcard/com.google.android.youtube.tv.zip -d /mnt/sdcard" );

            //moveAssetToStorageDir( "" );


			// 3. Copy the default data for youtube from /system/appstv_data/com.google.android.youtube.tv
			//UtilShell.executeShellCommandWithOp( "cp -r /system/appstv_data/com.google.android.youtube.tv/* /data/data/com.google.android.youtube.tv" );
			UtilShell.executeShellCommandWithOp( "cp -r /mnt/sdcard/com.google.android.youtube.tv/* /data/data/com.google.android.youtube.tv" );

			// 4. CHMOD -R 777 to make it executable, just in case
			UtilShell.executeShellCommandWithOp( "chmod -R 777 /data/data/com.google.android.youtube.tv" );
		}
		else {
			Log.d( TAG, "Android is below 6" );

			// Kill Youtube running in background
			String pid = UtilShell.executeShellCommandWithOp("pidof com.google.android.youtube.tv").trim();
			UtilShell.executeShellCommandWithOp("kill " + pid);

			// 1. Check if youtube package exist com.google.android.youtube.tv
			File file = new File("/data/data/com.google.android.youtube.tv");
			String s_file = UtilShell.executeShellCommandWithOp("[ -d \"/data/data/com.google.android.youtube.tv\" ] && echo \"yes\"");
			Log.d(TAG, s_file);
			//if( ! file.exists() )
			if (!s_file.trim().equals("yes"))
				return;
			Log.i(TAG, "/data/data/com.google.android.youtube.tv : exist");

			// 2. Check if the Directory shared_prefs exist inside com.google.android.youtube.tv
			File file1 = new File("/data/data/com.google.android.youtube.tv/shared_prefs");
			String s_file1 = UtilShell.executeShellCommandWithOp("[ -d \"/data/data/com.google.android.youtube.tv/shared_prefs\" ] && echo \"yes\"");
			Log.d(TAG, s_file1);
			//if( ! file1.exists() ){
			if (!s_file1.trim().equals("yes")) {
				Log.e(TAG, "/data/data/com.google.android.youtube.tv/shared_prefs : Not exist, hence creating it");
				UtilShell.executeShellCommandWithOp("chmod -R 777 /data/data/com.google.android.youtube.tv");
				UtilShell.executeShellCommandWithOp("mkdir /data/data/com.google.android.youtube.tv/shared_prefs");
			}

			// 3. Check if the youtube.xml exist at /system/appstv_data/youtube.xml
			File file2 = new File("/system/appstv_data/youtube.xml");
			String s_file2 = UtilShell.executeShellCommandWithOp("[ -f \"/system/appstv_data/youtube.xml\" ] && echo \"yes\"");
			if (!s_file2.trim().equals("yes")) {
				// if( ! file2.exists() ){
				Log.e(TAG, "/system/appstv_data/youtube.xml : not exist, hence exiting !");
				return;
			}

			// 4. Copy /system/appstv_data/youtube.xml TO /data/data/com.google.android.youtube.tv/shared_prefs/youtube.xml
			UtilShell.executeShellCommandWithOp("chmod -R 777 /data/data/com.google.android.youtube.tv");
			UtilShell.executeShellCommandWithOp("chmod -R 777 /data/data/com.google.android.youtube.tv/shared_prefs");
			UtilShell.executeShellCommandWithOp("chmod 777 /data/data/com.google.android.youtube.tv/shared_prefs/youtube.xml");

			UtilShell.executeShellCommandWithOp("chmod -R 777 /system/appstv_data");
			UtilShell.executeShellCommandWithOp("chmod -R 777 /system/appstv_data/youtube.xml");

			UtilShell.executeShellCommandWithOp("rm /data/data/com.google.android.youtube.tv/shared_prefs/youtube.xml");
			UtilShell.executeShellCommandWithOp("cp /system/appstv_data/youtube.xml /data/data/com.google.android.youtube.tv/shared_prefs/youtube.xml");

			Log.i(TAG, "restored youtube.xml");

			// 5. Delete Google Account Database
			UtilShell.executeShellCommandWithOp("chmod -R 777 /data/system/users/0");
			UtilShell.executeShellCommandWithOp("rm /data/system/users/0/accounts.db");
			UtilShell.executeShellCommandWithOp("rm /data/system/users/0/accounts.db-journal");

			UtilShell.executeShellCommandWithOp("am force-stop com.google.android.youtube.tv");
		}
	}



    /* TV Channels restore related functions BEGINS */

	public boolean isTvChannelRestored(){
		String is_it = UtilShell.executeShellCommandWithOp( "getprop is_tv_ch_restored" ).trim();
		return ( is_it.equals( "0" ) || is_it.equals( "" ) )?false:true;
	}

	public void setTvChannelRestored( boolean is_it ){
		String s = ( is_it )?"1":"0";
		UtilShell.executeShellCommandWithOp( "setprop is_tv_ch_restored " + s );
	}

	public void unzipTvChannelsZip(){

        Log.i( TAG, "unzipTvChannelsZip() executed" );

        UtilShell.executeShellCommandWithOp( "rm -r /mnt/sdcard/appstv_data/tv_channels/backup",
                "unzip -o /mnt/sdcard/appstv_data/tv_channels/tv_channels.zip -d /mnt/sdcard/appstv_data/tv_channels" );

        // 1. kill com.android.dtv
        String pid = UtilShell.executeShellCommandWithOp( "pidof com.android.dtv" );
        //UtilShell.executeShellCommandWithOp( "kill "+pid );

        UtilShell.executeShellCommandWithOp( "chmod -R 777 /data/hdtv",
                "rm -r /data/hdtv/*",
                "cp -r /mnt/sdcard/appstv_data/tv_channels/backup/hdtv/* /data/hdtv",
                "chmod -R 777 /data/hdtv" );

        // last. kill com.android.dtv
        pid = UtilShell.executeShellCommandWithOp( "pidof com.android.dtv" );
        //UtilShell.executeShellCommandWithOp( "kill "+pid );

        setTvChannelRestored( true );
        Log.d( TAG, "tv_channels.zip extracted successfully" );
	    /*
		Log.i( TAG, "unzipTvChannelsZip() executed" );

		UtilShell.executeShellCommandWithOp( "rm -r /mnt/sdcard/appstv_data/tv_channels/backup",
				"unzip -o /mnt/sdcard/appstv_data/tv_channels/tv_channels.zip -d /mnt/sdcard/appstv_data/tv_channels" );

		// Compare the file program.db
		File programdb_internal = new File( "/data/hdtv/program.db" );
		File programdb_sdcard	= new File( "/mnt/sdcard/appstv_data/tv_channels/backup/hdtv/program.db" );


		if( !programdb_sdcard.exists() ) {
			Log.e( TAG, "Restore terminated, program.db file does not exist on sdcard" );
			setTvChannelRestored( true );
			return;
		}

		try {

			String md5_internal = "";
			String md5_sdcard 	= MD5.getMD5Checksum( programdb_sdcard );

			if( !programdb_internal.exists() ) {
				md5_internal = "xxxxx";
				Log.d( TAG, "Internal program.db does not exist" );
				//return;
			}
			else{
				md5_internal = MD5.getMD5Checksum( programdb_internal );
			}

			Log.d( TAG, md5_internal + "," +md5_sdcard );

			// If the two files do not match, then restore
			//if( ! md5_internal.equals( md5_sdcard ) ) {


				// 1. kill com.android.dtv
				//String pid = UtilShell.executeShellCommandWithOp( "pidof com.android.dtv" );
				//UtilShell.executeShellCommandWithOp( "kill "+pid );



				UtilShell.executeShellCommandWithOp( "chmod -R 777 /data/hdtv",
						//"rm -r /data/hdtv/*",
						"rm /data/hdtv/program.db",
						"cp /mnt/sdcard/appstv_data/tv_channels/backup/hdtv/program.db /data/hdtv/program.db",
						"chmod -R 777 /data/hdtv" );



				UtilShell.executeShellCommandWithOp( "chmod -R 777 /data/hdtv",
						"rm -r /data/hdtv/*",
						"cp /mnt/sdcard/appstv_data/tv_channels/backup/hdtv/blackpackagefilter.xml /data/hdtv/blackpackagefilter.xml",
						"cp /mnt/sdcard/appstv_data/tv_channels/backup/hdtv/dtv_pass.xml /data/hdtv/dtv_pass.xml",
						"cp /mnt/sdcard/appstv_data/tv_channels/backup/hdtv/epg.db /data/hdtv/epg.db",
						"cp /mnt/sdcard/appstv_data/tv_channels/backup/hdtv/program.db /data/hdtv/program.db",
						"chmod -R 777 /data/hdtv" );


				Log.d( TAG, "tv_channels.zip extracted successfully" );

				// last. kill com.android.dtv
				//pid = UtilShell.executeShellCommandWithOp("pidof com.android.dtv");
				//UtilShell.executeShellCommandWithOp( "kill "+pid );

				Log.i( TAG, "Restored tv channels successfully !" );

				//setTvChannelRestored( true );
			}
			else{
				Log.i( TAG, "TV channels not restored because they are same !" );

			}
			setTvChannelRestored( true );
		}
		catch ( Exception e ){
			e.printStackTrace();
		}
*/

	}

	public void restoreTvChannels(){
		if( ! isTvChannelRestored() ){
		    UtilShell.executeShellCommandWithOp( "monkey -p com.excel.datagrammonitor.secondgen -c android.intent.category.LAUNCHER 1" );
			//unzipTvChannelsZip();
			restoreYoutubeSettings();
		}
	}

    /* TV Channels restore related functions ENDS */


    /* Permission related content */

	String[] permissions = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.ACCESS_WIFI_STATE,
			Manifest.permission.ACCESS_NETWORK_STATE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.INTERNET,
	};

	@Override
	public void onRequestPermissionsResult( int requestCode, String permissions[], int[] grantResults ) {
		switch ( requestCode ) {
			case 10:
			{
				if( grantResults.length > 0 && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ){
					// permissions granted.
					Log.d( TAG, grantResults.length + " Permissions granted : " );
				} else {
					String permission = "";
					for ( String per : permissions ) {
						permission += "\n" + per;
					}
					// permissions list of don't granted permission
					Log.d( TAG, "Permissions not granted : "+permission );
				}
				return;
			}
		}
	}

	private  boolean checkPermissions() {
		int result;
		List<String> listPermissionsNeeded = new ArrayList<>();
		for ( String p:permissions ) {
			result = ContextCompat.checkSelfPermission( this, p );
			if ( result != PackageManager.PERMISSION_GRANTED ) {
				listPermissionsNeeded.add( p );
			}
		}
		if ( !listPermissionsNeeded.isEmpty() ) {
			ActivityCompat.requestPermissions( this, listPermissionsNeeded.toArray( new String[ listPermissionsNeeded.size() ] ), 10 );
			return false;
		}
		return true;
	}

    /* Permission related content */

}





/*
 * Yahoo Weather APIU
 * 
 *  Client ID (Consumer Key)
    dj0yJmk9ZlFqTDhNUk1YY2tOJmQ9WVdrOVJtWlRUMGxvTm0wbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD00OA--
	
	Client Secret (Consumer Secret)
    a7f2f0a89165ada1ceacdfc0efdadd3de79bde45
    
    Time Sync From Internet Servers : http://stackoverflow.com/questions/13064750/how-to-get-current-time-from-internet-in-android
*/


