package com.excel.excelclasslibrary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UtilSQLite {
	
	// ---- Instantiation SQLite Database Starts Here
	public static SQLiteDatabase makeDatabase( String dbname, Context context ){
		SQLiteDatabase sqldb = null;
		try{
			sqldb = context.openOrCreateDatabase( dbname, Context.MODE_PRIVATE, null );		
		}
		catch( Exception e ){
			Log.i(null, " Opening DB error : "+e.toString());
		}
		return sqldb;
	}
	// ---- Instantiating SQLite Database Ends Here

	// ---- Executing SQLite Query Starts Here
	public static Cursor executeQuery( SQLiteDatabase sqldb, String sql, boolean insert ){
		Cursor c = null;
		try{
			if( insert )
				sqldb.execSQL( sql );
			else
				c = sqldb.rawQuery( sql, null );			
		}
		catch( Exception e ){
			Log.i( null,"Error executing query : "+e.toString() );
		}
		return c;
	}
	// ---- Executing SQLite Query Ends Here
}
