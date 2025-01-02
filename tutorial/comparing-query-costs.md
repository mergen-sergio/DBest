As with SQL, there may exists many query plans that are equivalent. It means they return the same result set, regardless of the stored data. In such, cases, it is important to understand which query plan is more efficient. Efficieny can be related to both the execution time and the memory usage. This is related to the amount of data processed and the logic of each operator. 

The exame below shows an example. 

#### Key Difference:
- The **left tree** applies the filter operator after the join.  
- The **right tree** applies the filter operator before the join.

The position of the operators can significantly affect query performance. In the example provided, it is better to perform the filter before the join, as it reduces the effort spent on finding matches. 

It is possible to analyze the query execution impact by comparing query plans. To compare query plans, right click  the nodes you wish to compare and select the mark menu item, as indicated in the figure below. The root node of the two query plans were selected, as we wish to compare the whole execution tree. However, we could chose to compare a sub-tree, if the purpose is to analyze a inner part of the query.  


After marking the nodes, select the 'Comparator' menu item from the bottom menu bar. The image below shows the comparator panel. There is one column per query node selected, and several queyr costs indicators.  You can advance the records by a variable number or compute the whole result set. As the tuples are read, the query cost indicators change for each of the marked nodes.

In the example provided, we can see that the left tree is more expansive. It demands reading many more pages from disk (blocks). 

The whole set of query indicators are:

Accessed Blocks
Loaded Blocks
Filter comparisons
memory usage
next calls
primary key searches
records read
sorted tuples
