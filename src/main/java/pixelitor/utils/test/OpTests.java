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

package pixelitor.utils.test;

import pixelitor.Build;
import pixelitor.ChangeReason;
import pixelitor.Composition;
import pixelitor.ImageComponents;
import pixelitor.PixelitorWindow;
import pixelitor.automate.SingleDirChooserPanel;
import pixelitor.filters.Canny;
import pixelitor.filters.Fade;
import pixelitor.filters.Filter;
import pixelitor.filters.FilterUtils;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.Lightning;
import pixelitor.filters.RandomFilter;
import pixelitor.filters.comp.CompositionUtils;
import pixelitor.filters.gui.ParametrizedAdjustPanel;
import pixelitor.history.History;
import pixelitor.io.FileChoosers;
import pixelitor.io.OutputFormat;
import pixelitor.layers.ImageLayer;
import pixelitor.utils.Dialogs;
import pixelitor.utils.Utils;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 *
 */
public class OpTests {
    /**
     * Utility class with static methods
     */
    private OpTests() {
    }

    public static void saveTheResultOfEachFilter() {

        boolean cancelled = !SingleDirChooserPanel.selectOutputDir(true);
        if (cancelled) {
            return;
        }
        File selectedDir = FileChoosers.getLastSaveDir();
        OutputFormat outputFormat = OutputFormat.getLastOutputFormat();

        ParametrizedAdjustPanel.setResetParams(false);
        ProgressMonitor progressMonitor = Utils.createPercentageProgressMonitor("Saving the Results of Each Operation");

        ImageLayer imageLayer = ImageComponents.getActiveImageLayer().get();
        imageLayer.startPreviewing();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                if (selectedDir != null) {
                    Filter[] filters = FilterUtils.getFiltersShuffled(
                            filter -> (!(filter instanceof Fade)) // TODO Fade just hangs... (Threads?)
                                    && (!(filter instanceof RandomFilter))
                                    && filter.isEnabled());
                    progressMonitor.setProgress(0);

                    for (int i = 0; i < filters.length; i++) {
                        Filter filter = filters[i];
                        String filterName = filter.getName();
                        System.out.println(String.format("OpTests::doInBackground: filterName = '%s'", filterName));

                        progressMonitor.setProgress((int) ((float) i * 100 / filters.length));


                        progressMonitor.setNote("Running " + filter.getMenuName());
                        if (progressMonitor.isCanceled()) {
                            break;
                        }

                        filter.randomizeSettings();
                        filter.execute(ChangeReason.TEST_WITH_HISTORY_AND_PREVIEW);
                        Composition comp = ImageComponents.getActiveComp().get();
                        String fileName = "test_" + Utils.toFileName(filter.getMenuName()) + '.' + outputFormat.toString();
                        File f = new File(selectedDir, fileName);
                        outputFormat.saveComposition(comp, f, false);

                        if (History.canUndo()) {
                            History.undo();
                        }

                    }
                    progressMonitor.close();
                }
                return null;
            }

            // Executed in EDT
            @Override
            protected void done() {
                try {
                    System.out.println("Done");
                    get();
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    Dialogs.showExceptionDialog(cause);
                } catch (InterruptedException e) {
                    // Process e here
                } finally {
                    imageLayer.stopPreviewing(); // reset to NORMAL
                    ParametrizedAdjustPanel.setResetParams(true);
                }
            }
        };
        worker.execute();
    }

    public static void runAllFiltersOnCurrentLayer() {
        ParametrizedAdjustPanel.setResetParams(false);
        try {
            ProgressMonitor progressMonitor = new ProgressMonitor(PixelitorWindow.getInstance(),
                    "Run All Filters on Current Layer",
                    "", 0, 100);

            progressMonitor.setProgress(0);

            // It is best to run this on the current EDT thread, using SwingWorker leads to strange things here

            Filter[] allOps = FilterUtils.getFiltersShuffled(
                    filter -> !(filter instanceof RandomFilter));

            for (int i = 0, allOpsLength = allOps.length; i < allOpsLength; i++) {
                progressMonitor.setProgress((int) ((float) i * 100 / allOpsLength));
                Filter op = allOps[i];

                String msg = "Running " + op.getMenuName();

                progressMonitor.setNote(msg);
                if (progressMonitor.isCanceled()) {
                    break;
                }

                op.randomizeSettings();
                op.actionPerformed(null);
            }
            progressMonitor.close();
        } finally {
            ParametrizedAdjustPanel.setResetParams(true);
        }
    }

    public static void getCompositeImagePerformanceTest() {
        Composition comp = ImageComponents.getActiveComp().get();

        Runnable task = () -> {
            long startTime = System.nanoTime();
            int times = 100;
            for (int i = 0; i < times; i++) {
                comp.getCompositeImage();
            }

            long totalTime = (System.nanoTime() - startTime) / 1000000;
            String msg = String.format(
                    "Executing getCompositeImage() %d times took %d ms, average time = %d ms",
                    times, totalTime, totalTime / times);
            Dialogs.showInfoDialog("Test Result", msg);
        };
        Utils.executeWithBusyCursor(task);
    }

    public static void randomResize() {
        ImageComponents.getActiveComp().ifPresent(comp -> {
            int targetWidth = 10 + RobotTest.rand.nextInt(1200);
            int targetHeight = 10 + RobotTest.rand.nextInt(800);
            CompositionUtils.resize(comp, targetWidth, targetHeight, false);
        });
    }

    public static void findSlowestFilter() {
        assert SwingUtilities.isEventDispatchThread();
        Build.CURRENT.setPerformanceTest(true);

        Filter[] filters = FilterUtils.getFiltersShuffled(
                f -> (!(f instanceof Fade || f instanceof Canny || f instanceof Lightning))
                        && f.isEnabled() && (f instanceof FilterWithParametrizedGUI));

        Map<String, Double> results = new HashMap<>();

        ImageLayer imageLayer = ImageComponents.getActiveImageLayer().get();
        imageLayer.startPreviewing();

        // warmup
        for (Filter filter : filters) {
            System.out.println("Warmup for " + filter.getName());

            filter.execute(ChangeReason.TEST_WITH_HISTORY_AND_PREVIEW);
        }

        // measuring
        for (Filter filter : filters) {
            long startTime = System.nanoTime();
            System.out.println("Testing " + filter.getName());

            filter.execute(ChangeReason.TEST_WITH_HISTORY_AND_PREVIEW);

            double estimatedSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;
            results.put(filter.getName(), estimatedSeconds);
        }
        imageLayer.stopPreviewing();

        Arrays.sort(filters, (o1, o2) -> {
            double o1Time = results.get(o1.getName());
            double o2Time = results.get(o2.getName());
            return (o1Time - o2Time) > 0 ? 1 : -1;
        });
        for (Filter filter : filters) {
            String name = filter.getName();
            double time = results.get(name);
            System.out.println(String.format("%s: %.2f seconds", name, time));
        }
    }
}