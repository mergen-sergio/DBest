
## Exporting Result Sets

In DBest, the result sets produced by the query operators can be exported in two formats:

1. **CSV Format**
2. **Proprietary B+ Tree Format**

These export options provide flexibility depending on how you want to use or store the resulting data after query execution.

The **Proprietary B+ Tree export** option allows the result set to be saved in DBest's optimized **B+ Tree format**. This format is ideal for use cases where you need to perform efficient range queries or lookups on the exported data.

- **Indexing**: The B+ Tree format indexes a set of columns, which enables efficient searches and range queries. When exporting to the B+ Tree format, users can define the key columns that will be indexed for quick access.
- **Use Case**: This is particularly useful when preparing data once, so it can later be reused for fast, repeated queries that require sorting, equality checks, or range searches.

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



### Example Workflow
1. **Step 1**: Open a CSV for a large dataset and define the datatypes of each column.
2. **Step 2**: Create a query plan over the CSV file to select the columns of interest
3. **Step 3**: Export the result set as a B+ tree, indexing specific columns (e.g., `Age`, `Salary`) for efficient querying. Export multiple times if different indexes are needed.
1. **Step 4**: Prepare a B+ Tree from a large dataset, indexing specific columns (e.g., `Age`, `Salary`) for efficient querying.
2. **Step 5**: Open the B+ Tree and used it in multiple queries to perform range queries, sorting, or lookups without rebuilding the index each time.



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
