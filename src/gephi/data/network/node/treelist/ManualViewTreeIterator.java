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
package gephi.data.network.node.treelist;

import gephi.data.network.TreeStructure;
import gephi.data.network.node.PreNode;

import java.util.Iterator;

public class ManualViewTreeIterator implements Iterator<PreNode> 
{
	protected int treeSize;
	protected PreNodeTreeList treeList;
	protected PreNode next;
	protected int nextIndex = 0;
	protected PreNode pointer;
	
	public ManualViewTreeIterator(TreeStructure treeStructure)
	{
		this.treeList = treeStructure.getTree();
		treeSize = treeList.size();
		pointer = treeList.get(0);
	}
	
	@Override
	public boolean hasNext()
	{
		if(nextIndex >= treeSize)
			return false;
		
		while(nextIndex < treeSize && !pointer.enabled)
		{
			pointer = pointer.avlNode.next().value;
			nextIndex++;
		}
		return true;
	}
	
	@Override
	public PreNode next()
	{
		return pointer;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
