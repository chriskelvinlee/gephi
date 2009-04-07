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
package org.gephi.project.controller;

import org.gephi.branding.desktop.actions.SaveAsProject;
import org.gephi.project.api.Project;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Projects;
import org.gephi.project.api.Workspace;
import org.openide.util.actions.SystemAction;
import org.gephi.branding.desktop.actions.SaveProject;
import org.gephi.project.api.Project.Status;
import org.gephi.project.filetype.GephiDataObject;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu
 */
public class DesktopProjectController implements ProjectController {

    private Projects projects = new Projects();

    public DesktopProjectController() {
        //Actions
        disableAction(SaveProject.class);
        disableAction(SaveAsProject.class);
    }

    public void newProject() {
        closeCurrentProject();
        Project project = new Project();
        projects.addProject(project);
        openProject(project);
    }

    public void loadProject(DataObject dataObject) {
        GephiDataObject gephiDataObject = (GephiDataObject) dataObject;
        Project project = gephiDataObject.load();
        if (project != null) {
            projects.addProject(project);
            enableAction(SaveAsProject.class);
        }
    }

    public void closeCurrentProject()
    {
        if(projects.getCurrentProject()!=null)
        {
            closeProject(projects.getCurrentProject());
        }
    }

    public void closeProject(Project project) {

        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(DesktopProjectController.class, "CloseProject_confirm_message"),
                NbBundle.getMessage(DesktopProjectController.class, "CloseProject_confirm_title"),
                NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION) {
            // really do it...
        }

        project.setStatus(Status.CLOSED);
        disableAction(SaveProject.class);
        disableAction(SaveAsProject.class);
        projects.setCurrentProject(null);
    }

    public void removeProject(Project project) {
        if (projects.getCurrentProject() == project) {
            closeProject(project);
        }
        projects.removeProject(project);
    }

    public Projects getProjects() {
        return projects;
    }

    public void setProjects(Projects projects) {
        this.projects = projects;
    }

    public Workspace newWorkspace(Project project) {
        Workspace ws = project.newWorkspace();
        enableAction(SaveProject.class);
        return ws;
    }

    public Workspace importFile() {
        if(projects.getCurrentProject()==null)
            newProject();
        return newWorkspace(projects.getCurrentProject());
    }

    public void deleteWorkspace(Workspace workspace) {
        workspace.getProject().removeWorkspace(workspace);
        enableAction(SaveProject.class);
    }

    public void openProject(Project project) {
        if (projects.getCurrentProject() != null) {
            closeProject(projects.getCurrentProject());
        }
        projects.setCurrentProject(project);
        enableAction(SaveAsProject.class);
    }

    public Project getCurrentProject() {
        return projects.getCurrentProject();
    }

    public void renameProject(Project project, String name) {
        project.setName(name);
        enableAction(SaveProject.class);
    }

    public void enableAction(Class clazz) {
        SystemAction action = SystemAction.get(clazz);
        if (action != null) {
            action.setEnabled(true);
        }
    }

    public void disableAction(Class clazz) {
        SystemAction action = SystemAction.get(clazz);
        if (action != null) {
            action.setEnabled(false);
        }
    }
}
