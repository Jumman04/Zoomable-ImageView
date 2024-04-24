package com.jummania.zoomableimageview;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jummania.Alignment;
import com.jummania.ZoomableImageView;
import com.jummania.listener.OnBoundsChangeListener;
import com.jummania.listener.OnGestureListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.zoomableImageView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ZoomableImageView zoomableImageView = findViewById(R.id.zoomableImageView);

        // Set an OnGestureListener for the ZoomableImageView
        zoomableImageView.setOnGestureListener(new OnGestureListener() {
            @Override
            public void onDoubleTapEvent() {
                // Handle the onDoubleTapEvent
                Log.d("OnGestureListener", "onDoubleTapEvent");
            }

            @Override
            public void onSingleTapEvent() {
                // Handle the onSingleTapEvent
                Log.d("OnGestureListener", "onSingleTapEvent");
            }

            @Override
            public void onZoomEvent(boolean isZooming, float currentScaleFactor) {
                // Handle the onZoomEvent
                Log.d("OnGestureListener", "isZooming: " + isZooming + ", currentScaleFactor: " + currentScaleFactor);
            }
        });

        zoomableImageView.setBoundsChangeListener(new OnBoundsChangeListener() {
            @Override
            public void onBoundAlignmentChanged(Alignment alignment, float offScreenPercentage) {
                Log.d("OnBoundsChangeListener", "Alignment: " + alignment + ", offScreenPercentage: " + offScreenPercentage);
            }

            @Override
            public void onBoundDistanceChanged(float leftDistance, float rightDistance, float topDistance, float bottomDistance) {
                Log.d("OnBoundsChangeListener", "leftDistance: " + leftDistance);
            }
        });

    }
}