/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke, Eduardo Ramos
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
package org.gephi.ui.datatable;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.RowFilter;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.properties.PropertiesColumn;
import org.gephi.datalaboratory.api.DataLaboratoryHelper;
import org.gephi.datalaboratory.spi.edges.EdgesManipulator;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.HierarchicalGraph;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import org.openide.awt.MouseUtils;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class EdgeDataTable {

    private JXTable table;
    private PropertyEdgeDataColumn[] propertiesColumns;
    private RowFilter rowFilter;
    private Edge[] selectedEdges;

    public EdgeDataTable() {
        table = new JXTable();
        table.setHighlighters(HighlighterFactory.createAlternateStriping());
        table.setColumnControlVisible(true);
        table.setSortable(true);
        table.setRowFilter(rowFilter);
        table.setHorizontalScrollEnabled(true);

        propertiesColumns = new PropertyEdgeDataColumn[3];

        propertiesColumns[0] = new PropertyEdgeDataColumn(NbBundle.getMessage(EdgeDataTable.class, "EdgeDataTable.source.column.text")) {

            @Override
            public Class getColumnClass() {
                return String.class;
            }

            @Override
            public Object getValueFor(Edge edge) {
                return edge.getSource().getId() + " - " + edge.getSource().getNodeData().getLabel();
            }
        };

        propertiesColumns[1] = new PropertyEdgeDataColumn(NbBundle.getMessage(EdgeDataTable.class, "EdgeDataTable.target.column.text")) {

            @Override
            public Class getColumnClass() {
                return String.class;
            }

            @Override
            public Object getValueFor(Edge edge) {
                return edge.getTarget().getId() + " - " + edge.getTarget().getNodeData().getLabel();
            }
        };
        propertiesColumns[2] = new PropertyEdgeDataColumn(NbBundle.getMessage(EdgeDataTable.class, "EdgeDataTable.type.column.text")) {

            @Override
            public Class getColumnClass() {
                return String.class;
            }

            @Override
            public Object getValueFor(Edge edge) {
                if (edge.isDirected()) {
                    return NbBundle.getMessage(EdgeDataTable.class, "EdgeDataTable.type.column.directed");
                } else {
                    return NbBundle.getMessage(EdgeDataTable.class, "EdgeDataTable.type.column.undirected");
                }
            }
        };
        table.addMouseListener(new PopupAdapter());
        table.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    Edge[] selectedEdges = getEdgesFromSelectedRows();
                    if (selectedEdges.length > 0) {
                        DataLaboratoryHelper dlh = Lookup.getDefault().lookup(DataLaboratoryHelper.class);
                        EdgesManipulator del = dlh.getDeleEdgesManipulator();
                        if (del != null) {
                            del.setup(selectedEdges, null);
                            if (del.canExecute()) {
                                dlh.executeManipulator(del);
                            }
                        }
                    }
                }
            }
        });
    }

    public JXTable getTable() {
        return table;
    }

    public boolean setPattern(String regularExpr, int column) {
        try {
            if (regularExpr.startsWith("(?i)")) {   //CASE_INSENSITIVE
                regularExpr = "(?i)" + regularExpr;
            }
            rowFilter = RowFilter.regexFilter(regularExpr, column);
            table.setRowFilter(rowFilter);
        } catch (PatternSyntaxException e) {
            return false;
        }
        return true;
    }

    public void refreshModel(HierarchicalGraph graph, AttributeColumn[] cols, DataTablesModel dataTablesModel) {
        if (selectedEdges == null) {
            selectedEdges = getEdgesFromSelectedRows();
        }
        ArrayList<EdgeDataColumn> columns = new ArrayList<EdgeDataColumn>();
        columns.addAll(Arrays.asList(propertiesColumns));

        for (AttributeColumn c : cols) {
            columns.add(new AttributeEdgeDataColumn(c));
        }

        EdgeDataTableModel model = new EdgeDataTableModel(graph.getEdges().toArray(), columns.toArray(new EdgeDataColumn[0]));
        table.setModel(model);
        setEdgesSelection(selectedEdges);//Keep row selection before refreshing.
        selectedEdges = null;
    }

    public void setEdgesSelection(Edge[] edges) {
        this.selectedEdges = edges;//Keep this selection request to be able to do it if the table is first refreshed later.
        HashSet<Edge> edgesSet = new HashSet<Edge>();
        edgesSet.addAll(Arrays.asList(edges));
        table.clearSelection();
        for (int i = 0; i < table.getRowCount(); i++) {
            if (edgesSet.contains(getEdgeFromRow(i))) {
                table.addRowSelectionInterval(i, i);
            }
        }
        //TODO: Scroll to first selected edge
    }

    public boolean hasData() {
        return table.getRowCount() > 0;
    }

    private String[] getHiddenColumns() {
        List<String> hiddenCols = new ArrayList<String>();
        TableColumnModelExt columnModel = (TableColumnModelExt) table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumnExt col = columnModel.getColumnExt(i);
            if (!col.isVisible()) {
                hiddenCols.add((String) col.getHeaderValue());
            }
        }
        return hiddenCols.toArray(new String[0]);
    }

    private void setHiddenColumns(String[] columns) {
        TableColumnModelExt columnModel = (TableColumnModelExt) table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumnExt col = columnModel.getColumnExt(i);
            for (int j = 0; j < columns.length; j++) {
                if (columns[j].equals(col.getHeaderValue())) {
                    col.setVisible(false);
                }
            }
        }
    }

    private class EdgeDataTableModel implements TableModel {

        private Edge[] edges;
        private EdgeDataColumn[] columns;

        public EdgeDataTableModel(Edge[] edges, EdgeDataColumn[] cols) {
            this.edges = edges;
            this.columns = cols;
        }

        public int getRowCount() {
            return edges.length;
        }

        public int getColumnCount() {
            return columns.length;
        }

        public String getColumnName(int columnIndex) {
            return columns[columnIndex].getColumnName();
        }

        public Class<?> getColumnClass(int columnIndex) {
            return columns[columnIndex].getColumnClass();
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columns[columnIndex].isEditable();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return columns[columnIndex].getValueFor(edges[rowIndex]);
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            columns[columnIndex].setValueFor(edges[rowIndex], aValue);
        }

        public void addTableModelListener(TableModelListener l) {
        }

        public void removeTableModelListener(TableModelListener l) {
        }

        public Edge getEdgeAtRow(int row) {
            return edges[row];
        }
    }

    private static interface EdgeDataColumn {

        public Class getColumnClass();

        public String getColumnName();

        public Object getValueFor(Edge edge);

        public void setValueFor(Edge edge, Object value);

        public boolean isEditable();
    }

    private static class AttributeEdgeDataColumn implements EdgeDataTable.EdgeDataColumn {

        private AttributeColumn column;

        public AttributeEdgeDataColumn(AttributeColumn column) {
            this.column = column;
        }

        public Class getColumnClass() {
            Class clazz = column.getType().getType();
            if (clazz == Character.class) {
                return String.class;//The table implementation does not allow to edit Character cells. Treat them as Strings.
            } else {
                return clazz;
            }
        }

        public String getColumnName() {
            return column.getTitle();
        }

        public Object getValueFor(Edge edge) {
            return edge.getEdgeData().getAttributes().getValue(column.getIndex());
        }

        public void setValueFor(Edge edge, Object value) {
            if (column.getType().getType() == Character.class) {
                //The table implementation does not allow to edit Character cells. Treat them as Strings and use first character of them:
                String str = (String) value;
                if (str != null && str.length() > 0) {
                    value = new Character(str.charAt(0));
                } else {
                    value = null;
                }
            }
            edge.getEdgeData().getAttributes().setValue(column.getIndex(), value);

        }

        public boolean isEditable() {
            return column.getOrigin().equals(AttributeOrigin.DATA) || (column.getOrigin().equals(AttributeOrigin.PROPERTY) && column.getIndex() != PropertiesColumn.EDGE_ID.getIndex());
        }
    }

    private static abstract class PropertyEdgeDataColumn implements EdgeDataTable.EdgeDataColumn {

        private String name;

        public PropertyEdgeDataColumn(String name) {
            this.name = name;
        }

        public abstract Class getColumnClass();

        public String getColumnName() {
            return name;
        }

        public abstract Object getValueFor(Edge edge);

        public void setValueFor(Edge edge, Object value) {
        }

        public boolean isEditable() {
            return false;
        }
    }

    private class PopupAdapter extends MouseUtils.PopupMouseAdapter {

        PopupAdapter() {
        }

        protected void showPopup(MouseEvent e) {
            int selRow = table.rowAtPoint(e.getPoint());

            if (selRow != -1) {
                if (!table.getSelectionModel().isSelectedIndex(selRow)) {
                    table.getSelectionModel().clearSelection();
                    table.getSelectionModel().setSelectionInterval(selRow, selRow);
                }
                Point p = e.getPoint();
                JPopupMenu pop = createPopup(p);
                showPopup(p.x, p.y, pop);
            } else {
                table.getSelectionModel().clearSelection();
            }
            e.consume();
        }

        private void showPopup(int xpos, int ypos, final JPopupMenu popup) {
            if ((popup != null) && (popup.getSubElements().length > 0)) {
                final PopupMenuListener p = new PopupMenuListener() {

                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    }

                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        popup.removePopupMenuListener(this);
                        table.requestFocus();
                    }

                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                };
                popup.addPopupMenuListener(p);
                popup.show(table, xpos, ypos);
            }
        }

        private JPopupMenu createPopup(Point p) {
            final Edge[] selectedEdges = getEdgesFromSelectedRows();
            final Edge clickedEdge = getEdgeFromRow(table.rowAtPoint(p));
            JPopupMenu contextMenu = new JPopupMenu();
            DataLaboratoryHelper dlh = Lookup.getDefault().lookup(DataLaboratoryHelper.class);
            Integer lastManipulatorType = null;
            for (EdgesManipulator em : dlh.getEdgesManipulators()) {
                em.setup(selectedEdges, clickedEdge);
                if (lastManipulatorType == null) {
                    lastManipulatorType = em.getType();
                }
                if (lastManipulatorType != em.getType()) {
                    contextMenu.addSeparator();
                }
                lastManipulatorType = em.getType();
                contextMenu.add(createMenuItemFromEdgesManipulator(em));
            }
            return contextMenu;
        }

        private JMenuItem createMenuItemFromEdgesManipulator(final EdgesManipulator em) {
            JMenuItem menuItem = new JMenuItem();
            menuItem.setText(em.getName());
            if (em.getDescription() != null && !em.getDescription().isEmpty()) {
                menuItem.setToolTipText(em.getDescription());
            }
            menuItem.setIcon(em.getIcon());
            if (em.canExecute()) {
                menuItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        DataLaboratoryHelper dlh = Lookup.getDefault().lookup(DataLaboratoryHelper.class);
                        dlh.executeManipulator(em);
                    }
                });
            } else {
                menuItem.setEnabled(false);
            }
            return menuItem;
        }
    }

    private Edge getEdgeFromRow(int row) {
        return ((EdgeDataTableModel) table.getModel()).getEdgeAtRow(table.convertRowIndexToModel(row));
    }

    public Edge[] getEdgesFromSelectedRows() {
        int[] selectedRows = table.getSelectedRows();
        Edge[] edges = new Edge[selectedRows.length];
        for (int i = 0; i < edges.length; i++) {
            edges[i] = getEdgeFromRow(selectedRows[i]);
        }
        return edges;
    }
}
