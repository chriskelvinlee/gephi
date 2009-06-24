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
package org.gephi.graph.dhns.graph;

import java.util.Iterator;
import org.gephi.graph.api.ClusteredMixedGraph;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.gephi.graph.dhns.core.Dhns;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.edge.iterators.EdgeIterator;
import org.gephi.graph.dhns.edge.iterators.EdgeNodeIterator;
import org.gephi.graph.dhns.edge.iterators.VisibleEdgeIterator;
import org.gephi.graph.dhns.edge.iterators.VisibleEdgeNodeIterator;
import org.gephi.graph.dhns.node.PreNode;
import org.gephi.graph.dhns.node.iterators.CompleteTreeIterator;
import org.gephi.graph.dhns.node.iterators.NeighborIterator;
import org.gephi.graph.dhns.node.iterators.VisibleTreeIterator;

/**
 * Implementation of clustered sparse graph.
 *
 * @author Mathieu Bastian
 */
public class ClusteredMixedGraphImpl extends ClusteredGraphImpl implements ClusteredMixedGraph {

    private Condition<Edge> undirectedCondition;
    private Condition<Edge> directedCondition;

    public ClusteredMixedGraphImpl(Dhns dhns, boolean visible) {
        super(dhns, visible);
        directedCondition = new Condition<Edge>() {

            public boolean isValid(Edge t) {
                return t.isDirected();
            }
        };
        undirectedCondition = new Condition<Edge>() {

            public boolean isValid(Edge t) {
                return !t.isDirected();
            }
        };
    }

    public boolean addEdge(Edge edge) {
        AbstractEdge absEdge = checkEdge(edge);
        if (!edge.isDirected()) {
            throw new IllegalArgumentException("Can't add an undirected egde");
        }
        if (checkEdgeExist(absEdge.getSource(), absEdge.getTarget())) {
            //Edge already exist
            return false;
        }
        AbstractEdge symmetricEdge = getSymmetricEdge(absEdge);
        if (symmetricEdge != null && (!symmetricEdge.isDirected() || !absEdge.isDirected())) {
            //The symmetric edge exist and is undirected
            return false;
        }
        if (!absEdge.hasAttributes()) {
            absEdge.setAttributes(dhns.getGraphFactory().newEdgeAttributes());
        }
        dhns.getStructureModifier().addEdge(edge);
        if (absEdge.isDirected()) {
            dhns.touchDirected();
        } else {
            dhns.touchUndirected();
        }
        return true;
    }

    public boolean addEdge(Node source, Node target, boolean directed) {
        PreNode preSource = checkNode(source);
        PreNode preTarget = checkNode(target);
        if (checkEdgeExist(preSource, preTarget)) {
            //Edge already exist
            return false;
        }
        AbstractEdge symmetricEdge = preSource.getEdgesInTree().getItem(preTarget.getNumber());
        if (symmetricEdge != null && (!symmetricEdge.isDirected() || !directed)) {
            //The symmetric edge exist and is undirected
            return false;
        }

        AbstractEdge edge = dhns.getGraphFactory().newEdge(source, target, 1.0f, directed);
        dhns.getStructureModifier().addEdge(edge);
        if (directed) {
            dhns.touchDirected();
        } else {
            dhns.touchUndirected();
        }
        return true;
    }

    public boolean removeEdge(Edge edge) {
        AbstractEdge absEdge = checkEdge(edge);
        AbstractEdge undirected = absEdge.getUndirected();      //Ensure that the edge with the min id is removed before his mutual with a greater id
        return dhns.getStructureModifier().deleteEdge(undirected);
    }

    public EdgeIterable getDirectedEdges() {
        readLock();
        if (visible) {
            return dhns.newEdgeIterable(new VisibleEdgeIterator(dhns.getTreeStructure(), new VisibleTreeIterator(dhns.getTreeStructure()), false), directedCondition);
        } else {
            return dhns.newEdgeIterable(new EdgeIterator(dhns.getTreeStructure(), new CompleteTreeIterator(dhns.getTreeStructure()), false), directedCondition);
        }
    }

    public EdgeIterable getUndirectedEdges() {
        readLock();
        if (visible) {
            return dhns.newEdgeIterable(new VisibleEdgeIterator(dhns.getTreeStructure(), new VisibleTreeIterator(dhns.getTreeStructure()), false), undirectedCondition);
        } else {
            return dhns.newEdgeIterable(new EdgeIterator(dhns.getTreeStructure(), new CompleteTreeIterator(dhns.getTreeStructure()), false), undirectedCondition);
        }
    }

    public boolean isDirected(Edge edge) {
        AbstractEdge absEdge = checkEdge(edge);
        return absEdge.isDirected();
    }

    public boolean contains(Edge edge) {
        return getEdge(edge.getSource(), edge.getTarget()) != null;
    }

