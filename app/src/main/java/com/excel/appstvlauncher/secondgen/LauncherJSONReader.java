package com.excel.appstvlauncher.secondgen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.excel.configuration.ConfigurationReader;

public class LauncherJSONReader {
	String main_items_count, item_name, item_thumbnail, item_thumbnail_url, 
	item_type, package_name, clickable, sub_items_count, sub_items, web_view_url, params;
	
	JSONObject highest_level_object;
	JSONArray highest_level_array;
	
	ConfigurationReader cr;
	
	final static String TAG = "LauncherJSONReader";
	
	public LauncherJSONReader( String json_string ){
		cr = ConfigurationReader.getInstance();
		try{
			highest_level_object = new JSONObject( json_string );
			highest_level_array = highest_level_object.getJSONArray( "main_items" );
		}
		catch( Exception e ){
			e.printStackTrace();
		}
	}
	
	public int getMainItemsCount(){
		try {
			return Integer.parseInt( highest_level_object.getString( "main_items_count" ) );
		} catch ( NumberFormatException e ) {
			e.printStackTrace();
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int getSubItemsCount( int main_menu_item_index ){
		try {
			JSONObject jsonObject = highest_level_array.getJSONObject( main_menu_item_index );
			return Integer.parseInt( jsonObject.getString( "sub_items_count" ) );
		} catch ( NumberFormatException e ) {
			e.printStackTrace();
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public JSONObject getMainItemJSON( int index ){
		try {
			return highest_level_array.getJSONObject( index );
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getMainItemValue( int index, String key ){
		try {
			return getMainItemJSON( index ).getString( key );
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject getSubItemJSON( int main_item_index, int sub_item_index ){
		JSONObject jsonObject = getMainItemJSON( main_item_index );
		try {
			JSONArray jsonArray	  = jsonObject.getJSONArray( "sub_items" );
			return jsonArray.getJSONObject( sub_item_index );
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getSubItemValue( int main_item_index, int sub_item_index, String key ){
		JSONObject jsonObject = getMainItemJSON( main_item_index );
		try {
			JSONArray jsonArray	  = jsonObject.getJSONArray( "sub_items" );
			jsonObject =  jsonArray.getJSONObject( sub_item_index );
			return jsonObject.getString( key );
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String[] getSubMenuItemNames( int main_menu_item_index ){
		String arr[] = new String[ getSubItemsCount( main_menu_item_index ) ];
		String temp = "";
		for( int i = 0 ; i < arr.length ; i++ ){
			temp = getSubItemValue( main_menu_item_index, i, "item_name" );
			
			if( temp.equals( "{SSID}" ) ){
				// Log.d( TAG, ","+temp+"," );
				arr[ i ] = cr.getSSID();
				continue;
			}
			else if( temp.equals( "{PASSWORD}" ) ){
				// Log.d( TAG, ","+temp+"," );
				arr[ i ] = cr.getHotspotPassword();
				continue;
			}
			
			arr[ i ] = temp;
		}
		return arr;
	}
}
