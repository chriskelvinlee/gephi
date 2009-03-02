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
package gephi.data.network.avl.param;



import gephi.data.network.avl.ResetableIterator;

import java.util.Iterator;

/**
 * Iterator for the {@link ParamAVLTree}. Return items in an ascending order.
 * 
 * @author Mathieu Bastian
 * @param <Item> The type of Object in the tree
 */
public class ParamAVLIterator<Item> implements Iterator<Item>, ResetableIterator {

	private ParamAVLNode<Item> next;
	private Item current;
	
	public ParamAVLIterator()
	{
		
	}
	
	public ParamAVLIterator(ParamAVLNode node)
	{
		this.next = node;
		goToDownLeft();
	}
	
	public ParamAVLIterator(ParamAVLTree tree)
	{
		this(tree.root);
	}
	
	
	public void setNode(ParamAVLTree tree)
	{
		this.next = tree.root;
		goToDownLeft();
	}
	
	private void goToDownLeft()
	{
         if (next != null)
         {
             while (next.left != null)
             {
            	 next = next.left;
             }
         }
	}
	
	public boolean hasNext() 
	{
		if (next == null)
		{
			return false;
		}

		current = this.next.item;

		if (next.right == null)
		{
			while ((next.parent != null) && (next == next.parent.right))
			{
				this.next = this.next.parent;
			}

			this.next = this.next.parent;
		}
		else
		{
			this.next = this.next.right;

			while (this.next.left != null)
			{
				this.next = this.next.left;
			}
		}

		return true;
	}
	
	public Item next() {
		return current;
	}
	
	public void remove() {
		throw new UnsupportedOperationException();	
	}
}
