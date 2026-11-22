package ibd.query.sourceop;

import ibd.index.btree.DictionaryPair;
import ibd.index.btree.table.BinaryValue;
import ibd.query.Tuple;
import ibd.query.UnpagedOperationIterator;
import ibd.table.btree.BTreeTable;
import ibd.table.prototype.column.Column;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParallelIndexScan extends SourceOperation {

    final BTreeTable table;

    int part = 0;

    int parts = 1;

    volatile boolean cancelled = false;

    public ParallelIndexScan(String tableAlias, BTreeTable table) {
        super(tableAlias);
        this.table = table;
    }

    public void setPart(int part, int parts) {
        if (parts < 1 || part < 0 || part >= parts) {
            throw new IllegalArgumentException("Invalid part " + part + " of " + parts);
        }
        this.part = part;
        this.parts = parts;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public BTreeTable getTable() {
        return table;
    }

    @Override
    public void setExposedDataSources() throws Exception {
        super.setExposedDataSources();
        dataSources[0].prototype = table.getPrototype();
    }

    @Override
    public void setConnectedDataSources() throws Exception {
        super.setConnectedDataSources();
        connectedDataSources[0].prototype = table.getPrototype();
    }

    @Override
    public boolean canProcessDelegatedFilters() {
        return true;
    }

    @Override
    public String getDataSourceName() {
        return table.getName();
    }

    @Override
    protected Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new PartIterator(processedTuples, withFilterDelegation);
    }

    class PartIterator extends UnpagedOperationIterator {

        Iterator<DictionaryPair> it;

        PartIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            it = table.getTree().searchPartIterator(part, parts);
        }

        @Override
        protected Tuple findNextTuple() {
            while (!cancelled && it.hasNext()) {
                Tuple tuple = new Tuple();
                tuple.setSingleSourceRow(alias, ((BinaryValue) it.next().getValue()).rowData);
                if (lookup.match(tuple)) {
                    return tuple;
                }
            }
            return null;
        }
    }

    @Override
    public Map<String, List<String>> getContentInfo() {
        List<String> columns = new ArrayList<>();
        for (Column c : table.getPrototype().getColumns()) {
            columns.add(c.getName());
        }
        Map<String, List<String>> map = new LinkedHashMap<>();
        map.put(alias, columns);
        return map;
    }

    @Override
    public void close() {
    }

    @Override
    public String toString() {
        return "[" + alias + "] Parallel Index Scan " + (part + 1) + "/" + parts;
    }
}
