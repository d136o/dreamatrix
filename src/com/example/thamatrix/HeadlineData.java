package com.example.thamatrix;

public class HeadlineData implements Comparable<HeadlineData> {
	public final static String TAG = "HeadlineData";
	private final String mText;
	private final int mPriority;
	
	public HeadlineData() {
		mText = "";
		mPriority = 0;
	}

	public HeadlineData(String text, int ranking) {
		mText = text;
		mPriority = ranking;
	}
	
	public String getText() {
		return mText;
	}
	
	public int getPriority() {
		return mPriority;
	}

	@Override
	public int compareTo(HeadlineData another) {
		int distance = another.mPriority - this.mPriority;
		return distance;
	}
}
