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
package pixelitor.filters;

import com.jhlabs.image.TransformFilter;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.filters.impl.Sphere3DFilter;

import java.awt.image.BufferedImage;

/**
 * Sphere3D based on Sphere3DFilter
 */
public class Sphere3D extends FilterWithParametrizedGUI {
    private final RangeParam alpha = new RangeParam("alpha", 0, 100, 50);
    private final RangeParam beta = new RangeParam("beta", 0, 100, 50);
    private final RangeParam gamma = new RangeParam("gamma", 0, 100, 50);
    private final ImagePositionParam center = new ImagePositionParam("Center");
    private final IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private Sphere3DFilter filter;

    public Sphere3D() {
        super("Sphere3D", true, false);
        setParamSet(new ParamSet(
                center,
                alpha,
                beta,
                gamma,
                interpolation
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        if(filter == null) {
            filter = new Sphere3DFilter();
        }

        filter.setAlpha(alpha.getValueAsPercentage());
        filter.setBeta(beta.getValueAsPercentage());
        filter.setGamma(gamma.getValueAsPercentage());

        filter.setCenterX(center.getRelativeX());
        filter.setCenterY(center.getRelativeY());
        filter.setInterpolation(interpolation.getValue());
        filter.setEdgeAction(TransformFilter.TRANSPARENT);

        dest = filter.filter(src, dest);
        return dest;
    }
}
