package com.jummania.listener;

import androidx.annotation.NonNull;

import com.jummania.enums.Alignment;

/**
 * Interface definition for a callback to be invoked when image bounds change alignment
 * or off-screen percentage relative to the view edges.
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
    void onBoundAlignmentChanged(@NonNull Alignment alignment, float offScreenPercentage);

}
