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
package org.gephi.graph.dhns.core;

import org.gephi.graph.api.View;
import org.gephi.graph.api.Views;
import org.gephi.graph.dhns.views.ViewImpl;

/**
 *
 * @author Mathieu Bastian
 */
public class ViewManager implements Views {

    private Dhns dhns;
    private ViewImpl visibleView;

    public ViewManager(Dhns dhns) {
        this.dhns = dhns;
        visibleView = new ViewImpl(dhns);
    }

    public View getVisibleView() {
        return visibleView;
    }

    public View createView(View view) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setVisibleView(View view) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
