/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.datalaboratory.impl;

import java.math.BigDecimal;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.api.AttributeUtils;
import org.gephi.datalaboratory.api.AttributeColumnsMergeStrategiesController;
import org.gephi.datalaboratory.api.AttributeColumnsController;
import org.gephi.datalaboratory.api.utils.MathUtils;
import org.gephi.graph.api.Attributes;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Implementation of the AttributeColumnsMergeStrategiesController interface
 * declared in the Data Laboratory API.
 * @author Eduardo Ramos <eduramiba@gmail.com>
 * @see AttributeColumnsMergeStrategiesController
 */
@ServiceProvider(service = AttributeColumnsMergeStrategiesController.class)
public class AttributeColumnsMergeStrategiesControllerImpl implements AttributeColumnsMergeStrategiesController {

    public AttributeColumn joinWithSeparatorMerge(AttributeTable table, AttributeColumn[] columnsToMerge, String newColumnTitle, String separator) {
        if (table == null || columnsToMerge == null) {
            throw new IllegalArgumentException("Table or columns can't be null");
        }

        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        AttributeColumn newColumn;
        newColumn = ac.addAttributeColumn(table, newColumnTitle, AttributeType.STRING);//Create as STRING column by default. Then it can be duplicated to other type.
        final int newColumnIndex = newColumn.getIndex();

        if (separator == null) {
            separator = "";
        }

        Object value;
        StringBuilder sb;
        final int columnsCount = columnsToMerge.length;

        for (Attributes row : ac.getTableAttributeRows(table)) {
            sb = new StringBuilder();
            for (int i = 0; i < columnsCount; i++) {
                value = row.getValue(columnsToMerge[i].getIndex());
                if (value != null) {
                    sb.append(value.toString());
                    if (i < columnsCount - 1) {
                        sb.append(separator);
                    }
                }
            }
            row.setValue(newColumnIndex, sb.toString());
        }

        return newColumn;
    }

    public AttributeColumn booleanLogicOperationsMerge(AttributeTable table, AttributeColumn[] columnsToMerge, BooleanOperations[] booleanOperations, String newColumnTitle) {
        AttributeUtils attributeUtils = AttributeUtils.getDefault();
        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        if (table == null || columnsToMerge == null || !attributeUtils.areAllColumnsOfType(columnsToMerge, AttributeType.BOOLEAN) || booleanOperations == null || booleanOperations.length != columnsToMerge.length - 1) {
            throw new IllegalArgumentException("All columns have to be boolean columns, table, columns or operations can't be null and operations length must be columns length -1");
        }

        AttributeColumn newColumn;
        newColumn = ac.addAttributeColumn(table, newColumnTitle, AttributeType.BOOLEAN);
        final int newColumnIndex = newColumn.getIndex();

        Boolean value;
        Boolean secondValue;

        for (Attributes row : ac.getTableAttributeRows(table)) {
            value = (Boolean) row.getValue(columnsToMerge[0].getIndex());
            value = value != null ? value : false;//Use false if null
            for (int i = 0; i < booleanOperations.length; i++) {
                secondValue = (Boolean) row.getValue(columnsToMerge[i + 1].getIndex());
                secondValue = secondValue != null ? secondValue : false;//Use false if null
                switch (booleanOperations[i]) {
                    case AND:
                        value = value && secondValue;
                        break;
                    case OR:
                        value = value || secondValue;
                        break;
                    case XOR:
                        value = value ^ secondValue;
                        break;
                    case NAND:
                        value = !(value && secondValue);
                        break;
                    case NOR:
                        value = !(value || secondValue);
                        break;
                }
            }
            row.setValue(newColumnIndex, value);
        }

        return newColumn;
    }

    public AttributeColumn averageNumberMerge(AttributeTable table, AttributeColumn[] columnsToMerge, String newColumnTitle) {
        checkTableAndColumnsAreNumberOrNumberList(table, columnsToMerge);

        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        AttributeColumn newColumn;
        newColumn = ac.addAttributeColumn(table, newColumnTitle, AttributeType.BIGDECIMAL);//Create as BIGDECIMAL column by default. Then it can be duplicated to other type.
        final int newColumnIndex = newColumn.getIndex();

        BigDecimal average;
        for (Attributes row : ac.getTableAttributeRows(table)) {
            average = MathUtils.average(ac.getRowNumbers(row, columnsToMerge));
            row.setValue(newColumnIndex, average);
        }

        return newColumn;
    }

