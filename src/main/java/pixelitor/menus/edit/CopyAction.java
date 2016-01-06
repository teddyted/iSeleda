/*
 * Copyright (c) 2015 Laszlo Balazs-Csiki
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

package pixelitor.menus.edit;

import pixelitor.Build;
import pixelitor.ImageComponents;
import pixelitor.utils.Dialogs;
import pixelitor.utils.debug.BufferedImageNode;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

/**
 * Copies an image to the system clipboard
 */
public class CopyAction extends AbstractAction {
    private final CopySource source;

    public CopyAction(CopySource source) {
        super(source.toString());
        this.source = source;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if(!ImageComponents.isActiveLayerImageLayer()) {
                // TODO we could try to rasterize text layers

                if(!Build.CURRENT.isRobotTest()) {
                    Dialogs.showNotImageLayerDialog();
                }
                return;
            }

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            BufferedImage activeImage = source.getImage();
            Transferable imageTransferable = new ImageTransferable(activeImage);

            try {
                // Sun Jan 04 08:00:30 CET 2015, robot test:
                // java.awt.image.RasterFormatException: Incorrect scanline stride: 12
                // at sun.awt.image.ByteComponentRaster.verify(ByteComponentRaster.java:894)
                // at sun.awt.image.ByteComponentRaster.<init>(ByteComponentRaster.java:201)
                // https://bugs.openjdk.java.net/browse/JDK-8041558
                clipboard.setContents(imageTransferable, null);
            } catch (RasterFormatException rfe) {
                rfe.printStackTrace();
                BufferedImageNode node = new BufferedImageNode(activeImage);
                String s = node.toDetailedString();
                System.out.println(String.format("CopyAction::RasterFormatException in actionPerformed: s = '%s'", s));
            }
        } catch (Exception ex) {
            Dialogs.showExceptionDialog(ex);
        }
    }
}

