/*
 * Copyright 2015 Laszlo Balazs-Csiki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor. If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor.tools.gradientpaints;

import pixelitor.tools.UserDrag;

import java.awt.Color;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import static java.awt.MultipleGradientPaint.CycleMethod.NO_CYCLE;
import static java.awt.MultipleGradientPaint.CycleMethod.REFLECT;
import static java.awt.MultipleGradientPaint.CycleMethod.REPEAT;

/**
 * A Paint that creates a "diamond gradient"
 */
public class DiamondGradientPaint implements Paint {
    private final UserDrag userDrag;
    private final Color startColor;
    private final Color endColor;
    private final MultipleGradientPaint.CycleMethod cycleMethod;

    private static final int AA_RES = 4; // the resolution of AA supersampling
    private static final int AA_RES2 = AA_RES * AA_RES;

    public DiamondGradientPaint(UserDrag userDrag, Color startColor, Color endColor, MultipleGradientPaint.CycleMethod cycleMethod) {
        this.userDrag = userDrag;
        this.startColor = startColor;
        this.endColor = endColor;
        this.cycleMethod = cycleMethod;
    }

    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        return new DiamondGradientPaintContext(userDrag, startColor, endColor, cm, cycleMethod);
    }

    @Override
    public int getTransparency() {
        int a1 = startColor.getAlpha();
        int a2 = endColor.getAlpha();
        return (((a1 & a2) == 0xFF) ? OPAQUE : TRANSLUCENT);
    }

    static class DiamondGradientPaintContext implements PaintContext {
        private final UserDrag userDrag;
        private final MultipleGradientPaint.CycleMethod cycleMethod;

        private final int startAlpha;
        private final int startRed;
        private final int startGreen;
        private final int startBlue;

        private final int endAlpha;
        private final int endRed;
        private final int endGreen;
        private final int endBlue;

        private final ColorModel cm;

        private final float dragRelDX;
        private final float dragRelDY;
        private final double dragDist;

        private DiamondGradientPaintContext(UserDrag userDrag, Color startColor, Color endColor, ColorModel cm, MultipleGradientPaint.CycleMethod cycleMethod) {
            this.userDrag = userDrag;
            this.cycleMethod = cycleMethod;

            startAlpha = startColor.getAlpha();
            startRed = startColor.getRed();
            startGreen = startColor.getGreen();
            startBlue = startColor.getBlue();

            endAlpha = endColor.getAlpha();
            endRed = endColor.getRed();
            endGreen = endColor.getGreen();
            endBlue = endColor.getBlue();

            this.cm = cm;

            dragDist = userDrag.getDistance();
            double dragDistSqr = dragDist * dragDist;
            dragRelDX = (float) (userDrag.getDX() / dragDistSqr);
            dragRelDY = (float) (userDrag.getDY() / dragDistSqr);
        }

        @Override
        public void dispose() {

        }

        @Override
        public ColorModel getColorModel() {
            return cm;
        }

        @Override
        public Raster getRaster(int startX, int startY, int width, int height) {
            WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
            int[] rasterData = new int[width * height * 4];

            for (int j = 0; j < height; j++) {
                int y = startY + j;
                for (int i = 0; i < width; i++) {
                    int base = (j * width + i) * 4;
                    int x = startX + i;

                    float interpolationValue = getInterpolationValue(x, y);

                    boolean needsAA = false;
                    if (cycleMethod == REPEAT) {
                        double threshold = 1.0 / dragDist;
                        needsAA = interpolationValue > (1.0 - threshold) || interpolationValue < threshold;
                    }

                    final boolean debugAARegion = false;
                    if (needsAA) {
                        if (debugAARegion) {
                            rasterData[base] = 255;
                            rasterData[base + 1] = 255;
                            rasterData[base + 2] = 255;
                            rasterData[base + 3] = 255;
                        } else {
                            int a = 0;
                            int r = 0;
                            int g = 0;
                            int b = 0;

                            for (int m = 0; m < AA_RES; m++) {
                                float yy = (y + 1.0f / AA_RES * m - 0.5f);
                                for (int n = 0; n < AA_RES; n++) {
                                    float xx = x + 1.0f / AA_RES * n - 0.5f;

                                    double interpolationValueAA = getInterpolationValue(xx, yy);

                                    a += (int) (startAlpha + interpolationValueAA * (endAlpha - startAlpha));
                                    r += (int) (startRed + interpolationValueAA * (endRed - startRed));
                                    g += (int) (startGreen + interpolationValueAA * (endGreen - startGreen));
                                    b += (int) (startBlue + interpolationValueAA * (endBlue - startBlue));
                                }
                            }
                            a /= AA_RES2;
                            r /= AA_RES2;
                            g /= AA_RES2;
                            b /= AA_RES2;

                            rasterData[base] = r;
                            rasterData[base + 1] = g;
                            rasterData[base + 2] = b;
                            rasterData[base + 3] = a;
                        }
                    } else { // no AA
                        int a = (int) (startAlpha + interpolationValue * (endAlpha - startAlpha));
                        int r = (int) (startRed + interpolationValue * (endRed - startRed));
                        int g = (int) (startGreen + interpolationValue * (endGreen - startGreen));
                        int b = (int) (startBlue + interpolationValue * (endBlue - startBlue));

                        rasterData[base] = r;
                        rasterData[base + 1] = g;
                        rasterData[base + 2] = b;
                        rasterData[base + 3] = a;
                    }
                }
            }

            raster.setPixels(0, 0, width, height, rasterData);
            return raster;
        }

        public float getInterpolationValue(float x, float y) {
            float dx = x - userDrag.getStartX();
            float dy = y - userDrag.getStartY();

            float v1 = Math.abs((dx * this.dragRelDX) + (dy * this.dragRelDY));
            float v2 = Math.abs((dx * this.dragRelDY) - (dy * this.dragRelDX));

            float interpolationValue = v1 + v2;

            if (cycleMethod == NO_CYCLE) {
                if (interpolationValue > 1.0) {
                    interpolationValue = 1.0f;
                }
            } else if (cycleMethod == REFLECT) {
                interpolationValue %= 1.0;
                if (interpolationValue < 0.5) {
                    interpolationValue = 2.0f * interpolationValue;
                } else {
                    interpolationValue = 2.0f * (1 - interpolationValue);
                }
            } else if (cycleMethod == REPEAT) {
                interpolationValue %= 1.0;
                if (interpolationValue < 0.5) {
                    interpolationValue = 2.0f * interpolationValue;
                } else {
                    interpolationValue = 2.0f * (interpolationValue - 0.5f);
                }
            }
            return interpolationValue;
        }
    }
}