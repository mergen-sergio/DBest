# Working with Indexes in DBest

In DBest, **data nodes** provide **sequential access** to their tuples. When performing a lookup, all tuples must be traversed to find matching results. To improve performance, DBest supports **B+ tree-based indexing**, enabling **efficient lookups** and **ordered access** over key columns.

## Overview of B+ Tree Indexes

A B+ tree index:
- Stores **key/value pairs**.
- Supports **efficient lookups** using the key columns.
- Provides **ordered access** to the key columns.

An index can be unique or non-unique. A non-unique index accepts duplicate values for its key part. It is designed for column whose value may appear multiple times in a data node. 

### How B+ Tree Indexes Work
The nodes of a B+ tree are stored on disk in a persistent data format called a **page**. Pages are designed to minimize the effort of loading data from disk into memory by aligning with the size of typical virtual memory pages (e.g., 4 KB). 

Since each node in a B+ tree can store a large number of children, the tree's height is reduced. This high degree ensures that searches require loading only a few pages, optimizing performance.


---

## Creating an Index
To create an index for a data node:
1. **Right-click** on the node whose tuples you wish to index.
2. Select the **"Export Table"** menu item and chose **"Unique Index"** or **"Non-Unique Index"**.
3. A window will display:
   - All columns returned by the node.
   - Sample tuples for reference.
4. Select the key column using radio buttons
5. **Specify the index name** and its **location on disk**.
   - The index will be saved with a `.dat` extension.

### Using an Index
After creating the index:
1. **Load the index**:
   - Use the appropriate menu item in the **top menu**, or
   - Drag and drop the index file into the query editor.
2. The node will appear in the left panel.
3. **Use the index**:
   - Drag the node from the left panel into the query editor.
   - Double-click the node to view the tuples, which are now ordered by the key column.
---

## Example: Creating an Index over Year 
The `movies` data node contains the columns `movie_id`, `title`, and `year`. In this first example we will create an index over the year column.  Right click the data node, select the **"Export Table"** menu item and chose **"Non-Unique Index"**.
ON the data frame, only the year radio button is checked. The image below shows an example where 



The image below shows a query tree where a filter over the year column is connected to the index node. The Filter is resolved  by the B+tree key search.  If the query is selective, the number of page access on disk will be much lower than if a sequentil access was performed.  To see this, run the query and go the the Cost panel, where you can see the number of pages loaded from disk.


## Composite Index

A **composite index** allows indexing using multiple key columns:
- The order of columns in the index is determined by the order in which the radio buttons are checked.
- **Efficient lookups** are possible only if the filter includes the **prefix of the key**.
- For an index with `n` key columns, the first `n-1` keys must be filtered with equality conditions.



## Primary and Secondary Indexes

To enable efficient access, it is recommended that all data nodes are indexed.  Also, you can structure the indexes as primary and secondary indexes. The primary index is built on the primary key column(s)(the column(s) that uniquely identify tuples of a data node). A secondary index is built on non-primary key columns. There is only one primary index per data node. All other indexes are secondary. 



### Steps to Create a Primary Index

1. Load a data node into the query editor.
2. Use the **Indexed Data** option over the node.
3. Check the radio buttons for the primary key column(s). 

The image below shows an example of primary index creation for a movie data node. During index creation, check only the `movie_id` column. The remaining columns will be part of the value. 

After index creation, load the index into the editor and run a query over it. All columns are displayed, only that they are ordered by movie_id. 

### Steps to Create a Secondary Index

1. Build a query tree to project the indexed column(s) and primary key column(s).
2. Export the projection node as an index using the **"Indexed Data"** option.
3. Ensure the indexed column(s) are the **first levels** and the primary key column(s) are the **last levels**.




The image below shows an example of the tree used to index the year column. During index creation, the indexed column `year` needs to be selected as the first level and the movie_id as the second level. 


## Using Secondary Indexes

Secondary indexes store only the **key column(s)**, leaving the value part empty. To retrieve additional columns:
- **Join the secondary index** with the primary index using the primary key as the join predicate.

The image below shows an example. The filter over the secondary index efficiently finds movies released in 1950. For those movies, the join operator finds the corresponding entry on the primary index on the right side, which gives access to the title column. 
The join lookup over movie_id is efficiently resolved, since the primary key is indexed by movie_id. 

To avoid the join, we can create a secondary index that stores all movie information as value columns. This is an option that need to be carefully decided, since it duplicate the contents of a data node for all indexes created. This speeds up search, since no primary index joins are required. However, it leads to a high memory consumption. Also, it the columns are updated, all indexes need to be updated as well. 

An alternative is to add to a secondary index the most important columns to the value part, instead of adding all of the columns. This reduces redundancy and update costs, and it speeds up queries that focus on the included columns. 


## Best Practices for Indexing in DBest
- Index all data nodes used in lookups.
- When creating composite indexes:
  - Prioritize frequently filtered columns in the prefix.
  - Use equality conditions for all but the last key column.
- Avoid excessive redundancy in secondary indexes to balance query performance and storage overhead.

By leveraging **B+ tree indexes** and carefully defining key levels, DBest achieves efficient data access, significantly improving query performance.
