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
package pixelitor.utils;

import javax.swing.*;
import java.io.File;

public class ConfirmSaveFileChooser extends JFileChooser {
    public ConfirmSaveFileChooser(File currentDirectory) {
        super(currentDirectory);
    }

    public ConfirmSaveFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
    }

    @Override
    public void approveSelection() {
        File f = getSelectedFile();
        if (f.exists()) {
            int userResponse = JOptionPane.showConfirmDialog(
                    this, f.getName() + " exists already. Overwrite?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (userResponse != JOptionPane.OK_OPTION) {
                return;
            }
        }
        super.approveSelection();
    }

}
