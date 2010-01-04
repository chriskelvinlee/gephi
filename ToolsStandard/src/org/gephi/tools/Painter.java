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
package org.gephi.tools;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.gephi.graph.api.Node;
import org.gephi.tools.spi.NodeClickEventListener;
import org.gephi.tools.spi.Tool;
import org.gephi.tools.spi.ToolEventListener;
import org.gephi.tools.spi.ToolEventType;
import org.gephi.tools.spi.ToolSelectionType;
import org.gephi.tools.spi.ToolUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = Tool.class)
public class Painter implements Tool {

    private ToolEventListener[] listeners;

    public void select() {
    }

    public void unselect() {
        listeners = null;
    }

    public ToolEventListener[] getListeners() {
        listeners = new ToolEventListener[1];
        listeners[0] = new NodeClickEventListener() {

            public ToolEventType getType() {
                return ToolEventType.NODE_CLICKED;
            }

            public void clickNodes(Node[] nodes) {
                System.out.println("-------Nodes clicked");
                for (int i = 0; i < nodes.length; i++) {
                    System.out.println(nodes[i].getNodeData().getLabel());
                }
            }
        };
        return listeners;
    }

    public ToolUI getUI() {
        return new ToolUI() {

            public JPanel getPropertiesBar(Tool tool) {
                JLabel lbl = new JLabel("painter");
                JPanel pnl = new JPanel();
                pnl.add(lbl);
                return pnl;
            }

            public String getName() {
                return NbBundle.getMessage(Painter.class, "Painter.name");
            }

            public Icon getIcon() {
                return new ImageIcon(getClass().getResource("/org/gephi/tools/resources/painter.png"));
            }

            public String getDescription() {
                return NbBundle.getMessage(Painter.class, "Painter.description");
            }

            public int getPosition() {
                return 100;
            }
        };
    }

    public ToolSelectionType getSelectionType() {
        return ToolSelectionType.SELECTION_AND_DRAGGING;
    }
}
