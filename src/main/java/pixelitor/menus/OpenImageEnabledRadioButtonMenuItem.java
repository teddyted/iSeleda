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

package pixelitor.menus;

import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.ImageComponents;
import pixelitor.utils.ImageSwitchListener;

import javax.swing.*;

/**
 * A JRadioButtonMenuItem that becomes enabled only if there is an open image
 */
public class OpenImageEnabledRadioButtonMenuItem extends JRadioButtonMenuItem implements ImageSwitchListener {
    public OpenImageEnabledRadioButtonMenuItem(String name) {
        super(name);
        setName(name);
        setEnabled(false);
        ImageComponents.addImageSwitchListener(this);
    }

    @Override
    public void newImageOpened(Composition comp) {
        setEnabled(true);
    }

    @Override
    public void activeImageHasChanged(ImageComponent oldIC, ImageComponent newIC) {

    }

    @Override
    public void noOpenImageAnymore() {
        setEnabled(false);
    }
}