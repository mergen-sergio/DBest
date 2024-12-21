/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join;

import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import ibd.query.binaryop.BinaryOperation;
import java.util.List;

/**
 * Performs a join between the left and the right operations using
 * the join terms provided by a join predicate.
 *
 * @author Sergio
 */
public abstract class Join extends BinaryOperation {

    //the join predicate
    protected JoinPredicate joinPredicate;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @param joinPredicate the join predicate
     * @throws Exception
     */
    public Join(Operation leftOperation, Operation rightOperation, JoinPredicate joinPredicate) throws Exception {
        super(leftOperation, rightOperation);
        //the predicate is cloned to ease query formulation, as the same predicate can be reused for different join operations
        this.joinPredicate = cloneTerms(joinPredicate);
    }
    
    //creates a copy of the join predicate
    protected JoinPredicate cloneTerms(JoinPredicate joinPredicate){
        JoinPredicate newjoinPredicate = new JoinPredicate();
        for (JoinTerm term : joinPredicate.getTerms()) {
            newjoinPredicate.addTerm(term.getLeftTableAlias(), term.getLeftColumn(), 
                                           term.getRightTableAlias(), term.getRightColumn());
        }
        return newjoinPredicate;
    }

    /**
     *
     * @return the join predicate terms
     */
    public List<JoinTerm> getTerms() {
        //return (List<Term>) Collections.unmodifiableCollection(terms.getTerms());
        return (List<JoinTerm>) joinPredicate.getTerms();
    }
    
    @Override
    public void prepare() throws Exception {

        
        //sets the column indexes for the terms of the join predicate
        setJoinTermsIndexes();
        
        super.prepare();

        

    }
    
    //sets the column indexes for the terms of the join predicate
    protected void setJoinTermsIndexes() throws Exception {
        for (JoinTerm term : joinPredicate.getTerms()) {
            leftOperation.setColumnLocation(term.getLeftColumnDescriptor());
            rightOperation.setColumnLocation(term.getRightColumnDescriptor());
        }
    }
    
    /**
     * {@inheritDoc }
     * the data sources array  is a concatenation of the data sources
     * that come from the left and the right subtrees
     *
     * @throws Exception
     */
    @Override
    public void setDataSourcesInfo() throws Exception {
        
        getLeftOperation().setDataSourcesInfo();
        getRightOperation().setDataSourcesInfo();

        ReferedDataSource left[] = getLeftOperation().getDataSources();
        ReferedDataSource right[] = getRightOperation().getDataSources();
        dataSources = new ReferedDataSource[left.length + right.length];
        int count = 0;
        for (int i = 0; i < left.length; i++) {
            dataSources[count] = left[i];
            count++;
        }
        for (int i = 0; i < right.length; i++) {
            dataSources[count] = right[i];
            count++;
        }

    }
    
    public abstract String getJoinAlgorithm();
    
    @Override
    public String toString(){
        return getJoinAlgorithm()+"("+joinPredicate+")";
    }
    
    public static JoinPredicate createJoinPredicate(List<String> arguments){
        JoinPredicate joinPredicate = new JoinPredicate();
       
        for (String term : arguments) {
            if (term.isBlank() || term.isEmpty()) continue;
            int index = term.indexOf("=", 0);
            String col1 = term.substring(0, index);
            String col2 = term.substring(index+1, term.length());
            try {
                joinPredicate.addTerm(col1, col2);
            } catch (Exception ex) {
            }
        }
        return joinPredicate;
    }

}
