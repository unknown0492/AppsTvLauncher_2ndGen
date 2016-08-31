package com.excel.excelclasslibrary;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

public class UtilAlert {
	
	public static AlertDialog.Builder createCustomDialog( Context ct, String title, String message, View view ){
    	AlertDialog.Builder alert = new AlertDialog.Builder( ct );
    	alert.setTitle( title );
    	alert.setMessage( message );
    	alert.setCancelable( false );
    	alert.setView( view );

    	return alert;
    }
}
