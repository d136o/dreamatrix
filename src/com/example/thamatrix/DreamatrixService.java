package com.example.thamatrix;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.service.dreams.DreamService;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import android.view.Surface.OutOfResourcesException;

public class DreamatrixService extends DreamService implements TextureView.SurfaceTextureListener {
	
	static final String TAG = "DreamatrixService";
	private boolean runningUpdates = false;
	private static final int updateDelay = 20000;
	
	private DreamatrixView mDreamatrixView;
	private DreamatrixTextReceiver mTextReceiver;
	
	private SurfaceListenerRunnable mSLRunable;
	
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
        this.mDreamatrixView.setSurfaceTextureListener(this);
        
        this.mSLRunable = new SurfaceListenerRunnable();

        getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
        	    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        Log.d(TAG, "hw accel?" );
        Log.d(TAG, String.valueOf( this.mDreamatrixView.isHardwareAccelerated() ) );
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
        this.mSLRunable.start();
        Log.d(TAG, "after running");
		
//		runningUpdates = true;
//		new Thread() {
//			public void run() {
//				Log.e(TAG, String.format(
//						"Checking for headlines every %d milliseconds", updateDelay));
//				while (runningUpdates) {
//					((DreamatrixApp) getApplication()).pullAndInsert();
//					try {
//						Thread.sleep(updateDelay);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				Log.e(TAG, "DONE pulling and inserting data.\n");
//
//			}
//		}.start();		
	}

	@Override
	public void onDreamingStopped() {
		Log.d(TAG, "onDreamingStopped");
		super.onDreamingStopped();
		runningUpdates = false;
		this.mSLRunable.stopDrawing();
	}
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		Log.d(TAG, "onSurfaceTextureAvailable" );
		this.mSLRunable.setSurface( surface );
		Log.d(TAG, "leaving onSurfaceTextureAvailable" );
	}	
	
	@Override 
	public boolean onSurfaceTextureDestroyed(SurfaceTexture st) {
		Log.d(TAG, "surfaceDestroyed");	
        this.mSLRunable.stopDrawing();
        return true;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		// TODO Auto-generated method stub
		
	}
	private class SurfaceListenerRunnable extends Thread {
    	
    	private boolean mRun = true;
    	private SurfaceTexture mSurfaceTexture = null;
    	private int drawCount = 0;
        
        private String TAG = "SurfaceListenerRunnable";
        
        public SurfaceListenerRunnable(){
        	synchronized (this) {
            	this.mRun = true;
			}
        }
    	
    	public void run() {
    		synchronized (this) {
			
    			while(this.mSurface == null || !this.mRun) {
    				Log.d(TAG, "waiting for mSurfaceReady and mRun");
    				try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						Log.d(TAG, "Was waiting for surface to be ready! Interrupted we exit!" );
						break;
					}
    			}
	    			    		    				
	            while( this.mRun ){
	            	Log.d(TAG, "running!");
	                Canvas c = null;
	                try {
	                    c = mSurface.lockCanvas(null);
	                    synchronized (mSurface) {
	                        doDraw(c);
	                    }
	                } catch (IllegalArgumentException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			} catch (OutOfResourcesException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	                finally {
	                   if (c != null) {
	                	   mSurface.unlockCanvasAndPost(c);
	                   }
	                }
	                try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Log.d(TAG, "Was drawing! Interrupted we exit!" );
					}
	            }
	    		
    		}
        }
    	
    	private void doDraw(Canvas c) {
    		Log.d(TAG, "doDraw");
    		this.drawCount++;
    		
    		if(c != null && this.drawCount >= 0) {
    				Log.d(TAG, "hw accel?" );	
    				Log.d(TAG, String.valueOf( c.isHardwareAccelerated() ) );
    				c.drawColor(Color.RED); 
    		}else{
    			if(c == null){
    				Log.d(TAG, "NULL CANVAS!");
    			}
    			if( this.drawCount <= 0 && c != null ){
    				Log.d(TAG, "ltz count");
    				c.drawColor(Color.TRANSPARENT);
    			}
    		}
    	}
    	
    	public void setSurface(SurfaceTexture surface){
    			this.mSurfaceTexture = surface;
    	}
    	
    	public void stopDrawing(){
    		interrupt();
    		this.mRun = false;
    	}
    }
	
	class DreamatrixTextReceiver extends BroadcastReceiver {
		
		static final String TAG = "DreamatrixTextReceiver";
		
		@Override
		public void onReceive(Context appContext, Intent intent) {
			Log.d(TAG, "onReceive");
			List<HeadlineData> list = ((DreamatrixApp) getApplication()).headline.getList();
			for (HeadlineData headline : list) {
				mDreamatrixView.addMatrixText(headline);	
			}
		}		
	}
}
