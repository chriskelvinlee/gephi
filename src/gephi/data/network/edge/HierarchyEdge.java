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
package gephi.data.network.edge;

import java.util.LinkedList;

import gephi.data.network.edge.PreEdge.EdgeType;
import gephi.data.network.node.PreNode;

public class HierarchyEdge implements DytsEdge 
{
	protected PreNode preNodeFrom;
	protected PreNode preNodeTo;
	
	public HierarchyEdge(PreNode preNodeFrom, PreNode preNodeTo)
	{
		this.preNodeFrom = preNodeFrom;
		this.preNodeTo = preNodeTo;
	}
	
	@Override
	public PreNode getPreNodeFrom() {
		return preNodeFrom;
	}
	
	@Override
	public void setPreNodeFrom(PreNode preNodeFrom) {
		this.preNodeFrom = preNodeFrom;
	}
	
	@Override
	public PreNode getPreNodeTo() {
		return preNodeTo;
	}
	
	@Override
	public void setPreNodeTo(PreNode preNodeTo) {
		this.preNodeTo = preNodeTo;
	}
}
