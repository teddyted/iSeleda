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
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.filters.impl.SliceFilter;

import java.awt.image.BufferedImage;

/**
 * Slice based on SliceFilter
 */
public class Slice extends FilterWithParametrizedGUI {
    private final RangeParam size = new RangeParam("Size", 0, 100, 6);
    private final RangeParam offset = new RangeParam("Offset", 0, 100, 10);
    private final RangeParam shiftH = new RangeParam("Shift Effect Horizontal", 0, 100, 0);
    private final RangeParam shiftV = new RangeParam("Shift Effect Vertical", 0, 100, 0);
    private final IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();

    private SliceFilter filter;

    public Slice() {
        super("Slice", true, false);
        setParamSet(new ParamSet(
                size.adjustRangeToImageSize(0.25),
                offset.adjustRangeToImageSize(0.25),
                shiftH,
                shiftV,
                edgeAction
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        if(filter == null) {
            filter = new SliceFilter();
        }

        filter.setOffset(offset.getValue());
        filter.setShiftHorizontal(shiftH.getValueAsPercentage());
        filter.setShiftVertical(shiftV.getValueAsPercentage());
        filter.setSize(size.getValue());
        filter.setEdgeAction(edgeAction.getValue());
        filter.setInterpolation(TransformFilter.NEAREST_NEIGHBOUR); // no difference

        dest = filter.filter(src, dest);
        return dest;
    }
}