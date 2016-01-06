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

package pixelitor.filters.animation;

import pixelitor.ChangeReason;
import pixelitor.ImageComponent;
import pixelitor.ImageComponents;
import pixelitor.PixelitorWindow;
import pixelitor.filters.Filter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.ParamSetState;
import pixelitor.layers.ImageLayer;
import pixelitor.utils.Dialogs;
import pixelitor.utils.Utils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

class RenderFramesTask extends SwingWorker<Void, Void> {
    private final TweenAnimation animation;

    public RenderFramesTask(TweenAnimation tweenAnimation) {
        this.animation = tweenAnimation;
    }

    @SuppressWarnings("ProhibitedExceptionDeclared")
    @Override
    protected Void doInBackground() throws Exception {
        try {
            renderFrames();
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> Dialogs.showExceptionDialog(e));
        }

        return null;
    }

    private void renderFrames() {
        int numFrames = animation.getNumFrames();
        FilterWithParametrizedGUI filter = animation.getFilter();

        AnimationWriter animationWriter = animation.createAnimationWriter();
        boolean canceled = false;

        ImageLayer activeImageLayer = ImageComponents.getActiveImageLayer().get();
        PixelitorWindow busyCursorParent = PixelitorWindow.getInstance();

        activeImageLayer.tweenCalculatingStarted();

        int numTotalFrames = numFrames;
        boolean pingPong = animation.isPingPong() && numFrames > 2;
        if (pingPong) {
            numTotalFrames = 2 * numFrames - 2;
        }

        for (int frameNr = 0; frameNr < numTotalFrames; frameNr++) {
            if (isCancelled()) {
                canceled = true;
                break;
            }
            int percentProgress = (int) ((100.0 * frameNr) / numTotalFrames);
            setProgress(percentProgress);

            double time;
            if (frameNr < numFrames) { // ping: normal animation forwards
                time = ((double) frameNr) / numFrames;
            } else { // pong: animating backwards
                // TODO we are calculating the same frames again
                // they could be cached somewhere
                // perhaps in an array of soft references with the calculated frames
                // or on the disk
                int effectiveFrame = 2 * (numFrames - 1) - frameNr;
                time = ((double) effectiveFrame) / numFrames;
            }

            BufferedImage image = renderFrame(filter, time, busyCursorParent);

            try {
                animationWriter.addFrame(image);
            } catch (IOException e) {
                canceled = true;
                Dialogs.showExceptionDialog(e);
                break;
            }
        }

        setProgress(100);
        activeImageLayer.tweenCalculatingEnded();

        if (canceled) {
            animationWriter.cancel();
        } else {
            animationWriter.finish();
        }
    }

    private BufferedImage renderFrame(FilterWithParametrizedGUI filter, double time, PixelitorWindow busyCursorParent) {
        long runCountBefore = Filter.runCount;

        ParamSetState intermediateState = animation.tween(time);
        filter.getParamSet().setState(intermediateState);

        Utils.executeFilterWithBusyCursor(filter, ChangeReason.TWEEN_PREVIEW, busyCursorParent);

        long runCountAfter = Filter.runCount;
        assert runCountAfter == runCountBefore + 1;

        ImageComponent ic = ImageComponents.getActiveIC();
        ic.repaint();

        return ImageComponents.getActiveCompositeImage().get();
    }

    @Override
    protected void done() {
    }
}
