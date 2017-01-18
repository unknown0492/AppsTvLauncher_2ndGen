package com.excel.appstvlauncher.secondgen;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.excel.configuration.ConfigurationReader;
import com.excel.configuration.LauncherJSONReader;
import com.excel.excelclasslibrary.UtilFile;
import com.excel.excelclasslibrary.UtilMisc;
import com.excel.excelclasslibrary.UtilShell;
import com.excel.imagemanipulator.DigitalSignage;
import com.excel.imagemanipulator.DigitalSignageHolder;
import com.excel.perfecttime.PerfectTimeService;
import com.excel.yahooweather.YahooWeatherService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Stack;

import static com.excel.appstvlauncher.secondgen.Constants.LAUNCHER_IDLE_TIMEOUT_SECONDS;
import static com.excel.appstvlauncher.secondgen.Constants.TEN_SECONDS_MILLIS;
import static com.excel.configuration.Constants.PATH_LAUNCHER_CONFIG_FILE;
import static com.excel.configuration.Constants.PATH_LAUNCHER_CONFIG_FILE_SYSTEM;

public class MainActivity extends ActionBarActivity{

	final static String TAG = "MainActivity";
	TextView tv_first;
	ScrollTextView tv_collar_text;
	HorizontalScrollView hsv_menu, hsv_sub_menu;
	LinearLayout ll_main_menu_items, ll_sub_menu_items;

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

    RelativeLayout rl_elements, rl_launcher_bg;

    boolean areLauncherElementsHidden = false;

    BroadcastReceiver perfectTimeReceiver;

    TextView tv_clock_hours, tv_clock_minutes, tv_date, tv_day_name;

    String clock_hours, clock_minutes, clock_seconds, clock_date, clock_month, clock_year;

    boolean isDateShowingOnClock = true;

    BroadcastReceiver yahooWeatherReceiver;

    ImageView iv_weather;
    TextView tv_temperature, tv_text;
    boolean isWeatherShowing = false;
    static boolean isYahooWeatherServicePaused = false;
    boolean isClockAndWeatherFlippingStarted = false;
    RelativeLayout rl_weather, rl_clock;

   	DigitalSignageHolder digitalSignageHolder;
	/* boolean isDefaultWallpaperActive = true;
    boolean isDigitalSignageSwitcherStarted = false;
    boolean isDigitalSignageSwitcherPaused = false;*/
	DigitalSignage ds;

    //Timer digitalSignageSwitcher;

    ConfigurationReader configurationReader;

    String launcher_config_json = "";

    LauncherJSONReader ljr;
	BroadcastReceiver launcherConfigUpdateReceiver;

    Stack<String> key_combination = new Stack<String>();
    static String Z = "KEYCODE_Z";
	static String K = "KEYCODE_K";
	static String X = "KEYCODE_X";

    @Override
    protected void onCreate( Bundle savedInstanceState )  {
        super.onCreate( savedInstanceState );
        hideActionBar();
        setContentView( R.layout.activity_main );

        init();
// yoaaaaa
    }

    /* Launcher Menu Items Related Functions */

	@SuppressWarnings("deprecation")
	public void init(){
		initViews();

		tv_collar_text.setText( "Tier Bar and Rooms at Sanctuary Block are undergoing renovation and should be progressively completed by August 2016." );
		tv_collar_text.startScroll();

		setLauncherMenuItems();
		createLauncheritemsUpdateBroadcast();

		startPerfectTimeService();
		createPerfectTimeReceiver();
		startClockTicker();

		startDateAndDayNameSwitcher();

		startYahooWeatherService();
		createYahooWeatherReceiver();

		configurationReader = ConfigurationReader.getInstance();
        /*Log.d( TAG, "getCountry() : "+","+configurationReader.getCountry()+"," );
        Log.d( TAG, "getTimezone() : "+","+configurationReader.getTimezone()+"," );
        Log.d( TAG, "getCmsIp() : "+","+configurationReader.getCmsIp()+"," );
        Log.d( TAG, "getLocation() : "+","+configurationReader.getLocation()+"," );
        Log.d( TAG, "getFirmwareName() : "+","+configurationReader.getFirmwareName()+"," );
        Log.d( TAG, "getIsRebootScheduled() : "+","+configurationReader.getIsRebootScheduled()+"," );
        Log.d( TAG, "getRebootTime() : "+","+configurationReader.getRebootTime()+"," );
        Log.d( TAG, "getDigitalSignageInterval() : "+","+configurationReader.getDigitalSignageInterval()+"," );*/
		startScreenCastService();

	}

