/*******************************************************************************
 * Copyright (c) 2020 Ecole Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.scripting.core.tracemarker;

import java.util.ArrayList;
import java.util.List;

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

    /** The message INVALID_START_TIMESTAMPT to print if the marker was set with an bad start time stamp. */
    public static final String INVALID_START_TIMESTAMPT = "Invalid start time"; //$NON-NLS-1$

    /** The message INVALID_END_TIMESTAMPT to print if the marker was set with an bad end time stamp. */
    public static final String INVALID_END_TIMESTAMPT = "Invalid end time"; //$NON-NLS-1$

    /** The message INVALID_TRACE to print if the active trace is invalid or non-existing. */
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
     * @param label : the marker's label to show
     * @param category : the marker's group category
     * @param startTime : the start of the marker in ns
     * @param endTime : the end of the marker in ns
     * @param color : the marker's highlight color
     */
    @WrapToScript
    public void addTraceMarker(String label, @Nullable String category, long startTime, long endTime, @Nullable String color) {
        if (fSourceStatus == 0) {
            initializeScriptingMarkerSource();
        }
        if (createTraceMarker(label, category, startTime, endTime, color)) {
            // Configure with the adapter the last added trace marker
            fSource.configureMarker(fTraceMarkersList.get(fTraceMarkersList.size()-1));
        }
    }


    /**
     * Create a trace marker object.
     *
     * @param label : the marker's label to show
     * @param category : the marker's group category
     * @param startTime : the start of the marker in ns
     * @param endTime : the end of the marker in ns
     * @param color : the marker's highlight color
     */
    private boolean createTraceMarker(String label, @Nullable String category, long startTime, long endTime, @Nullable String color) {
        if (startTime > endTime || endTime > fTrace.getTimeRange().getEndTime().toNanos()) {
            System.out.println(INVALID_END_TIMESTAMPT);
            return false;
        }
        if (startTime < fTrace.getTimeRange().getStartTime().toNanos()) {
            System.out.println(INVALID_START_TIMESTAMPT);
            return false;
        }
        TraceMarker traceMarker = new TraceMarker(label, category, startTime, endTime, color);
        fTraceMarkersList.add(traceMarker);
        return true;
    }


    /**
     * Retrieve and initialize the trace marker adapter of the active trace.
     */
    private void initializeScriptingMarkerSource() {
        fTrace = TmfTraceManager.getInstance().getActiveTrace();
        if (fTrace == null) {
            System.out.println(INVALID_TRACE);
            return;
        }
        for (IMarkerEventSource source : TmfTraceAdapterManager.getAdapters(fTrace, IMarkerEventSource.class)) {
            if (source.getClass() == ScriptingMarkerSource.class) {
                fTraceMarkersList = new ArrayList<>();
                fSource = (ScriptingMarkerSource) source;
                fSource.initializeAdapterMarkersLists();
                fSourceStatus = 1;
                return;
            }
        }
    }
}
