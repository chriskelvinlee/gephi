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
package org.gephi.graph.dhns.graph;

import org.gephi.graph.api.ClusteredGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.ImmutableTreeNode;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.gephi.graph.api.View;
import org.gephi.graph.dhns.core.Dhns;
import org.gephi.graph.dhns.core.GraphStructure;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.edge.iterators.HierarchyEdgeIterator;
import org.gephi.graph.dhns.node.AbstractNode;
import org.gephi.graph.dhns.node.CloneNode;
import org.gephi.graph.dhns.node.PreNode;
import org.gephi.graph.dhns.node.iterators.TreeListIterator;
import org.gephi.graph.dhns.node.iterators.ChildrenIterator;
import org.gephi.graph.dhns.node.iterators.DescendantIterator;
import org.gephi.graph.dhns.node.iterators.LevelIterator;
import org.gephi.graph.dhns.node.iterators.TreeIterator;
import org.gephi.graph.dhns.utils.TreeNodeWrapper;
import org.gephi.graph.dhns.views.ViewImpl;

/**
 * Abstract Clustered graph class. Implements methods for both directed and undirected graphs.
 *
 * @author Mathieu Bastian
 */
public abstract class ClusteredGraphImpl extends AbstractGraphImpl implements ClusteredGraph {

    public ClusteredGraphImpl(Dhns dhns, GraphStructure structure, View view) {
        this.dhns = dhns;
        this.structure = structure;
        this.view = (ViewImpl) view;
    }

    public abstract ClusteredGraphImpl copy(ClusteredGraphImpl graph);

    public boolean addNode(Node node, Node parent) {
        if (node == null) {
            throw new IllegalArgumentException("Node can't be null");
        }
        AbstractNode absNode = (AbstractNode) node;
        AbstractNode absParent = null;
        if (parent != null) {
            absParent = checkNode(parent);
        }
        if (!dhns.getSettingsManager().isAllowMultilevel() && absNode.isValid()) {
            return false;
        }
        if (dhns.getSettingsManager().isAllowMultilevel() && parent != null && absNode.isValid()) {
            //Verify the parent node is not a descendant of node
            readLock();
            if (isDescendant(node, parent)) {
                readUnlock();
                throw new IllegalArgumentException("Parent can't be a descendant of node");
            }
            readUnlock();
        }
        if (!absNode.hasAttributes()) {
            absNode.setAttributes(dhns.factory().newNodeAttributes());
        }
        dhns.getDynamicManager().pushNode(node.getNodeData());
        dhns.getStructureModifier().addNode(node, absParent);
        return true;
    }

    public boolean addNode(Node node) {
        return addNode(node, null);
    }

    public boolean contains(Node node) {
        if (node == null) {
            throw new NullPointerException();
        }
        readLock();
        view.checkUpdate();
        AbstractNode absNode = (AbstractNode) node;
        boolean res = false;
        if (absNode.isValid()) {
            res = structure.getStructure().getTree().contains(absNode);
        }
        readUnlock();
        return res;
    }

    public NodeIterable getNodes() {
        readLock();
        view.checkUpdate();
        return dhns.newNodeIterable(new TreeIterator(structure.getStructure(), false));
    }

    public int getNodeCount() {
        readLock();
        view.checkUpdate();
        int count = structure.getStructure().getTreeSize() - 1;// -1 Exclude virtual root
        readUnlock();
        return count;
    }

    public NodeIterable getNodes(int level) {
        level += 1;     //Because we ignore the virtual root
        readLock();
        view.checkUpdate();
        int height = structure.getStructure().treeHeight;
        if (level > height) {
            readUnlock();
            throw new IllegalArgumentException("Level must be between 0 and the height of the tree, currently height=" + (height - 1));
        }
        return dhns.newNodeIterable(new LevelIterator(structure.getStructure(), level));

    }

    public int getLevelSize(int level) {
        level += 1;     //Because we ignore the virtual root
        readLock();
        view.checkUpdate();
        int height = structure.getStructure().treeHeight;
        if (level > height) {
            readUnlock();
            throw new IllegalArgumentException("Level must be between 0 and the height of the tree, currently height=" + (height - 1));
        }
        int res = 0;
        for (LevelIterator itr = new LevelIterator(structure.getStructure(), level); itr.hasNext();) {
            itr.next();
            res++;
        }

        readUnlock();
        return res;
    }

    public boolean isSelfLoop(Edge edge) {
        checkEdge(edge);
        return edge.getSource() == edge.getTarget();
    }

