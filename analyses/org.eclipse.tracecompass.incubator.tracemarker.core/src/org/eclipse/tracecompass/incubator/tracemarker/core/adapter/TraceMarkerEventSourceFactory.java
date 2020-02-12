/*******************************************************************************
 * Copyright (c) 2020 Ecole Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.tracemarker.core.adapter;

import org.eclipse.tracecompass.incubator.tracemarker.core.analysis.TraceMarkerGeneratorModule;
import org.eclipse.tracecompass.tmf.core.trace.AbstractTmfTraceAdapterFactory;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.IMarkerEventSource;

/**
 * The Class TraceMarkerEventSourceFactory.
 *
 * @author mathir
 *
 */
public class TraceMarkerEventSourceFactory extends AbstractTmfTraceAdapterFactory {

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
