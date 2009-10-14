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
package org.gephi.graph.dhns.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.gephi.datastructure.avl.param.ParamAVLIterator;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Filters;
import org.gephi.graph.api.GraphEvent.EventType;
import org.gephi.graph.api.NodePredicate;
import org.gephi.graph.api.Predicate;
import org.gephi.graph.api.TopologicalPredicate;
import org.gephi.graph.dhns.core.Dhns;
import org.gephi.graph.dhns.core.GraphVersion;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.edge.MetaEdgeImpl;
import org.gephi.graph.dhns.edge.iterators.AbstractEdgeIterator;
import org.gephi.graph.dhns.graph.ClusteredGraphImpl;
import org.gephi.graph.dhns.node.AbstractNode;
import org.gephi.graph.dhns.node.iterators.AbstractNodeIterator;
import org.gephi.graph.dhns.node.iterators.TreeIterator;
import org.gephi.graph.dhns.proposition.Tautology;
import org.gephi.graph.dhns.utils.avl.AbstractEdgeTree;
import org.gephi.graph.dhns.utils.avl.AbstractNodeTree;
import org.openide.util.Exceptions;

/**
 *  Cette classe contient un etant du graphe, après filtrage. Un graphe non filtré na pas besoin de cache car les iterateurs directs
 * sont assez rapides pour fonctionner en boucle continue. Cette cache doit être regénéré à la demande de lecture du graphe après une
 * modification. Ces modifications peuvent être structurelles (ajou/suppression de noeuds, changement dans la hierarchie) ou bien
 * liées aux filtres eux memes. En effet lorsqu'un paramètre de filtres est modifiée, le filtrage change et donc le graphe résultat change.
 * Ceci doit être très rapide afin de permettre les changements reguliers, mais doit aussi faire profiter une reduction du nombre d'elements
 * a iterer.
 * On doit pouvoir paramétrer la mise à jour automatique ou non de cette cache.
 * Cette cache est propre à une instance de graphe, et pourra eventuellement s'echanger/se cloner.
 * Cette chache représente ce qui subsiste après filtrage. Elle est utilisée par les methodes annexes comme getNeigbors(Node). En effet
 * la methode getNeighbors se base sur le graphe complet et lance son iterateur sur les edges de Node. C'est grace au test evaluate(edge) et
 * evaluate(Node) que l'iterateurs de getNieighbors donne le bon resultat.
 * @author Mathieu
 */
public class FilterControl implements Filters {

    private FilterResult currentResult;
    private Dhns dhns;
    private ClusteredGraphImpl graph;
    private final List<Predicate> predicates = new ArrayList<Predicate>();
    private boolean filtered = false;
    private GraphVersion graphVersion;
    private ReentrantLock lock;
    private DispatchThread dispatchThread;

    //Versionning
    private int nodeVersion = -1;
    private int edgeVersion = -1;

    public FilterControl(Dhns dhns, ClusteredGraphImpl graph) {
        this.dhns = dhns;
        this.graphVersion = dhns.getGraphVersion();
        this.graph = graph;
        currentResult = new FilterResult(new AbstractNodeTree(), new AbstractEdgeTree(), new AbstractEdgeTree());
        dispatchThread = new DispatchThread();
        lock = new ReentrantLock();
    }

    private void checkUpdate() {
        if (lock.isHeldByCurrentThread()) {
            return;
        }
        int nv = graphVersion.getNodeVersion();
        int ev = graphVersion.getEdgeVersion();
        if (nodeVersion != nv || edgeVersion != ev) {
            lock.lock();
            update();
            nodeVersion = nv;
            edgeVersion = ev;
            lock.unlock();
        }
    }

    private boolean update() {

        //Initial
        filtered = false;
        AbstractEdgeTree initialEdges = new AbstractEdgeTree();
        AbstractNodeTree initialNodes = new AbstractNodeTree();
        AbstractEdgeTree initialMetaEdges = new AbstractEdgeTree();
        for (TreeIterator treeIterator = new TreeIterator(dhns.getTreeStructure(), Tautology.instance); treeIterator.hasNext();) {
            initialNodes.add(treeIterator.next());
        }
        for (Edge edge : graph.getEdges()) {
            initialEdges.add((AbstractEdge) edge);
        }
        for (Edge metaEdge : graph.getMetaEdges()) {
            initialMetaEdges.add((AbstractEdge) metaEdge);
        }
        currentResult = new FilterResult(initialNodes, initialEdges, initialMetaEdges);
        filtered = true;

        for (Predicate p : predicates) {
            AbstractEdgeTree edgeTree = currentResult.getEdgeTree();
            AbstractNodeTree nodeTree = currentResult.getNodeTree();
            AbstractEdgeTree metaEdgeTree = currentResult.getMetaEdgeTree();
            if (p instanceof NodePredicate) {
                nodeTree = new AbstractNodeTree();
                for (AbstractNodeIterator itr = currentResult.nodeIterator(); itr.hasNext();) {
                    AbstractNode node = itr.next();
                    boolean val;
                    if (p instanceof TopologicalPredicate) {
                        val = ((TopologicalPredicate) p).evaluate(node, graph);
                    } else {
                        val = p.evaluate(node);
                    }
                    if (val) {
                        nodeTree.add(node);
                    }
                }

                //Clean edges
                for (AbstractEdgeIterator itr = edgeTree.iterator(); itr.hasNext();) {
                    AbstractEdge e = itr.next();
                    if (!nodeTree.contains(e.getSource()) || !nodeTree.contains(e.getTarget())) {
                        itr.remove();
                    }
                }
            } else {
                edgeTree = new AbstractEdgeTree();
                for (AbstractEdgeIterator itr = currentResult.edgeIterator(); itr.hasNext();) {
                    AbstractEdge edge = itr.next();
                    boolean val;
                    if (p instanceof TopologicalPredicate) {
                        val = ((TopologicalPredicate) p).evaluate(edge, graph);
                    } else {
                        val = p.evaluate(edge);
                    }
                    if (val) {
                        edgeTree.add(edge);
                    }
                }
            }

            //Clean meta Edges
            //ParamAVLIterator<AbstractEdge> innerEdgeIterator = new ParamAVLIterator<AbstractEdge>();
            for (AbstractEdgeIterator itr = metaEdgeTree.iterator(); itr.hasNext();) {
                AbstractEdge e = itr.next();
                if (!nodeTree.contains(e.getSource()) || !nodeTree.contains(e.getTarget())) {
                    itr.remove();
                } /*else {
            MetaEdgeImpl metaEdge = (MetaEdgeImpl)e;
            boolean atLeastOneInnerEdgeIsNotFiltered = false;
            for(innerEdgeIterator.setNode(metaEdge.getEdges());innerEdgeIterator.hasNext();) {
            if(edgeTree.contains(innerEdgeIterator.next())) {
            atLeastOneInnerEdgeIsNotFiltered = true;
            break;
            }
            }
            if(!atLeastOneInnerEdgeIsNotFiltered) {
            itr.remove();
            }
            }*/
            }

            currentResult = new FilterResult(nodeTree, edgeTree, metaEdgeTree);
        }
        return true;
    }

