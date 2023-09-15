package operations.binary.set;

import java.util.List;

import sgbd.query.Operator;
import sgbd.query.binaryop.UnionOperator;

public class Union extends SetOperators {

    @Override
    public Operator createSetOperator(Operator operator1, Operator operator2, List<String> columns1, List<String> columns2) {
        return new UnionOperator(operator1, operator2, columns1, columns2);
    }
}
