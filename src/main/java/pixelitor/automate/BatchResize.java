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

package pixelitor.automate;

import pixelitor.PixelitorWindow;
import pixelitor.filters.comp.CompositionUtils;
import pixelitor.utils.CompositionAction;
import pixelitor.utils.IntTextField;
import pixelitor.utils.ValidatedDialog;
import pixelitor.utils.ValidatedForm;

import javax.swing.*;

/**
 * The batch resize functionality
 */
public class BatchResize {
    private BatchResize() { // do not instantiate
    }

    public static void start() {
        BatchResizePanel batchResizePanel = new BatchResizePanel();
        ValidatedDialog chooser = new ValidatedDialog(batchResizePanel, PixelitorWindow.getInstance(), "Batch Resize");
        chooser.setVisible(true);
        if (!chooser.isOkPressed()) {
            return;
        }
        batchResizePanel.saveValues();

        int maxWidth = batchResizePanel.getNewWidth();
        int maxHeight = batchResizePanel.getNewHeight();

        CompositionAction resizeAction = comp -> CompositionUtils.resize(comp, maxWidth, maxHeight, true);
        Automate.processEachFile(resizeAction, true, "Batch Resize...");
    }

    static class BatchResizePanel extends ValidatedForm {
        private String errorMessage;
        private final OpenSaveDirsPanel openSaveDirsPanel = new OpenSaveDirsPanel(false);
        private final IntTextField widthTF;
        private final IntTextField heightTF;

        private BatchResizePanel() {
            JPanel dimensionsPanel = new JPanel();
            dimensionsPanel.add(new JLabel("Max Width:"));
            widthTF = new IntTextField(5);
            widthTF.setName("widthTF");
            widthTF.setText("300");
            dimensionsPanel.add(widthTF);
            dimensionsPanel.add(new JLabel("Max Height:"));
            heightTF = new IntTextField(5);
            heightTF.setName("heightTF");
            heightTF.setText("300");
            dimensionsPanel.add(heightTF);

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(dimensionsPanel);
            add(openSaveDirsPanel);
        }

        @Override
        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public boolean isDataValid() {
            if (!openSaveDirsPanel.isDataValid()) {
                errorMessage = openSaveDirsPanel.getErrorMessage();
                return false;
            }
            if (widthTF.getText().trim().isEmpty()) {
                errorMessage = "The 'width' field is empty";
                return false;
            }
            if (heightTF.getText().trim().isEmpty()) {
                errorMessage = "The 'height' field is empty";
                return false;
            }

            return true;
        }

        private void saveValues() {
            openSaveDirsPanel.saveValues();
        }

        private int getNewWidth() {
            return widthTF.getIntValue();
        }

        private int getNewHeight() {
            return heightTF.getIntValue();
        }
    }
}
