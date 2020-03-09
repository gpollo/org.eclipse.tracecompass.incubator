/*******************************************************************************
 * Copyright (c) 2020 Ecole Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.internal.scripting.core.tracemarker;

import org.eclipse.tracecompass.tmf.core.dataprovider.X11ColorUtils;
import org.eclipse.tracecompass.tmf.core.presentation.RGBAColor;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.graphics.RGBA;

/**
 * The Class TraceMarker.
 *
 * @author Maxime Thibault
 * @author Ibrahima Sega Sangare
 */
public class TraceMarker {

    /** The Constant DEFAULT_CATEGORY. */
    public static final String DEFAULT_CATEGORY = "Default"; //$NON-NLS-1$

    /** The Constant DEFAULT_UNIT. */
    public static final String DEFAULT_COLOR = "FF0000"; //$NON-NLS-1$

    /** The hexadecimal base */
    private static final int HEX = 16;

    /** The label. */
    private final String fLabel;

    /** The category. */
    private String fCategory;

    /** The color RGBA. */
    private RGBA fRGBAColor;

    /** The start time. */
    private final long fStartTime;

    /** The duration. */
    private final long fDuration;

    /** The end time. */
    private final long fEndTime;


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
        setRGBAColor(color);
        fStartTime = startTime;
        fEndTime = endTime;
        fDuration = (fEndTime - fStartTime);
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
     * Gets the RGBA Color.
     *
     * @return the RGBA color
     */
    public RGBA getRGBAColor() {
        return fRGBAColor;
    }

    /**
     * Sets the RGBAColor.
     *
     * @param color The color to set
     */
    private void setRGBAColor(String color) {
        String hexColor;
        if(color == null) {
            hexColor = DEFAULT_COLOR;
        } else {
            hexColor = X11ColorUtils.toHexColor(color);
        }
        hexColor = hexColor.substring(1);
        int intColor = Integer.parseInt(hexColor, HEX);
        RGBAColor rgbaColor = new RGBAColor(intColor);
        fRGBAColor = new RGBA(rgbaColor.getRed(),
                              rgbaColor.getBlue(),
                              rgbaColor.getGreen(),
                              70);
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

}
