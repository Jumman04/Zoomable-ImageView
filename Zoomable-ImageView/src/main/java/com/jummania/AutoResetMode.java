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

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Describes how the {@link ZoomableImageView} will reset to its original size
 * once interaction with it stops.
 *
 * <p>{@link #UNDER} will reset when the image is smaller than or equal to its starting size,
 * {@link #OVER} when it's larger than or equal to its starting size,
 * {@link #ALWAYS} in both situations,
 * and {@link #NEVER} causes no reset. Note that when using {@link #NEVER},
 * the image will still animate to within the screen bounds in certain situations.
 *
 * @see ZoomableImageView
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({AutoResetMode.NEVER, AutoResetMode.UNDER, AutoResetMode.OVER, AutoResetMode.ALWAYS})
public @interface AutoResetMode {

    /**
     * Indicates that the image will reset when smaller than or equal to its starting size.
     */
    int UNDER = 0;

    /**
     * Indicates that the image will reset when larger than or equal to its starting size.
     */
    int OVER = 1;

    /**
     * Indicates that the image will always reset regardless of its size.
     */
    int ALWAYS = 2;

    /**
     * Indicates that the image will never reset once interaction stops.
     */
    int NEVER = 3;

    /**
     * A utility class to parse integer values into {@link AutoResetMode} constants.
     */
    class Parser {

        /**
         * Converts an integer value to the corresponding {@link AutoResetMode} constant.
         *
         * @param value The integer value to convert.
         * @return The {@link AutoResetMode} constant corresponding to the input value.
         */
        @AutoResetMode
        public static int fromInt(final int value) {
            switch (value) {
                case OVER:
                    return OVER;
                case ALWAYS:
                    return ALWAYS;
                case NEVER:
                    return NEVER;
                default:
                    return UNDER;
            }
        }
    }

}

