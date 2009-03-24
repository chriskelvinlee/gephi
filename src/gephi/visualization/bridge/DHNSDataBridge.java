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

package gephi.visualization.bridge;

import gephi.data.network.Edge;
import gephi.data.network.Node;
import gephi.data.network.node.PreNode;
import gephi.data.network.reader.MainReader;
import gephi.visualization.VizArchitecture;
import gephi.visualization.VizController;
import gephi.visualization.initializer.Object3dInitializer;
import gephi.visualization.objects.Object3dClass;
import gephi.visualization.opengl.AbstractEngine;
import gephi.visualization.opengl.Object3d;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Mathieu
 */
public class DHNSDataBridge implements DataBridge, VizArchitecture {

    //Architecture
    protected AbstractEngine engine;
    protected MainReader reader;
    private AtomicBoolean update = new AtomicBoolean(true);

    @Override
    public void initArchitecture() {
       this.engine = VizController.getInstance().getEngine();
       this.reader = new MainReader();
    }

    public void updateWorld() {
        updateNodes();
        updateEdges();
    }


    private void updateNodes()
    {
        Object3dInitializer nodeInit = engine.getObject3dClasses()[AbstractEngine.CLASS_NODE].getCurrentObject3dInitializer();

        Iterator<PreNode> itr = reader.getNodes();
        for(;itr.hasNext();itr.next())
        {
            PreNode preNode = itr.next();
            Node n = preNode.initNodeInstance();
            n.size = 10f;

            Object3d obj = nodeInit.initObject(n);
            engine.addObject(AbstractEngine.CLASS_NODE, obj);
        }
    }

    private void updateEdges()
    {

    }

    public boolean requireUpdate() {
        return update.getAndSet(false);
    }
    
}
