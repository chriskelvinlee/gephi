/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.datalaboratory.impl;

import org.gephi.datalaboratory.api.GraphElementsController;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Implementation of the GraphElementsController interface 
 * declared in the Data Laboratory API
 * @author Eduardo Ramos <eduramiba@gmail.com>
 * @see GraphElementsController
 */
@ServiceProvider(service = GraphElementsController.class)
public class GraphElementsControllerImpl implements GraphElementsController {

    public void deleteNode(Node node) {
        removeNode(node, getGraph());
    }

    public void deleteNodes(Node[] nodes) {
        Graph graph = getGraph();
        for (Node node : nodes) {
            removeNode(node, graph);
        }
    }

    public void deleteEdge(Edge edge) {
        removeEdge(edge, getGraph());
    }

    public void deleteEdges(Edge[] edges) {
        Graph graph = getGraph();
        for (Edge edge : edges) {
            removeEdge(edge, graph);
        }
    }

    public boolean groupNodes(Node[] nodes) {
        if (canGroupNodes(nodes)) {
            HierarchicalGraph hg = getHierarchicalGraph();
            Node group = hg.groupNodes(nodes);
            hg.readLock();
            group.getNodeData().setLabel(NbBundle.getMessage(GraphElementsControllerImpl.class, "Group.nodeCount.label", hg.getChildrenCount(group)));
            hg.readUnlock();
            return true;
        } else {
            return false;
        }
    }

    public boolean canGroupNodes(Node[] nodes) {
        if(!areNodesInGraph(nodes)){
            return false;
        }
        HierarchicalGraph hg = getHierarchicalGraph();
        Node parent = hg.getParent(nodes[0]);
        for (Node n : nodes) {
            if (hg.getParent(n) != parent) {
                return false;
            }
        }
        return true;
    }

    public boolean ungroupNode(Node node) {
        if (canUngroupNode(node)) {
            HierarchicalGraph hg = getHierarchicalGraph();
            hg.ungroupNodes(node);
            return true;
        } else {
            return false;
        }
    }

    public void ungroupNodes(Node[] nodes) {
        for (Node n : nodes) {
            ungroupNode(n);
        }
    }

    public boolean ungroupNodeRecursively(Node node) {
        if (canUngroupNode(node)) {
            HierarchicalGraph hg = getHierarchicalGraph();
            for (Node n : hg.getDescendant(node).toArray()) {
                ungroupNode(n);
            }
            hg.ungroupNodes(node);
            return true;
        } else {
            return false;
        }
    }

    public void ungroupNodesRecursively(Node[] nodes) {
        for (Node n : nodes) {
            ungroupNodeRecursively(n);
        }
    }

    public boolean canUngroupNode(Node node) {
        if(!isNodeInGraph(node)){
            return false;
        }
        boolean canUngroup;
        HierarchicalGraph hg = getHierarchicalGraph();
        hg.readLock();
        canUngroup = hg.getChildrenCount(node) > 0;
        hg.readUnlock();
        return canUngroup;
    }

    public boolean removeNodeFromGroup(Node node) {
        if (isNodeInGroup(node)) {
            HierarchicalGraph hg = getHierarchicalGraph();
            Node parent = hg.getParent(node);
            hg.readLock();
            int childrenCount = hg.getChildrenCount(parent);
            hg.readUnlock();
            if (childrenCount == 1) {
                hg.ungroupNodes(parent);//Break group when the last child is removed.
            } else {
                hg.removeFromGroup(node);
            }
            return true;
        } else {
            return false;
        }
    }

    public void removeNodesFromGroup(Node[] nodes) {
        for (Node n : nodes) {
            removeNodeFromGroup(n);
        }
    }

    public boolean isNodeInGroup(Node node) {
        if(!isNodeInGraph(node)){
            return false;
        }
        HierarchicalGraph hg = getHierarchicalGraph();
        return hg.getParent(node) != null;
    }

    public void setNodeFixed(Node node, boolean fixed){
        if(isNodeInGraph(node)){
            node.getNodeData().setFixed(fixed);
        }
    }

    public void setNodesFixed(Node[] nodes, boolean fixed){
        for(Node n:nodes){
            setNodeFixed(n, fixed);
        }
    }

    public boolean isNodeFixed(Node node){
        return node.getNodeData().isFixed();
    }

    public boolean isNodeInGraph(Node node) {
        return getGraph().contains(node);
    }

    public boolean areNodesInGraph(Node[] nodes) {
        Graph graph = getGraph();
        for (Node n : nodes) {
            if (!graph.contains(n)) {
                return false;
            }
        }
        return true;
    }

    public boolean isEdgeInGraph(Edge edge) {
        return getGraph().contains(edge);
    }

    public boolean areEdgesInGraph(Edge[] edges) {
        Graph graph = getGraph();
        for (Edge e : edges) {
            if (!graph.contains(e)) {
                return false;
            }
        }
        return true;
    }

    /************Private methods : ************/
    private Graph getGraph() {
        return Lookup.getDefault().lookup(GraphController.class).getModel().getGraph();
    }

    private HierarchicalGraph getHierarchicalGraph() {
        return Lookup.getDefault().lookup(GraphController.class).getModel().getHierarchicalGraph();
    }

    private void removeNode(Node node, Graph graph) {
        if (isNodeInGraph(node)) {
            graph.removeNode(node);
        }
    }

    private void removeEdge(Edge edge, Graph graph) {
        if (isEdgeInGraph(edge)) {
            graph.removeEdge(edge);
        }
    }    
}
