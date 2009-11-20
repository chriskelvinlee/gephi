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
package org.gephi.visualization.opengl.text;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.graph.api.EdgeData;
import org.gephi.graph.api.NodeData;
import org.gephi.graph.api.TextData;
import org.gephi.graph.api.TextDataFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = TextDataFactory.class)
public class TextDataBuilderImpl implements TextDataFactory {

    public TextData newTextData() {
        return new TextDataImpl();
    }

    public void buildNodeText(NodeData nodeData, TextDataImpl textDataImpl, TextModel model) {
        if (model.getNodeTextColumns() != null) {
            String str = "";
            int i = 0;
            for (AttributeColumn c : model.getNodeTextColumns()) {
                if (i++ > 0) {
                    str += " - ";
                }
                str += nodeData.getAttributes().getValue(c.getIndex());
            }
            textDataImpl.setLine(str);
        }
    }

    public void buildEdgeText(EdgeData edgeData, TextDataImpl textDataImpl, TextModel model) {
        if (model.getEdgeTextColumns() != null) {
            String str = "";
            int i = 0;
            for (AttributeColumn c : model.getEdgeTextColumns()) {
                if (i++ > 0) {
                    str += " - ";
                }
                str += edgeData.getAttributes().getValue(c.getIndex());
            }
            textDataImpl.setLine(str);
        }
    }
}
