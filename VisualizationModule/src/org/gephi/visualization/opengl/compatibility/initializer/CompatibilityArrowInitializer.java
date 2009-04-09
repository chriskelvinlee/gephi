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
package org.gephi.visualization.opengl.compatibility.initializer;

import org.gephi.visualization.api.initializer.CompatibilityObject3dInitializer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.JPanel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Renderable;
import org.gephi.visualization.opengl.AbstractEngine;
import org.gephi.visualization.api.Object3dImpl;
import org.gephi.visualization.opengl.compatibility.CompatibilityEngine;
import org.gephi.visualization.opengl.compatibility.objects.Arrow3dObject;

/**
 *
 * @author Mathieu Bastian
 */
public class CompatibilityArrowInitializer implements CompatibilityObject3dInitializer<Node> {

    private CompatibilityEngine engine;

    public CompatibilityArrowInitializer(AbstractEngine engine) {
        this.engine = (CompatibilityEngine) engine;
    }

    @Override
    public Object3dImpl initObject(Renderable n) {
        Edge e = (Edge) n;

        Arrow3dObject arrow = new Arrow3dObject(e);
        arrow.setObj(e.getTarget());

        return arrow;
    }

    public void chooseModel(Object3dImpl obj) {
        float distance = engine.cameraDistance(obj) + obj.getObj().getRadius();	//Radius is added to cancel the cameraDistance diff
        if (distance < 100) {
            obj.mark = false;
        } else {
            obj.mark = true;
        }
    }

    public int initDisplayLists(GL gl, GLU glu, GLUquadric quadric, int ptr) {
        return ptr;
    }

    public void initFromOpenGLThread() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JPanel getPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
