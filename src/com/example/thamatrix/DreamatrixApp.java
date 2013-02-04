package com.example.thamatrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class DreamatrixApp extends Application {
	static final String TAG = "DreamatrixApp";
	static final String WebURL = "http://news.ycombinator.com";

	
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
			List<HeadlineData> list = scrape();
			if(list != null) {
				count = list.size();
				headline.setList(list);
			}
		} /* catch (TwitterException e) {
			Log.e(TAG, "Network failure", e);
		}*/
		finally {
		} if (count > 0) {
			sendBroadcast(new Intent(NEW_HEADLINE_ACTION).putExtra("count", count));
		}
		return count;
	}
	
	private List<HeadlineData> scrape() {
		return scrape("");
		
		/*
		
		HttpClient httpclient = new DefaultHttpClient();
	    HttpResponse response;
		try {
			response = httpclient.execute(new HttpGet(WebURL));
			StatusLine statusLine = response.getStatusLine();
		    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		        response.getEntity().writeTo(out);
		        out.close();
		        String responseString = out.toString();
		        
		        return scrape(responseString);
		    } else{
		        //Closes the connection.
		        response.getEntity().getContent().close();
		        throw new IOException(statusLine.getReasonPhrase());
		    }
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;*/
	}
	
	private List<HeadlineData> scrape(String responseString) {
		List<HeadlineData> list = new ArrayList<HeadlineData>();
		list.add(new HeadlineData("Headline 1", 1));
		list.add(new HeadlineData("Headline 2", 3));
		list.add(new HeadlineData("Headline 3", 5));
		return list;
	}
}