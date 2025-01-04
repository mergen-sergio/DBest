
## Exporting Result Sets

In DBest, the result sets produced by the query operators can be exported in two formats:

1. **CSV Format**
2. **Proprietary B+ Tree Format**

These export options provide flexibility depending on how you want to use or store the resulting data after query execution.

The **Proprietary B+ Tree export** option allows the result set to be saved in DBest's optimized **B+ Tree format**. This format is ideal for use cases where you need to perform efficient range queries or lookups on the exported data.

- **Indexing**: The B+ Tree format indexes a set of columns, which enables efficient searches and range queries. When exporting to the B+ Tree format, users can define the key columns that will be indexed for quick access.
- **Use Case**: This is particularly useful when preparing data once, so it can later be reused for fast, repeated queries that require sorting, equality checks, or range searches.

---