    public boolean isAdjacent(Edge edge1, Edge edge2) {
        if (edge1 == edge2) {
            throw new IllegalArgumentException("Edges can't be the same");
        }
        checkEdge(edge1);
        checkEdge(edge2);
        return edge1.getSource() == edge2.getSource() ||
                edge1.getSource() == edge2.getTarget() ||
                edge1.getTarget() == edge2.getSource() ||
                edge1.getTarget() == edge2.getTarget();
    }

    public Node getOpposite(Node node, Edge edge) {
        checkNode(node);
        checkEdge(edge);
        if (edge.getSource() == node) {
            return edge.getTarget();
        } else if (edge.getTarget() == node) {
            return edge.getSource();
        }
        throw new IllegalArgumentException("Node must be either source or target of the edge.");
    }

    public boolean removeNode(Node node) {
        AbstractNode absNode = checkNode(node);
        if (!absNode.isValid()) {
            return false;
        }
        dhns.getStructureModifier().deleteNode(node);
        return true;
    }

    public void clear() {
        dhns.getStructureModifier().clear();
    }

    public void clearEdges() {
        dhns.getStructureModifier().clearEdges();
    }

    public void clearEdges(Node node) {
        checkNode(node);
        dhns.getStructureModifier().clearEdges(node);
    }

    public void clearMetaEdges(Node node) {
        checkNode(node);
        dhns.getStructureModifier().clearMetaEdges(node);
    }

    public ImmutableTreeNode wrapToTreeNode() {
        TreeNodeWrapper wrapper = new TreeNodeWrapper(structure.getStructure());
        ImmutableTreeNode treeNode;
        readLock();
        view.checkUpdate();
        treeNode = wrapper.wrap(new TreeIterator(structure.getStructure(), false));
        readUnlock();
        return treeNode;
    }

    public int getChildrenCount(Node node) {
        readLock();
        view.checkUpdate();
        AbstractNode absNode = checkNode(node);
        int count = 0;

        ChildrenIterator itr = new ChildrenIterator(structure.getStructure(), absNode);
        for (; itr.hasNext();) {
            itr.next();
            count++;
        }

        readUnlock();
        return count;
    }

    public Node getParent(Node node) {
        readLock();
        view.checkUpdate();
        AbstractNode absNode = checkNode(node);
        Node parent = null;
        if (absNode.parent != structure.getStructure().getRoot()) {
            parent = absNode.parent;
        }
        readUnlock();
        return parent;
    }

    public NodeIterable getChildren(Node node) {
        readLock();
        view.checkUpdate();
        AbstractNode absNode = checkNode(node);
        return dhns.newNodeIterable(new ChildrenIterator(structure.getStructure(), absNode));
    }

    public NodeIterable getDescendant(Node node) {
        readLock();
        view.checkUpdate();
        AbstractNode absNode = checkNode(node);
        return dhns.newNodeIterable(new DescendantIterator(structure.getStructure(), absNode));
    }

    public NodeIterable getTopNodes() {
        readLock();
        view.checkUpdate();
        return dhns.newNodeIterable(new ChildrenIterator(structure.getStructure()));
    }

    public boolean isDescendant(Node node, Node descendant) {
        readLock();
        view.checkUpdate();
        AbstractNode abstractNode = checkNode(node);
        AbstractNode preDesc = checkNode(descendant);
        boolean res = false;
        if (dhns.getSettingsManager().isAllowMultilevel()) {
            //Check if clones of descendants are descendants
            PreNode preNode = preDesc.getOriginalNode();
            res = preDesc.getPre() > abstractNode.getPre() && preDesc.getPost() < abstractNode.getPost();
            CloneNode cn = preNode.getClones();
            while (cn != null) {
                res = res || cn.getPre() > abstractNode.getPre() && cn.getPost() < abstractNode.getPost();
                cn = cn.getNext();
            }
        } else {
            res = preDesc.getPre() > abstractNode.getPre() && preDesc.getPost() < abstractNode.getPost();
        }
        readUnlock();
        return res;
    }

    public boolean isAncestor(Node node, Node ancestor) {
        return isDescendant(ancestor, node);
    }

    public boolean isFollowing(Node node, Node following) {
        readLock();
        view.checkUpdate();
        AbstractNode absNode = checkNode(node);
        AbstractNode preFoll = checkNode(following);
        boolean res = preFoll.getPre() > absNode.getPre() && preFoll.getPost() > absNode.getPost();
        readUnlock();
        return res;
    }

