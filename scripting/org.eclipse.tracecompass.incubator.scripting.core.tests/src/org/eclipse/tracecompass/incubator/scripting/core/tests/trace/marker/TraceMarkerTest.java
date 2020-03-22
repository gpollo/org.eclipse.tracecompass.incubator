/*******************************************************************************
 * Copyright (c) 2020 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License 2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.scripting.core.tests.trace.marker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.graphics.RGBA;
import org.eclipse.tracecompass.incubator.internal.scripting.core.tracemarker.*;
import org.eclipse.tracecompass.incubator.scripting.core.tests.ActivatorTest;
import org.eclipse.tracecompass.incubator.scripting.core.tests.stubs.ScriptingTestUtils;
import org.eclipse.tracecompass.incubator.scripting.core.tracemarker.TraceMarkerScriptingModule;
import org.eclipse.tracecompass.tmf.core.TmfCommonConstants;
import org.eclipse.tracecompass.tmf.core.dataprovider.X11ColorUtils;
import org.eclipse.tracecompass.tmf.core.io.ResourceUtil;
import org.eclipse.tracecompass.tmf.core.presentation.RGBAColor;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.tests.stubs.trace.xml.TmfXmlTraceStub;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test the following classes
 * {@link TraceMarker}
 * {@link TraceMarkerEventSourceFactory}
 * {@link TraceMarkerScriptingModule}
 * {@link ScriptingMarkerSource}
 *
 * @author Ibrahima Sega Sangare
 */
public class TraceMarkerTest {

 // ------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------

    private static final @NonNull String SOME_PROJECT_NAME = "traceMarkerProject";
    private static final @NonNull String TRACE_PATH = "testfiles/traces/callstack.xml";
    private static final @NonNull String TRACE_FILE = "callstack.xml";
    private static final @NonNull String TRACE_FOLDER_PATH = "folder";
    private static final @NonNull String NONEXISTENT_FILE_TRACE = "NotARealFile.xml";
    private static final @NonNull String NONESISTENT_TRACE_IN_EXISTENT_PATH = "testfiles/traces/NotARealFile.xml";
    private static final @NonNull IProgressMonitor PROGRESS_MONITOR = new NullProgressMonitor();
    private static final TemporaryFolder TEMPORARY_FOLDER = new TemporaryFolder();
    /** The constant ALPHA for the marker transparency. */
    private static final int ALPHA = 70;
    private static final String DEFAULT_COLOR = "Red";

    private IProject fProject;
    private ITmfTrace fTrace;
    private ScriptingMarkerSource fScriptingMarkerSource;
    private ScriptingMarkerSourceFactory fScriptingMarkerSourceFactory;
    private TraceMarkerScriptingModule fTraceMarkerScriptingModule;


    // ------------------------------------------------------------------------
    // Initialization
    // ------------------------------------------------------------------------
    /**
     * Setup a project with traces
     *
     * @throws CoreException
     *             Exception thrown during project creation
     * @throws IOException
     *             Exception thrown by supplementary file folder
     */
    @Before
    public void setUpEnvironment() throws CoreException, IOException {

        TEMPORARY_FOLDER.create();
        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        // Create a project inside workspace location
        IWorkspaceRoot wsRoot = workspace.getRoot();
        IProject project = wsRoot.getProject(SOME_PROJECT_NAME);
        project.create(PROGRESS_MONITOR);
        project.open(PROGRESS_MONITOR);

        fProject = project;

        // Create the traces and experiments folder
        IFolder folder = project.getFolder("Experiments");
        if (!folder.exists()) {
            folder.create(true, true, PROGRESS_MONITOR);
        }
        folder = project.getFolder("Traces");
        if (!folder.exists()) {
            folder.create(true, true, PROGRESS_MONITOR);
        }

        // Initialize test trace
        fTrace = ScriptingTestUtils.getTrace();
        assertNotNull(fTrace);
        assertTrue(fTrace instanceof TmfXmlTraceStub);

        // Test: Try to add marker before trace exists in project
        assertFalse(fTraceMarkerScriptingModule.addTraceMarker(fTrace.getStartTime().toNanos(), fTrace.getEndTime().toNanos(), "", "", DEFAULT_COLOR));

        // Add the trace
        IPath filePath = ActivatorTest.getAbsoluteFilePath(TRACE_PATH);
        IResource resource = folder.getFile(TRACE_FILE);
        assertNotNull(resource);
        assertTrue(ResourceUtil.createSymbolicLink(resource, filePath, true, PROGRESS_MONITOR));

        // Create the supplementary files folder for this trace
        File supplementaryFile = TEMPORARY_FOLDER.newFolder(TRACE_FILE);
        resource.setPersistentProperty(TmfCommonConstants.TRACE_SUPPLEMENTARY_FOLDER, supplementaryFile.getPath());

        // Add the folder and the trace inside it
        IFolder traceFolder = folder.getFolder(TRACE_FOLDER_PATH);
        if (!traceFolder.exists()) {
            traceFolder.create(true, true, PROGRESS_MONITOR);
        }
        IResource extraTraceResource = traceFolder.getFile(TRACE_FILE);
        assertNotNull(extraTraceResource);
        assertTrue(ResourceUtil.createSymbolicLink(extraTraceResource, filePath, true, PROGRESS_MONITOR));

        // Create the supplementary files folder for the trace in a folder
        extraTraceResource.setPersistentProperty(TmfCommonConstants.TRACE_SUPPLEMENTARY_FOLDER, supplementaryFile.getPath());

        // Add the trace element pointing to an unexistent trace
        resource = folder.getFile(NONEXISTENT_FILE_TRACE);
        assertNotNull(resource);
        assertTrue(ResourceUtil.createSymbolicLink(resource, new Path(NONESISTENT_TRACE_IN_EXISTENT_PATH), true, PROGRESS_MONITOR));

        // Initialize scriptingMarkerSource
        fScriptingMarkerSource = new ScriptingMarkerSource(fTrace);
        fScriptingMarkerSource.initializeAdapterMarkersLists();
        assertTrue(fTrace.equals(fScriptingMarkerSource.getTrace()));
        assertNotNull(fScriptingMarkerSource);
        assertNotNull(fScriptingMarkerSource.getMarkerCategories());
        assertTrue(fScriptingMarkerSource.getMarkerCategories().isEmpty());

        // Initialize scriptingMarkerSourceFactory
        fScriptingMarkerSourceFactory = ScriptingMarkerSourceFactory.getInstance();
        assertNotNull(fScriptingMarkerSourceFactory);
        assertNotNull(fScriptingMarkerSourceFactory.getAdapterList()[0]);
    }

