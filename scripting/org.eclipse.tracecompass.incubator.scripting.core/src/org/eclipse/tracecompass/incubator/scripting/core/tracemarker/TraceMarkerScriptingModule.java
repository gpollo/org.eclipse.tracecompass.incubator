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
package org.eclipse.tracecompass.incubator.scripting.core.tracemarker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ease.modules.ScriptParameter;
import org.eclipse.ease.modules.WrapToScript;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.incubator.internal.scripting.core.tracemarker.TraceMarker;
import org.eclipse.tracecompass.incubator.internal.scripting.core.tracemarker.ScriptingMarkerSource;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceAdapterManager;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceManager;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.IMarkerEventSource;

/**
 * The Class TraceMarkerScriptingModule.
 *
 * @author Maxime Thibault
 */
public class TraceMarkerScriptingModule {
    /** The DEFAULT_ENDTIME for the marker if it was left unspecified. */
    public static final String DEFAULT_ENDTIME = "-1"; //$NON-NLS-1$

    /** The DEFAULT_DURATION for the marker if it was left unspecified. */
    public static final long DEFAULT_DURATION = 100;

    /** The DEFAULT_LABEL for the marker if it was left unspecified. */
    public static final String DEFAULT_LABEL = "TraceMarker"; //$NON-NLS-1$

    /** The DEFAULT_CATEGORY for the marker if it was left unspecified. */
    public static final String DEFAULT_CATEGORY = "Generals markers"; //$NON-NLS-1$

    /** The DEFAULT_COLOR for the marker if it was left unspecified. */
    public static final String DEFAULT_COLOR = "Red"; //$NON-NLS-1$

    /**
     * The message INVALID_START_TIMESTAMP to print if the marker was set with
     * an bad start time stamp.
     */
    public static final String INVALID_START_TIMESTAMP = "Invalid start time"; //$NON-NLS-1$

    /**
     * The message INVALID_END_TIMESTAMP to print if the marker was set with an
     * bad end time stamp.
     */
    public static final String INVALID_END_TIMESTAMP = "Invalid end time"; //$NON-NLS-1$

    /**
     * The message INVALID_TRACE to print if the active trace is invalid or
     * non-existing.
     */
    public static final String INVALID_TRACE = "Invalid trace"; //$NON-NLS-1$

    /** The active trace. */
    private ITmfTrace fTrace;

    /** The trace markers list. */
    private List<TraceMarker> fTraceMarkersList;

    /** The source (adapter). */
    private ScriptingMarkerSource fSource;

    /** The source status. */
    private int fSourceStatus = 0;

    /**
     * Adds a trace marker to the time graph view.
     *
     * @param label
     *            : the marker's label to show (it's name)
     * @param category
     *            : the marker's group category (can be anything and is used to
     *            group markers together in sets that can be enabled/disabled)
     * @param startTime
     *            : the starting time stamp of the marker in ns
     * @param endTime
     *            : the ending time stamp of the marker in ns
     * @param color
     *            : the marker's highlighting color in X11 format (ex: "Red",
     *            "Green", "Cyan", "Gold", more at
     *            https://en.wikipedia.org/wiki/X11_color_names)
     */
    @WrapToScript
    public boolean addTraceMarker(long startTime, @ScriptParameter(defaultValue = DEFAULT_ENDTIME) long endTime, @ScriptParameter(defaultValue = DEFAULT_LABEL) String label, @ScriptParameter(defaultValue = DEFAULT_CATEGORY) String category,
            @ScriptParameter(defaultValue = DEFAULT_COLOR) String color) {
        if (fSourceStatus == 0) {
            initializeScriptingMarkerSource();
        }
        if ((fSourceStatus != 0) && createTraceMarker(label, category, startTime, endTime, color)) {
            // Configure with the adapter the last added trace marker
            fSource.configureMarker(fTraceMarkersList.get(fTraceMarkersList.size() - 1));
            return true;
        }
        return false;
    }

    /**
     * Create a trace marker object.
     *
     * @param label
     *            : the marker's label to show
     * @param category
     *            : the marker's group category
     * @param startTime
     *            : the start of the marker in ns
     * @param endTime
     *            : the end of the marker in ns
     * @param color
     *            : the marker's highlight color
     */
    private boolean createTraceMarker(String label, @Nullable String category, long startTime, long endTime, String color) {
        long calculatedEndTime = endTime;

        if (calculatedEndTime == Long.valueOf(DEFAULT_ENDTIME)) {
            calculatedEndTime = startTime + DEFAULT_DURATION;
        }

        if (startTime > calculatedEndTime || calculatedEndTime > fTrace.getEndTime().toNanos()) {
            System.out.println(INVALID_END_TIMESTAMP);
            return false;
        }

        if (startTime < fTrace.getStartTime().toNanos()) {
            System.out.println(INVALID_START_TIMESTAMP);
            return false;
        }

        if (startTime == calculatedEndTime) {
            System.out.println(INVALID_START_TIMESTAMP);
            return false;
        }
        TraceMarker traceMarker = new TraceMarker(label, category, startTime, calculatedEndTime, color);
        fTraceMarkersList.add(traceMarker);
        return true;
    }

    /**
     * Retrieve and initialize the trace marker adapter of the active trace.
     */
    private void initializeScriptingMarkerSource() {
        if (TmfTraceManager.getInstance().getActiveTrace() != null) {
            fTrace = TmfTraceManager.getInstance().getActiveTrace();
            for (IMarkerEventSource source : TmfTraceAdapterManager.getAdapters(fTrace, IMarkerEventSource.class)) {
                if (source.getClass() == ScriptingMarkerSource.class) {
                    fTraceMarkersList = new ArrayList<>();
                    fSource = (ScriptingMarkerSource) source;
                    fSource.initializeAdapterMarkersLists();
                    fSourceStatus = 1;
                    return;
                }
            }
        } else {
            System.out.println(INVALID_TRACE);
            return;
        }
    }
}
