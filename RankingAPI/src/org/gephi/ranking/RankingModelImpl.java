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
package org.gephi.ranking;

import org.gephi.ranking.api.RankingModel;
import org.gephi.ranking.api.NodeRanking;
import org.gephi.ranking.api.EdgeRanking;
import org.gephi.ranking.api.Ranking;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.project.api.ProjectController;
import org.gephi.workspace.api.Workspace;
import org.gephi.workspace.api.WorkspaceListener;
import org.openide.util.Lookup;

/**
 *
 * @author Mathieu Bastian
 */
public class RankingModelImpl implements RankingModel {

    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    public RankingModelImpl() {
        final ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.addWorkspaceListener(new WorkspaceListener() {

            public void initialize(Workspace workspace) {
            }

            public void select(Workspace workspace) {
                fireChangeEvent();
            }

            public void unselect(Workspace workspace) {
            }

            public void close(Workspace workspace) {
            }

            public void disable() {
            }
        });
    }

    public NodeRanking[] getNodeRanking() {
        AttributeController attributeController = Lookup.getDefault().lookup(AttributeController.class);
        List<Ranking> rankingList = new ArrayList<Ranking>();
        GraphModel model = Lookup.getDefault().lookup(GraphController.class).getModel();
        Graph graph = model.getGraphVisible();

        //Topology
        NodeRanking degreeRanking = RankingFactory.getNodeDegreeRanking(graph);
        if (degreeRanking.getMinimumValue() != null && degreeRanking.getMaximumValue() != null && !degreeRanking.getMinimumValue().equals(degreeRanking.getMaximumValue())) {
            rankingList.add(degreeRanking);
        }

        if (model.isDirected()) {
            DirectedGraph directedGraph = model.getDirectedGraphVisible();
            NodeRanking inDegreeRanking = RankingFactory.getNodeInDegreeRanking(directedGraph);
            if (inDegreeRanking.getMinimumValue() != null && inDegreeRanking.getMaximumValue() != null && !inDegreeRanking.getMinimumValue().equals(inDegreeRanking.getMaximumValue())) {
                rankingList.add(inDegreeRanking);
            }
            NodeRanking outDegreeRanking = RankingFactory.getNodeOutDegreeRanking(directedGraph);
            if (outDegreeRanking.getMinimumValue() != null && outDegreeRanking.getMaximumValue() != null && !outDegreeRanking.getMinimumValue().equals(outDegreeRanking.getMaximumValue())) {
                rankingList.add(outDegreeRanking);
            }
        }

        if (model.isHierarchical()) {
            HierarchicalGraph hierarchicalGraph = model.getHierarchicalGraphVisible();
            NodeRanking childrenRanking = RankingFactory.getNodeChildrenCountRanking(hierarchicalGraph);
            if (childrenRanking.getMinimumValue() != null && childrenRanking.getMaximumValue() != null && !childrenRanking.getMinimumValue().equals(childrenRanking.getMaximumValue())) {
                rankingList.add(childrenRanking);
            }
        }

        //Attributes
        for (AttributeColumn column : attributeController.getModel().getNodeTable().getColumns()) {
            if (RankingFactory.isNumberColumn(column)) {
                NodeRanking r = RankingFactory.getNodeAttributeRanking(column, graph);
                if (r.getMinimumValue() != null && r.getMaximumValue() != null && !r.getMinimumValue().equals(r.getMaximumValue())) {
                    rankingList.add(r);
                }
            }
        }
        return rankingList.toArray(new NodeRanking[0]);
    }

    public EdgeRanking[] getEdgeRanking() {
        AttributeController attributeController = Lookup.getDefault().lookup(AttributeController.class);
        Graph graph = Lookup.getDefault().lookup(GraphController.class).getModel().getGraph();
        List<Ranking> rankingList = new ArrayList<Ranking>();
        for (AttributeColumn column : attributeController.getModel().getEdgeTable().getColumns()) {
            if (RankingFactory.isNumberColumn(column)) {
                EdgeRanking r = RankingFactory.getEdgeAttributeRanking(column, graph);
                if (r.getMinimumValue() != null && r.getMaximumValue() != null && !r.getMinimumValue().equals(r.getMaximumValue())) {
                    rankingList.add(r);
                }
            }
        }
        return rankingList.toArray(new EdgeRanking[0]);
    }

    public void addChangeListener(ChangeListener changeListener) {
        if (!listeners.contains(changeListener)) {
            listeners.add(changeListener);
        }
    }

    public void removeChangeListener(ChangeListener changeListener) {
        listeners.remove(changeListener);
    }

    private void fireChangeEvent() {
        ChangeEvent changeEvent = new ChangeEvent(this);
        for (ChangeListener changeListener : listeners) {
            changeListener.stateChanged(changeEvent);
        }
    }
}
