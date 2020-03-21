/*******************************************************************************
 * Copyright (c) 2020 Ecole Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License 2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.internal.scripting.core.tracemarker;

import org.eclipse.tracecompass.tmf.core.dataprovider.X11ColorUtils;
import org.eclipse.tracecompass.tmf.core.presentation.RGBAColor;
import org.eclipse.swt.graphics.RGBA;

/**
 * The Class TraceMarker.
 *
 * @author Maxime Thibault
 * @author Ibrahima Sega Sangare
 */
public class TraceMarker {

    /** The DEFAULT_COLOR for the marker if it was left unspecified. */
    public static final String DEFAULT_COLOR = "FF0000"; //$NON-NLS-1$

    /** The constant ALPHA for the marker transparency. */
    private static final int ALPHA = 70;

    /** The hexadecimal base for the color conversion. */
    private static final int HEX = 16;

    /** The marker's label. */
    private final String fLabel;

    /** The marker's category. */
    private String fCategory;

    /** The marker's color in RGBA. */
    private RGBA fRGBAColor;

    /** The marker's start time stamp in ns. */
    private final long fStartTime;

    /** The marker's duration in ns. */
    private final long fDuration;

    /** The marker's end time stamp in ns. */
    private final long fEndTime;

    /**
     * Instantiates a new trace marker object.
     *
     * @param label
     *            : the marker's label to show
     * @param category
     *            : the marker's category
     * @param startTime
     *            : the start of the marker in ns
     * @param endTime
     *            : the end of the marker in ns
     * @param color
     *            : the marker's highlight color
     */
    public TraceMarker(String label, String category, long startTime, long endTime, String color) {
        fLabel = label;
        fCategory = category;
        setRGBAColor(color);
        fStartTime = startTime;
        fEndTime = endTime;
        fDuration = (fEndTime - fStartTime);
    }

    /**
     * Get the label.
     *
     * @return the label
     */
    public String getLabel() {
        return fLabel;
    }

    /**
     * Get the category.
     *
     * @return the category
     */
    public String getCategory() {
        return fCategory;
    }

    /**
     * Get the RGBA Color.
     *
     * @return the RGBA color
     */
    public RGBA getRGBAColor() {
        return fRGBAColor;
    }

    /**
     * Set the RGBAColor of the marker by converting a color string into RGBA
     * data.
     *
     * @param color
     *            : the marker's highlight color
     */
    private void setRGBAColor(String color) {
        String hexColor = color;

        if (hexColor != null && (hexColor = X11ColorUtils.toHexColor(hexColor)) != null) {
            // Get rid of the # at the beginning of the string
            hexColor = hexColor.substring(1);
        } else {
            hexColor = DEFAULT_COLOR;
        }

        int intColor = Integer.parseInt(hexColor, HEX);
        RGBAColor rgbaColor = new RGBAColor(intColor);
        fRGBAColor = new RGBA(rgbaColor.getRed(),
                rgbaColor.getBlue(),
                rgbaColor.getGreen(),
                ALPHA);
    }

    /**
     * Get the start time stamp.
     *
     * @return the start time stamp in ns
     */
    public long getStartTime() {
        return fStartTime;
    }

    /**
     * Get the duration.
     *
     * @return the duration in ns
     */
    public long getDuration() {
        return fDuration;
    }

    /**
     * Get the end time stamp.
     *
     * @return the end time stamp in ns
     */
    public long getEndTime() {
        return fEndTime;
    }

}
