package com.excel.excelclasslibrary;

public class UtilString {
	
	public static String appendSingleZero( int i ){
        String s1 = String.valueOf( i );
        String s = s1;
        if ( s1.length() == 1 ){
            s = ( new StringBuilder( "0" ) ).append( s1 ).toString();
        }
        return s;
    }
	
	
}
