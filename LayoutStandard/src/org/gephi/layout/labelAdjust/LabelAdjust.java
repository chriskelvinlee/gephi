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
package org.gephi.layout.labelAdjust;

import java.util.ArrayList;
import java.util.List;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.layout.AbstractLayout;
import org.gephi.layout.ForceVectorNodeLayoutData;
import org.gephi.layout.ForceVectorUtils;
import org.gephi.layout.api.Layout;
import org.gephi.layout.api.LayoutBuilder;
import org.gephi.layout.api.LayoutProperty;
import org.openide.nodes.Node.PropertySet;

/**
 *
 * @author Mathieu Bastian
 */
public class LabelAdjust extends AbstractLayout implements Layout {

    //Graph
    protected Graph graph;
    private double repulsionStrength;
    private double speed;

    public LabelAdjust(LayoutBuilder layoutBuilder) {
        super(layoutBuilder);
    }

    public void initAlgo() {
    }

    @Override
    public void setGraphController(GraphController graphController) {
        super.setGraphController(graphController);
        this.graph = graphController.getModel().getGraphVisible();
    }

    public void goAlgo() {
        boolean somethingMoved = false;

        Node[] nodes = graph.getNodes().toArray();
        for (Node n : nodes) {
            ForceVectorNodeLayoutData layoutData = n.getNodeData().getLayoutData();
            if (layoutData == null) {
                layoutData = new ForceVectorNodeLayoutData();
                n.getNodeData().setLayoutData(layoutData);
            }
            layoutData.dx = 0;
            layoutData.dy = 0;
        }

        // repulsion
        for (Node n1 : nodes) {
            for (Node n2 : nodes) {
                if (n1 != n2) {
                    double xDist = Math.abs(n1.getNodeData().x() - n2.getNodeData().x());
                    double label_occupied_width = 0.5 * (n1.getNodeData().getTextData().getWidth() + n2.getNodeData().getTextData().getWidth());
                    double yDist = Math.abs(n1.getNodeData().y() - n2.getNodeData().y());
                    double label_occupied_height = 0.5 * ((n1.getNodeData().getTextData().getHeight()) + n2.getNodeData().getTextData().getHeight());
                    boolean collision = (xDist < label_occupied_width && yDist < label_occupied_height);
                    if (collision) {
                        ForceVectorUtils.fcBiRepulsor_y(n1.getNodeData(), n2.getNodeData(), (0.8 + 0.4 * Math.random()) * repulsionStrength, 0.005 * label_occupied_width);
                        somethingMoved = true;
                    }
                }
            }
        }

        // apply forces
        for (Node n : nodes) {
            ForceVectorNodeLayoutData layoutData = n.getNodeData().getLayoutData();
            if (!n.getNodeData().isFixed()) {
                layoutData.dx *= speed;
                layoutData.dy *= speed;
                float x = n.getNodeData().x() + layoutData.dx;
                float y = n.getNodeData().y() + layoutData.dy;

                n.getNodeData().setX(x);
                n.getNodeData().setY(y);
            }
        }
    }

    public void endAlgo() {
    }

    public LayoutProperty[] getProperties() {
        List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
        final String LABELADJUST_CATEGORY = "LabelAdjust";
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Double.class, "repulsionStrength", LABELADJUST_CATEGORY, "repulsionStrength",
                    "getRepulsionStrength", "setRepulsionStrength"));
            properties.add(LayoutProperty.createProperty(
                    this, Double.class, "speed", LABELADJUST_CATEGORY, "speed", "getSpeed", "setSpeed"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.toArray(new LayoutProperty[0]);
    }

    public void resetPropertiesValues() {
        setSpeed(1.);
        setRepulsionStrength(150.);
    }

    public Double getRepulsionStrength() {
        return repulsionStrength;
    }

    public void setRepulsionStrength(Double repulsionStrength) {
        this.repulsionStrength = repulsionStrength;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
