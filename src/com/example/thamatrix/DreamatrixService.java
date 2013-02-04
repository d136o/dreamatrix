package com.example.thamatrix;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.dreams.DreamService;
import android.util.Log;

public class DreamatrixService extends DreamService {
	
	static final String TAG = "DreamatrixService";
	private boolean runningUpdates = false;
	private static final int updateDelay = 20000;
	
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
		
		runningUpdates = true;
		
		new Thread() {
			public void run() {
				Log.e(TAG, String.format(
						"Checking for headlines every %d milliseconds", updateDelay));
				while (runningUpdates) {
					((DreamatrixApp) getApplication()).pullAndInsert();
					try {
						Thread.sleep(updateDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Log.e(TAG, "DONE pulling and inserting data.\n");

			}
		}.start();		
	}

	@Override
	public void onDreamingStopped() {
		super.onDreamingStopped();
		runningUpdates = false;
		Log.d(TAG, "onDreamingStopped");
	}
	
	class DreamatrixTextReceiver extends BroadcastReceiver {
		
		static final String TAG = "DreamatrixTextReceiver";
		
		@Override
		public void onReceive(Context appContext, Intent intent) {
			Log.d(TAG, "onReceive");

			String headline = ((DreamatrixApp) getApplication()).headline.getNext();
			mDreamatrixView.addMatrixText(headline);
			
			/* TODO: DIEGO (what's happening man!!??)
				Change the above (now broken) code to something like the following:
				List<HeadlineData> list = ((DreamatrixApp) getApplication()).headline.getList();
				for (HeadlineData headline : list) {
					doSomething(headline.getText());
					doSomethingElse(headline.getRanking())
				}
			}
			*/
		}
		
	}
}
