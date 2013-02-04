package com.example.thamatrix;

import android.app.Application;
import android.content.Intent;
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
		try {
			++count;
			// List<Status> timeline = getTwitter().getHomeTimeline();

			// for (Status status : timeline) {
			//	if (statusData.insert(status)) {
			//		++count;
			//	}
			//}
		} /* catch (TwitterException e) {
			Log.e(TAG, "Network failure", e);
		}*/
		finally {
		} if (count > 0) {
			sendBroadcast(new Intent(NEW_HEADLINE_ACTION).putExtra("count", count));
		}
		return count;
	}
}