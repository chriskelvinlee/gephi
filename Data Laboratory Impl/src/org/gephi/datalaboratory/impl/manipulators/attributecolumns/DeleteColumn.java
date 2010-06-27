/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.datalaboratory.impl.manipulators.attributecolumns;

import java.awt.Image;
import javax.swing.JOptionPane;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.datalaboratory.api.AttributesController;
import org.gephi.datalaboratory.api.DataTablesController;
import org.gephi.datalaboratory.spi.attributecolumns.AttributeColumnsManipulator;
import org.gephi.datalaboratory.spi.attributecolumns.AttributeColumnsManipulatorUI;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * AttributeColumnsManipulator that deletes a AttributeColumn of a AttributeTable.
 * Only allows to delete columns with DATA AttributeOrigin.
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
@ServiceProvider(service = AttributeColumnsManipulator.class)
public class DeleteColumn implements AttributeColumnsManipulator {

    public void execute(AttributeTable table, AttributeColumn column) {
        if (JOptionPane.showConfirmDialog(null, NbBundle.getMessage(ClearColumnData.class, "ClearColumnData.confirmation.message", column.getTitle()), getName(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Lookup.getDefault().lookup(AttributesController.class).deleteAttributeColumn(table, column);
            Lookup.getDefault().lookup(DataTablesController.class).selectTable(table);
        }
    }

    public String getName() {
        return NbBundle.getMessage(DeleteColumn.class, "DeleteColumn.name");
    }

    public String getDescription() {
        return "";
    }

    public boolean canManipulateColumn(AttributeTable table, AttributeColumn column) {
        return column.getOrigin() == AttributeOrigin.DATA;
    }

    public AttributeColumnsManipulatorUI getUI() {
        return null;
    }

    public int getType() {
        return 0;
    }

    public int getPosition() {
        return 0;
    }

    public Image getIcon() {
        return ImageUtilities.loadImage("org/gephi/datalaboratory/impl/manipulators/resources/table-delete-column.png");
    }
}
