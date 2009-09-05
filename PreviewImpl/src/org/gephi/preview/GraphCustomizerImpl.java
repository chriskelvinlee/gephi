package org.gephi.preview;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.gephi.preview.api.GraphCustomizer;
import org.gephi.preview.api.color.EdgeChildColorizer;
import org.gephi.preview.api.color.EdgeColorizer;
import org.gephi.preview.api.color.GenericColorizer;
import org.gephi.preview.api.color.NodeChildColorizer;
import org.gephi.preview.api.color.NodeColorizer;
import org.gephi.preview.color.CustomColorMode;
import org.gephi.preview.color.EdgeB1ColorMode;
import org.gephi.preview.color.EdgeBothBColorMode;
import org.gephi.preview.color.NodeOriginalColorMode;
import org.gephi.preview.color.ParentEdgeColorMode;

/**
 *
 * @author jeremy
 */
public class GraphCustomizerImpl implements GraphCustomizer {

    //TODO set some parameters as static class fields

    private Boolean showNodes = true;
    private Float nodeBorderWidth = 1f;
    private NodeColorizer nodeColor = new NodeOriginalColorMode();
    private GenericColorizer nodeBorderColor = new CustomColorMode(0, 0, 0);
    private Boolean showNodeLabels = true;
    private Font nodeLabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private Integer nodeLabelMaxChar = 10;
    private NodeChildColorizer nodeLabelColor = new CustomColorMode(0, 0, 0);
    private Boolean showNodeLabelBorders = true;
    private NodeChildColorizer nodeLabelBorderColor = new CustomColorMode(255, 255, 255);
    private Boolean showEdges = true;
    private Boolean curvedUniEdges = false;
    private Boolean curvedBiEdges = false;
    private EdgeColorizer uniEdgeColor = new EdgeB1ColorMode();
    private EdgeColorizer biEdgeColor = new EdgeBothBColorMode();
    private Boolean showSelfLoops = true;
    private EdgeColorizer selfLoopColor = new CustomColorMode(0, 0, 0);
    private Boolean showUniEdgeLabels = true;
    private Integer uniEdgeLabelMaxChar = 10;
    private Font uniEdgeLabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private EdgeChildColorizer uniEdgeLabelColor = new ParentEdgeColorMode();
    private Boolean showUniEdgeMiniLabels = true;
    private Float uniEdgeMiniLabelAddedRadius = 15f;
    private Integer uniEdgeMiniLabelMaxChar = 10;
    private Font uniEdgeMiniLabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 8);
    private EdgeChildColorizer uniEdgeMiniLabelColor = new ParentEdgeColorMode();
    private Boolean showUniEdgeArrows = true;
    private Float uniEdgeArrowAddedRadius = 65f;
    private Float uniEdgeArrowSize = 20f;
    private EdgeChildColorizer uniEdgeArrowColor = new ParentEdgeColorMode();
    private Boolean showBiEdgeLabels = true;
    private Integer biEdgeLabelMaxChar = 10;
    private Font biEdgeLabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private EdgeChildColorizer biEdgeLabelColor = new ParentEdgeColorMode();
    private Boolean showBiEdgeMiniLabels = true;
    private Float biEdgeMiniLabelAddedRadius = 15f;
    private Integer biEdgeMiniLabelMaxChar = 10;
    private Font biEdgeMiniLabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 8);
    private EdgeChildColorizer biEdgeMiniLabelColor = new ParentEdgeColorMode();
    private Boolean showBiEdgeArrows = true;
    private Float biEdgeArrowAddedRadius = 65f;
    private Float biEdgeArrowSize = 20f;
    private EdgeChildColorizer biEdgeArrowColor = new ParentEdgeColorMode();

    private List<PropertyChangeListener> listeners = Collections.synchronizedList(new LinkedList());

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.add(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        listeners.remove(pcl);
    }

    private void fire(String propertyName, Object old, Object nue) {
        for (PropertyChangeListener pcls : listeners)
            pcls.propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
    }

    public Boolean getShowNodes() {
        return showNodes;
    }

    public Float getNodeBorderWidth() {
        return nodeBorderWidth;
    }

    public NodeColorizer getNodeColorizer() {
        return nodeColor;
    }

    public GenericColorizer getNodeBorderColorizer() {
        return nodeBorderColor;
    }

    public Boolean getShowNodeLabels() {
        return showNodeLabels;
    }

    public Font getNodeLabelFont() {
        return nodeLabelFont;
    }

    public Integer getNodeLabelMaxChar() {
        return nodeLabelMaxChar;
    }

    public NodeChildColorizer getNodeLabelColorizer() {
        return nodeLabelColor;
    }

    public Boolean getShowNodeLabelBorders() {
        return showNodeLabelBorders;
    }

    public NodeChildColorizer getNodeLabelBorderColorizer() {
        return nodeLabelBorderColor;
    }

    public Boolean getShowEdges() {
        return showEdges;
    }

    public Boolean getCurvedUniEdges() {
        return curvedUniEdges;
    }

    public Boolean getCurvedBiEdges() {
        return curvedBiEdges;
    }

    public EdgeColorizer getUniEdgeColorizer() {
        return uniEdgeColor;
    }

    public EdgeColorizer getBiEdgeColorizer() {
        return biEdgeColor;
    }

    public Boolean getShowSelfLoops() {
        return showSelfLoops;
    }

    public EdgeColorizer getSelfLoopColorizer() {
        return selfLoopColor;
    }

    public Boolean getShowUniEdgeLabels() {
        return showUniEdgeLabels;
    }

    public Integer getUniEdgeLabelMaxChar() {
        return uniEdgeLabelMaxChar;
    }

    public Font getUniEdgeLabelFont() {
        return uniEdgeLabelFont;
    }

    public EdgeChildColorizer getUniEdgeLabelColorizer() {
        return uniEdgeLabelColor;
    }

    public Boolean getShowUniEdgeMiniLabels() {
        return showUniEdgeMiniLabels;
    }

    public Float getUniEdgeMiniLabelAddedRadius() {
        return uniEdgeMiniLabelAddedRadius;
    }

    public Integer getUniEdgeMiniLabelMaxChar() {
        return uniEdgeMiniLabelMaxChar;
    }

    public Font getUniEdgeMiniLabelFont() {
        return uniEdgeMiniLabelFont;
    }

    public EdgeChildColorizer getUniEdgeMiniLabelColorizer() {
        return uniEdgeMiniLabelColor;
    }

    public Boolean getShowUniEdgeArrows() {
        return showUniEdgeArrows;
    }

    public Float getUniEdgeArrowAddedRadius() {
        return uniEdgeArrowAddedRadius;
    }

    public Float getUniEdgeArrowSize() {
        return uniEdgeArrowSize;
    }

    public EdgeChildColorizer getUniEdgeArrowColorizer() {
        return uniEdgeArrowColor;
    }

    public Boolean getShowBiEdgeLabels() {
        return showBiEdgeLabels;
    }

    public Integer getBiEdgeLabelMaxChar() {
        return biEdgeLabelMaxChar;
    }

    public Font getBiEdgeLabelFont() {
        return biEdgeLabelFont;
    }

    public EdgeChildColorizer getBiEdgeLabelColorizer() {
        return biEdgeLabelColor;
    }

    public Boolean getShowBiEdgeMiniLabels() {
        return showBiEdgeMiniLabels;
    }

    public Float getBiEdgeMiniLabelAddedRadius() {
        return biEdgeMiniLabelAddedRadius;
    }

    public Integer getBiEdgeMiniLabelMaxChar() {
        return biEdgeMiniLabelMaxChar;
    }

    public Font getBiEdgeMiniLabelFont() {
        return biEdgeMiniLabelFont;
    }

    public EdgeChildColorizer getBiEdgeMiniLabelColorizer() {
        return biEdgeMiniLabelColor;
    }

    public Boolean getShowBiEdgeArrows() {
        return showBiEdgeArrows;
    }

    public Float getBiEdgeArrowAddedRadius() {
        return biEdgeArrowAddedRadius;
    }

    public Float getBiEdgeArrowSize() {
        return biEdgeArrowSize;
    }

    public EdgeChildColorizer getBiEdgeArrowColorizer() {
        return biEdgeArrowColor;
    }

    public void setShowNodes(Boolean value) {
        Boolean old = showNodes;
        showNodes = value;
        fire("showNodes", old, showNodes);
    }

    public void setNodeBorderWidth(Float value) {
        Float old = nodeBorderWidth;
        nodeBorderWidth = value;
        fire("nodeBorderWidth", old, nodeBorderWidth);
    }

    public void setNodeColorizer(NodeColorizer value) {
        NodeColorizer old = nodeColor;
        nodeColor = value;
        fire("nodeColor", old, nodeColor);
    }

    public void setNodeBorderColorizer(GenericColorizer value) {
        GenericColorizer old = nodeBorderColor;
        nodeBorderColor = value;
        fire("nodeBorderColor", old, nodeBorderColor);
    }

    public void setShowNodeLabels(Boolean value) {
        Boolean old = showNodeLabels;
        showNodeLabels = value;
        fire("showNodeLabels", old, showNodeLabels);
    }

    public void setNodeLabelFont(Font value) {
        Font old = nodeLabelFont;
        nodeLabelFont = value;
        fire("nodeLabelFont", old, nodeLabelFont);
    }

    public void setNodeLabelMaxChar(Integer value) {
        Integer old = nodeLabelMaxChar;
        nodeLabelMaxChar = value;
        fire("nodeLabelMaxChar", old, nodeLabelMaxChar);
    }

    public void setNodeLabelColorizer(NodeChildColorizer value) {
        NodeChildColorizer old = nodeLabelColor;
        nodeLabelColor = value;
        fire("nodeLabelColor", old, nodeLabelColor);
    }

    public void setShowNodeLabelBorders(Boolean value) {
        Boolean old = showNodeLabelBorders;
        showNodeLabelBorders = value;
        fire("showNodeLabelBorders", old, showNodeLabelBorders);
    }

    public void setNodeLabelBorderColorizer(NodeChildColorizer value) {
        NodeChildColorizer old = nodeLabelBorderColor;
        nodeLabelBorderColor = value;
        fire("nodeLabelBorderColor", old, nodeLabelBorderColor);
    }

    public void setShowEdges(Boolean value) {
        Boolean old = showEdges;
        showEdges = value;
        fire("showEdges", old, showEdges);
    }

    public void setCurvedUniEdges(Boolean value) {
        Boolean old = curvedUniEdges;
        curvedUniEdges = value;
        fire("curvedUniEdges", old, curvedUniEdges);
    }

    public void setCurvedBiEdges(Boolean value) {
        Boolean old = curvedBiEdges;
        curvedBiEdges = value;
        fire("curvedBiEdges", old, curvedBiEdges);
    }

    public void setUniEdgeColorizer(EdgeColorizer value) {
        EdgeColorizer old = uniEdgeColor;
        uniEdgeColor = value;
        fire("uniEdgeColor", old, uniEdgeColor);
    }

    public void setBiEdgeColorizer(EdgeColorizer value) {
        EdgeColorizer old = biEdgeColor;
        biEdgeColor = value;
        fire("biEdgeColor", old, biEdgeColor);
    }

    public void setShowSelfLoops(Boolean value) {
        Boolean old = showSelfLoops;
        showSelfLoops = value;
        fire("showSelfLoops", old, showSelfLoops);
    }

    public void setSelfLoopColorizer(EdgeColorizer value) {
        EdgeColorizer old = selfLoopColor;
        selfLoopColor = value;
        fire("selfLoopColor", old, selfLoopColor);
    }

    public void setShowUniEdgeLabels(Boolean value) {
        Boolean old = showUniEdgeLabels;
        showUniEdgeLabels = value;
        fire("showUniEdgeLabels", old, showUniEdgeLabels);
    }

    public void setUniEdgeLabelMaxChar(Integer value) {
        Integer old = uniEdgeLabelMaxChar;
        uniEdgeLabelMaxChar = value;
        fire("uniEdgeLabelMaxChar", old, uniEdgeLabelMaxChar);
    }

    public void setUniEdgeLabelFont(Font value) {
        Font old = uniEdgeLabelFont;
        uniEdgeLabelFont = value;
        fire("uniEdgeLabelFont", old, uniEdgeLabelFont);
    }

    public void setUniEdgeLabelColorizer(EdgeChildColorizer value) {
        EdgeChildColorizer old = uniEdgeLabelColor;
        uniEdgeLabelColor = value;
        fire("uniEdgeLabelColor", old, uniEdgeLabelColor);
    }

    public void setShowUniEdgeMiniLabels(Boolean value) {
        Boolean old = showUniEdgeMiniLabels;
        showUniEdgeMiniLabels = value;
        fire("showUniEdgeMiniLabels", old, showUniEdgeMiniLabels);
    }

    public void setUniEdgeMiniLabelAddedRadius(Float value) {
        Float old = uniEdgeMiniLabelAddedRadius;
        uniEdgeMiniLabelAddedRadius = value;
        fire("uniEdgeMiniLabelAddedRadius", old, uniEdgeMiniLabelAddedRadius);
    }

    public void setUniEdgeMiniLabelMaxChar(Integer value) {
        Integer old = uniEdgeMiniLabelMaxChar;
        uniEdgeMiniLabelMaxChar = value;
        fire("uniEdgeMiniLabelMaxChar", old, uniEdgeMiniLabelMaxChar);
    }

    public void setUniEdgeMiniLabelFont(Font value) {
        Font old = uniEdgeMiniLabelFont;
        uniEdgeMiniLabelFont = value;
        fire("uniEdgeMiniLabelFont", old, uniEdgeMiniLabelFont);
    }

    public void setUniEdgeMiniLabelColorizer(EdgeChildColorizer value) {
        EdgeChildColorizer old = uniEdgeMiniLabelColor;
        uniEdgeMiniLabelColor = value;
        fire("uniEdgeMiniLabelColor", old, uniEdgeMiniLabelColor);
    }

    public void setShowUniEdgeArrows(Boolean value) {
        Boolean old = showUniEdgeArrows;
        showUniEdgeArrows = value;
        fire("showUniEdgeArrows", old, showUniEdgeArrows);
    }

    public void setUniEdgeArrowAddedRadius(Float value) {
        Float old = uniEdgeArrowAddedRadius;
        uniEdgeArrowAddedRadius = value;
        fire("uniEdgeArrowAddedRadius", old, uniEdgeArrowAddedRadius);
    }

    public void setUniEdgeArrowSize(Float value) {
        Float old = uniEdgeArrowSize;
        uniEdgeArrowSize = value;
        fire("uniEdgeArrowSize", old, uniEdgeArrowSize);
    }

    public void setUniEdgeArrowColorizer(EdgeChildColorizer value) {
        EdgeChildColorizer old = uniEdgeArrowColor;
        uniEdgeArrowColor = value;
        fire("uniEdgeArrowColor", old, uniEdgeArrowColor);
    }

    public void setShowBiEdgeLabels(Boolean value) {
        Boolean old = showBiEdgeLabels;
        showBiEdgeLabels = value;
        fire("showBiEdgeLabels", old, showBiEdgeLabels);
    }

    public void setBiEdgeLabelMaxChar(Integer value) {
        Integer old = biEdgeLabelMaxChar;
        biEdgeLabelMaxChar = value;
        fire("biEdgeLabelMaxChar", old, biEdgeLabelMaxChar);
    }

    public void setBiEdgeLabelFont(Font value) {
        Font old = biEdgeLabelFont;
        biEdgeLabelFont = value;
        fire("biEdgeLabelFont", old, biEdgeLabelFont);
    }

    public void setBiEdgeLabelColorizer(EdgeChildColorizer value) {
        EdgeChildColorizer old = biEdgeLabelColor;
        biEdgeLabelColor = value;
        fire("biEdgeLabelColor", old, biEdgeLabelColor);
    }

    public void setShowBiEdgeMiniLabels(Boolean value) {
        Boolean old = showBiEdgeMiniLabels;
        showBiEdgeMiniLabels = value;
        fire("showBiEdgeMiniLabels", old, showBiEdgeMiniLabels);
    }

    public void setBiEdgeMiniLabelAddedRadius(Float value) {
        Float old = biEdgeMiniLabelAddedRadius;
        biEdgeMiniLabelAddedRadius = value;
        fire("biEdgeMiniLabelAddedRadius", old, biEdgeMiniLabelAddedRadius);
    }

    public void setBiEdgeMiniLabelMaxChar(Integer value) {
        Integer old = biEdgeMiniLabelMaxChar;
        biEdgeMiniLabelMaxChar = value;
        fire("biEdgeMiniLabelMaxChar", old, biEdgeMiniLabelMaxChar);
    }

    public void setBiEdgeMiniLabelFont(Font value) {
        Font old = biEdgeMiniLabelFont;
        biEdgeMiniLabelFont = value;
        fire("biEdgeMiniLabelFont", old, biEdgeMiniLabelFont);
    }

    public void setBiEdgeMiniLabelColorizer(EdgeChildColorizer value) {
        EdgeChildColorizer old = biEdgeMiniLabelColor;
        biEdgeMiniLabelColor = value;
        fire("biEdgeMiniLabelColor", old, biEdgeMiniLabelColor);
    }

    public void setShowBiEdgeArrows(Boolean value) {
        Boolean old = showBiEdgeArrows;
        showBiEdgeArrows = value;
        fire("showBiEdgeArrows", old, showBiEdgeArrows);
    }

    public void setBiEdgeArrowAddedRadius(Float value) {
        Float old = biEdgeArrowAddedRadius;
        biEdgeArrowAddedRadius = value;
        fire("biEdgeArrowAddedRadius", old, biEdgeArrowAddedRadius);
    }

    public void setBiEdgeArrowSize(Float value) {
        Float old = biEdgeArrowSize;
        biEdgeArrowSize = value;
        fire("biEdgeArrowSize", old, biEdgeArrowSize);
    }

    public void setBiEdgeArrowColorizer(EdgeChildColorizer value) {
        EdgeChildColorizer old = biEdgeArrowColor;
        biEdgeArrowColor = value;
        fire("biEdgeArrowColor", old, biEdgeArrowColor);
    }
}
