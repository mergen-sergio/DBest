package operations;

import com.mxgraph.model.mxCell;

import entities.cells.OperationCell;

import java.util.List;

import static entities.utils.cells.CellUtils.changeCellName;
import ibd.query.SingleSource;

public class Operation {

    private Operation() {

    }

    public static void operationSetter(OperationCell cell, String name, List<String> arguments, ibd.query.Operation operator) {
        mxCell jCell = cell.getJCell();
//                int index = name.indexOf(":");
//                String alias = "";
//                String composedName = "";
//                if (index!=-1){
//                    alias = name.substring(index+1, name.length()).trim();
//                    cell.setAlias(alias);
//                    name = name.substring(0, index);
//                    cell.setName(name);
//                    composedName = name+"("+alias+")";
//                }
//                else composedName = name;

        cell.setOperator(operator);
        cell.setName(name);
        String a = "";
        //if (operator instanceof SingleSource ssOp) 
        {
            cell.setAlias(operator.getDataSourceAlias());
            a = operator.getDataSourceAlias();
            if (!a.isBlank()) {
                a = ":" + a;
            }
        }
        String formattedName = cell.getType().symbol + a;
        if (!arguments.isEmpty())
            formattedName+=arguments;
        
        cell.setArguments(arguments);
        cell.removeError();

        changeCellName(jCell, formattedName);

    }
}
