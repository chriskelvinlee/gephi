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
package org.gephi.visualization;

import org.gephi.project.api.ProjectController;
import org.gephi.workspace.api.Workspace;
import org.gephi.workspace.api.WorkspaceDataKey;
import org.gephi.workspace.api.WorkspaceDataProvider;
import org.openide.util.Lookup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Mathieu Bastian
 */
public class VizWorkspaceDataProvider implements WorkspaceDataProvider<VizModel> {

    private WorkspaceDataKey<VizModel> key;

    public VizWorkspaceDataProvider() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        Workspace workspace = pc.getCurrentWorkspace();
        if (workspace != null) {
            VizModel currentModel = workspace.getWorkspaceData().getData(key);
        }
    }

    public Element writeXML(Document document) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public VizModel readXML(Element element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public VizModel getDefaultData() {
        return null;
    }

    public boolean isPersistent() {
        return true;
    }

    public String getName() {
        return "vizmodel";
    }

    public void setWorkspaceDataKey(WorkspaceDataKey<VizModel> key) {
        this.key = key;
    }
}
