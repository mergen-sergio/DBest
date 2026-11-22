package ibd.query;

import java.util.concurrent.atomic.LongAdder;

public class QueryStats {

    public static final LongAdder PK_SEARCH = new LongAdder();
    public static final LongAdder NEXT_CALLS = new LongAdder();
    public static final LongAdder MEMORY_USED = new LongAdder();

    public static final LongAdder COMPARE_FILTER = new LongAdder();
    public static final LongAdder COMPARE_DISTINCT_TUPLE = new LongAdder();

    public static final LongAdder SORT_TUPLES = new LongAdder();

    public static void clear() {
        PK_SEARCH.reset();
        NEXT_CALLS.reset();
        MEMORY_USED.reset();

        COMPARE_FILTER.reset();
        COMPARE_DISTINCT_TUPLE.reset();

        SORT_TUPLES.reset();
    }

}
