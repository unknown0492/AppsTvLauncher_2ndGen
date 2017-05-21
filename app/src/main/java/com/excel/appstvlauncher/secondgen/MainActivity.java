package com.excel.appstvlauncher.secondgen;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
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
import java.util.Calendar;
import java.util.Stack;

import static com.excel.configuration.Constants.PATH_LAUNCHER_CONFIG_FILE;
import static com.excel.configuration.Constants.PATH_LAUNCHER_CONFIG_FILE_SYSTEM;

public class MainActivity extends Activity {

	final static String TAG = "MainActivity";
	TextView tv_first;
	ScrollTextView tv_collar_text;
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
	String ALPHABET = "KEYCODE_";

    @Override
    protected void onCreate( Bundle savedInstanceState )  {
        super.onCreate( savedInstanceState );
        hideActionBar();

        setContentView( R.layout.activity_main );

        init();



    }

    /* Launcher Menu Items Related Functions */

	@SuppressWarnings("deprecation")
	public void init(){
        configurationReader = ConfigurationReader.getInstance();

        initViews();

		setLauncherMenuItems();
		createLauncheritemsUpdateBroadcast();

		startPerfectTimeService();
		createPerfectTimeReceiver();
		startClockTicker();

		startDateAndDayNameSwitcher();

		initializeWeatherFeatures();

        initializeClockWeatherHotelLogoFlipper();
        checkIfHotelLogoToBeDisplayed();

        restoreTvChannels();

        //PreinstallAppsManager pamm = new PreinstallAppsManager( context );
        //test();
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

    /*int TOTAL_OPTIONS = 6;
    int OPTION_PACKAGE_NAME =   0;
    int OPTION_MD5          =   1;
    int OPTION_BUTTON_ID    =   2;
    int OPTION_SHOW         =   3;
    int OPTION_WIPE_CACHE   =   4;
    int OPTION_FORCE_KILL   =   5;
    String papps_arr[][];
    public void test(){
        configurationReader = ConfigurationReader.getInstance();
        String preinstall_apps = UtilFile.readData( "appstv_data", "preinstall_apps" );
        Log.d( TAG, "preinstall_apps : "+preinstall_apps );
        String temp[] = preinstall_apps.split( "," );
        Log.d( TAG, "temp.length: " + temp.length );
        String t[] = new String[ TOTAL_OPTIONS ];
        int j = 0, k = 0;
        papps_arr = new String[ temp.length/TOTAL_OPTIONS ][ TOTAL_OPTIONS ];
        for( int i = 0 ; i < temp.length ; i++ ){

            //for( int j = 0 ; j < TOTAL_OPTIONS ; j++ ){
            Log.d( TAG, String.format( "Putting %s in t[ %d ]", temp[ i ], j ) );
            t[ j++ ] = temp[ i ];

            //}
            if( (i + 1)%TOTAL_OPTIONS == 0 ){
                Log.d( TAG, "" + (i+1) + " reached " );
                j = 0;
                Log.d( TAG, String.format( "putting papps_arr[ %d ] = t", k ) );
                papps_arr[ k++ ] = t;
                t = new String[ TOTAL_OPTIONS ];
            }

        }

        for( int l = 0 ; l < papps_arr.length ; l++ ){
            Log.d( TAG, "" + l + "->" );
            for( int m = 0 ; m < papps_arr[ l ].length ; m++ ){
                Log.d( TAG, "   " + m + "->" + papps_arr[ l ][ m ]);
            }
        }
    }*/

    LinearLayout first_main_item = null;
    LinearLayout current_main_item = null;
    LinearLayout prev_main_item = null;
    public void setMainMenuAdapter( final MenuAdapter adapter ){
    	for( int i = 0 ; i < ma.getCount(); i++ ){
    		LinearLayout v = (LinearLayout) ma.getView( i, null, null );
    		ll_main_menu_items.addView( v );

            if( i == 0 ){
				first_main_item = v;
				//v.requestFocus();
            }

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

					prev_main_item = current_main_item;

    				if( hasFocus ){
						// This is to prevent animation when going back to main menu parent from its child sub menu
						if( prev_main_item == ll )
							return;

    					Log.d( null, "focus gained on "+tv.getText().toString() );
    					// tv.setTextColor( context.getResources().getColor( R.color.menu_text_active_color ) );
    					tv.setTextColor( context.getResources().getColor( R.color.light_blue ) );
                        tv.setScaleX( 1.45f );
                        tv.setScaleY( 1.45f );

						current_main_item = ll;
                        //tv.setBackground( context.getResources().getDrawable( R.drawable.menu_bg_cut1 ) );
                        /*LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
                        params.setMargins( 0, -50, 0, 0 );
                        ll.setLayoutParams(params);*/

    					// Hide its sub-menu
    					ObjectAnimator oa = ObjectAnimator.ofFloat( hsv_sub_menu, "translationY", 0 );
    					oa.setDuration( 250 );
    					oa.start();

    					ObjectAnimator oa1 = ObjectAnimator.ofFloat( hsv_sub_menu, "alpha", 1.0f, 0.0f );
    					oa1.setDuration( 250 );
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
                                        oaa.setStartDelay( 250 );
                                        oaa.setDuration( 250 );
                                        oaa.start();

                                        ObjectAnimator oaa1 = ObjectAnimator.ofFloat( hsv_sub_menu, "alpha", 0.0f, 1.0f );
                                        oaa1.setStartDelay( 250 );
                                        oaa1.setDuration( 250 );
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

    		v.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick( View v ) {
					int index = Integer.parseInt( v.getTag().toString() );
					// Log.d( TAG, "Clicked : "+index );
					processMainMenuItemClick( index );
				}
			});
    	}

    	// This is to set the First Item on main menu as Active
		first_main_item.requestFocus();
		TextView tv = (TextView) first_main_item.findViewById( R.id.tv_menu_item_name );
		tv.setTextColor( context.getResources().getColor( R.color.light_blue ) );
		tv.setScaleX( 1.45f );
		tv.setScaleY( 1.45f );
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

        //first_main_item.requestFocus();

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

        tv_collar_text.setText( collar_text );
		tv_collar_text.setSpeed( new Double( configurationReader.getCollarTextSpeed() ) );
        tv_collar_text.startScroll();
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

		if( i == 4 ){
			return true;
		}

		return super.onKeyDown( i, keyevent );
	}

