package com.example.thamatrix;

import java.nio.*;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

import android.opengl.GLUtils;
import android.os.Handler;
import android.os.HandlerThread;
import static android.opengl.GLES20.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
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
	
	private TextureView mDreamatrixView;
	
	private Handler mRendererHandler = null;
	private HandlerThread mRenderHandlerThread = null;
	private DreamatrixRenderer mRenderer;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
        setInteractive(false);
        
        this.mDreamatrixView = new TextureView(this);
        this.mDreamatrixView.setSurfaceTextureListener(this);
    
        if( this.mRenderHandlerThread == null){
        	this.mRenderHandlerThread = new HandlerThread("RendererThread");
            this.mRenderHandlerThread.start();
            this.mRendererHandler = new Handler(this.mRenderHandlerThread.getLooper());
        }		
	};
	
	@Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow");
        
        setInteractive(false); //leave upon touch
        setFullscreen(true);   // Hide system UI
        setContentView(this.mDreamatrixView);
    }

	@Override
	public void onSurfaceTextureAvailable(final SurfaceTexture surface,
			final int width,
			final int height) {
		Log.d(TAG, "onSurfaceTextureAvailable" );
		
		// Start dream animation here
		mRendererHandler.post(new Runnable() {

			@Override
			public void run() {
				
				if(mRenderer != null ){
					mRenderer.stop();
				}
				
				mRenderer = new DreamatrixRenderer(surface, width, height);
				mRenderer.start();	
			}
		});
		
	}	
	
	@Override 
	public boolean onSurfaceTextureDestroyed(SurfaceTexture st) {
		Log.d(TAG, "surfaceDestroyed");	
		mRendererHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mRenderer != null) {
                    mRenderer.stop();
                    mRenderer = null;
                }
            }
        });
        return false;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
				final int width,
				final int height) {
		// TODO Auto-generated method stub
		 mRendererHandler.post(new Runnable() {
			 @Override
			 public void run() {
				 if (mRenderer != null) {
					 mRenderer.setSize(width, height);
                }
			 }	
		 });
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		// TODO Auto-generated method stub
	}
}
