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

package org.gephi.data.network.sight;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.gephi.data.network.Dhns;
import org.gephi.data.network.DhnsController;
import org.gephi.data.network.tree.TreeStructure;
import org.gephi.data.network.edge.VirtualEdge;
import org.gephi.data.network.node.PreNode;
import org.gephi.data.network.node.treelist.SingleTreeIterator;
import org.gephi.data.network.utils.CachedIterator;
import org.gephi.data.network.edge.iterators.EdgesOutIterator;


/**
 *
 * @author Mathieu Bastian
 */
public class SightCache {

    private Sight sight;
    private Dhns dhns;

    //States
    private AtomicBoolean resetNode         = new AtomicBoolean(true);
    private AtomicBoolean resetEdge         = new AtomicBoolean(true);

    //Cache
    private List<PreNode> nodeCache         = new ArrayList<PreNode>();
    private List<VirtualEdge> edgeCache     = new ArrayList<VirtualEdge>();
    
    public SightCache(Dhns dhns, Sight sight)
    {
        this.dhns = dhns;
        this.sight = sight;
    }

    public Iterator<PreNode> getNodeIterator()
    {
        TreeStructure treeStructure = dhns.getTreeStructure();
        if(resetNode.getAndSet(false))
		{
			nodeCache.clear();
            SingleTreeIterator treeIterator = new SingleTreeIterator(treeStructure, sight);
            CachedIterator<PreNode> cacheIterator = new CachedIterator<PreNode>(treeIterator,nodeCache);
			return cacheIterator;
		}

		return nodeCache.iterator();
    }

    public Iterator<VirtualEdge> getEdgeIterator()
    {
        TreeStructure treeStructure = dhns.getTreeStructure();
        if(resetEdge.getAndSet(false))
		{
			edgeCache.clear();
            EdgesOutIterator edgesIterator = new EdgesOutIterator(treeStructure, sight);
            CachedIterator<VirtualEdge> cacheIterator = new CachedIterator<VirtualEdge>(edgesIterator,edgeCache);
			return cacheIterator;
		}

		return edgeCache.iterator();
    }

    public boolean requireNodeUpdate()
    {
        return resetNode.get();
    }


    public boolean requireEdgeUpdate()
    {
        return resetEdge.get();
    }

    public void reset()
    {
        resetNode.set(true);
        resetEdge.set(true);
    }
}
