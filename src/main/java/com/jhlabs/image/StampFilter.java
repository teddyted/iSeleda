/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.jhlabs.image;

import org.jdesktop.swingx.image.FastBlurFilter;

import java.awt.image.BufferedImage;

/**
 * A filter which produces a rubber-stamp type of effect by performing a thresholded blur.
 */
public class StampFilter extends PointFilter {
    public static final int FAST_BLUR = 1;
    public static final int BOX3_BLUR = 2;
    public static final int GAUSSIAN_BLUR = 3;
    private int blurMethod = FAST_BLUR;

    private float threshold;
    private float softness = 0;
    private float radius = 5;
    private float lowerThreshold3;
    private float upperThreshold3;
    private int white = 0xffffffff;
    private int black = 0xff000000;

    /**
     * Construct a StampFilter.
     */
    public StampFilter() {
        this(0.5f);
    }

    /**
     * Construct a StampFilter.
     *
     * @param threshold the threshold value
     */
    public StampFilter(float threshold) {
        setThreshold(threshold);
    }

    /**
     * Set the radius of the effect.
     *
     * @param radius the radius
     * @min-value 0
     * @see #getRadius
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * Get the radius of the effect.
     *
     * @return the radius
     * @see #setRadius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Set the threshold value.
     *
     * @param threshold the threshold value
     * @see #getThreshold
     */
    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    /**
     * Get the threshold value.
     *
     * @return the threshold value
     * @see #setThreshold
     */
    public float getThreshold() {
        return threshold;
    }

    /**
     * Set the softness of the effect in the range 0..1.
     *
     * @param softness the softness
     * @min-value 0
     * @max-value 1
     * @see #getSoftness
     */
    public void setSoftness(float softness) {
        this.softness = softness;
    }

    /**
     * Get the softness of the effect.
     *
     * @return the softness
     * @see #setSoftness
     */
    public float getSoftness() {
        return softness;
    }

    /**
     * Set the color to be used for pixels above the upper threshold.
     *
     * @param white the color
     * @see #getWhite
     */
    public void setWhite(int white) {
        this.white = white;
    }

    /**
     * Get the color to be used for pixels above the upper threshold.
     *
     * @return the color
     * @see #setWhite
     */
    public int getWhite() {
        return white;
    }

    /**
     * Set the color to be used for pixels below the lower threshold.
     *
     * @param black the color
     * @see #getBlack
     */
    public void setBlack(int black) {
        this.black = black;
    }

    /**
     * Set the color to be used for pixels below the lower threshold.
     *
     * @return the color
     * @see #setBlack
     */
    public int getBlack() {
        return black;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        if(blurMethod == FAST_BLUR) {
            dst = new FastBlurFilter((int) radius).filter(src, null);
        } else if (blurMethod == BOX3_BLUR) {
            if ((src.getWidth() == 1) || (src.getHeight() == 1)) {
                // otherwise we get ArrayIndexOutOfBoundsException in BoxBlurFilter
                return src;
            }
            dst = new BoxBlurFilter(radius, radius, 3).filter(src, null);
        } else if(blurMethod == GAUSSIAN_BLUR) {
            dst = new GaussianFilter(radius).filter(src, null);
        } else {
            throw new IllegalStateException("blurMethod = " + blurMethod);
        }

        lowerThreshold3 = 255 * 3 * (threshold - softness * 0.5f);
        upperThreshold3 = 255 * 3 * (threshold + softness * 0.5f);
        return super.filter(dst, dst);
    }

    @Override
    public int filterRGB(int x, int y, int rgb) {
//        int a = rgb & 0xff000000;
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        int l = r + g + b;
        float f = ImageMath.smoothStep(lowerThreshold3, upperThreshold3, l);
        return ImageMath.mixColors(f, black, white);
	}

	public String toString() {
		return "Stylize/Stamp...";
	}

    public void setBlurMethod(int blurMethod) {
        this.blurMethod = blurMethod;
    }
}
