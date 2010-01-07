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
package org.gephi.filters.impl;

import java.beans.PropertyEditorManager;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.FilterModel;
import org.gephi.filters.api.Query;
import org.gephi.filters.api.Range;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.filters.spi.Operator;

/**
 *
 * @author Mathieu Bastian
 */
public class FilterControllerImpl implements FilterController {

    private FilterModelImpl model;

    public FilterControllerImpl() {
        this.model = new FilterModelImpl();

        //Register range editor
        PropertyEditorManager.registerEditor(Range.class, RangePropertyEditor.class);
    }

    public Query createQuery(Filter filter) {
        if (filter instanceof Operator) {
            return new OperatorQueryImpl((Operator) filter);
        }
        return new FilterQueryImpl(filter);
    }

    public void add(Query query) {
        if (!model.hasQuery(query)) {
            model.addFirst(query);
        }
        model.setCurrentQuery(query);
    }

    public void remove(Query query) {
        model.remove(query);
    }

    public void rename(Query query, String name) {
        model.rename(query, name);
    }

    public void setSubQuery(Query query, Query subQuery) {
        model.setSubQuery(query, subQuery);
    }

    public void removeSubQuery(Query query, Query parent) {
        model.removeSubQuery(query, parent);
    }

    public FilterModel getModel() {
        return model;
    }

    public void propertyChanged(FilterProperty property) {
        model.propertyChanged(property);
    }
}
