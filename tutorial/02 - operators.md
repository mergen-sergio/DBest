<div align="left">
    <a href="./01a - tutorial-data.md">Previous</a>
</div>
<div align="right">
  <a href="./03 - creating-query-tree.md">Next</a>
</div>


# Available Operators

In DBest, you can build query execution plans by using a variety of operators. These operators are designed to help you manipulate and filter data as it flows through your query plan. They are similar to the operators you would find in a typical Database Management System (DBMS). Below is a list of the common operators available in DBest, with a special focus on **join** operators, as these play a critical role in most database queries.

## Data Retrieval Operators
- **Scan**: Requests **all tuples** from the data source.  
- **Reference**: Uses columns from **already processed operations** for independent processing.  

## Data Filtering and Transformation Operators
- **Filter**: Removes tuples based on **boolean expressions**.  
- **Projection**: Removes **specific columns** from tuples.  
- **Explode**: Splits a tuple into **multiple tuples** based on a delimiter in a column.  
- **Auto Increment**: Adds a **new column** with incrementing values.  

## Data Deduplication Operators
- **Duplicate Removal**: Removes **duplicate tuples** (requires sorted data).  
- **Hash Duplicate Removal**: Uses a **hash table** to remove duplicates (**materialized**).  

## Data Limiting and Sorting Operators
- **Limit**: Keeps **only the top-k tuples**.  
- **Sort**: Orders tuples in **ascending or descending** order (**materialized**).  


## Join Operators  

- **Cartesian Product**  
  A binary operator that combines tuples from the outer and inner sides .  
  The resulting tuples contain columns from both sides. 
  
- **Join (Nested Loop Join)**  
  A binary operator that finds correspondences between tuples from the outer and inner sides based on join conditions (a list of equality filters).  
  The resulting tuples contain columns from both sides. Given an outer tuple, the algorithm searches for matching tuples in the inner side.  

- **Hash Join**  
  A binary operator that finds correspondences between tuples from both sides using a hash-based approach.  
  The inner-side tuples are stored in a hash table indexed by the join columns. During the join phase, this hash table is used to find matches efficiently.  
  This operation requires materialization.  

- **Merge Join**  
  A binary operator that finds correspondences between tuples from both sides by scanning them sequentially, assuming they are already sorted on the join columns.  
  Matches are retrieved as they are found. This method requires sorted data.  

## Outer Joins  

- **Left Outer Join**  
  A variation of the Join where unmatched outer-side tuples are still included in the result, with inner-side columns filled with `NULL` values.  

- **Hash Left Outer Join**  
  A variation of the Hash Join where unmatched outer-side tuples are included, with inner-side columns filled with `NULL` values.  

- **Hash Right Outer Join**  
  A variation of the Hash Join where unmatched inner-side tuples are included, with outer-side columns filled with `NULL` values.  

- **Hash Full Outer Join**  
  A variation of the Hash Join that includes all unmatched tuples from both sides.  
  - Outer-side unmatched tuples have inner-side columns filled with `NULL` values.  
  - Inner-side unmatched tuples have outer-side columns filled with `NULL` values.  

- **Merge Left Outer Join**  
  A variation of the Merge Join where unmatched outer-side tuples are included, with inner-side columns filled with `NULL` values.  

- **Merge Right Outer Join**  
  A variation of the Merge Join where unmatched inner-side tuples are included, with outer-side columns filled with `NULL` values.  

- **Merge Full Outer Join**  
  A variation of the Merge Join that includes all unmatched tuples from both sides.  
  - Outer-side unmatched tuples have inner-side columns filled with `NULL` values.  
  - Inner-side unmatched tuples have outer-side columns filled with `NULL` values. 


## Semi Join Operators  

- **Semi Join**  
  A variation of the Join that checks whether an outer tuple has corresponding inner tuples.  
  If at least one match is found, the outer tuple is returned.  

- **Hash Left Semi Join**  
  A variation of the Hash Join that checks whether an outer (left) tuple has corresponding inner tuples.  
  If at least one match is found, the outer tuple is returned.  

- **Hash Right Semi Join**  
  A variation of the Hash Join that checks whether an inner (right) tuple has corresponding outer tuples.  
  If at least one match is found, the inner tuple is returned.  

- **Merge Left Semi Join**  
  A variation of the Merge Join that checks whether an outer (left) tuple has corresponding inner tuples.  
  If at least one match is found, the outer tuple is returned.  

- **Merge Right Semi Join**  
  A variation of the Merge Join that checks whether an inner (right) tuple has corresponding outer tuples.  
  If at least one match is found, the inner tuple is returned.  

## Anti Join Operators  

- **Anti Join**  
  A variation of the Join that checks whether an outer tuple has corresponding inner tuples.  
  If no match is found, the outer tuple is returned.  

- **Hash Left Anti Join**  
  A variation of the Hash Join that checks whether an outer (left) tuple has corresponding inner tuples.  
  If no match is found, the outer tuple is returned.  

