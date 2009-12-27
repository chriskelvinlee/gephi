package org.gephi.preview.supervisors;

import java.awt.Font;
import java.util.HashSet;
import java.util.Set;
import org.gephi.preview.NodeImpl;
import org.gephi.preview.NodeLabelBorderImpl;
import org.gephi.preview.NodeLabelImpl;
import org.gephi.preview.api.GenericColorizer;
import org.gephi.preview.api.NodeChildColorizer;
import org.gephi.preview.api.NodeColorizer;
import org.gephi.preview.api.supervisors.NodeSupervisor;
import org.gephi.preview.updaters.CustomColorMode;
import org.gephi.preview.updaters.LabelShortener;
import org.gephi.preview.updaters.NodeOriginalColorMode;

/**
 * Node supervisor implementation.
 *
 * @author Jérémy Subtil <jeremy.subtil@gephi.org>
 */
public class NodeSupervisorImpl implements NodeSupervisor {

    private Boolean showNodes = true;
    private Float nodeBorderWidth = 1f;
    private NodeColorizer nodeColorizer = new NodeOriginalColorMode();
    private GenericColorizer nodeBorderColorizer = new CustomColorMode(0, 0, 0);
    private Boolean showNodeLabels = true;
    private Font nodeLabelfont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private Boolean shortenLabelsFlag = false;
    private Integer nodeLabelMaxChar = 10;
    private NodeChildColorizer nodeLabelColorizer = new CustomColorMode(0, 0, 0);
    private Boolean showNodeLabelBorders = true;
    private NodeChildColorizer nodeLabelBorderColorizer = new CustomColorMode(255, 255, 255);
    private final Set<NodeImpl> supervisedNodes = new HashSet<NodeImpl>();

    /**
     * Adds the given node to the list of the supervised nodes.
     *
     * It updates the node with the supervisor's values.
     *
     * @param node  the node to supervise
     */
    public void addNode(NodeImpl node) {
        supervisedNodes.add(node);

        colorNode(node);
        colorNodeLabel(node.getLabel());
        colorNodeLabelBorder(node.getLabelBorder());
        updateLabelValue(node.getLabel());
    }

    /**
     * Clears the list of supervised nodes.
     */
    public void clearSupervised() {
        supervisedNodes.clear();
    }

    /**
     * Returns true if the nodes must be displayed in the preview.
     *
     * @return true if the nodes must be displayed in the preview
     */
    public Boolean getShowNodes() {
        return showNodes;
    }

    /**
     * Defines if the nodes must be displayed in the preview.
     *
     * @param value  true to display the nodes in the preview
     */
    public void setShowNodes(Boolean value) {
        showNodes = value;
    }

    /**
     * Returns the node border width.
     *
     * @return the node border width
     */
    public Float getNodeBorderWidth() {
        return nodeBorderWidth;
    }

    /**
     * Defines the node border width.
     *
     * @param value  the node border width to set
     */
    public void setNodeBorderWidth(Float value) {
        nodeBorderWidth = value;
    }

    /**
     * Returns the node colorizer.
     *
     * @return the node colorizer
     */
    public NodeColorizer getNodeColorizer() {
        return nodeColorizer;
    }

    /**
     * Defines the node colorizer.
     *
     * @param value  the node colorizer to set
     */
    public void setNodeColorizer(NodeColorizer value) {
        nodeColorizer = value;
        colorNodes();
    }

    /**
     * Returns the node border colorizer.
     *
     * @return the node border colorizer
     */
    public GenericColorizer getNodeBorderColorizer() {
        return nodeBorderColorizer;
    }

    /**
     * Defines the node border colorizer.
     *
     * @param value  the node border colorizer to set
     */
    public void setNodeBorderColorizer(GenericColorizer value) {
        nodeBorderColorizer = value;
    }

    /**
     * Returns true if the node labels must be displayed in the preview.
     *
     * @return true if the node labels must be displayed in the preview
     */
    public Boolean getShowNodeLabels() {
        return showNodeLabels;
    }

    /**
     * Defines if the node labels must be displayed in the preview.
     *
     * @param value  true to display the node labels in the preview
     */
    public void setShowNodeLabels(Boolean value) {
        showNodeLabels = value;
    }

    /**
     * Returns the node label font.
     *
     * @return the node label font
     */
    public Font getNodeLabelFont() {
        return nodeLabelfont;
    }

    /**
     * Defines the node label font.
     *
     * @param value  the node label font to set
     */
    public void setNodeLabelFont(Font value) {
        nodeLabelfont = value;
    }

    /**
     * Returns the node label character limit.
     *
     * @return the node label character limit
     */
    public Integer getNodeLabelMaxChar() {
        return nodeLabelMaxChar;
    }

    /**
     * Defines the node label character limit.
     *
     * @param value  the node label character limit to set
     */
    public void setNodeLabelMaxChar(Integer value) {
        nodeLabelMaxChar = value;
        updateLabelValues();
    }

