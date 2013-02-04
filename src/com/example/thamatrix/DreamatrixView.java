package com.example.thamatrix;

import java.util.concurrent.PriorityBlockingQueue;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DreamatrixView extends SurfaceView implements SurfaceHolder.Callback {
	
	static final String TAG = "DreamatrixView";
	private DreamatrixThread thread;
	private PriorityBlockingQueue<MatrixText> matrixTextQueue;
	
	class DreamatrixThread extends Thread {
		
		static final String TAG = "DreamatrixThread";

		private boolean mRun = false;
        private SurfaceHolder mSurfaceHolder;
        private Handler mHandler;
        private Context mContext;
        
        public DreamatrixThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;
            
            Log.d(TAG, "DreamatrixThread");
        }
        
        public void setRunning(boolean b) {
        	Log.d(TAG, "setRunning");
        	
            mRun = b;
        }

		@Override
        public void run() {
			Log.d(TAG, "run");
			
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        doDraw(c);
                    }
                }
                finally {
                   if (c != null) {
                       mSurfaceHolder.unlockCanvasAndPost(c);
                   }
                }
            }
        }
		
		private void doDraw(Canvas c) {
			Log.d(TAG, "doDraw");
			
		}
	}
	
	class MatrixText implements Comparable<MatrixText> {
		private String text;
		private int priority; //lower is better
		
		public MatrixText(String txt, int priority){
			this.text = txt;
			this.priority = priority;
		}

		@Override
		public int compareTo(MatrixText another) {
			int distance = another.priority - this.priority;
			return distance;
		}
	}

	public DreamatrixView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "DreamatrixView (after call to super)");
		
		this.matrixTextQueue = new PriorityBlockingQueue<DreamatrixView.MatrixText>();
		
		//setBackgroundResource(R.drawable.matrix);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new DreamatrixThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            }
        });

        setFocusable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.d(TAG, "onDraw");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(TAG, "surfaceChanged");		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");	
		boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
	}
	
	public void startDrawing() {
		Log.d(TAG, "startDrawing");
		thread.setRunning(true);
        thread.start();
	}

	public void addMatrixText(String headline) {
		// TODO Auto-generated method stub
		MatrixText mt = new MatrixText(headline, 0);
		this.matrixTextQueue.add(mt);
	}
	
	public String getMatrixText() {
		MatrixText mt = this.matrixTextQueue.poll();
		if(mt == null){
			return null;
		} else {
			return mt.text;
		}
	}
	
	
}
