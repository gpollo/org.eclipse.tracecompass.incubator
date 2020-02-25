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

/**
 * The Class TraceMarkerSet.
 *
 * @author mathir
 */
public class TraceMarkerSet {

    /** The name. */
    private final String fName;

    /** The id. */
    private final String fId;

    /** The trace markers. */
    private final List<TraceMarker> fTraceMarkers;

    /**
     * Constructor.
     *
     * @param name the name
     * @param id the id
     */
    public TraceMarkerSet(String name, String id) {
        super();
        fName = name;
        fId = id;
        fTraceMarkers = new ArrayList<>();
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return fName;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return fId;
    }

    /**
     * Gets the markers.
     *
     * @return the markers
     */
    public List<TraceMarker> getMarkers() {
        return fTraceMarkers;
    }

    /**
     * Add a trace marker.
     *
     * @param traceMarker the marker
     */
    public void addTraceMarker(TraceMarker traceMarker) {
        fTraceMarkers.add(traceMarker);
    }
}
