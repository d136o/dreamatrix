package com.example.thamatrix;

import android.service.dreams.DreamService;

public class DreamatrixService extends DreamService {
    
	@Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Exit dream upon user touch
        setInteractive(false);
        // Hide system UI
        setFullscreen(true);
        // Set the dream layout
        setContentView(R.layout.service_dreamatrix);
    }
}
