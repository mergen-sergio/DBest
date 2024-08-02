/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join;

import ibd.query.Operation;

/**
 * Performs a rigth nested loop join between the left and the right operations using
 * the join conditions provided by the join terms.
 *
 * @author Sergio
 */
public class RightNestedLoopJoin extends LeftNestedLoopJoin {

    public RightNestedLoopJoin(Operation leftOperation, Operation rightOperation, JoinPredicate terms) throws Exception {
        super(rightOperation, leftOperation, terms);
    }

    @Override
    protected JoinPredicate cloneTerms(JoinPredicate terms) {
        JoinPredicate newTerms = new JoinPredicate();
        for (JoinTerm term : terms.getTerms()) {
            newTerms.addTerm(term.getRightTableAlias(), term.getRightColumn(),
                    term.getLeftTableAlias(), term.getLeftColumn());
        }
        return newTerms;
    }

    @Override
    public String toString() {
        return "Right Nested Loop Join";
    }

}
