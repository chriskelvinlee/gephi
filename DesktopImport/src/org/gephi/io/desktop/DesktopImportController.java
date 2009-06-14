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
package org.gephi.io.desktop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.gephi.io.container.Container;
import org.gephi.io.importer.FileFormatImporter;
import org.gephi.io.importer.FileType;
import org.gephi.io.importer.ImportController;
import org.gephi.io.importer.ImportException;
import org.gephi.io.importer.StreamImporter;
import org.gephi.io.importer.TextImporter;
import org.gephi.io.importer.XMLImporter;
import org.gephi.io.processor.Processor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 *
 * @author Mathieu Bastian
 */
public class DesktopImportController implements ImportController {

    private FileFormatImporter[] importers;

    public DesktopImportController() {
        //Get importers
        importers = new FileFormatImporter[0];
        importers = Lookup.getDefault().lookupAll(FileFormatImporter.class).toArray(importers);

    }

    public void doDynamicImport(InputStream stream) throws ImportException {
    }

    public void doImport(FileObject fileObject) throws ImportException {
        FileFormatImporter im = getMatchingImporter(fileObject);
        if (im == null) {
            throw new ImportException(NbBundle.getMessage(getClass(), "error_no_matching_importer"));
        }

        ProjectController projectController = Lookup.getDefault().lookup(ProjectController.class);
        Workspace workspace = projectController.importFile();

        //Create Container
        Container container = Lookup.getDefault().lookup(Container.class);
        container.setSource(""+im.getClass());

        if (im instanceof XMLImporter) {
            Document document = getDocument(fileObject);
            XMLImporter xmLImporter = (XMLImporter) im;
            xmLImporter.importData(document, container.getLoader());
            finishImport(container);
        } else if (im instanceof TextImporter) {
            BufferedReader reader = getTextReader(fileObject);
            TextImporter textImporter = (TextImporter) im;
            textImporter.importData(reader, container.getLoader());
            finishImport(container);
        } else if (im instanceof StreamImporter) {
        }
    }

    private void finishImport(Container container) {
        Lookup.getDefault().lookup(Processor.class).process(container.getUnloader());
    }

    private BufferedReader getTextReader(FileObject fileObject) throws ImportException {
        File file = FileUtil.toFile(fileObject);
        try {
            if (file == null) {
                throw new FileNotFoundException();
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            return reader;
        } catch (FileNotFoundException ex) {
            throw new ImportException(NbBundle.getMessage(getClass(), "error_file_not_found"));
        }
    }

    private Document getDocument(FileObject fileObject) throws ImportException {
        File file = FileUtil.toFile(fileObject);
        try {
            if (file == null) {
                throw new FileNotFoundException();
            }
            InputStream stream = new FileInputStream(file);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(stream);
            return document;
        } catch (ParserConfigurationException ex) {
            throw new ImportException(NbBundle.getMessage(getClass(), "error_missing_document_instance_factory"));
        } catch (FileNotFoundException ex) {
            throw new ImportException(NbBundle.getMessage(getClass(), "error_file_not_found"));
        } catch (SAXException ex) {
            throw new ImportException(NbBundle.getMessage(getClass(), "error_sax"));
        } catch (IOException ex) {
            throw new ImportException(NbBundle.getMessage(getClass(), "error_io"));
        }
    }

    private FileFormatImporter getMatchingImporter(FileObject fileObject) {
        for (FileFormatImporter im : importers) {
            if (im.isMatchingImporter(fileObject)) {
                return im;
            }
        }
        return null;
    }

    public FileType[] getFileTypes() {
        ArrayList<FileType> list = new ArrayList<FileType>();
        for (FileFormatImporter im : importers) {
            for (FileType ft : im.getFileTypes()) {
                list.add(ft);
            }
        }
        return list.toArray(new FileType[0]);
    }
}
