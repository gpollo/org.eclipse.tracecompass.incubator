/*******************************************************************************
 * Copyright (c) 2020 Ecole Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.internal.scripting.core.tracemarker;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.tmf.core.trace.AbstractTmfTraceAdapterFactory;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceAdapterManager;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.IMarkerEventSource;

/**
 * The Class TraceMarkerEventSourceFactory.
 *
 * @author Maxime Thibeault
 * @author Gabriel-Andrew Pollo-Guilbert
 */
public class TraceMarkerEventSourceFactory extends AbstractTmfTraceAdapterFactory {

    private static @Nullable TraceMarkerEventSourceFactory INSTANCE;

    /**
     * Get the instance of the factory.
     *
     * @return the singleton instance
     */
    public synchronized static TraceMarkerEventSourceFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TraceMarkerEventSourceFactory();
        }

        return INSTANCE;
    }

    /**
     * Register this factory to the trace adapter manager.
     */
    public void register() {
        TmfTraceAdapterManager.registerFactory(this, TmfTrace.class);
    }

    /**
     * Unregister this factory to the trace adapter manager.
     */
    public void unregister() {
        TmfTraceAdapterManager.unregisterFactory(this);
    }

    /**
     * Instantiate the adapter.
     *
     * @return the adapter
     */
    @Override
    protected <T> T getTraceAdapter(ITmfTrace trace, Class<T> adapterType) {
        if (IMarkerEventSource.class.equals(adapterType)) {
            TraceMarkerGeneratorModule adapter = new TraceMarkerGeneratorModule(trace);
            return adapterType.cast(adapter);
        }
        return null;
    }


    /**
     * Gets the adapter list.
     *
     * @return the adapter list
     */
    @Override
    public Class<?>[] getAdapterList() {
        return new Class[] {
                IMarkerEventSource.class
        };
    }
}
