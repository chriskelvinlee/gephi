/*
Copyright 2008-2009 Gephi
Authors : Helder Suzuki <heldersuzuki@gephi.org>
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
package org.gephi.layout.force.yifanHu;

import java.util.List;
import org.gephi.graph.api.GraphController;
import org.gephi.layout.GraphUtils;
import org.gephi.layout.api.Layout;
import org.gephi.layout.api.LayoutBuilder;
import org.gephi.layout.api.LayoutProperty;
import org.gephi.layout.force.SimpleForceLayout;

/**
 * Hu's basic algorithm
 * @author Helder Suzuki <heldersuzuki@gephi.org>
 */
public class YifanHuLayout extends SimpleForceLayout implements Layout {

    public float optimalDistance;
    public float relativeStrength;
    private float step;
    private int progress;
    public float stepRatio;
    private boolean converged;

    public YifanHuLayout(LayoutBuilder layoutBuilder) {
        super(layoutBuilder);
    }

//    protected Displacement getDisplacement() {
//        return new StepDisplacement(step);
//    }
//
//    protected AbstractForce getNodeForce() {
//        return new ElectricalForce(relativeStrength, optimalDistance);
//    }
//
//    protected AbstractForce getEdgeForce() {
//        return new SpringForce(optimalDistance);
//    }
    protected void postAlgo() {
        updateStep();
        if (Math.abs((energy - energy0) / energy) < 1e-3) {
            converged = true;
        }
    }

    private void updateStep() {
        System.out.println("energy: " + energy);
        if (energy < energy0) {
            progress++;
            if (progress >= 5) {
                progress = 0;
                step /= stepRatio;
            }
        } else {
            progress = 0;
            step *= stepRatio;
        }
    }

    public List<LayoutProperty> getProperties() {
//        LayoutProperty[] properties = new LayoutProperty[3];
//        properties[0] = LayoutProperty.createProperty(YifanHuLayout.class, "optimalDistance");
//        properties[1] = LayoutProperty.createProperty(YifanHuLayout.class, "relativeStrength");
//        properties[2] = LayoutProperty.createProperty(YifanHuLayout.class, "stepRatio");
//        return properties;
        return null;
    }

    public void resetPropertiesValues() {
        stepRatio = (float) 0.9;
        progress = 0;
        converged = false;
        relativeStrength = (float) 0.2;
        optimalDistance = (float) (Math.pow(relativeStrength, 1.0 / 3) * GraphUtils.getAverageEdge(graph));
        step = optimalDistance / 10000;
    }

    protected boolean hasConverged() {
        return converged;
    }

    protected int getQuadTreeMaxLevel() {
        return 10;
    }
}
