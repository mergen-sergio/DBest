<div align="left">
    <a href="../README.md">Previous</a>
</div>
<div align="right">
  <a href="./01a - tutorial-data.md">Next</a>
</div>

## Data Sources

In DBest, a **data source** refers to a collection of records, where each record consists of a set of columns. Each column has a specific **data type** that determines the kind of values it can store.

The following primitive data types are supported:  
`STRING`, `INT`, `LONG`, `DOUBLE`, `FLOAT`, and `BOOLEAN`.

### Supported Data Source Types

DBest currently supports three main types of data sources:

1. **CSV Files**
2. **Memory Tables**
3. **Proprietary Format (B+ Tree)**

#### Proprietary Format (B+ Tree)

The proprietary format uses indexed `.dat` files. In this format:  
- A set of columns is designated as **keys**.  
- The remaining columns are stored as **values**.  

This indexing structure enables:  
- Reading records in **key order**.  
- Efficient lookups for both **equality** and **range queries** on key columns.


<br>

<div align="left">
    <a href="../README.md">Previous</a>
</div>
<div align="right">
  <a href="./01a - tutorial-data.md">Next</a>
</div>
