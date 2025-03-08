<div align="left">
    <a href="./01a - tutorial-data.md">Previous</a>
</div>
<div align="right">
  <a href="./03 - creating-query-tree.md">Next</a>
</div>


## Available Operators

In DBest, you can build query execution plans by using a variety of operators. These operators are designed to help you manipulate and filter data as it flows through your query plan. They are similar to the operators you would find in a typical Database Management System (DBMS). Below is a list of the common operators available in DBest, with a special focus on **join** operators, as these play a critical role in most database queries.

- **Filter**: An unary operator that filters incoming tuples based on boolean expressions
 
- **Projection**: An unary operator that removes columns from the incoming tuples


  
- **Join**: A binary operator that maintains all correspondences between the outer and the inner side tuples based on join conditions, expressed as a list of equality filters. The correspondencenes are built as a sum of the columns coming from both sides. Given an outer tuple, the algorithm asks for matches for the connected child of hthe right part (the inner part). Also known as Nested Loop Join.
- **Nested Loop Join**: A binary operator that maintains all correspondences between the outer and the inner side tuples based on join conditions, expressed as a list of equality filters. The correspondencenes are built as a sum of the columns coming from both sides.  The algorithm stores all incoming tuples from the inner part on a hash table indexed by the columns used as join conditions. The hash tbale is used during the join phase. This operation is materialized.
   **Merge Join**: A binary operator that finds correspondences all correspondences between the outer and the inner side tuples based on join conditions, expressed as a list of equality filters. The correspondencenes are built as a sum of the columns coming from both sides.  The algorithm traverses both sides sequencially and retrieved the macthes as they are found. It requires sorted data to function properly.
  - **Left Outer Join**: A variation of the Join. An outer tuple that has no matches to inner tuples  is still returned, and have the inner side columns complemented with null values.
  - **Hash Left Outer Join**: A variation of the Hash Join. An outer tuple that has no matches to inner tuples  is still returned, and have the inner side columns complemented with null values. 
    - **Hash Right Outer Join**: A variation of the Hash Join. An inner tuple that has no matches to outer tuples is still returned, and have the outer side columns complemented with null values.
    - **Hash Full Outer Join**: A variation of the Hash Join. An inner tuple that has no matches to outer tuples is still returned, and have the outer side columns complemented with null values. Similarly,  an outer tuple that has no matches to inner tuples is still returned, and have the inner side columns complemented with null values.
    - - **Merge Left Outer Join**: A variation of the Merge Join. An outer tuple that has no matches to inner tuples  is still returned, and have the inner side columns complemented with null values. 
    - **Merge Right Outer Join**: A variation of the Merge Join. An inner tuple that has no matches to outer tuples is still returned, and have the outer side columns complemented with null values.
    - - **Merge Full Outer Join**: A variation of the Merge Join. An inner tuple that has no matches to outer tuples is still returned, and have the outer side columns complemented with null values. Similarly,  an outer tuple that has no matches to inner tuples is still returned, and have the inner side columns complemented with null values.
     
    **Semi Join**: A variation of the Join that checks if a an outer tuple has correspondences to inner tuples. If so, the outer tuple is returned. 
   **Hash Left Semi Join**: A variation of the Hash Join that checks if a an outer(left) tuple has correspondences to inner tuples. If so, the outer tuple is returned. 
 **Hash Right Semi Join**: A variation of the Hash Join that checks if a an inner(right) tuple has correspondences to outer tuples. If so, the inner tuple is returned.
   **Merge Left Semi Join**: A variation of the Merge Join that checks if a an outer(left) tuple has correspondences to inner tuples. If so, the outer tuple is returned. 
 **Merge Right Semi Join**: A variation of the Merge Join that checks if a an inner(right) tuple has correspondences to outer tuples. If so, the inner tuple is returned.


    **Anti Join**: A variation of the Join that checks if a an outer tuple has correspondences to inner tuples. If no correspondences are found, the outer tuple is returned. 
   **Hash Left Anti Join**: A variation of the Hash Join that checks if a an outer(left) tuple has correspondences to inner tuples. If no correspondences are found, the outer tuple is returned. 
 **Hash Right Anti Join**: A variation of the Hash Join that checks if a an inner(right) tuple has correspondences to outer tuples. If no correspondences are found, the inner tuple is returned.
   **Merge Left Anti Join**: A variation of the Merge Join that checks if a an outer(left) tuple has correspondences to inner tuples. If no correspondences are found,the outer tuple is returned. 
 **Merge Right Anti Join**: A variation of the Merge Join that checks if a an inner(right) tuple has correspondences to outer tuples. If no correspondences are found, the inner tuple is returned.

**Append**: A binary operator that maintains all tuples from both inner and outer sides. The tuples are appended into a single result set. This operator requires both sides to be union compatible to work properly. It means they need to be formed by the same number of columns, and columns must be of the same data type. 
**Union**: A variation of the Append operator that removes duplicate tuples from the result.  THis operator requires both sides to be sorted on all columns to work properly. 
**Hash Union**: A variation of the Union operator that removes duplicate tuples from the result. This operator does not rely on sorted data. Instead, it uses a hash table to remove duplicates. This oeprator is materialized.
**Intersection**: A binary operator that maintains tuples from the outer side if it exists as an inner side tuple.   This operator requires both sides to be union compatible to work properly. It means they need to be formed by the same number of columns, and columns must be of the same data type. THis operator requires both sides to be sorted on all columns to work properly. 
**Difference**: A binary operator that maintains tuples from the outer side if it does not exists as an inner side tuple.   This operator requires both sides to be union compatible to work properly. It means they need to be formed by the same number of columns, and columns must be of the same data type. THis operator requires both sides to be sorted on all columns to work properly. 
**Hash Intersection**: A variation of the Intersection operator that does not rely on sorted data. Instead, it uses a hash table to check for matches. This oeprator is materialized.
**Hash Difference**: A variation of the Difference operator that does not rely on sorted data. Instead, it uses a hash table to check for matches. This oeprator is materialized.

