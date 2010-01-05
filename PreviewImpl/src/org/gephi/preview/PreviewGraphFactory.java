package org.gephi.preview;

import java.util.HashMap;

/**
 * Factory creating preview graphs from workspace graphs.
 *
 * @author Jérémy Subtil <jeremy.subtil@gephi.org>
 */
public class PreviewGraphFactory {

    private final HashMap<Integer, NodeImpl> nodeMap = new HashMap<Integer, NodeImpl>();

    /**
     * Creates a preview graph from the given undirected graph.
     *
     * @param sourceGraph   the undirected graph
     * @return              a generated preview graph
     */
    public GraphImpl createPreviewGraph(org.gephi.graph.api.UndirectedGraph sourceGraph) {
        // creates graph
        GraphImpl previewGraph = new GraphImpl();

        // creates nodes
        for (org.gephi.graph.api.Node sourceNode : sourceGraph.getNodes()) {
            createPreviewNode(previewGraph, sourceNode);
        }

        // creates edges
        for (org.gephi.graph.api.Edge sourceEdge : sourceGraph.getEdges()) {

            if (sourceEdge.isSelfLoop()) {
                createPreviewSelfLoop(previewGraph, sourceEdge);
                continue;
            }

            createPreviewUndirectedEdge(previewGraph, sourceEdge);
        }

        // clears the node map
        nodeMap.clear();

        return previewGraph;
    }

    /**
     * Creates a preview graph from the given directed graph.
     *
     * @param sourceGraph   the directed graph
     * @return              a generated preview graph
     */
    public GraphImpl createPreviewGraph(org.gephi.graph.api.DirectedGraph sourceGraph) {
        // creates graph
        GraphImpl previewGraph = new GraphImpl();

        // creates nodes
        for (org.gephi.graph.api.Node sourceNode : sourceGraph.getNodes()) {
            createPreviewNode(previewGraph, sourceNode);
        }

        // creates edges
        for (org.gephi.graph.api.Edge sourceEdge : sourceGraph.getEdges()) {

            if (sourceEdge.isSelfLoop()) {
                createPreviewSelfLoop(previewGraph, sourceEdge);
                continue;
            }

            if (isBidirectional(sourceGraph, sourceEdge)) {
                createPreviewBidirectionalEdge(previewGraph, sourceEdge);
            } else {
                createPreviewUnidirectionalEdge(previewGraph, sourceEdge);
            }
        }

        // clears the node map
        nodeMap.clear();

        return previewGraph;
    }

    /**
     * Creates a preview graph from the given mixed graph.
     *
     * @param sourceGraph   the mixed graph
     * @return              a generated preview graph
     */
    public GraphImpl createPreviewGraph(org.gephi.graph.api.MixedGraph sourceGraph) {
        // creates graph
        GraphImpl previewGraph = new GraphImpl();

        // creates nodes
        for (org.gephi.graph.api.Node sourceNode : sourceGraph.getNodes()) {
            createPreviewNode(previewGraph, sourceNode);
        }

        // creates edges
        for (org.gephi.graph.api.Edge sourceEdge : sourceGraph.getEdges()) {

            if (sourceEdge.isSelfLoop()) {
                createPreviewSelfLoop(previewGraph, sourceEdge);
                continue;
            }

            if (sourceEdge.isDirected()) {
                if (isBidirectional(sourceGraph, sourceEdge)) {
                    createPreviewBidirectionalEdge(previewGraph, sourceEdge);
                } else {
                    createPreviewUnidirectionalEdge(previewGraph, sourceEdge);
                }
            } else {
                createPreviewUndirectedEdge(previewGraph, sourceEdge);
            }
        }

        // clears the node map
        nodeMap.clear();

        return previewGraph;
    }

    /**
     * Creates a preview node from the given source node.
     *
     * @param previewGraph  the parent preview graph
     * @param sourceNode    the source node
     * @return              the generated preview node
     */
    private NodeImpl createPreviewNode(GraphImpl previewGraph, org.gephi.graph.api.Node sourceNode) {
        org.gephi.graph.api.NodeData sourceNodeData = sourceNode.getNodeData();

        NodeImpl previewNode = new NodeImpl(
                previewGraph,
                sourceNodeData.getLabel(),
                sourceNodeData.x(),
                -sourceNodeData.y(), // different referential from the workspace one
                sourceNodeData.getRadius(),
                sourceNodeData.r(),
                sourceNodeData.g(),
                sourceNodeData.b());

        previewGraph.addNode(previewNode);

        // adds the preview node to the node map
        nodeMap.put(sourceNode.getId(), previewNode);

        return previewNode;
    }

