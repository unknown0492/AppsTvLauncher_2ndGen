package com.excel.appstvlauncher.secondgen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.excel.configuration.ConfigurationReader;
import com.excel.configuration.ConfigurationWriter;
import com.excel.customitems.CustomItems;
import com.excel.excelclasslibrary.UtilMisc;
import com.excel.excelclasslibrary.UtilNetwork;
import com.excel.excelclasslibrary.UtilShell;
import com.excel.excelclasslibrary.UtilURL;

import org.json.JSONArray;
import org.json.JSONObject;

public class ShortcutsActivity extends Activity {
	
	TextView tv_mac_address, tv_firmware_version, tv_cms_ip;
	Button  bt_reboot, bt_root_browser, bt_mbox, bt_settings,
			bt_terminal, bt_tv_channels_backup, bt_reboot_recovery, bt_ip_address,
			bt_run_ots;
	Button bt_room_no;
	Context context = this;
	final static String TAG = "MinimumShortcutsActivity";
	ConfigurationReader configurationReader;
	ConfigurationWriter configurationWriter;
	LinearLayout ll_left_remaining, ll_right_remaining;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_minimum_shortcuts );
		
		init();
		
		
	}

	public void init(){
		initViews();
		
		Intent in = getIntent();
		String who = in.getStringExtra( "who" );
		if( who.equals( "xkx" ) ){ // staff -> hide all remaining views
			ll_left_remaining.setVisibility( View.INVISIBLE );
			ll_right_remaining.setVisibility( View.INVISIBLE );
		}
		else{
			ll_left_remaining.setVisibility( View.VISIBLE );
			ll_right_remaining.setVisibility( View.VISIBLE );
		}
		
		// Set Mac Address
		setMacAddress();
		
		// Set Firmware Version
		setFirmwareVersion();
		
		// Change Room Number
		roomNumberButtonClick();
		
		// Reboot Box
		rebootBoxButtonClick();

		// show IP Address
		ipAddressRetrieve();

		// Run OTS
		runOTSButtonClick();

		// Set CMS IP
		setCMSIP();

		// Root Browser Click
		rootBrowserClick();
		
		// MBox Settings Click
		mboxSettingsClick();
		
		// Android Settings Click
		androidSettingsClick();
		
		// Terminal Shell Open
		androidShellTerminalClick();
		
		// TV Channels backup app
		tvChannelsBackupAppClick();
		
		// Reboot To Recovery
		rebootToRecovery();
	}

	@Override
	protected void onResume() {
		super.onResume();

		configurationReader = ConfigurationReader.reInstantiate();
	}

	public void initViews(){
		tv_firmware_version = (TextView) findViewById( R.id.tv_firmware_version );
		tv_mac_address = (TextView) findViewById( R.id.tv_mac_address );
		tv_cms_ip = (TextView) findViewById( R.id.tv_cms_ip );
		bt_room_no = (Button) findViewById( R.id.bt_room_no );
		bt_reboot = (Button) findViewById( R.id.bt_reboot_box );
		configurationReader = ConfigurationReader.reInstantiate();
		ll_left_remaining = (LinearLayout) findViewById( R.id.ll_left_remaining );
		ll_right_remaining = (LinearLayout) findViewById( R.id.ll_right_remaining );
		bt_root_browser = (Button) findViewById( R.id.bt_root_browser );
		bt_mbox = (Button) findViewById( R.id.bt_mbox );
		bt_settings = (Button) findViewById( R.id.bt_settings );
		bt_terminal = (Button) findViewById( R.id.bt_terminal );
		bt_tv_channels_backup = (Button) findViewById( R.id.bt_tv_channels_backup );
		bt_reboot_recovery = (Button) findViewById( R.id.bt_reboot_recovery );
		bt_ip_address = (Button) findViewById( R.id.bt_ip_address );
		bt_run_ots = (Button) findViewById( R.id.bt_run_ots );
	}
	
	public void setMacAddress(){
		String mac_address = UtilNetwork.getMacAddress( context );
		if( mac_address == null )
			mac_address = "Network Disconnected";
		tv_mac_address.setText( mac_address );
	}
	
	public void setFirmwareVersion(){
		tv_firmware_version.setText( configurationReader.getFirmwareName() );
	}
	
	public void roomNumberButtonClick(){


		bt_room_no.setText( configurationReader.getRoomNo() );
		
		bt_room_no.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				configurationReader = ConfigurationReader.reInstantiate();

				LinearLayout ll_parent = new LinearLayout( context );
				ll_parent.setOrientation( LinearLayout.VERTICAL );
				LinearLayout ll = new LinearLayout( context );
				ll.setFocusableInTouchMode( true );
				ll.setFocusable( true );
				ll_parent.addView( ll );

				AlertDialog.Builder ab = new AlertDialog.Builder( context );
				ab.setTitle( "Set Room Number" );
				ab.setMessage( "Note : Type only numbers without the word `ROOM`" );
				
				final EditText et = new EditText( context );
				et.setHint( "1013" );
				et.setInputType( InputType.TYPE_CLASS_NUMBER );
				
				String room_no = configurationReader.getRoomNo();
				et.setText( room_no.substring( 4, room_no.length() ) );

				ll_parent.addView( et );

				ab.setView( ll_parent );
				ab.setPositiveButton( "Update", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick( DialogInterface dialog, int which ) {
						final String r_no = et.getText().toString().trim();
						if( r_no.equals( "" ) ){
							CustomItems.showCustomToast( context, "error", "Room Number cannot be empty", 3000 );
							return;
						}

						// Update the Room Number on CMS
						//CustomItems.showCustomToast( context, "warning", "Room Number not yet updated on the CMS !", 5000 );

						new Thread(new Runnable() {
							@Override
							public void run() {
								String s = UtilNetwork.makeRequestForData( UtilURL.getWebserviceURL(), "POST",
										UtilURL.getURLParamsFromPairs( new String[][]{
												{ "mac_address", UtilNetwork.getMacAddress( context ) },
												{ "what_do_you_want", "update_room_no" },
												{ "room_no", "ROOM"+r_no }
										} ));

								if( s == null ){
									Log.d( TAG, "Failed to Update Room Number" );
									CustomItems.showCustomToast( context, "error", "Failed to Update Room Number !", 5000 );
									//setRetryTimer();
									return;
								}

								Log.d( TAG, "response : "+s );
								JSONArray jsonArray = null;
								JSONObject jsonObject = null;

								try{
									jsonArray = new JSONArray( s );
									jsonObject = jsonArray.getJSONObject( 0 );

									String type = jsonObject.getString( "type" );
									String info = jsonObject.getString( "info" );
									if ( type.equals( "error" ) ){
										Log.e( TAG, "Failed to update room number" );

										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												CustomItems.showCustomToast( context, "error", "Failed to update room number", 3000 );
											}
										});

										return;
									}
									else if( type.equals( "success" ) ){
										Log.d( TAG, info );
										//CustomItems.showCustomToast( context, "success", info, 5000 );

										// Update the Room Number in the /mnt/sdcard/appstv_data/configuration file
										configurationWriter = ConfigurationWriter.getInstance( context );
										if( !configurationWriter.setRoomNumber( "ROOM" + r_no ) ){

											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													CustomItems.showCustomToast( context, "error", "Failed to update Configuration File", 3000 );
												}
											});

											return;
										}

										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												bt_room_no.setText( "ROOM"+r_no );
                                                CustomItems.showCustomToast( context, "success", "Room number has been updated !", 5000 );
											}
										});

										Intent in = new Intent( "get_box_configuration" );
										sendBroadcast( in );

									}
								}
								catch( Exception e ){
									e.printStackTrace();

								}
							}
						}).start();

					}
					
				});
				
				ab.setNegativeButton( "Cancel", null );
				
				ab.show();
				
			}
		});
	}

	public void rebootBoxButtonClick(){
		bt_reboot.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				UtilShell.executeShellCommandWithOp( "reboot" );
			}
		});
	}

	public void ipAddressRetrieve(){
		String ip = "";
		try {
			ip = UtilNetwork.getLocalIpAddressIPv4(context);
		}
		catch( Exception e ){
			ip = null;
		}
		if( ip == null ){
			bt_ip_address.setText( "Network Disconnected" );
			return;
		}
		bt_ip_address.setText( ip );
	}

	public void runOTSButtonClick(){
		final String is_ots_completed = configurationReader.getIsOtsCompleted();
		if( is_ots_completed.equals( "1" ) ){
			bt_run_ots.setText( "OTS Completed" );
		}
		else{
			bt_run_ots.setText( "Click To Run OTS" );
		}

		bt_run_ots.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick( View v ) {

				if( is_ots_completed.equals( "1" ) ){
					CustomItems.showCustomToast( context, "warning", "One Time Setup has already been completed !", 5000 );
					return;
				}
				else{
					UtilMisc.startApplicationUsingPackageName( context, "com.excel.onetimesetup.secondgen" );
					return;
				}
			}

		});
	}

	public void rootBrowserClick(){
		bt_root_browser.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				if( !UtilMisc.startApplicationUsingPackageName( context, "com.jrummy.root.browserfree" ) ){
					CustomItems.showCustomToast( context, "error", "Root Borwser Free version is not installed", 5000 );
					
					if( !UtilMisc.startApplicationUsingPackageName( context, "com.jrummy.root.browser" ) ){
						CustomItems.showCustomToast( context, "error", "Root Borwser Full version is not installed", 5000 );
					}
				}
			}
		});
	}

	public void mboxSettingsClick(){
		bt_mbox.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				if( !UtilMisc.startApplicationUsingPackageName( context, "com.sdmc.settings" ) ){
				//if( !UtilMisc.startApplicationUsingPackageName( context, "com.mbx.settingsmbox" ) ){
					CustomItems.showCustomToast( context, "error", "SDMC Settings not found", 5000 );
				}
			}
		});
	}
	
	public void androidSettingsClick(){
		bt_settings.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				UtilShell.executeShellCommandWithOp( "am start -a android.intent.action.MAIN -n com.android.settings/.Settings" );
			}
		});
	}
	
	public void androidShellTerminalClick(){
		bt_terminal.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				if( !UtilMisc.startApplicationUsingPackageName( context, "jackpal.androidterm" ) ){
					CustomItems.showCustomToast( context, "error", "Android Terminal Jackpal not found", 5000 );
				}
			}
		});
	}

	public void tvChannelsBackupAppClick(){
		bt_tv_channels_backup.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				if( !UtilMisc.startApplicationUsingPackageName( context, "com.excel.tvchannelsbackup" ) ){
					CustomItems.showCustomToast( context, "error", "TV Channels Backup App not found", 5000 );
				}
			}
		});
	}

	public void rebootToRecovery(){
		bt_reboot_recovery.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				UtilShell.executeShellCommandWithOp( "reboot recovery" );
			}
		});
	}

	public void setCMSIP(){
		tv_cms_ip.setText( configurationReader.getCmsIp() );
	}
}
