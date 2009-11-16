package org.gephi.preview.controller;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.preview.GraphImpl;
import org.gephi.preview.api.Graph;
import org.gephi.preview.api.controller.PreviewController;
import org.gephi.preview.api.supervisor.EdgeSupervisor;
import org.gephi.preview.api.supervisor.GlobalEdgeSupervisor;
import org.gephi.preview.api.supervisor.NodeSupervisor;
import org.gephi.preview.api.supervisor.SelfLoopSupervisor;
import org.gephi.preview.supervisor.BidirectionalEdgeSupervisorImpl;
import org.gephi.preview.supervisor.EdgeSupervisorImpl;
import org.gephi.preview.supervisor.GlobalEdgeSupervisorImpl;
import org.gephi.preview.supervisor.NodeSupervisorImpl;
import org.gephi.preview.supervisor.SelfLoopSupervisorImpl;
import org.gephi.preview.supervisor.UnidirectionalEdgeSupervisorImpl;
import org.openide.util.Lookup;

/**
 * Implementation of the preview controller.
 *
 * @author Jérémy Subtil <jeremy.subtil@gephi.org>
 */
public class PreviewControllerImpl implements PreviewController {

    private GraphImpl previewGraph = null;
    private final NodeSupervisorImpl nodeSupervisor = new NodeSupervisorImpl();
    private final GlobalEdgeSupervisorImpl globalEdgeSupervisor = new GlobalEdgeSupervisorImpl();
    private final SelfLoopSupervisorImpl selfLoopSupervisor = new SelfLoopSupervisorImpl();
    private final EdgeSupervisorImpl uniEdgeSupervisor = new UnidirectionalEdgeSupervisorImpl();
    private final EdgeSupervisorImpl biEdgeSupervisor = new BidirectionalEdgeSupervisorImpl();
    private final PreviewGraphFactory factory = new PreviewGraphFactory();

    /**
     * Returns the current preview graph.
     *
     * @return the current preview graph
     */
    public Graph getGraph() {
        return previewGraph;
    }

    /**
     * Retreives the workspace graph and builds a preview graph from it.
     */
    public void buildGraph() {
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

        if (graphModel.isUndirected()) {
            previewGraph = factory.createPreviewGraph(graphModel.getUndirectedGraph());
        }
        else if (graphModel.isDirected()) {
            previewGraph = factory.createPreviewGraph(graphModel.getDirectedGraph());
        }
        else if (graphModel.isMixed()) {
            previewGraph = factory.createPreviewGraph(graphModel.getMixedGraph());
        }
    }

    /**
     * Returns the node supervisor.
     *
     * @return the controller's node supervisor
     */
    public NodeSupervisor getNodeSupervisor() {
        return nodeSupervisor;
    }

    /**
     * Returns the global edge supervisor.
     *
     * @return the controller's global edge supervisor
     */
    public GlobalEdgeSupervisor getGlobalEdgeSupervisor() {
        return globalEdgeSupervisor;
    }

    /**
     * Returns the self-loop supervisor.
     *
     * @return the controller's self-loop supervisor
     */
    public SelfLoopSupervisor getSelfLoopSupervisor() {
        return selfLoopSupervisor;
    }

    /**
     * Returns the unidirectional edge supervisor.
     *
     * @return the controller's unidirectional edge supervisor
     */
    public EdgeSupervisor getUniEdgeSupervisor() {
        return uniEdgeSupervisor;
    }

    /**
     * Returns the bidirectional edge supervisor.
     *
     * @return the controller's bidirectional edge supervisor
     */
    public EdgeSupervisor getBiEdgeSupervisor() {
        return biEdgeSupervisor;
    }
}
