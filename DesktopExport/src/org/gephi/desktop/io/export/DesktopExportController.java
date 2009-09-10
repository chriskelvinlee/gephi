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
package org.gephi.desktop.io.export;

import org.gephi.io.exporter.ExportController;
import org.gephi.io.exporter.Exporter;
import org.gephi.io.exporter.FileExporter;
import org.gephi.io.exporter.FileType;
import org.gephi.io.exporter.GraphFileExporter;
import org.gephi.io.exporter.TextExporter;
import org.gephi.io.exporter.XMLExporter;
import org.gephi.ui.exporter.ExporterUI;
import org.gephi.utils.longtask.LongTaskExecutor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class DesktopExportController implements ExportController {

    private LongTaskExecutor executor;
    private GraphFileExporter[] graphFileExporters;

    public DesktopExportController() {

        //Get FileFormatExporters
        graphFileExporters = new GraphFileExporter[0];
        graphFileExporters = Lookup.getDefault().lookupAll(GraphFileExporter.class).toArray(graphFileExporters);

        executor = new LongTaskExecutor(true, "Exporter", 10);
    }

    public GraphFileExporter[] getGraphFileExporters() {
        return graphFileExporters;
    }

    public void doExport(Exporter exporter, FileObject fileObject) {
        try {
            if (exporter instanceof TextExporter) {
                exportText(exporter);
            } else if (exporter instanceof XMLExporter) {
                exportXML(exporter);
            }

        } catch (Exception ex) {
            NotifyDescriptor.Message e = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notifyLater(e);
            ex.printStackTrace();
        }
    }

    public void doExport(FileObject fileObject) {
        FileExporter exporter = getMatchingExporter(fileObject);
        if (exporter == null) {
            throw new RuntimeException(NbBundle.getMessage(getClass(), "error_no_matching_file_importer"));
        }
        doExport(exporter, fileObject);
    }

    private void exportText(Exporter exporter) {
        TextExporter textExporter = (TextExporter) exporter;
    }

    private void exportXML(Exporter exporter) {
        XMLExporter xmlExporter = (XMLExporter) exporter;

    }

    public boolean hasUI(Exporter exporter) {
        ExporterUI[] exporterUIs = Lookup.getDefault().lookupAll(ExporterUI.class).toArray(new ExporterUI[0]);
        for (ExporterUI ui : exporterUIs) {
            if (ui.isMatchingExporter(exporter)) {
                return true;
            }
        }
        return false;
    }

    public ExporterUI getUI(Exporter exporter) {
        ExporterUI[] exporterUIs = Lookup.getDefault().lookupAll(ExporterUI.class).toArray(new ExporterUI[0]);
        for (ExporterUI ui : exporterUIs) {
            if (ui.isMatchingExporter(exporter)) {
                return ui;
            }
        }
        return null;
    }

    private FileExporter getMatchingExporter(FileObject fileObject) {
        for (GraphFileExporter im : graphFileExporters) {
            for (FileType ft : im.getFileTypes()) {
                for (String ex : ft.getExtensions()) {
                    if (fileObject.hasExt(ex)) {
                        return im;
                    }
                }
            }
        }
        return null;
    }
}