- **Hash Right Anti Join**  
  A variation of the Hash Join that checks whether an inner (right) tuple has corresponding outer tuples.  
  If no match is found, the inner tuple is returned.  

- **Merge Left Anti Join**  
  A variation of the Merge Join that checks whether an outer (left) tuple has corresponding inner tuples.  
  If no match is found, the outer tuple is returned.  

- **Merge Right Anti Join**  
  A variation of the Merge Join that checks whether an inner (right) tuple has corresponding outer tuples.  
  If no match is found, the inner tuple is returned.  


## Aggregation Operators  

- **Aggregation**  
  A **unary operator** that computes an **aggregation function** over all incoming tuples.  
  The available functions are:  
  - `COUNT`, `COUNT_ALL`, `COUNT_NULL`  
  - `MAX`, `MIN`, `SUM`, `AVG`  
  - `FIRST`, `LAST`  

- **Group**  
  A **unary operator** that divides incoming tuples into **groups** and computes aggregation functions for each group.  
  - The available functions are the same as in **Aggregation**.  
  - Groups contain tuples that **share the same value** for a **group-by column**.  
  - This operator **requires tuples to be sorted** by the group-by column to function correctly.  

- **Hash Group**  
  A variation of the **Group** operator that uses a **hash table** to process each group.  
  - This operator is **materialized**.  

## Set Operators  

- **Append**  
  A binary operator that includes all tuples from both inner and outer sides by appending them into a single result set.  
  This operator requires both sides to be **union compatible**, meaning they must have the same number of columns,  and corresponding columns must be of the same data type.  

- **Union**  
  A variation of the Append operator that **removes duplicate tuples** from the result.  
  This operator requires both sides to be **sorted on all columns** to function properly.  

- **Hash Union**  
  A variation of the Union operator that **removes duplicate tuples** from the result without relying on sorted data.  
  Instead, it uses a **hash table** to eliminate duplicates. This operator is **materialized**.  

- **Intersection**  
  A binary operator that **retains tuples from the outer side only if they also exist on the inner side**.  
  This operator requires both sides to be **union compatible**. It also requires **sorted data** on all columns.  

- **Difference**  
  A binary operator that **retains tuples from the outer side only if they do not exist on the inner side**.  
  Like Intersection, this operator requires both sides to be **union compatible** and **sorted on all columns**.  

- **Hash Intersection**  
  A variation of the Intersection operator that does **not rely on sorted data**.  
  Instead, it uses a **hash table** to check for matches. This operator is **materialized**.  

- **Hash Difference**  
  A variation of the Difference operator that does **not rely on sorted data**.  
  Instead, it uses a **hash table** to check for matches. This operator is **materialized**.  


## Logical Operators  

- **And**  
  A binary operator that checks the validity of two connected operators.  
  A connected operator is **valid** if it either:  
  - Returns a **non-empty result**  
  - Returns a **boolean column** named `EVAL` with content `TRUE`.  

  This operator returns a **boolean column `EVAL`**, which evaluates to `TRUE` **only if both connected operators are valid**.  

- **Or**  
  A binary operator that checks the validity of two connected operators.  
  The operator is **valid** under the same conditions as **And**.  

  This operator returns a **boolean column `EVAL`**, which evaluates to `TRUE` **if at least one of the connected operators is valid**.  

- **XOr**  
  A binary operator that checks the validity of two connected operators.  
  The operator is **valid** under the same conditions as **And**.  

  This operator returns a **boolean column `EVAL`**, which evaluates to `TRUE` **only if exactly one of the connected operators is valid**.  

- **Condition**  
  A **nullary** operator (i.e., it takes no input) that contains a **boolean expression**.  
  This operator returns a **boolean column `EVAL`**, which evaluates to `TRUE` **if the boolean expression is satisfied**.  

- **If**  
  A binary operator that contains a **boolean expression**.  
  - If the **boolean expression is satisfied**, it returns the **outer side tuples**.  
  - Otherwise, it returns the **inner side tuples**.  




## Storage and Caching Operators  

- **Hash**  
  A **unary operator** that stores all incoming tuples in a **hash table**,  indexed by the columns used by the connected operator as **equality filters**.  
  - This operation is **materialized**.  

- **Materialization**  
  A **unary operator** that stores all incoming tuples **in memory**,  preventing repeated processing of the subtree starting at this operator.  
  - This operation is **materialized**.  

- **Memoize**  
  A **unary operator** that **remembers accessed tuples** and redirects lookups on the connected operator.  
  - If the same lookup occurs again, the operator retrieves the found tuples **from memory**.  
  - This operation is **materialized**.  



<br><br>
 
<div align="left">
    <a href="./01a - tutorial-data.md">Previous</a>
</div>
<div align="right">
  <a href="./03 - creating-query-tree.md">Next</a>
</div>
