package com.excel.appstvlauncher.secondgen;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {
	
	int resId;
	Context context;
	String values[];
	
	public MenuAdapter( int resId, Context context, String[] values ){
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
		
		ImageView iv_menu_item_separator = (ImageView) ll.findViewById( R.id.iv_menu_item_separator );
		TextView tv_menu_item_name = (TextView) ll.findViewById( R.id.tv_menu_item_name );
		
		if( ( position == 0 ) ){
			// Set Left margin to 200dp
			LayoutParams llp = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
		    llp.setMargins( 200, 0, 0, 0 ); // llp.setMargins(left, top, right, bottom);
		    tv_menu_item_name.setLayoutParams( llp );
		}
		else if( ( position == values.length - 1 ) ){
			// Set Right margin to 200dp
			LayoutParams llp = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
			llp.setMargins( 0, 0, 200, 0 ); // llp.setMargins(left, top, right, bottom);
			tv_menu_item_name.setLayoutParams( llp );

			// Hide the separator
			iv_menu_item_separator.setVisibility( View.INVISIBLE );
		}
		
		tv_menu_item_name.setText( values[ position ] );
		
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
