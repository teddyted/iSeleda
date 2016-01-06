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

import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.filters.impl.SepiaFilter;

import java.awt.image.BufferedImage;

/**
 * Sepia filter based on Daniel Wreczycki's sepia filter
 */
public class Sepia extends FilterWithParametrizedGUI  {
    private final RangeParam intensity = new RangeParam("Intensity", 0, 100, 20);

    private SepiaFilter filter;

    public Sepia() {
        super("Sepia", true, false);
        setParamSet(new ParamSet(intensity));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        if(filter == null) {
            filter = new SepiaFilter();
        }

        filter.setIntensity(intensity.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}