    public void addPredicate(Predicate predicate) {
        if (!dispatchThread.isAlive()) {
            dispatchThread.start();
        }
        dispatchThread.addPredicate(predicate);
        if (graph.getClusteredGraph() != null) {
            graph.getClusteredGraph().getFilters().addPredicate(predicate);
        }
        filtered = true;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public boolean evaluateNode(AbstractNode node) {
        if (filtered) {
            checkUpdate();
            return currentResult.evaluateNode(node);
        }
        return true;
    }

    public boolean evaluateEdge(AbstractEdge edge) {
        if (filtered) {
            checkUpdate();
            return currentResult.evaluateEdge(edge);
        }
        return true;
    }

    public int getNodeCount() {
        checkUpdate();
        return currentResult.getNodeCount();
    }

    public int getEdgeCount() {
        checkUpdate();
        return currentResult.getEdgeCount();
    }

    public Predicate<AbstractNode> getNodePredicate() {
        if (filtered) {
            checkUpdate();
            return currentResult.getNodePredicate();
        }
        return Tautology.instance;
    }

    public Predicate<AbstractEdge> getEdgePredicate() {
        if (filtered) {
            checkUpdate();
            return currentResult.getEdgePredicate();
        }
        return Tautology.instance;
    }

    public Predicate<AbstractEdge> getMetaEdgePredicate() {
        if (filtered) {
            checkUpdate();
            return currentResult.getMetaEdgePredicate();
        }
        return Tautology.instance;
    }

    public AbstractNodeIterator nodeIterator() {
        checkUpdate();
        return currentResult.nodeIterator();
    }

    public AbstractEdgeIterator edgeIterator() {
        checkUpdate();
        return currentResult.edgeIterator();
    }

    public AbstractEdgeIterator metaEdgeIterator() {
        checkUpdate();
        return currentResult.metaEdgeIterator();
    }

    public void predicateParametersUpdates() {
        dhns.getGraphVersion().incNodeAndEdgeVersion();
        dhns.getEventManager().fireEvent(EventType.NODES_AND_EDGES_UPDATED);
    }

    public void updatePredicate(Predicate oldPredicate, Predicate newPredicate) {
        dispatchThread.updatePredicate(new Predicate[]{oldPredicate}, new Predicate[]{newPredicate});
        if (graph.getClusteredGraph() != null) {
            graph.getClusteredGraph().getFilters().updatePredicate(oldPredicate, newPredicate);
        }
        predicateParametersUpdates();
    }

    public void updatePredicate(Predicate[] oldPredicate, Predicate[] newPredicate) {
        dispatchThread.updatePredicate(oldPredicate, newPredicate);
        if (graph.getClusteredGraph() != null) {
            graph.getClusteredGraph().getFilters().updatePredicate(oldPredicate, newPredicate);
        }
        predicateParametersUpdates();
    }

    public void removePredicate(Predicate predicate) {
    }

    private class DispatchThread extends Thread {

        private final List<Predicate> predicateQueue = new ArrayList<Predicate>();
        private final Object monitor = new Object();
        private boolean waiting = false;

        @Override
        public void run() {
            synchronized (monitor) {
                while (true) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    //Action
                    waiting = true;
                    graph.writeLock();
                    synchronized (predicateQueue) {
                        predicates.clear();
                        predicates.addAll(predicateQueue);
                    }
                    graph.writeUnlock();
                    waiting = false;
                }
            }
        }

        public void addPredicate(Predicate predicate) {
            synchronized (predicateQueue) {
                predicateQueue.add(predicate);
            }
            synchronized (monitor) {
                monitor.notify();
            }
        }

        public void updatePredicate(Predicate[] oldPredicate, Predicate[] newPredicate) {
            synchronized (predicateQueue) {
                for (int i = 0; i < oldPredicate.length; i++) {
                    predicateQueue.set(predicateQueue.indexOf(oldPredicate[i]), newPredicate[i]);
                }
            }
            if (waiting) {
                return;
            }
            synchronized (monitor) {
                monitor.notify();
            }
        }
    }
}
