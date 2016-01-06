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

import pixelitor.layers.LayersContainer;
import pixelitor.menus.MenuBar;
import pixelitor.tools.FgBgColorSelector;
import pixelitor.tools.ToolSettingsPanelContainer;
import pixelitor.tools.Tools;
import pixelitor.tools.ToolsPanel;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.Dialogs;
import pixelitor.utils.HistogramsPanel;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The main application window.
 */
public class PixelitorWindow extends JFrame {
    private JLabel statusBar;
    private HistogramsPanel histogramsPanel;
    private Box verticalBoxEast;
    private Box verticalBoxWest;
    private ToolsPanel toolsPanel;

    private PixelitorWindow() {
        super(Build.getPixelitorWindowFixTitle());

        setupWindowClosing();

        addMenus();
        addDesktopArea();
        addLayersAndHistograms();
        addToolsPanel();
        addStatusBar();

        setupFrameIcons();

        GlobalKeyboardWatch.init();

        AppPreferences.loadFramePosition(this);
        setVisible(true);
    }

    private void setupWindowClosing() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {
                        AppLogic.exitApp(PixelitorWindow.this);
                    }
                }
        );
    }

    private void addMenus() {
        MenuBar menuBar = new MenuBar(this);
        setJMenuBar(menuBar);
    }

    private void addDesktopArea() {
        add(Desktop.INSTANCE.getDesktopPane(), BorderLayout.CENTER);
    }

    private void addLayersAndHistograms() {
        verticalBoxEast = Box.createVerticalBox();
        histogramsPanel = HistogramsPanel.INSTANCE;
        ImageComponents.addImageSwitchListener(histogramsPanel);

        if (AppPreferences.WorkSpace.getHistogramsVisibility()) {
            verticalBoxEast.add(histogramsPanel);
        }
        if (AppPreferences.WorkSpace.getLayersVisibility()) {
            verticalBoxEast.add(LayersContainer.INSTANCE);
        }

        add(verticalBoxEast, BorderLayout.EAST);
    }

    private void addToolsPanel() {
        verticalBoxWest = Box.createVerticalBox();
        toolsPanel = new ToolsPanel();

        if (AppPreferences.WorkSpace.getToolsVisibility()) {
            verticalBoxWest.add(toolsPanel);
            verticalBoxWest.add(FgBgColorSelector.INSTANCE);
            add(ToolSettingsPanelContainer.INSTANCE, BorderLayout.NORTH);
        }

        add(verticalBoxWest, BorderLayout.WEST);
    }

    private void addStatusBar() {
        statusBar = new JLabel("Pixelitor started");
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        if (AppPreferences.WorkSpace.getStatusBarVisibility()) {
            add(statusBar, BorderLayout.SOUTH);
        }
    }

    private void setupFrameIcons() {
        URL imgURL32 = getClass().getResource("/images/pixelitor_icon32.png");
        URL imgURL48 = getClass().getResource("/images/pixelitor_icon48.png");
        URL imgURL256 = getClass().getResource("/images/pixelitor_icon256.png");

        if (imgURL32 != null) {
            List<Image> icons = new ArrayList<>(2);
            icons.add(new ImageIcon(imgURL32).getImage());
            icons.add(new ImageIcon(imgURL48).getImage());
            icons.add(new ImageIcon(imgURL256).getImage());
            setIconImages(icons);
        } else {
            String message = "icon imgURL is null";
//            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
            Dialogs.showErrorDialog(this, "Error", message);
        }
    }

    public static PixelitorWindow getInstance() {
        return PixelitorWindowHolder.field;
    }

    /**
     * See "Effective Java" 2nd edition, Item 71, page 283
     */
    private static class PixelitorWindowHolder {
        static final PixelitorWindow field = new PixelitorWindow();
    }

    /**
     * Adds a composition to the app.
     */
    public void addComposition(Composition comp) {
        try {
            ImageComponent ic = new ImageComponent(comp);
            ic.setCursor(Tools.getCurrentTool().getCursor());
            ImageComponents.setActiveImageComponent(ic, false);
            comp.addLayersToGUI();

            Desktop.INSTANCE.addNewImageComponent(ic);
        } catch (Exception e) {
            Dialogs.showExceptionDialog(e);
        }
    }

    public void setStatusBarMessage(String msg) {
        statusBar.setText(msg);
    }

    public boolean isStatusBarShown() {
        return (statusBar.getParent() != null);
    }

    public void setStatusBarVisibility(boolean v, boolean revalidate) {
        if (v) {
            add(statusBar, BorderLayout.SOUTH);
        } else {
            remove(statusBar);
        }
        if (revalidate) {
            getContentPane().revalidate();
        }
    }

    public void setHistogramsVisibility(boolean v, boolean revalidate) {
        if (v) {
            verticalBoxEast.add(histogramsPanel);

            ImageComponents.getActiveComp().ifPresent(histogramsPanel::updateFromCompIfShown);
        } else {
            verticalBoxEast.remove(histogramsPanel);
        }
        if (revalidate) {
            verticalBoxEast.revalidate();
        }
    }

    public boolean areHistogramsShown() {
        return histogramsPanel.areHistogramsShown();
    }

    public void setLayersVisibility(boolean v, boolean revalidate) {
        if (v) {
            verticalBoxEast.add(LayersContainer.INSTANCE);
        } else {
            verticalBoxEast.remove(LayersContainer.INSTANCE);
        }
        if (revalidate) {
            verticalBoxEast.revalidate();
        }
    }

    public void setToolsVisibility(boolean v, boolean revalidate) {
        if (v) {
            verticalBoxWest.add(toolsPanel);
            verticalBoxWest.add(FgBgColorSelector.INSTANCE);
            add(ToolSettingsPanelContainer.INSTANCE, BorderLayout.NORTH);

        } else {
            verticalBoxWest.remove(toolsPanel);
            verticalBoxWest.remove(FgBgColorSelector.INSTANCE);
            remove(ToolSettingsPanelContainer.INSTANCE);
        }
        if (revalidate) {
            getContentPane().revalidate();
        }
    }

    public boolean areToolsShown() {
        return (toolsPanel.getParent() != null);
    }

    /**
     * This method iconifies a frame; the maximized bits are not affected.
     */
    public void iconify() {
        int state = getExtendedState();

        // Set the iconified bit
        state |= Frame.ICONIFIED;

        // Iconify the frame
        setExtendedState(state);
    }

    /**
     * This method deiconifies a frame; the maximized bits are not affected.
     */
    public void deiconify() {
        int state = getExtendedState();

        // Clear the iconified bit
        state &= ~Frame.ICONIFIED;

        // Deiconify the frame
        setExtendedState(state);
    }
}

