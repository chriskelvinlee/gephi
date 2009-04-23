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
package org.gephi.data.network.api;

import org.gephi.graph.api.NodeWrap;
import org.gephi.graph.api.EdgeWrap;
import java.util.Iterator;
import org.gephi.graph.api.Potato;

/**
 *
 * @author Mathieu Bastian
 */
public interface AsyncReader {

    public Iterator<? extends NodeWrap> getNodes();

    public Iterator<? extends EdgeWrap> getEdges();

    public Iterator<? extends Potato> getPotatoes();

    public void setUpdated();

    public boolean requireUpdate();

    public boolean requireNodeUpdate();

    public boolean requireEdgeUpdate();

    public boolean requirePotatoUpdate();
}
