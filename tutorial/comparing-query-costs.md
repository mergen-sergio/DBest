### Query Plan Comparison and Efficiency

As with SQL, multiple **query plans** can be logically equivalent, meaning they produce the same result set regardless of the stored data. However, their efficiency can vary significantly. Efficiency is determined by execution time and memory usage, which depend on the amount of data processed and the logic of each operator.

#### Example: Movie Query
We want to retrieve the **movie titles** and **character names** of movies released before 1930.

#### Key Difference:
- The **left tree** applies the filter operator **after** the join.  
- The **right tree** applies the filter operator **before** the join.

This difference in operator placement affects query performance. In this example, applying the filter **before** the join is more efficient, as it reduces the number of rows processed during the join.

---

### Query Indicators in DBest
DBest provides several indicators to help analyze query performance:

- **Tuples Read:** Number of tuples returned by the query.
- **Accessed Blocks:** Total page accesses (includes hits in memory).
- **Loaded Blocks:** Pages loaded into memory (can vary due to buffering).
- **Filter Comparisons:** Number of atomic filter conditions evaluated.
- **Memory Usage:** Bytes used by materialized operators.
- **Next Calls:** Iterations performed by operators to fetch tuples.
- **Primary Key Searches:** B+ tree searches executed.
- **Records Read:** Total records accessed (includes non-returned records).
- **Sorted Tuples:** Tuples sorted due to `Sort` operators.

The most critical indicators are:
1. **Accessed Blocks** – Reflects I/O efficiency.
2. **Memory Usage** – Highlights resource demands.

Query indicators are displayed during execution. A **button** in the result data frame reveals these metrics.

---

### Comparing Query Plans
To compare query plans, follow these steps:

1. **Mark Nodes for Comparison:**
   - Right-click the nodes to compare and select **Mark**.
   - Typically, the root nodes of two query trees are marked to compare the entire trees. You can also select subtrees for focused analysis.
   - Marked nodes are renamed as `Q1` (left tree) and `Q2` (right tree).

2. **Open the Comparator Panel:**
   - Select the **Comparator** menu item from the bottom menu bar.
   - The comparator panel displays a column for each marked query, with various cost indicators.

3. **Analyze Costs:**
   - Advance records incrementally or compute the full result set.
   - Observe how query costs evolve during execution.

---

### Example Analysis
In the example provided:
- The **left tree** incurs higher costs. It reads more disk pages (blocks) because filtering occurs **after** the join, leading to unnecessary processing of `movie_cast` rows.
- The **number of filter comparisons** is higher for the left tree because the same movie is filtered multiple times, once per match in `movie_cast`.

---

### Benefits of Query Plan Comparison
Comparing query plans provides valuable insights:
- **Snapshots of Costs:** Understand query effort at different stages.
- **Partial vs. Final Costs:** See how intermediate steps influence overall performance.
- **Optimization Insights:** Identify inefficiencies in operator placement or query logic.

By advancing records one at a time, you can evaluate the incremental effort required to fetch the next tuple and better optimize query execution.
