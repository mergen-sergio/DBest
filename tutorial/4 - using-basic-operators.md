## Getting Started with DBest Operators

The **DBest** tool provides a wide range of operators that allow users to create complex queries. To get started, focus on the foundational operators of relational algebra, such as:  
- **Projection**  
- **Selection (Filter)**  
- **Join**  
- **Aggregation**  

---

### Example: Configuring a Query Tree

The example showcases a query tree that combine basic operators:  
- **Join:** Combines `movie` and `movie_cast` tables.  
- **Filter (Selection):** Filters rows based on the `year`.  
- **Projection:** Projects only the `title` and `character_name` columns.  

<img src="assets/images/basic-queries.png" alt="Basic Queries" width="700"/>

---

### Configuring Query Tree Operators

The following screenshots demonstrate how to configure the query tree operators:

1. **Join Operator**:  
   The property window displays the available columns from the left (`movie`) and right (`movie_cast`) child nodes. The join predicate is defined as:  
   `movie.movie_id = movie_cast.movie_id`.

   ![Join Operator Properties](assets/images/join-properties.png)

2. **Filter Operator**:  
   The property window for the filter operator defines an atomic expression that compares the `cast_order` column to a constant value.

   ![Filter Operator Properties](assets/images/filter-properties_.png)

3. **Projection Operator**:  
   The property window for the projection operator specifies two columns for retrieval:  
   - `title` from the `movie` data node.  
   - `character_name` from the `movie_cast` data node.

   ![Projection Operator Properties](assets/images/projection-properties_.png)

---

### Exploring Join Variations

Operators like **joins** offer multiple variations, such as:  
- **Join Types:** Cross-join, inner-join, semi-join, anti-join, outer-join.  
- **Join Algorithms:** Nested-loop, merge-join, hash-join.  

To begin, use the classic **Inner Join**. As you become familiar with basic operators, you can explore other join types and algorithms.

---

### Tips for Beginners

1. **Start Small:** Use small data sources initially to prevent inefficiencies that may cause the tool to hang.  
2. **Experiment:** Rearrange operators and observe how they affect the result set and query performance.  
3. **Learn by Doing:** Focus on understanding the purpose and behavior of each basic operator before experimenting with advanced operators or indexing strategies.