    public boolean isPreceding(Node node, Node preceding) {
        readLock();
        view.checkUpdate();
        return isFollowing(preceding, node);
    }

    public boolean isParent(Node node, Node parent) {
        readLock();
        view.checkUpdate();
        AbstractNode absNode = checkNode(node);
        checkNode(parent);
        PreNode preNode = absNode.getOriginalNode();
        boolean res = preNode.parent == parent;
        if (dhns.getSettingsManager().isAllowMultilevel() && !res) {
            CloneNode cn = preNode.getClones();
            while (cn != null) {
                res = res | cn.parent == parent;
            }
        }
        readUnlock();
        return res;
    }

    public int getHeight() {
        readLock();
        view.checkUpdate();
        int res = structure.getStructure().treeHeight - 1;
        readUnlock();
        return res;
    }

    public int getLevel(Node node) {
        readLock();
        view.checkUpdate();
        AbstractNode absNode = checkNode(node);
        int res = absNode.level - 1;
        readUnlock();
        return res;
    }

    public boolean expand(Node node) {
        AbstractNode absNode = checkNode(node);
        if (absNode.size == 0 || !absNode.isEnabled()) {
            return false;
        }
        dhns.getStructureModifier().expand(node);
        return true;
    }

    public boolean retract(Node node) {
        AbstractNode absNode = checkNode(node);
        if (absNode.size == 0 || absNode.isEnabled()) {
            return false;
        }
        dhns.getStructureModifier().retract(node);
        return true;
    }

    public void moveToGroup(Node node, Node nodeGroup) {
        checkNode(node);
        checkNode(nodeGroup);
        if (isDescendant(node, nodeGroup)) {
            throw new IllegalArgumentException("nodeGroup can't be a descendant of node");
        }
        dhns.getStructureModifier().moveToGroup(node, nodeGroup);
    }

    public void removeFromGroup(Node node) {
        AbstractNode absNode = checkNode(node);
        if (absNode.parent.parent == null) {   //Equal root
            throw new IllegalArgumentException("Node parent can't be the root of the tree");
        }
        dhns.getStructureModifier().moveToGroup(node, absNode.parent.parent);
    }

    public Node groupNodes(Node[] nodes) {
        if (nodes == null || nodes.length == 0) {
            throw new IllegalArgumentException("nodes can't be null or empty");
        }
        AbstractNode parent = null;
        for (int i = 0; i < nodes.length; i++) {
            AbstractNode node = checkNode(nodes[i]);
            if (parent == null) {
                parent = node.parent;
            } else if (parent != node.parent) {
                throw new IllegalArgumentException("All nodes must have the same parent");
            }
        }
        Node group = dhns.getStructureModifier().group(nodes);
        return group;
    }

    public void ungroupNodes(Node nodeGroup) {
        AbstractNode absNode = checkNode(nodeGroup);
        if (absNode.size == 0) {
            throw new IllegalArgumentException("nodeGroup can't be empty");
        }

        dhns.getStructureModifier().ungroup(absNode);
    }

    public boolean isInView(Node node) {
        readLock();
        view.checkUpdate();
        AbstractNode absNode = checkNode(node);
        boolean res = absNode.isEnabled();
        readUnlock();
        return res;
    }

    public void resetViewToLeaves() {
        dhns.getStructureModifier().resetViewToLeaves();
    }

    public void resetViewToLevel(int level) {
        readLock();
        view.checkUpdate();
        level += 1;     //Because we ignore the virtual root
        int height = structure.getStructure().treeHeight;
        if (level > height) {
            readUnlock();
            throw new IllegalArgumentException("Level must be between 0 and the height of the tree, currently height=" + (height - 1));
        }
        readUnlock();
        dhns.getStructureModifier().resetViewToLevel(level);
    }

    public void resetViewToTopNodes() {
        dhns.getStructureModifier().resetViewToTopNodes();
    }

    public EdgeIterable getHierarchyEdges() {
        readLock();
        view.checkUpdate();
        return dhns.newEdgeIterable(new HierarchyEdgeIterator(structure.getStructure(), new TreeListIterator(structure.getStructure().getTree(), 1)));
    }

    public void setVisible(Node node, boolean visible) {
        AbstractNode absNode = checkNode(node);
        writeLock();
        //absNode.setVisible(visible);
        writeUnlock();
    }

    public void setVisible(Edge edge, boolean visible) {
        AbstractEdge absEdge = checkEdge(edge);
        writeLock();
        //absEdge.setVisible(visible);
        writeUnlock();
    }
}
