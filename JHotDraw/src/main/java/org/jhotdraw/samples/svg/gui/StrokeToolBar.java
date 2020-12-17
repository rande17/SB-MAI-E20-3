/*
 * @(#)StrokeToolBar.java  1.2  2008-05-23
 *
 * Copyright (c) 2007-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.samples.svg.gui;

import dk.sdu.mmmi.featuretracer.lib.FeatureEntryPoint;
import org.jhotdraw.text.JavaNumberFormatter;
import javax.swing.border.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.gui.plaf.palette.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.plaf.SliderUI;
import org.jhotdraw.app.JHotDrawFeatures;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.text.ColorFormatter;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * StrokeToolBar.
 *
 * @author Werner Randelshofer
 * @version 1.2 2008-05-23 Hide the toolbar if nothing is selected, and no
 * creation tool is active.
 * <br>1.1 2008-03-26 Don't draw button borders.
 * <br>1.0 May 1, 2007 Created.
 */
public class StrokeToolBar extends AbstractToolBar {

    private SelectionComponentDisplayer displayer;
    private JPanel p;

    /**
     * Creates new instance.
     */
    public StrokeToolBar() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
        setName(labels.getString("stroke.toolbar"));
        setDisclosureStateCount(3);
    }

    @Override
    public void setEditor(DrawingEditor newValue) {
        DrawingEditor oldValue = getEditor();
        if (displayer != null) {
            displayer.dispose();
            displayer = null;
        }
        super.setEditor(newValue);
        if (newValue != null) {
            displayer = new SelectionComponentDisplayer(editor, this);
        }
    }

    /**
     * Adds a layout for the stroke functionalities and consequently adds the
     * buttons associated with the functionality
     *
     * @param state Depending on the state if should add with field or not
     * @return return the JPanel with the added buttons
     */
    @Override
    @FeatureEntryPoint(JHotDrawFeatures.STROKE_PALETTE)
    protected JComponent createDisclosedComponent(int state) {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
        Map<AttributeKey, Object> defaultAttributes = new HashMap<>();
        AbstractButton btn = ButtonFactory.createSelectionColorButton(editor,
                STROKE_COLOR, ButtonFactory.HSV_COLORS, ButtonFactory.HSV_COLORS_COLUMN_COUNT,
                "attribute.strokeColor", labels, defaultAttributes, new Rectangle(3, 3, 10, 10));
        STROKE_GRADIENT.set(defaultAttributes, null);
        switch (state) {
            case 1: {
                createLayout();
                createStrokeButtons(labels, btn, defaultAttributes, false);
            }
            break;
            case 2: {
                createLayout();
                createStrokeFields(labels, defaultAttributes, btn);
                createStrokeButtons(labels, btn, defaultAttributes, true);
            }
            break;
        }
        return p;
    }

    void createStrokeButtons(ResourceBundleUtil labels, AbstractButton btn, Map<AttributeKey, Object> defaultAttributes, boolean hasFields) {
        createStrokeColorButton(labels, btn, defaultAttributes, hasFields);
        createOpacitySlider(labels, hasFields);
        createStrokeWidthPopupSliderButton(labels, hasFields);
        createStrokeJoinButton(labels, btn, hasFields);
        createStrokeCapButton(labels, btn, hasFields);
        createStrokeDashesButton(labels, btn, hasFields);

    }

    void createStrokeFields(ResourceBundleUtil labels, Map<AttributeKey, Object> defaultAttributes, AbstractButton btn) {
        createStrokeColorField(labels, btn, defaultAttributes);
        createOpacityField(labels);
        createStrokeWidthField(labels);
        createStrokeDashField(labels);

    }

    void createLayout() {
        p = new JPanel();
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(5, 5, 5, 8));
        GridBagLayout layout = new GridBagLayout();
        p.setLayout(layout);
    }

    void createStrokeColorButton(ResourceBundleUtil labels, AbstractButton btn, Map<AttributeKey, Object> defaultAttributes, boolean hasFields) {
        btn = ButtonFactory.createSelectionColorButton(editor,
                STROKE_COLOR, ButtonFactory.HSV_COLORS, ButtonFactory.HSV_COLORS_COLUMN_COUNT,
                "attribute.strokeColor", labels, defaultAttributes, new Rectangle(3, 3, 10, 10));
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        ((JPopupButton) btn).setAction(null, null);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = (hasFields) ? 3 : 0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        p.add(btn, gbc);
    }

    void createStrokeColorField(ResourceBundleUtil labels, AbstractButton btn, Map<AttributeKey, Object> defaultAttributes) {
        STROKE_GRADIENT.set(defaultAttributes, null);
        JAttributeTextField<Color> colorField = new JAttributeTextField<Color>();
        colorField.setColumns(7);
        colorField.setToolTipText(labels.getString("attribute.strokeColor.toolTipText"));
        colorField.putClientProperty("Palette.Component.segmentPosition", "first");
        colorField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(colorField));
        colorField.setFormatterFactory(ColorFormatter.createFormatterFactory());
        colorField.setHorizontalAlignment(JTextField.LEFT);
        new FigureAttributeEditorHandler<Color>(STROKE_COLOR, defaultAttributes, colorField, editor, true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        p.add(colorField, gbc);

    }

    void createOpacityField(ResourceBundleUtil labels) {
        JAttributeTextField<Double> opacityField = new JAttributeTextField<Double>();
        opacityField.setColumns(3);
        opacityField.setToolTipText(labels.getString("attribute.strokeOpacity.toolTipText"));
        opacityField.setHorizontalAlignment(JAttributeTextField.RIGHT);
        opacityField.putClientProperty("Palette.Component.segmentPosition", "first");
        opacityField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(opacityField));
        opacityField.setFormatterFactory(JavaNumberFormatter.createFormatterFactory(0d, 1d, 100d));
        opacityField.setHorizontalAlignment(JTextField.LEFT);
        new FigureAttributeEditorHandler<Double>(STROKE_OPACITY, opacityField, editor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(3, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        p.add(opacityField, gbc);
    }

    void createOpacitySlider(ResourceBundleUtil labels, boolean hasFields) {
        JPopupButton opacityPopupButton = new JPopupButton();
        JAttributeSlider opacitySlider = new JAttributeSlider(JSlider.VERTICAL, 0, 100, 100);
        opacityPopupButton.add(opacitySlider);
        labels.configureToolBarButton(opacityPopupButton, "attribute.strokeOpacity");
        opacityPopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(opacityPopupButton));
        opacityPopupButton.setIcon(
                new SelectionOpacityIcon(editor, STROKE_OPACITY, null, STROKE_COLOR, getClass().getResource(labels.getString("attribute.strokeOpacity.icon")),
                        new Rectangle(5, 5, 6, 6), new Rectangle(4, 4, 7, 7)));
        opacityPopupButton.setPopupAnchor(SOUTH_EAST);
        new SelectionComponentRepainter(editor, opacityPopupButton);
        GridBagConstraints gbc = new GridBagConstraints();
        if (hasFields) {
            gbc.gridx = 1;
            gbc.weighty = 1f;
        } else {
            gbc.gridx = 0;
        }
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.insets = new Insets(3, 0, 0, 0);
        p.add(opacityPopupButton, gbc);
        opacitySlider.setUI((SliderUI) PaletteSliderUI.createUI(opacitySlider));
        opacitySlider.setScaleFactor(100d);
        new FigureAttributeEditorHandler<Double>(STROKE_OPACITY, opacitySlider, editor);
    }

    void createStrokeWidthField(ResourceBundleUtil labels) {
        JAttributeTextField<Double> strokeWidthField = new JAttributeTextField<Double>();
        strokeWidthField.setColumns(2);
        strokeWidthField.setToolTipText(labels.getString("attribute.strokeWidth.toolTipText"));
        strokeWidthField.setHorizontalAlignment(JAttributeTextField.LEFT);
        strokeWidthField.putClientProperty("Palette.Component.segmentPosition", "first");
        strokeWidthField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(strokeWidthField));
        strokeWidthField.setFormatterFactory(JavaNumberFormatter.createFormatterFactory(0d, 100d, 1d));
        new FigureAttributeEditorHandler<Double>(STROKE_WIDTH, strokeWidthField, editor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(3, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        p.add(strokeWidthField, gbc);

    }

    void createStrokeWidthPopupSliderButton(ResourceBundleUtil labels, boolean hasFields) {
        JPopupButton strokeWidthPopupButton = new JPopupButton();
        JAttributeSlider strokeWidthSlider = new JAttributeSlider(
                JSlider.VERTICAL, 0, 50, 1);
        strokeWidthSlider.setUI((SliderUI) PaletteSliderUI.createUI(strokeWidthSlider));
        strokeWidthPopupButton.add(strokeWidthSlider);
        labels.configureToolBarButton(strokeWidthPopupButton, "attribute.strokeWidth");
        strokeWidthPopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(strokeWidthPopupButton));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridx = (hasFields) ? 1 : 0;
        gbc.insets = new Insets(3, 0, 0, 0);
        p.add(strokeWidthPopupButton, gbc);
        new FigureAttributeEditorHandler<Double>(STROKE_WIDTH, strokeWidthSlider, editor);

    }

    void createStrokeJoinButton(ResourceBundleUtil labels, AbstractButton btn, boolean hasFields) {
        btn = ButtonFactory.createStrokeJoinButton(editor, labels);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        if (hasFields) {
            gbc.gridx = 4;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
        } else {
            gbc.gridy = 0;
        }
        gbc.insets = new Insets(0, 3, 0, 0);
        p.add(btn, gbc);
    }

    void createStrokeCapButton(ResourceBundleUtil labels, AbstractButton btn, boolean hasFields) {
        btn = ButtonFactory.createStrokeCapButton(editor, labels);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        if (hasFields) {
            gbc.gridx = 4;
        }
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 3, 0, 0);
        p.add(btn, gbc);
    }

    void createStrokeDashField(ResourceBundleUtil labels) {
        JAttributeTextField<Double> dashOffsetField = new JAttributeTextField<Double>();
        dashOffsetField.setColumns(1);
        dashOffsetField.setToolTipText(labels.getString("attribute.strokeDashPhase.toolTipText"));
        dashOffsetField.setHorizontalAlignment(JAttributeTextField.LEFT);
        dashOffsetField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(dashOffsetField));
        dashOffsetField.setFormatterFactory(JavaNumberFormatter.createFormatterFactory(-1000d, 1000d, 1d));
        new FigureAttributeEditorHandler<Double>(STROKE_DASH_PHASE, dashOffsetField, editor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.insets = new Insets(3, 3, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 2;
        p.add(dashOffsetField, gbc);
    }

    void createStrokeDashesButton(ResourceBundleUtil labels, AbstractButton btn, boolean hasFields) {
        btn = ButtonFactory.createStrokeDashesButton(editor, labels);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        if (hasFields) {
            gbc.gridx = 4;
        }
        gbc.gridy = 2;
        gbc.insets = new Insets(3, 3, 0, 0);
        p.add(btn, gbc);
    }

    @Override
    protected String getID() {
        return "stroke";
    }

    @Override
    protected int getDefaultDisclosureState() {
        return 1;
    }

    public JPanel getJPanel() {
        return p;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setOpaque(false);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
