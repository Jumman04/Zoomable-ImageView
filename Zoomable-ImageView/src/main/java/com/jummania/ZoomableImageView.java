/**
 * Copyright 2016 Jeffrey Sibbold
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jummania;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.ScaleGestureDetectorCompat;

import com.jummania.listener.AnimatorListener;
import com.jummania.listener.OnGestureListener;

/**
 * A pinch-to-zoom extension of {@link ImageView} that provides smooth zooming and translating
 * functionality, along with automatic resetting and exterior bounds restriction to keep the
 * image within the visible window.
 *
 * <p>This class implements the pinch-to-zoom behavior using {@link ScaleGestureDetector}
 * and supports touch gestures for zooming and panning. It enhances the user experience
 * by providing natural zooming and translating with automatic resetting when interaction stops.
 *
 * <p>To use `ZoomableImageView` in your layout, include it like a regular ImageView in your XML:
 * <pre>{@code
 * <com.jummania.ZoomableImageView
 *     android:id="@+id/zoomableImageView"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     android:src="@drawable/my_image"
 *     />
 * }</pre>
 *
 * <p>By default, the `ZoomableImageView` uses a matrix scale type (`android:scaleType="matrix"`)
 * to handle custom transformations for zooming and panning. Ensure that the image's
 * initial scale type is set to `matrix` for proper functionality.
 * <p>
 * * Implemented by Jummania on 23,April,2024.
 * * Email: sharifuddinjumman@gmail.com
 * * Dhaka, Bangladesh.
 */
public class ZoomableImageView extends AppCompatImageView implements OnScaleGestureListener {


    /**
     * The minimum scale factor allowed for zooming.
     */
    private static final float MIN_SCALE = 0.6f;

    /**
     * The maximum scale factor allowed for zooming.
     */
    private static final float MAX_SCALE = 8f;

    /**
     * The matrix used to apply transformations (move and zoom) to the image.
     */
    private final Matrix matrix = new Matrix();

    /**
     * Array to hold the values of the transformation matrix.
     */
    private final float[] matrixValues = new float[9];

    /**
     * Rectangular bounds of the zoomable image.
     */
    private final RectF bounds = new RectF();

    /**
     * Last touch point coordinates.
     */
    private final PointF last = new PointF(0, 0);

    /**
     * Flag indicating whether the image is currently being zoomed.
     */
    private boolean isZooming = false;

    /**
     * The initial scale type of the ImageView when zooming started.
     */
    private ScaleType startScaleType;

    /**
     * The initial transformation matrix when zooming started.
     */
    private Matrix startMatrix = new Matrix();

    /**
     * Initial scale values.
     */
    private float[] startValues = null;

    /**
     * The minimum scale factor that can be applied to the image.
     */
    private float minScale = MIN_SCALE;

    /**
     * The maximum scale factor that can be applied to the image.
     */
    private float maxScale = MAX_SCALE;

    /**
     * The calculated minimum scale factor considering the image's starting scale.
     */
    private float calculatedMinScale = MIN_SCALE;

    /**
     * The calculated maximum scale factor considering the image's starting scale.
     */
    private float calculatedMaxScale = MAX_SCALE;

    /**
     * Flag indicating whether the image can be translated (panned).
     */
    private boolean translatable;

    /**
     * Flag indicating whether zooming is enabled.
     */
    private boolean zoomable;

    /**
     * Flag indicating whether double-tap gesture triggers zooming.
     */
    private boolean doubleTapToZoom;

    /**
     * Flag indicating whether to restrict the image within specified bounds.
     */
    private boolean restrictBounds;

    /**
     * Flag indicating whether to animate the image on reset.
     */
    private boolean animateOnReset;

    /**
     * Flag indicating whether to automatically center the image after zooming.
     */
    private boolean autoCenter;

    /**
     * The scale factor applied when double-tap gesture is detected.
     */
    private float doubleTapToZoomScaleFactor;

    /**
     * The mode for automatically resetting the image after interaction stops.
     * See {@link AutoResetMode} for possible values.
     */
    @AutoResetMode
    private int autoResetMode;

    /**
     * The initial scale factor when zooming starts.
     */
    private float startScale = 1f;

    /**
     * The factor by which the image is scaled.
     */
    private float scaleBy = 1f;

    /**
     * The current scale factor applied to the image.
     */
    private float currentScaleFactor = 1f;

    /**
     * Number of pointers in the previous touch event.
     */
    private int previousPointerCount = 1;

    /**
     * Number of pointers in the current touch event.
     */
    private int currentPointerCount = 0;

    /**
     * The scale gesture detector for detecting pinch gestures.
     */
    private ScaleGestureDetector scaleDetector;

    /**
     * Value animator used for resetting the image.
     */
    private ValueAnimator resetAnimator;

    /**
     * Gesture detector for detecting single and double taps.
     */
    private GestureDetector gestureDetector;

    /**
     * Listener for handling gesture events.
     */
    private OnGestureListener onGestureListener = null;

    /**
     * Flag indicating whether a double-tap gesture has been detected.
     */
    private boolean doubleTapDetected = false;

    /**
     * Flag indicating whether a single-tap gesture has been detected.
     */
    private boolean singleTapDetected = false;