    public Edge getEdge(Node node1, Node node2) {
        PreNode sourceNode = checkNode(node1);
        PreNode targetNode = checkNode(node2);
        readLock();
        AbstractEdge res = null;
        if (visible) {
            AbstractEdge edge = sourceNode.getEdgesOutTree().getItem(targetNode.getNumber());
            if (edge == null) {
                edge = sourceNode.getEdgesInTree().getItem(targetNode.getNumber());
            } else if (edge.isVisible()) {
                res = edge;
            }
        } else {
            res = sourceNode.getEdgesOutTree().getItem(targetNode.getNumber());
            if (res == null) {
                res = sourceNode.getEdgesInTree().getItem(targetNode.getNumber());
            }
        }
        readUnlock();
        return res;
    }

    public EdgeIterable getEdges() {
        readLock();
        if (visible) {
            return dhns.newEdgeIterable(new VisibleEdgeIterator(dhns.getTreeStructure(), new VisibleTreeIterator(dhns.getTreeStructure()), false));
        } else {
            return dhns.newEdgeIterable(new EdgeIterator(dhns.getTreeStructure(), new CompleteTreeIterator(dhns.getTreeStructure()), false));
        }
    }

    public NodeIterable getNeighbors(Node node) {
        PreNode preNode = checkNode(node);
        readLock();
        if (visible) {
            return dhns.newNodeIterable(new NeighborIterator(new VisibleEdgeNodeIterator(preNode, VisibleEdgeNodeIterator.EdgeNodeIteratorMode.BOTH, true), preNode));
        } else {
            return dhns.newNodeIterable(new NeighborIterator(new EdgeNodeIterator(preNode, EdgeNodeIterator.EdgeNodeIteratorMode.BOTH, true), preNode));
        }
    }

    public EdgeIterable getEdges(Node node) {
        PreNode preNode = checkNode(node);
        readLock();
        if (visible) {
            return dhns.newEdgeIterable(new VisibleEdgeNodeIterator(preNode, VisibleEdgeNodeIterator.EdgeNodeIteratorMode.BOTH, false));
        } else {
            return dhns.newEdgeIterable(new EdgeNodeIterator(preNode, EdgeNodeIterator.EdgeNodeIteratorMode.BOTH, false));
        }
    }

    public int getEdgeCount() {
        readLock();
        int count = 0;
        if (visible) {
            for (VisibleEdgeIterator itr = new VisibleEdgeIterator(dhns.getTreeStructure(), new VisibleTreeIterator(dhns.getTreeStructure()), false); itr.hasNext();) {
                itr.next();
                count++;
            }
        } else {
            for (EdgeIterator itr = new EdgeIterator(dhns.getTreeStructure(), new CompleteTreeIterator(dhns.getTreeStructure()), false); itr.hasNext();) {
                itr.next();
                count++;
            }
        }
        readUnlock();
        return count;
    }

    public int getDegree(Node node) {
        return getInDegree(node) + getOutDegree(node);
    }

    //Directed
    public int getInDegree(Node node) {
        PreNode preNode = checkNode(node);
        readLock();
        int count = 0;
        if (visible && !preNode.getEdgesInTree().isEmpty()) {
            for (Iterator<AbstractEdge> itr = preNode.getEdgesInTree().iterator(); itr.hasNext();) {
                if (itr.next().isVisible()) {
                    count++;
                }
            }
        } else {
            count = preNode.getEdgesInTree().getCount();
        }
        readUnlock();
        return count;
    }

    //Directed
    public int getOutDegree(Node node) {
        PreNode preNode = checkNode(node);
        readLock();
        int count = 0;
        if (visible && !preNode.getEdgesInTree().isEmpty()) {
            for (Iterator<AbstractEdge> itr = preNode.getEdgesOutTree().iterator(); itr.hasNext();) {
                if (itr.next().isVisible()) {
                    count++;
                }
            }
        } else {
            count = preNode.getEdgesOutTree().getCount();
        }
        readUnlock();
        return count;
    }

    public boolean isAdjacent(Node node1, Node node2) {
        if (node1 == node2) {
            throw new IllegalArgumentException("Nodes can't be the same");
        }
        return isSuccessor(node1, node2) || isPredecessor(node1, node2);
    }

    //Directed
    public boolean isSuccessor(Node node, Node successor) {
        return getEdge(node, successor) != null;
    }

    //Directed
    public boolean isPredecessor(Node node, Node predecessor) {
        return getEdge(predecessor, node) != null;
    }

    public EdgeIterable getInnerEdges(Node nodeGroup) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public EdgeIterable getOuterEdges(Node nodeGroup) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public EdgeIterable getMetaEdges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public EdgeIterable getMetaEdges(Node node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMetaDegree(Node node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public EdgeIterable getMetaEdgeContent(Edge metaEdge) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
