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

package org.gephi.data.network.utils.avl;

import org.gephi.datastructure.avl.param.AVLItemAccessor;
import org.gephi.datastructure.avl.param.ParamAVLTree;

/**
 *
 * @author Mathieu Bastian
 */
public class DhnsEdgeSightTree extends ParamAVLTree<DhnsEdgeTree> {

    public DhnsEdgeSightTree() {
        super();
        setAccessor(new DhnsEdgeSightAVLItemAccessor());
    }

    private class DhnsEdgeSightAVLItemAccessor implements AVLItemAccessor<DhnsEdgeTree> {

        @Override
        public int getNumber(DhnsEdgeTree item) {
            return item.getNumber();
        }
    }
}
