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
1. Select the **source node** by clicking on it.
2. Click the **"Edge"** button in the **bottom menu**.
3. Select the **target node** to complete the connection.

Once the connection is established:
- A **property window** will appear for the operator.
- For **binary operators**, this window only appears after both child nodes are connected.

Alternatively:
- Open the properties window by **right-clicking** the operator node and selecting **"Edit"**.

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

When editing an operator’s properties, the **property window** displays information about the schemas of the connected nodes. This helps the user configure the operation, as it will be applied to the **tuples** coming from those nodes.

### Dynamic Schema Updates
If an operator’s child node is disconnected or replaced by a node with a different schema:
- The operator's schema is recalculated.
- If the new schema is incompatible with the current configuration, the operator's label turns **red**, indicating invalid properties.
- The user must update the properties to resolve the issue.

## Configuring Operator Properties
The properties depend on the type of operator. For example:
- **Projection Operator**: Prompts for columns to project, based on the schema of its child node.
- **Join Operator**: Requires a join predicate and shows columns from the left and right child nodes.

### Example: Projection Properties
The screenshot below illustrates the property window for a projection operator, showing the available columns from its child node’s schema:

![Projection Operator Properties](assets/images/projection_properties.png)

## Summary
- Operators derive their schema from their connected nodes, adapting dynamically based on the operation they perform.
- The property window shows schema-related details to assist in configuring the operation.
- Invalid properties are flagged to maintain the integrity of the query tree.

By understanding how schemas are constructed and updated, users can build and manage query trees efficiently in DBest.
