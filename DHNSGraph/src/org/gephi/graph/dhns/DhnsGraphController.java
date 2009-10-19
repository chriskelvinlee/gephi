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
package org.gephi.graph.dhns;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeRowFactory;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.dhns.core.Dhns;
import org.gephi.graph.dhns.core.GraphFactoryImpl;
import org.gephi.graph.dhns.core.IDGen;
import org.gephi.project.api.ProjectController;
import org.gephi.workspace.api.Workspace;
import org.openide.util.Lookup;

/**
 * Singleton which manages the graph access.
 *
 * @author Mathieu Bastian
 */
public class DhnsGraphController implements GraphController {

    //Common architecture
    protected IDGen iDGen;
    protected GraphFactoryImpl factory;
    private AttributeRowFactory attributesFactory;
    private Executor eventBus;
    private GraphWorkspaceDataProvider workspaceDataProvider;

    public DhnsGraphController() {
        iDGen = new IDGen();

        if (Lookup.getDefault().lookup(AttributeController.class) != null) {
            attributesFactory = Lookup.getDefault().lookup(AttributeController.class).rowFactory();
        }

        factory = new GraphFactoryImpl(iDGen, attributesFactory);
        eventBus = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

            public Thread newThread(Runnable r) {
                return new Thread(r, "DHNS Event Bus");
            }
        });

        workspaceDataProvider = Lookup.getDefault().lookup(GraphWorkspaceDataProvider.class);
    }

    public Dhns newDhns(Workspace workspace) {
        Dhns dhns = new Dhns(this);
        workspace.getWorkspaceData().setData(workspaceDataProvider.getWorkspaceDataKey(), dhns);
        return dhns;
    }

    public Executor getEventBus() {
        return eventBus;
    }

    public GraphFactoryImpl factory() {
        return factory;
    }

    public IDGen getIDGen() {
        return iDGen;
    }

    private Dhns getCurrentDhns() {
        Workspace currentWorkspace = Lookup.getDefault().lookup(ProjectController.class).getCurrentWorkspace();
        if (currentWorkspace == null) {
            return null;
        }
        Dhns dhns = currentWorkspace.getWorkspaceData().getData(workspaceDataProvider.getWorkspaceDataKey());
        if (dhns == null) {
            dhns = newDhns(currentWorkspace);
        }
        return dhns;
    }

    public GraphModel getModel() {
        return getCurrentDhns();
    }
}
