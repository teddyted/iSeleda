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
import pixelitor.layers.ImageLayer;
import pixelitor.selection.Selection;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.debug.DataBufferNode;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.util.Optional;

import static pixelitor.Composition.ImageChangeActions.FULL;

/**
 * Represents the changes made to a part of an image (for example brush strokes).
 * Only the affected pixels are saved in order to reduce overall memory usage
 */
public class PartialImageEdit extends FadeableEdit {
    private final Rectangle saveRectangle;
    private final boolean canRepeat;
    private Raster backupRaster;

    private final ImageLayer layer;

    public PartialImageEdit(String name, Composition comp, BufferedImage image, Rectangle saveRectangleParam, boolean canRepeat) {
        super(comp, name);

        this.canRepeat = canRepeat;
        comp.setDirty(true);
        this.layer = comp.getActiveImageLayerOrMask();

        saveRectangle = saveRectangleParam;
        backupRaster = image.getData(saveRectangle);
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        swapRasters();
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        swapRasters();
    }

    private void swapRasters() {
        BufferedImage image = layer.getImage();

        Raster tmpRaster = null;
        try {
            tmpRaster = image.getData(saveRectangle);
            image.setData(backupRaster);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("PartialImageEdit.swapRasters saveRectangle = " + saveRectangle);
            int width = image.getWidth();
            int height = image.getHeight();
            System.out.println("PartialImageEdit.swapRasters width = " + width + ", height = " + height);

            debugRaster("tmpRaster", tmpRaster);
            debugRaster("backupRaster", backupRaster);

            throw e;
        }

        backupRaster = tmpRaster;

        comp.imageChanged(FULL);
        layer.updateIconImage();

        History.notifyMenus(this);
    }

    private void debugRaster(String name, Raster raster) {
        Rectangle rasterBounds = raster.getBounds();
        String className = raster.getClass().getSimpleName();
        DataBuffer dataBuffer = raster.getDataBuffer();
        int dataType = dataBuffer.getDataType();
        String typeAsString = DataBufferNode.getDataBufferTypeDescription(dataType);
        int numBanks = dataBuffer.getNumBanks();
        int numBands = raster.getNumBands();
        int numDataElements = raster.getNumDataElements();

        String msg = String.format("className = %s, rasterBounds = %s, dataType = %d, typeAsString=%s, numBanks = %d, numBands = %d, numDataElements = %d",
                className, rasterBounds, dataType, typeAsString, numBanks, numBands, numDataElements);

        System.out.println("PartialImageEdit::debugRaster debugging raster: " + name + ": " + msg);
    }

    @Override
    public void die() {
        super.die();

        backupRaster = null;
    }

    @Override
    public boolean canRepeat() {
        return canRepeat;
    }

    @Override
    public BufferedImage getBackupImage() {
        // recreate the full image as if it was backed up entirely
        // because Fade expects to fade images of equal size
        // TODO this is not the optimal solution  - Fade should fade only the changed area
        BufferedImage fullImage = layer.getImage();
        BufferedImage previousImage = ImageUtils.copyImage(fullImage);
        previousImage.setData(backupRaster);

        Optional<Selection> selection = layer.getComp().getSelection();
        if (selection.isPresent()) {
            // backupRaster is relative to the full image, but we need to return a selection-sized image
            // TODO this is another ugly hack
            previousImage = layer.getSelectionSizedPartFrom(previousImage, selection.get(), true);
        }

        return previousImage;
    }
}
