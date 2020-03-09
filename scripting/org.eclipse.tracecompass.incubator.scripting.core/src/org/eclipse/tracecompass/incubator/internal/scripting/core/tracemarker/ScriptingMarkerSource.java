/*******************************************************************************
 * Copyright (c) 2020 Ecole Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.scripting.core.tracemarker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.RGBA;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.tmf.core.signal.TmfMarkerEventSourceUpdatedSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalManager;
import org.eclipse.tracecompass.tmf.core.trace.AbstractTmfTraceAdapterFactory.IDisposableAdapter;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.IMarkerEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.IMarkerEventSource;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.MarkerEvent;

/**
 * The Class ScriptingMarkerSource.
 *
 * @author Maxime Thibault
 */
public class ScriptingMarkerSource implements IMarkerEventSource, IDisposableAdapter {

    /** The trace marker events. */
    private List<IMarkerEvent> fTraceMarkerEvents;

    /** The categories. */
    private List<String> fCategories;

    /** The trace. */
    private final ITmfTrace fTrace;


    /**
     * Instantiates a new trace marker generator module.
     *
     * @param trace the trace
     */
    public ScriptingMarkerSource(ITmfTrace trace) {
        fTrace = trace;
        fTraceMarkerEvents = new ArrayList<>();
        fCategories = new ArrayList<>();
        TmfSignalManager.register(this);
    }


    /**
     * Configure set.
     *
     * @param traceMarkerSet the trace marker set
     */
    public void configureSet(TraceMarkerSet traceMarkerSet) {
        fTraceMarkerEvents.clear();
        fCategories.clear();
        if (traceMarkerSet != null) {
            for (TraceMarker traceMarker : traceMarkerSet.getMarkers()) {
                configureMarker(traceMarker);
            }
        }
    }


    /**
     * Configure marker.
     *
     * @param traceMarker the trace marker
     */
    private void configureMarker(TraceMarker traceMarker) {
        RGBA color = traceMarker.getRGBAColor();
        String category = traceMarker.getCategory();
        if(!fCategories.contains(category)) {
            fCategories.add(category);
        }
        String label = traceMarker.getLabel();
        long start = traceMarker.getStartTime();
        long duration = traceMarker.getDuration();
        MarkerEvent traceMarkerEvent = new MarkerEvent(null, start, duration, category, color, label, true);
        fTraceMarkerEvents.add(traceMarkerEvent);

        TmfMarkerEventSourceUpdatedSignal signal = new TmfMarkerEventSourceUpdatedSignal(this);
        TmfSignalManager.dispatchSignal(signal);
    }


    /**
     * Gets the marker categories.
     *
     * @return the marker categories
     */
    @Override
    public @NonNull List<@NonNull String> getMarkerCategories() {
        return fCategories;
    }


    /**
     * Gets the marker list.
     *
     * @param category the category
     * @param startTime the start time
     * @param endTime the end time
     * @param resolution the resolution
     * @param monitor the monitor
     * @return the marker list
     */
    @Override
    public @NonNull List<@NonNull IMarkerEvent> getMarkerList(@NonNull String category, long startTime, long endTime, long resolution, @NonNull IProgressMonitor monitor) {
        return fTraceMarkerEvents;
    }


    /**
     * Dispose.
     */
    @Override
    public void dispose() {
        TmfSignalManager.deregister(this);
    }


    /**
     * Get the trace linked to this adapter
     *
     * @return the trace
     */
    public ITmfTrace getTrace() {
        return fTrace;
    }

}
