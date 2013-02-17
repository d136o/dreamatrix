package com.example.thamatrix;

import java.util.concurrent.PriorityBlockingQueue;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

public class DreamatrixView extends TextureView {
	
	static final String TAG = "DreamatrixView";
		private PriorityBlockingQueue<HeadlineData> matrixTextQueue;

    public DreamatrixView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "DreamatrixView (after call to super)");
		this.matrixTextQueue = new PriorityBlockingQueue<HeadlineData>();
		
		//prepopulate the queue with some things
		String[] testStrings = {"one",
		                        "two",
		                        "three"};
		
		for (int i = 0; i < testStrings.length; i++) {
			this.matrixTextQueue.add( new HeadlineData(testStrings[i], i) );
		}
		
        // create thread only; it's started via outside call to doDraw
        setFocusable(true);
        
        Log.d(TAG, "hw accel?" );
        Log.d(TAG, String.valueOf( this.isHardwareAccelerated() ) );
	}
    	    
	public void addMatrixText(HeadlineData headline) {
		// TODO Auto-generated method stub
		this.matrixTextQueue.add(headline);
	}
	
	public String getMatrixText() {
		HeadlineData mt = this.matrixTextQueue.poll();
		if(mt == null){
			return null;
		} else {
			return mt.getText();
		}
	}
}