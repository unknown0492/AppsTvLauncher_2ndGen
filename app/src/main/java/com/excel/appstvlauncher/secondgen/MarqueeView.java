package com.excel.appstvlauncher.secondgen;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class MarqueeView extends TextView{

	private static final String TAG = "MarqueeView";
	private int mCoordinateX = 0;
	private int speed = 1;
	private int mDuration = 20;
	private Thread mScrollThread = null;
	private Boolean mIsFinish = true;
	float mTextWidth = 0;
	private String mText = "";
	private Boolean mIsScroll = true;
	private int initLeft = 0;

	public MarqueeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MarqueeView(Context context, AttributeSet attrs) {
		this(context, attrs,-1);
	}

	public MarqueeView(Context context) {
		this(context,null);
	}

	public void setText(String text){
		mText = text;
		if(text == null){
			return ;
		}
		mTextWidth = getPaint().measureText(mText);
		invalidate();
	}

	public void start(){
		if(mTextWidth < getMeasuredWidth()){
			return ;
		}
		if(mIsFinish){
			mIsFinish = false;
			mScrollThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while(!mIsFinish){
						mCoordinateX -= speed;
						if(mCoordinateX <= -(mTextWidth+initLeft)){
							mCoordinateX = (int) getMeasuredWidth();
						}
						postInvalidate();
						try {
							Thread.sleep(mDuration);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			mScrollThread.start();
		}
	}

	public void stop(){
		if(mTextWidth < getMeasuredWidth()){
			return ;
		}
		mIsFinish = true;
		mCoordinateX = 0;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		initLeft = getPaddingLeft();
		if(getGravity() == Gravity.CENTER){
			initLeft = (int) ((getMeasuredWidth() - mTextWidth*1.0f)/2)+getPaddingLeft();
			if(initLeft <= 0){
				initLeft = getPaddingLeft();
			}
		}
		canvas.drawText(mText, initLeft+mCoordinateX, getBaseline(),getPaint());
	}

}
