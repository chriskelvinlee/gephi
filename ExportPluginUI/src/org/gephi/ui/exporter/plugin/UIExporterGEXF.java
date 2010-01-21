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
package org.gephi.ui.exporter.plugin;

import javax.swing.JPanel;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.exporter.plugin.ExporterGEXF;
import org.gephi.io.exporter.spi.ExporterUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = ExporterUI.class)
public class UIExporterGEXF implements ExporterUI {

    private UIExporterGEXFPanel panel;
    private ExporterGEXF exporterGEXF;

    public void setup(Exporter exporter) {
        exporterGEXF = (ExporterGEXF) exporter;
        panel.setup(exporterGEXF);
    }

    public void unsetup(boolean update) {
        if (update) {
            panel.unsetup(exporterGEXF);
        }
        panel = null;
        exporterGEXF = null;
    }

    public JPanel getPanel() {
        panel = new UIExporterGEXFPanel();
        return panel;
    }

    public boolean isMatchingExporter(Exporter exporter) {
        return exporter instanceof ExporterGEXF;
    }
}
