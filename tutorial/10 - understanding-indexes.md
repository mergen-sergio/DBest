<div align="left">
    <a href="./09 - understanding-schemas.md">Previous</a>
</div>
<div align="right">
  <a href="./11 - creating-indexes.md">Next</a>
</div>

# DBest Indexing Strategy

In DBest, **data nodes** can be indexed or non-indexed. Non-indexed data nodes, such as memory tables or CSV files, provide **sequential access** to their tuples. When performing a lookup, all tuples must be traversed to find matching results. To improve performance, DBest supports indexed data nodes in the form of **B+ trees**, enabling **efficient lookups** and **ordered access** over key columns.

## Overview of B+ Tree Indexes

A B+ tree index:
- Stores **key/value pairs**.
- Supports **efficient lookups** using the key columns.
- Provides **ordered access** to the key columns.

An index can be:
- **Unique**, ensuring no duplicate values in the key part.
- **Non-unique**, allowing duplicate values for the key part (useful for columns with repeated values).


### How B+ Tree Indexes Work
The nodes of a B+ tree are stored on disk in a persistent data format called a **page**. Pages are designed to minimize the effort of loading data from disk into memory by aligning with the size of typical virtual memory pages (e.g., 4 KB). 

Since each node in a B+ tree can store a large number of children, the tree's height is reduced. This high degree ensures that searches require loading only a few pages, optimizing performance.

---


## Clustered and Non-clustered Indexes


Indexes in DBest can be **clustered** or **non-clustered**, differing in how the value part is structured:
- A **clustered index** stores the entire tuple as the value part, effectively replacing the original data node.
- A **non-clustered index** stores pointers to the tuple location, requiring access to the original data node for complete information.

### Primary Index (Clustered)
A clustered index is typically built on the **primary key** column(s), which uniquely identify tuples. For this reason, it is also referred to as the **primary index**. A data node usually have only one clustered index.

### Secondary Index (Non-Clustered)
Non-clustered indexes, or **secondary indexes**, are built on columns other than the primary key. During querying, the secondary index is typically joined with the primary index to retrieve full tuple information.

<br>

---

<br>

## Notes

- **No Concurrent Updates**: DBest is not designed for concurrent updates or multi-user write operations. The system is optimized for batch processing and query execution rather than real-time data updates. This means that once data is loaded or a B+ Tree is created, it is expected to remain static during query execution.
  
- **Pre-Processing B+ Tree**: Since DBest does not support concurrent updates, a B+ Tree can be **prepared once**—i.e., indexed and stored for later use. Once created, the B+ Tree can be reused across multiple queries, enabling efficient range queries and lookups without needing to rebuild the index each time. This approach provides significant performance improvements when querying large datasets, as the B+ Tree can quickly resolve range and equality queries over the indexed columns.

- **Custom Updates via Java Code**: While DBest is not designed for concurrent updates, **records can be updated manually by writing custom Java code**. If you need to modify the data in a dataset or B+ Tree after it has been loaded, you can write Java code to perform these updates. 


<br>

<div align="left">
    <a href="./09 - understanding-schemas.md">Previous</a>
</div>
<div align="right">
  <a href="./11 - creating-indexes.md">Next</a>
</div>
