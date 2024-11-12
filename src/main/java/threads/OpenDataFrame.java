package threads;

import com.mxgraph.model.mxCell;

import controllers.ConstantController;
import controllers.MainController;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import entities.utils.cells.CellRepository;
import gui.frames.DataFrame;

import javax.swing.JOptionPane;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenDataFrame implements Runnable{

    private final mxCell jCell;

    public OpenDataFrame(mxCell jCell){
        this.jCell = jCell;
    }

    @Override
    public void run() {

        synchronized (MainController.getGraph()) {
            Optional<Cell> optionalCell = CellRepository.getActiveCell(jCell);

            if (optionalCell.isEmpty()) return;

            Cell cell = optionalCell.orElse(null);

            if (!(cell instanceof TableCell || ((OperationCell) cell).hasBeenInitialized())) return;

            if (!cell.hasError()) {
                try {
                    new DataFrame(cell);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), ConstantController.getString("error"), JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, ConstantController.getString("cell.operationCell.error"), ConstantController.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
        }

    }
}
