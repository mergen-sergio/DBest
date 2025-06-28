/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui.frames;

import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.swing.util.mxGraphTransferable;
import controllers.MainController;
import database.TableCreator;
import entities.cells.TableCell;
import entities.utils.TreeUtils;
import entities.utils.cells.CellUtils;
import enums.CellType;
import files.FileUtils;
import files.ImportFile;
import files.csv.CSVInfo;
import gui.frames.forms.importexport.CSVRecognizerForm;
import gui.frames.main.MainFrame;
import ibd.table.Table;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 * @author ferna
 */
// Custom TransferHandler to handle file drop
public class FileTransferHandler extends mxGraphTransferHandler {

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        // Check if the data is a file list first
        if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return true;
        }

        // If not, fallback to the superclass's implementation for graph nodes
        return super.canImport(support);
    }

    /**
     *
     */
    public boolean canImport(JComponent comp, DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i] != null
                    && (flavors[i].equals(mxGraphTransferable.dataFlavor)
                    || flavors[i].isFlavorJavaFileListType())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        // Handle the file drop
        if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                Transferable transferable = support.getTransferable();
                List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

                for (File file : files) {
                    String extension = FileUtils.getFileExtension(file);
                    if (extension.equals("txt")) {
                        ImportFile.importQuery(file);  // Call your custom function to open the file
                    } else if (extension.equals("csv")) {
                        openCSVFile(file);
                    } else if (extension.equals("head")) {
                        openHeadFile(file);
                    } else if (extension.equals("dat")) {
                        openDataFile(file);
                    } else {
                        return false;
                    }
                }
                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // If it's not a file drop, fallback to the superclass for graph node drops
        return super.importData(support);
    }

    public static void openCSVFile(File file) {
        AtomicReference<Boolean> exitReference = new AtomicReference(false);
        CSVInfo info = new CSVRecognizerForm(
                Path.of(file.getAbsolutePath()), exitReference
        ).getCSVInfo();
        if (!exitReference.get()) {
            TableCell tableCell = TableCreator.createCSVTable(
                    info.tableName(), info.columns(), info, false
            );
            String newName = MainController.resolveTableNameConflict(tableCell.getName());
            if (newName == null) {
                TreeUtils.deleteTree(tableCell.getTree());
                return;
            }
            tableCell.setName(newName);
            MainController.executeImportTableCommand(tableCell);
            CellUtils.deactivateActiveJCell(MainFrame.getGraph(), tableCell.getJCell());
        }
    }

    public static void openHeadFile(File file) throws Exception {
        Table table = TableCreator.loadFromHeader(file.getAbsolutePath());

        CellType cellType = ImportFile.getCellType(file);

        TableCell tableCell = ImportFile.importHeaderFile(table, cellType, file);
        String newName = MainController.resolveTableNameConflict(tableCell.getName());
        if (newName == null) {
            TreeUtils.deleteTree(tableCell.getTree());
            return;
        }
        tableCell.setName(newName);

        MainController.executeImportTableCommand(tableCell);
        CellUtils.deactivateActiveJCell(MainFrame.getGraph(), tableCell.getJCell());
    }

    public static void openDataFile(File file) throws Exception {
        Table table = TableCreator.openBTreeTable(file.getAbsolutePath());

        CellType cellType = CellType.FYI_TABLE;

        TableCell tableCell = ImportFile.importHeaderFile(table, cellType, file);
        String newName = MainController.resolveTableNameConflict(tableCell.getName());
        if (newName == null) {
            TreeUtils.deleteTree(tableCell.getTree());
            return;
        }
        tableCell.setName(newName);

        MainController.executeImportTableCommand(tableCell);
        CellUtils.deactivateActiveJCell(MainFrame.getGraph(), tableCell.getJCell());
    }

}
