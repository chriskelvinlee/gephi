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
package org.gephi.data.network.tree;

import org.gephi.data.network.node.PreNode;
import org.gephi.data.network.node.treelist.PreNodeTreeList;
import org.gephi.data.network.sight.SightImpl;

public class TreeStructure {

    PreNodeTreeList tree;
    PreNode root;
    public int treeHeight;
    private PreNode cacheNode;

    public TreeStructure() {
        tree = new PreNodeTreeList();
    }

    public TreeStructure(int treeCapacity) {
        tree = new PreNodeTreeList();
    }

    public PreNode getNodeAt(int pre) {
        /*if(cacheNode!=null && cacheNode.avlNode.isConsistent() && cacheNode.pre == pre-1)
        {
        cacheNode = cacheNode.avlNode.next().getValue();
        return cacheNode;
        }

        cacheNode = tree.get(pre);
        return cacheNode;*/
        return tree.get(pre);
    }

    public PreNode getEnabledAncestorOrSelf(PreNode node, SightImpl sight) {
        PreNode parent = node;
        while (!parent.isEnabled(sight)) {
            parent = parent.parent;
            if (parent == null || parent.pre == 0) {
                return null;
            }
        }
        return parent;
    }

    public PreNode getEnabledAncestor(PreNode node, SightImpl sight) {
        PreNode parent = node.parent;
        while (!parent.isEnabled(sight)) {
            if (parent.pre == 0) {
                return null;
            }
            parent = parent.parent;
        }
        return parent;
    }

    public void insertAtEnd(PreNode node) {
        node.pre = tree.size();

        tree.add(node);
    }

    public void insertAsChild(PreNode node, PreNode parent) {
        node.parent = parent;
        node.pre = parent.pre + parent.size + 1;
        node.level = parent.level + 1;
        if (node.level > treeHeight) {
            treeHeight++;
        }

        tree.add(node.pre, node);
        incrementAncestorsSize(node);
    }

    public void deleteAtPre(PreNode node) {
        int pre = node.getPre();
        tree.remove(pre);
        for (int i = 0; i < node.size; i++) {
            tree.remove(pre);
        }

    }

    public void deleteDescendantAndSelf(PreNode node) {
        deleteAtPre(node);
        decrementAncestorSize(node, node.size + 1);
    }

    public void incrementAncestorsSize(PreNode node) {
        while (node.parent != null) {
            node = node.parent;
            node.size++;
            node.getPost();
        }
    }

    public void decrementAncestorSize(PreNode node, int shift) {
        while (node.parent != null) {
            node = node.parent;
            node.size -= shift;
            node.getPost();
        }
    }

    public void resetAllEnabled() {
        for (PreNode p : tree) {
            p.setAllEnabled(false);
        }
    }

    public void showTreeAsTable() {
        System.out.println("pre\tsize\tlevel\tparent\tpost\tpreTrace");
        System.out.println("-------------------------------------------------------");

        int pre = 0;
        for (PreNode p : tree) {
            System.out.println(p.pre + "\t" + p.size + "\t" + p.level + "\t" + p.parent + "\t" + p.post + "\t" + p.preTrace);
            pre++;
        }
    }

    public int getTreeSize() {
        return tree.size();
    }

    public PreNodeTreeList getTree() {
        return tree;
    }

    public PreNode getRoot() {
        return root;
    }

    public void setRoot(PreNode root) {
        this.root = root;
    }
}
