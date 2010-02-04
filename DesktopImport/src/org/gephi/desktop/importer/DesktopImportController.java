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
package org.gephi.desktop.importer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.io.importer.api.Database;
import org.gephi.io.importer.api.FileType;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.api.Report;
import org.gephi.io.importer.spi.DatabaseImporter;
import org.gephi.io.importer.spi.DatabaseType;
import org.gephi.io.importer.spi.DatabaseTypeUI;
import org.gephi.io.importer.spi.FileFormatImporter;
import org.gephi.io.importer.spi.Importer;
import org.gephi.io.importer.spi.StreamImporter;
import org.gephi.io.importer.spi.TextImporter;
import org.gephi.io.importer.spi.XMLImporter;
import org.gephi.io.processor.spi.Processor;
import org.gephi.io.processor.spi.Scaler;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.longtask.api.LongTaskErrorHandler;
import org.gephi.utils.longtask.api.LongTaskExecutor;
import org.netbeans.validation.api.ui.ValidationPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = ImportController.class)
public class DesktopImportController implements ImportController {

    private LongTaskExecutor executor;
    private FileFormatImporter[] fileFormatImporters;
    private DatabaseImporter[] databaseImporters;
    private DatabaseType[] databaseTypes;

    public DesktopImportController() {
        //Get FileFormatImporters
        fileFormatImporters = new FileFormatImporter[0];
        fileFormatImporters = Lookup.getDefault().lookupAll(FileFormatImporter.class).toArray(fileFormatImporters);

        //Get DatabaseImporters
        databaseImporters = new DatabaseImporter[0];
        databaseImporters = Lookup.getDefault().lookupAll(DatabaseImporter.class).toArray(databaseImporters);

        //Get DatabaseTypes
        databaseTypes = new DatabaseType[0];
        databaseTypes = Lookup.getDefault().lookupAll(DatabaseType.class).toArray(databaseTypes);

        executor = new LongTaskExecutor(true, "Importer", 10);
    }

    public void doImport(FileObject fileObject) {
        try {
            fileObject = getArchivedFile(fileObject);   //Unzip and return content file
            FileFormatImporter im = getMatchingImporter(fileObject);
            if (im == null) {
                throw new RuntimeException(NbBundle.getMessage(getClass(), "error_no_matching_file_importer"));
            }

            //Create Container
            final Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();
            container.setSource(fileObject.getNameExt());

            //Report
            Report report = new Report();
            container.setReport(report);

            if (im instanceof XMLImporter) {
                importXML(fileObject, im, container);
            } else if (im instanceof TextImporter) {
                importText(fileObject, im, container);
            } else if (im instanceof StreamImporter) {
            }

        } catch (Exception ex) {
            Logger.getLogger("").log(Level.WARNING, "", ex);
        }
    }

