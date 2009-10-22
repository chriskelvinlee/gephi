package org.gephi.preview.controller;

import java.awt.Font;
import org.gephi.graph.api.GraphController;
import org.gephi.preview.GraphImpl;
import org.gephi.preview.api.Graph;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.color.colorizer.GenericColorizer;
import org.gephi.preview.api.color.colorizer.NodeChildColorizer;
import org.gephi.preview.api.color.colorizer.NodeColorizer;
import org.gephi.preview.api.supervisor.SelfLoopSupervisor;
import org.gephi.preview.supervisor.GraphSupervisor;
import org.gephi.preview.supervisor.SelfLoopSupervisorImpl;
import org.openide.util.Lookup;

/**
 *
 * @author jeremy
 */
public class PreviewControllerImpl implements PreviewController {

    private GraphImpl previewGraph = null;
    protected final GraphSupervisor gs = new GraphSupervisor();
	private final SelfLoopSupervisorImpl selfLoopSupervisor = new SelfLoopSupervisorImpl();
	private final PreviewGraphFactory factory = new PreviewGraphFactory();

    public Graph getGraph() {
		if (null == previewGraph) {
			buildGraph();
		}

        return previewGraph;
    }

	private void buildGraph() {
		GraphController gc = Lookup.getDefault().lookup(GraphController.class);

		org.gephi.graph.api.Graph sourceGraph = null;

		if (gc.getDirectedGraph().isDirected()) {
			sourceGraph = gc.getDirectedGraph();
		}
		else if (gc.getDirectedGraph().isUndirected()) {
			sourceGraph = gc.getUndirectedGraph();
		}
		else if (gc.getDirectedGraph().isMixed()) {
			sourceGraph = gc.getMixedGraph();
		}

		previewGraph = factory.createPreviewGraph(sourceGraph, gs);
	}

	/**
	 * Returns the self-loop supervisor.
	 *
	 * @return the controller's self-loop supervisor
	 */
	public SelfLoopSupervisor getSelfLoopSupervisor() {
		return selfLoopSupervisor;
	}

    public Boolean getShowNodes() {
        return gs.getNodeSupervisor().getShowNodes();
    }

    public void setShowNodes(Boolean value) {
        gs.getNodeSupervisor().setShowNodes(value);
    }

    public Float getNodeBorderWidth() {
        return gs.getNodeSupervisor().getNodeBorderWidth();
    }

    public void setNodeBorderWidth(Float value) {
        gs.getNodeSupervisor().setNodeBorderWidth(value);
    }

    public NodeColorizer getNodeColorizer() {
        return gs.getNodeSupervisor().getNodeColorizer();
    }

    public void setNodeColorizer(NodeColorizer value) {
        gs.getNodeSupervisor().setNodeColorizer(value);
    }

    public GenericColorizer getNodeBorderColorizer() {
        return gs.getNodeSupervisor().getNodeBorderColorizer();
    }

    public void setNodeBorderColorizer(GenericColorizer value) {
        gs.getNodeSupervisor().setNodeBorderColorizer(value);
    }

    public Boolean getShowNodeLabels() {
        return gs.getNodeLabelSupervisor().getShowNodeLabels();
    }

    public void setShowNodeLabels(Boolean value) {
        gs.getNodeLabelSupervisor().setShowNodeLabels(value);
    }

    public Font getNodeLabelFont() {
        return gs.getNodeLabelSupervisor().getNodeLabelFont();
    }

    public void setNodeLabelFont(Font value) {
        gs.getNodeLabelSupervisor().setNodeLabelFont(value);
    }

    public Integer getNodeLabelMaxChar() {
        return gs.getNodeLabelSupervisor().getNodeLabelMaxChar();
    }

    public void setNodeLabelMaxChar(Integer value) {
        gs.getNodeLabelSupervisor().setNodeLabelMaxChar(value);
    }

    public NodeChildColorizer getNodeLabelColorizer() {
        return gs.getNodeLabelSupervisor().getNodeLabelColorizer();
    }

    public void setNodeLabelColorizer(NodeChildColorizer value) {
        gs.getNodeLabelSupervisor().setNodeLabelColorizer(value);
    }

    public Boolean getShowNodeLabelBorders() {
        return gs.getNodeLabelBorderSupervisor().getShowNodeLabelBorders();
    }

    public void setShowNodeLabelBorders(Boolean value) {
        gs.getNodeLabelBorderSupervisor().setShowNodeLabelBorders(value);
    }

    public NodeChildColorizer getNodeLabelBorderColorizer() {
        return gs.getNodeLabelBorderSupervisor().getNodeLabelBorderColorizer();
    }

    public void setNodeLabelBorderColorizer(NodeChildColorizer value) {
        gs.getNodeLabelBorderSupervisor().setNodeLabelBorderColorizer(value);
    }
}
