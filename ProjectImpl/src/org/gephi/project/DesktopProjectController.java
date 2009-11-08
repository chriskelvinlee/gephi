/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.gephi.project.api.Project;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Projects;
import org.gephi.workspace.api.Workspace;
import org.gephi.io.project.GephiDataObject;
import org.gephi.io.project.LoadTask;
import org.gephi.io.project.SaveTask;
import org.gephi.project.ProjectImpl;
import org.gephi.project.ProjectsImpl;
import org.gephi.ui.utils.DialogFileFilter;
import org.gephi.utils.longtask.LongTask;
import org.gephi.utils.longtask.LongTaskErrorHandler;
import org.gephi.utils.longtask.LongTaskExecutor;
import org.gephi.utils.longtask.LongTaskListener;
import org.gephi.workspace.api.WorkspaceListener;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;
import org.openide.windows.WindowManager;

/**
 *
 * @author Mathieu Bastian
 */
public class DesktopProjectController implements ProjectController {

    private enum EventType {

        INITIALIZE, SELECT, UNSELECT, CLOSE, DISABLE
    };
    private final Projects projects = new ProjectsImpl();
    private final LongTaskExecutor longTaskExecutor;
    private final List<WorkspaceListener> listeners;

    public DesktopProjectController() {
        //Project IO executor
        longTaskExecutor = new LongTaskExecutor(true, "Project IO");
        longTaskExecutor.setDefaultErrorHandler(new LongTaskErrorHandler() {

            public void fatalError(Throwable t) {
                unlockProjectActions();
                NotifyDescriptor.Exception ex = new NotifyDescriptor.Exception(t);
                DialogDisplayer.getDefault().notify(ex);
                t.printStackTrace();
            }
        });
        longTaskExecutor.setLongTaskListener(new LongTaskListener() {

            public void taskFinished(LongTask task) {
                unlockProjectActions();
            }
        });

        //Listeners
        listeners = new ArrayList<WorkspaceListener>();
        listeners.addAll(Lookup.getDefault().lookupAll(WorkspaceListener.class));

        //Actions
        disableAction("SaveProject");
        disableAction("SaveAsProject");
        disableAction("ProjectProperties");
        disableAction("CloseProject");
        disableAction("NewWorkspace");
        disableAction("DeleteWorkspace");
        disableAction("CleanWorkspace");
        disableAction("DuplicateWorkspace");
    }

    public void startup() {
        final String OPEN_LAST_PROJECT_ON_STARTUP = "Open_Last_Project_On_Startup";
        final String NEW_PROJECT_ON_STARTUP = "New_Project_On_Startup";
        boolean openLastProject = NbPreferences.forModule(DesktopProjectController.class).getBoolean(OPEN_LAST_PROJECT_ON_STARTUP, false);
        boolean newProject = NbPreferences.forModule(DesktopProjectController.class).getBoolean(NEW_PROJECT_ON_STARTUP, false);

        //Default project
        if (!openLastProject && newProject) {
            newProject();
        }
    }

    private void lockProjectActions() {
        disableAction("SaveProject");
        disableAction("SaveAsProject");
        disableAction("OpenProject");
        disableAction("CloseProject");
        disableAction("NewProject");
        disableAction("OpenFile");
        disableAction("NewWorkspace");
        disableAction("DeleteWorkspace");
        disableAction("CleanWorkspace");
        disableAction("DuplicateWorkspace");
    }

    private void unlockProjectActions() {
        if (projects.hasCurrentProject()) {
            enableAction("SaveProject");
            enableAction("SaveAsProject");
            enableAction("CloseProject");
            enableAction("NewWorkspace");
            if (projects.getCurrentProject().hasCurrentWorkspace()) {
                enableAction("DeleteWorkspace");
                enableAction("CleanWorkspace");
                enableAction("DuplicateWorkspace");
            }
        }
        enableAction("OpenProject");
        enableAction("NewProject");
        enableAction("OpenFile");
    }

    public void newProject() {
        closeCurrentProject();
        ProjectImpl project = new ProjectImpl();
        projects.addProject(project);
        openProject(project);
    }

