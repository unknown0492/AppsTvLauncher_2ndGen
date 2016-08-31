package com.excel.imagemanipulator;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageManipulator {
	
	public ImageManipulator(){}
	
	@SuppressWarnings("deprecation")
	public static Drawable getDecodedDrawable( Resources res, int resId,
            int reqWidth, int reqHeight ){
		
		Bitmap bmp = getDecodedBitmap( res, resId, reqWidth, reqHeight );
		if( bmp == null )
			return null;
		
		return new BitmapDrawable( bmp );
	}
	
	@SuppressWarnings("deprecation")
	public static Drawable getDecodedDrawable( String path, 
            int reqWidth, int reqHeight ){
		
		Bitmap bmp = getDecodedBitmap( path, reqWidth, reqHeight );
		if( bmp == null )
			return null;
		
		return new BitmapDrawable( bmp );		
	}
	
	public static Bitmap getDecodedBitmap( Resources res, int resId,
            int reqWidth, int reqHeight ) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        /*Bitmap bmp = BitmapFactory.decodeResource(res, resId, options);
        if( bmp == null )
        	return null;*/
        	
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
	
	public static Bitmap getDecodedBitmap( String path,
            int reqWidth, int reqHeight ) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        /*Bitmap bmp = BitmapFactory.decodeFile( path, options );;
        if( bmp == null )
        	return null;*/
        
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize( options, reqWidth, reqHeight );

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile( path, options );
    }

    public static int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
	
}
