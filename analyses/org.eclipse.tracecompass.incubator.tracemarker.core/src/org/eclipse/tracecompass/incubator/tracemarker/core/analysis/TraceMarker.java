/*******************************************************************************
 * Copyright (c) 2020 Ecole Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.tracemarker.core.analysis;

import java.awt.Color;
import java.lang.reflect.Field;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.graphics.RGBA;

/**
 * The Class TraceMarker.
 *
 * @author mathir
 */
public class TraceMarker {

    /** The Constant DEFAULT_CATEGORY. */
    public static final String DEFAULT_CATEGORY = "Default"; //$NON-NLS-1$

    /** The Constant DEFAULT_UNIT. */
    public static final String DEFAULT_UNIT = "ns"; //$NON-NLS-1$

    /** The Constant COLOR_PACKAGE. */
    public static final String COLOR_PACKAGE = "java.awt.Color"; //$NON-NLS-1$

    /** The Constant ALPHA. */
    private static final int ALPHA = 70;

    /** The label. */
    private final String fLabel;

    /** The category. */
    private String fCategory;

    /** The color. */
    private Color fColor;

    /** The color RGBA. */
    private RGBA fColorRGBA;

    /** The start time. */
    private final long fStartTime;

    /** The duration. */
    private final long fDuration;

    /** The end time. */
    private final long fEndTime;

    /** The unit. */
    private final String fUnit;


    /**
     * Instantiates a new trace marker.
     *
     * @param label the label
     * @param category the category
     * @param startTime the start time
     * @param endTime the end time
     * @param color the color
     */
    public TraceMarker(String label, @Nullable String category, long startTime, long endTime, @Nullable String color) {
        fLabel = label;
        setCategory(category);
        setColor(color);
        fStartTime = startTime;
        fEndTime = endTime;
        fDuration = (fEndTime - fStartTime);
        fUnit = DEFAULT_UNIT;
    }


    /**
     * Gets the label.
     *
     * @return the label
     */
    public String getLabel() {
        return fLabel;
    }


    /**
     * Gets the category.
     *
     * @return the category
     */
    public String getCategory() {
        return fCategory;
    }


    /**
     * Sets the category.
     *
     * @param category the category to set
     */
    public void setCategory(String category) {
        if (category != null) {
            fCategory = category;

        } else {
            fCategory = DEFAULT_CATEGORY;
        }
    }


    /**
     * Gets the color.
     *
     * @return the color
     */
    public Color getColor() {
        return fColor;
    }


    /**
     * Gets the color RGBA.
     *
     * @return the colorRGBA
     */
    public RGBA getColorRGBA() {
        return fColorRGBA;
    }


    /**
     * Sets the color.
     *
     * @param color the color to set
     */
    public void setColor(String color) {
        try {
            Field field = Class.forName(COLOR_PACKAGE).getField(color);
            fColor = (Color)field.get(null);

        } catch(Exception e) {
            fColor = Color.RED;
        }
        setColorRGBA(fColor);
    }


    /**
     * Sets the color RGBA.
     */
    private void setColorRGBA(Color color) {
        fColorRGBA = new RGBA(color.getRed(),
                              color.getBlue(),
                              color.getGreen(),
                              ALPHA);
    }


    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public long getStartTime() {
        return fStartTime;
    }


    /**
     * Gets the duration.
     *
     * @return the duration
     */
    public long getDuration() {
        return fDuration;
    }


    /**
     * Gets the end time.
     *
     * @return the end time
     */
    public long getEndTime() {
        return fEndTime;
    }


    /**
     * Gets the unit.
     *
     * @return the unit
     */
    public String getUnit() {
        return fUnit;
    }
}
