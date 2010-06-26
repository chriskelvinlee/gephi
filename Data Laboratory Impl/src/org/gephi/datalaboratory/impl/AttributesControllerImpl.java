/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.datalaboratory.impl;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.api.AttributeValue;
import org.gephi.data.properties.PropertiesColumn;
import org.gephi.datalaboratory.api.AttributesController;
import org.gephi.datalaboratory.api.GraphElementsController;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Implementation of the AttributesController interface
 * declared in the Data Laboratory API.
 * @author Eduardo Ramos <eduramiba@gmail.com>
 * @see AttributesController
 */
@ServiceProvider(service = AttributesController.class)
public class AttributesControllerImpl implements AttributesController {

    public AttributeColumn addAttributeColumn(AttributeTable table, String id, String title, AttributeType type){
        return table.addColumn(id, title, type, AttributeOrigin.DATA, null);
    }

    public void deleteAttributeColumn(AttributeTable table, AttributeColumn column) {
        table.removeColumn(column);
    }

    public void clearColumnData(AttributeTable table, AttributeColumn column) {
        Graph graph = Lookup.getDefault().lookup(GraphController.class).getModel().getGraph();
        if (isNodeTable(table)) {
            //Clear column data for nodes:
            Node[] nodes = graph.getNodes().toArray();
            for (Node n : nodes) {
                n.getNodeData().getAttributes().setValue(column.getIndex(), null);
            }
        } else {
            //Clear column data for edges:
            Edge[] edges = graph.getEdges().toArray();
            for (Edge e : edges) {
                e.getEdgeData().getAttributes().setValue(column.getIndex(), null);
            }
        }
    }

    public void clearNodeData(Node node) {
        GraphElementsController gec = Lookup.getDefault().lookup(GraphElementsController.class);
        if (gec.isNodeInGraph(node)) {
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            AttributeValue[] values = row.getValues();
            for (int i = 0; i < values.length; i++) {
                //Clear all except id and computed attributes:
                if (isNotComputedOrIDColumn(values[i].getColumn(), true)) {
                    row.setValue(i, null);
                }
            }
        }
    }

    public void clearNodesData(Node[] nodes) {
        for (Node n : nodes) {
            clearNodeData(n);
        }
    }

    public void clearEdgeData(Edge edge) {
        GraphElementsController gec = Lookup.getDefault().lookup(GraphElementsController.class);
        if (gec.isEdgeInGraph(edge)) {
            AttributeRow row = (AttributeRow) edge.getEdgeData().getAttributes();
            AttributeValue[] values = row.getValues();
            for (int i = 0; i < values.length; i++) {
                //Clear all except id and computed attributes:
                if (isNotComputedOrIDColumn(values[i].getColumn(), false)) {
                    row.setValue(i, null);
                }
            }
        }
    }

    public void clearEdgesData(Edge[] edges) {
        for (Edge e : edges) {
            clearEdgeData(e);
        }
    }

    /************Private methods : ************/

    /**
     * Used for checking if a table is nodes table or edges table (assumed because data laboratory uses nodes table and edges table only).
     * @return True if the table is nodes table, false otherwise
     */
    private boolean isNodeTable(AttributeTable table) {
        AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
        return table == ac.getModel().getNodeTable();
    }

    /**
     * Used for checking if a column is not a computed column or id column. Must indicate if the column is from nodes table or not.
     * (Then assumes it is edges table column because data laboratory uses nodes table and edges table only)
     * @return True if the column is not a computed column or id column, false otherwise
     */
    private boolean isNotComputedOrIDColumn(AttributeColumn column, boolean nodeTable){
        if(nodeTable){
            return column.getIndex() != PropertiesColumn.NODE_ID.getIndex() && column.getOrigin() != AttributeOrigin.COMPUTED;
        }else {
            return column.getIndex() != PropertiesColumn.EDGE_ID.getIndex() && column.getOrigin() != AttributeOrigin.COMPUTED;
        }
    }
}
