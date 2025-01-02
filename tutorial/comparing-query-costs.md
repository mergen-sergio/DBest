
#### Key Difference:
- The **left tree** applies the filter operator after the join.  
- The **right tree** applies the filter operator before the join.

The position of the operators can significantly affect query performance. In the example provided, it is better to perform the filter before the join, as it reduces the effort spent on finding matches. 
