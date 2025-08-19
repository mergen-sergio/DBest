package threads;

import com.mxgraph.model.mxCell;

import controllers.ConstantController;
import controllers.MainController;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import entities.utils.cells.CellRepository;
import enums.OperationType;
import gui.frames.DataFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenDataFrame implements Runnable{

    private final mxCell jCell;
    private SwingWorker<Void, Void> dataFrameWorker;
    private JDialog loadingDialog;

    public OpenDataFrame(mxCell jCell){
        this.jCell = jCell;
    }
    
    /**
     * Cleans up hash table memory when hash operations are cancelled
     */
    private static void cleanupHashMemoryIfNeeded(Cell cell) {
        try {
            if (cell instanceof OperationCell operationCell) {
                if (operationCell.getType().name.equalsIgnoreCase("hash")) {
                    ibd.query.Operation op = operationCell.getOperator();
                    if (op instanceof ibd.query.unaryop.HashIndex hashIndex) {
                        hashIndex.cleanMemory();
                    }
                }
            }
        } catch (Exception e) {
            // Log error but don't prevent cancellation
            System.err.println("Error cleaning hash memory: " + e.getMessage());
        }
    }

    @Override
    public void run() {

        synchronized (MainController.getGraph()) {
            Optional<Cell> optionalCell = CellRepository.getActiveCell(jCell);

            if (optionalCell.isEmpty()) return;

            Cell cell = optionalCell.orElse(null);

            if (!(cell instanceof TableCell || ((OperationCell) cell).hasBeenInitialized())) return;

            if (!cell.hasError()) {
                // Check if this is a hash operation
                if (cell instanceof OperationCell operationCell && 
                    operationCell.getType().name.equalsIgnoreCase("hash")) {
                    showHashLoadingDialog(cell);
                } else {
                    try {
                        new DataFrame(cell);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), ConstantController.getString("error"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, ConstantController.getString("cell.operationCell.error"), ConstantController.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showHashLoadingDialog(Cell cell) {
        SwingUtilities.invokeLater(() -> {
            loadingDialog = new JDialog((Frame) null, "Generating Hash Table", true);
            JButton cancelButton = new JButton("Cancel");
            MovingSquareBar movingBar = new MovingSquareBar();

            JPanel dialogPanel = new JPanel();
            dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
            dialogPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            JLabel messageLabel = new JLabel("Generating hash table. You can cancel the operation.");
            messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            movingBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            dialogPanel.add(messageLabel);
            dialogPanel.add(Box.createVerticalStrut(15));
            dialogPanel.add(movingBar);
            dialogPanel.add(Box.createVerticalStrut(15));
            dialogPanel.add(cancelButton);

            loadingDialog.setContentPane(dialogPanel);
            loadingDialog.setSize(400, 150);
            loadingDialog.setLocationRelativeTo(null);
            loadingDialog.setResizable(false);

            dataFrameWorker = new SwingWorker<>() {
                private DataFrame dataFrame = null;
                
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        // Create DataFrame in background thread
                        dataFrame = new DataFrame(cell);
                        return null;
                    } catch (Exception ex) {
                        if (!isCancelled()) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(null, ex.getMessage(), ConstantController.getString("error"), JOptionPane.ERROR_MESSAGE);
                            });
                        }
                        return null;
                    }
                }

                @Override
                protected void done() {
                    movingBar.stopAnimation();
                    loadingDialog.dispose();
                    
                    // If cancelled, dispose of any created DataFrame and clean up memory
                    if (isCancelled()) {
                        cleanupHashMemoryIfNeeded(cell);
                        if (dataFrame != null) {
                            dataFrame.dispose();
                        }
                    }
                    // If not cancelled, the DataFrame will already be visible
                }
            };

            cancelButton.addActionListener(e -> {
                movingBar.stopAnimation();
                DataFrame.requestCancellation(); // Request DataFrame to cancel
                dataFrameWorker.cancel(true);
                loadingDialog.dispose();
            });

            loadingDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    DataFrame.requestCancellation(); // Request DataFrame to cancel
                    if (dataFrameWorker != null && !dataFrameWorker.isDone()) {
                        dataFrameWorker.cancel(true);
                    }
                }
            });

            dataFrameWorker.execute();
            loadingDialog.setVisible(true);
        });
    }

    private static class MovingSquareBar extends JPanel {
        private int x = 0;
        private final int squareSize = 18;
        private final int barWidth = 300;
        private final int barHeight = 15;
        private final int step = 6;
        private final Color squareColor = new Color(0, 180, 0);
        private Timer timer;

        public MovingSquareBar() {
            setPreferredSize(new Dimension(barWidth, barHeight));
            setMinimumSize(new Dimension(barWidth, barHeight));
            setMaximumSize(new Dimension(barWidth, barHeight));
            setBorder(BorderFactory.createLoweredBevelBorder());
            setOpaque(true);
            setBackground(Color.LIGHT_GRAY);
            startAnimation();
        }

        private void startAnimation() {
            timer = new Timer(30, e -> {
                x += step;
                if (x > barWidth - squareSize) {
                    x = 0;
                }
                repaint();
            });
            timer.start();
        }

        public void stopAnimation() {
            if (timer != null) timer.stop();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(squareColor);
            g.fillRoundRect(x, (barHeight - squareSize) / 2, squareSize, squareSize, 6, 6);
        }
    }
}