**And**: A binary operator that checks the validity of the two connected operators. TO be valid, a connected operator has to either return a non-empty result or return a boolean column named EVAL whose content is TRUE.  This operator returns a boolean column EVAL. The evaluation is true only if both two connected operators are valid. 
**Or**: A binary operator that checks the validity of the two connected operators. TO be valid, a connected operator has to either return a non-empty result or return a boolean column named EVAL whose content is TRUE.  This operator returns a boolean column EVAL. The evaluation is true if at least on of two connected operators are valid. 
**XOr**: A binary operator that checks the validity of the two connected operators. TO be valid, a connected operator has to either return a non-empty result or return a boolean column named EVAL whose content is TRUE.  This operator returns a boolean column EVAL. The evaluation is true if just one of two connected operators is valid. 
**Condition**: A nullary operator that contains a boolean expression.   This operator returns a boolean column EVAL. The evaluation is true if the boolnea expression is satisfied. 
**If**: A binary operator that contains a boolean expression. If the boolean expression is satisfied, the oeprator returns the outer side tuples. It returned the inner side tuples otherwise. 

**Scan**: An unary operator that informs its connected operator that it requires all tuples.  
**Cartesian product**: A binary operator that maintains all combinations between the outer and the inner side tuples. The combinations are built as a sum of the columns coming from both sides. 

**Reference**: A nullary operator that is formed by columns from already processed operations. Useful for independent processing regarding parts of the queyr already processed.


**Sort**: An unary operator that sorts the incoming tuples by one of its columns in ascending or descending order. This operator is materialized.


**Agggregation**: An unary operator that computes an aggregation function from all the incoming tuples. The available fucctions are COUNT, COUNT_ALL, COUNT_NULL, MAX, MIN, SUM, AVG, FIRST and LAST. 
**Group**: An unary operator that that divides the incoming tuples into groups and compute aggregation functions for each group. The available fuctions are COUNT, COUNT_ALL, COUNT_NULL, MAX, MIN, SUM, AVG, FIRST and LAST.  The groups contain tuples that share the same value for a group by column. This operator requires tuples to be sorted by the group by column to work properly.
**Hash Group**: A variation of the Group operator that uses a hash to process each group.  This operator is materialized.

**Duplicate Removal**: An unary operator that only keeps on the duplicate incoming tuples. This operator requires tuples to be sorted to work properly. 
**Hash Duplicate Removal**: A variation of the Duplicate Removal operator that uses a hash table to identify duplicates. This operator is materialized.

**Limit**: An unary operator that only maintains the top-k incoming tuples in the result set. 

**Explode**: An unary operator that splits the incoming tuples into multiples tuples based on the presence of a delimiter in one the columns. 

**Auto increment**: An unary operator that adds a new column into the incoming tuples. The value of the column is incremented as tuples are processed. 

- **Hash**: An unary operator that stores all incoming tuples on a hash table indexed by the columns used by the connected operator as equality filters. This operation is materialized
- **Materialization**: An unary operator that stores all incoming tuples in memory to avoid repetadly processing the subtree starting at this operator. This operation is materialized
- **Memoize**: An unary operator that remembers accessed tuples. It redirects lookups on the connected operator. If the same lookup occurs again, the opertor retrieves the found tuples from a collection in memory.This operation is materialized

splits the incoming tuples into multiples tuples based on the presence of a delimiter in one the columns. 

variation of the Group operator that uses a hash to process each group.  This operator is materialized.




- **Inner Join**
    - Nested Loop Join
    - Hash Join
    - Merge Join
- **Left Outer Join**
    - Nested Loop Join
    - Hash Join
    - Merge Join
- **Right Outer Join**
    - Nested Loop Join
    - Hash Join
    - Merge Join
- **Full Outer Join**
    - Hash Join
    - Merge Join
- **Left Semi Join**
    - Nested Loop Join
    - Hash Join
    - Merge Join
- **Right Semi Join**
    - Nested Loop Join
    - Hash Join
    - Merge Join
- **Left Anti Join**
    - Nested Loop Join
    - Hash Join
    - Merge Join
- **Right Anti Join**
    - Nested Loop Join
    - Hash Join
    - Merge Join
- **Cross Join**
- **Aggregation**
- **Sorting**
- **Group By**
    - Sorted
    - Hashed
- **Append**
- **Union**
    - Sorted
    - Hashed
- **Difference**
    - Sorted
    - Hashed
- **Intersect**
    - Sorted
    - Hashed

<br><br>
 
<div align="left">
    <a href="./01a - tutorial-data.md">Previous</a>
</div>
<div align="right">
  <a href="./03 - creating-query-tree.md">Next</a>
</div>
