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

import pixelitor.ImageComponent;
import pixelitor.ImageComponents;
import pixelitor.filters.Filter;
import pixelitor.layers.ImageLayer;
import pixelitor.utils.Dialogs;

import java.awt.event.ActionEvent;

/**
 * A filter that has a GUI for customization
 */
public abstract class FilterWithGUI extends Filter {
    private final String name;

    protected FilterWithGUI(String name) {
        super(name + "...", null);
        this.name = name;
    }

    /**
     * Creates a new adjustment panel for this GUI filter
     * The panel must be created at the moment of this call (cannot be cached)
     * Creating an adjustment panel should also automatically execute the first
     * preview run of this filter based on the default settings
     */
    public abstract AdjustPanel createAdjustPanel();

    /**
     * Returns the menu name, but without the "..." at the end
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ImageComponent ic = ImageComponents.getActiveIC();
        if(ic != null) {
            if (!ic.activeIsImageLayer()) {
                Dialogs.showNotImageLayerDialog();
                return;
            }

            ImageLayer layer = ic.getComp().getActiveImageLayerOrMask();
            layer.startPreviewing();

            AdjustPanel p = createAdjustPanel();
            AdjustDialog.showDialog(p, this);
        }
    }
}
