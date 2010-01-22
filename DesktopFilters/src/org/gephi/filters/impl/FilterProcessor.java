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
package org.gephi.filters.impl;

import java.util.ArrayList;
import java.util.List;
import org.gephi.filters.spi.ComplexFilter;
import org.gephi.filters.spi.EdgeFilter;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.NodeFilter;
import org.gephi.filters.spi.Operator;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;

/**
 *
 * @author Mathieu Bastian
 */
public class FilterProcessor {

    public Graph process(AbstractQueryImpl query, GraphModel graphModel) {
        Graph rootGraph = graphModel.getGraph();
        query = simplifyQuery(query);
        AbstractQueryImpl[] tree = getTree(query, true);
        for (int i = 0; i < tree.length; i++) {
            AbstractQueryImpl q = tree[tree.length - i - 1];
            Graph[] input = new Graph[0];
            if (q.getChildrenCount() > 0) {
                input = new Graph[q.getChildrenCount()];
                for (int j = 0; j < input.length; j++) {
                    input[j] = q.getChildAt(j).getResult();
                }
            } else {
                //Leaves
                GraphView newView = graphModel.newView();
                input = new Graph[]{graphModel.getGraph(newView)};    //duplicate root
            }
            //PROCESS
            if (q instanceof OperatorQueryImpl && !((OperatorQueryImpl) q).isSimple()) {
                OperatorQueryImpl operatorQuery = (OperatorQueryImpl) q;
                Operator op = (Operator) operatorQuery.getFilter();
                q.setResult(op.filter(input));
            } else if (q instanceof OperatorQueryImpl && ((OperatorQueryImpl) q).isSimple()) {
                OperatorQueryImpl operatorQuery = (OperatorQueryImpl) q;
                Operator op = (Operator) operatorQuery.getFilter();
                Filter[] filters = new Filter[operatorQuery.getChildrenCount()];
                for (int k = 0; k < filters.length; k++) {
                    filters[k] = operatorQuery.getChildAt(k).getFilter();
                }
                GraphView newView = graphModel.newView();
                q.setResult(op.filter(graphModel.getGraph(newView), filters));
            } else {
                FilterQueryImpl filterQuery = (FilterQueryImpl) q;
                Filter filter = filterQuery.getFilter();
                if (filter instanceof NodeFilter) {
                    processNodeFilter((NodeFilter) filter, input[0]);
                    q.setResult(input[0]);
                } else if (filter instanceof EdgeFilter) {
                    processEdgeFilter((EdgeFilter) filter, input[0]);
                    q.setResult(input[0]);
                } else {
                    ComplexFilter cf = (ComplexFilter) filter;
                    q.setResult(cf.filter(input[0]));
                }
            }
        }
        Graph finalResult = tree[0].result;
        return finalResult;
    }

    private void processNodeFilter(NodeFilter nodeFilter, Graph graph) {
        List<Node> nodesToRemove = new ArrayList<Node>();
        for (Node n : graph.getNodes()) {
            if (!nodeFilter.evaluate(graph, n)) {
                nodesToRemove.add(n);
            }
        }

        for (Node n : nodesToRemove) {
            graph.removeNode(n);
        }
    }

    private void processEdgeFilter(EdgeFilter edgeFilter, Graph graph) {
        List<Edge> edgesToRemove = new ArrayList<Edge>();
        for (Edge e : graph.getEdges()) {
            if (!edgeFilter.evaluate(graph, e)) {
                edgesToRemove.add(e);
            }
        }

        for (Edge e : edgesToRemove) {
            graph.removeEdge(e);
        }
    }

    private AbstractQueryImpl simplifyQuery(AbstractQueryImpl query) {
        AbstractQueryImpl copy = query.copy();
        for (AbstractQueryImpl q : getTree(copy, false)) {
            if (q instanceof OperatorQueryImpl && q.getChildrenCount() > 0) {
                boolean canSimplify = true;
                for (AbstractQueryImpl child : q.children) {
                    if (child.getChildrenCount() > 0 || !(child.getFilter() instanceof NodeFilter || child.getFilter() instanceof EdgeFilter)) {
                        canSimplify = false;
                    }
                }
                if (canSimplify) {
                    ((OperatorQueryImpl) q).setSimple(true);
                }
            }
        }
        return copy;
    }

    private AbstractQueryImpl[] getTree(AbstractQueryImpl query, boolean ignoreSimple) {
        ArrayList<AbstractQueryImpl> tree = new ArrayList<AbstractQueryImpl>();
        int pointer = 0;
        tree.add(query);
        while (pointer < tree.size()) {
            AbstractQueryImpl q = tree.get(pointer++);
            if (q.children.size() > 0) {
                if (!(q instanceof OperatorQueryImpl && ((OperatorQueryImpl) q).isSimple())) {
                    tree.addAll(q.children);
                }
            }
        }
        return tree.toArray(new AbstractQueryImpl[0]);
    }
}
