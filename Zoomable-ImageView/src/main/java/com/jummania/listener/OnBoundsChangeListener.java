package com.jummania.listener;

import com.jummania.Alignment;

/**
 * Interface definition for a callback to be invoked when image bounds change alignment
 * or distance relative to the view edges.
 * <p>
 * Created by Jummania on April 24, 2024.
 * Email: sharifuddinjumman@gmail.com
 * Location: Dhaka, Bangladesh.
 */
public interface OnBoundsChangeListener {

    /**
     * Called when the image bounds are aligned with or exceed the view edges.
     *
     * @param alignment           the alignment of the image bounds exceeding the view edges (LEFT, RIGHT, TOP, BOTTOM).
     * @param offScreenPercentage the percentage of the image off-screen relative to the corresponding edge.
     */
    default void onBoundAlignmentChanged(Alignment alignment, float offScreenPercentage) {
        // Default empty implementation
    }

    /**
     * Called when the distance between the image bounds and the view edges changes.
     *
     * @param leftDistance   the distance between the left edge of the image and the view's left edge.
     * @param rightDistance  the distance between the right edge of the image and the view's right edge.
     * @param topDistance    the distance between the top edge of the image and the view's top edge.
     * @param bottomDistance the distance between the bottom edge of the image and the view's bottom edge.
     */
    default void onBoundDistanceChanged(float leftDistance, float rightDistance, float topDistance, float bottomDistance) {
        // Default empty implementation
    }
}
