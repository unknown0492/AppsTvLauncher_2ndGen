package com.excel.appstvlauncher.secondgen;

import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

//import java.util.logging.Handler;

public class ScrollTextView extends android.support.v7.widget.AppCompatTextView {

	// scrolling feature
	private Scroller mSlr;

	// milliseconds for a round of scrolling
	private int mRndDuration = 30000;

	// the X offset when paused
	private int mXPaused = 0;

	// whether it's being paused
	private boolean mPaused = true;

	private  boolean started = false;

	//Context context = getContext();

	 final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (!mPaused)
				return;

			setHorizontallyScrolling(true);

			// use LinearInterpolator for steady scrolling
			mSlr = new Scroller( getContext(), new LinearInterpolator());
			setScroller(mSlr);

			int scrollingLen = calculateScrollingLen();
			int distance = scrollingLen - (getWidth() + mXPaused);
			int duration = new Double(  (1.00000 * distance) / getSpeed()).intValue() * 1000;//(new Double(mRndDuration * distance * 1.00000/ scrollingLen)).intValue();

			Log.e( "AAA", "scrolling..."/*String.format( "scrollingLen %d, distance %d, duration %d, speed %.02f" , scrollingLen, distance, duration, getSpeed())*/);

			setVisibility(VISIBLE);
			mSlr.startScroll(mXPaused, 0, distance, 0, duration );
			invalidate();
			mPaused = false;

			Log.i( "SSS", "Inside runnable" );
		}
	};

	/*
	 * constructor
	 */
	public ScrollTextView(Context context) {
		this(context, null);
		// customize the TextView
		setSingleLine();
		setEllipsize(null);
		setVisibility(INVISIBLE);
	}

	/*
	 * constructor
	 */
	public ScrollTextView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.textViewStyle);
		// customize the TextView
		setSingleLine();
		setEllipsize(null);
		setVisibility(INVISIBLE);
	}

	/*
	 * constructor
	 */
	public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// customize the TextView
		setSingleLine();
		setEllipsize(null);
		setVisibility(INVISIBLE);
	}

	/**
	 * begin to scroll the text from the original position
	 */
	public void startScroll() {
		// begin from the very right side
		mXPaused = -1 * getWidth();
		// assume it's paused
		mPaused = true;
		resumeScroll();
		//new Handler().postDelayed( runnable, 100 );
		Log.d( null, "called" );

	}

	private double speed = 83.67;
	//double speed = 300.67;

    AsyncScroll as = null;

	/**
	 * resume the scroll from the pausing point
	 */
	public void resumeScroll() {

		/*if (!mPaused)
			return;

		// Do not know why it would not scroll sometimes
		// if setHorizontallyScrolling is called in constructor.
		//pauseScroll();


		setHorizontallyScrolling(true);

		// use LinearInterpolator for steady scrolling
		mSlr = new Scroller(this.getContext(), new LinearInterpolator());
		setScroller(mSlr);

		int scrollingLen = calculateScrollingLen();
		int distance = scrollingLen - (getWidth() + mXPaused);
		int duration = new Double(  (1.00000 * distance) / getSpeed()).intValue() * 1000;//(new Double(mRndDuration * distance * 1.00000/ scrollingLen)).intValue();

		Log.e( "AAA", String.format( "scrollingLen %d, distance %d, duration %d, speed %.02f" , scrollingLen, distance, duration, getSpeed()));

		setVisibility(VISIBLE);
		mSlr.startScroll(mXPaused, 0, distance, 0, duration );
		invalidate();
		mPaused = false;*/

		if( as != null ){
		    pauseScroll();
		    as.cancel( false );
        }

		as = new AsyncScroll(){

			@Override
			protected Void doInBackground( Void... params ) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler( Looper.getMainLooper() ).postDelayed( runnable, 4000 );
                    }
                }).start();
                return null;
            }
		};
		as.execute();



		//new Handler().postDelayed( runnable, 100 );

	}

	static class AsyncScroll extends AsyncTask< Void, Void, Void >{

	    @Override
        protected Void doInBackground( Void... params ) {

            return null;
        }
    }

	/**
	 * calculate the scrolling length of the text in pixel
	 *
	 * @return the scrolling length in pixels
	 */
	private int calculateScrollingLen() {
		TextPaint tp = getPaint();
		Rect rect = new Rect();
		String strTxt = getText().toString();
		tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
		int scrollingLen = rect.width() + getWidth();
		rect = null;
		return scrollingLen;
	}

	/**
	 * pause scrolling the text
	 */
	public void pauseScroll() {
		if (null == mSlr)
			return;

		if (mPaused)
			return;

		mPaused = true;

		// abortAnimation sets the current X to be the final X,
		// and sets isFinished to be true
		// so current position shall be saved
		mXPaused = mSlr.getCurrX();

		mSlr.abortAnimation();
	}

	@Override
	/*
	 * override the computeScroll to restart scrolling when finished so as that
	 * the text is scrolled forever
	 */
	public void computeScroll() {
		super.computeScroll();

		if (null == mSlr) return;

		if (mSlr.isFinished() && (!mPaused)) {
			this.startScroll();
		}
	}

	public int getRndDuration() {
		return mRndDuration;
	}

	public void setRndDuration(int duration) {
		this.mRndDuration = duration;
	}


	public boolean isPaused() {
		return mPaused;
	}


	public double getSpeed() {
		return speed;
	}

	public void setSpeed( double speed ) {
		this.speed = speed;
	}
}

