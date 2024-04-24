package com.jummania.listener;

/**
 * Interface definition for a callback to be invoked when certain gestures occur.
 * <p>
 * Created by Jummania on 23,April,2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
public interface OnGestureListener {


    /**
     * Called when a double tap gesture event occurs.
     */
    default void onDoubleTapEvent() {
        // Default empty implementation
    }


    /**
     * Called when a single tap gesture event occurs.
     */
    default void onSingleTapEvent() {
        // Default empty implementation
    }


    /**
     * Called when a zoom gesture event occurs.
     *
     * @param isZooming          true if the zooming gesture is ongoing, false otherwise.
     * @param currentScaleFactor the current scale factor applied to the view.
     */
    default void onZoomEvent(boolean isZooming, float currentScaleFactor) {
        // Default empty implementation
    }


}
