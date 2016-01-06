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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.swing.*;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static pixelitor.filters.gui.ColorParam.OpacitySetting.FREE_OPACITY;
import static pixelitor.filters.gui.ColorParam.OpacitySetting.NO_OPACITY;
import static pixelitor.filters.gui.ColorParam.OpacitySetting.USER_ONLY_OPACITY;
import static pixelitor.filters.gui.FilterGUIComponent.EnabledReason.APP_LOGIC;
import static pixelitor.filters.gui.FilterGUIComponent.EnabledReason.FINAL_ANIMATION_SETTING;

@RunWith(Parameterized.class)
public class FilterParamTest {
    private FilterParam param;
    private ParamAdjustmentListenerSpy adjustmentListener;

    @Before
    public void setUp() throws Exception {
        adjustmentListener = new ParamAdjustmentListenerSpy();
        param.setAdjustmentListener(adjustmentListener);
    }

    public FilterParamTest(FilterParam param) {
        this.param = param;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> instancesToTest() {
        return Arrays.asList(new Object[][]{
                {new RangeParam("Param Name", 0, 10, 0)},
                {new RangeWithColorsParam(CYAN, RED, "Param Name", -100, 100, 0)},
                {new GroupedRangeParam("Param Name", 0, 100, 0, true)},
                {new GroupedRangeParam("Param Name", 0, 100, 0, false)},
                {new ImagePositionParam("Param Name")},
                {new GradientParam("Param Name", BLACK, WHITE)},
                {new TextParam("Param Name", "default text")},
                {new ColorParam("Param Name", BLACK, FREE_OPACITY)},
                {new ColorParam("Param Name", WHITE, USER_ONLY_OPACITY)},
                {new ColorParam("Param Name", BLUE, NO_OPACITY)},
                {new BooleanParam("Param Name", true)},
                {new AngleParam("Param Name", 0)},
                {new ElevationAngleParam("Param Name", 0)},
                {new IntChoiceParam("Param Name", new IntChoiceParam.Value[]{
                        new IntChoiceParam.Value("Better", 0),
                        new IntChoiceParam.Value("Faster", 1),
                })},
        });
    }

    @Test
    public void testCreateGUI() {
        JComponent gui = param.createGUI();
        assertNotNull(gui);
        checkThatFilterWasNotCalled();
    }

    @Test
    public void testGetNrOfGridBagCols() {
        int cols = param.getNrOfGridBagCols();
        assertTrue(cols > 0 && cols < 3);
        checkThatFilterWasNotCalled();
    }

    @Test
    public void testRandomize() {
        param.randomize();
        checkThatFilterWasNotCalled();
    }

    @Test
    public void testResetFalse() {
        param.reset(false);
        checkThatFilterWasNotCalled();
        assertTrue(param.isSetToDefault());
    }

    @Test
    public void testResetTrue() {
        // make sure that randomize changes the value
        boolean changed = false;
        while (!changed) {
            param.randomize();
            checkThatFilterWasNotCalled();
            changed = !param.isSetToDefault();
        }

        param.reset(true);
        assertTrue(param.isSetToDefault());

        assertEquals("incorrect number of calls for " + param.getClass().getName(), 1, adjustmentListener.getNumCalled());
    }

    @Test
    public void testCopyAndSetState() {
        try {
            ParamState paramState = param.copyState();
            assertNotNull(paramState);
            param.setState(paramState);
        } catch (UnsupportedOperationException e) {
            // It is OK to throw this exception
        }
        checkThatFilterWasNotCalled();
    }

    @Test
    public void testSimpleMethods() {
        assertEquals("Param Name", param.getName());


        JComponent gui = param.createGUI();
        param.considerImageSize(new Rectangle(0, 0, 1000, 600));

        boolean b = param.canBeAnimated();

        assertTrue(param.getClass().getName() + " is not enabled by default", gui.isEnabled());

        param.setEnabled(false, APP_LOGIC);
        assertFalse(param.getClass().getName() + " was enabled", gui.isEnabled());
        param.setEnabled(true, APP_LOGIC);
        assertTrue(gui.isEnabled());

        param.setEnabled(false, FINAL_ANIMATION_SETTING);
        if (param.canBeAnimated()) {
            assertTrue(gui.isEnabled());
        } else {
            assertFalse(param.getClass().getName() + " was enabled", gui.isEnabled());
        }

        param.setEnabled(true, FINAL_ANIMATION_SETTING);
        assertTrue(gui.isEnabled());

        checkThatFilterWasNotCalled();
    }

    private void checkThatFilterWasNotCalled() {
        assertEquals("Filter was called for " + param.getClass().getName(), 0, adjustmentListener.getNumCalled());
    }
}