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

package gephi.visualization.opengl.compatibility.objects;

import gephi.data.network.Edge;
import gephi.data.network.Node;
import gephi.visualization.VizController;
import gephi.visualization.opengl.Object3d;
import gephi.visualization.opengl.gleem.linalg.Vec3d;
import gephi.visualization.opengl.gleem.linalg.Vec3f;
import gephi.visualization.opengl.octree.Octant;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 *
 * @author Mathieu
 */
public class Arrow3dObject extends Object3d<Node> {

    private static float ARROW_WIDTH=1.5f;
    private static float ARROW_HEIGHT=4.5f;
	private Edge edge;
    private Vec3f cameraVector;

    public Arrow3dObject()
    {
        super();
        cameraVector = VizController.getInstance().getDrawable().getCameraVector();
    }

	public Arrow3dObject(Edge edge)
	{
        this();
		this.edge = edge;
	}

    @Override
    public void display(GL gl, GLU glu) {
		Node nodeFrom = edge.getSource();
		Node nodeTo = edge.getTarget();

		Vec3f edgeVector = new Vec3f(nodeTo.x - nodeFrom.x,nodeTo.y - nodeFrom.y,nodeTo.z - nodeFrom.z);
        edgeVector.normalize();
		Vec3f sideVector = edgeVector.cross(cameraVector);
        sideVector.normalize();

		double angle = Math.atan2(nodeTo.y-nodeFrom.y,nodeTo.x-nodeFrom.x);
		float collisionDistance = nodeTo.getObject3d().getCollisionDistance(angle);

        float x2 = nodeTo.x;
        float y2 = nodeTo.y;
        float z2 = nodeTo.z;

        float targetX = x2 - edgeVector.x()*collisionDistance;
        float targetY = y2 - edgeVector.y()*collisionDistance;
        float targetZ = z2 - edgeVector.z()*collisionDistance;

        float baseX = targetX - edgeVector.x()*ARROW_HEIGHT;
        float baseY = targetY - edgeVector.y()*ARROW_HEIGHT;
        float baseZ = targetZ - edgeVector.z()*ARROW_HEIGHT;

        gl.glColor4f(edge.r, edge.g, edge.b, edge.a);
        
        gl.glVertex3d(baseX+sideVector.x()*ARROW_WIDTH, baseY+sideVector.y()*ARROW_WIDTH, baseZ+sideVector.z()*ARROW_WIDTH);
		gl.glVertex3d(baseX-sideVector.x()*ARROW_WIDTH, baseY-sideVector.y()*ARROW_WIDTH, baseZ-sideVector.z()*ARROW_WIDTH);
		gl.glVertex3d(targetX, targetY, targetZ);
	}

	@Override
	public float getCollisionDistance(double angle) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isInOctreeLeaf(Octant leaf)
	{
		return obj.getObject3d().getOctant()==leaf;
	}

	@Override
	public int[] octreePosition(float centerX, float centerY, float centerZ,
			float size) {
		return null;
	}

	@Override
	public boolean selectionTest(Vec3f distanceFromMouse, float selectionSize) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toSVG() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOctant(Octant octant) {
		
	}

	@Override
	public Octant getOctant() {
		return obj.getObject3d().getOctant();
	}

}