    /**
     * Creates a preview self-loop from the given source edge.
     *
     * @param previewGraph  the parent preview graph
     * @param sourceEdge    the source edge
     * @return              the generated preview self-loop
     */
    private SelfLoopImpl createPreviewSelfLoop(GraphImpl previewGraph, org.gephi.graph.api.Edge sourceEdge) {
        org.gephi.graph.api.EdgeData sourceEdgeData = sourceEdge.getEdgeData();

        SelfLoopImpl previewSelfLoop = new SelfLoopImpl(
                previewGraph,
                sourceEdgeData.getSize(),
                nodeMap.get(sourceEdge.getSource().getId()));

        previewGraph.addSelfLoop(previewSelfLoop);

        return previewSelfLoop;
    }

    /**
     * Creates a preview unidirectional edge from the given source edge.
     *
     * @param previewGraph  the parent preview graph
     * @param sourceEdge    the source edge
     * @return              the generated preview unidirectional edge
     */
    private UnidirectionalEdgeImpl createPreviewUnidirectionalEdge(GraphImpl previewGraph, org.gephi.graph.api.Edge sourceEdge) {
        org.gephi.graph.api.EdgeData sourceEdgeData = sourceEdge.getEdgeData();
        org.gephi.graph.api.TextData sourceEdgeTextData = sourceEdgeData.getTextData();

        UnidirectionalEdgeImpl previewEdge = new UnidirectionalEdgeImpl(
                previewGraph,
                sourceEdgeData.getSize(),
                nodeMap.get(sourceEdge.getSource().getId()),
                nodeMap.get(sourceEdge.getTarget().getId()),
                sourceEdgeData.getLabel(),
                sourceEdgeTextData.getSize());

        previewGraph.addUnidirectionalEdge(previewEdge);

        return previewEdge;
    }

    /**
     * Creates a preview bidirectional edge from the given source edge.
     *
     * @param previewGraph  the parent preview graph
     * @param sourceEdge    the source edge
     * @return              the generated preview bidirectional edge
     */
    private BidirectionalEdgeImpl createPreviewBidirectionalEdge(GraphImpl previewGraph, org.gephi.graph.api.Edge sourceEdge) {
        org.gephi.graph.api.EdgeData sourceEdgeData = sourceEdge.getEdgeData();
        org.gephi.graph.api.TextData sourceEdgeTextData = sourceEdgeData.getTextData();

        BidirectionalEdgeImpl previewEdge = new BidirectionalEdgeImpl(
                previewGraph,
                sourceEdgeData.getSize(),
                nodeMap.get(sourceEdge.getSource().getId()),
                nodeMap.get(sourceEdge.getTarget().getId()),
                sourceEdgeData.getLabel(),
                sourceEdgeTextData.getSize());

        previewGraph.addBidirectionalEdge(previewEdge);

        return previewEdge;
    }

    /**
     * Creates a preview undirected edge from the given source edge.
     *
     * @param previewGraph  the parent preview graph
     * @param sourceEdge    the source edge
     * @return              the generated preview undirected edge
     */
    private UndirectedEdgeImpl createPreviewUndirectedEdge(GraphImpl previewGraph, org.gephi.graph.api.Edge sourceEdge) {
        org.gephi.graph.api.EdgeData sourceEdgeData = sourceEdge.getEdgeData();
        org.gephi.graph.api.TextData sourceEdgeTextData = sourceEdgeData.getTextData();

        UndirectedEdgeImpl previewEdge = new UndirectedEdgeImpl(
                previewGraph,
                sourceEdgeData.getSize(),
                nodeMap.get(sourceEdge.getSource().getId()),
                nodeMap.get(sourceEdge.getTarget().getId()),
                sourceEdgeData.getLabel(),
                sourceEdgeTextData.getSize());

        previewGraph.addUndirectedEdge(previewEdge);

        return previewEdge;
    }

    /**
     * Returns whether the given source edge is bidirectional or not.
     *
     * @param sourceGraph   the directed graph
     * @param sourceEdge    the source edge
     * @return              true if the source edge is bidirectional
     */
    private boolean isBidirectional(org.gephi.graph.api.DirectedGraph sourceGraph, org.gephi.graph.api.Edge sourceEdge) {
        return sourceGraph.getEdge(sourceEdge.getTarget(), sourceEdge.getSource()) != null;
    }

    /**
     * Returns whether the given source edge is bidirectional or not.
     *
     * @param sourceGraph   the mixed graph
     * @param sourceEdge    the source edge
     * @return              true if the source edge is bidirectional
     */
    private boolean isBidirectional(org.gephi.graph.api.MixedGraph sourceGraph, org.gephi.graph.api.Edge sourceEdge) {
        return sourceGraph.getEdge(sourceEdge.getTarget(), sourceEdge.getSource()) != null;
    }
}
