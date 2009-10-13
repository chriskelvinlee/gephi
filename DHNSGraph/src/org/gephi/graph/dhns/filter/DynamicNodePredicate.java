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
package org.gephi.graph.dhns.filter;

import org.gephi.graph.api.DynamicData;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodePredicate;

/**
 *
 * @author Mathieu Bastian
 */
public class DynamicNodePredicate implements NodePredicate {

    private float from = 0;
    private float to = 1;

    public DynamicNodePredicate(float from, float to) {
        this.from  = from;
        this.to = to;
    }

    public boolean evaluate(Node element) {
        //Check if element is in the range
        DynamicData dd = element.getNodeData().getDynamicData();
        if (dd.getRangeFrom() == -1 || dd.getRangeTo() == -1) {
            return true;
        }
        return dd.getRangeFrom() >= from && dd.getRangeTo() <= to;
    }
}
