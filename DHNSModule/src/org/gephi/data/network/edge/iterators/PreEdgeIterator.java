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

package org.gephi.data.network.edge.iterators;

import java.util.Iterator;
import org.gephi.data.network.edge.PreEdge;
import org.gephi.data.network.node.PreNode;
import org.gephi.data.network.node.treelist.CompleteTreeIterator;
import org.gephi.data.network.tree.TreeStructure;
import org.gephi.datastructure.avl.param.ParamAVLIterator;

/**
 * Iterator for physical edges for the complete graph.
 *
 * @author Mathieu Bastian
 */
public class PreEdgeIterator implements Iterator<PreEdge> {

    protected CompleteTreeIterator treeIterator;
    protected ParamAVLIterator<PreEdge> edgeIterator;
    protected PreNode currentNode;
    protected PreEdge pointer;

    public PreEdgeIterator(TreeStructure treeStructure) {
        treeIterator = new CompleteTreeIterator(treeStructure);
        edgeIterator = new ParamAVLIterator<PreEdge>();
    }

    @Override
    public boolean hasNext() {
        while (!edgeIterator.hasNext()) {
            if (treeIterator.hasNext()) {
                currentNode = treeIterator.next();
                if(currentNode.countForwardEdges() > 0)
                {
                    edgeIterator.setNode(currentNode.getForwardEdges());
                }
            } else {
                return false;
            }
        }

        pointer = edgeIterator.next();
        return true;
    }

    @Override
    public PreEdge next() {
        return pointer;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