    @Override
	protected void onPause() {
		super.onPause();
		Log.d( TAG,  "insde onPause()" );

		ds.pauseDigitalSignageSwitcher();
        weather.pauseYahooWeatherService();

        pauseTetheringInfoFlipper();
        clock_weather_hotel_logo_flipper.pauseClockWeatherLogoFlipper();

		pauseLauncherIdleTimer();
	}


    @Override
	protected void onResume() {
		super.onResume();

        if( ! isLoadingCompleted() ) {
			//first_main_item.requestFocus();

            showLoadingActivity();
        }

		Log.d( TAG,  "insde onResume()" );
        configurationReader = ConfigurationReader.reInstantiate();

		//current_timestamp = System.currentTimeMillis();
		//startLauncherIdleTimer();

		onUserInteraction();


		ds.resumeDigitalSignageSwitcher();
        weather.resumeYahooWeatherService();

        clock_weather_hotel_logo_flipper.startClockWeatherLogoFlipper();
        startTetheringInfoSwitcher();
	}

    @Override
	protected void onDestroy() {
		super.onDestroy();

		deletePerfectTimeReceiver();
        weather.deleteYahooWeatherReceiver();
		deleteLauncheritemsUpdateBroadcast();
        clock_weather_hotel_logo_flipper.deleteHotelLogoAvailabilityReceiver();
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
        // iv_weather = (ImageView) findViewById( R.id.iv_weather );
        iv_weather = (AnimatedGifImageView) findViewById( R.id.iv_weather );
        tv_temperature = (TextView) findViewById( R.id.tv_temperature );
        tv_text = (TextView) findViewById( R.id.tv_text );
        rl_weather = (LinearLayout) findViewById( R.id.rl_weather );
        rl_clock = (RelativeLayout) findViewById( R.id.rl_clock );
        ll_clock_time = (LinearLayout) findViewById( R.id.ll_clock_time );
        rl_launcher_bg = (RelativeLayout) findViewById( R.id.rl_launcher_bg );
        tv_ssid = (TextView) findViewById( R.id.tv_ssid );
        tv_tethering_password = (TextView) findViewById(R.id.tv_tethering_password);
        rl_tethering_info = (RelativeLayout) findViewById( R.id.rl_tethering_info );
        rl_hotel_logo = (LinearLayout) findViewById( R.id.rl_hotel_logo );
        //digitalSignageHolder = new DigitalSignageHolder( this );
		ds = new DigitalSignage( context, rl_launcher_bg );
        //digitalSignageSwitcher = new Timer();

        //hsv_menu.setBackground( context.getResources().getDrawable( R.drawable.menu_bg7 ) );
        //tv_collar_text.setBackground( context.getResources().getDrawable( R.drawable.submenu_bg1 ) );
        //hsv_sub_menu.setBackground( context.getResources().getDrawable( R.drawable.submenu_bg1 ) );
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

		if( areLauncherElementsHidden ){
			ObjectAnimator.ofFloat( rl_elements, "alpha", 0.0f, 1.0f ).setDuration( 500 ).start();
			areLauncherElementsHidden = false;
			startLauncherIdleTimer();
		}
		else{
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
                    tv_date.animate().rotationXBy( 90f ).setDuration( 300 ).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            //ObjectAnimator.ofFloat( tv_day_name, "rotationX", 270f, 360f ).setDuration( 300 ).start();
                            tv_day_name.animate().rotationXBy( -90f ).setDuration( 300 );
                        }
                    }).start();
		    		//ObjectAnimator.ofFloat( tv_date, "rotationX", 0.0f, 90f ).setDuration( 1500 ).start();

		    	}
		    	else{
                    tv_day_name.animate().rotationXBy( 90f ).setDuration( 300 ).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // ObjectAnimator.ofFloat( tv_date, "rotationX", 270f, 360f ).setDuration( 300 ).start();
                            tv_date.animate().rotationXBy( -90f ).setDuration( 300 ).start();
                        }
                    }).start();
		    		// ObjectAnimator.ofFloat( tv_day_name, "rotationX", 0.0f, 90f ).setDuration( 1500 ).start();
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
			key_combination.removeAllElements();
    	}
    }
    
    
    /*
     * Not so important functions, or used only for one time
     * 
     *********************************************************/

	private void startScreenCastService(){
		UtilShell.executeShellCommandWithOp( "am startservice -n com.waxrain.airplaydmr/com.waxrain.airplaydmr.WaxPlayService" );
	}

    private void checkIfHotelLogoToBeDisplayed(){
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

    public static void setIsLoadingCompleted( boolean is_it ){
        String s = (is_it)?"1":"0";
        UtilShell.executeShellCommandWithOp( "setprop is_loading_complete " + s );
    }

    public boolean isLoadingCompleted(){
        String s = UtilShell.executeShellCommandWithOp( "getprop is_loading_complete" ).trim();
        return s.equals( "1" )?true:false;
    }

    private void showLoadingActivity(){
        Intent intent = new Intent( context, LoadingActivity.class );
        startActivity( intent );
        overridePendingTransition( 0, 0 );
    }

	public void restoreYoutubeSettings(){
		// Kill Youtube running in background
		String pid = UtilShell.executeShellCommandWithOp( "pidof com.google.android.youtube.tv" ).trim();
		UtilShell.executeShellCommandWithOp( "kill " + pid );

		// 1. Check if youtube package exist com.google.android.youtube.tv
		File file = new File( "/data/data/com.google.android.youtube.tv" );
		String s_file = UtilShell.executeShellCommandWithOp( "[ -d \"/data/data/com.google.android.youtube.tv\" ] && echo \"yes\"" );
		Log.d( TAG, s_file );
		//if( ! file.exists() )
		if( ! s_file.trim().equals( "yes" ) )
			return;
		Log.i( TAG, "/data/data/com.google.android.youtube.tv : exist" );

		// 2. Check if the Directory shared_prefs exist inside com.google.android.youtube.tv
		File file1 = new File( "/data/data/com.google.android.youtube.tv/shared_prefs" );
		String s_file1 = UtilShell.executeShellCommandWithOp( "[ -d \"/data/data/com.google.android.youtube.tv/shared_prefs\" ] && echo \"yes\"" );
		Log.d( TAG, s_file1 );
		//if( ! file1.exists() ){
		if( ! s_file1.trim().equals( "yes" ) ){
			Log.e( TAG, "/data/data/com.google.android.youtube.tv/shared_prefs : Not exist, hence creating it" );
			UtilShell.executeShellCommandWithOp( "chmod -R 777 /data/data/com.google.android.youtube.tv" );
			UtilShell.executeShellCommandWithOp( "mkdir /data/data/com.google.android.youtube.tv/shared_prefs" );
		}

		// 3. Check if the youtube.xml exist at /system/appstv_data/youtube.xml
		File file2 = new File( "/system/appstv_data/youtube.xml" );
		String s_file2 = UtilShell.executeShellCommandWithOp( "[ -f \"/system/appstv_data/youtube.xml\" ] && echo \"yes\"" );
		if( ! s_file2.trim().equals( "yes" ) ){
			// if( ! file2.exists() ){
			Log.e( TAG, "/system/appstv_data/youtube.xml : not exist, hence exiting !" );
			return;
		}

		// 4. Copy /system/appstv_data/youtube.xml TO /data/data/com.google.android.youtube.tv/shared_prefs/youtube.xml
		UtilShell.executeShellCommandWithOp( "chmod -R 777 /data/data/com.google.android.youtube.tv" );
		UtilShell.executeShellCommandWithOp( "chmod -R 777 /data/data/com.google.android.youtube.tv/shared_prefs" );
		UtilShell.executeShellCommandWithOp( "chmod 777 /data/data/com.google.android.youtube.tv/shared_prefs/youtube.xml" );

		UtilShell.executeShellCommandWithOp( "chmod -R 777 /system/appstv_data" );
		UtilShell.executeShellCommandWithOp( "chmod -R 777 /system/appstv_data/youtube.xml" );

		UtilShell.executeShellCommandWithOp( "rm /data/data/com.google.android.youtube.tv/shared_prefs/youtube.xml" );
		UtilShell.executeShellCommandWithOp( "cp /system/appstv_data/youtube.xml /data/data/com.google.android.youtube.tv/shared_prefs/youtube.xml" );

		Log.i( TAG, "restored youtube.xml" );

		// 5. Delete Google Account Database
		UtilShell.executeShellCommandWithOp( "chmod -R 777 /data/system/users/0" );
		UtilShell.executeShellCommandWithOp( "rm /data/system/users/0/accounts.db" );
		UtilShell.executeShellCommandWithOp( "rm /data/system/users/0/accounts.db-journal" );

		UtilShell.executeShellCommandWithOp( "am force-stop com.google.android.youtube.tv" );
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

        // 1. kill com.android.dtv
        String pid = UtilShell.executeShellCommandWithOp( "pidof com.android.dtv" );
        UtilShell.executeShellCommandWithOp( "kill "+pid );

        UtilShell.executeShellCommandWithOp( "rm -r /mnt/sdcard/appstv_data/tv_channels/backup",
                "unzip -o /mnt/sdcard/appstv_data/tv_channels/tv_channels.zip -d /mnt/sdcard/appstv_data/tv_channels" );

        UtilShell.executeShellCommandWithOp( "chmod -R 777 /data/hdtv",
                "rm -r /data/hdtv/*",
                "cp -r /mnt/sdcard/appstv_data/tv_channels/backup/hdtv/* /data/hdtv",
                "chmod -R 777 /data/hdtv" );

        // last. kill com.android.dtv
        pid = UtilShell.executeShellCommandWithOp( "pidof com.android.dtv" );
        UtilShell.executeShellCommandWithOp( "kill "+pid );

        setTvChannelRestored( true );
        Log.d( TAG, "tv_channels.zip extracted successfully" );
    }

    public void restoreTvChannels(){
        if( ! isTvChannelRestored() ){
            unzipTvChannelsZip();
			restoreYoutubeSettings();
        }
    }

    /* TV Channels restore related functions ENDS */



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


