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
package org.gephi.data.network.tree.importer;

import org.gephi.data.network.tree.TreeStructure;
import org.gephi.data.network.node.PreNode;
import org.gephi.data.network.sight.Sight;

/**
 * Hierarchical graph importer. Must import the complete tree.
 * 
 * @author Mathieu Bastian
 */
public class CompleteTreeImporter {

    TreeStructure treeStructure;
    int currentLevel;
    PreNode currentParent;
    int currentSize;
    PreNode lastPos;
    int treeHeight;
    int currentPre = 0;
    Sight sight;

    public CompleteTreeImporter(TreeStructure tree, Sight sight) {
        this.treeStructure = tree;
        this.sight = sight;
    }

    /**
     * Create the (virtual) root of the tree and prepare import.
     */
    public void initImport() {
        PreNode root = new PreNode(0, 0, 0, null);
        treeStructure.insertAtEnd(root);
        treeStructure.setRoot(root);
        currentLevel = 1;
        currentParent = root;
        currentSize = 0;
        lastPos = root;
        treeHeight = 0;
    }

    /**
     * Go down into the tree and increase the level. Next <code>addSibling()</code> will be a child
     * of the current node.
     */
    public void addChild() {
        currentParent.size = currentSize;
        currentLevel++;
        currentParent = lastPos;
        currentSize = 0;
        treeHeight = Math.max(treeHeight, currentLevel);
    }

    /**
     * Add a new node to the current parent. Create the {@link PreNode} object.
     */
    public void addSibling(int node) {
        PreNode p = new PreNode(currentPre, 0, currentLevel, currentParent);

        //Insert
        treeStructure.insertAtEnd(p);
        currentSize++;
        lastPos = p;
        currentPre++;
    }

    /**
     * Go up into the tree and decrease the current level.
     */
    public void closeChild() {
        PreNode parent = currentParent;
        parent.size = currentSize;
        if (parent.parent != null) {
            PreNode parentParent = parent.parent;
            parentParent.size += currentSize;
            currentSize = parentParent.size;
            currentParent = parent.parent;
        }

        currentLevel--;
    }

    /**
     * Finish import.
     */
    public void endImport() {
        closeChild();
        treeStructure.treeHeight = treeHeight;

        for (PreNode p : treeStructure.getTree()) {
            p.getPost();
            if (p.size == 0) {
                p.setEnabled(sight, true);
            }
            p.addSight(sight);
        }
    }

    public void importGraph(int numSibling, boolean random) {
        initImport();

        int counter = 0;

        for (int i = 0; i < numSibling; i++) {
            counter++;

            addSibling(counter);
            addChild();

            int rand = numSibling;
            if (random) {
                rand = (int) (Math.random() * numSibling + 1);
            }

            for (int j = 0; j < rand; j++) {
                counter++;

                addSibling(counter);
                addChild();

                int rand2 = numSibling;
                if (random) {
                    rand2 = (int) (Math.random() * numSibling + 1);
                }

                for (int k = 0; k < rand2; k++) {
                    counter++;
                    addSibling(counter);
                }

                closeChild();
            }

            closeChild();
        }

        endImport();
    }

    public void shuffleEnable() {
        for (PreNode p : treeStructure.getTree()) {
            p.setEnabled(sight, false);
        }

        for (int i = 1; i < treeStructure.getTreeSize(); i++) {
            int enabled = (int) Math.round(Math.random());
            if (enabled == 0) {
                PreNode n = treeStructure.getNodeAt(i);
                n.setEnabled(sight, false);
            } else if (enabled == 1) {
                PreNode n = treeStructure.getNodeAt(i);
                n.setEnabled(sight, true);
                i += n.size;
            }
        }
    }
}
