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
package pixelitor.filters.jhlabsproxies;

import com.jhlabs.image.GlintFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.GradientParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

import static java.awt.Color.WHITE;

/**
 * Glint based on the JHLabs GlintFilter
 */
public class JHGlint extends FilterWithParametrizedGUI {
    private final RangeParam threshold = new RangeParam("Threshold (%)", 0, 100, 70);
    private final RangeParam coverage = new RangeParam("Coverage (%)", 0, 100, 50);
    private final RangeParam intensity = new RangeParam("Intensity (%)", 0, 100, 15);

    private final RangeParam lengthParam = new RangeParam("Length", 0, 100, 20);
    private final RangeParam blur = new RangeParam("Blur", 0, 20, 1);
//    private BooleanParam glintOnly = new BooleanParam("Glint Only", false);

    private final GradientParam colors = new GradientParam("Colors", WHITE, WHITE);


    private GlintFilter filter;

    public JHGlint() {
        super("Glint", true, false);
        setParamSet(new ParamSet(
                threshold,
                coverage,
                intensity,
                lengthParam, // if we adjust to the max of image, render times become unbearable for large images
                blur,
                colors
//                glintOnly
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        int length = lengthParam.getValue();
        if (length == 0) {
            // mot just for performance, a 0 length would cause division by 0
            return src;
        }

        if (filter == null) {
            filter = new GlintFilter();
        }

        filter.setThreshold(threshold.getValueAsPercentage());
        filter.setCoverage(coverage.getValueAsPercentage());
        filter.setAmount(intensity.getValueAsPercentage());
        filter.setLength(length);
        filter.setBlur(blur.getValueAsFloat());
        filter.setColormap(colors.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}