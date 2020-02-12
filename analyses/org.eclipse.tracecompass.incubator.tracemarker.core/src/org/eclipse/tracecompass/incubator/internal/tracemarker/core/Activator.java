/*******************************************************************************
 * Copyright (c) 2020 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.tracemarker.core;

import org.eclipse.tracecompass.common.core.TraceCompassActivator;
import org.eclipse.tracecompass.incubator.tracemarker.core.adapter.TraceMarkerEventSourceFactory;
import org.eclipse.tracecompass.tmf.core.trace.TmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceAdapterManager;

// TODO: Auto-generated Javadoc
/**
 * Activator.
 */
public class Activator extends TraceCompassActivator {

    /**  The plug-in ID. */
    public static final String PLUGIN_ID = "org.eclipse.tracecompass.incubator.tracemarker.core"; //$NON-NLS-1$

    /** The trace marker event source factory. */
    private TraceMarkerEventSourceFactory fTraceMarkerEventSourceFactory;

    /**
     * The constructor.
     */
    public Activator() {
        super(PLUGIN_ID);

        fTraceMarkerEventSourceFactory = new TraceMarkerEventSourceFactory();
    }

    /**
     * Returns the instance of this plug-in.
     *
     * @return The plugin instance
     */
    public static TraceCompassActivator getInstance() {
        return TraceCompassActivator.getInstance(PLUGIN_ID);
    }

    /**
     * Start actions.
     */
    @Override
    protected void startActions() {
        TmfTraceAdapterManager.registerFactory(fTraceMarkerEventSourceFactory, TmfTrace.class);
    }

    /**
     * Stop actions.
     */
    @Override
    protected void stopActions() {
        TmfTraceAdapterManager.unregisterFactory(fTraceMarkerEventSourceFactory);
        fTraceMarkerEventSourceFactory.dispose();
    }

}

