package com.excel.appstvlauncher.secondgen;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SubMenuAdapter extends BaseAdapter {
	
	int resId;
	Context context;
	String values[];
	
	public SubMenuAdapter( int resId, Context context, String[] values ){
		this.resId = resId;
		this.context = context;
		this.values = values;
	}
	
	@Override
	public int getCount() {
		return values.length;
	}

	@Override
	public Object getItem( int position ) {
		return values[ position ];
	}

	@Override
	public long getItemId( int position ) {
		return position;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		LayoutInflater lf = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		LinearLayout ll;
		if( convertView == null ){
			ll = (LinearLayout) lf.inflate( resId, null );
		}
		else{
			ll = (LinearLayout) convertView;
		}
		
		ImageView iv_sub_menu_left_end = (ImageView) ll.findViewById( R.id.iv_sub_menu_left_end );
		ImageView iv_sub_menu_item_separator = (ImageView) ll.findViewById( R.id.iv_sub_menu_item_separator );
		ImageView iv_sub_menu_right_end = (ImageView) ll.findViewById( R.id.iv_sub_menu_right_end );
		TextView tv_sub_menu_item_name = (TextView) ll.findViewById( R.id.tv_sub_menu_item_name );
		
		if( getCount() == 1 ){
			// show the Left Sub Menu Image
			iv_sub_menu_left_end.setVisibility( View.VISIBLE );
			
			// show the Right Sub Menu Image
			iv_sub_menu_right_end.setVisibility( View.VISIBLE );
			
			// Hide the separator
			iv_sub_menu_item_separator.setVisibility( View.GONE );
		}
		else if( position == 0 ){
			// show the Left Sub Menu Image
			iv_sub_menu_left_end.setVisibility( View.VISIBLE );
			
			// Hide the separator
			//iv_sub_menu_item_separator.setVisibility( View.GONE );
		}
		else if( ( position == values.length - 1 ) ){
			// show the Right Sub Menu Image
			iv_sub_menu_right_end.setVisibility( View.VISIBLE );

			// Hide the separator
			iv_sub_menu_item_separator.setVisibility( View.GONE );
		}
		
		tv_sub_menu_item_name.setText( values[ position ] );
		
		ll.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				Log.d( null, "clicked" );
				
			}
		});
		
		ll.setTag( (Integer)position );  // Setting Tag as the Index for this View
		
		/*ll.setOnFocusChangeListener( new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange( View v, boolean hasFocus) {
				LinearLayout ll = (LinearLayout) v;
				TextView tv = (TextView) ll.findViewById( R.id.tv_menu_item_name );
				
				// Log.d( null, tv.getText().toString() );
				
				if( hasFocus ){
					Log.d( null, "focus gained on "+tv.getText().toString() );
					tv.setTextColor( context.getResources().getColor( R.color.menu_text_active_color ) );
				}
				else{
					Log.d( null, "focus lost from "+tv.getText().toString() );
					tv.setTextColor( context.getResources().getColor( R.color.menu_text_default_color ) );
				}
			}
		});*/
		
		return ll;
	}

}
