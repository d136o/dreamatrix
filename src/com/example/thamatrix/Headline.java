package com.example.thamatrix;

import android.content.Context;
import android.util.Log;

public class Headline {
	static final String TAG = "DreamatrixApp";
	
	Context mContext;
	
	public Headline(Context context) {
		Log.d(TAG,"Headline");
		mContext = context;
	}
	
	public String getNext() {
		return "All work and no play makes Jack a dull boy.\n";
	}
}
