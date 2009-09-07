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
package org.gephi.visualization.component;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class VizBarController {

    private VizToolbarGroup[] groups;

    public VizBarController() {
        createDefaultGroups();
    }

    private void createDefaultGroups() {
        groups = new VizToolbarGroup[3];

        groups[0] = new NodeGroupBar();
        groups[1] = new EdgeGroupBar();
        groups[2] = new LabelGroupBar();
    }

    public VizToolbar getToolbar() {
        VizToolbar toolbar = new VizToolbar(groups);
        return toolbar;
    }

    public VizExtendedBar getExtendedBar() {
        VizExtendedBar extendedBar = new VizExtendedBar();
        return extendedBar;
    }

    private static class NodeGroupBar implements VizToolbarGroup {

        public String getName() {
            return "Nodes";
        }

        public JComponent[] getToolbarComponents() {
            JComponent[] components = new JComponent[1];

            //Show nodes buttons
            JButton showNodeButton = new JButton();
            showNodeButton.setToolTipText(NbBundle.getMessage(VizBarController.class, "VizToolbar.Nodes.showNodes"));
            showNodeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/showNodes.png")));
            components[0] = showNodeButton;

            return components;
        }

        public JComponent getExtendedComponent() {
            return new JPanel();
        }

        public boolean hasToolbar() {
            return true;
        }

        public boolean hasExtended() {
            return true;
        }
    }

    private static class EdgeGroupBar implements VizToolbarGroup {

        public String getName() {
            return "Edges";
        }

        public JComponent[] getToolbarComponents() {
            JComponent[] components = new JComponent[1];

            //Show edges buttons
            JButton showEdgeButton = new JButton();
            showEdgeButton.setToolTipText(NbBundle.getMessage(VizBarController.class, "VizToolbar.Edges.showEdges"));
            showEdgeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/showEdges.png")));
            components[0] = showEdgeButton;

            return components;
        }

        public JComponent getExtendedComponent() {
            return new JPanel();
        }

        public boolean hasToolbar() {
            return true;
        }

        public boolean hasExtended() {
            return true;
        }
    }

    private static class LabelGroupBar implements VizToolbarGroup {

        public String getName() {
            return "Labels";
        }

        public JComponent[] getToolbarComponents() {
            JComponent[] components = new JComponent[2];

            //Show node labels buttons
            JButton showNodeLabelsButton = new JButton();
            showNodeLabelsButton.setToolTipText(NbBundle.getMessage(VizBarController.class, "VizToolbar.Labels.showNodeLabels"));
            showNodeLabelsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/showNodeLabels.png")));
            components[0] = showNodeLabelsButton;

            //Show edge labels buttons
            JButton showEdgeLabelsButton = new JButton();
            showEdgeLabelsButton.setToolTipText(NbBundle.getMessage(VizBarController.class, "VizToolbar.Labels.showEdgeLabels"));
            showEdgeLabelsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/showEdgeLabels.png")));
            components[1] = showEdgeLabelsButton;


            return components;
        }

        public JComponent getExtendedComponent() {
            return new JPanel();
        }

        public boolean hasToolbar() {
            return true;
        }

        public boolean hasExtended() {
            return true;
        }
    }
}
