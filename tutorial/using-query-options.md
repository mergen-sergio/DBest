## Node Options in the Query Tree

In addition to running a query, you can perform various actions on the nodes of a query tree. To access these options, **right-click** on a node. The available options are:

- **Run Query**: Executes a query starting from the selected node.

- **Information**: Displays general metadata about the node.  
  - If the node is invalid (highlighted in red), the information panel will display the associated error message.

- **Export Table**: Exports the result set generated by the selected node.  
  - Export options include:
    - **DBest indexed `.dat` file**
    - **CSV file**
    - **SQL**

- **Rename**: Updates the name of the selected node.  
  - Node names are useful for easy identification and for defining schema names when operators generate their own schema.

- **Save Query**: Saves a textual version of the query tree to a file.  
  - The saved query tree can be reopened using the **"Open Query"** menu option in the **File** menu or by dragging the file into the editor.

- **Edit**: Modifies the properties of the operator associated with the selected node (if applicable).

- **Operators**: Provides a shortcut for connecting the selected node to another operator.

- **Remove**: Deletes the selected node and any edges connected to it.

- **Mark**: Marks a node for comparison.  
  - Query indicators of marked nodes can be compared via the **"Comparator"** menu at the bottom of the interface.