    private void importXML(FileObject fileObject, Importer importer, final Container container) {
        final Document document = getDocument(fileObject);
        final XMLImporter xmlImporter = (XMLImporter) importer;
        final Report report = container.getReport();
        LongTask task = null;
        if (importer instanceof LongTask) {
            task = (LongTask) importer;
        }

        //ErrorHandler
        final LongTaskErrorHandler errorHandler = new LongTaskErrorHandler() {

            public void fatalError(Throwable t) {
                Logger.getLogger("").log(Level.WARNING, "", t.getCause());
            }
        };

        //Execute task
        executor.execute(task, new Runnable() {

            public void run() {
                try {
                    if (xmlImporter.importData(document, container.getLoader(), report)) {
                        finishImport(container);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }, "Import " + fileObject.getNameExt(), errorHandler);
    }

    private void importText(FileObject fileObject, Importer importer, final Container container) {
        final LineNumberReader reader = getTextReader(fileObject);
        final TextImporter textImporter = (TextImporter) importer;
        final Report report = container.getReport();
        LongTask task = null;
        if (importer instanceof LongTask) {
            task = (LongTask) importer;
        }

        //ErrorHandler
        final LongTaskErrorHandler errorHandler = new LongTaskErrorHandler() {

            public void fatalError(Throwable t) {
                Logger.getLogger("").log(Level.WARNING, "", t.getCause());
            }
        };

        //Execute task
        executor.execute(task, new Runnable() {

            public void run() {
                try {
                    if (textImporter.importData(reader, container.getLoader(), report)) {
                        finishImport(container);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }, "Import " + fileObject.getNameExt(), errorHandler);
    }

    public void doImport(Database database) {
        try {
            DatabaseType type = getDatabaseType(database);
            if (type == null) {
                throw new RuntimeException(NbBundle.getMessage(getClass(), "error_no_matching_db_importer"));
            }
            final DatabaseImporter importer = getMatchingImporter(type);
            if (importer == null) {
                throw new RuntimeException(NbBundle.getMessage(getClass(), "error_no_matching_db_importer"));
            }

            DatabaseTypeUI ui = type.getUI();
            if (ui != null) {
                ui.setup(type);
                String title = NbBundle.getMessage(DesktopImportController.class, "DesktopImportController.database.ui.dialog.title");
                JPanel panel = ui.getPanel();
                final DialogDescriptor dd = new DialogDescriptor(panel, title);
                if (panel instanceof ValidationPanel) {
                    ValidationPanel vp = (ValidationPanel) panel;
                    vp.addChangeListener(new ChangeListener() {

                        public void stateChanged(ChangeEvent e) {
                            dd.setValid(!((ValidationPanel) e.getSource()).isProblem());
                        }
                    });
                }
                if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.CANCEL_OPTION)) {
                    return;
                }
                ui.unsetup();
                database = ui.getDatabase();
            }

            //Create Container
            final Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();
            container.setSource(database.getName());

            //Report
            final Report report = new Report();
            container.setReport(report);

            LongTask task = null;
            if (importer instanceof LongTask) {
                task = (LongTask) importer;
            }

            //ErrorHandler
            final LongTaskErrorHandler errorHandler = new LongTaskErrorHandler() {

                public void fatalError(Throwable t) {
                    Logger.getLogger("").log(Level.WARNING, "", t.getCause());
                }
            };

            //Execute task
            final Database db = database;
            executor.execute(task, new Runnable() {

                public void run() {
                    try {
                        if (importer.importData(db, container.getLoader(), report)) {
                            finishImport(container);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
            }, "Import database", errorHandler);

        } catch (Exception ex) {
            Logger.getLogger("").log(Level.WARNING, "", ex);
        }
    }

    private void finishImport(Container container) {
        if (container.verify()) {
            Report report = container.getReport();

            //Report panel
            ReportPanel reportPanel = new ReportPanel();
            reportPanel.setData(report, container);
            DialogDescriptor dd = new DialogDescriptor(reportPanel, NbBundle.getMessage(DesktopImportController.class, "ReportPanel.title"));
            if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.CANCEL_OPTION)) {
                reportPanel.destroy();
                return;
            }
            reportPanel.destroy();

            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            Workspace workspace;
            if (pc.getCurrentProject() == null) {
                pc.newProject();
                workspace = pc.getCurrentWorkspace();
            } else {
                if (reportPanel.getProcessorStrategy().equals(ProcessorStrategyEnum.FULL)) {
                    //New workspace
                    workspace = pc.newWorkspace(pc.getCurrentProject());
                    pc.openWorkspace(workspace);
                } else if (pc.getCurrentWorkspace() == null) {
                    //Append mode but no workspace
                    workspace = pc.newWorkspace(pc.getCurrentProject());
                    pc.openWorkspace(workspace);
                } else {
                    //Append mode, current workspace is fine
                    workspace = pc.getCurrentWorkspace();
                }
            }
            if (container.getSource() != null) {
                pc.setSource(workspace, container.getSource());
            }

            container.closeLoader();
            if (container.isAutoScale()) {
                Scaler scaler = Lookup.getDefault().lookup(Scaler.class);
                if (scaler != null) {
                    scaler.doScale(container);
                }
            }
            Lookup.getDefault().lookup(Processor.class).process(workspace, container.getUnloader());

            //StatusLine notify
            String source = container.getSource();
            if (source.isEmpty()) {
                source = NbBundle.getMessage(DesktopImportController.class, "DesktopImportController.status.importSuccess.default");
            }
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(DesktopImportController.class, "DesktopImportController.status.importSuccess", source));
        }
    }

    private LineNumberReader getTextReader(FileObject fileObject) throws RuntimeException {
        try {
            LineNumberReader reader;
            CharsetToolkit charsetToolkit = new CharsetToolkit(fileObject);
            reader = (LineNumberReader) charsetToolkit.getReader();
            return reader;
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(NbBundle.getMessage(getClass(), "error_file_not_found"));
        } catch (IOException ex) {
            throw new RuntimeException(NbBundle.getMessage(getClass(), "error_io"));
        }
    }

    private Document getDocument(FileObject fileObject) throws RuntimeException {
        try {
            InputStream stream = fileObject.getInputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(stream);
            return document;
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(NbBundle.getMessage(getClass(), "error_missing_document_instance_factory"));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(NbBundle.getMessage(getClass(), "error_file_not_found"));
        } catch (SAXException ex) {
            throw new RuntimeException(NbBundle.getMessage(getClass(), "error_sax"));
        } catch (IOException ex) {
            throw new RuntimeException(NbBundle.getMessage(getClass(), "error_io"));
        }
    }

    private FileObject getArchivedFile(FileObject fileObject) {
        if (FileUtil.isArchiveFile(fileObject)) {
            //Unzip
            fileObject = FileUtil.getArchiveRoot(fileObject).getChildren()[0];
        }
        return fileObject;
    }

    private FileFormatImporter getMatchingImporter(FileObject fileObject) {
        for (FileFormatImporter im : fileFormatImporters) {
            if (im.isMatchingImporter(fileObject)) {
                return im;
            }
        }
        return null;
    }

    private DatabaseImporter getMatchingImporter(DatabaseType type) {
        for (DatabaseImporter im : databaseImporters) {
            if (im.isMatchingImporter(type)) {
                return im;
            }
        }

        return null;
    }

    private DatabaseType getDatabaseType(Database database) {
        for (DatabaseType dbt : databaseTypes) {
            if (dbt.getDatabaseClass().isAssignableFrom(database.getClass())) {
                return dbt;
            }
        }
        return null;
    }

    public FileType[] getFileTypes() {
        ArrayList<FileType> list = new ArrayList<FileType>();
        for (FileFormatImporter im : fileFormatImporters) {
            for (FileType ft : im.getFileTypes()) {
                list.add(ft);
            }
        }
        return list.toArray(new FileType[0]);
    }

    public DatabaseType[] getDatabaseTypes() {
        return databaseTypes;
    }

    public Database[] getDatabases(DatabaseType type) {
        Database[] dbs = new Database[0];
        return Lookup.getDefault().lookupAll(type.getDatabaseClass()).toArray(dbs);
    }

    public boolean isFileSupported(FileObject fileObject) {
        for (FileFormatImporter im : fileFormatImporters) {
            if (im.isMatchingImporter(fileObject)) {
                return true;
            }
        }
        if (fileObject.hasExt("zip")) {
            return true;
        }
        return false;
    }
}
