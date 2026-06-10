package ibd.query.unaryop.sort;

import ibd.query.ColumnLocation;
import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.IntegerColumn;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortTest {

    @Test
    void comparatorUsesIndependentDirectionForEachSortColumn() throws Exception {
        Sort sort = new Sort(
            new StubOperation(),
            new String[]{"movie.year", "movie.budget"},
            new boolean[]{true, false}
        );
        sort.sortColumns.get(0).setColumnLocation(columnLocation(0));
        sort.sortColumns.get(1).setColumnLocation(columnLocation(1));

        List<Tuple> tuples = new ArrayList<>(List.of(
            tuple(2020, 100),
            tuple(2020, 200),
            tuple(2019, 300)
        ));

        tuples.sort(sort.createComparator());

        assertEquals(2019, tuples.get(0).rows[0].getValue(0));
        assertEquals(300, tuples.get(0).rows[0].getValue(1));
        assertEquals(2020, tuples.get(1).rows[0].getValue(0));
        assertEquals(200, tuples.get(1).rows[0].getValue(1));
        assertEquals(2020, tuples.get(2).rows[0].getValue(0));
        assertEquals(100, tuples.get(2).rows[0].getValue(1));
    }

    private static ColumnLocation columnLocation(int colIndex) {
        ColumnLocation columnLocation = new ColumnLocation();
        columnLocation.rowIndex = 0;
        columnLocation.colIndex = colIndex;
        return columnLocation;
    }

    private static Tuple tuple(int year, int budget) {
        Prototype prototype = new Prototype();
        prototype.addColumn(new IntegerColumn("year"));
        prototype.addColumn(new IntegerColumn("budget"));

        LinkedDataRow row = new LinkedDataRow(prototype, false);
        row.setValue(0, year);
        row.setValue(1, budget);

        Tuple tuple = new Tuple();
        tuple.setSingleSourceRow("movie", row);
        return tuple;
    }

    private static class StubOperation extends Operation {
        @Override
        public void setConnectedDataSources() {
        }

        @Override
        protected Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
            return Collections.emptyIterator();
        }

        @Override
        public Map<String, List<String>> getContentInfo() {
            return Collections.emptyMap();
        }

        @Override
        public void close() {
        }
    }
}
