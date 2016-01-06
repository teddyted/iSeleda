/*
 * Copyright 2010-2014 Laszlo Balazs-Csiki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor.  If not, see <http://www.gnu.org/licenses/>.
 */
package pixelitor.filters.jhlabsproxies;

import com.jhlabs.image.DiffuseFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;

import java.awt.image.BufferedImage;

/**
 * Frosted Glass based on the JHLabs DiffuseFilter
 */
public class JHFrostedGlass extends FilterWithParametrizedGUI {
    private final RangeParam amount = new RangeParam("Amount", 1, 100, 10);

    private final IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private final IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();
    private DiffuseFilter filter;

    public JHFrostedGlass() {
        super("Frosted Glass", true, false);
        edgeAction.setDefaultChoice(IntChoiceParam.EDGE_REPEAT_PIXELS);
        setParamSet(new ParamSet(amount.adjustRangeToImageSize(0.1),
                interpolation,
                edgeAction));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new DiffuseFilter();
        }

        filter.setScale(amount.getValueAsFloat());
        filter.setEdgeAction(edgeAction.getValue());
        filter.setInterpolation(interpolation.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}