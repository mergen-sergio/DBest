<img src="./assets/images/dbest-logo.png" alt="Logo do DBest" width="256">

# Database Basics for Engaging Students and Teachers (DBest)

## Índice

1. [Resumo](#resumo)
2. [Objetivo inicial](#objetivo-inicial)
3. [Objetivo atual](#objetivo-atual)
4. [Ferramentas](#ferramentas)
5. [Utilização](#utilização)
6. [Dependências externas](#dependências-externas)
7. [Contribuições](#contribuições)
8. 
# DBest: Visual Query Engine

**DBest** is a powerful, interactive tool for creating and visualizing database query execution plans. It allows users to design query plans by dragging and dropping operators, providing a user-friendly interface to understand query execution, optimize performance, and integrate heterogeneous data sources. It is suitable for teaching, data integration, and improving query performance.

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Installation](#installation)
4. [Usage](#usage)
   - [Creating a Query Plan](#creating-a-query-plan)
   - [Executing a Query Plan](#executing-a-query-plan)
   - [Optimizing Query Performance](#optimizing-query-performance)
5. [Contributing](#contributing)
6. [License](#license)
7. [Acknowledgments](#acknowledgments)

---

## Overview

**DBest** allows users to create query execution plans visually, which helps in understanding query execution, optimizing database performance, and integrating multiple data sources (e.g., relational, NoSQL, or external APIs).

With DBest, you can:
- Create complex query execution plans through a drag-and-drop interface.
- Visualize query execution trees and operators.
- Optimize queries by adjusting execution paths.
- Use DBest for educational purposes to teach students about query optimization.
- Integrate data from heterogeneous data sources.

---

## Features

- **Visual Query Plan Creation**: Drag and drop operators to build execution plans.
- **Execution Trees**: View query execution steps and data flow.
- **Query Optimization**: Experiment with different operators and execution paths for performance improvements.
- **Educational Tool**: Ideal for teaching database concepts such as query planning and optimization.
- **Data Integration**: Connect and query multiple data sources using a unified execution plan.

---


## Available Operators

In DBest, you can build query execution plans by using a variety of operators. These operators are designed to help you manipulate and filter data as it flows through your query plan. They are similar to the operators you would find in a typical Database Management System (DBMS). Below is a list of the common operators available in DBest, with a special focus on **join** operators, as these play a critical role in most database queries.

- **Selection**
- **Projection**
- **Scan** 
- **Materialized**
     - Hash
     - Memoize
     - Binary Search Tree 
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

## Data Sources

In DBest, a **data source** refers to a collection of records. Each record contains a set of columns. Each column within a record has a specific **data type**, which determines the kind of values it can hold. 

The current version supports three main types of data sources:

1. **CSV Files**
2. **Memory Tables**
3. **Proprietary Format (B+ Tree)**

A B+ tree data source is indexed. A set of columns is defined as keys, and the remaining columns are the stored values. The index allows reading the records in key order and it provides efficient lookups for equality and range queries over the key columns.


## Exporting Result Sets

In DBest, the result sets produced by the query operators can be exported in two formats:

1. **CSV Format**
2. **Proprietary B+ Tree Format**

These export options provide flexibility depending on how you want to use or store the resulting data after query execution.

The **Proprietary B+ Tree export** option allows the result set to be saved in DBest's optimized **B+ Tree format**. This format is ideal for use cases where you need to perform efficient range queries or lookups on the exported data.

- **Indexing**: The B+ Tree format indexes a set of columns, which enables efficient searches and range queries. When exporting to the B+ Tree format, users can define the key columns that will be indexed for quick access.
- **Use Case**: This is particularly useful when preparing data once, so it can later be reused for fast, repeated queries that require sorting, equality checks, or range searches.

---

### Key Considerations

- **No Concurrent Updates**: DBest is not designed for concurrent updates or multi-user write operations. The system is optimized for batch processing and query execution rather than real-time data updates. This means that once data is loaded or a B+ Tree is created, it is expected to remain static during query execution.
  
- **Pre-Processing B+ Tree**: Since DBest does not support concurrent updates, a B+ Tree can be **prepared once**—i.e., indexed and stored for later use. Once created, the B+ Tree can be reused across multiple queries, enabling efficient range queries and lookups without needing to rebuild the index each time. This approach provides significant performance improvements when querying large datasets, as the B+ Tree can quickly resolve range and equality queries over the indexed columns.

- **Custom Updates via Java Code**: While DBest is not designed for concurrent updates, **records can be updated manually by writing custom Java code**. If you need to modify the data in a dataset or B+ Tree after it has been loaded, you can write Java code to perform these updates. 

### Example Workflow
1. **Step 1**: Open a CSV for a large dataset and define the datatypes of each column.
2. **Step 2**: Create a query plan over the CSV file to select the columns of interest
3. **Step 3**: Export the result set as a B+ tree, indexing specific columns (e.g., `Age`, `Salary`) for efficient querying. Export multiple times if different indexes are needed.
1. **Step 4**: Prepare a B+ Tree from a large dataset, indexing specific columns (e.g., `Age`, `Salary`) for efficient querying.
2. **Step 5**: Open the B+ Tree and used it in multiple queries to perform range queries, sorting, or lookups without rebuilding the index each time.


---

This flexibility allows users to work with their data in a variety of ways, either exporting results for analysis or preparing indexed datasets for efficient querying.




## DBest as a Query Execution Tool

It is important to note that **DBest is not a query optimizer**. While traditional database management systems (DBMS) often include sophisticated query optimization techniques that automatically transform SQL queries into efficient execution plans, DBest operates differently. DBest's primary function is to **execute predefined query plans** that have been manually created by the user.

---

### Query Execution, Not Optimization

In DBest:

- **User-Defined Query Plans**: Users manually create query plans by selecting operators and arranging them into an execution tree. The query plan is explicitly designed by the user, specifying exactly how the query should be executed.
  
- **No Automatic Query Optimization**: DBest does not automatically optimize query plans. Unlike a DBMS, which analyzes queries to find the most efficient execution path, DBest assumes that the user has designed the query plan based on their specific needs and understanding of the data. 

- **Execution of Query Plans**: DBest's role is to **execute the provided query plan** exactly as it is defined. This includes reading the input data, applying the selected operators, and producing a result set based on the sequence of operations specified by the user.

---

### Query Cost Indicators

While DBest does not optimize queries, it provides **query cost indicators** to help users analyze the performance of their query execution:

- **Execution Time**: DBest tracks the time it takes to execute each operator and the overall query plan.
- **Memory Usage**: DBest provides information on memory usage during query execution, helping users identify areas where memory may become a bottleneck.
- **Operator Costs**: Each operator in the query plan has an associated cost, and DBest can collect and report on the cost of executing individual operators, allowing users to identify expensive operations and adjust their query plans accordingly.

These indicators help users understand the efficiency of their query plans, enabling them to make informed decisions about how to optimize their plans manually if needed.

---

### Key Takeaways

- **Manual Query Plan Creation**: DBest requires the user to create the query plan manually, using a visual interface or through other tools that DBest provides.
- **No Query Optimization**: Unlike traditional DBMSs, DBest does not automatically analyze or optimize query plans.
- **Query Execution and Cost Reporting**: DBest focuses on executing the user-defined query plan and provides useful cost indicators (such as execution time and memory usage) to help users evaluate the performance of their queries.

DBest is designed as a **query execution tool** for those who want to experiment with and explore the execution of predefined query plans. It is not intended for use cases that require automatic query optimization or real-time query tuning.



## Dependências externas

| Nome                                                         | Descrição                                                                        |
| :----------------------------------------------------------- | :------------------------------------------------------------------------------- |
| [For Your Information Database (FYI Database)][fyi-database] | Utilizado como o banco de dados principal                                        |
| [ANTLR4][antlr4]                                             | Utilizado para validar as entradas possíveis da DSL desenvolvida para o software |
| [JGraphX][jgraphx]                                           | Utilizado para construir os nós visuais e suas conexões                          |
| [Data Faker][data-faker]                                     | Utilizado para gerar dados e popular as tabelas criadas na ferramenta            |

## Contribuições

Caso você queira contribuir com o projeto, abaixo estão os contatos das pessoas que participam dele.

| Nome   | Contato                                                                                           |
| :----- | :------------------------------------------------------------------------------------------------ |
| Sérgio | [sergio@inf.ufsm.br][email-inf-sergio]                                                            |
| Rhuan  | [rmoreiramaciel@gmail.com][email-pessoal-rhuan] <br> [rmmaciel@inf.ufsm.br][email-inf-rhuan]      |
| Luiz   | [lhlago@inf.ufsm.br][email-inf-luiz]                                                              |
| Marcos | [marcosvisentini@gmail.com][email-pessoal-marcos] <br> [mvisentini@inf.ufsm.br][email-inf-marcos] |

<!-- Links -->

[sgbd]:                 <https://pt.wikipedia.org/wiki/Sistema_de_gerenciamento_de_banco_de_dados> "Sistema Gerenciador de Banco de Dados"
[algebra-relacional]:   <https://pt.wikipedia.org/wiki/%C3%81lgebra_relacional>                    "Álgebra relacional"
[fyi-database]:         <https://github.com/crazynds/FyiDatabase-Java>                             "For Your Information Database (FYI Database)"
[antlr4]:               <https://github.com/antlr/antlr4>                                          "ANTLR4"
[jgraphx]:              <https://github.com/vlsi/jgraphx-publish>                                  "JGraphX"
[data-faker]:           <https://github.com/datafaker-net/datafaker>                               "Data Faker"
[email-inf-sergio]:     <mailto:sergio@inf.ufsm.br>                                                "E-mail da informática do Sérgio"
[email-pessoal-rhuan]:  <mailto:rmoreiramaciel@gmail.com>                                          "E-mail pessoal do Rhuan"
[email-inf-rhuan]:      <mailto:rmmaciel@inf.ufsm.br>                                              "E-mail da informática do Rhuan"
[email-inf-luiz]:       <mailto:lhlago@inf.ufsm.br>                                                "E-mail da informática do Luiz"
[email-pessoal-marcos]: <mailto:marcosvisentini@gmail.com>                                         "E-mail pessoal do Marcos"
[email-inf-marcos]:     <mailto:mvisentini@inf.ufsm.br>                                            "E-mail da informática do Marcos"
