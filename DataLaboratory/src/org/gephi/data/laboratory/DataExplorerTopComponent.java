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
package org.gephi.data.laboratory;

import java.awt.Color;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphEvent;
import org.gephi.graph.api.GraphListener;
import org.gephi.graph.api.HierarchicalDirectedGraph;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.project.api.ProjectController;
import org.gephi.ui.utils.BusyUtils;
import org.gephi.workspace.api.Workspace;
import org.gephi.workspace.api.WorkspaceListener;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.Utilities;

/**
 *
 * @author Mathieu Bastian
 */
final class DataExplorerTopComponent extends TopComponent implements LookupListener, GraphListener {

    private enum ClassDisplayed {

        NONE, NODE, EDGE
    };
    private static DataExplorerTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "DataExplorerTopComponent";

    //Data
    private Lookup.Result<AttributeColumn> nodeColumnsResult;
    private Lookup.Result<AttributeColumn> edgeColumnsResult;
    private HierarchicalGraph graph;

    //Table
    private NodeDataTable nodeTable;
    private EdgeDataTable edgeTable;

    //States
    ClassDisplayed classDisplayed = ClassDisplayed.NONE;
    //Executor
    ExecutorService taskExecutor;

    private DataExplorerTopComponent() {

        taskExecutor = new ThreadPoolExecutor(0, 1, 10L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(20), new ThreadFactory() {

            public Thread newThread(Runnable r) {
                return new Thread(r, "Data Laboratory fetching");
            }
        });

        initComponents();
        setName(NbBundle.getMessage(DataExplorerTopComponent.class, "CTL_DataExplorerTopComponent"));
        setToolTipText(NbBundle.getMessage(DataExplorerTopComponent.class, "HINT_DataExplorerTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));

        initEvents();

        //Init table
        nodeTable = new NodeDataTable();
        edgeTable = new EdgeDataTable();

        //Init
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        if (pc.getCurrentWorkspace() == null) {
            nodesButton.setEnabled(false);
            edgesButton.setEnabled(false);
            filterTextField.setEnabled(false);
            labelFilter.setEnabled(false);
            bannerPanel.setVisible(false);
        }
        bannerPanel.setVisible(false);
    }

    private void initEvents() {
        //Workspace Listener
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.addWorkspaceListener(new WorkspaceListener() {

            public void initialize(Workspace workspace) {
            }

            public void select(Workspace workspace) {
                elementGroup.clearSelection();
                tableScrollPane.setViewportView(null);
                nodesButton.setEnabled(true);
                edgesButton.setEnabled(true);
                filterTextField.setEnabled(true);
                labelFilter.setEnabled(true);
                bannerPanel.setVisible(false);
            }

            public void unselect(Workspace workspace) {
                if (graph != null) {
                    graph.removeGraphListener(DataExplorerTopComponent.this);
                }
                graph = null;
            }

            public void close(Workspace workspace) {
            }

            public void disable() {
                elementGroup.clearSelection();
                nodesButton.setEnabled(false);
                edgesButton.setEnabled(false);
                filterTextField.setEnabled(false);
                labelFilter.setEnabled(false);
                bannerPanel.setVisible(false);
            }
        });

        //Init lookups
        AttributeController attributeController = Lookup.getDefault().lookup(AttributeController.class);
        nodeColumnsResult = attributeController.getNodeColumnsLookup().lookupResult(AttributeColumn.class);
        edgeColumnsResult = attributeController.getEdgeColumnsLookup().lookupResult(AttributeColumn.class);
        nodeColumnsResult.addLookupListener(this);
        edgeColumnsResult.addLookupListener(this);
    }

    private void initNodesView() {
        Runnable initNodesRunnable = new Runnable() {

            public void run() {
                try {
                    String busyMsg = NbBundle.getMessage(DataExplorerTopComponent.class, "DataExplorerTopComponent.tableScrollPane.busyMessage");
                    BusyUtils.BusyLabel busylabel = BusyUtils.createCenteredBusyLabel(tableScrollPane, busyMsg, nodeTable.getTreeTable());
                    busylabel.setBusy(true);

                    //Attributes columns
                    Collection<? extends AttributeColumn> attributeColumns = nodeColumnsResult.allInstances();
                    final AttributeColumn[] cols = attributeColumns.toArray(new AttributeColumn[0]);

                    //Nodes from DHNS
                    graph = Lookup.getDefault().lookup(GraphController.class).getHierarchicalDirectedGraph();
                    if (graph == null) {
                        tableScrollPane.setViewportView(null);
                        return;
                    }

                    //Listener
                    graph.addGraphListener(DataExplorerTopComponent.this);

                    nodeTable.refreshModel(graph, cols);
                    busylabel.setBusy(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    JLabel errorLabel = new JLabel(NbBundle.getMessage(DataExplorerTopComponent.class, "DataExplorerTopComponent.tableScrollPane.error"), SwingConstants.CENTER);
                    tableScrollPane.setViewportView(errorLabel);
                }
            }
        };
        Future future = taskExecutor.submit(initNodesRunnable);
    }

    private void initEdgesView() {
        Runnable initEdgesRunnable = new Runnable() {

            public void run() {
                try {
                    String busyMsg = NbBundle.getMessage(DataExplorerTopComponent.class, "DataExplorerTopComponent.tableScrollPane.busyMessage");
                    BusyUtils.BusyLabel busylabel = BusyUtils.createCenteredBusyLabel(tableScrollPane, busyMsg, edgeTable.getTreeTable());
                    busylabel.setBusy(true);

                    //Attributes columns
                    Collection<? extends AttributeColumn> attributeColumns = edgeColumnsResult.allInstances();
                    final AttributeColumn[] cols = attributeColumns.toArray(new AttributeColumn[0]);

                    //Edges from DHNS
                    HierarchicalDirectedGraph graph = Lookup.getDefault().lookup(GraphController.class).getHierarchicalDirectedGraph();
                    if (graph == null) {
                        tableScrollPane.setViewportView(null);
                        return;
                    }
                    //Listener
                    graph.addGraphListener(DataExplorerTopComponent.this);

                    edgeTable.refreshModel(graph, cols);
                    busylabel.setBusy(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    JLabel errorLabel = new JLabel(NbBundle.getMessage(DataExplorerTopComponent.class, "DataExplorerTopComponent.tableScrollPane.error"), SwingConstants.CENTER);
                    tableScrollPane.setViewportView(errorLabel);
                }
            }
        };
        Future future = taskExecutor.submit(initEdgesRunnable);
    }

    public void resultChanged(LookupEvent ev) {
        if (!bannerPanel.isVisible() && classDisplayed != ClassDisplayed.NONE) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    bannerPanel.setVisible(true);
                }
            });
        }
    }

