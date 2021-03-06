/*
Copyright 2008-2010 Gephi
Authors : Jeremy Subtil <jeremy.subtil@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gephi.preview.propertyeditors;

import java.awt.Component;

/**
 *
 * @author jeremy
 */
public class EdgeColorizerPropertyEditor extends GenericColorizerPropertyEditor {

    @Override
    public boolean supportsEdgeB1ColorMode() {
        return true;
    }

    @Override
    public boolean supportsEdgeB2ColorMode() {
        return true;
    }

    @Override
    public boolean supportsEdgeBothBColorMode() {
        return true;
    }

    @Override
    public boolean supportEdgeOriginalColorMode() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        ColorModePanel p;
        p = (ColorModePanel) super.getCustomEditor();
        p = new EdgeOriginalColorModePanelDecorator(this, p);
        p = new EdgeBothBColorModePanelDecorator(this, p);
        p = new EdgeB2ColorModePanelDecorator(this, p);
        p = new EdgeB1ColorModePanelDecorator(this, p);
        
        return p;
    }
}
