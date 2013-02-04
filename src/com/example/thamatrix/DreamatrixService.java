package com.example.thamatrix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.dreams.DreamService;
import android.util.Log;

public class DreamatrixService extends DreamService {
	
	static final String TAG = "DreamatrixService";
	
	private DreamatrixView mDreamatrixView;
	private DreamatrixTextReceiver mTextReceiver;
	
	@Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        
        Log.d(TAG, "onAttachedToWindow");

        // Exit dream upon user touch
        setInteractive(false);
        // Hide system UI
        setFullscreen(true);
		// Set the dream layout        
        setContentView(R.layout.dreamatrix_layout);
        this.mDreamatrixView = (DreamatrixView) findViewById(R.id.dreamatrix);

    }

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Log.d(TAG, "onDetachedFromWindow");	
	}

	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
		Log.d(TAG, "onDreamingStarted");
		
		//sign up for news updates!
        this.mTextReceiver = new DreamatrixTextReceiver();
        Context c = getApplicationContext();
        c.registerReceiver(this.mTextReceiver, new IntentFilter(DreamatrixApp.NEW_HEADLINE_ACTION));
		
		// Start dream animation here
		mDreamatrixView.startDrawing();
	}

	@Override
	public void onDreamingStopped() {
		super.onDreamingStopped();
		Log.d(TAG, "onDreamingStopped");
	}
	
	class DreamatrixTextReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context appContext, Intent intent) {
			String headline = ((DreamatrixApp) getApplication()).headline.getNext();;
			mDreamatrixView.addMatrixText(headline);
		}
		
	}
}
