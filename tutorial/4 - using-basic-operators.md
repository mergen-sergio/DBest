## Getting Started with DBest Operators

The **DBest** tool includes a wide range of operators that enable the creation of complex queries. To begin, it’s recommended to start with the basic operators, which are foundational to relational algebra:  
- **Projection**  
- **Selection**  
- **Join**  
- **Aggregation**  




# Example: Configuring a Query Tree

The example below illustrates two query trees that combine basic operators:
- **Join:** Combines `movie` and `movie_cast`.
- **Filter (Selection):** Filters rows based on the `year`.
- **Projection:** Projects only the `title` and `character_name` columns.
  


<img src="assets/images/basic-queries.png" alt="Basic Queries" width="700"/>




The following screenshots demonstrates configuring the query tree operators.

1. **Join Operator**:  
   The property window for the join operator shows the available columns from the left (`movie`) and right (`movie_cast`) child nodes. The join predicate is defined as:  
   `movie.movie_id = movie_cast.movie_id`.

   ![Join Operator Properties](assets/images/join-properties.png)

2. **Filter Operator**:  
   The property window for the filter operator defines an atomic expression comparing the `cast_order` column to a constant value.  

   ![Filter Operator Properties](assets/images/filter-properties_.png)

3. **Projection Operator**:  
   The property window for the projection operator specifies two columns for retrieval:  
   - `title` from the `movie` data node.  
   - `character_name` from the `movie_cast` data node.

   ![Projection Operator Properties](assets/images/projection-properties_.png)


Some operators, like **joins**, have multiple variations, as join types (cross-join, inner-join, semi-join, anti-join, outer-join) and join algorithm (nested-loop, merge-join, hash-join). As a starting point, use the classic **Inner Join**. One you become familiar with how  the basic operators work, you can try different ones. 

---

### Tips for Beginners
1. **Start Small:** Use small data sources to avoid the tool hanging due to inefficient operator choices.
2. **Experiment:** Change the position of operators and observe how they impact the result set and query execution time.
3. **Learn by Doing:** Focus on understanding the purpose and behavior of each basic operator before moving on to advanced operators or creating indexes.





