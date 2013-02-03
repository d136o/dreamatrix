package com.example.thamatrix;

import android.app.Application;
import android.util.Log;

public class DreamatrixApp extends Application {
	static final String TAG = "DreamatrixApp";

	Headline headline;

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