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
package org.gephi.data.network;

import org.gephi.data.network.api.FreeModifier;
import org.gephi.data.network.tree.TreeStructure;
import org.gephi.data.network.config.DHNSConfig;
import org.gephi.data.network.edge.PreEdge;
import org.gephi.data.network.mode.FreeMode;
import org.gephi.data.network.node.PreNode;
import org.gephi.data.network.sight.SightImpl;
import org.gephi.data.network.sight.SightManager;
import org.gephi.data.network.tree.importer.CompleteTreeImporter;
import org.gephi.data.network.utils.RandomEdgesGenerator;

public class Dhns {

    private DHNSConfig config;
    private TreeStructure treeStructure;
    private FreeMode freeMode;
    private SightManager sightManager;

    public Dhns() {
        config = new DHNSConfig();
        treeStructure = new TreeStructure();
        sightManager = new SightManager(this);
        freeMode = new FreeMode(this);
    }

    public void init(SightImpl sight) {
        importFakeGraph();
        treeStructure.showTreeAsTable();
    }

    private void importFakeGraph() {
        CompleteTreeImporter importer = new CompleteTreeImporter(treeStructure, sightManager.getMainSight());

        importer.importGraph(5, true);
        importer.shuffleEnable();
        System.out.println("Tree size : " + treeStructure.getTreeSize());
        RandomEdgesGenerator reg = new RandomEdgesGenerator(treeStructure);
        reg.generatPhysicalEdges(20);
        freeMode.init();
    }


    

    public void deleteNode(PreNode node) {
        /*PreNode enabledAncestor = treeStructure.getEnabledAncestor(node);
        ParamAVLIterator<PreEdge> iterator=null;

        int nodeSize = node.getPre()+node.size;
        for(int i=node.pre;i<=nodeSize;i++)		//Children & Self
        {
        PreNode child = treeStructure.getNodeAt(i);
        if(enabledAncestor==null)
        {
        //if(child.enabled)
        //	edgeProcessing.clearVirtualEdges(child, sight);		//Clear virtual edges
        //edgeProcessing.clearPhysicalEdges(child);
        }
        else
        {
        boolean hasBackwardEdges = child.countBackwardEdges() > 0;
        boolean hasForwardEdges = child.countForwardEdges() > 0;

        if(iterator==null && (hasForwardEdges || hasBackwardEdges))
        iterator = new ParamAVLIterator<PreEdge>();

        //Delete Backward edges
        if(hasBackwardEdges)
        {
        iterator.setNode(child.getBackwardEdges());
        for(;iterator.hasNext();)
        {
        PreEdge edge = iterator.next();
        delEdge(edge);
        }
        }

        //Delete Forward edges
        if(hasForwardEdges)
        {
        iterator.setNode(child.getForwardEdges());
        for(;iterator.hasNext();)
        {
        PreEdge edge = iterator.next();
        delEdge(edge);
        }
        }
        }
        }

        treeStructure.deleteDescendantAndSelf(node);*/
    }

    /*public void deleteNodes(PreNode[] nodes)
    {
    for(PreNode node : nodes)
    {
    if(node.preTrace!=1)
    {
    boolean childOrSelfEnabled=false;

    if(node.size > 0)
    {
    int nodeSize = node.pre+node.size;
    for(int i=node.pre+1;i<=nodeSize;i++)		//Children
    {
    PreNode child = treeStructure.getNodeAt(i);
    if(child.enabled)
    {
    edgeProcessing.clearVirtualEdges(child);		//Clear virual edges
    childOrSelfEnabled=true;
    }

    edgeProcessing.clearPhysicalEdges(child);
    child.preTrace=1;
    }
    }

    if(node.enabled)
    {
    edgeProcessing.clearVirtualEdges(node);
    childOrSelfEnabled=true;
    }

    edgeProcessing.clearPhysicalEdges(node);
    treeStructure.deleteDescendantAndSelf(node);

    if(!childOrSelfEnabled)
    {
    PreNode parent = treeStructure.getEnabledAncestor(node);
    if(parent!=null)
    {
    //Reprocess parent
    edgeProcessing.clearVirtualEdges(parent);
    PreNodeAVLTree nodeToReprocess = new PreNodeAVLTree();
    edgeProcessing.appendEdgeHostingNeighbours(parent, nodeToReprocess, parent.pre);
    edgeProcessing.reprocessInducedEdges(nodeToReprocess, parent);
    edgeProcessing.processLocalInducedEdges(parent);
    }
    }

    node.preTrace=1;
    }
    }
    }*/
    public void addNode(PreNode node) //We assume parent is well defined
    {
        treeStructure.insertAsChild(node, node.parent);

    }

    public void addNodes(PreNode[] nodes) {
    }

    public void addEdge(PreEdge edge) {
        /*PreNode minNode = edge.minNode;
        PreNode maxNode = edge.maxNode;

        //Add physical edges
        minNode.getForwardEdges().add(edge);
        maxNode.getBackwardEdges().add(edge);

        //Get nodes' parent
        PreNode minParent = treeStructure.getEnabledAncestorOrSelf(minNode);
        PreNode maxParent = treeStructure.getEnabledAncestorOrSelf(maxNode);

        if(minParent!=null && maxParent!=null && minParent!=maxParent)
        {
        DhnsEdge dhnsEdge = minParent.getVirtualEdge(edge, maxParent.getPre());
        if(dhnsEdge!=null)
        {
        VirtualEdge virtualEdge = (VirtualEdge)dhnsEdge;
        virtualEdge.addPhysicalEdge(edge);
        }
        else
        {
        //Create the virtual edge
        edgeProcessing.createVirtualEdge(edge, minParent, maxParent);
        }
        }*/
    }

    

    public TreeStructure getTreeStructure() {
        return treeStructure;
    }

    public SightManager getSightManager()
    {
        return sightManager;
    }

    public DHNSConfig getConfig()
    {
        return config;
    }

    public FreeModifier getFreeModifier()
    {
        return freeMode;
    }
}
