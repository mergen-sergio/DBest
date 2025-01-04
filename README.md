<img src="./assets/images/dbest-logo2.png" alt="Logo do DBest" width="256">

# Database Basics for Engaging Students and Teachers (DBest)


**DBest** is an interactive tool for creating and visualizing database query execution plans. It allows users to design query plans by dragging and dropping operators, providing a user-friendly interface to understand query execution, optimize performance, and integrate heterogeneous data sources. It is suitable for teaching, data integration, and improving query performance.

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
