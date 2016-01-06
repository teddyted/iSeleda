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

import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.filters.impl.EmptyPolarFilter;

import java.awt.image.BufferedImage;

/**
 *
 */
public class EmptyPolar extends FilterWithParametrizedGUI {
    private final ImagePositionParam centerParam = new ImagePositionParam("Center");

    private final RangeParam zoomParam = new RangeParam("Zoom (%)", 1, 500, 100);
    private final AngleParam rotateResultParam = new AngleParam("Rotate Result", 0);

    private final IntChoiceParam edgeActionParam = IntChoiceParam.getEdgeActionChoices();
    private final IntChoiceParam interpolationParam = IntChoiceParam.getInterpolationChoices();

    private EmptyPolarFilter filter;

    public EmptyPolar() {
        super("Empty Polar", true, false);
        setParamSet(new ParamSet(
                centerParam,
                zoomParam,
                rotateResultParam,
                edgeActionParam,
                interpolationParam
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new EmptyPolarFilter();
        }

        filter.setCenterX(centerParam.getRelativeX());
        filter.setCenterY(centerParam.getRelativeY());
        filter.setEdgeAction(edgeActionParam.getValue());
        filter.setInterpolation(interpolationParam.getValue());
        filter.setRotateResult((float) rotateResultParam.getValueInIntuitiveRadians());
        filter.setZoom(zoomParam.getValueAsPercentage());

        dest = filter.filter(src, dest);
        return dest;
    }
}