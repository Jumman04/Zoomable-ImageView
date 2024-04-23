package com.jummania.listener;

import android.animation.Animator;

import androidx.annotation.NonNull;

/**
 * An extension of Animator.AnimatorListener that provides default empty implementations
 * for all methods. This interface can be used to listen for animation events without
 * implementing all methods.
 * <p>
 * Created by Jummania on 23,April,2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
public interface AnimatorListener extends Animator.AnimatorListener {

    /**
     * Notifies that an animation has started.
     *
     * @param animation The Animator object that is started.
     */
    @Override
    default void onAnimationStart(@NonNull Animator animation) {
        // Default empty implementation
    }

    /**
     * Notifies that an animation has been canceled.
     *
     * @param animation The Animator object whose animation has been canceled.
     */
    @Override
    default void onAnimationCancel(@NonNull Animator animation) {
        // Default empty implementation
    }

    /**
     * Notifies that an animation has been repeated.
     *
     * @param animation The Animator object that has been repeated.
     */
    @Override
    default void onAnimationRepeat(@NonNull Animator animation) {
        // Default empty implementation
    }

}
