package org.gephi.desktop.neo4j;


import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.gephi.desktop.neo4j.ui.RemoteDatabasePanel;
import org.gephi.neo4j.api.Neo4jExporter;
import org.gephi.neo4j.api.Neo4jImporter;
import org.gephi.utils.longtask.api.LongTaskExecutor;
import org.gephi.utils.longtask.spi.LongTask;
import org.netbeans.validation.api.ui.ValidationPanel;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;


/**
 *
 * @author Martin Škurla
 */
public class Neo4jImportExportAction extends CallableSystemAction {
    @Override
    public void performAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return "importNeo4jDB";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public JMenuItem getMenuPresenter() {
        JMenu menu = new JMenu(NbBundle.getMessage(Neo4jImportExportAction.class, "CTL_Neo4j_MenuLabel"));

        String localImportMessage = NbBundle.getMessage(Neo4jImportExportAction.class, "CTL_Neo4j_LocalImportMenuLabel");
        JMenuItem localImport = new JMenuItem(new AbstractAction(localImportMessage) {

            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                String localImportDialogTitle = NbBundle.getMessage(Neo4jImportExportAction.class, "CTL_Neo4j_LocalImportDialogTitle");
                fileChooser.setDialogTitle(localImportDialogTitle);

                Neo4jCustomDirectoryProvider.setEnabled(true);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int dialogResult = fileChooser.showOpenDialog(null);

                if (dialogResult == JFileChooser.APPROVE_OPTION) {
                    final File neo4jDirectory = fileChooser.getSelectedFile();
                    final Neo4jImporter neo4jImporter = Lookup.getDefault().lookup(Neo4jImporter.class);

                    LongTaskExecutor executor = new LongTaskExecutor(true);
                    executor.execute((LongTask) neo4jImporter, new Runnable() {
                        @Override
                        public void run() {
                            neo4jImporter.importLocal(neo4jDirectory);
                        }
                    });
                }

                Neo4jCustomDirectoryProvider.setEnabled(false);
            }
        });

        String localExportMessage = NbBundle.getMessage(Neo4jImportExportAction.class, "CTL_Neo4j_LocalExportMenuLabel");
        JMenuItem localExport = new JMenuItem(new AbstractAction(localExportMessage) {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                String localExportDialogTitle = NbBundle.getMessage(Neo4jImportExportAction.class, "CTL_Neo4j_LocalExportDialogTitle");
                fileChooser.setDialogTitle(localExportDialogTitle);

                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int dialogResult = fileChooser.showOpenDialog(null);

                if (dialogResult == JFileChooser.APPROVE_OPTION) {
                    final File neo4jDirectory = fileChooser.getSelectedFile();
                    final Neo4jExporter neo4jExporter = Lookup.getDefault().lookup(Neo4jExporter.class);

                    LongTaskExecutor executor = new LongTaskExecutor(true);
                    executor.execute((LongTask) neo4jExporter, new Runnable() {
                        @Override
                        public void run() {
                            neo4jExporter.exportLocal(neo4jDirectory);
                        }
                    });
                }
            }
        });

        String remoteImportMessage = NbBundle.getMessage(Neo4jImportExportAction.class, "CTL_Neo4j_RemoteImportMenuLabel");
        JMenuItem remoteImport = new JMenuItem(new AbstractAction(remoteImportMessage) {
            public void actionPerformed(ActionEvent e) {
                final RemoteDatabasePanel databasePanel = new RemoteDatabasePanel();
                final ValidationPanel panel = RemoteDatabasePanel.createValidationPanel(databasePanel);
                String remoteImportDialogTitle = NbBundle.getMessage(Neo4jImportExportAction.class, "CTL_Neo4j_RemoteImportDialogTitle");

                if (panel.showOkCancelDialog(remoteImportDialogTitle)) {
                    final Neo4jImporter neo4jImporter = Lookup.getDefault().lookup(Neo4jImporter.class);

                    LongTaskExecutor executor = new LongTaskExecutor(true);
                    executor.execute((LongTask) neo4jImporter, new Runnable() {
                        @Override
                        public void run() {
                            neo4jImporter.importRemote(databasePanel.getRemoteUrl(),
                                                       databasePanel.getLogin(),
                                                       databasePanel.getPassword());
                        }
                    });
                }
            }
        });

        String remoteExportMessage = NbBundle.getMessage(Neo4jImportExportAction.class, "CTL_Neo4j_RemoteExportMenuLabel");
        JMenuItem remoteExport = new JMenuItem(new AbstractAction(remoteExportMessage) {
            public void actionPerformed(ActionEvent e) {
                final RemoteDatabasePanel databasePanel = new RemoteDatabasePanel();
                final ValidationPanel panel = RemoteDatabasePanel.createValidationPanel(databasePanel);
                String remoteExportDialogTitle = NbBundle.getMessage(Neo4jImportExportAction.class, "CTL_Neo4j_RemoteExportDialogTitle");

                if (panel.showOkCancelDialog(remoteExportDialogTitle)) {
                    final Neo4jExporter neo4jExporter = Lookup.getDefault().lookup(Neo4jExporter.class);

                    LongTaskExecutor executor = new LongTaskExecutor(true);
                    executor.execute((LongTask) neo4jExporter, new Runnable() {
                        @Override
                        public void run() {
                            neo4jExporter.exportRemote(databasePanel.getRemoteUrl(),
                                                       databasePanel.getLogin(),
                                                       databasePanel.getPassword());
                        }
                    });
                }
            }
        });
        

        menu.add(localImport);
        menu.add(remoteImport);
        menu.add(localExport);
        menu.add(remoteExport);
        return menu;
    }
}
