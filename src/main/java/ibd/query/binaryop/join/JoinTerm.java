/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.binaryop.join;

import ibd.query.ColumnDescriptor;

/**
 * A term is a join condition between two columns
 *
 * @author Sergio
 */
public class JoinTerm {

    private final ColumnDescriptor leftColumn;
    private final ColumnDescriptor rightColumn;

    private int leftTupleRowIndex = -1;
    private int rightTupleRowIndex = -1; 
    
    private int leftColumnIndex;
    private int rightColumnIndex;

    /**
     *
     * @param leftTableAlias the name of the left side table
     * @param leftColumn the name of the left side column
     * @param rightTableAlias the name of the right side table
     * @param rightColumn the name of the right side table
     */
    public JoinTerm(String leftTableAlias, String leftColumn, String rightTableAlias, String rightColumn) {
        this.leftColumn = new ColumnDescriptor(leftTableAlias, leftColumn);
        this.rightColumn = new ColumnDescriptor(rightTableAlias, rightColumn);
    }

    /**
     *
     * @param leftColumn the name of the left side column. The name can be
     * prefixed by the table name (e.g. tab.col)
     * @param rightColumn the name of the right side column. The name can be
     * prefixed by the table name (e.g. tab.col)
     * @throws Exception
     */
    public JoinTerm(String leftColumn, String rightColumn) throws Exception {
        this.leftColumn = new ColumnDescriptor(leftColumn);
        this.rightColumn = new ColumnDescriptor(rightColumn);

    }

    /**
     *
     * @return the name of the left side table
     */
    public String getLeftTableAlias() {
        return leftColumn.getTableName();
    }

    /**
     * @return the name of the left side column
     */
    public String getLeftColumn() {
        return leftColumn.getColumnName();
    }

    public int getLeftColumnIndex(){
        return leftColumnIndex; 
    }
    
    public void setLeftColumnIndex(int leftColumnIndex){
        this.leftColumnIndex = leftColumnIndex;
    }
    
    public int getRightColumnIndex(){
        return rightColumnIndex;
    }
    
    public void setRightColumnIndex(int rightColumnIndex){
        this.rightColumnIndex = rightColumnIndex;
    }
    
    public ColumnDescriptor getLeftColumnDescriptor(){
        return leftColumn;
    }
    
    public ColumnDescriptor getRightColumnDescriptor(){
        return rightColumn;
    }
    
    
    /**
     *
     * @return the name of the right side table
     */
    public String getRightTableAlias() {
        return rightColumn.getTableName();
    }

    /**
     * @return the name of the right side column
     */
    public String getRightColumn() {
        return rightColumn.getColumnName();
    }

    /**
     *
     * @param tupleIndex the index of the row from the left side tuple that contains the left
     * side column
     */
    public void setLeftTupleRowIndex(int tupleIndex) {
        this.leftTupleRowIndex = tupleIndex;
    }

    /**
     *
     * @param tupleIndex the index of the row from the right side tuple that contains the
     * righr side column
     */
    public void setRightTupleRowIndex(int tupleIndex) {
        this.rightTupleRowIndex = tupleIndex;
    }

    /**
     *
     * @return the index of the row from the left side tuple that contains the left side
     * column
     */
    public int getLeftTupleRowIndex() {
        return leftTupleRowIndex;
    }

    /**
     *
     * @return the index of the row from right side tuple that contains the right side
     * column
     */
    public int getRightTupleRowIndex() {
        return rightTupleRowIndex;
    }

}