    public AttributeColumn medianNumberMerge(AttributeTable table, AttributeColumn[] columnsToMerge, String newColumnTitle) {
        checkTableAndColumnsAreNumberOrNumberList(table, columnsToMerge);

        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        AttributeColumn newColumn;
        newColumn = ac.addAttributeColumn(table, newColumnTitle, AttributeType.BIGDECIMAL);//Create as BIGDECIMAL column by default. Then it can be duplicated to other type.
        final int newColumnIndex = newColumn.getIndex();

        BigDecimal median;
        for (Attributes row : ac.getTableAttributeRows(table)) {
            median = MathUtils.median(ac.getRowNumbers(row, columnsToMerge));
            row.setValue(newColumnIndex, median);
        }

        return newColumn;
    }

    public AttributeColumn sumNumbersMerge(AttributeTable table, AttributeColumn[] columnsToMerge, String newColumnTitle) {
        checkTableAndColumnsAreNumberOrNumberList(table, columnsToMerge);

        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        AttributeColumn newColumn;
        newColumn = ac.addAttributeColumn(table, newColumnTitle, AttributeType.BIGDECIMAL);//Create as BIGDECIMAL column by default. Then it can be duplicated to other type.
        final int newColumnIndex = newColumn.getIndex();

        BigDecimal sum;
        for (Attributes row : ac.getTableAttributeRows(table)) {
            sum = MathUtils.sum(ac.getRowNumbers(row, columnsToMerge));
            row.setValue(newColumnIndex, sum);
        }

        return newColumn;
    }

    public AttributeColumn minValueNumbersMerge(AttributeTable table, AttributeColumn[] columnsToMerge, String newColumnTitle) {
        checkTableAndColumnsAreNumberOrNumberList(table, columnsToMerge);

        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        AttributeColumn newColumn;
        newColumn = ac.addAttributeColumn(table, newColumnTitle, AttributeType.BIGDECIMAL);//Create as BIGDECIMAL column by default. Then it can be duplicated to other type.
        final int newColumnIndex = newColumn.getIndex();

        BigDecimal min;
        for (Attributes row : ac.getTableAttributeRows(table)) {
            min = MathUtils.minValue(ac.getRowNumbers(row, columnsToMerge));
            row.setValue(newColumnIndex, min);
        }

        return newColumn;
    }

    public AttributeColumn maxValueNumbersMerge(AttributeTable table, AttributeColumn[] columnsToMerge, String newColumnTitle) {
        checkTableAndColumnsAreNumberOrNumberList(table, columnsToMerge);

        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        AttributeColumn newColumn;
        newColumn = ac.addAttributeColumn(table, newColumnTitle, AttributeType.BIGDECIMAL);//Create as BIGDECIMAL column by default. Then it can be duplicated to other type.
        final int newColumnIndex = newColumn.getIndex();

        BigDecimal max;
        for (Attributes row : ac.getTableAttributeRows(table)) {
            max = MathUtils.maxValue(ac.getRowNumbers(row, columnsToMerge));
            row.setValue(newColumnIndex, max);
        }

        return newColumn;
    }

    /*************Private methods:*************/

    private void checkTableAndColumnsAreNumberOrNumberList(AttributeTable table, AttributeColumn[] columns) {
        if (table == null) {
            throw new IllegalArgumentException("Table can't be null");
        }
        checkColumnsAreNumberOrNumberList(columns);
    }

    private void checkColumnsAreNumberOrNumberList(AttributeColumn[] columns) {
        if (columns == null || !AttributeUtils.getDefault().areAllNumberOrNumberListColumns(columns)) {
            throw new IllegalArgumentException("All columns have to be number or number list columns and can't be null");
        }
    }
}
