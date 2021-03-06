/*
 * Copyright 2009-2014 Laszlo Balazs-Csiki
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
package pixelitor.filters.lookup;

import pixelitor.filters.gui.AdjustPanel;
import pixelitor.filters.levels.gui.LevelsPanel;

/**
 * The Levels filter
 */
public class Levels extends DynamicLookupFilter {
    public Levels() {
        super("Levels");
    }

    @Override
    public AdjustPanel createAdjustPanel() {
        return new LevelsPanel(this);
    }
}