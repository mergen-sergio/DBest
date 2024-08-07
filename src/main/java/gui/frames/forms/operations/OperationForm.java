package gui.frames.forms.operations;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import enums.ColumnDataType;
import gui.frames.forms.FormBase;
import ibd.query.Operation;
import ibd.query.OperationUtils;
import ibd.query.binaryop.BinaryOperation;
import ibd.query.binaryop.join.Join;
import ibd.query.binaryop.join.MergeJoin;
import operations.IOperator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class OperationForm extends FormBase {

    private final Class<? extends IOperator> operator;

    protected final mxCell jCell;
    protected final Cell parent1;

    protected final JComboBox<String> comboBoxSource = new JComboBox<>();
    protected final JComboBox<String> comboBoxColumn = new JComboBox<>();
    protected final JPanel centerPanel = new JPanel(new GridBagLayout());

    protected final List<String> arguments = new ArrayList<>();
    protected final List<String> previousArguments = new ArrayList<>();
    protected final List<String> restrictedColumns = new ArrayList<>();

    public OperationForm(mxCell jCell) {

        super(null);
        setModal(true);

        OperationCell cell = (OperationCell) CellUtils.getActiveCell(jCell).get();

        this.operator = cell.getType().operatorClass;
        this.jCell = jCell;
        this.parent1 = cell.getParents().get(0);

        if(!cell.getArguments().isEmpty()) previousArguments.addAll(cell.getArguments());

        setTitle(cell.getType().displayName);

        initializeGUI();

    }

    private void initializeGUI(){

        setLocationRelativeTo(null);

        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        initializeComboBoxes();

        pack();

    }

    private void initializeComboBoxes() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(new JLabel(ConstantController.getString("operationForm.source") +":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(comboBoxSource, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        centerPanel.add(new JLabel(ConstantController.getString("operationForm.column") +":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(comboBoxColumn, gbc);

        contentPanel.add(centerPanel, BorderLayout.CENTER);

        parent1.getColumns().stream()
                .map(x -> x.SOURCE).distinct()
                .forEach(comboBoxSource::addItem);

        comboBoxSource.addActionListener(actionEvent -> setColumns(comboBoxColumn, comboBoxSource, parent1));

        setColumns(comboBoxColumn, comboBoxSource, parent1);

    }

    protected void addExtraComponent(Component component, int gridx, int gridy, int gridwidth, int gridheight) {

        GridBagConstraints gbc = ((GridBagLayout) centerPanel.getLayout()).getConstraints(centerPanel);

        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(component, gbc);

        pack();
        revalidate();
        repaint();
    }

//    protected void setColumns(JComboBox<String> comboBox, JComboBox<String> comboBoxS, Cell parent){
//
//        comboBox.removeAllItems();
//        parent.getColumns().stream().filter(x -> x.SOURCE.
//                        equals(Objects.requireNonNull(comboBoxS.getSelectedItem()).toString())).
//                        map(Column::getSourceAndName).filter(x -> !restrictedColumns.contains(x)).
//                        map(Column::removeSource).forEach(comboBox::addItem);
//
//        pack();
//
//    }
    
    protected void setColumns(JComboBox<String> comboBox, JComboBox<String> comboBoxS, Cell parent) {
    comboBox.removeAllItems();
    if (comboBoxS.getSelectedItem()==null) return;
    String selectedItem = Objects.requireNonNull(comboBoxS.getSelectedItem()).toString();
    List<Column> columns = parent.getColumns();

    for (Column column : columns) {
        if (column.SOURCE.equals(selectedItem)) {
            String sourceAndName = column.getSourceAndName();
            if (!restrictedColumns.contains(sourceAndName)) {
                String name = column.removeSource(sourceAndName);
                comboBox.addItem(name);
            }
        }
    }

    pack();
}
    
    protected void setColumns2(JComboBox<String> comboBox, JComboBox<String> comboBoxS, Cell parent, List<Column> leftSideCols) {
    comboBox.removeAllItems();
    String selectedItem = Objects.requireNonNull(comboBoxS.getSelectedItem()).toString();
    List<Column> columns = parent.getColumns();

    
    
    for (Column column : columns) {
        if (column.SOURCE.equals(selectedItem)) {
            String sourceAndName = column.getSourceAndName();
            if (!restrictedColumns.contains(sourceAndName)) {
                String name = column.removeSource(sourceAndName);
                comboBox.addItem(name);
            }
        }
    }
    
    for (Column column : leftSideCols) {
        if (column.SOURCE.equals(selectedItem)) {
            String sourceAndName = column.getSourceAndName();
            if (!restrictedColumns.contains(sourceAndName)) {
                String name = column.removeSource(sourceAndName);
                comboBox.addItem(name);
            }
        }
    }

    pack();
}
    
    public static List<Column> getLeftSideCorrelationColumns(Operation op) {
        
        List<Column> columns = new ArrayList<>();
        
        List<Operation> leftSideCorrelations = OperationUtils.findLeftSideCorrelations(op);
        for (Operation leftSideCorrelation : leftSideCorrelations) {
            addColumns(leftSideCorrelation, columns);
        }
        
        return columns;
    }
    
    public static List<Operation> getLeftSideCorrelationOperations(OperationCell cell) {
        
        
        List<Operation> operations = new ArrayList();
        while (cell!=null){
            Operation op1 = cell.getOperator();
            if (op1 instanceof BinaryOperation){
                if (!(op1 instanceof Join)) return operations;
                if (op1 instanceof MergeJoin) return operations;
                operations.add(((BinaryOperation) op1).getLeftOperation());
            }
            cell = cell.getChild();
        }
        return operations;
        
    }
    
    public static List<Column> getLeftSideCorrelationColumns(OperationCell cell) {
        
        List<Operation> leftSideCorrelations = getLeftSideCorrelationOperations(cell);
        List<Column> columns = new ArrayList<>();
        
        for (Operation leftSideCorrelation : leftSideCorrelations) {
            addColumns(leftSideCorrelation, columns);
        }
        
        return columns;
        
    }
    
    private static void addColumns(Operation op, List<Column> columns){
    for (Map.Entry<String, List<String>> contentInfo : op.getContentInfo().entrySet()) {
            for (String columnName : contentInfo.getValue()) {
                Column column = new Column(columnName, contentInfo.getKey(), ColumnDataType.NONE, false);
                columns.add(column);
            }
        }
    }

    protected void btnReady(){

        dispose();

        try {

            Constructor<? extends IOperator> constructor = operator.getDeclaredConstructor();
            IOperator operation = constructor.newInstance();
            operation.executeOperation(jCell, arguments);

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                 | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    protected abstract void setPreviousArgs();

}
