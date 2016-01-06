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

package pixelitor;

import pixelitor.utils.AppPreferences;
import pixelitor.utils.GridBagHelper;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.IntTextField;
import pixelitor.utils.OKCancelDialog;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import static pixelitor.FillType.TRANSPARENT;

/**
 * Static utility methods related to creating new images
 */
public final class NewImage {
    private static int untitledCount = 1;

    private static Dimension lastNew;

    private NewImage() {
    }

    public static void addNewImage(FillType bg, int width, int height, String title) {
        Composition comp = createNewComposition(bg, width, height, title);
        PixelitorWindow.getInstance().addComposition(comp);
    }

    public static Composition createNewComposition(FillType bg, int width, int height, String title) {
//        BufferedImage newImage = new BufferedImage(width, height, TYPE_INT_ARGB_PRE);
        BufferedImage newImage = ImageUtils.createCompatibleImage(width, height);
        fillImage(newImage, bg);
        return Composition.fromImage(newImage, null, title);
    }

    private static void fillImage(BufferedImage img, FillType bg) {
        if (bg == TRANSPARENT) {
            return;
        }
        Color c = bg.getColor();
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        Graphics2D g = img.createGraphics();
        g.setColor(c);
        g.fillRect(0, 0, imgWidth, imgHeight);
        g.dispose();
    }

    private static void showInDialog() {
        assert SwingUtilities.isEventDispatchThread();
        if(lastNew == null) {
            //noinspection NonThreadSafeLazyInitialization
            lastNew = AppPreferences.getNewImageSize();
        }
        NewImagePanel p = new NewImagePanel(lastNew.width, lastNew.height);
        OKCancelDialog d = new OKCancelDialog(p, "New Image") {
            @Override
            public void dialogAccepted() {
                int selectedWidth = p.getSelectedWidth();
                int selectedHeight = p.getSelectedHeight();
                FillType bg = p.getSelectedBackground();

                String title = "Untitled" + untitledCount;
                addNewImage(bg, selectedWidth, selectedHeight, title);
                untitledCount++;

                lastNew.width = selectedWidth;
                lastNew.height = selectedHeight;

                close();
            }
        };
        d.setVisible(true);
    }

    public static Action getAction() {
        return new AbstractAction("New Image...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInDialog();
            }
        };
    }

    public static Dimension getLastNew() {
        return lastNew;
    }

    private static class NewImagePanel extends JPanel {
        private final JTextField widthTextField;
        private final JTextField heightTextField;

        private static final int BORDER_WIDTH = 5;
        private final JComboBox<FillType> backgroundSelector;

        private NewImagePanel(int defaultWidth, int defaultHeight) {
            setLayout(new GridBagLayout());
            GridBagHelper gridBagHelper = new GridBagHelper(this);

            setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));

            widthTextField = new IntTextField(String.valueOf(defaultWidth));
            widthTextField.setName("widthTF");
            gridBagHelper.addLabelWithControl("Width:", widthTextField);

            heightTextField = new IntTextField(String.valueOf(defaultHeight));
            heightTextField.setName("heightTF");
            gridBagHelper.addLabelWithControl("Height:", heightTextField);

            backgroundSelector = new JComboBox(FillType.values());
            gridBagHelper.addLabelWithControl("Fill:", backgroundSelector);
        }

        private int getSelectedWidth() {
            return Integer.parseInt(widthTextField.getText());
        }

        private int getSelectedHeight() {
            return Integer.parseInt(heightTextField.getText());
        }

        private FillType getSelectedBackground() {
            return (FillType) backgroundSelector.getSelectedItem();
        }
    }
}

