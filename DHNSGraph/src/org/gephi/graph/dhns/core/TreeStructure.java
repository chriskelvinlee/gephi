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

import org.gephi.graph.dhns.node.AbstractNode;
import org.gephi.graph.dhns.node.PreNode;
import org.gephi.graph.dhns.node.iterators.TreeListIterator;

/**
 * Holds nodes tree and manage basic operations.
 *
 * @author Mathieu Bastian
 */
public class TreeStructure {

    DurableTreeList tree;
    AbstractNode root;
    public int treeHeight;
    private AbstractNode cacheNode;

    public TreeStructure() {
        this(0);
    }

    public TreeStructure(int treeCapacity) {
        tree = new DurableTreeList();
        initRoot();
    }

    private void initRoot() {
        root = new PreNode(-1, 0, 0, 0, null);
        root.setEnabled(false);
        tree.add(root);
    }

    public AbstractNode getNodeAt(int pre) {
        /*if(cacheNode!=null && cacheNode.avlNode.isConsistent() && cacheNode.pre == pre-1)
        {
        cacheNode = cacheNode.avlNode.next().getValue();
        return cacheNode;
        }

        cacheNode = tree.get(pre);
        return cacheNode;*/
        return tree.get(pre);
    }

    public AbstractNode getEnabledAncestorOrSelf(AbstractNode node) {
        AbstractNode parent = node;
        while (!parent.isEnabled()) {
            parent = parent.parent;
            if (parent == null || parent.getPre() == 0) {
                return null;
            }
        }
        return parent;
    }

    public AbstractNode getEnabledAncestor(AbstractNode node) {
        AbstractNode parent = node.parent;
        while (!parent.isEnabled()) {
            if (parent.getPre() == 0) {
                return null;
            }
            parent = parent.parent;
        }
        return parent;
    }

    public void insertAtEnd(AbstractNode node) {
        node.pre = tree.size();

        tree.add(node);
    }

    public void insertAsChild(AbstractNode node, AbstractNode parent) {
        node.parent = parent;
        node.pre = parent.getPre() + parent.size + 1;
        node.level = parent.level + 1;
        if (node.level > treeHeight) {
            treeHeight++;
        }

        tree.add(node.pre, node);
        incrementAncestorsSize(node);
    }

    public void move(AbstractNode node, AbstractNode newParent) {

        AbstractNode sourceParent = node.parent;
        int sourceSize = 1 + node.size;

        int maxLevel = tree.move(node.getPre(), newParent.getPre());

        if (sourceParent != null) {
            decrementAncestorAndSelfSize(sourceParent, sourceSize);
        }

        //Increment ancestor & self
        incrementAncestorsAndSelfSize(newParent, sourceSize);

        //Update tree height
        if (maxLevel > treeHeight) {
            treeHeight = maxLevel;
        }

    /* nodeSize = node.size;
    int nodePre = node.getPre();
    boolean forward = newParent.getPre()+newParent.size+1 > nodePre;

    //Move node itself
    decrementAncestorSize(node, 1);
    tree.removeAndKeepParent(nodePre);
    insertAsChild(node, newParent);
    node.size = 0;

    //showTreeAsTable();

    if(nodeSize>0) {
    //Move descendants
    for(int i=0;i<nodeSize;i++) {
    int descPre = nodePre;
    if(!forward) {
    descPre += i + 1;
    }
    decrementAncestorSize(node, 1);
    PreNode descendant = tree.removeAndKeepParent(descPre);
    //System.out.println("descendant "+descendant.getId());
    PreNode parent = descendant.parent;
    insertAsChild(descendant, parent);
    descendant.size = 0;
    }
    }*/
    }

    public void deleteAtPre(AbstractNode node) {
        int pre = node.getPre();
        tree.remove(pre);
        for (int i = 0; i < node.size; i++) {
            tree.remove(pre);
        }
    }

    public void deleteDescendantAndSelf(AbstractNode node) {
        decrementAncestorSize(node, node.size + 1);
        deleteAtPre(node);
    }

    public void incrementAncestorsSize(AbstractNode node) {
        incrementAncestorsSize(node, 1);
    }

    public void incrementAncestorsSize(AbstractNode node, int shift) {
        while (node.parent != null) {
            node = node.parent;
            node.size += shift;
            node.getPost();
        }
    }

    public void incrementAncestorsAndSelfSize(AbstractNode node, int shift) {
        while (node != null) {
            node.size += shift;
            node.getPost();
            node = node.parent;
        }
    }

    public void decrementAncestorSize(AbstractNode node, int shift) {
        while (node.parent != null) {
            node = node.parent;
            node.size -= shift;
            node.getPost();
        }
    }

    public void decrementAncestorAndSelfSize(AbstractNode node, int shift) {
        while (node != null) {
            node.size -= shift;
            node.getPost();
            node = node.parent;
        }
    }

    public boolean hasEnabledDescendant(AbstractNode node) {
        for (int i = node.getPre() + 1; i <= node.pre + node.size; i++) {
            AbstractNode descendant = tree.get(i);
            if (descendant.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    public void showTreeAsTable() {
        System.out.println("pre\tsize\tlevel\tparent\tpost\tenabled\tid\tclone");
        System.out.println("-----------------------------------------------------------------");

        int pre = 0;
        for (AbstractNode p : tree) {
            System.out.println(p.pre + "\t" + p.size + "\t" + p.level + "\t" + p.parent + "\t" + p.post + "\t" + p.isEnabled() + "\t" + p.getId() + "\t" + p.isClone());
            pre++;
        }
    }

    public void clear() {
        //Clean nodes
        for (TreeListIterator itr = new TreeListIterator(tree); itr.hasNext();) {
            AbstractNode preNode = itr.next();
            preNode.avlNode = null;
            preNode.parent = null;
        }
        tree.clear();
        root = null;
        treeHeight = 0;
        initRoot();
    }

    public int getTreeSize() {
        return tree.size();
    }

    public DurableTreeList getTree() {
        return tree;
    }

    public AbstractNode getRoot() {
        return root;
    }
}
