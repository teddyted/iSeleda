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

package pixelitor.filters.gui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;

import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static org.junit.Assert.assertNotNull;
import static pixelitor.filters.gui.ColorParam.OpacitySetting.FREE_OPACITY;

@RunWith(Parameterized.class)
public class ParamStateTest {
    private ParamState start;
    private ParamState end;

    public ParamStateTest(ParamState start, ParamState end) {
        this.end = end;
        this.start = start;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> instancesToTest() {
        FilterParam angleParamStart = new AngleParam("AngleParam", 0);
        FilterParam angleParamEnd = new AngleParam("AngleParam", 1);

        FilterParam rangeParamStart = new RangeParam("RangeParam", 0, 100, 10);
        FilterParam rangeParamEnd = new RangeParam("RangeParam", 0, 100, 100);

        FilterParam groupedRangeParamStart = new GroupedRangeParam("GroupedRangeParam", 0, 200, 0);
        FilterParam groupedRangeParamEnd = new GroupedRangeParam("GroupedRangeParam", 0, 200, 0);

        FilterParam gradientParamStart = new GradientParam("GradientParam", Color.BLACK, Color.GREEN);
        FilterParam gradientParamEnd = new GradientParam("GradientParam", BLUE, RED);

        FilterParam imagePositionParamStart = new ImagePositionParam("ImagePositionParam", 0.1f, 0.0f);
        FilterParam imagePositionParamEnd = new ImagePositionParam("ImagePositionParam", 0.9f, 1.0f);

        FilterParam colorParamStart = new ColorParam("ColorParam", RED, FREE_OPACITY);
        FilterParam colorParamEnd = new ColorParam("ColorParam", BLUE, FREE_OPACITY);

        return Arrays.asList(new Object[][]{
                {angleParamStart.copyState(), angleParamEnd.copyState()},
                {rangeParamStart.copyState(), rangeParamEnd.copyState()},
                {groupedRangeParamStart.copyState(), groupedRangeParamEnd.copyState()},
                {gradientParamStart.copyState(), gradientParamEnd.copyState()},
                {imagePositionParamStart.copyState(), imagePositionParamEnd.copyState()},
                {colorParamStart.copyState(), colorParamEnd.copyState()},
        });
    }

    @Test
    public void testInterpolate() {
        ParamState interpolated = start.interpolate(end, 0.0);
        assertNotNull(interpolated);

        interpolated = start.interpolate(end, 0.5);
        assertNotNull(interpolated);

        interpolated = start.interpolate(end, 1.0);
        assertNotNull(interpolated);
    }
}