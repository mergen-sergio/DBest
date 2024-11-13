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

- **Selection (`SELECT`)**
- **Projection (`PROJECT`)**
- **Filter (`FILTER`)**
- **Inner Join (`INNER JOIN`)**
- **Left Join (`LEFT JOIN`)**
- **Right Join (`RIGHT JOIN`)**
- **Full Outer Join (`FULL OUTER JOIN`)**
- **Cross Join (`CROSS JOIN`)**
- **Self Join (`SELF JOIN`)**
- **Aggregation (`AGGREGATE`)**
- **Sorting (`SORT`)**
- **Group By (`GROUP BY`)**
- **Union (`UNION`)**
- **Difference (`DIFFERENCE`)**
- **Intersect (`INTERSECT`)**


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
