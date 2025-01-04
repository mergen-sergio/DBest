
# Creating and Using an Index

## Steps to Create an Index:

1. **Right-click** on the node whose tuples you want to index.
2. Select the **"Export Table"** menu item and chose **"Unique Index"** or **"Non-Unique Index"**.
3. A configuration window appears, displaying:
   - All columns returned by the node.
   - Sample tuples for reference.
4. Select the key column(s) using radio buttons
5. **Specify the index name** and its **location on disk**.
   - The index will be saved with a `.dat` extension.

When selecting multiple columns for the key, their order in the index is determined by the order in which the radio buttons are checked. This is called a **composite index**. Efficient lookups are possible only if the filter includes the **prefix of the key**. For a composite index with `n` key columns, the first `n-1` keys must have equality conditions.

When creating composite indexes:
  - Prioritize frequently filtered columns in the prefix.
  - Use equality conditions for all but the last key column.

---

## Using the Index:
1. **Load the index**:
   - Use the appropriate menu item in the **top menu**, or
   - Drag and drop the index file into the query editor.
2. The node will appear in the left panel.
3. **Query the index**:
   - Drag the indexed node into the query editor.
   - Double-click the node to view tuples ordered by the key column(s).
---

# Example: Creating a Clustered Primary Index and a Non-Clustered Secondary Index

This tutorial utilizes pre-generated `.dat` index files derived from CSV data. However, the following steps demonstrate how to create these indexes manually.

---

## Creating a Clustered Primary Index

1. Open the CSV file containing the movie database.
2. Drag the `movie` node into the query editor.
3. Choose the **"Unique Index"** option.
4. Select `movie_id` as the key column.
5. Name the file as `movie`.

The image below shows the configuration window for setting the key and value parts of the index:

![Primary Index Creation](assets/images/pk-index-creation.png)

- **Key**: The `movie_id` column serves as the key.
- **Value**: All remaining columns are stored in the value part.  

The resulting file (`movie.dat`) allows querying the `movies` data, ordered by `movie_id`.

---

## Creating a Non-Clustered Secondary Index

1. Load the `movie.dat` file into the tool.
2. Drag the `movie` data node into the query editor.
3. Build a query tree to project the `year` and `movie_id` columns.
4. Export the projection node as an index using the **"Non-Unique Index"** option.
5. Select `year` as the key column.
6. Name the file as `idx_year`.

The image below illustrates the configuration for defining the key/value parts of the secondary index:

![Secondary Index Creation](assets/images/fk-index-creation.png)

- **Key**: The `year` column becomes the key.
- **Value**: The `movie_id` column is stored as the value.  

If a **unique index** is chosen instead, any rows with duplicate `year` values will be excluded.

---

## Using the Secondary Index

The image below shows a query tree where a filter on the `year` column is connected to the secondary index node:

![Querying Secondary Index](assets/images/querying-year-index.png)

- The filter leverages the B+ tree to perform efficient key lookups.
- For highly selective queries, the number of disk page accesses is significantly reduced compared to sequential scanning.

After running the query, the **Cost panel** displays the number of disk pages accessed, providing insights into query efficiency.  
Queries executed on the `idx_year` index return only the `year` and `movie_id` columns, ordered by `year`.

---

## Joining Secondary and Primary Indexes

To access additional columns (e.g., `title`), perform a join between the secondary index and the primary index using `movie_id` as the join predicate.  

The image below shows an example query tree:

![Join Example](assets/images/fk-index-join.png)

- The filter on `year` retrieves movies efficiently from the secondary index.
- The join operator accesses full details from the primary index.

---

## Clustered Secondary Indexes

To avoid performing joins, consider creating a **clustered secondary index** where all tuple information is stored in the value part. While this eliminates the need for a primary index join, it duplicates data content, leading to increased memory usage. Carefully weigh redundancy, query performance, and update costs when deciding.

Alternatively, you can include only the most critical columns in the value part of a secondary index. This approach reduces redundancy and update costs while optimizing query performance for specific columns.  
Indexes designed to store all columns needed for a query within the key/value parts are referred to as **Covering Indexes**.
