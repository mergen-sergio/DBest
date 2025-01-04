<img src="./assets/images/dbest-logo2.png" alt="Logo do DBest" width="256">

# Database Basics for Engaging Students and Teachers (DBest)


**DBest** is an interactive tool for creating and visualizing database query execution plans. It allows users to design query plans by dragging and dropping operators, providing a user-friendly interface to understand query execution, optimize performance, and integrate heterogeneous data sources. It is suitable for teaching, data integration, and improving query performance.

With DBest, you can:
- Create complex query execution plans through a drag-and-drop interface.
- Visualize query execution trees and operators.
- Optimize queries by adjusting execution paths.
- Use DBest for educational purposes to teach students about query optimization.
- Integrate data from heterogeneous data sources.


## DBest as a Query Execution Tool

It is important to note that **DBest is not a query optimizer**. While traditional database management systems (DBMS) often include sophisticated query optimization techniques that automatically transform SQL queries into efficient execution plans, DBest operates differently. DBest's primary function is to **execute predefined query plans** that have been manually created by the user.


In DBest:

- **User-Defined Query Plans**: Users manually create query plans by selecting operators and arranging them into an execution tree. The query plan is explicitly designed by the user, specifying exactly how the query should be executed.
  
- **No Automatic Query Optimization**: DBest does not automatically optimize query plans. Unlike a DBMS, which analyzes queries to find the most efficient execution path, DBest assumes that the user has designed the query plan based on their specific needs and understanding of the data. 

- **Execution of Query Plans**: DBest's role is to **execute the provided query plan** exactly as it is defined. This includes reading the input data, applying the selected operators, and producing a result set based on the sequence of operations specified by the user.

While DBest does not optimize queries, it provides **query cost indicators** to help users analyze the performance of their query execution:

<!-- - **Execution Time**: DBest tracks the time it takes to execute each operator and the overall query plan. -->
- **Memory Usage**: DBest provides information on memory usage during query execution, helping users identify areas where memory may become a bottleneck.
- **Operator Costs**: Each operator in the query plan has an associated cost, and DBest can collect and report on the cost of executing individual operators, allowing users to identify expensive operations and adjust their query plans accordingly.

These indicators help users understand the efficiency of their query plans, enabling them to make informed decisions about how to optimize their plans manually if needed.

---

## Getting Started

To get started with DBest, follow these steps:

1. **Install DBest:**  
   Download the DBest JAR [file](DBest.jar) and run it to launch the tool. Make sure you have Java 20 installed, as it is required to execute the application.

2. **Read the [Tutorial](tutorial/README.md):**  
   Explore the tutorial for a comprehensive, step-by-step guide to using DBest. Learn how to create indexes, build query trees, and run queries effectively.

3. **Access Sample Data:**  
   Practice using the tool with sample data provided in the tutorial. You can download the dataset [here](data/01a - tutorial-data.md).







