/*
Copyright 2010 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke, Patrick J. McSweeney
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
package org.gephi.timeline.api;

import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.project.api.Workspace;

/**
 *
 * @author Julian Bilcke <julian.bilcke@gmail.com>
 */
public interface TimelineController {

    public void pushSlice(Workspace workspace, String from, String to, Node node);
    public void pushSlice(Workspace workspace, String from, String to, Edge edge);
    public TimelineModel getModel(Workspace workspace);
    public double getFrom(Workspace workspace);
    public double getTo(Workspace workspace);
    public void setMin(Workspace workspace, String min);
    public void setMax(Workspace workspace, String max);
    public void setMin(Workspace workspace, double min);
    public void setMax(Workspace workspace, double max);
    public TimeInterval getTimeInterval(Workspace workspace);

    // extends methods (will use the current workspace)
    public TimelineModel getModel();
    public TimeInterval getTimeInterval();
    public double getFrom();
    public double getTo();
    public void setMin(String min);
    public void setMax(String max);
    public void setMin(double min);
    public void setMax(double max);
}
