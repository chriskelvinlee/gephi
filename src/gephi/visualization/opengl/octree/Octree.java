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

package gephi.visualization.opengl.octree;

import com.sun.opengl.util.BufferUtil;
import gephi.data.network.avl.ResetableIterator;
import gephi.data.network.avl.param.AVLItemAccessor;
import gephi.data.network.avl.param.ParamAVLTree;
import gephi.data.network.avl.simple.SimpleAVLTree;
import gephi.visualization.opengl.Object3d;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.media.opengl.GL;

/**
 *
 * @author Mathieu
 */
public class Octree
{
    //Static
    public static byte CLASS_0 = 0;
	public static byte CLASS_1 = 1;
	public static byte CLASS_2 = 2;
	public static byte CLASS_3 = 3;

    //Attributes
    private int objectsIDs;
    private int maxDepth;
    private int classesCount;

    //Octant
	private Octant root;
    private ParamAVLTree<Octant> leaves;

    //Iterator
    private List<OctreeIterator> objectIterators;

    //States
    private List<Octant> visibleLeaves;

    public Octree(int maxDepth, int size, int nbClasses)
    {
        this.maxDepth = maxDepth;
        this.classesCount = nbClasses;

        leaves = new ParamAVLTree<Octant>(new AVLItemAccessor<Octant>()
        {
            public int getNumber(Octant item) {
                return item.getNumber();
            }
        });
        visibleLeaves = new ArrayList<Octant>();

        objectIterators = new ArrayList<OctreeIterator>(nbClasses);
        for(int i=0; i< nbClasses; i++)
            objectIterators.add(new OctreeIterator(visibleLeaves, i));

        float dis = size/(float)Math.pow(2, maxDepth+1);
		root = new Octant(this, 0,dis, dis, dis, size);
    }

    public void addObject(int classID, Object3d obj)
    {
        root.addObject(classID, obj);
    }

    public void removeObject(int classID, Object3d obj)
    {
        root.removeObject(classID, obj);
    }

    public void updateVisibleOctant(GL gl)
	{
		//Switch to OpenGL select mode
		int capacity = 1*4*leaves.getCount();      //Each object take in maximium : 4 * name stack depth
		IntBuffer hitsBuffer = BufferUtil.newIntBuffer(capacity);
		gl.glSelectBuffer(hitsBuffer.capacity(), hitsBuffer);
		gl.glRenderMode(GL.GL_SELECT);
		gl.glInitNames();
		gl.glPushName(0);

		//Draw the nodes cube in the select buffer
		for(Octant n : leaves)
		{
			gl.glLoadName(n.getNumber());
			n.displayOctreeNode(gl);
		}
		int nbRecords = gl.glRenderMode(GL.GL_RENDER);

		visibleLeaves.clear();

		//Get the hits and add the nodes' objects to the array
		for(int i=0; i< nbRecords; i++)
		{
			int hit = hitsBuffer.get(i*4+3); 		//-1 Because of the glPushName(0)

			Octant nodeHit = leaves.getItem(hit);
			visibleLeaves.add(nodeHit);
		}

	}

    public Iterator<Object3d> getObjectIterator(int classID)
	{
		OctreeIterator itr = objectIterators.get(classID);
        itr.reset();
        return itr;
	}

    public void displayOctree(GL gl)
	{
		gl.glColor3i(0, 0, 0);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		root.displayOctreeNode(gl);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

	}

    void addLeaf(Octant leaf)
    {
        leaves.add(leaf);
    }

    void removeLeaf(Octant leaf)
    {
        leaves.add(leaf);
    }

    int getClassesCount() {
        return classesCount;
    }
    
    int getMaxDepth()
    {
        return maxDepth;
    }

    int getNextObjectID()
    {
        return objectsIDs++;
    }

    private static class OctreeIterator implements Iterator<Object3d>, ResetableIterator
    {
        private int i;
        private int classID;
        private List<Octant> visibleLeaves;
        private Iterator<Object3d> currentIterator;

        public OctreeIterator(List<Octant> visibleLeaves, int classID)
        {
            this.visibleLeaves = visibleLeaves;
            this.classID = classID;
        }

        public void reset()
        {
            currentIterator=null;
            i=0;
        }

        public boolean hasNext() {
           if(currentIterator==null || !currentIterator.hasNext())
           {
               if(i<visibleLeaves.size())
               {
                    currentIterator = visibleLeaves.get(i).iterator(classID);
                    i++;
                    if(currentIterator.hasNext())
                        return true;
               }
               return false;
           }
           return true;
        }

        public Object3d next() {
            return currentIterator.next();
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
