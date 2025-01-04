<div align="left">
    <a href="../README.md">Previous</a>
</div>
<div align="right">
  <a href="./01a - tutorial-data.md">Next</a>
</div>

## Data Sources

In DBest, a **data source** refers to a collection of records. Each record contains a set of columns. Each column within a record has a specific **data type**, which determines the kind of values it can hold. 

The current version supports three main types of data sources:

1. **CSV Files**
2. **Memory Tables**
3. **Proprietary Format (B+ Tree)**

The proprietary format is an indexed .dat file.  A set of columns is defined as keys, and the remaining columns are the stored values. The index allows reading the records in key order and it provides efficient lookups for equality and range queries over the key columns.

<br>

<div align="left">
    <a href="../README.md">Previous</a>
</div>
<div align="right">
  <a href="./01a - tutorial-data.md">Next</a>
</div>