    public void setMainMenuAdapter( final MenuAdapter adapter ){
    	for( int i = 0 ; i < ma.getCount(); i++ ){
    		LinearLayout v = (LinearLayout) ma.getView( i, null, null );
    		ll_main_menu_items.addView( v );

    		v.setOnFocusChangeListener( new OnFocusChangeListener() {

    			@Override
    			public void onFocusChange( View v, boolean hasFocus) {
    				LinearLayout ll = (LinearLayout) v;
    				TextView tv = (TextView) ll.findViewById( R.id.tv_menu_item_name );

    				scrollCenter( ll, hsv_menu );

    				main_menu_last_element_reached = (Integer.parseInt( v.getTag().toString() ) == adapter.getCount() - 1)?true:false;
    	    		main_menu_first_element_reached = (Integer.parseInt( v.getTag().toString() ) == 0)?true:false;
    	    		sub_menu_first_element_reached = false;
    	    		sub_menu_last_element_reached = false;

    				if( hasFocus ){
    					Log.d( null, "focus gained on "+tv.getText().toString() );
    					tv.setTextColor( context.getResources().getColor( R.color.menu_text_active_color ) );

    					// Hide its sub-menu
    					ObjectAnimator oa = ObjectAnimator.ofFloat( hsv_sub_menu, "translationY", 0 );
    					oa.setDuration( 250 );
    					oa.start();

    					ObjectAnimator oa1 = ObjectAnimator.ofFloat( hsv_sub_menu, "alpha", 1.0f, 0.0f );
    					oa1.setDuration( 250 );
    					oa1.start();

    					new Handler().postDelayed( new Runnable() {

							@Override
							public void run() {
								sub_menu_values = ljr.getSubMenuItemNames( last_index_of_main_menu );
		    					sma = new SubMenuAdapter( R.layout.sub_menu_items, context, sub_menu_values );
		    					setSubMenuAdapter( sma );
							}
						}, 250 );

    					// Show its Sub-Menu
    					ObjectAnimator oaa = ObjectAnimator.ofFloat( hsv_sub_menu, "translationY", 40 );
    					oaa.setStartDelay( 250 );
    					oaa.setDuration( 250 );
    					oaa.start();

    					ObjectAnimator oaa1 = ObjectAnimator.ofFloat( hsv_sub_menu, "alpha", 0.0f, 1.0f );
    					oaa1.setStartDelay( 250 );
    					oaa1.setDuration( 250 );
    					oaa1.start();

    					last_index_of_main_menu = Integer.parseInt( v.getTag().toString() );




    				}
    				else{
    					// Log.d( null, "focus lost from "+tv.getText().toString() );
    					tv.setTextColor( context.getResources().getColor( R.color.white ) );

    					// Save the state of this View

    				}
    			}
    		});

    		v.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick( View v ) {
					int index = Integer.parseInt( v.getTag().toString() );
					// Log.d( TAG, "Clicked : "+index );
					processMainMenuItemClick( index );
				}
			});
    	}

    }

    public void setSubMenuAdapter( final SubMenuAdapter adapter ){
    	View first_element = null, last_element = null;
    	ll_sub_menu_items.removeAllViews();
    	for( int i = 0 ; i < adapter.getCount(); i++ ){
    		LinearLayout v = (LinearLayout) adapter.getView( i, null, null );
    		ll_sub_menu_items.addView( v );

    		v.setOnFocusChangeListener( new OnFocusChangeListener() {

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
    					tv.setBackground( context.getResources().getDrawable( R.drawable.submenu_bg ) );

    				}
    			}
    		});

    		v.setOnClickListener( new OnClickListener() {

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

    public void setLauncherMenuItems(){
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

		// Step-5
		int sub_items_count;
		/*configurationReader = ConfigurationReader.reInstantiate();
		String hotspot_enabled = configurationReader.getHotspotEnabled();
		if( hotspot_enabled.equals( "0" ) )
			main_menu_values = new String[ main_items_count-1 ];
		else*/
			main_menu_values = new String[ main_items_count ];

		for( int i = 0, j=0 ; i < main_items_count ; i++ ){
			sub_items_count = ljr.getSubItemsCount( i );
			// Log.d( TAG, "sub_items_count : "+sub_items_count );
			//j = i;
			// Step-6
			sub_items_count = ljr.getSubItemsCount( i );

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

			main_menu_values[ i ] = ljr.getMainItemValue( i, "item_name" );

		}
		ma = new MenuAdapter( R.layout.main_menu_items, this, main_menu_values );



    	setMainMenuAdapter( ma );
        setSubMenuAdapter( sma );

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
    			showCustomToast( "error", "Failed to Open Application !", 3000 );
    			return;
    		}
    		else{
    			showCustomToast( "success", "Launching information, please wait...", 3000 );
    			return;
    		}
    	}
    	else if( item_type.equals( "expandable-hotspot" ) ){
    		configurationReader = ConfigurationReader.reInstantiate();
    		String is_hotspot_enabled = configurationReader.getHotspotEnabled();
    		if( is_hotspot_enabled.equals( "0" ) ){
    			showCustomToast( "error", "Hotspot is Unavailable !", 3000 );
    			return;
    		}
    	}
    }

    public void processSubMenuItemClick( int main_menu_item_index, int sub_item_index ){
    	// Check item_type for this main_menu_item_index, sub_item_index
    	String item_type = ljr.getSubItemValue( main_menu_item_index, sub_item_index, "item_type" );
    	String clickable = ljr.getSubItemValue( main_menu_item_index, sub_item_index, "clickable" );

    	if( item_type.equals( "app" ) ){
    		if( !UtilMisc.startApplicationUsingPackageName( context, ljr.getSubItemValue( main_menu_item_index, sub_item_index, "package_name" ) ) ){
    			showCustomToast( "error", "Failed to Open Application !", 3000 );
    			return;
    		}
    		else{
    			showCustomToast( "success", "Launching application, please wait...", 3000 );
    			return;
    		}
    	}
    	else if( clickable.equals( "true" ) ){
    		if( item_type.equals( "web_view" ) ){
        		startWebViewActivity( ljr.getSubItemValue( main_menu_item_index, sub_item_index, "web_view_url" ),
        				ljr.getSubItemValue( main_menu_item_index, sub_item_index, "params" ) );
        		return;
        	}
    	}
    }

    public void showCustomToast( String type, String text, int duration ){
    	Toast t = new Toast( context );
    	LayoutInflater lf = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
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
    	in.putExtra( "params", params );
    	startActivity( in );
    }
    
    /* Launcher Menu Items Related Functions */





    @Override
	public boolean onKeyDown( int i, KeyEvent keyevent ){
		String key_name = KeyEvent.keyCodeToString( i );
		Log.d( null, "KeyPressed : "+i+","+key_name );

		// Handle the Overflow left and right key movements for MAIN menu
		if( handleMainMenuOverflow( i, keyevent ) ) return true;

		// Handle the Overflow left and right key movements for SUB menu
		if( handleSubMenuOverflow( i, keyevent ) ) return true;

		// When on Sub menu, and Up is pressed, Move the focus back to Sub-Menu's Parent
		if( handleSubMenuToMainMenuFocus( i, keyevent ) ) return true;

		// When on Main menu, and Down is pressed, Move the focus back to Sub-Menu's First Item
		if( handleMainMenuToSubMenuFocus( i, keyevent ) ) return true;

		// Short-Cut key toggling
		shortCutKeyMonitor( key_name );

		return super.onKeyDown( i, keyevent );
	}

    @Override
	protected void onPause() {
		super.onPause();
		Log.d( TAG,  "insde onPause()" );

		ds.pauseDigitalSignageSwitcher();
		pauseYahooWeatherService();

	}


    @Override
	protected void onResume() {
		super.onResume();
		Log.d( TAG,  "insde onResume()" );

		startLauncherIdleTimer();

		current_timestamp = System.currentTimeMillis();

		ds.resumeDigitalSignageSwitcher();
		resumeYahooWeatherService();

	}

    @Override
	protected void onDestroy() {
		super.onDestroy();

		deletePerfectTimeReceiver();
		deleteYahooWeatherReceiver();
		deleteLauncheritemsUpdateBroadcast();
	}

	/**********************************************************
     * Not so important functions, or used only for one time
     *
     */

    public void hideActionBar(){
    	android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.hide();
    }

    public void initViews(){
    	hsv_menu = (HorizontalScrollView) findViewById( R.id.hsv_menu );
        iv_menu_left_fade = (ImageView) findViewById( R.id.iv_menu_left_fade );
        iv_menu_right_fade = (ImageView) findViewById( R.id.iv_menu_right_fade );
        ll_main_menu_items = (LinearLayout) findViewById( R.id.ll_main_menu_items );
        main_menu_values = new String[]{ "Live TV", "Information", "Settings", "Movies", "Games", "WiFi" };
        //sub_menu_values = new String[]{ "Sub Menu 1", "Sub Menu 2", "Sub Menu 3", "Sub Menu 4", "Sub Menu 5", "Sub Menu 6", "Sub Menu 7", "Sub Menu 8" };
        sub_menu_values = new String[]{ "" };
        sma = new SubMenuAdapter( R.layout.sub_menu_items, context, sub_menu_values );

        tv_collar_text = (ScrollTextView) findViewById( R.id.tv_collar_text );
        hsv_sub_menu = (HorizontalScrollView) findViewById( R.id.hsv_sub_menu );
        ll_sub_menu_items = (LinearLayout) findViewById( R.id.ll_sub_menu_items );
        rl_elements = (RelativeLayout) findViewById( R.id.rl_elements );
        tv_clock_hours = (TextView) findViewById( R.id.tv_clock_hours );
        tv_clock_minutes = (TextView) findViewById( R.id.tv_clock_minutes );
        tv_date = (TextView) findViewById( R.id.tv_date );
        tv_day_name = (TextView) findViewById( R.id.tv_day_name );
        iv_weather = (ImageView) findViewById( R.id.iv_weather );
        tv_temperature = (TextView) findViewById( R.id.tv_temperature );
        tv_text = (TextView) findViewById( R.id.tv_text );
        rl_weather = (RelativeLayout) findViewById( R.id.rl_weather );
        rl_clock = (RelativeLayout) findViewById( R.id.rl_clock );
        rl_launcher_bg = (RelativeLayout) findViewById( R.id.rl_launcher_bg );
        //digitalSignageHolder = new DigitalSignageHolder( this );
		ds = new DigitalSignage( context, rl_launcher_bg );
        //digitalSignageSwitcher = new Timer();
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
			}
			catch ( Exception e ){
				Log.e( TAG, "This main menu has no Sub Menu :-(" );
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

		if( areLauncherElementsHidden ){
			ObjectAnimator.ofFloat( rl_elements, "alpha", 0.0f, 1.0f ).setDuration( 500 ).start();
			areLauncherElementsHidden = false;
			startLauncherIdleTimer();
		}
	}

    public void startLauncherIdleTimer(){
    	// Log.d( null, "startLauncherIdleTimer()" );

    	new Handler().postDelayed( new Runnable() {

			@Override
			public void run() {
				// Log.d( null, "startLauncherIdleTimer() inside run() every "+(TEN_SECONDS_MILLIS/1000)+" seconds" );

				long now = System.currentTimeMillis();
				long difference = ( now - current_timestamp )/1000;

				if( difference > LAUNCHER_IDLE_TIMEOUT_SECONDS ){
					Log.d( null, "difference > 10" );
					ObjectAnimator.ofFloat( rl_elements, "alpha", 1.0f, 0.0f ).setDuration( 500 ).start();
					areLauncherElementsHidden = true;
				}
				else{
					startLauncherIdleTimer();
				}

			}
		}, TEN_SECONDS_MILLIS );

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
				date_string = clock_date + " " + (new SimpleDateFormat( "MMM" )).format( Calendar.getInstance().getTime() ) + ", " + clock_year;
				tv_date.setText( date_string );
				tv_day_name.setText( ( new SimpleDateFormat( "EEEE" )).format( Calendar.getInstance().getTime() ) );
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
				date_string = date + " " + (new SimpleDateFormat( "MMM" )).format( cal.getTime() ) + ", " + year;
				tv_date.setText( date_string );
				tv_day_name.setText( ( new SimpleDateFormat( "EEEE" )).format( cal.getTime() ) );

				startClockTicker();

			}
		}, 1000*60 );
    }

    public void startDateAndDayNameSwitcher(){
    	new Handler().postDelayed( new Runnable() {

			@Override
			public void run() {
				if( isDateShowingOnClock ){
		    		ObjectAnimator.ofFloat( tv_date, "alpha", 1.0f, 0.0f ).setDuration( 1500 ).start();
		    		ObjectAnimator.ofFloat( tv_day_name, "alpha", 0.0f, 1.0f ).setDuration( 1500 ).start();
		    	}
		    	else{
		    		ObjectAnimator.ofFloat( tv_day_name, "alpha", 1.0f, 0.0f ).setDuration( 1500 ).start();
		    		ObjectAnimator.ofFloat( tv_date, "alpha", 0.0f, 1.0f ).setDuration( 1500 ).start();
		    	}
				isDateShowingOnClock = !isDateShowingOnClock;
				startDateAndDayNameSwitcher();
			}
		}, 10000 );


    }



    
    /* Weather Related Functions */

    public void startYahooWeatherService(){
    	Intent in = new Intent( this, YahooWeatherService.class );
        startService( in );
    }

    public void createYahooWeatherReceiver(){
    	yahooWeatherReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive( Context context, Intent intent ) {
				Log.i( TAG, "Weather Received on Launcher" );
				String code = intent.getStringExtra( "code" );
				String temp = intent.getStringExtra( "temp" );
				String text = intent.getStringExtra( "text" );

				iv_weather.setBackgroundResource( getResources().getIdentifier( "drawable/weather_icon_"+code, null, getPackageName() ) );
			    tv_temperature.setText( temp + "°C" );
			    tv_text.setText( text );

			    // Step-6 from YahooWeatherService.class
			    if( !isClockAndWeatherFlippingStarted )
			    	startFlippingClockAndWeather();

			}
		};
    	LocalBroadcastManager.getInstance( context ).registerReceiver( yahooWeatherReceiver, new IntentFilter( "update_weather" ) );
    }

    public void deleteYahooWeatherReceiver(){
    	LocalBroadcastManager.getInstance( this).unregisterReceiver( yahooWeatherReceiver );
    }

    public void pauseYahooWeatherService(){
    	isYahooWeatherServicePaused = true;
    }

    public void resumeYahooWeatherService(){
		isYahooWeatherServicePaused = false;
    	/*if( ! isYahooWeatherServicePaused ){
    		isYahooWeatherServicePaused = false;
    	}*/
    }

    public static boolean isYahooWeatherServicePaused(){
    	return isYahooWeatherServicePaused;
    }
    
    /* Weather Related Functions */




    public void startFlippingClockAndWeather(){
    	isClockAndWeatherFlippingStarted = true;
    	new Handler().postDelayed( new Runnable() {

			@Override
			public void run() {
				if( !isWeatherShowing ){
		    		ObjectAnimator.ofFloat( rl_clock, "alpha", 1.0f, 0.0f ).setDuration( 1500 ).start();
		    		ObjectAnimator.ofFloat( rl_weather, "alpha", 0.0f, 1.0f ).setDuration( 1500 ).start();
		    	}
		    	else{
		    		ObjectAnimator.ofFloat( rl_weather, "alpha", 1.0f, 0.0f ).setDuration( 1500 ).start();
		    		ObjectAnimator.ofFloat( rl_clock, "alpha", 0.0f, 1.0f ).setDuration( 1500 ).start();
		    	}
				isWeatherShowing = !isWeatherShowing;
				startFlippingClockAndWeather();
			}
		}, Long.parseLong( configurationReader.getClockWeatherFlipInterval() ) );
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
			key_combination.removeAllElements();
    	}
    }
    
    
    /*
     * Not so important functions, or used only for one time
     * 
     *********************************************************/

	private void startScreenCastService(){
		UtilShell.executeShellCommandWithOp( "am startservice --user 0 com.waxrain.airplaydmr/com.waxrain.airplaydmr.WaxPlayService" );
	}

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


