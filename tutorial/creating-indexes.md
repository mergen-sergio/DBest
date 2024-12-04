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

4. **Choose columns for the index key**:
   - For primary indexes:
     - The **key** is the primary key column(s).
     - The **value** includes all remaining columns.
   - For secondary indexes:
     - The **key** includes the indexed column(s) and the primary key column(s).
     - The **value** is **empty**.

5. **Define the order of columns within the key**:
   - The user selects columns using radio buttons, which infer the **key levels**.
   - It is crucial to place the **indexed column** as the **first level** of the key.
     - Efficient lookups are possible only if the filter reaches the **prefix of the key**.

6. **Specify the index name** and its **location on disk**.
   - The index will be saved with a `.dat` extension.

### Loading an Index
To load the index:
- Use the appropriate menu item in the **top menu**, or
- Drag and drop the index file into the query editor.

The index will appear in the **left panel** along with other data nodes.

## Best Practices for Indexing
- Convert all data nodes used in lookups into indexes.
- Use the lookup column(s) as the **first level** of the key for maximum efficiency.

## Primary and Secondary Indexes
1. **Primary Index**:
   - Built on the column(s) that uniquely identify tuples (e.g., a primary key like `movie_id`).
   - **Key**: Primary key column(s).
   - **Value**: Remaining columns in the schema.

2. **Secondary Index**:
   - Built on non-primary key columns for efficient lookups.
   - **Key**: Combination of the indexed column(s) and the primary key column(s).
   - **Value**: **Empty**.

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
1. Build a **query tree** that projects:
   - The column(s) to be indexed (e.g., `year`).
   - The primary key column(s) (e.g., `movie_id`).

2. Use the **"Export Table"** functionality to create the index, selecting the desired key column(s) and defining their levels.

### Using Secondary Indexes
Once loaded into the editor:
- Use the index node for efficient lookups.
- Example:
  - A **filter operator** connected to the `year` index retrieves matching tuples efficiently.
  - If no index is available, a sequential scan is required, which is less efficient.

#### Handling Additional Columns
Secondary indexes store only the **key column(s)**. 
- If additional columns (e.g., `title`) are required, **join the secondary index** with the primary index using the primary key as the join predicate.

### Illustrated Workflow
1. Build a query tree to project the key column(s) and primary key column(s).
2. Export the data node as an index using the **Indexed Data** option.
3. Define the key levels, ensuring the indexed column is the **first level**.
4. Load the index into the left panel for use in the query tree.

The image below shows a query tree where:
- A filter operator is connected to a `year` secondary index.
- Efficient lookups are performed on the `year` column.

![Index Example](assets/images/index_example.png)

Without the index:
- A sequential scan would be required, reducing performance.

With the index:
- The secondary index retrieves relevant tuples efficiently.
- If additional columns are needed, join the secondary index with the primary index.

By leveraging B+ tree indexes and carefully defining key levels, DBest enables efficient data access, improving query performance significantly.
