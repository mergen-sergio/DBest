# Creating a Query Tree in DBest

In DBest, a **query tree** is composed of:
- **Data Nodes**: Represent data sources, found in the **left panel**.
- **Operator Nodes**: Represent transformations or operations, found in the **right panel**.

## Adding Nodes to the Editor
1. **Adding Operator Nodes**:
   - Click on the desired operator in the **right panel**.
   - Move the mouse to the editor and click to place the operator.

2. **Adding Data Nodes**:
   - Drag and drop the data node from the **left panel** into the editor.

## Connecting Nodes
1. Click the **"Edge"** button in the **bottom menu**.
2. Select the **source node** by clicking on it.
3. Select the **target node** to complete the connection.


## Editing the node properties
Once the connection is established, a **property window** will appear for the operator. Alternatively, open the properties window by **right-clicking** the operator node and selecting **"Edit"**.

For **binary operators**, this window only appears after both child nodes are connected. The window only appears if the node is valid and accept properties.

The properties depend on the type of operator. For example:
- **Projection Operator**: Prompts for columns to project, based on the schema of its child node.
- **Join Operator**: Requires a join predicate and shows columns from the left and right child nodes.

The screenshot below illustrates the property window for a projection operator, showing the available columns from its child node’s schema:

![Projection Operator Properties](assets/images/projection-properties.png)


## Operator Schema
Every operator in DBest has a **schema** that defines the structure of its output data. 
- The schema of an operator is **built based on the schema of its connected nodes**.
- The way the schema is constructed depends on the **type of operation** performed by the operator.

### Examples of Schema Construction
1. **Projection Operator**:
   - The schema is a **subset** of the schema of its child node.
   - It includes only the columns specified in the projection properties.

2. **Join Operator**:
   - The schema is the **union** of the schemas of its left and right child nodes.
   - It includes all columns from both child nodes.

You can view the schemas of an operator by double-clicking the node or running a query over it. It will display the result set. The columns of the result set are prefixed by the data source (schema) name. 


### Dynamic Schema Updates

When editing an operator’s properties, the **property window** displays information about the schemas of the connected nodes,  as the operator will be applied to the **tuples** coming from those nodes.


If an operator’s child node is disconnected or replaced by a node with a different schema:
- The operator's schema is recalculated.
- If the new schema is incompatible with the current configuration, the operator's label turns **red**, indicating invalid properties.
- The user must update the properties to resolve the issue.


### Defining schema names

The name of the schemas are derived from the name of the data nodes. Some operators group all schemas coming from the connected operators into a single one, with an default name. These are called schemafull operators. Examples include aggregation (whose default name if agg) and AND (whose default name is condition).  The name the data nodes and the schemafull operators can be edited by right clicking the node and chosing 'Rename'. Renaming schemas is important for query plans that contain two schemas with the same name. It can happen when using the same data node more than once (example, in queries with self-relationships) or when using two schemafull operators of the same kind (example, two aggregate operators than are united by a join operator. 




## Summary
- Operators derive their schema from their connected nodes, adapting dynamically based on the operation they perform.
- The property window shows schema-related details to assist in configuring the operation.
- Invalid properties are flagged to maintain the integrity of the query tree.

By understanding how schemas are constructed and updated, users can build and manage query trees efficiently in DBest.
