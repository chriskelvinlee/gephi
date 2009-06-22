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
package org.gephi.graph.dhns.core;

import org.gephi.graph.api.Attributes;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.Node;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.edge.MetaEdgeImpl;
import org.gephi.graph.dhns.edge.ProperEdgeImpl;
import org.gephi.graph.dhns.edge.SelfLoopImpl;
import org.gephi.graph.dhns.edge.MixedEdgeImpl;
import org.gephi.graph.dhns.node.AbstractNode;
import org.gephi.graph.dhns.node.PreNode;

/**
 * Implementation of a basic node and edge factory. If possible set {@link Attributes} to objets.
 * <p>
 * Return {@link AbstractNode} or {@link AbstractEdge}.
 *
 * @author Mathieu Bastian
 */
public class GraphFactoryImpl implements GraphFactory {

    private Dhns dhns;
    private IDGen idGen;

    public GraphFactoryImpl(Dhns dhns) {
        this.dhns = dhns;
        this.idGen = dhns.getIdGen();
    }

    public PreNode newNode() {
        PreNode node = new PreNode(idGen.newNodeId(), 0, 0, 0, null);
        node.setAttributes(dhns.newNodeAttributes());
        return node;
    }

    public AbstractEdge newEdge(Node source, Node target) {
        if (source == null || target == null) {
            throw new NullPointerException();
        }
        PreNode nodeSource = (PreNode) source;
        PreNode nodeTarget = (PreNode) target;
        AbstractEdge edge;
        if (source == target) {
            edge = new SelfLoopImpl(idGen.newEdgeId(), nodeSource);
        } else {
            edge = new ProperEdgeImpl(idGen.newEdgeId(), nodeSource, nodeTarget);
        }
        edge.setAttributes(dhns.newEdgeAttributes());
        return edge;
    }

    public AbstractEdge newEdge(Node source, Node target, float weight, boolean directed) {
        if (source == null || target == null) {
            throw new NullPointerException();
        }
        PreNode nodeSource = (PreNode) source;
        PreNode nodeTarget = (PreNode) target;
        AbstractEdge edge;
        if (source == target) {
            edge = new SelfLoopImpl(idGen.newEdgeId(), nodeSource);
        } else {
            edge = new MixedEdgeImpl(idGen.newEdgeId(), nodeSource, nodeTarget, directed);
        }
        if (weight != 0) {
            edge.setWeight(weight);
        }
        edge.setAttributes(dhns.newEdgeAttributes());
        return edge;
    }

    public PreNode duplicateNode(PreNode node) {
        PreNode duplicate = new PreNode(node.getId(), 0, 0, 0, node.parent);
        duplicate.setVisible(node.isVisible());
        return duplicate;
    }

    public AbstractEdge duplicateEdge(AbstractEdge edge, PreNode source, PreNode target) {
        AbstractEdge duplicate;
        if (edge.isSelfLoop()) {
            duplicate = new SelfLoopImpl(edge.getId(), source);
        } else if (edge.isMixed()) {
            duplicate = new MixedEdgeImpl(edge.getId(), source, target, edge.isDirected());
        } else {
            duplicate = new ProperEdgeImpl(edge.getId(), source, target);
        }
        duplicate.setWeight(edge.getWeight());
        duplicate.setVisible(edge.isVisible());
        return duplicate;
    }
}
