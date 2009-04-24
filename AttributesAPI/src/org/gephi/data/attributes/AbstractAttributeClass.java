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
package org.gephi.data.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeClass;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeType;

/**
 *
 * @author Mathieu Bastian
 */
public abstract class AbstractAttributeClass implements AttributeClass {

    protected String name;

    //Columns
    protected List<AttributeColumnImpl> columns = new ArrayList<AttributeColumnImpl>();
    protected Map<String, AttributeColumnImpl> columnsMap = new HashMap<String, AttributeColumnImpl>();

    public AbstractAttributeClass(String name) {
        this.name = name;
    }

    public abstract String getName();

    public synchronized AttributeColumn[] getAttributeColumns() {
        return columns.toArray(new AttributeColumnImpl[]{});
    }

    public synchronized int countAttributeColumns() {
        return columns.size();
    }

    public void addAttributeColumn(String id, AttributeType type) {
        addAttributeColumn(id, id, type, AttributeOrigin.DATA, null);
    }

    public void addAttributeColumn(String id, AttributeType type, AttributeOrigin origin) {
        addAttributeColumn(id, id, type, origin, null);
    }

    public synchronized void addAttributeColumn(String id, String title, AttributeType type, AttributeOrigin origin, Object defaultValue) {
        AttributeColumnImpl column = new AttributeColumnImpl(columns.size(), id, title, type, origin, defaultValue);
        columns.add(column);
        columnsMap.put(id, column);
        if (title != null && !title.equals(id)) {
            columnsMap.put(title, column);
        }
    }

    public synchronized AttributeColumn getAttributeColumn(int index) {
        if (index >= 0 && index < columns.size()) {
            return columns.get(index);
        }

        return null;
    }

    public synchronized AttributeColumn getAttributeColumn(String id) {
        return columnsMap.get(id);
    }

    public synchronized AttributeColumn getAttributeColumn(String title, AttributeType type) {
        AttributeColumn c = columnsMap.get(title);
        if (c != null && c.getAttributeType().equals(type)) {
            return c;
        }
        return null;
    }

    public synchronized boolean hasAttributeColumn(String title) {
        return columnsMap.containsKey(title);
    }

    public Object getManagedValue(Object object, AttributeType type)
    {
        return object;
    }
}
