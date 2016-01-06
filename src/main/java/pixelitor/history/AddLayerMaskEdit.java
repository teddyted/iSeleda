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
package pixelitor.history;

import pixelitor.Composition;
import pixelitor.layers.Layer;
import pixelitor.layers.LayerMask;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * A PixelitorEdit that represents the adding of a layer mask
 */
public class AddLayerMaskEdit extends PixelitorEdit {
    private Layer layer;
    private LayerMask layerMask;

    public AddLayerMaskEdit(Composition comp, Layer layer) {
        super(comp, "Add Layer Mask");
        comp.setDirty(true);
        this.layer = layer;
        this.layerMask = layer.getMask();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        layer.deleteMask(AddToHistory.NO, false);

        History.notifyMenus(this);
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        layer.addMaskBack(layerMask);

        History.notifyMenus(this);
    }

    @Override
    public void die() {
        super.die();

        layer = null;
        layerMask = null;
    }

    @Override
    public boolean canRepeat() {
        return false;
    }
}