    /**
     * Delete the project after tests
     *
     * @throws CoreException
     *             Exception thrown by project deletion
     */
    @After
    public void cleanUpEnvironment() throws CoreException {
        fTrace.dispose();
        IProject project = fProject;
        if (project != null) {
            project.delete(true, PROGRESS_MONITOR);
        }

        TEMPORARY_FOLDER.delete();
    }

    // ------------------------------------------------------------------------
    // Test Cases
    // ------------------------------------------------------------------------
    @Test
    public void testMarkerColor() {
        TraceMarker traceMarker = new TraceMarker("marker1", "", 0, 50, "blue");
        assertNotNull(traceMarker.getCategory());

        RGBAColor rgbaColor = new RGBAColor(Integer.parseInt(X11ColorUtils.toHexColor("blue").substring(1)));
        RGBA colorToCompare = new RGBA(rgbaColor.getRed(), rgbaColor.getGreen(), rgbaColor.getBlue(), ALPHA);
        assertTrue(traceMarker.getRGBAColor().equals(colorToCompare));

        traceMarker = new TraceMarker("marker2", "", 0, 50, "");
        assertEquals(traceMarker.getRGBAColor(), Integer.parseInt("FF0000"));

        traceMarker = new TraceMarker("marker3", "", 0, 50, "invalid");
        assertEquals(traceMarker.getRGBAColor(), Integer.parseInt("FF0000"));
    }

    @Test
    public void testMarkerTimeStamp() {

        long startTime = -10;
        long endTime = -2;
        long halfDuration = (fTrace.getStartTime().toNanos() + fTrace.getEndTime().toNanos()) / 2;

        // Test: negative value for startTime
        assertFalse(fTraceMarkerScriptingModule.addTraceMarker(startTime, fTrace.getEndTime().toNanos(), "", "", DEFAULT_COLOR));

        // Test: negative value for endTime
        assertFalse(fTraceMarkerScriptingModule.addTraceMarker(fTrace.getStartTime().toNanos(), endTime, "", "", ""));

        // Test: negative values for both startTime and endTime
        assertFalse(fTraceMarkerScriptingModule.addTraceMarker(startTime, endTime, "", "", ""));

        // Test: startTime greater than endtime
        assertFalse(fTraceMarkerScriptingModule.addTraceMarker(fTrace.getEndTime().toNanos(), fTrace.getStartTime().toNanos(), "", "", ""));

        // Test: startTime equal to endTime
        assertFalse(fTraceMarkerScriptingModule.addTraceMarker(fTrace.getStartTime().toNanos(), fTrace.getStartTime().toNanos(), "", "", ""));

        // Test: startTime below startTime of trace
        assertFalse(fTraceMarkerScriptingModule.addTraceMarker(fTrace.getStartTime().toNanos() - 1, fTrace.getStartTime().toNanos(), "", "", ""));

        // Test: endTime above endTime of trace
        assertFalse(fTraceMarkerScriptingModule.addTraceMarker(fTrace.getStartTime().toNanos(), fTrace.getStartTime().toNanos() + 1, "", "", ""));

        // Test: startTime and endTime below outside of trace time range
        assertFalse(fTraceMarkerScriptingModule.addTraceMarker(fTrace.getStartTime().toNanos() - 1, fTrace.getStartTime().toNanos() + 1, "", "", ""));

        // Test: startTime equal to starTime of trace
        assertTrue(fTraceMarkerScriptingModule.addTraceMarker(fTrace.getStartTime().toNanos(), fTrace.getStartTime().toNanos() + halfDuration, "", "", ""));

        // Test: endTime equal to endTime of trace
        assertTrue(fTraceMarkerScriptingModule.addTraceMarker(fTrace.getStartTime().toNanos() + halfDuration, fTrace.getEndTime().toNanos(), "", "", ""));

        // Test: duration equal to duration of trace
        assertTrue(fTraceMarkerScriptingModule.addTraceMarker(fTrace.getStartTime().toNanos(), fTrace.getEndTime().toNanos(), "", "", ""));

        // Test: startTime and endTime inside trace time range
        assertTrue(fTraceMarkerScriptingModule.addTraceMarker(fTrace.getStartTime().toNanos() + 1, fTrace.getEndTime().toNanos() - 1, "", "", ""));

    }

    @Test
    public void testMarkerLabel() {

    }

    @Test
    public void testMarkerCategory(){
        TraceMarker traceMarker = new TraceMarker("marker1", "", 0, 50, "blue");
        assertNotNull(traceMarker.getCategory());
    }

    @Test
    public void testAdaptersMarkersLists() {

    }

    @Test
    public void testAddTraceMarker() {

    }

    @Test
    public void testConfigureTraceMarker() {

    }


}
