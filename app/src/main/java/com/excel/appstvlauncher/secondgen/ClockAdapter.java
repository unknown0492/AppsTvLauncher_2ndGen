package com.excel.appstvlauncher.secondgen;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ClockAdapter extends BaseAdapter {

	Context context;
	int resId;
	String location_names[];
	String clock_times[];
	String clock_dates[];
	
	public ClockAdapter( Context context, int resId, String[] location_names, String[] clock_times, String[] clock_dates ){
		this.context = context;
		this.resId = resId;
		this.location_names = location_names;
		this.clock_times = clock_times;
		this.clock_dates = clock_dates;
	}
	
	@Override
	public int getCount() {
		return location_names.length;
	}

	@Override
	public Object getItem( int position ) {
		return position;
	}

	@Override
	public long getItemId( int position ) {
		return position;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		View view = View.inflate( context, resId, null );
		TextView clock_time 	= (TextView) view.findViewById( R.id.clock_time );
		TextView clock_location = (TextView) view.findViewById( R.id.clock_location );
		TextView clock_date = (TextView) view.findViewById( R.id.clock_date );
		
		clock_time.setText( clock_times[ position ] );
		clock_location.setText( location_names[ position ] );
		clock_date.setText( clock_dates[ position ] );
		
		return view;
	}

}
