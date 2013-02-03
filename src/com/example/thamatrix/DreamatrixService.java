package com.example.thamatrix;

import android.service.dreams.DreamService;
import android.util.Log;

public class DreamatrixService extends DreamService {
	
	static final String TAG = "DreamatrixService";
    
	@Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        
        Log.d(TAG, "onAttachedToWindow");

        // Exit dream upon user touch
        setInteractive(false);
        // Hide system UI
        setFullscreen(true);
        // Set the dream layout
        setContentView(R.layout.service_dreamatrix);
    }
}
