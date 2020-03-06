/*******************************************************************************
 * Copyright (c) 2020 Ecole Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.scripting.core.tracemarker;

import org.eclipse.ease.modules.WrapToScript;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.incubator.internal.scripting.core.tracemarker.TraceMarker;
import org.eclipse.tracecompass.incubator.internal.scripting.core.tracemarker.TraceMarkerGeneratorModule;
import org.eclipse.tracecompass.incubator.internal.scripting.core.tracemarker.TraceMarkerSet;
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

    /** The Constant DEFAULT_NAME. */
    public static final String DEFAULT_NAME = "TracerMarkerSet"; //$NON-NLS-1$

    /** The Constant DEFAULT_ID. */
    public static final String DEFAULT_ID = "Set0"; //$NON-NLS-1$

    /** The Constant INVALID_START_TIMESTAMPT. */
    public static final String INVALID_START_TIMESTAMPT = "Invalid start time"; //$NON-NLS-1$

    /** The Constant INVALID_END_TIMESTAMPT. */
    public static final String INVALID_END_TIMESTAMPT = "Invalid end time"; //$NON-NLS-1$

    /** The active trace. */
    private ITmfTrace fTrace;

    /** The source. */
    private TraceMarkerGeneratorModule fSource;

    /** The trace marker set. */
    private TraceMarkerSet fTraceMarkerSet;

    /** The source status. */
    private int fSourceStatus = 0;


    /**
     * Adds a default marker.
     *
     * @param name the name
     * @param startTime the start time
     * @param endTime the end time
     * @param color the color
     */
    @WrapToScript
    public void addSimpleMarker(String name, long startTime, long endTime, @Nullable String color) {
        if (fSourceStatus == 0) {
            initializeTraceMarkerGeneratorModule();
        }
        if (createTraceMarker(name, null, startTime, endTime, color)) {
            fSource.configureSet(fTraceMarkerSet);
        }
    }


    /**
     * Adds a category marker.
     *
     * @param name
     * @param category
     * @param startTime
     * @param endTime
     * @param color the color
     */
    @WrapToScript
    public void addCategoryMarker(String name, String category, long startTime, long endTime, @Nullable String color) {
        if (fSourceStatus == 0) {
            initializeTraceMarkerGeneratorModule();
        }
        if (createTraceMarker(name, category, startTime, endTime, color)) {
            fSource.configureSet(fTraceMarkerSet);
        }
    }


    /**
     * Create a trace marker.
     *
     * @param name
     * @param category
     * @param startTime
     * @param endTime
     * @param color the color
     */
    private boolean createTraceMarker(String name, @Nullable String category, long startTime, long endTime, @Nullable String color) {
        if (startTime > endTime || endTime > fTrace.getTimeRange().getEndTime().toNanos()) {
            System.out.println(INVALID_END_TIMESTAMPT);
            return false;
        }
        if (startTime < fTrace.getTimeRange().getStartTime().toNanos()) {
            System.out.println(INVALID_START_TIMESTAMPT);
            return false;
        }
        TraceMarker traceMarker = new TraceMarker(name, category, startTime, endTime, color);
        fTraceMarkerSet.addTraceMarker(traceMarker);
        return true;
    }


    /**
     * Initialize trace marker generator module.
     */
    private void initializeTraceMarkerGeneratorModule() {
        fTrace = TmfTraceManager.getInstance().getActiveTrace();
        if (fTrace == null) {
            // No trace opened, can't add marker
            return;
        }
        for (IMarkerEventSource source : TmfTraceAdapterManager.getAdapters(fTrace, IMarkerEventSource.class)) {
            if (source.getClass() == TraceMarkerGeneratorModule.class) {
                fTraceMarkerSet = new TraceMarkerSet(DEFAULT_NAME, DEFAULT_ID);
                fSource = (TraceMarkerGeneratorModule) source;
                fSourceStatus = 1;
                return;
            }
        }
    }
}