    /**
     * Gesture listener for handling touch events such as double-tap and single-tap gestures.
     * This listener is used internally by {@link ZoomableImageView} to detect gestures.
     */
    private final GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        /**
         * Called when a double-tap gesture event is detected.
         *
         * @param e The motion event for the double-tap.
         * @return Always returns false.
         */
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                // Double-tap gesture detected
                doubleTapDetected = true;
                // Trigger the onDoubleTapEvent callback if listener is set
                if (onGestureListener != null) {
                    onGestureListener.onDoubleTapEvent();
                }
            }
            // Reset single-tap detection
            singleTapDetected = false;
            return false;
        }

        /**
         * Called when a single-tap-up gesture event is detected.
         *
         * @param e The motion event for the single-tap-up.
         * @return Always returns false.
         */
        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {
            // Single-tap-up gesture detected
            singleTapDetected = true;
            return false;
        }

        /**
         * Called when a confirmed single-tap gesture event is detected.
         *
         * @param e The motion event for the single-tap.
         * @return Always returns false.
         */
        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            // Reset single-tap detection
            singleTapDetected = false;
            // Trigger the onSingleTapEvent callback if listener is set
            if (onGestureListener != null) {
                onGestureListener.onSingleTapEvent();
            }
            return false;
        }
    };


    /**
     * Custom ImageView that supports pinch-to-zoom functionality and smooth transformations.
     * This class extends {@link AppCompatImageView} and implements touch gestures for zooming and panning.
     *
     * @param context The context in which the view is created.
     */
    public ZoomableImageView(Context context) {
        super(context);
        init(context, null);
    }


    /**
     * Custom ImageView that supports pinch-to-zoom functionality and smooth transformations.
     * This class extends {@link AppCompatImageView} and implements touch gestures for zooming and panning.
     *
     * @param context The context in which the view is created.
     * @param attrs   The attribute set containing the view's attributes.
     */
    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * Custom ImageView that supports pinch-to-zoom functionality and smooth transformations.
     * This class extends {@link AppCompatImageView} and implements touch gestures for zooming and panning.
     *
     * @param context  The context in which the view is created.
     * @param attrs    The attribute set containing the view's attributes.
     * @param defStyle The default style resource ID.
     */
    public ZoomableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * Initializes the ZoomableImageView with necessary settings and attributes.
     *
     * @param context The context in which the view is created.
     * @param attrs   The attribute set containing the view's attributes.
     */
    private void init(Context context, AttributeSet attrs) {
        // Initialize ScaleGestureDetector for handling pinch gestures
        scaleDetector = new ScaleGestureDetector(context, this);

        // Initialize GestureDetector for handling tap and double-tap gestures
        gestureDetector = new GestureDetector(context, gestureListener);

        // Disable quick scaling to avoid conflicts with custom scaling behavior
        ScaleGestureDetectorCompat.setQuickScaleEnabled(scaleDetector, false);

        // Store the initial scale type of the ImageView
        startScaleType = getScaleType();

        // Read custom attributes from XML (if supported)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try (TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.ZoomableImageView)) {
                // Read boolean attributes
                zoomable = values.getBoolean(R.styleable.ZoomableImageView_zoomable, true);
                translatable = values.getBoolean(R.styleable.ZoomableImageView_translatable, true);
                animateOnReset = values.getBoolean(R.styleable.ZoomableImageView_animateOnReset, true);
                autoCenter = values.getBoolean(R.styleable.ZoomableImageView_autoCenter, true);
                restrictBounds = values.getBoolean(R.styleable.ZoomableImageView_restrictBounds, false);
                doubleTapToZoom = values.getBoolean(R.styleable.ZoomableImageView_doubleTapToZoom, true);

                // Read float attributes
                minScale = values.getFloat(R.styleable.ZoomableImageView_minScale, MIN_SCALE);
                maxScale = values.getFloat(R.styleable.ZoomableImageView_maxScale, MAX_SCALE);
                doubleTapToZoomScaleFactor = values.getFloat(R.styleable.ZoomableImageView_doubleTapToZoomScaleFactor, 3);

                // Read integer attribute and convert to AutoResetMode enum value
                autoResetMode = AutoResetMode.Parser.fromInt(values.getInt(R.styleable.ZoomableImageView_autoResetMode, AutoResetMode.UNDER));
            }
        } else {
            // Read custom attributes from XML (fallback for older versions)
            TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.ZoomableImageView);
            zoomable = values.getBoolean(R.styleable.ZoomableImageView_zoomable, true);
            translatable = values.getBoolean(R.styleable.ZoomableImageView_translatable, true);
            animateOnReset = values.getBoolean(R.styleable.ZoomableImageView_animateOnReset, true);
            autoCenter = values.getBoolean(R.styleable.ZoomableImageView_autoCenter, true);
            restrictBounds = values.getBoolean(R.styleable.ZoomableImageView_restrictBounds, false);
            doubleTapToZoom = values.getBoolean(R.styleable.ZoomableImageView_doubleTapToZoom, true);
            minScale = values.getFloat(R.styleable.ZoomableImageView_minScale, MIN_SCALE);
            maxScale = values.getFloat(R.styleable.ZoomableImageView_maxScale, MAX_SCALE);
            doubleTapToZoomScaleFactor = values.getFloat(R.styleable.ZoomableImageView_doubleTapToZoomScaleFactor, 3);
            autoResetMode = AutoResetMode.Parser.fromInt(values.getInt(R.styleable.ZoomableImageView_autoResetMode, AutoResetMode.UNDER));

            // Recycle the TypedArray to release its resources
            values.recycle();
        }

        // Ensure that the specified scale range is valid
        verifyScaleRange();
    }


    /**
     * Verifies and ensures that the scale range parameters (minScale and maxScale) are valid.
     * Throws IllegalStateException if the scale range is invalid.
     */
    private void verifyScaleRange() {
        // Check if minScale is less than maxScale
        if (minScale >= maxScale) {
            throw new IllegalStateException("minScale must be less than maxScale");
        }

        // Check if minScale is greater than 0
        if (minScale < 0) {
            throw new IllegalStateException("minScale must be greater than 0");
        }

        // Check if maxScale is greater than 0
        if (maxScale < 0) {
            throw new IllegalStateException("maxScale must be greater than 0");
        }

        // Adjust doubleTapToZoomScaleFactor if it exceeds the maxScale or falls below the minScale
        if (doubleTapToZoomScaleFactor > maxScale) {
            doubleTapToZoomScaleFactor = maxScale;
        }

        if (doubleTapToZoomScaleFactor < minScale) {
            doubleTapToZoomScaleFactor = minScale;
        }
    }


    /**
     * Set the minimum and maximum allowed scale for zooming. {@code minScale} cannot
     * be greater than {@code maxScale} and neither can be 0 or less. This will result
     * in an {@link IllegalStateException}.
     *
     * @param minScale minimum allowed scale
     * @param maxScale maximum allowed scale
     */
    public void setScaleRange(final float minScale, final float maxScale) {
        this.minScale = minScale;
        this.maxScale = maxScale;

        startValues = null;

        verifyScaleRange();
    }


    /**
     * Returns whether the image is translatable.
     *
     * @return true if translation of image is allowed, false otherwise
     */
    public boolean isTranslatable() {
        return translatable;
    }


    /**
     * Set the image's translatable state.
     *
     * @param translatable true to enable translation, false to disable it
     */
    public void setTranslatable(boolean translatable) {
        this.translatable = translatable;
    }


    /**
     * Returns the zoomable state of the image.
     *
     * @return true if pinch-zooming of the image is allowed, false otherwise.
     */
    public boolean isZoomable() {
        return zoomable;
    }


    /**
     * Set the zoomable state of the image.
     *
     * @param zoomable true to enable pinch-zooming of the image, false to disable it
     */
    public void setZoomable(final boolean zoomable) {
        this.zoomable = zoomable;
    }


    /**
     * If restricted bounds are enabled, the image will not be allowed to translate
     * farther inward than the edges of the view's bounds, unless the corresponding
     * dimension (width or height) is smaller than those of the view's frame.
     *
     * @return true if image bounds are restricted to the view's edges, false otherwise
     */
    public boolean getRestrictBounds() {
        return restrictBounds;
    }


    /**
     * Set the restrictBounds status of the image.
     * If restricted bounds are enabled, the image will not be allowed to translate
     * farther inward than the edges of the view's bounds, unless the corresponding
     * dimension (width or height) is smaller than those of the view's frame.
     *
     * @param restrictBounds true if image bounds should be restricted to the view's edges, false otherwise
     */
    public void setRestrictBounds(final boolean restrictBounds) {
        this.restrictBounds = restrictBounds;
    }


    /**
     * Returns status of animateOnReset. This causes the image to smoothly animate back
     * to its start position when reset. Default value is true.
     *
     * @return true if animateOnReset is enabled, false otherwise
     */
    public boolean getAnimateOnReset() {
        return animateOnReset;
    }


    /**
     * Set whether or not the image should animate when resetting.
     *
     * @param animateOnReset true if image should animate when resetting, false to snap
     */
    public void setAnimateOnReset(final boolean animateOnReset) {
        this.animateOnReset = animateOnReset;
    }


    /**
     * Get the current {@link AutoResetMode} mode of the image. Default value is {@link AutoResetMode#UNDER}.
     *
     * @return the current {@link AutoResetMode} mode, one of {@link AutoResetMode#OVER OVER}, {@link AutoResetMode#UNDER UNDER},
     * {@link AutoResetMode#ALWAYS ALWAYS}, or {@link AutoResetMode#NEVER NEVER}
     */
    @AutoResetMode
    public int getAutoResetMode() {
        return autoResetMode;
    }


    /**
     * Set the {@link AutoResetMode} mode for the image.
     *
     * @param autoReset the desired mode, one of {@link AutoResetMode#OVER OVER}, {@link AutoResetMode#UNDER UNDER},
     *                  {@link AutoResetMode#ALWAYS ALWAYS}, or {@link AutoResetMode#NEVER NEVER}
     */
    public void setAutoResetMode(@AutoResetMode final int autoReset) {
        this.autoResetMode = autoReset;
    }


    /**
     * Whether or not the image should automatically center itself when it's dragged partially or
     * fully out of view.
     *
     * @return true if image should center itself automatically, false if it should not
     */
    public boolean getAutoCenter() {
        return autoCenter;
    }


    /**
     * Set whether or not the image should automatically center itself when it's dragged
     * partially or fully out of view.
     *
     * @param autoCenter true if image should center itself automatically, false if it should not
     */
    public void setAutoCenter(final boolean autoCenter) {
        this.autoCenter = autoCenter;
    }


    /**
     * Gets double tap to zoom state.
     *
     * @return whether double tap to zoom is enabled
     */
    public boolean getDoubleTapToZoom() {
        return doubleTapToZoom;
    }


    /**
     * Sets double tap to zoom state.
     *
     * @param doubleTapToZoom true if double tap to zoom should be enabled
     */
    public void setDoubleTapToZoom(boolean doubleTapToZoom) {
        this.doubleTapToZoom = doubleTapToZoom;
    }


    /**
     * Gets the double tap to zoom scale factor.
     *
     * @return double tap to zoom scale factor
     */
    public float getDoubleTapToZoomScaleFactor() {
        return doubleTapToZoomScaleFactor;
    }


    /**
     * Sets the double tap to zoom scale factor. Can be a maximum of max scale.
     *
     * @param doubleTapToZoomScaleFactor the scale factor you want to zoom to when double tap occurs
     */
    public void setDoubleTapToZoomScaleFactor(float doubleTapToZoomScaleFactor) {
        this.doubleTapToZoomScaleFactor = doubleTapToZoomScaleFactor;
        verifyScaleRange();
    }


    /**
     * Get the current scale factor of the image, in relation to its starting size.
     *
     * @return the current scale factor
     */
    public float getCurrentScaleFactor() {
        return currentScaleFactor;
    }


    /**
     * Sets the scale type of this ImageView.
     * <p>
     * This method overrides the behavior of {@link ImageView#setScaleType(ScaleType)} to
     * also update internal state variables used for zooming and resetting.
     *
     * @param scaleType The desired scale type to be set.
     *                  If null, this method does nothing.
     */
    @Override
    public void setScaleType(@Nullable ScaleType scaleType) {
        if (scaleType != null) {
            // Set the scale type using the parent ImageView method
            super.setScaleType(scaleType);

            // Update the initial scale type for reference
            startScaleType = scaleType;

            // Clear the initial scale values to prepare for recalibration
            startValues = null;
        }
    }


    /**
     * Set enabled state of the view. Note that this will reset the image's
     * {@link ScaleType} to its pre-zoom state.
     *
     * @param enabled enabled state
     */
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);

        if (!enabled) {
            setScaleType(startScaleType);
        }
    }


    /**
     * Sets the image resource for this ImageView and adjusts the scale type accordingly.
     * <p>
     * This method overrides the behavior of {@link ImageView#setImageResource(int)}
     * to set the image resource and reset the scale type to the initial value specified
     * during view creation.
     *
     * @param resId The resource ID of the image to be set.
     */
    @Override
    public void setImageResource(int resId) {
        // Set the image resource using the parent ImageView method
        super.setImageResource(resId);

        // Reset the scale type to the initial value specified during view creation
        setScaleType(startScaleType);
    }


    /**
     * Sets the image drawable for this ImageView and adjusts the scale type accordingly.
     * <p>
     * This method overrides the behavior of {@link ImageView#setImageDrawable(Drawable)}
     * to set the image drawable and reset the scale type to the initial value specified
     * during view creation.
     *
     * @param drawable The drawable to be set as the image.
     */
    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        // Set the image drawable using the parent ImageView method
        super.setImageDrawable(drawable);

        // Reset the scale type to the initial value specified during view creation
        setScaleType(startScaleType);
    }


    /**
     * Sets the content of this ImageView to the specified Bitmap.
     * <p>
     * This method overrides the behavior of {@link ImageView#setImageBitmap(Bitmap)} to
     * also reset the scale type to the initial value ({@link #startScaleType}).
     *
     * @param bm The Bitmap to set as the content of this ImageView.
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        // Set the Bitmap content using the parent ImageView method
        super.setImageBitmap(bm);

        // Reset the scale type to the initial value
        setScaleType(startScaleType);
    }


    /**
     * Sets the content of this ImageView to the specified URI.
     * <p>
     * This method overrides the behavior of {@link ImageView#setImageURI(Uri)} to
     * also reset the scale type to the initial value ({@link #startScaleType}).
     *
     * @param uri The URI of the image to set as the content of this ImageView.
     */
    @Override
    public void setImageURI(@Nullable Uri uri) {
        // Set the image URI using the parent ImageView method
        super.setImageURI(uri);

        // Reset the scale type to the initial value
        setScaleType(startScaleType);
    }


    /**
     * Update the bounds of the displayed image based on the current matrix.
     *
     * @param values the image's current matrix values.
     */
    private void updateBounds(final float[] values) {
        if (getDrawable() != null) {
            bounds.set(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y], getDrawable().getIntrinsicWidth() * values[Matrix.MSCALE_X] + values[Matrix.MTRANS_X], getDrawable().getIntrinsicHeight() * values[Matrix.MSCALE_Y] + values[Matrix.MTRANS_Y]);
        }
    }


    /**
     * Get the width of the displayed image.
     *
     * @return the current width of the image as displayed (not the width of the {@link ImageView} itself.
     */
    private float getCurrentDisplayedWidth() {
        if (getDrawable() != null)
            return getDrawable().getIntrinsicWidth() * matrixValues[Matrix.MSCALE_X];
        else return 0;
    }


    /**
     * Get the height of the displayed image.
     *
     * @return the current height of the image as displayed (not the height of the {@link ImageView} itself.
     */
    private float getCurrentDisplayedHeight() {
        if (getDrawable() != null)
            return getDrawable().getIntrinsicHeight() * matrixValues[Matrix.MSCALE_Y];
        else return 0;
    }


    /**
     * Records the initial transformation values of the image matrix for later use in resetting.
     * <p>
     * This method captures the initial values of the image matrix, including scale factors,
     * to enable animating the image back to its original position and scale after interaction stops.
     * <p>
     * The recorded values are used to calculate the minimum and maximum scale bounds
     * considering the current minScale and maxScale settings.
     */
    private void setStartValues() {
        // Initialize the startValues array to store the initial transformation values
        startValues = new float[9];

        // Capture the current image matrix as the startMatrix
        startMatrix = new Matrix(getImageMatrix());

        // Retrieve the transformation values (including scale factors) from the startMatrix
        startMatrix.getValues(startValues);

        // Calculate the adjusted minimum and maximum scale bounds based on the current minScale setting
        calculatedMinScale = minScale * startValues[Matrix.MSCALE_X];
        calculatedMaxScale = maxScale * startValues[Matrix.MSCALE_X];
    }


    /**
     * Handles touch events for zooming, panning, and resetting the image within this ImageView.
     * <p>
     * This method overrides the default touch event handling to enable pinch-to-zoom,
     * translation (panning), and other interactive behaviors based on the current configuration
     * of the ZoomableImageView (zoomable, translatable, etc.).
     *
     * @param event The MotionEvent representing the touch event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Check if the view is clickable and enabled, and if zooming or translation is allowed
        if (!isClickable() && isEnabled() && (zoomable || translatable)) {
            // Ensure the scale type is set to MATRIX for custom transformations
            if (getScaleType() != ScaleType.MATRIX) {
                super.setScaleType(ScaleType.MATRIX);
            }

            // Capture and initialize start values if not already set
            if (startValues == null) {
                setStartValues();
            }

            // Update the current pointer count from the MotionEvent
            currentPointerCount = event.getPointerCount();

            // Get the current image matrix, its values, and update bounds
            matrix.set(getImageMatrix());
            matrix.getValues(matrixValues);
            updateBounds(matrixValues);

            // Handle scale gesture events (pinch-to-zoom)
            scaleDetector.onTouchEvent(event);

            // Handle gesture events (tap, double-tap)
            gestureDetector.onTouchEvent(event);

            // Handle double-tap-to-zoom behavior
            if (doubleTapToZoom && doubleTapDetected) {
                doubleTapDetected = false;
                singleTapDetected = false;
                if (matrixValues[Matrix.MSCALE_X] != startValues[Matrix.MSCALE_X]) {
                    // Reset to original scale if already zoomed in
                    reset();
                } else {
                    // Zoom in to a specified factor centered at the tap location
                    Matrix zoomMatrix = new Matrix(matrix);
                    zoomMatrix.postScale(doubleTapToZoomScaleFactor, doubleTapToZoomScaleFactor, scaleDetector.getFocusX(), scaleDetector.getFocusY());
                    animateScaleAndTranslationToMatrix(zoomMatrix);
                }
                return true;
            } else if (!singleTapDetected) {
                // Handle translation (panning) based on touch movement
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN || currentPointerCount != previousPointerCount) {
                    // Reset the starting touch point for translation
                    last.set(scaleDetector.getFocusX(), scaleDetector.getFocusY());
                } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                    final float focusx = scaleDetector.getFocusX();
                    final float focusy = scaleDetector.getFocusY();

                    // Apply translation if allowed
                    if (allowTranslate()) {
                        float xdistance = getXDistance(focusx, last.x);
                        float ydistance = getYDistance(focusy, last.y);
                        matrix.postTranslate(xdistance, ydistance);
                    }

                    // Apply zoom (scaling) if allowed
                    if (allowZoom()) {
                        matrix.postScale(scaleBy, scaleBy, focusx, focusy);
                        currentScaleFactor = matrixValues[Matrix.MSCALE_X] / startValues[Matrix.MSCALE_X];
                    }

                    // Update the image matrix and record the last touch point
                    setImageMatrix(matrix);
                    last.set(focusx, focusy);
                }

                // Reset scaling factor and image state on touch up or cancel
                if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                    scaleBy = 1f;
                    resetImage();
                }
            }

            // Prevent parent from intercepting touch events if necessary
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(disallowParentTouch());
            }

            // Update the previous pointer count for tracking finger changes
            previousPointerCount = currentPointerCount;

            return true; // Event handled
        }

        // Allow default touch event handling if conditions are not met
        return super.onTouchEvent(event);
    }


    /**
     * Determines whether the parent should disallow intercepting touch events based on the current state.
     * <p>
     * This method is called during touch event processing to decide whether the parent view should
     * disallow intercepting touch events, ensuring smooth interaction within the ZoomableImageView.
     *
     * @return True if parent should disallow intercepting touch events, false otherwise.
     */
    protected boolean disallowParentTouch() {
        // Check conditions based on current touch state
        return currentPointerCount > 1 || (currentScaleFactor > 1.0f && !isScrollToEdge()) || isAnimating();
    }


    /**
     * Checks if the image is scrolled to the edge of the view bounds.
     * <p>
     * This method evaluates whether the current image position is at the edge of the visible
     * bounds (left, right, top, or bottom) of the ZoomableImageView.
     *
     * @return True if the image is scrolled to the edge, false otherwise.
     */
    private boolean isScrollToEdge() {
        // Check if any edge of the image is aligned with the edge of the view
        return bounds.left >= 0.0 || bounds.right <= getWidth() || bounds.top >= 0.0 || bounds.bottom <= getHeight();
    }


    /**
     * Checks if translation (panning) is allowed based on the current configuration and scale factor.
     * <p>
     * This method determines whether translation (panning) of the image is allowed based on
     * the `translatable` flag and the current scale factor (`currentScaleFactor`).
     *
     * @return True if translation is allowed, false otherwise.
     */
    protected boolean allowTranslate() {
        return translatable && currentScaleFactor > 1.0f;
    }


    /**
     * Checks if zooming is allowed based on the current configuration.
     * <p>
     * This method determines whether zooming of the image is allowed based on the `zoomable` flag.
     *
     * @return True if zooming is allowed, false otherwise.
     */
    protected boolean allowZoom() {
        return zoomable;
    }


    /**
     * Checks if an animation (reset animation) is currently running.
     * <p>
     * This method evaluates whether there is an ongoing reset animation (`resetAnimator`) that
     * is currently running.
     *
     * @return True if an animation is running, false otherwise.
     */
    private boolean isAnimating() {
        return resetAnimator != null && resetAnimator.isRunning();
    }


    /**
     * Reset the image based on the specified {@link AutoResetMode} mode.
     * <p>
     * This method resets the image transformation (scale and translation) based on the
     * configured {@link AutoResetMode}. It compares the current scale factor (`matrixValues[Matrix.MSCALE_X]`)
     * with the initial scale factor (`startValues[Matrix.MSCALE_X]`) and performs the following actions
     * based on the selected auto-reset mode:
     * - {@link AutoResetMode#UNDER}: Reset if the scale is less than or equal to the initial scale, otherwise center the image.
     * - {@link AutoResetMode#OVER}: Reset if the scale is greater than or equal to the initial scale, otherwise center the image.
     * - {@link AutoResetMode#ALWAYS}: Always reset the image to its initial state.
     * - {@link AutoResetMode#NEVER}: Center the image without resetting.
     */
    private void resetImage() {
        switch (autoResetMode) {
            case AutoResetMode.UNDER:
                if (matrixValues[Matrix.MSCALE_X] <= startValues[Matrix.MSCALE_X]) {
                    // Reset if scale is under or equal to initial scale
                    reset();
                } else {
                    // Center the image if scale is over initial scale
                    center();
                }
                break;
            case AutoResetMode.OVER:
                if (matrixValues[Matrix.MSCALE_X] >= startValues[Matrix.MSCALE_X]) {
                    // Reset if scale is over or equal to initial scale
                    reset();
                } else {
                    // Center the image if scale is under initial scale
                    center();
                }
                break;
            case AutoResetMode.ALWAYS:
                // Always reset the image
                reset();
                break;
            case AutoResetMode.NEVER:
                // Center the image without resetting
                center();
                break;
        }
    }


    /**
     * This helps to keep the image on-screen by animating the translation to the nearest
     * edge, both vertically and horizontally.
     */
    private void center() {
        if (autoCenter) {
            animateTranslationX();
            animateTranslationY();
        }
    }


    /**
     * Reset image back to its original size. Will snap back to original size
     * if animation on reset is disabled via {@link #setAnimateOnReset(boolean)}.
     */
    public void reset() {
        reset(animateOnReset);
    }


    /**
     * Reset image back to its starting size. If {@code animate} is false, image
     * will snap back to its original size.
     *
     * @param animate animate the image back to its starting size
     */
    public void reset(final boolean animate) {
        if (animate) {
            animateToStartMatrix();
        } else {
            setImageMatrix(startMatrix);
        }
    }


    /**
     * Animate the matrix back to its original position after the user stopped interacting with it.
     */
    private void animateToStartMatrix() {
        animateScaleAndTranslationToMatrix(startMatrix);
    }


    /**
     * Animates the scale and translation of the current matrix to the target matrix.
     * <p>
     * This method animates the transformation (scale and translation) of the current image matrix
     * to the target matrix using a ValueAnimator. It calculates the differences between the current
     * and target matrix values (scale and translation) and applies the interpolated values during
     * the animation to smoothly transition the image.
     *
     * @param targetMatrix The target matrix to animate values towards.
     */
    private void animateScaleAndTranslationToMatrix(final Matrix targetMatrix) {
        // Get values from the target matrix and the current image matrix
        final float[] targetValues = new float[9];
        targetMatrix.getValues(targetValues);

        final Matrix beginMatrix = new Matrix(getImageMatrix());
        beginMatrix.getValues(matrixValues);

        // Calculate differences between current and target matrix values
        final float xsdiff = targetValues[Matrix.MSCALE_X] - matrixValues[Matrix.MSCALE_X];
        final float ysdiff = targetValues[Matrix.MSCALE_Y] - matrixValues[Matrix.MSCALE_Y];
        final float xtdiff = targetValues[Matrix.MTRANS_X] - matrixValues[Matrix.MTRANS_X];
        final float ytdiff = targetValues[Matrix.MTRANS_Y] - matrixValues[Matrix.MTRANS_Y];

        // Notify gesture listener of zoom event direction based on xsdiff
        if (onGestureListener != null) {
            onGestureListener.onZoomEvent(xsdiff > 0);
        }

        // Create a ValueAnimator to interpolate between start and end matrix values
        resetAnimator = ValueAnimator.ofFloat(0, 1f);
        resetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            final Matrix activeMatrix = new Matrix(getImageMatrix());
            final float[] values = new float[9];

            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                float val = (Float) animation.getAnimatedValue();
                activeMatrix.set(beginMatrix);
                activeMatrix.getValues(values);
                // Interpolate matrix values based on animation progress
                values[Matrix.MTRANS_X] = values[Matrix.MTRANS_X] + xtdiff * val;
                values[Matrix.MTRANS_Y] = values[Matrix.MTRANS_Y] + ytdiff * val;
                values[Matrix.MSCALE_X] = values[Matrix.MSCALE_X] + xsdiff * val;
                values[Matrix.MSCALE_Y] = values[Matrix.MSCALE_Y] + ysdiff * val;
                activeMatrix.setValues(values);
                setImageMatrix(activeMatrix); // Apply interpolated matrix to the ImageView
            }
        });

        // Set a listener to update the final matrix when animation completes
        resetAnimator.addListener((AnimatorListener) animation -> {
            setImageMatrix(targetMatrix); // Apply the final target matrix after animation
        });

        resetAnimator.setDuration(200); // Set animation duration (in milliseconds)
        resetAnimator.start(); // Start the animator
    }


    /**
     * Animates the translation (panning) of the image horizontally (along the x-axis) if needed.
     * <p>
     * This method checks the current bounds of the image (`bounds`) relative to the view width
     * to determine if horizontal translation animation is necessary. If the displayed image width
     * exceeds the view width, it ensures that the image edges are within the visible bounds.
     * <p>
     * If the image is larger than the view width:
     * - If the left edge of the image is outside the view, it animates to bring it back to the edge.
     * - If the right edge of the image is outside the view, it animates to adjust the position.
     * <p>
     * If the image fits within the view width:
     * - If the left edge of the image is negative (outside the view), it animates to align it to the edge.
     * - If the right edge of the image exceeds the view width, it animates to adjust the position.
     */
    private void animateTranslationX() {
        if (getCurrentDisplayedWidth() > getWidth()) {
            // The left edge is too far to the right
            if (bounds.left > 0) {
                animateMatrixIndex(Matrix.MTRANS_X, 0);
            }
            // The right edge is too far to the left
            else if (bounds.right < getWidth()) {
                animateMatrixIndex(Matrix.MTRANS_X, bounds.left + getWidth() - bounds.right);
            }
        } else {
            // Image fits within the view width
            // Left edge needs adjustment if negative (outside the view)
            if (bounds.left < 0) {
                animateMatrixIndex(Matrix.MTRANS_X, 0);
            }
            // Right edge needs adjustment if exceeds the view width
            else if (bounds.right > getWidth()) {
                animateMatrixIndex(Matrix.MTRANS_X, bounds.left + getWidth() - bounds.right);
            }
        }
    }


    /**
     * Animates the translation (panning) of the image vertically (along the y-axis) if needed.
     * <p>
     * This method checks the current bounds of the image (`bounds`) relative to the view height
     * to determine if vertical translation animation is necessary. If the displayed image height
     * exceeds the view height, it ensures that the image edges are within the visible bounds.
     * <p>
     * If the image is taller than the view height:
     * - If the top edge of the image is outside the view, it animates to bring it back to the edge.
     * - If the bottom edge of the image is outside the view, it animates to adjust the position.
     * <p>
     * If the image fits within the view height:
     * - If the top edge of the image is negative (outside the view), it animates to align it to the edge.
     * - If the bottom edge of the image exceeds the view height, it animates to adjust the position.
     */
    private void animateTranslationY() {
        if (getCurrentDisplayedHeight() > getHeight()) {
            // The top edge is too far to the bottom
            if (bounds.top > 0) {
                animateMatrixIndex(Matrix.MTRANS_Y, 0);
            }
            // The bottom edge is too far to the top
            else if (bounds.bottom < getHeight()) {
                animateMatrixIndex(Matrix.MTRANS_Y, bounds.top + getHeight() - bounds.bottom);
            }
        } else {
            // Image fits within the view height
            // Top edge needs adjustment if negative (outside the view)
            if (bounds.top < 0) {
                animateMatrixIndex(Matrix.MTRANS_Y, 0);
            }
            // Bottom edge needs adjustment if exceeds the view height
            else if (bounds.bottom > getHeight()) {
                animateMatrixIndex(Matrix.MTRANS_Y, bounds.top + getHeight() - bounds.bottom);
            }
        }
    }


    /**
     * Animates the specified index of the image matrix to a target value using a ValueAnimator.
     * <p>
     * This method animates a specific index (`index`) of the image matrix (`matrixValues`) to
     * a target value (`to`) using a ValueAnimator. The animation updates the matrix values
     * over a specified duration and applies the updated matrix to the ImageView.
     *
     * @param index The index of the matrix value to animate (e.g., Matrix.MTRANS_X or Matrix.MTRANS_Y).
     * @param to    The target value to animate the matrix index towards.
     */
    private void animateMatrixIndex(final int index, final float to) {
        ValueAnimator animator = ValueAnimator.ofFloat(matrixValues[index], to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            final float[] values = new float[9];
            final Matrix current = new Matrix();

            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                current.set(getImageMatrix());
                current.getValues(values);
                values[index] = (Float) animation.getAnimatedValue();
                current.setValues(values);
                setImageMatrix(current);
            }
        });
        animator.setDuration(200); // Animation duration in milliseconds
        animator.start(); // Start the animator
    }


    /**
     * Calculate the x distance to translate the current image based on touch input.
     *
     * @param toX   The current x location of the touch focus.
     * @param fromX The last x location of the touch focus.
     * @return The calculated distance to move the image horizontally.
     * If bounds restriction is enabled, the translation is restricted to keep the image on screen.
     */
    private float getXDistance(final float toX, final float fromX) {
        float xdistance = toX - fromX;

        // Restrict the x distance based on bounds to keep the image on screen
        if (restrictBounds) {
            xdistance = getRestrictedXDistance(xdistance);
        }

        // Prevent the image from translating too far offscreen
        if (bounds.right + xdistance < 0) {
            xdistance = -bounds.right; // Align with the left edge of the screen
        } else if (bounds.left + xdistance > getWidth()) {
            xdistance = getWidth() - bounds.left; // Align with the right edge of the screen
        }

        return xdistance;
    }


    /**
     * Get the horizontal distance to translate the current image, but restrict
     * it to the outer bounds of the {@link ImageView}. If the current
     * image is smaller than the bounds, keep it within the current bounds.
     * If it is larger, prevent its edges from translating farther inward
     * from the outer edge.
     *
     * @param xdistance the current desired horizontal distance to translate
     * @return the actual horizontal distance to translate with bounds restrictions
     */
    private float getRestrictedXDistance(final float xdistance) {
        float restrictedXDistance = xdistance;

        if (getCurrentDisplayedWidth() >= getWidth()) {
            if (bounds.left <= 0 && bounds.left + xdistance > 0 && !scaleDetector.isInProgress()) {
                restrictedXDistance = -bounds.left;
            } else if (bounds.right >= getWidth() && bounds.right + xdistance < getWidth() && !scaleDetector.isInProgress()) {
                restrictedXDistance = getWidth() - bounds.right;
            }
        } else if (!scaleDetector.isInProgress()) {
            if (bounds.left >= 0 && bounds.left + xdistance < 0) {
                restrictedXDistance = -bounds.left;
            } else if (bounds.right <= getWidth() && bounds.right + xdistance > getWidth()) {
                restrictedXDistance = getWidth() - bounds.right;
            }
        }

        return restrictedXDistance;
    }


    /**
     * Get the y distance to translate the current image.
     *
     * @param toY   the current y location of touch focus
     * @param fromY the last y location of touch focus
     * @return the distance to move the image,
     * will restrict the translation to keep the image on screen.
     */
    private float getYDistance(final float toY, final float fromY) {
        float ydistance = toY - fromY;

        if (restrictBounds) {
            ydistance = getRestrictedYDistance(ydistance);
        }

        //prevents image from translating an infinite distance offscreen
        if (bounds.bottom + ydistance < 0) {
            ydistance = -bounds.bottom;
        } else if (bounds.top + ydistance > getHeight()) {
            ydistance = getHeight() - bounds.top;
        }

        return ydistance;
    }


    /**
     * Get the vertical distance to translate the current image, but restrict
     * it to the outer bounds of the {@link ImageView}. If the current
     * image is smaller than the bounds, keep it within the current bounds.
     * If it is larger, prevent its edges from translating farther inward
     * from the outer edge.
     *
     * @param ydistance the current desired vertical distance to translate
     * @return the actual vertical distance to translate with bounds restrictions
     */
    private float getRestrictedYDistance(final float ydistance) {
        float restrictedYDistance = ydistance;

        if (getCurrentDisplayedHeight() >= getHeight()) {
            if (bounds.top <= 0 && bounds.top + ydistance > 0 && !scaleDetector.isInProgress()) {
                restrictedYDistance = -bounds.top;
            } else if (bounds.bottom >= getHeight() && bounds.bottom + ydistance < getHeight() && !scaleDetector.isInProgress()) {
                restrictedYDistance = getHeight() - bounds.bottom;
            }
        } else if (!scaleDetector.isInProgress()) {
            if (bounds.top >= 0 && bounds.top + ydistance < 0) {
                restrictedYDistance = -bounds.top;
            } else if (bounds.bottom <= getHeight() && bounds.bottom + ydistance > getHeight()) {
                restrictedYDistance = getHeight() - bounds.bottom;
            }
        }

        return restrictedYDistance;
    }


    /**
     * Handles the scaling gesture detected by {@link ScaleGestureDetector}.
     *
     * @param detector The {@link ScaleGestureDetector} instance detecting the scale gesture.
     * @return {@code true} to consume the event, {@code false} otherwise.
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        // Calculate the value we should scale by, which will modify the current scale factor
        scaleBy = (startScale * detector.getScaleFactor()) / matrixValues[Matrix.MSCALE_X];

        // Calculate the projected scale after applying the transformation
        final float projectedScale = scaleBy * matrixValues[Matrix.MSCALE_X];

        // Clamp the scale to the minimum and maximum scale limits if it's going beyond
        if (projectedScale < calculatedMinScale) {
            scaleBy = calculatedMinScale / matrixValues[Matrix.MSCALE_X];
        } else if (projectedScale > calculatedMaxScale) {
            scaleBy = calculatedMaxScale / matrixValues[Matrix.MSCALE_X];
        }

        // Notify the gesture listener about zooming state changes
        if (onGestureListener != null) {
            boolean newZoomingState = scaleBy >= 1;
            if (isZooming != newZoomingState) {
                isZooming = newZoomingState;
                onGestureListener.onZoomEvent(isZooming);
            }
        }

        // Return false to indicate that the event is not consumed
        return false;
    }


    /**
     * Called when a scaling gesture begins.
     *
     * @param detector The {@link ScaleGestureDetector} instance detecting the scale gesture.
     * @return {@code true} to continue detecting the scale gesture, {@code false} to ignore further events.
     */
    @Override
    public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
        // Store the initial scale factor when the scaling gesture begins
        startScale = matrixValues[Matrix.MSCALE_X];

        // Return true to continue detecting the scale gesture
        return true;
    }


    /**
     * Called when a scaling gesture ends.
     *
     * @param detector The {@link ScaleGestureDetector} instance detecting the scale gesture.
     */
    @Override
    public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
        // Reset the scaleBy factor to 1 to prepare for the next scaling gesture
        scaleBy = 1f;
    }


    /**
     * Sets the listener for gesture events on the ZoomableImageView.
     * <p>
     * This method allows you to set a custom {@link OnGestureListener} to receive
     * gesture events (such as double tap, single tap, and zoom events) from the ZoomableImageView.
     * The provided listener will be notified of these events during user interactions.
     *
     * @param onGestureListener The listener to be set for handling gesture events.
     *                          Pass {@code null} to clear the current listener.
     */
    public void setOnGestureListener(OnGestureListener onGestureListener) {
        this.onGestureListener = onGestureListener;
    }


}
