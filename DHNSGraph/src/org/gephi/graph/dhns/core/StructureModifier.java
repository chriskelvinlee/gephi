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
package org.gephi.graph.dhns.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.node.PreNode;
import org.gephi.graph.dhns.node.iterators.ChildrenIterator;
import org.gephi.graph.dhns.node.iterators.DescendantAndSelfIterator;
import org.gephi.graph.dhns.node.iterators.TreeListIterator;

/**
 * Business class for external operations on the data structure. Propose blocking mechanism.
 *
 * @author Mathieu Bastian
 */
public class StructureModifier {

    private Dhns dhns;
    private GraphVersion graphVersion;
    private TreeStructure treeStructure;
    private EdgeProcessor edgeProcessor;

    //Executor
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public StructureModifier(Dhns dhns) {
        this.dhns = dhns;
        this.treeStructure = dhns.getTreeStructure();
        this.graphVersion = dhns.getGraphVersion();
        edgeProcessor = new EdgeProcessor(dhns);
    }

    public void expand(final Node node) {
        expandBlock(node);
//        executor.execute(new Runnable() {
//
//            public void run() {
//                expandBlock(node);
//            }
//        });
    }

    public void expandBlock(Node node) {
        dhns.getWriteLock().lock();
        PreNode preNode = (PreNode) node;
        if (preNode.level < treeStructure.treeHeight) {
            expand(preNode);
            //sightManager.updateSight((SightImpl) sight);
        }
        graphVersion.incNodeVersion();
        dhns.getWriteLock().unlock();
    }

    public void retract(final Node node) {
        retractBlock(node);
//        executor.execute(new Runnable() {
//
//            public void run() {
//                retractBlock(node);
//            }
//        });
    }

    public void retractBlock(Node node) {
        dhns.getWriteLock().lock();
        PreNode preNode = (PreNode) node;
        if (preNode.level < treeStructure.treeHeight) {
            retract(((PreNode) node));
            //sightManager.updateSight((SightImpl)sight);
        }
        graphVersion.incNodeVersion();
        dhns.getWriteLock().unlock();
    }

    public void addNode(final Node node, final Node parent) {
        addNodeBlock(node, parent);
//        executor.execute(new Runnable() {
//
//            public void run() {
//                addNodeBlock(node, parent);
//            }
//        });
    }

    public void addNodeBlock(Node node, Node parent) {
        dhns.getWriteLock().lock();
        PreNode parentNode;
        if (parent == null) {
            parentNode = treeStructure.getRoot();
        } else {
            parentNode = ((PreNode) parent);
        }
        PreNode preNode = (PreNode) node;
        preNode.parent = parentNode;
        if (treeStructure.getEnabledAncestor(preNode) == null) {
            preNode.setEnabled(true);
        } else {
            preNode.setEnabled(false);
        }
        addNode(preNode);
        //dhns.getDictionary().addNode(preNode);      //Dico
        graphVersion.incNodeVersion();
        dhns.getWriteLock().unlock();
    }

    public void deleteNode(final Node node) {
        deleteNodeBlock(node);
//        executor.execute(new Runnable() {
//
//            public void run() {
//                deleteNodeBlock(node);
//            }
//        });
    }

    public void deleteNodeBlock(Node node) {
        dhns.getWriteLock().lock();
        PreNode preNode = (PreNode) node;
        deleteNode(preNode);
        //dhns.getDictionary().removeNode(preNode);      //Dico
        graphVersion.incNodeAndEdgeVersion();
        dhns.getWriteLock().unlock();
    }

    public void addEdge(final Edge edge) {
        addEdgeBlock(edge);
//        executor.execute(new Runnable() {
//
//            public void run() {
//                addEdgeBlock(edge);
//            }
//        });
    }

    public void addEdgeBlock(Edge edge) {
        dhns.getWriteLock().lock();
        AbstractEdge abstractEdge = (AbstractEdge) edge;

        addEdge(abstractEdge);
        //dhns.getDictionary().addEdge(preEdge);     //Dico
        graphVersion.incEdgeVersion();
        dhns.getWriteLock().unlock();
    }

    public boolean deleteEdge(final Edge edge) {
        return deleteEdgeBlock(edge);
//        executor.execute(new Runnable() {
//
//            public void run() {
//                deleteEdgeBlock(edge);
//            }
//        });
    }

    public boolean deleteEdgeBlock(Edge edge) {
        dhns.getWriteLock().lock();
        AbstractEdge edgeImpl = (AbstractEdge) edge;
        //dhns.getDictionary().removeEdge(edge);
        boolean res = delEdge(edgeImpl);
        graphVersion.incEdgeVersion();
        dhns.getWriteLock().unlock();
        return res;
    }

    public void clear() {
        clearBlock();
//        executor.execute(new Runnable() {
//
//            public void run() {
//                clearBlock();
//            }
//        });
    }

    public void clearBlock() {
        dhns.getWriteLock().lock();
        clearAllEdges();
        clearAllNodes();
        graphVersion.incNodeAndEdgeVersion();
        dhns.getWriteLock().unlock();
    }

    public void clearEdges() {
        clearEdgesBlock();
//        executor.execute(new Runnable() {
//
//            public void run() {
//                clearEdgesBlock();
//            }
//        });
    }

    public void clearEdgesBlock() {
        dhns.getWriteLock().lock();
        clearAllEdges();
        graphVersion.incEdgeVersion();
        dhns.getWriteLock().unlock();
    }

