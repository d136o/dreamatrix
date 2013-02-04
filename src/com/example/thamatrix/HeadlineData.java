package com.example.thamatrix;

public class HeadlineData {
	public final static String TAG = "HeadlineData";
	private final String mText;
	private final int mRanking;
	
	public HeadlineData() {
		mText = "";
		mRanking = 0;
	}

	public HeadlineData(String text, int ranking) {
		mText = text;
		mRanking = ranking;
	}
	
	public String getText() {
		return mText;
	}
	
	public int getRanking() {
		return mRanking;
	}
}
