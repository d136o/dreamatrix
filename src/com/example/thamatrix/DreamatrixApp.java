package com.example.thamatrix;

import android.app.Application;
import android.util.Log;

public class DreamatrixApp extends Application {
	static final String TAG = "DreamatrixApp";

	
	Headline headline;
	static final String NEW_HEADLINE_ACTION = "com.example.thamatrix.NEW_HEADLINE_ACTION";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(TAG, "onCreate");
		headline = new Headline(this);
	}

	public int pullAndInsert() {
		Log.d(TAG, "pullAndInsert");
		int count = 0;
		return count;
	}
}