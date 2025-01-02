## Operator Schema in DBest

In DBest, every operator has a **schema** that defines the structure of its output data. 
This schema is constructed based on the schemas of the operator’s connected nodes and is influenced by the type of operation performed. Ultimately, the schemas are taken from the name of the data nodes. 

---

### Schema Construction Examples

1. **Projection Operator**  
   - The schema is a **subset** of the schema from its child node.  
   - It includes only the columns specified in the projection's properties.

2. **Join Operator**  
   - The schema is the **union** of the schemas from its left and right child nodes.  
   - It includes all columns from both child nodes.

You can view an operator’s schema by **Running a query** over the node to see the result set, where columns are prefixed with their corresponding schema name.


---

### Dynamic Schema Updates

When editing an operator's properties, the **property window** shows the schemas of its connected nodes. The user can then define how the operator transforms data that comes from the connected nodes. 

If a **child node is disconnected** or replaced by a node with a different schema, the operator’s schema is automatically recalculated.
If the new schema is **incompatible** with the current operator configuration, the operator’s label turns **red**, indicating invalid properties. The user must update the properties to resolve the issue.

---

### Defining Schema Names

Schema names are **derived from the names of the data nodes**.  There are also some operators, called **schema-full operators**, which consolidate all schemas from their connected nodes into a single schema with a default name. Here are some examples of schema-full operators:  
  - **Aggregation Operator:** Default name is `agg`.  
  - **AND Operator:** Default name is `condition`.

Data nodes and schema-full operators can be renamed by **Right-clicking the node** and selecting **Rename**.
Renaming is critical in scenarios with duplicate schema names, such as:
    - **Self-relationships:** Using the same data node multiple times in a query.
    - **Multiple schema-full operators:** For example, two aggregation operators joined together.

Proper naming ensures clarity and avoids conflicts in complex query plans.
