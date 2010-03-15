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
package org.gephi.project.io;

import java.io.IOException;
import org.gephi.project.impl.ProjectControllerImpl;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.gephi.project.io.GephiDataObject;
import org.gephi.project.io.GephiFormatException;
import org.gephi.project.io.GephiWriter;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.w3c.dom.Document;

/**
 *
 * @author Mathieu Bastian
 */
public class SaveTask implements LongTask, Runnable {

    private static final String ZIP_LEVEL_PREFERENCE = "ProjectIO_Save_ZipLevel_0_TO_9";
    private GephiDataObject dataObject;
    private GephiWriter gephiWriter;
    private boolean cancel = false;
    private ProgressTicket progressTicket;

    public SaveTask(GephiDataObject dataObject) {
        this.dataObject = dataObject;
    }

    public void run() {
        //System.out.println("Save " + dataObject.getName());
        ZipOutputStream zipOut = null;
        boolean useTempFile = false;
        File writeFile = null;
        try {
            Progress.start(progressTicket);
            Progress.setDisplayName(progressTicket, NbBundle.getMessage(SaveTask.class, "SaveTask.name"));
            FileObject fileObject = dataObject.getPrimaryFile();
            File outputFile = FileUtil.toFile(fileObject);
            writeFile = outputFile;
            if (writeFile.exists()) {
                useTempFile = true;
                String tempFileName = writeFile.getName() + "_temp";
                writeFile = new File(writeFile.getParent(), tempFileName);
            }

            //Stream
            int zipLevel = NbPreferences.forModule(SaveTask.class).getInt(ZIP_LEVEL_PREFERENCE, 9);
            FileOutputStream outputStream = new FileOutputStream(writeFile);
            zipOut = new ZipOutputStream(outputStream);
            zipOut.setLevel(zipLevel);

            zipOut.putNextEntry(new ZipEntry("Project"));
            gephiWriter = new GephiWriter();

            //Write Document
            Document document = gephiWriter.writeAll(dataObject.getProject());

            //Write file output
            Source source = new DOMSource(document);
            Result result = new StreamResult(zipOut);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(source, result);

            //Close
            zipOut.closeEntry();
            zipOut.finish();
            zipOut.close();

            //Clean and copy
            if (useTempFile && !cancel) {
                String name = fileObject.getName();
                String ext = fileObject.getExt();

                //Delete original file
                fileObject.delete();

                //Rename temp file
                FileObject tempFileObject = FileUtil.toFileObject(writeFile);
                FileLock lock = tempFileObject.lock();
                tempFileObject.rename(lock, name, ext);
                lock.releaseLock();
            } else if (cancel) {
                //Delete temp file
                FileObject tempFileObject = FileUtil.toFileObject(writeFile);
                tempFileObject.delete();
            }
            Progress.finish(progressTicket);
            //Status line
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(ProjectControllerImpl.class, "DesktoProjectController.status.saved", dataObject.getName()));
        } catch (Exception ex) {
            ex.printStackTrace();
            if (zipOut != null) {
                try {
                    zipOut.close();
                } catch (IOException ex1) {
                }
            }
            if (useTempFile && writeFile != null) {
                writeFile.delete();
            }
            throw new GephiFormatException(GephiWriter.class, ex);
        }
    }

    public boolean cancel() {
        if (gephiWriter != null) {
            gephiWriter.cancel();
        }
        return true;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
}
