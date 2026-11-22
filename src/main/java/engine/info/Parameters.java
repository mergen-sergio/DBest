package engine.info;

import java.util.concurrent.atomic.LongAdder;

public final class Parameters {

        public static final LongAdder BLOCKS_ACCESSED = new LongAdder();
	public static final LongAdder BLOCKS_LOADED = new LongAdder();
	public static final LongAdder BLOCKS_SAVED = new LongAdder();
        public static final LongAdder RECORDS_READ = new LongAdder();

	public static final LongAdder IO_READ_TIME = new LongAdder();
	public static final LongAdder IO_WRITE_TIME = new LongAdder();
	public static final LongAdder IO_SYNC_TIME = new LongAdder();
	
	public static final LongAdder IO_SEEK_READ_TIME = new LongAdder();
	public static final LongAdder IO_SEEK_WRITE_TIME = new LongAdder();

	public static final LongAdder MEMORY_ALLOCATED_BY_BLOCKS = new LongAdder();
	public static final LongAdder MEMORY_ALLOCATED_BY_DIRECT_BLOCKS = new LongAdder();
	public static final LongAdder MEMORY_ALLOCATED_BY_INDIRECT_BLOCKS = new LongAdder();
	public static final LongAdder MEMORY_ALLOCATED_BY_RECORDS = new LongAdder();
	public static final LongAdder MEMORY_ALLOCATED_BY_COMMITTABLE_BLOCKS = new LongAdder();
	
	public static final LongAdder MEMORY_ALLOCATED_BY_BYTE_ARRAY = new LongAdder();
	
}
