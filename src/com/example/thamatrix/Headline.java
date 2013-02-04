package com.example.thamatrix;

import java.util.List;

import android.content.Context;
import android.util.Log;

public class Headline {
	static final String TAG = "DreamatrixApp";
	
	Context mContext;
	List<HeadlineData> mHeadlineList;
	
	public Headline(Context context) {
		Log.d(TAG,"Headline");
		mContext = context;
	}
	
	public List<HeadlineData> getList() {
		return mHeadlineList;
	}
	
	public void setList(List<HeadlineData> list) {
		mHeadlineList = list;
	}
}
