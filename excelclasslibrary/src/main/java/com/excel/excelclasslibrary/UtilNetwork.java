package com.excel.excelclasslibrary;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.excel.excelclasslibrary.Constants.APP_TAG;
import static com.excel.excelclasslibrary.Constants.SDCARD_PATH;
import static com.excel.excelclasslibrary.Constants.TAG_SEPARATOR;
import static com.excel.excelclasslibrary.UtilFile.getFile;

public class UtilNetwork {
	
	final public static String TAG = APP_TAG + TAG_SEPARATOR + "UtilNetwork";
	
	public static String getMacAddress( Context context ){
		String address = "";

		try{
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );

			NetworkInfo ni = cm.getActiveNetworkInfo();
			if( ni == null ){
				Log.i( TAG, "ni is Null" );
			}
			int network_type;

			if( ni.isConnected() ){
				network_type = ni.getType();
			}
			else
				return address;

			if( network_type == ConnectivityManager.TYPE_WIFI ){
				WifiManager manager = (WifiManager) context.getSystemService( Context.WIFI_SERVICE );
				WifiInfo info = manager.getConnectionInfo();
				address = info.getMacAddress();
				Log.i( TAG, "WiFi mac : "+address );
			}
			else if( network_type == ConnectivityManager.TYPE_ETHERNET ){
				File mac_bak = getFile( SDCARD_PATH, "mac.bak" );
				try{
					FileInputStream fis = new FileInputStream( mac_bak );
					address = "";
					int ch;
					for( int i = 0 ; i < 17 ; i++ ){
						ch = fis.read();
						address += (char) ch;
					}
					
					Log.i( TAG, "eth mac : "+address );

					fis.close();
				}
				catch( Exception e ){
					Log.i( TAG, "Exception in reading mac.bak file : "+e.getMessage()+". Now retrieving mac from ip addr show eth0" );
					return trimMac();
				}
			}
		}
		catch( Exception e ){
			Log.i( TAG, e.getMessage().toString() );
		}
		return address;
	}
	
	public static String trimMac(){
		String op = UtilShell.executeShellCommandWithOp( "ip addr show eth0" ); //ip addr show wlan0 | awk '/inet / {print $2}' | cut -d/ -f 1
        String arr[] = op.split( "\n" );
        for( int i = 0 ; i < arr.length ; i++ ){
        	if( arr[ i ].contains( "link/ether" ) ){
        		String s = arr[ i ];
        		s = s.trim();
        		s = s.substring( s.indexOf( "link/ether" ) + 11, 28 ); // start from first character at index 11, till 28
        		s = s.trim();
        		Log.i( TAG, "Trimmed Mac : "+s );
                return s.trim();
        	}
        }
        return "error";
	}

	// Network Connection Detector
	public static boolean isConnectedToInternet( Context context ){
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
		if ( connectivity != null ){
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if ( info != null )
				for ( int i = 0; i < info.length; i++ )
					if ( info[i].getState() == NetworkInfo.State.CONNECTED ){
						return true;
					}
		}
		return false;
	}
	// Network Connection Detector

	// ----- Making GET/POST Request Starts Here
	public static String makeRequestForData(String url, String request_method, String urlParameters){
		StringBuffer response = null;
		String resp = "";

		try{
			URL obj = null;
			HttpURLConnection con = null;

			if( request_method.equals( "GET" ) ){
				String encodedURL = url + "?" + urlParameters;
				//encodedURL = encodedURL.replaceAll("+", "%20");

				obj = new URL( encodedURL );
				con = (HttpURLConnection) obj.openConnection();
				//con.setRequestProperty("User-Agent", USER_AGENT);
				con.setRequestMethod( "GET" );
				con.setDoOutput( true );
			}
			else{
				String encodedURL = url;//URLEncoder.encode(url, "UTF-8");
				//urlParameters     = URLEncoder.encode(urlParameters, "UTF-8");
				//urlParameters     = urlParameters.replaceAll("+", "%20");

				obj = new URL( encodedURL );
				con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod( "POST" );
				con.setDoOutput( true );
				DataOutputStream wr = new DataOutputStream( con.getOutputStream() );
				wr.writeBytes( urlParameters );
				wr.flush();
				wr.close();
			}

			int responseCode = con.getResponseCode();
			if( responseCode == 200 ){
				BufferedReader in = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
				String inputLine;
				resp = "";
				response = new StringBuffer();
				char buff[] = new char[ 65535 ];

				FileOutputStream fos = new FileOutputStream( UtilFile.createFileIfNotExist( "Launcher", "temp.txt" ) );

				while ( ( inputLine = in.readLine() ) != null ) {
					response.append( inputLine );
				}
				fos.close();
				in.close();
			}
			else{
				throw new Exception( "No Response from server." );
			}
		}
		catch( Exception e ){
			Log.i( TAG, "Exception : "+e.toString() );
			return null;
		}
		return response.toString();
	}

	public static String getLocalIpAddressIPv4( Context ct ) {
		String address = "error";

		try{
			ConnectivityManager cm = (ConnectivityManager) ct.getSystemService( Context.CONNECTIVITY_SERVICE );

			NetworkInfo ni = cm.getActiveNetworkInfo();
			if( ni == null ){
				Log.i( TAG, "ni is null" );
			}
			int network_type = -1;

			if( ni.isConnected() ){
				network_type = ni.getType();
			}

			if( network_type == ConnectivityManager.TYPE_WIFI ){
				WifiManager manager = (WifiManager ) ct.getSystemService( Context.WIFI_SERVICE );
				WifiInfo info = manager.getConnectionInfo();
				address = UtilShell.executeShellCommandWithOp( "ip addr show wlan0" ); //ip addr show wlan0 | awk '/inet / {print $2}' | cut -d/ -f 1
				String ip = trimIp( address );
				Log.i( TAG, "WiFi IP : "+ip );
				return ip;
			}
			else if( network_type == ConnectivityManager.TYPE_ETHERNET ){
				address = UtilShell.executeShellCommandWithOp( "ip addr show eth0" );  //ip addr show eth0 | awk '/inet / {print $2}' | cut -d/ -f 1
				String ip = trimIp( address );
				Log.i( TAG, "Ethernet IP : "+ip );
				return ip;
			}
		}
		catch( Exception e ){
			Log.i( TAG, "Exception : "+e.getMessage() );
		}
		return address;
    }
	
	public static String trimIp( String op ){
		//String op = executeShellCommandWithOP( "ip addr show eth0" ); //ip addr show wlan0 | awk '/inet / {print $2}' | cut -d/ -f 1
        String arr[] = op.split( "\n" );
        for( int i = 0 ; i < arr.length ; i++ ){
        	if( arr[ i ].contains( "inet" ) ){
        		String s = arr[ i ];
        		s = s.trim();
        		s = s.substring( s.indexOf( "index" ) + 5, s.indexOf( "/" ) );
        		s = s.trim();
        		Log.i( null, "Output : "+s );
                return s;
        	}
        }
        return "error";
	}
}
