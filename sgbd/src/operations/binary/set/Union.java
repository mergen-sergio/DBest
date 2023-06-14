package operations.binary.set;

import sgbd.query.Operator;
import sgbd.query.binaryop.IntersectionOperator;

import java.util.List;

public class Union extends SetOperators {

	public Union() {

	}

	@Override
	public Operator createSetOperator(Operator op1, Operator op2, List<String> columns1, List<String> columns2) {
		System.out.println(op1.getContentInfo());
		System.out.println(op2.getContentInfo());
		System.out.println(columns1);
		System.out.println(columns2);
		return new IntersectionOperator(op1, op2, columns1, columns2);
	}
}
