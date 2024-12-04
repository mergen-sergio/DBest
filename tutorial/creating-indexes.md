# Working with Indexes in DBest

In DBest, **data nodes** provide **sequential access** to their tuples. When performing a lookup, all tuples must be traversed to find matching results. To improve performance, DBest supports **B+ tree-based indexing**, enabling **efficient lookups** and **ordered access** over key columns.

## Overview of B+ Tree Indexes
A B+ tree index:
- Stores **key/value pairs**.
- Supports **efficient lookups** using the key columns.
- Provides **ordered access** to the key columns.

## Creating an Index
To create an index for a data node:
1. **Right-click** on the node whose tuples you wish to index.
2. Select the **"Export Table"** menu item and click **"Indexed Data"**.
3. A window will display:
   - All columns returned by the node.
   - Sample tuples for reference.

4. **Define the order of columns within the key**:
   - The user selects the indexed columns using radio buttons
   - It is possible to create a composite index, composed by more than one column
   - In a composite index, the order in which the radion buttons are check determine the **key levels**.
   - Efficient lookups are possible only if the filter reaches the **prefix of the key**. So chose the key levels wisely, placing the columns that are most oftenly filtered in the first levels. 

5. **Specify the index name** and its **location on disk**.
   - The index will be saved with a `.dat` extension.
6. **Loading the index**.
   - Use the appropriate menu item in the **top menu**, or
   - Drag and drop the index file into the query editor.
   - The node will be place on the left panel.
7. **Using the index**.
   - Drag the node from the left panel into the qeury editor.
   - Double-click to view the tuples.
   - Tuples appear in the defined key order


The image belos shows an example where the CSV data node about movies is transformed into an index that uses movie_id as the key. Once created and loaded into the tool, the index node can be dragged into the query editor. Double click the node to check that the tuples are returned ordered by movie_id.





## Primary and Secondary Indexes

There are two types of indexes: Primary and secondary. 

1. **Primary Index**:
   - Built on the column(s) that uniquely identify tuples (e.g., a primary key like `movie_id`).
   - **Key**: Primary key column(s).
   - **Value**: Remaining columns in the schema.
   - Useful for queries that perform lookups over the primary key.

2. **Secondary Index**:
   - Built on non-primary key columns.
   - **Key**: Combination of the indexed column(s) and the primary key column(s).
   - **Value**: **Empty**.
   - Useful for queries thay perform lookups over non primary key columns.

### Example
For a table `movie`:
- **Primary Index**:
  - **Key** = `movie_id`.
  - **Value** = Remaining columns (e.g., `title`, `year`).
- **Secondary Index**:
  - **Key** = `year, movie_id`.
  - **Value** = **Empty**.
  - **Year** is the first-level column in the key for efficient lookups.


### Creating a Secondary Index
1. Build a query tree to project the key column(s) and primary key column(s).
2. Export the projection node as an index using the **Indexed Data** option.
3. Define the key levels, ensuring the indexed column is the **first level**.
4. Load the index into the left panel for use in the query tree.


### Using Secondary Indexes
Once loaded into the editor, a secondary index can be used for efficient lookups.
The image below shows a query tree where:
- A filter operator is connected to a `year` secondary index.
- Efficient lookups are performed on the `year` column.

![Index Example](assets/images/index_example.png)

Without the index:
- A sequential scan would be required, reducing performance.

With the index:
- The secondary index retrieves relevant tuples efficiently.
- If additional columns are needed, join the secondary index with the primary index.


#### Handling Additional Columns
Secondary indexes store only the **key column(s)**. 
- If additional columns (e.g., `title`) are required, **join the secondary index** with the primary index using the primary key as the join predicate.




## Best Practices for Maximum Efficiency
- Convert all data nodes used in lookups into indexes.
- In composite indexes, u
- 
By leveraging B+ tree indexes and carefully defining key levels, DBest enables efficient data access, improving query performance significantly.
