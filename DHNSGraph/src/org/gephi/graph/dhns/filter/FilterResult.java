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
package org.gephi.graph.dhns.filter;

import org.gephi.graph.api.Predicate;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.edge.iterators.AbstractEdgeIterator;
import org.gephi.graph.dhns.node.AbstractNode;
import org.gephi.graph.dhns.node.iterators.AbstractNodeIterator;
import org.gephi.graph.dhns.utils.avl.AbstractEdgeTree;
import org.gephi.graph.dhns.utils.avl.AbstractNodeTree;

public class FilterResult {

    private AbstractNodeTree nodeTree;
    private AbstractEdgeTree edgeTree;
    private NodeInFilterResultPredicate nodePredicate;
    private EdgeInFilterResultPredicate edgePredicate;

    public FilterResult(AbstractNodeTree nodeTree, AbstractEdgeTree edgeTree) {
        this.nodeTree = nodeTree;
        this.edgeTree = edgeTree;
        nodePredicate = new NodeInFilterResultPredicate();
        edgePredicate = new EdgeInFilterResultPredicate();
    }

    public AbstractNodeIterator nodeIterator() {
        return nodeTree.iterator();
    }

    public AbstractEdgeIterator edgeIterator() {
        return edgeTree.iterator();
    }

    public Predicate<AbstractNode> getNodePredicate() {
        return nodePredicate;
    }

    public Predicate<AbstractEdge> getEdgePredicate() {
        return edgePredicate;
    }

    public boolean evaluateNode(AbstractNode node) {
        return nodePredicate.evaluate(node);
    }

    public boolean evaluateEdge(AbstractEdge edge) {
        return edgePredicate.evaluate(edge);
    }

    public int getNodeCount() {
        return nodeTree.getCount();
    }

    public int getEdgeCount() {
        return edgeTree.getCount();
    }

    public AbstractNodeTree getNodeTree() {
        return nodeTree;
    }

    public AbstractEdgeTree getEdgeTree() {
        return edgeTree;
    }

    private class NodeInFilterResultPredicate implements Predicate<AbstractNode> {

        public boolean evaluate(AbstractNode element) {
            return nodeTree.contains(element);
        }
    }

    private class EdgeInFilterResultPredicate implements Predicate<AbstractEdge> {

        public boolean evaluate(AbstractEdge element) {
            if (element.isMetaEdge()) {
                return true;
            }
            return edgeTree.contains(element);
        }
    }
}
