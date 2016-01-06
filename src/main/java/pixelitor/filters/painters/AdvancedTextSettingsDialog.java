package pixelitor.filters.painters;

import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.GridBagHelper;
import pixelitor.utils.OKDialog;

import javax.swing.*;
import java.awt.Dialog;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.Map;

public class AdvancedTextSettingsDialog extends OKDialog {
    private final JCheckBox underlineCB;
    private final JCheckBox strikeThroughCB;
    private final JCheckBox kerningCB;
    private final JCheckBox ligaturesCB;
    private final RangeParam trackingParam;

    public AdvancedTextSettingsDialog(Dialog owner, ActionListener actionListener, Map<TextAttribute, Object> map) {
        super(owner, "Advanced Text Settings", "Close");
        JPanel p = new JPanel(new GridBagLayout());
        GridBagHelper gbh = new GridBagHelper(p);

//        if (TextAttribute.UNDERLINE_ON.equals(attributes.get(TextAttribute.UNDERLINE))) {
//            defaultUnderline = true;
//        }

        boolean defaultStrikethrough = false;
        boolean defaultKerning = false;
        boolean defaultUnderline = false;
        boolean defaultLigatures = false;
        int defaultTracking = 0;
        if (map != null) {
            Object strikeThroughSetting = map.get(TextAttribute.STRIKETHROUGH);
            if (TextAttribute.STRIKETHROUGH_ON.equals(strikeThroughSetting)) {
                defaultStrikethrough = true;
            }
            Object kerningSetting = map.get(TextAttribute.KERNING);
            if (TextAttribute.KERNING_ON.equals(kerningSetting)) {
                defaultKerning = true;
            }
            Object underlineSetting = map.get(TextAttribute.UNDERLINE);
            if (TextAttribute.UNDERLINE_ON.equals(underlineSetting)) {
                defaultUnderline = true;
            }
            Object ligaturesSetting = map.get(TextAttribute.LIGATURES);
            if (TextAttribute.LIGATURES_ON.equals(ligaturesSetting)) {
                defaultLigatures = true;
            }

            Float trackingSetting = (Float) map.get(TextAttribute.TRACKING);
            if (trackingSetting != null) {
                defaultTracking = (int) (100 * trackingSetting);
            }
        }
        strikeThroughCB = new JCheckBox("", defaultStrikethrough);
        strikeThroughCB.addActionListener(actionListener);
        gbh.addLabelWithControl("Strikethrough:", strikeThroughCB);

        underlineCB = new JCheckBox("", defaultUnderline);
        underlineCB.addActionListener(actionListener);
        gbh.addLabelWithControl("Underline:", underlineCB);

        kerningCB = new JCheckBox("", defaultKerning);
        kerningCB.addActionListener(actionListener);
        gbh.addLabelWithControl("Kerning:", kerningCB);

        ligaturesCB = new JCheckBox("", defaultLigatures);
        ligaturesCB.addActionListener(actionListener);
        gbh.addLabelWithControl("Ligatures:", ligaturesCB);

        trackingParam = new RangeParam("", -10, 30, 0);
        trackingParam.setValue(defaultTracking);
        trackingParam.addChangeListener(e -> actionListener.actionPerformed(null));
        JComponent trackingGUI = trackingParam.createGUI();
        gbh.addLabelWithControl("Tracking (Letter-spacing):", trackingGUI);

        setupGUI(p, false);
    }

    public void updateMap(Map<TextAttribute, Object> map) {
        Boolean strikeThroughSetting = Boolean.FALSE;
        if (strikeThroughCB.isSelected()) {
            strikeThroughSetting = TextAttribute.STRIKETHROUGH_ON;
        }
        map.put(TextAttribute.STRIKETHROUGH, strikeThroughSetting);

        Integer kerningSetting = 0;
        if (kerningCB.isSelected()) {
            kerningSetting = TextAttribute.KERNING_ON;
        }
        map.put(TextAttribute.KERNING, kerningSetting);

        Integer ligaturesSetting = 0;
        if (ligaturesCB.isSelected()) {
            ligaturesSetting = TextAttribute.LIGATURES_ON;
        }
        map.put(TextAttribute.LIGATURES, ligaturesSetting);

        Integer underlineSetting = -1;
        if (underlineCB.isSelected()) {
            underlineSetting = TextAttribute.UNDERLINE_ON;
        }
        map.put(TextAttribute.UNDERLINE, underlineSetting);

        Float tracking = trackingParam.getValueAsPercentage();
        map.put(TextAttribute.TRACKING, tracking);
    }
}
