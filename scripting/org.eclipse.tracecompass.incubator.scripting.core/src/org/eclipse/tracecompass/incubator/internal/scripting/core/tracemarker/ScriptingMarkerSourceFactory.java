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
public class ScriptingMarkerSourceFactory extends AbstractTmfTraceAdapterFactory {

    private static @Nullable ScriptingMarkerSourceFactory INSTANCE;

    /**
     * Get the instance of the factory.
     *
     * @return the singleton instance
     */
    public synchronized static ScriptingMarkerSourceFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptingMarkerSourceFactory();
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
     * Instantiate the marker adapter for a trace.
     *
     * @param trace
     *            : the active trace
     * @param adapterType
     *            : the class of adapter
     * @return the adapter
     */
    @Override
    protected <T> T getTraceAdapter(ITmfTrace trace, Class<T> adapterType) {
        if (IMarkerEventSource.class.equals(adapterType)) {
            ScriptingMarkerSource adapter = new ScriptingMarkerSource(trace);
            return adapterType.cast(adapter);
        }
        return null;
    }

    /**
     * Get the adapter list.
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