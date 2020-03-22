package org.eclipse.tracecompass.incubator.scripting.core.tests.trace.marker;

import static org.junit.Assert.assertEquals;
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
import org.eclipse.tracecompass.tmf.core.TmfCommonConstants;
import org.eclipse.tracecompass.tmf.core.dataprovider.X11ColorUtils;
import org.eclipse.tracecompass.tmf.core.io.ResourceUtil;
import org.eclipse.tracecompass.tmf.core.presentation.RGBAColor;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceAdapterManager;
import org.eclipse.tracecompass.tmf.tests.stubs.trace.xml.TmfXmlTraceStub;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.IMarkerEventSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TraceMarkerTest {

 // ------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------

    private static final @NonNull String SOME_PROJECT_NAME = "myProject";
    private static final @NonNull String TRACE_PATH = "testfiles/traces/callstack.xml";
    private static final @NonNull String TRACE_FILE = "callstack.xml";
    private static final @NonNull String TRACE_FOLDER_PATH = "folder";
    private static final @NonNull String NONEXISTENT_FILE_TRACE = "NotARealFile.xml";
    private static final @NonNull String NONESISTENT_TRACE_IN_EXISTENT_PATH = "testfiles/traces/NotARealFile.xml";
    private static final @NonNull IProgressMonitor PROGRESS_MONITOR = new NullProgressMonitor();
    private static final TemporaryFolder TEMPORARY_FOLDER = new TemporaryFolder();
    /** The constant ALPHA for the marker transparency. */
    private static final int ALPHA = 70;

    private IProject fProject;
    private ITmfTrace fTrace;
    private ScriptingMarkerSource fScriptingMarkerSource;
    private ScriptingMarkerSourceFactory fScriptingMarkerSourceFactory;

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

        // Initialize test trace
        fTrace = ScriptingTestUtils.getTrace();
        assertNotNull(fTrace);
        assertTrue(fTrace instanceof TmfXmlTraceStub);

        // Initialize scriptingMarkerSource
        fScriptingMarkerSource = new ScriptingMarkerSource(fTrace);
        fScriptingMarkerSource.initializeAdapterMarkersLists();
        assertNotNull(fScriptingMarkerSource);
        assertNotNull(fScriptingMarkerSource.getMarkerCategories());
        assertTrue(fScriptingMarkerSource.getMarkerCategories().isEmpty());
        assertNotNull(fScriptingMarkerSource.getMarkerCategories());

        // Initialize scriptingMarkerSourceFactory
        fScriptingMarkerSourceFactory = ScriptingMarkerSourceFactory.getInstance();
        assertNotNull(fScriptingMarkerSourceFactory);
        assertNotNull(fScriptingMarkerSourceFactory.getAdapterList()[0]);
        assertTrue(fScriptingMarkerSourceFactory.getAdapterList()[0] instanceof IMarkerEventSource);

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


}
