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
package org.gephi.importer.api;

import java.awt.Color;
import org.gephi.data.attributes.api.AttributeColumn;

/**
 *
 * @author Mathieu Bastian
 */
public interface EdgeDraft {

    public void setCardinal(float cardinal);

    public void setColor(Color color);

    public void setColor(String r, String g, String b);

    public void setColor(float r, float g, float b);

    public void setColor(int r, int g, int b);

    public void setLabelVisible(boolean labelVisible);

    public void setLabel(String label);

    public void setVisible(boolean visible);

    public void setDirected(boolean directed);

    public void setId(String id);

    public void setNodeSource(NodeDraft nodeSource);

    public void setNodeTarget(NodeDraft nodeTarget);

    public void addAttributeValue(AttributeColumn column, Object value);
}