    public void loadProject(DataObject dataObject) {
        final GephiDataObject gephiDataObject = (GephiDataObject) dataObject;
        LoadTask loadTask = new LoadTask(gephiDataObject);
        lockProjectActions();
        longTaskExecutor.execute(loadTask, loadTask);
    }

    public void saveProject(DataObject dataObject) {
        GephiDataObject gephiDataObject = (GephiDataObject) dataObject;
        Project project = getCurrentProject();
        project.setDataObject(gephiDataObject);
        gephiDataObject.setProject(project);
        SaveTask saveTask = new SaveTask(gephiDataObject);
        lockProjectActions();
        longTaskExecutor.execute(saveTask, saveTask);
    }

    public void saveProject(Project project) {
        if (project.hasFile()) {
            GephiDataObject gephiDataObject = (GephiDataObject) project.getDataObject();
            saveProject(gephiDataObject);
        } else {
            saveAsProject(project);
        }
    }

    public void saveAsProject(Project project) {
        final String LAST_PATH = "SaveAsProject_Last_Path";
        final String LAST_PATH_DEFAULT = "SaveAsProject_Last_Path_Default";

        DialogFileFilter filter = new DialogFileFilter(NbBundle.getMessage(DesktopProjectController.class, "SaveAsProject_filechooser_filter"));
        filter.addExtension(".gephi");

        //Get last directory
        String lastPathDefault = NbPreferences.forModule(DesktopProjectController.class).get(LAST_PATH_DEFAULT, null);
        String lastPath = NbPreferences.forModule(DesktopProjectController.class).get(LAST_PATH, lastPathDefault);

        //File chooser
        final JFileChooser chooser = new JFileChooser(lastPath);
        chooser.addChoosableFileFilter(filter);
        int returnFile = chooser.showSaveDialog(null);
        if (returnFile == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            //Save last path
            NbPreferences.forModule(DesktopProjectController.class).put(LAST_PATH, file.getAbsolutePath());

            //File management
            try {
                if (!file.getPath().endsWith(".gephi")) {
                    file = new File(file.getPath() + ".gephi");
                }
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        String failMsg = NbBundle.getMessage(
                                DesktopProjectController.class,
                                "SaveAsProject_SaveFailed", new Object[]{file.getPath()});
                        JOptionPane.showMessageDialog(null, failMsg);
                        return;
                    }
                } else {
                    String overwriteMsg = NbBundle.getMessage(
                            DesktopProjectController.class,
                            "SaveAsProject_Overwrite", new Object[]{file.getPath()});
                    if (JOptionPane.showConfirmDialog(null, overwriteMsg) != JOptionPane.OK_OPTION) {
                        return;
                    }
                }
                file = FileUtil.normalizeFile(file);
                FileObject fileObject = FileUtil.toFileObject(file);

                //File exist now, Save project
                DataObject dataObject = DataObject.find(fileObject);
                saveProject(dataObject);

            } catch (Exception e) {
                Exceptions.printStackTrace(e);
            }
        }
    }

    public void closeCurrentProject() {
        if (projects.hasCurrentProject()) {
            Project currentProject = projects.getCurrentProject();

            //Save ?
            String messageBundle = NbBundle.getMessage(DesktopProjectController.class, "CloseProject_confirm_message");
            String titleBundle = NbBundle.getMessage(DesktopProjectController.class, "CloseProject_confirm_title");
            String saveBundle = NbBundle.getMessage(DesktopProjectController.class, "CloseProject_confirm_save");
            String doNotSaveBundle = NbBundle.getMessage(DesktopProjectController.class, "CloseProject_confirm_doNotSave");
            String cancelBundle = NbBundle.getMessage(DesktopProjectController.class, "CloseProject_confirm_cancel");
            NotifyDescriptor msg = new NotifyDescriptor(messageBundle, titleBundle,
                    NotifyDescriptor.YES_NO_CANCEL_OPTION,
                    NotifyDescriptor.INFORMATION_MESSAGE,
                    new Object[]{saveBundle, doNotSaveBundle, cancelBundle}, saveBundle);
            Object result = DialogDisplayer.getDefault().notify(msg);
            if (result == saveBundle) {
                saveProject(currentProject);
            } else if (result == cancelBundle) {
                return;
            }

            //Close
            currentProject.close();
            projects.closeCurrentProject();


            //Actions
            disableAction("SaveProject");
            disableAction("SaveAsProject");
            disableAction("ProjectProperties");
            disableAction("CloseProject");
            disableAction("NewWorkspace");
            disableAction("DeleteWorkspace");
            disableAction("CleanWorkspace");
            disableAction("DuplicateWorkspace");

            //Title bar
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                    String title = frame.getTitle();
                    title = title.substring(0, title.indexOf('-') - 1);
                    frame.setTitle(title);
                }
            });

            //Event
            if (currentProject.hasCurrentWorkspace()) {
                fireWorkspaceEvent(EventType.UNSELECT, currentProject.getCurrentWorkspace());
            }
            for (Workspace ws : currentProject.getWorkspaces()) {
                fireWorkspaceEvent(EventType.CLOSE, ws);
            }
            fireWorkspaceEvent(EventType.DISABLE, null);
        }
    }

    public void removeProject(Project project) {
        if (projects.getCurrentProject() == project) {
            closeCurrentProject();
        }
        projects.removeProject(project);
    }

    public Projects getProjects() {
        return projects;
    }

    public void setProjects(Projects projects) {
        final String OPEN_LAST_PROJECT_ON_STARTUP = "Open_Last_Project_On_Startup";
        boolean openLastProject = NbPreferences.forModule(DesktopProjectController.class).getBoolean(OPEN_LAST_PROJECT_ON_STARTUP, false);

        Project lastOpenProject = null;
        for (Project p : ((ProjectsImpl) projects).getProjects()) {
            if (p.hasFile()) {
                ProjectImpl pImpl = (ProjectImpl) p;
                pImpl.init();
                this.projects.addProject(p);
                pImpl.close();
                if (p == projects.getCurrentProject()) {
                    lastOpenProject = p;
                }
            }
        }

        if (openLastProject && lastOpenProject != null && !lastOpenProject.isInvalid() && lastOpenProject.hasFile()) {
            openProject(lastOpenProject);
        } else {
            //newProject();
        }
    }

    public Workspace newWorkspace(Project project) {
        Workspace workspace = project.newWorkspace();

        //Event
        fireWorkspaceEvent(EventType.INITIALIZE, workspace);
        return workspace;
    }

    /*public Workspace importFile() {
    Project project = projects.getCurrentProject();
    if (project == null) {
    newProject();
    project = projects.getCurrentProject();
    }

    Workspace ws = newWorkspace(projects.getCurrentProject());
    openWorkspace(ws);
    return ws;
    }*/
    public void deleteWorkspace(Workspace workspace) {
        if (getCurrentWorkspace() == workspace) {
            closeCurrentProject();
        }

        workspace.getProject().removeWorkspace(workspace);

        //Event
        fireWorkspaceEvent(EventType.CLOSE, workspace);

        if (getCurrentProject() == null || getCurrentProject().getWorkspaces().length == 0) {
            //Event
            fireWorkspaceEvent(EventType.DISABLE, workspace);
        }
    }

    public void openProject(final Project project) {
        if (projects.hasCurrentProject()) {
            closeCurrentProject();
        }
        projects.addProject(project);
        projects.setCurrentProject(project);
        project.open();
        if (!project.hasCurrentWorkspace()) {
            if (project.getWorkspaces().length == 0) {
                Workspace workspace = project.newWorkspace();
                openWorkspace(workspace);
            } else {
                Workspace workspace = project.getWorkspaces()[0];
                openWorkspace(workspace);
            }
        }
        enableAction("SaveAsProject");
        enableAction("ProjectProperties");
        enableAction("SaveProject");
        enableAction("CloseProject");
        enableAction("NewWorkspace");
        if (project.hasCurrentWorkspace()) {
            enableAction("DeleteWorkspace");
            enableAction("CleanWorkspace");
            enableAction("DuplicateWorkspace");
        }

        //Title bar
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                String title = frame.getTitle() + " - " + project.getName();
                frame.setTitle(title);
            }
        });

        //Status line
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(DesktopProjectController.class, "DesktoProjectController.status.opened", project.getName()));
    }

    public Project getCurrentProject() {
        return projects.getCurrentProject();
    }

    public Workspace getCurrentWorkspace() {
        if (projects.hasCurrentProject()) {
            return getCurrentProject().getCurrentWorkspace();
        }
        return null;
    }

    public void closeCurrentWorkspace() {
        Workspace workspace = getCurrentWorkspace();
        if (workspace != null) {
            workspace.close();

            //Event
            fireWorkspaceEvent(EventType.UNSELECT, workspace);
        }
    }

    public void openWorkspace(Workspace workspace) {
        closeCurrentWorkspace();
        getCurrentProject().setCurrentWorkspace(workspace);
        workspace.open();

        //Event
        fireWorkspaceEvent(EventType.SELECT, workspace);
    }

    public void cleanWorkspace(Workspace workspace) {
    }

    public void duplicateWorkspace(Workspace workspace) {
    }

    public void renameProject(Project project, final String name) {
        project.setName(name);

        //Title bar
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                String title = frame.getTitle();
                title = title.substring(0, title.indexOf('-') - 1);
                title += " - " + name;
                frame.setTitle(title);
            }
        });
    }

    public void renameWorkspace(Workspace workspace, String name) {
        workspace.setName(name);
    }

    public void addWorkspaceListener(WorkspaceListener workspaceListener) {
        synchronized (listeners) {
            listeners.add(workspaceListener);
        }
    }

    public void removeWorkspaceListener(WorkspaceListener workspaceListener) {
        synchronized (listeners) {
            listeners.remove(workspaceListener);
        }
    }

    private void fireWorkspaceEvent(EventType event, Workspace workspace) {
        WorkspaceListener[] listenersArray;
        synchronized (listeners) {
            listenersArray = listeners.toArray(new WorkspaceListener[0]);
        }
        for (WorkspaceListener wl : listenersArray) {
            switch (event) {
                case INITIALIZE:
                    wl.initialize(workspace);
                    break;
                case SELECT:
                    wl.select(workspace);
                    break;
                case UNSELECT:
                    wl.unselect(workspace);
                    break;
                case CLOSE:
                    wl.close(workspace);
                    break;
                case DISABLE:
                    wl.disable();
                    break;
            }
        }
    }

    public void enableAction(String actionName) {
        /*boolean found = false;
        List<? extends Action> actionsFile = Utilities.actionsForPath("Actions/File/");
        for (Action a : actionsFile) {
        if (a.getValue(Action.NAME).equals(actionName)) {
        a.setEnabled(true);
        found = true;
        }
        }
        if (!found) {
        List<? extends Action> actionsEdit = Utilities.actionsForPath("Actions/Edit/");
        for (Action a : actionsEdit) {
        if (a.getValue(Action.NAME).equals(actionName)) {
        a.setEnabled(true);
        found = true;
        }
        }
        }
        if (!found) {
        throw new IllegalArgumentException(actionName + " cannot be found");
        }*/
    }

    public void disableAction(String actionName) {
        /*boolean found = false;
        List<? extends Action> actions = Utilities.actionsForPath("Actions/File/");
        for (Action a : actions) {
        if (a.getValue(Action.NAME).equals(actionName)) {
        a.setEnabled(false);
        found = true;
        }
        }
        if (!found) {
        List<? extends Action> actionsEdit = Utilities.actionsForPath("Actions/Edit/");
        for (Action a : actionsEdit) {
        if (a.getValue(Action.NAME).equals(actionName)) {
        a.setEnabled(false);
        found = true;
        }
        }
        }
        if (!found) {
        throw new IllegalArgumentException(actionName + " cannot be found");
        }*/
    }

    /*public Action findAction(String key) {
    FileObject fo = FileUtil.getConfigFile(key);
    if (fo != null && fo.isValid()) {
    try {
    DataObject dob = DataObject.find(fo);
    InstanceCookie ic = dob.getLookup().lookup(InstanceCookie.class);
    if (ic != null) {
    Object instance = ic.instanceCreate();
    if (instance instanceof Action) {
    Action a = (Action) instance;
    return a;
    }
    }
    } catch (Exception e) {
    ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
    return null;
    }
    }
    return null;
    }*/
}
