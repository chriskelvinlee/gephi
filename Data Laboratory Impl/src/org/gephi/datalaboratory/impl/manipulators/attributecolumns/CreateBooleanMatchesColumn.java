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
package org.gephi.datalaboratory.impl.manipulators.attributecolumns;

import java.awt.Image;
import java.util.regex.Pattern;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.datalaboratory.api.AttributesController;
import org.gephi.datalaboratory.impl.manipulators.attributecolumns.ui.CreateBooleanMatchesColumnUI;
import org.gephi.datalaboratory.impl.manipulators.attributecolumns.ui.DuplicateColumnUI;
import org.gephi.datalaboratory.spi.attributecolumns.AttributeColumnsManipulator;
import org.gephi.datalaboratory.spi.attributecolumns.AttributeColumnsManipulatorUI;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * AttributeColumnsManipulator that duplicate a AttributeColumn of a AttributeTable setting the same values for the rows.
 * Allow the user to select the title and AttributeType of the new column in the UI
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
@ServiceProvider(service = AttributeColumnsManipulator.class)
public class CreateBooleanMatchesColumn implements AttributeColumnsManipulator {

    private String title;
    private Pattern pattern;

    public void execute(AttributeTable table, AttributeColumn column) {
        if (pattern != null) {
            Lookup.getDefault().lookup(AttributesController.class).createBooleanMatchesColumn(table, column, title, pattern);
        }
    }

    public String getName() {
        return NbBundle.getMessage(CreateBooleanMatchesColumn.class, "CreateBooleanMatchesColumn.name");
    }

    public String getDescription() {
        return NbBundle.getMessage(CreateBooleanMatchesColumn.class, "CreateBooleanMatchesColumn.description");
    }

    public boolean canManipulateColumn(AttributeTable table, AttributeColumn column) {
        return true;
    }

    public AttributeColumnsManipulatorUI getUI() {
        return new CreateBooleanMatchesColumnUI();
    }

    public int getType() {
        return 200;
    }

    public int getPosition() {
        return 0;
    }

    public Image getIcon() {
        return ImageUtilities.loadImage("org/gephi/datalaboratory/impl/manipulators/resources/binocular--arrow.png");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