    public void graphChanged(GraphEvent event) {
        if (!bannerPanel.isVisible() && classDisplayed != ClassDisplayed.NONE) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    bannerPanel.setVisible(true);
                }
            });
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        elementGroup = new javax.swing.ButtonGroup();
        controlToolbar = new javax.swing.JToolBar();
        nodesButton = new javax.swing.JToggleButton();
        edgesButton = new javax.swing.JToggleButton();
        boxGlue = new javax.swing.JLabel();
        labelFilter = new org.jdesktop.swingx.JXLabel();
        filterTextField = new javax.swing.JTextField();
        tableScrollPane = new javax.swing.JScrollPane();
        bannerPanel = new javax.swing.JPanel();
        labelBanner = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        controlToolbar.setFloatable(false);
        controlToolbar.setRollover(true);

        elementGroup.add(nodesButton);
        org.openide.awt.Mnemonics.setLocalizedText(nodesButton, org.openide.util.NbBundle.getMessage(DataExplorerTopComponent.class, "DataExplorerTopComponent.nodesButton.text")); // NOI18N
        nodesButton.setFocusable(false);
        nodesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nodesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nodesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodesButtonActionPerformed(evt);
            }
        });
        controlToolbar.add(nodesButton);

        elementGroup.add(edgesButton);
        org.openide.awt.Mnemonics.setLocalizedText(edgesButton, org.openide.util.NbBundle.getMessage(DataExplorerTopComponent.class, "DataExplorerTopComponent.edgesButton.text")); // NOI18N
        edgesButton.setFocusable(false);
        edgesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        edgesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        edgesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgesButtonActionPerformed(evt);
            }
        });
        controlToolbar.add(edgesButton);

        org.openide.awt.Mnemonics.setLocalizedText(boxGlue, org.openide.util.NbBundle.getMessage(DataExplorerTopComponent.class, "DataExplorerTopComponent.boxGlue.text")); // NOI18N
        boxGlue.setMaximumSize(new java.awt.Dimension(32767, 32767));
        controlToolbar.add(boxGlue);

        org.openide.awt.Mnemonics.setLocalizedText(labelFilter, org.openide.util.NbBundle.getMessage(DataExplorerTopComponent.class, "DataExplorerTopComponent.labelFilter.text")); // NOI18N
        controlToolbar.add(labelFilter);

        filterTextField.setText(org.openide.util.NbBundle.getMessage(DataExplorerTopComponent.class, "DataExplorerTopComponent.filterTextField.text")); // NOI18N
        filterTextField.setMaximumSize(new java.awt.Dimension(1000, 30));
        filterTextField.setPreferredSize(new java.awt.Dimension(150, 20));
        controlToolbar.add(filterTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(controlToolbar, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tableScrollPane, gridBagConstraints);

        bannerPanel.setBackground(new java.awt.Color(178, 223, 240));
        bannerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        bannerPanel.setLayout(new java.awt.GridBagLayout());

        labelBanner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/data/laboratory/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(labelBanner, org.openide.util.NbBundle.getMessage(DataExplorerTopComponent.class, "DataExplorerTopComponent.labelBanner.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 0);
        bannerPanel.add(labelBanner, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(refreshButton, org.openide.util.NbBundle.getMessage(DataExplorerTopComponent.class, "DataExplorerTopComponent.refreshButton.text")); // NOI18N
        refreshButton.setOpaque(false);
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 1);
        bannerPanel.add(refreshButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(bannerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        bannerPanel.setVisible(false);
        if (classDisplayed.equals(ClassDisplayed.NODE)) {
            initNodesView();
        } else if (classDisplayed.equals(ClassDisplayed.EDGE)) {
            initEdgesView();
        }
}//GEN-LAST:event_refreshButtonActionPerformed

    private void edgesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edgesButtonActionPerformed
        classDisplayed = ClassDisplayed.EDGE;
        initEdgesView();
}//GEN-LAST:event_edgesButtonActionPerformed

    private void nodesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nodesButtonActionPerformed
        classDisplayed = ClassDisplayed.NODE;
        initNodesView();
}//GEN-LAST:event_nodesButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bannerPanel;
    private javax.swing.JLabel boxGlue;
    private javax.swing.JToolBar controlToolbar;
    private javax.swing.JToggleButton edgesButton;
    private javax.swing.ButtonGroup elementGroup;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JLabel labelBanner;
    private org.jdesktop.swingx.JXLabel labelFilter;
    private javax.swing.JToggleButton nodesButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JScrollPane tableScrollPane;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized DataExplorerTopComponent getDefault() {
        if (instance == null) {
            instance = new DataExplorerTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the DataExplorerTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized DataExplorerTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(DataExplorerTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof DataExplorerTopComponent) {
            return (DataExplorerTopComponent) win;
        }
        Logger.getLogger(DataExplorerTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return DataExplorerTopComponent.getDefault();
        }
    }
}
