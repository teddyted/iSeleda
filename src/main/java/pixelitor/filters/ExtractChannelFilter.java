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

import java.awt.image.BufferedImage;

/**
 * Extracts a channel from the image
 */
public class ExtractChannelFilter extends Filter {
    private final RGBPixelOp rgbOp;

    public ExtractChannelFilter(String name, RGBPixelOp rgbOp) {
        super(name);
        this.rgbOp = rgbOp;
        listNamePrefix = "Extract Channel: ";
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        return FilterUtils.runRGBPixelOp(rgbOp, src, dest);
    }

    @Override
    public void randomizeSettings() {
        // no settings
    }
}
