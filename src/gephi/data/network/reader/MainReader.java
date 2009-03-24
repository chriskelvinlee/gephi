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

package gephi.data.network.reader;

import gephi.data.network.DhnsController;
import gephi.data.network.TreeStructure;
import gephi.data.network.edge.VirtualEdge;
import gephi.data.network.node.PreNode;
import gephi.data.network.node.treelist.SingleTreeIterator;
import gephi.data.network.sight.Sight;
import gephi.data.network.tree.iterators.edges.EdgesOutIterator;
import java.util.Iterator;

/**
 *
 * @author Mathieu
 */
public class MainReader {

    private Sight currentSight;

    public MainReader()
    {
        DhnsController controller = DhnsController.getInstance();
        currentSight = controller.getMainSight();
    }

    public Iterator<PreNode> getNodes()
    {
        return currentSight.getSightCache().getNodeIterator();
    }

    public Iterator<VirtualEdge> getEdges()
    {
        return currentSight.getSightCache().getEdgeIterator();
    }

    public boolean requireUpdate()
    {
        return requireNodeUpdate() || requireEdgeUpdate();
    }

    public boolean requireNodeUpdate()
    {
        return currentSight.getSightCache().requireNodeUpdate();
    }

    public boolean requireEdgeUpdate()
    {
        return currentSight.getSightCache().requireEdgeUpdate();
    }
}