    public void clearEdges(final Node node) {
        clearEdgesBlock(node);
//        executor.execute(new Runnable() {
//
//            public void run() {
//                clearEdgesBlock(node);
//            }
//        });
    }

    public void clearEdgesBlock(Node node) {
        dhns.getWriteLock().lock();
        clearEdges((PreNode) node);
        graphVersion.incEdgeVersion();
        dhns.getWriteLock().unlock();
    }

    public void clearMetaEdges(final Node node) {
        clearMetaEdgesBlock(node);
//        executor.execute(new Runnable() {
//
//            public void run() {
//                clearMetaEdgesBlock(node);
//            }
//        });
    }

    public void clearMetaEdgesBlock(Node node) {
        dhns.getWriteLock().lock();
        clearMetaEdges((PreNode) node);
        graphVersion.incEdgeVersion();
        dhns.getWriteLock().unlock();
    }

    public void resetView() {
        dhns.getWriteLock().lock();
        edgeProcessor.clearAllMetaEdges();
        for (TreeListIterator itr = new TreeListIterator(treeStructure.getTree(), 1); itr.hasNext();) {
            PreNode node = itr.next();
            if (node.size == 0) {
                node.setEnabled(true);
            } else {
                node.setEnabled(false);
            }
        }
        graphVersion.incEdgeVersion();
        dhns.getWriteLock().unlock();
    }

    public void moveToGroup(Node node, Node nodeGroup) {
        dhns.getWriteLock().lock();
        PreNode preNode = (PreNode) node;
        PreNode preGroup = (PreNode) nodeGroup;
        moveToGroup(preNode, preGroup);
        graphVersion.incNodeAndEdgeVersion();
        dhns.getWriteLock().unlock();
    }

    public Node group(Node[] nodes) {
        dhns.getWriteLock().lock();
        PreNode group = (PreNode) dhns.getGraphFactory().newNode();
        PreNode parent = ((PreNode) nodes[0]).parent;
        group.parent = parent;
        addNode(group);
        for (int i = 0; i < nodes.length; i++) {
            PreNode nodeToGroup = (PreNode) nodes[i];
            moveToGroup(nodeToGroup, group);
        }
        graphVersion.incNodeAndEdgeVersion();
        dhns.getWriteLock().unlock();
        return group;
    }

    public void ungroup(PreNode nodeGroup) {
        dhns.getWriteLock().lock();
        //TODO Better implementation. Just remove nodeGroup from the treelist and lower level of children
        int count = 0;
        for (ChildrenIterator itr = new ChildrenIterator(treeStructure, nodeGroup); itr.hasNext();) {
            itr.next();
            count++;
        }

        for (int i = 0; i < count; i++) {
            PreNode node = treeStructure.getNodeAt(nodeGroup.getPre() + 1);
            dhns.getStructureModifier().moveToGroup(node, nodeGroup.parent);
        }

        deleteNode(nodeGroup);

        graphVersion.incNodeAndEdgeVersion();
        dhns.getWriteLock().unlock();
    }

    //------------------------------------------
    private void expand(PreNode preNode) {

        //Disable parent
        preNode.setEnabled(false);
        edgeProcessor.clearMetaEdges(preNode);

        //Enable children
        for (ChildrenIterator itr = new ChildrenIterator(treeStructure, preNode); itr.hasNext();) {
            PreNode child = itr.next();
            child.setEnabled(true);
            edgeProcessor.computeMetaEdges(child);
        }
    }

    private void retract(PreNode parent) {

        //Disable children
        for (ChildrenIterator itr = new ChildrenIterator(treeStructure, parent); itr.hasNext();) {
            PreNode child = itr.next();
            child.setEnabled(false);
            edgeProcessor.clearMetaEdges(child);
        }

        //Enable node
        parent.setEnabled(true);
        edgeProcessor.computeMetaEdges(parent);
    }

    private void addNode(PreNode node) {
        treeStructure.insertAsChild(node, node.parent);
    }

    private void addEdge(AbstractEdge edge) {
        PreNode sourceNode = edge.getSource();
        PreNode targetNode = edge.getTarget();

        //Add Edges
        sourceNode.getEdgesOutTree().add(edge);
        targetNode.getEdgesInTree().add(edge);

        //Add Meta Edge
        if (!edge.isSelfLoop()) {
            edgeProcessor.createMetaEdge(edge);
        }
    }

    private void deleteNode(PreNode node) {

        for (DescendantAndSelfIterator itr = new DescendantAndSelfIterator(treeStructure, node); itr.hasNext();) {
            PreNode descendant = itr.next();
            if (descendant.isEnabled()) {
                edgeProcessor.clearMetaEdges(descendant);
            }
            edgeProcessor.clearEdges(descendant);
        }

        treeStructure.deleteDescendantAndSelf(node);
    }

    private boolean delEdge(AbstractEdge edge) {
        //Remove edge
        boolean res = edge.getSource().getEdgesOutTree().remove(edge);
        res = res && edge.getTarget().getEdgesInTree().remove(edge);

        //Remove edge from possible metaEdge
        edgeProcessor.removeEdgeFromMetaEdge(edge);
        return res;
    }

    private void clearAllEdges() {
        edgeProcessor.clearAllEdges();
    }

    private void clearAllNodes() {
        treeStructure.clear();
    }

    private void clearEdges(PreNode node) {
        edgeProcessor.clearEdges(node);
    }

    private void clearMetaEdges(PreNode node) {
        edgeProcessor.clearMetaEdges(node);
    }

    private void moveToGroup(PreNode node, PreNode nodeGroup) {
        treeStructure.move(node, nodeGroup);
    }
}