    /**
     * Returns whether the node labels must be shortened.
     *
     * @return true to shorten the node labels
     */
    public Boolean getShortenLabelsFlag() {
        return shortenLabelsFlag;
    }

    /**
     * Defines if the node labels must be shortened.
     *
     * @param value  true to shorten the node labels
     */
    public void setShortenLabelsFlag(Boolean value) {
        shortenLabelsFlag = value;
        updateLabelValues();
    }

    /**
     * Returns the node label colorizer.
     *
     * @return the node label colorizer
     */
    public NodeChildColorizer getNodeLabelColorizer() {
        return nodeLabelColorizer;
    }

    /**
     * Defines the node label colorizer.
     *
     * @param value  the node label colorizer to set
     */
    public void setNodeLabelColorizer(NodeChildColorizer value) {
        nodeLabelColorizer = value;
        colorNodeLabels();
    }

    /**
     * Returns true if the node label borders must be displayed in the preview.
     *
     * @return true if the node label borders must be displayed in the preview
     */
    public Boolean getShowNodeLabelBorders() {
        return showNodeLabelBorders;
    }

    /**
     * Defines if the node label borders must be displayed in the preview.
     *
     * @param value  true to display the node label borders in the preview
     */
    public void setShowNodeLabelBorders(Boolean value) {
        showNodeLabelBorders = value;
    }

    /**
     * Returns the node label border colorizer.
     *
     * @return the node label border colorizer
     */
    public NodeChildColorizer getNodeLabelBorderColorizer() {
        return nodeLabelBorderColorizer;
    }

    /**
     * Defines the node label border colorizer.
     *
     * @param value  the node label border colorizer to set
     */
    public void setNodeLabelBorderColorizer(NodeChildColorizer value) {
        nodeLabelBorderColorizer = value;
        colorNodeLabelBorders();
    }

    /**
     * Colors the given node with the node colorizer.
     *
     * @param node  the node to color
     */
    private void colorNode(NodeImpl node) {
        nodeColorizer.color(node);
    }

    /**
     * Colors the supervised nodes with the node colorizer.
     */
    private void colorNodes() {
        for (NodeImpl n : supervisedNodes) {
            colorNode(n);
        }
    }

    /**
     * Updates the node label by shortening its value or by reverting its
     * original one.
     */
    private void updateLabelValue(NodeLabelImpl nodeLabel) {
        if (shortenLabelsFlag) {
            shortenNodeLabel(nodeLabel);
        } else {
            revertLabel(nodeLabel);
        }
    }

    /**
     * Updates the node labels by shortening their values or by reverting their
     * original ones.
     */
    private void updateLabelValues() {
        if (shortenLabelsFlag) {
            shortenNodeLabels();
        } else {
            revertLabels();
        }
    }

    /**
     * Shortens the given node label.
     *
     * @param nodeLabel  the node label to shorten
     */
    private void shortenNodeLabel(NodeLabelImpl nodeLabel) {
        LabelShortener.shortenLabel(nodeLabel, nodeLabelMaxChar);
    }

    /**
     * Shortens the labels of the supervised nodes.
     */
    private void shortenNodeLabels() {
        for (NodeImpl n : supervisedNodes) {
            if (n.hasLabel()) {
                shortenNodeLabel(n.getLabel());
            }
        }
    }

    /**
     * Reverts the original value of the given node label.
     *
     * @param nodeLabel  the node label to revert the original value
     */
    private void revertLabel(NodeLabelImpl nodeLabel) {
        LabelShortener.revertLabel(nodeLabel);
    }

    /**
     * Reverts the labels of the supervised nodes.
     */
    private void revertLabels() {
        for (NodeImpl n : supervisedNodes) {
            if (n.hasLabel()) {
                revertLabel(n.getLabel());
            }
        }
    }

    /**
     * Colors the given node label with the node label colorizer.
     *
     * @param node  the node label to color
     */
    private void colorNodeLabel(NodeLabelImpl nodeLabel) {
        nodeLabelColorizer.color(nodeLabel);
    }

    /**
     * Colors the label of the supervised nodes with the node label colorizer.
     */
    private void colorNodeLabels() {
        for (NodeImpl n : supervisedNodes) {
            if (n.hasLabel()) {
                colorNodeLabel(n.getLabel());
            }
        }
    }

    /**
     * Colors the given node label border with the node label border colorizer.
     *
     * @param node  the node label border to color
     */
    private void colorNodeLabelBorder(NodeLabelBorderImpl nodeLabelBorder) {
        nodeLabelBorderColorizer.color(nodeLabelBorder);
    }

    /**
     * Colors the label border of the supervised nodes with the node label
     * border colorizer.
     */
    private void colorNodeLabelBorders() {
        for (NodeImpl n : supervisedNodes) {
            colorNodeLabelBorder(n.getLabelBorder());
        }
    }
}
