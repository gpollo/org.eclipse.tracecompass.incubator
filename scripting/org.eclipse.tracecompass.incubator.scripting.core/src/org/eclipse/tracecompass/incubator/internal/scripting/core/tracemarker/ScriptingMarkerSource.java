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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.tmf.core.signal.TmfMarkerEventSourceUpdatedSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalManager;
import org.eclipse.tracecompass.tmf.core.trace.AbstractTmfTraceAdapterFactory.IDisposableAdapter;
import org.eclipse.tracecompass.tmf.core.trace.ICyclesConverter;
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

    /** The Constant MS. */
    private static final String MS = "ms"; //$NON-NLS-1$

    /** The Constant US. */
    private static final String US = "us"; //$NON-NLS-1$

    /** The Constant NS. */
    private static final String NS = "ns"; //$NON-NLS-1$

    /** The Constant CYCLES. */
    private static final String CYCLES = "cycles"; //$NON-NLS-1$

    /** The Constant NANO_PER_MILLI. */
    private static final long NANO_PER_MILLI = 1000000L;

    /** The Constant NANO_PER_MICRO. */
    private static final long NANO_PER_MICRO = 1000L;

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
        long start = convertToNanos(traceMarker.getStartTime(), traceMarker.getUnit());
        long duration = convertToNanos(traceMarker.getDuration(), traceMarker.getUnit());
        // TODO: problem when using other unit than nanos : *10 required
        MarkerEvent traceMarkerEvent = new MarkerEvent(null, start, duration, category, color, label, true);
        fTraceMarkerEvents.add(traceMarkerEvent);

        TmfMarkerEventSourceUpdatedSignal signal = new TmfMarkerEventSourceUpdatedSignal(this);
        TmfSignalManager.dispatchSignal(signal);
    }


    /**
     * Convert to nanos.
     *
     * @param number the number
     * @param unit the unit
     * @return the long
     */
    private long convertToNanos(long number, String unit) {
        if (unit.equalsIgnoreCase(MS)) {
            return number * NANO_PER_MILLI;
        } else if (unit.equalsIgnoreCase(US)) {
            return number * NANO_PER_MICRO;
        } else if (unit.equalsIgnoreCase(NS)) {
            return number;
        } else if (unit.equalsIgnoreCase(CYCLES) &&
                fTrace instanceof IAdaptable) {
            ICyclesConverter adapter = ((IAdaptable) fTrace).getAdapter(ICyclesConverter.class);
            if (adapter != null) {
                return adapter.cyclesToNanos(number);
            }
        }
        return number;
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

}
