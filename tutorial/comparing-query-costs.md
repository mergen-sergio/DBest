# Query Plan Comparison and Efficiency

When executing queries, there can be multiple **query plans** that are logically equivalent, meaning they produce the same result set regardless of the stored data. However, these plans can differ in efficiency, which depends on execution time and memory usage. Efficiency is influenced by the volume of data processed and the logic applied by each operator.

## Example: Movie Query
The goal is to retrieve **movie titles** and **character names** for movies released before 1930.

![Equivalent Queries](assets/images/basic-queries.png)

### Key Difference
- The **left tree** applies the filter operator **after** the join.  
- The **right tree** applies the filter operator **before** the join.

This distinction in operator placement significantly impacts query performance. In this case, applying the filter **before** the join is more efficient because it reduces the number of rows processed during the join.

---

## Query Indicators in DBest
DBest provides several metrics to help analyze query performance:

- **Tuples Read:** Number of tuples returned by the query.
- **Accessed Blocks:** Total page accesses, including memory hits.
- **Loaded Blocks:** Pages loaded into memory, which may vary due to buffering.
- **Filter Comparisons:** Number of atomic filter conditions evaluated.
- **Memory Usage:** Bytes used by materialized operators.
- **Next Calls:** Iterations performed by operators to fetch tuples.
- **Primary Key Searches:** Number of B+ tree searches executed.
- **Records Read:** Total records accessed, including non-returned records.
- **Sorted Tuples:** Tuples sorted by `Sort` operators.

### Key Metrics
1. **Accessed Blocks** – Reflects I/O efficiency.  
2. **Memory Usage** – Highlights resource demand.  

Query indicators are displayed during execution. A **button** in the result data frame reveals these metrics.

---

## Comparing Query Plans
### Steps to Compare Query Plans:
1. **Mark Nodes for Comparison:**
   - Right-click the nodes to compare and select **Mark**.
   - Mark root nodes to compare entire query trees or subtrees for specific analysis.

   ![Marking Queries](assets/images/marking-queries.png)

   Marked nodes are highlighted with a red rectangle and renamed as `Q1` (left tree) and `Q2` (right tree) for easy identification.

2. **Open the Comparator Panel:**
   - Select the **Comparator** menu from the bottom toolbar.
   - The comparator panel displays one column for each marked query with various cost indicators.

3. **Analyze Costs:**
   - Advance records incrementally or compute the full result set.
   - Observe how query costs evolve during execution.

---

## Example Analysis
The comparator panel reveals that the **left tree** incurs higher costs. Specifically:
- **Accessed Blocks:** More disk pages are read because filtering occurs **after** the join, leading to unnecessary processing of `movie_cast` rows.
- **Filter Comparisons:** Higher in the left tree because the same movie is filtered multiple times, once for each match in `movie_cast`.

![Comparing Queries](assets/images/comparing_queries.png)

---

## Benefits of Query Plan Comparison
Query plan comparisons offer several advantages:
- **Snapshots of Costs:** Gain insights into query effort at different stages.
- **Partial vs. Final Costs:** Understand how intermediate steps influence overall performance.
- **Optimization Opportunities:** Identify inefficiencies in operator placement or logic.

By advancing records one at a time, you can evaluate the incremental effort needed to fetch the next tuple and identify opportunities to optimize query execution.
